package com.lj.gulimail.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.constant.RedisConstant;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.CategoryDao;
import com.lj.gulimail.product.entity.CategoryBrandRelationEntity;
import com.lj.gulimail.product.entity.CategoryEntity;
import com.lj.gulimail.product.service.CategoryBrandRelationService;
import com.lj.gulimail.product.service.CategoryService;
import com.lj.gulimail.product.vo.Category2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 树形显示所有的分类   (按照父分类 -> 子分类的顺序显示)
     * @return
     */
    public List<CategoryEntity> listByTree() {
        //所有的分类
        List<CategoryEntity> list = baseMapper.selectList(null);
        //先查询所有的一级分类,再递归的将每个分类的子分类查询出来并添加到父分类中(排序)
        List<CategoryEntity> categoryEntityList = list.stream()
                .filter(item -> item.getParentCid() == 0)
                .map(item -> {
                    item.setChildren(findChildrens(item, list));
                    return item;
                })
                .sorted((item1, item2) -> {
                    int a = item1.getSort() == null ? 0 : item1.getSort();
                    int b = item2.getSort() == null ? 0 : item2.getSort();
                    return a - b;
                })
                .collect(Collectors.toList());
        return categoryEntityList;
    }

    /**
     * 逻辑删除菜单
     * @param catIds
     */
    public void deleteMenuByIds(Long[] catIds) {
        baseMapper.deleteBatchIds(Arrays.asList(catIds));

    }

    /**
     * 根据分类id查找完整分类路径
     * @param catelogId
     * @return
     */
    public Long[] findPathById(Long catelogId) {
        List<Long> ids = new ArrayList<>();
        List<Long> path = findPath(catelogId, ids);
        Collections.reverse(path);
        return path.toArray(new Long[path.size()]);
    }

    /**
     * 修改分类
     * @param category
     */
    @Transactional
    public void updateDetailById(CategoryEntity category) {
        //修改分类
        this.updateById(category);
        //修改品牌关联数据信息
        if (!StringUtils.isEmpty(category.getName())){
            CategoryBrandRelationEntity categoryBrandRelation = new CategoryBrandRelationEntity();
            categoryBrandRelation.setCatelogName(category.getName());
            categoryBrandRelationService.update(categoryBrandRelation
                    ,new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id",category.getCatId()));
        }
        //TODO 修改其他关联数据信息


    }

    /**
     * 查找所有一级分类
     * @return
     */
    public List<CategoryEntity> getLevel1Categorys() {
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     *  查找一级分类包含的二级分类,二级分类包含的三级分类,并按返回值封装
     *  缓存穿透:   查询不存在的数据,导致一直查询db   解决方案:将空值缓存
     *  缓存雪崩:   大量key同时失效(过期)   解决方案:缓存时给每个key设置不同的ttl
     *  缓存击穿:   热点key失效,导致短时间内db的高并发访问  解决方案:加锁
     * @return
     */
    public Map<String, List<Category2Vo>> getLevel2Categorys(){
        String json = redisTemplate.opsForValue().get(RedisConstant.CATEGORYS_KEY);
        if (!StringUtils.isEmpty(json)){
            //先从缓存中获取
            Map<String, List<Category2Vo>> stringListMap = JSON.parseObject(json, new TypeReference<Map<String, List<Category2Vo>>>() {
            });
            return stringListMap;
        }
        Map<String, List<Category2Vo>> level2Categorys = getLevel2CategorysFromDbWithRedissonLock();
        return level2Categorys;
    }

    private Map<String, List<Category2Vo>> getLevel2CategorysFromDbWithRedissonLock(){
        RLock lock = redissonClient.getLock(RedisConstant.CATEGORY_LOCK);
        //阻塞获取锁
        lock.lock();
        try {
            String json = redisTemplate.opsForValue().get(RedisConstant.CATEGORYS_KEY);
            if (!StringUtils.isEmpty(json)){
                //先从缓存中获取
                Map<String, List<Category2Vo>> stringListMap = JSON.parseObject(json, new TypeReference<Map<String, List<Category2Vo>>>() {
                });
                return stringListMap;
            }
            log.info("查询数据库");
            return this.getLevel2CategorysFromDb();
        }finally {
            lock.unlock();
        }
    }


    /**
     * 本地锁解决高并发下查询db
     * 缺点:分布式下无法解决锁的共享问题
     * @return
     */
    private Map<String, List<Category2Vo>> getLevel2CategorysFromDbWithLocalLock(){
        synchronized (this){
            //再次确定缓存中是否有数据
            String json = redisTemplate.opsForValue().get(RedisConstant.CATEGORYS_KEY);
            if (!StringUtils.isEmpty(json)){
                //先从缓存中获取
                Map<String, List<Category2Vo>> stringListMap = JSON.parseObject(json, new TypeReference<Map<String, List<Category2Vo>>>() {
                });
                return stringListMap;
            }
            log.info("查询数据库...");
            return getLevel2CategorysFromDb();
        }
    }

    /**
     * 利用redis的setnx实现分布式锁
     * @return
     */
    private Map<String, List<Category2Vo>> getLevel2CategorysFromDbWithRedisLock(){
        String uuid = UUID.randomUUID().toString();
        Boolean getLock = redisTemplate.opsForValue().setIfAbsent(RedisConstant.CATEGORY_LOCK, uuid, 300, TimeUnit.SECONDS);
        if (getLock){
            //抢到锁
            try{
                return getLevel2CategorysFromDb();
            }finally {
                //释放锁,只能释放自己的锁
                
            }
        }
        else {
            //未抢到锁,重试
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getLevel2CategorysFromDbWithRedisLock();
        }
    }



    /** 从数据库中查找
     * 查找一级分类包含的二级分类,二级分类包含的三级分类,并按返回值封装
     * @return
     */
    private Map<String, List<Category2Vo>> getLevel2CategorysFromDb() {
        //所有的分类
        List<CategoryEntity> categoryEntities = this.getBaseMapper().selectList(null);
        //先查找所有的一级分类
        List<CategoryEntity> level1Categorys = this.getParentCategorys(categoryEntities,0L);
        Map<String, List<Category2Vo>> map = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //找到二级分类,封装成Category2Vo
            List<CategoryEntity> level2Categorys = this.getParentCategorys(categoryEntities,v.getCatId());
            List<Category2Vo> level2CategoryVos = null;
            if (level2Categorys != null && !level2Categorys.isEmpty()) {
                level2CategoryVos = level2Categorys.stream().map(l2 -> {
                    //查找三级分类
                    List<CategoryEntity> level3Categorys = this.getParentCategorys(categoryEntities,l2.getCatId());
                    List<Category2Vo.Category3Vo> level3CategoryVos = null;
                    if (level3Categorys != null && !level3Categorys.isEmpty()){
                        level3CategoryVos = level3Categorys.stream().map(l3 -> new Category2Vo.Category3Vo(l2.getCatId().toString(),l3.getCatId().toString(),l3.getName()))
                                .collect(Collectors.toList());
                    }
                    return new Category2Vo(v.getCatId().toString(),level3CategoryVos,l2.getCatId().toString(),l2.getName());
                }).collect(Collectors.toList());
            }
            return level2CategoryVos;
        }));
        //若map数据为空,为了避免缓存穿透问题,缓存一份任意数据
        String s = map != null ? JSON.toJSONString(map) : RedisConstant.NO_DATA;
        redisTemplate.opsForValue().set(RedisConstant.CATEGORYS_KEY,s,1, TimeUnit.DAYS);
        return map;
    }

    /**
     * 获取父分类id为parentCid的子分类集合
     * @param categoryEntities
     * @param parentCid
     * @return
     */
    private List<CategoryEntity> getParentCategorys(List<CategoryEntity> categoryEntities, Long parentCid) {
        List<CategoryEntity> collect = categoryEntities.stream().filter(c -> c.getParentCid() == parentCid).collect(Collectors.toList());
        return collect;
    }

    private List<Long> findPath(Long catelogId,List<Long> path){
        path.add(catelogId);//添加当前id
        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0){
            //有父分类,递归查找父分类
            findPath(category.getParentCid(),path);
        }
        return path;
    }

    private List<CategoryEntity> findChildrens(CategoryEntity fatherCategory, List<CategoryEntity> list) {
        List<CategoryEntity> childrenList = list.stream()
                .filter(item -> item.getParentCid() == fatherCategory.getCatId())
                .map(item -> {
                    item.setChildren(findChildrens(item, list));
                    return item;
                })
                .sorted((item1,item2) -> {
                    int a = item1.getSort()==null?0:item1.getSort();
                    int b = item2.getSort()==null?0:item2.getSort();
                    return a - b;
                })
                .collect(Collectors.toList());
        return childrenList;
    }


}
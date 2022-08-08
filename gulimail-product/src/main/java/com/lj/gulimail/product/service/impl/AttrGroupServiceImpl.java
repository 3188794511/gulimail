package com.lj.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.constant.Constant;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.AttrGroupDao;
import com.lj.gulimail.product.entity.AttrAttrgroupRelationEntity;
import com.lj.gulimail.product.entity.AttrEntity;
import com.lj.gulimail.product.entity.AttrGroupEntity;
import com.lj.gulimail.product.service.AttrAttrgroupRelationService;
import com.lj.gulimail.product.service.AttrGroupService;
import com.lj.gulimail.product.service.AttrService;
import com.lj.gulimail.product.vo.AttrGroupWithAttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询指定三级分类下的属性分组
     * @param params  查询参数
     * @param catelogId
     * @return
     */
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId == 0){
            //查询所有的属性分组
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params)
                    , new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        }
        else{
            //查询指定catelogId下的属性分组
            LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttrGroupEntity::getCatelogId,catelogId);
            String key = (String) params.get("key");
            if (!StringUtils.isEmpty(key)){
                wrapper.and((i) -> i.eq(AttrGroupEntity::getAttrGroupId,key)
                        .or().like(AttrGroupEntity::getAttrGroupName,key));
            }
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据attrGroupId查询关联的属性
     * @param attrGroupId
     * @return
     */
    public AttrEntity[] findAttrRelationById(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelations = attrAttrgroupRelationService
                .list(new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_group_id", attrGroupId));
        if (!attrAttrgroupRelations.isEmpty()){
            List<Long> attrIds = attrAttrgroupRelations.stream()
                    .map(item -> item.getAttrId()).collect(Collectors.toList());
            return attrService.listByIds(attrIds).toArray(new AttrEntity[attrService.listByIds(attrIds).size()]);
        }
        return null;
    }

    /**
     * 批量删除属性分组关联的基本属性
     * @param attrAttrgroupRelationEntities
     */
    public void deleteBatchRelation(AttrAttrgroupRelationEntity[] attrAttrgroupRelationEntities) {
        getBaseMapper().deleteBatchRelation(Arrays.asList(attrAttrgroupRelationEntities));
    }

    /**
     * 查询当前属性分组可以关联且未关联的基本属性
     * @param attrGroupId
     * @param params
     * @return
     */
    public PageUtils findNotAttrRelation(Long attrGroupId, Map<String, Object> params) {
        //当前所属分类
        Long catelogId = this.getById(attrGroupId).getCatelogId();
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("catelog_id"
                , catelogId).eq("attr_type"
                , Constant.Product.BASE_TYPE.getType());
        //若查询条件不为空,拼接查询条件
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        //查询出当前属性分组所属分类下的所有基本属性
        IPage<AttrEntity> page = attrService.page(new Query<AttrEntity>().getPage(params),wrapper );
        List<AttrEntity> attrEntities = page.getRecords();

        //对attrEntities进行过滤,排除已经被其他属性分组关联的属性和已经被自己关联的属性
        List<AttrEntity> filterAttr = attrEntities.stream().filter(item -> {
            QueryWrapper<AttrAttrgroupRelationEntity> wrapper1 = new QueryWrapper<>();
            AttrAttrgroupRelationEntity attr = attrAttrgroupRelationService.getOne(wrapper1.eq("attr_id", item.getAttrId()));
            return attr == null ? true : false;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(filterAttr);
        return pageUtils;
    }

    /**
     * 添加关联属性
     * @param relation
     */
    public void saveAttrRelation(AttrAttrgroupRelationEntity[] relation) {
        attrAttrgroupRelationService.saveBatch(Arrays.asList(relation));
    }

    /**
     * 根据分类id查询所有属性分组以及属性分组所包含的属性
     * @param catelogId
     * @return
     */
    public AttrGroupWithAttrVo[] findAttrGroupWithAttr(Long catelogId) {
        //查询属性分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //查询属性分组并封装结果
        if (attrGroupEntities != null && !attrGroupEntities.isEmpty()) {
            List<AttrGroupWithAttrVo> attrGroupWithAttrVos = attrGroupEntities.stream().map(item -> {
                AttrGroupWithAttrVo attrGroupWithAttrVo = new AttrGroupWithAttrVo();
                AttrEntity[] attrEntities = this.findAttrRelationById(item.getAttrGroupId());
                BeanUtils.copyProperties(item, attrGroupWithAttrVo);
                attrGroupWithAttrVo.setAttrs(attrEntities);
                return attrGroupWithAttrVo;
            }).collect(Collectors.toList());
            return attrGroupWithAttrVos.toArray(new AttrGroupWithAttrVo[attrGroupWithAttrVos.size()]);
        }
        return null;
    }

}
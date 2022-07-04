package com.lj.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.CategoryDao;
import com.lj.gulimail.product.entity.CategoryEntity;
import com.lj.gulimail.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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
        //TODO 先查询所有的一级分类,再递归的将每个分类的子分类查询出来并添加到父分类中(排序)
        List<CategoryEntity> categoryEntityList = list.stream()
                .filter(item -> item.getParentCid() == 0)
                .map(item -> {
                    item.setChildrenCategory(findChildrens(item, list));
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

    private List<CategoryEntity> findChildrens(CategoryEntity fatherCategory, List<CategoryEntity> list) {
        List<CategoryEntity> childrenList = list.stream()
                .filter(item -> item.getParentCid() == fatherCategory.getCatId())
                .map(item -> {
                    item.setChildrenCategory(findChildrens(item, list));
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
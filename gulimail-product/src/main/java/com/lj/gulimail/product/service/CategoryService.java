package com.lj.gulimail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.common.utils.PageUtils;
import com.lj.gulimail.product.entity.CategoryEntity;
import com.lj.gulimail.product.vo.Category2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:23:16
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listByTree();

    void deleteMenuByIds(Long[] catIds);

    Long[] findPathById(Long catelogId);

    void updateDetailById(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Category2Vo>> getLevel2Categorys();
}


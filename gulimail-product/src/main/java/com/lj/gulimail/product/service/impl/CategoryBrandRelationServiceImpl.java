package com.lj.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.CategoryBrandRelationDao;
import com.lj.gulimail.product.entity.BrandEntity;
import com.lj.gulimail.product.entity.CategoryBrandRelationEntity;
import com.lj.gulimail.product.service.BrandService;
import com.lj.gulimail.product.service.CategoryBrandRelationService;
import com.lj.gulimail.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存品牌分类的关联详情
     * @param categoryBrandRelation
     */
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        //查询出商品信息
        String brandName = brandService.getById(categoryBrandRelation.getBrandId()).getName();
        //查询出分类信息
        String catelogName = categoryService.getById(categoryBrandRelation.getCatelogId()).getName();
        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(catelogName);
        //保存
        this.save(categoryBrandRelation);
    }

    /**
     * 根据分类id查询品牌
     * @param catId
     * @return
     */
    public List<BrandEntity> findBrandByCatelogId(Long catId) {
        List<CategoryBrandRelationEntity> relationEntities = getBaseMapper().selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        if(relationEntities.isEmpty() || relationEntities == null){
            return null;
        }
        List<Long> ids = relationEntities.stream().map(item -> item.getBrandId()).collect(Collectors.toList());
        List<BrandEntity> brandEntities = brandService.listByIds(ids);
        return brandEntities;
    }

}
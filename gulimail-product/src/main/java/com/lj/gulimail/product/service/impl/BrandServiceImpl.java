package com.lj.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.BrandDao;
import com.lj.gulimail.product.entity.BrandEntity;
import com.lj.gulimail.product.entity.CategoryBrandRelationEntity;
import com.lj.gulimail.product.service.BrandService;
import com.lj.gulimail.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 修改品牌
     * @param brand
     */
    @Transactional
    public void updateDetailById(BrandEntity brand) {
        //修改品牌信息
        this.updateById(brand);
        if (!StringUtils.isEmpty(brand.getName())){
            //若修改了品牌名称,要将关联的表数据一起更新
            CategoryBrandRelationEntity categoryBrandRelation = new CategoryBrandRelationEntity();
            categoryBrandRelation.setBrandName(brand.getName());
            categoryBrandRelationService.update(categoryBrandRelation,
                    new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brand.getBrandId()));
        }
        //TODO 修改其他表关联数据
    }
}
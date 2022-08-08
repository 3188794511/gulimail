package com.lj.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.ProductAttrValueDao;
import com.lj.gulimail.product.entity.AttrEntity;
import com.lj.gulimail.product.entity.ProductAttrValueEntity;
import com.lj.gulimail.product.service.AttrService;
import com.lj.gulimail.product.service.ProductAttrValueService;
import com.lj.gulimail.product.vo.basevo.BaseAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量保存spu基本属性
     * @param spuId
     * @param baseAttrs
     */
    public void saveBatchBaseAttrs(Long spuId, List<BaseAttrs> baseAttrs) {
        if (baseAttrs == null || baseAttrs.isEmpty()){
            return;
        }
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(baseAttr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            AttrEntity attr = attrService.getById(baseAttr.getAttrId());
            productAttrValueEntity.setSpuId(spuId);
            productAttrValueEntity.setAttrId(attr.getAttrId());
            productAttrValueEntity.setAttrName(attr.getAttrName());
            productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
            productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        this.saveBatch(productAttrValueEntities);
    }

    @Override
    public List<ProductAttrValueEntity> getBaseAttrs(Long spuId) {
        List<ProductAttrValueEntity> productAttrValueEntities = this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return productAttrValueEntities;
    }

}
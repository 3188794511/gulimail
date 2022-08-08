package com.lj.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.SkuSaleAttrValueDao;
import com.lj.gulimail.product.entity.SkuSaleAttrValueEntity;
import com.lj.gulimail.product.service.SkuSaleAttrValueService;
import com.lj.gulimail.product.vo.basevo.Attr;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量保存sku的销售属性
     * @param skuId
     * @param attrs
     */
    public void saveBatchSaleAttrs(Long skuId, List<Attr> attrs) {
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
            SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
            BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
            skuSaleAttrValueEntity.setSkuId(skuId);
            return skuSaleAttrValueEntity;
        }).collect(Collectors.toList());
        this.saveBatch(skuSaleAttrValueEntities);
    }


}
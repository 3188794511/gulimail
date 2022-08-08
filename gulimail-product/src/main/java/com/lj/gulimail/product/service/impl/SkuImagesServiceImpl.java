package com.lj.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.SkuImagesDao;
import com.lj.gulimail.product.entity.SkuImagesEntity;
import com.lj.gulimail.product.service.SkuImagesService;
import com.lj.gulimail.product.vo.basevo.Images;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量保存sku的图片集
     * @param skuId
     * @param images
     */
    public void saveBatchImages(Long skuId, List<Images> images) {
        List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
            BeanUtils.copyProperties(image, skuImagesEntity);
            skuImagesEntity.setSkuId(skuId);
            return skuImagesEntity;
        }).filter(image -> Strings.isNotBlank(image.getImgUrl())//过滤掉图片地址为空的图片
        ).collect(Collectors.toList());
        this.saveBatch(skuImagesEntities);
    }

}
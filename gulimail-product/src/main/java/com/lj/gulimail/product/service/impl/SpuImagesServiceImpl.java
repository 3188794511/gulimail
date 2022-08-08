package com.lj.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.SpuImagesDao;
import com.lj.gulimail.product.entity.SpuImagesEntity;
import com.lj.gulimail.product.service.SpuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量保存spu图片
     * @param spuId
     * @param images
     */
    public void saveImages(Long spuId, List<String> images) {
        if (images == null || images.isEmpty()){
            return;
        }
        List<SpuImagesEntity> imagesEntities = images.stream().map(item -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setSpuId(spuId);
            spuImagesEntity.setImgUrl(item);
            spuImagesEntity.setImgName(item.substring(item.lastIndexOf("/") + 1));
            return spuImagesEntity;
        }).collect(Collectors.toList());
        this.saveBatch(imagesEntities);
    }

}
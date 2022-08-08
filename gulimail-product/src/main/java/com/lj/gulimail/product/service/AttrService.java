package com.lj.gulimail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.common.utils.PageUtils;
import com.lj.gulimail.product.entity.AttrEntity;
import com.lj.gulimail.product.entity.ProductAttrValueEntity;
import com.lj.gulimail.product.vo.AttrRespVo;
import com.lj.gulimail.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:23:16
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(AttrVo attr);

    PageUtils queryPageByCatelogId(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getDetailById(Long attrId);

    void updateAttById(AttrVo attr);

    List<ProductAttrValueEntity> getBaseAttrBySpuId(Long spuId);

    void updateBaseAttr(Long spuId, List<ProductAttrValueEntity> entities);
}


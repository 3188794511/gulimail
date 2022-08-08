package com.lj.gulimail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.common.to.es.SkuWareVo;
import com.lj.common.utils.PageUtils;
import com.lj.gulimail.ware.entity.WmsPurchaseDetailEntity;
import com.lj.gulimail.ware.entity.WmsWareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-21 09:29:52
 */
public interface WmsWareSkuService extends IService<WmsWareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addOrUpdateStock(WmsPurchaseDetailEntity detailEntity);

    List<SkuWareVo> hasStockBySkuIds(List<Long> skuIds);
}


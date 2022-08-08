package com.lj.gulimail.ware.dao;

import com.lj.gulimail.ware.entity.WmsWareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-21 09:29:52
 */
@Mapper
public interface WmsWareSkuDao extends BaseMapper<WmsWareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId, @Param("skuName") String skuName);

    void updateStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum,@Param("wareId") Long wareId);

    Integer selectStockBySkuId(@Param("skuId") Long skuId);
}

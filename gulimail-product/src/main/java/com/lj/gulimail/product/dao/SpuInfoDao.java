package com.lj.gulimail.product.dao;

import com.lj.gulimail.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:23:16
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {


    void updateStatusById(@Param("spuId") Long spuId, @Param("type") Integer type);
}

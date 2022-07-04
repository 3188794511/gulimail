package com.lj.gulimail.product.dao;

import com.lj.gulimail.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:23:16
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}

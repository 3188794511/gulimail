package com.lj.gulimail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.common.utils.PageUtils;
import com.lj.gulimail.product.entity.AttrAttrgroupRelationEntity;
import com.lj.gulimail.product.entity.AttrEntity;
import com.lj.gulimail.product.entity.AttrGroupEntity;
import com.lj.gulimail.product.vo.AttrGroupWithAttrVo;

import java.util.Map;

/**
 * 属性分组
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:23:16
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    AttrEntity[]  findAttrRelationById(Long attrGroupId);

    void deleteBatchRelation(AttrAttrgroupRelationEntity[] attrAttrgroupRelationEntities);

    PageUtils findNotAttrRelation(Long attrGroupId,Map<String, Object> params);

    void saveAttrRelation(AttrAttrgroupRelationEntity[] relation);

    AttrGroupWithAttrVo[] findAttrGroupWithAttr(Long catelogId);
}


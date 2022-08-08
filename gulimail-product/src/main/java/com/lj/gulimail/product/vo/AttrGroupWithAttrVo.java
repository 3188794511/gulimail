package com.lj.gulimail.product.vo;

import com.lj.gulimail.product.entity.AttrEntity;
import lombok.Data;

@Data
public class AttrGroupWithAttrVo {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 包含的属性
     */
    private AttrEntity[] attrs;
}

package com.lj.gulimail.product.vo;

import lombok.Data;

@Data
public class AttrRespVo extends AttrVo {
    private String catelogName;//所属分类
    private String groupName;//所属属性分组
    private Long[] catelogPath;//所属分类完整路径
}

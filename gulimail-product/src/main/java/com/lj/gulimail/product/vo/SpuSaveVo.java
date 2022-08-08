/**
  * Copyright 2022 bejson.com 
  */
package com.lj.gulimail.product.vo;

import com.lj.gulimail.product.vo.basevo.BaseAttrs;
import com.lj.gulimail.product.vo.basevo.Bounds;
import com.lj.gulimail.product.vo.basevo.Skus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class SpuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private Integer publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;
}
/**
  * Copyright 2022 bejson.com 
  */
package com.lj.gulimail.product.vo.basevo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class Skus {
    private List<Attr> attr;
    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;
    private List<Images> images;
    private List<String> descar;
    private BigDecimal fullCount;
    private BigDecimal discount;
    private int countStatus; //是否叠加其他优惠[0-不可叠加，1-可叠加]
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
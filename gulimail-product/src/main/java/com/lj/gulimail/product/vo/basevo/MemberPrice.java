/**
  * Copyright 2022 bejson.com 
  */
package com.lj.gulimail.product.vo.basevo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;


}
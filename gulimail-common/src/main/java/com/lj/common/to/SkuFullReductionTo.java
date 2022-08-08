package com.lj.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuFullReductionTo {
    private Long skuId;
    private BigDecimal fullCount;
    private BigDecimal discount;
    private int countStatus; //是否叠加其他优惠[0-不可叠加，1-可叠加]
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}

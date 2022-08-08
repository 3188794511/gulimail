package com.lj.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}

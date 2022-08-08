package com.lj.gulimail.ware.vo;

import lombok.Data;

@Data
public class PurchaseDetailDoneVo {
    private Long itemId;//采购项id
    private Integer status;//状态
    private String reason;//失败原因
}

package com.lj.gulimail.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseDoneVo {
    private Long id; //采购单id
    private List<PurchaseDetailDoneVo> items;
}

package com.lj.gulimail.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 二级分类Vo
 */
public class Category2Vo {
    private String catalog1Id;
    private List<Category3Vo> catalog3List;
    private String id;
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    /**
     * 三级分类Vo
     */
    public static class Category3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}

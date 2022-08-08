package com.lj.common.constant;

public class ProductConstant {
    public enum ProductState{
        NEW(0,"新建"),PUBLISH(1,"上架"),DOWN(2,"下架");

        ProductState(Integer type, String descript) {
            this.type = type;
            this.descript = descript;
        }

        public Integer getType() {
            return type;
        }

        public String getDescript() {
            return descript;
        }

        private Integer type;
        private String descript;
    }
}

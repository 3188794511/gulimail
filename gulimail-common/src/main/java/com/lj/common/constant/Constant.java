package com.lj.common.constant;

public class Constant {
    public enum Product {
        BASE_TYPE(1,"base","基本属性"),
        SALE_TYPE(0,"sale","销售属性");

        private Integer type;
        private String eq;
        private String descript;

        Product(Integer type, String eq, String descript) {
            this.type = type;
            this.eq = eq;
            this.descript = descript;
        }

        public Integer getType() {
            return type;
        }

        public String getDescript() {
            return descript;
        }

        public String getEq() {
            return eq;
        }
    }

}

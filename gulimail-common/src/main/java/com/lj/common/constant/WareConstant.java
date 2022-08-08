package com.lj.common.constant;

public class WareConstant {
    public enum UnReceivePurchase{


        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        PURCHASED(2,"正在采购"),
        FINISHED(3,"已完成"),
        HASERROR(4,"采购失败");

        private Integer type;
        private String descript;

        UnReceivePurchase(Integer type,String descript) {
            this.type = type;
            this.descript = descript;
        }

        public Integer getType() {
            return type;
        }

        public String getDescript() {
            return descript;
        }

    }
}

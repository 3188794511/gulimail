package com.lj.common.constant;

public class ResultConstant {

    public enum Message{
        UP_FAIL(10000,"商品上架失败");

        Message(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        private Integer code;
        private String msg;
    }

}

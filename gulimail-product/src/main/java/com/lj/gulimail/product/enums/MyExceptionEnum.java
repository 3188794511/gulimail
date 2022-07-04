package com.lj.gulimail.product.enums;


/**
 * 异常枚举
 */
public enum MyExceptionEnum {
    VALID_EXCEPTION(401,"提交的数据不合法"),
    COMMON_EXCEPTION(500,"服务器异常");

    private Integer code;
    private String msg;
    MyExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

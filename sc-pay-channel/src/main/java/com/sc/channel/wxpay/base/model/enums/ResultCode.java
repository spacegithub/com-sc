package com.sc.channel.wxpay.base.model.enums;


public enum ResultCode {
    SUCCESS("SUCCESS"), FAIL("FAIL");
    private String code;

    ResultCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

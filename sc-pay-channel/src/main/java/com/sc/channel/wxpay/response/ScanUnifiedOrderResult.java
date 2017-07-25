package com.sc.channel.wxpay.response;

public class ScanUnifiedOrderResult extends  UnifiedOrderResult {

    private String codeUrl;

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }
}

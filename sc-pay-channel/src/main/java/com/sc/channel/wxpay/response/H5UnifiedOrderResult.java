package com.sc.channel.wxpay.response;


public class H5UnifiedOrderResult extends UnifiedOrderResult {

    /**
     * mweb_url为拉起微信支付收银台的中间页面，可通过访问该url来拉起微信客户端，完成支付,mweb_url的有效期为5分钟。
     */
    private String mwebUrl;

    public String getMwebUrl() {
        return mwebUrl;
    }

    public void setMwebUrl(String mwebUrl) {
        this.mwebUrl = mwebUrl;
    }
}

package com.sc.channel.alipay;


public class AliPayConf {
    
    
    private String partner;
    
    private String key;
    
    private String input_charset = "utf-8";
    
    
    private String sign_type = "RSA";
    
    private String alipayGateway;
    
    private String serviceName;


    public AliPayConf(String partner, String privateKey, String alipayGateway, String serviceName) {
        this.partner = partner;
        this.key = privateKey;
        this.alipayGateway=alipayGateway;
        this.serviceName=serviceName;
    }

    public String getInput_charset() {
        return input_charset;
    }

    public String getSign_type() {
        return sign_type;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAlipayGateway() {
        return alipayGateway;
    }

    public void setAlipayGateway(String alipayGateway) {
        this.alipayGateway = alipayGateway;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}

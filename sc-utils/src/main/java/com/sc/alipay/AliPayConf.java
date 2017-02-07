package com.sc.alipay;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class AliPayConf {
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 合作身份者ID，以2088开头由16位纯数字组成的字符串
    private String partner;
    // 商户的私钥
    private String key;
    // 字符编码格式 目前支持 gbk 或 utf-8
    private String input_charset = "utf-8";
    //↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
    // 签名方式 不需修改
    private String sign_type = "RSA";
    /**
     * 支付宝网关
     */
    private String alipayGateway;
    /**
     * 服务接口
     */
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

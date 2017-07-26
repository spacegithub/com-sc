package com.sc.channel.wxpay;



import com.sc.channel.wxpay.model.enums.PayOrderField;
import com.sc.channel.wxpay.model.enums.PayQueryField;
import com.sc.channel.wxpay.request.PayQueryParam;
import com.sc.channel.wxpay.request.PayQueryResult;
import com.sc.channel.wxpay.utils.EncryptUtil;
import com.sc.channel.wxpay.utils.ValidateUtil;
import com.sc.channel.wxpay.utils.WecatSignUtil;
import com.sc.channel.wxpay.utils.WeixinUtil;
import com.sc.utils.beanmap.BeanUtil;
import com.sc.utils.utils.XmlUtil;

import java.util.Map;

/**
 * 微信订单查詢

 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class QueryOrderCore extends  OrderCore {


    public QueryOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    /**
     *
     * @param outTradeNo 商户系统内部的订单号，当没提供transactionId时需要传这个
     * @param orderUrl
     * @return
     */
    public PayQueryResult queryOrderByOutTradeNo(String outTradeNo, String orderUrl){
        return  queryOrder(outTradeNo,null, orderUrl);
    }

    /**
     *
     * @param transactionId 微信的订单号，优先使用
     * @param orderUrl
     * @return
     */
    public PayQueryResult queryOrderByTransactionId(String transactionId, String orderUrl){
        return  queryOrder(null,transactionId, orderUrl);
    }

    /**
     *
     * @param outTradeNo 商户系统内部的订单号，当没提供transactionId时需要传这个
     * @param transactionId 微信的订单号，优先使用
     * @param orderUrl
     * @return
     */

    private PayQueryResult queryOrder(String outTradeNo, String transactionId, String orderUrl) {
        PayQueryParam param = new PayQueryParam();
        // 基本信息
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());

        param.setOutTradeNo(outTradeNo); // 客户订单号
        param.setTransactionId(transactionId); //微信订单号

        //签名
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); // 参数列表
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); // 计算sign
        data.put(PayOrderField.SIGN.getField(), param.getSign()); // sign放到map中，为后续转xml

        // 校验参数是否齐全
        ValidateUtil.validate(PayQueryField.values(), data);

        // 转成xml格式
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);
        // 发送支付请求
        String resultStr = null;
        try {
            resultStr = WeixinUtil.postXml(orderUrl, xml);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("发送请求失败");
        }
        System.out.println("result=" + resultStr);

        Map<String, Object> resultMap = WecatSignUtil.doVerifySign(resultStr, wecatPayConfig.getApiKey());


        PayQueryResult result = BeanUtil.map2Object(PayQueryResult.class, resultMap);

        return result;
    }
}

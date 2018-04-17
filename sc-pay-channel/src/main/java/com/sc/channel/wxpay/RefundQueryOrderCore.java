package com.sc.channel.wxpay;


import com.sc.channel.wxpay.model.enums.PayOrderField;
import com.sc.channel.wxpay.model.enums.PayRefundQueryField;
import com.sc.channel.wxpay.request.PayRefundQueryParam;
import com.sc.channel.wxpay.response.PayRefundQueryResult;
import com.sc.channel.wxpay.utils.EncryptUtil;
import com.sc.channel.wxpay.utils.ValidateUtil;
import com.sc.channel.wxpay.utils.WecatSignUtil;
import com.sc.channel.wxpay.utils.WeixinUtil;
import com.sc.utils.beanmap.BeanUtil;
import com.sc.utils.utils.xml.XmlUtil;

import java.util.Map;


public class RefundQueryOrderCore extends OrderCore {
    public RefundQueryOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    
    public PayRefundQueryResult refundQueryByOutTradeNo(String outTradeNo, String orderUrl) {
        return refundQuery(outTradeNo, null, null, null, orderUrl);
    }

    
    public PayRefundQueryResult refundQueryByTransactionId(String transactionId, String orderUrl) {
        return refundQuery(null, transactionId, null, null, orderUrl);
    }

    
    public PayRefundQueryResult refundQueryByoutRefundNo(String outRefundNo, String orderUrl) {
        return refundQuery(null, null, outRefundNo, null, orderUrl);
    }

    
    public PayRefundQueryResult refundQueryByrefundId(String refundId, String orderUrl) {
        return refundQuery(null, null, null, refundId, orderUrl);
    }

    
    private PayRefundQueryResult refundQuery(String outTradeNo, String transactionId, String outRefundNo, String refundId, String orderUrl) {
        PayRefundQueryParam param = new PayRefundQueryParam();
        
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());

        param.setOutTradeNo(outTradeNo); 
        param.setTransactionId(transactionId); 


        
        param.setOutRefundNo(outRefundNo);
        param.setRefundId(refundId);

        
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); 
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); 
        data.put(PayOrderField.SIGN.getField(), param.getSign()); 

        
        ValidateUtil.validate(PayRefundQueryField.values(), data);

        
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);
        
        String resultStr = WeixinUtil.postXml(orderUrl, xml);

        System.out.println("result=" + resultStr);

        Map<String, Object> resultMap = WecatSignUtil.doVerifySign(resultStr, wecatPayConfig.getApiKey());


        PayRefundQueryResult result = BeanUtil.map2Object(PayRefundQueryResult.class, resultMap);

        return result;
    }
}

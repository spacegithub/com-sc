package com.sc.channel.wxpay;


import com.sc.channel.wxpay.model.enums.PayOrderField;
import com.sc.channel.wxpay.model.enums.PayRefundField;
import com.sc.channel.wxpay.request.PayRefundParam;
import com.sc.channel.wxpay.response.PayRefundResult;
import com.sc.channel.wxpay.utils.EncryptUtil;
import com.sc.channel.wxpay.utils.ValidateUtil;
import com.sc.channel.wxpay.utils.WecatSignUtil;
import com.sc.channel.wxpay.utils.WeixinUtil;
import com.sc.utils.beanmap.BeanUtil;
import com.sc.utils.utils.xml.XmlUtil;

import java.util.Map;


public class RefundOrderCore extends OrderCore {
    public RefundOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    
    public PayRefundResult refundOrderByOutTradeNo(String outTradeNo, Long totalFee, Long refundFee, String outRefundNo, String orderUrl) {
        return refundOrder(outTradeNo, null, totalFee, refundFee, outRefundNo, orderUrl);
    }

    
    public PayRefundResult refundOrderByTransactionId(String transactionId, Long totalFee, Long refundFee, String outRefundNo, String orderUrl) {
        return refundOrder(null, transactionId, totalFee, refundFee, outRefundNo, orderUrl);
    }

    
    private PayRefundResult refundOrder(String outTradeNo, String transactionId, Long totalFee, Long refundFee, String outRefundNo, String orderUrl) {
        PayRefundParam param = new PayRefundParam();
        
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());

        param.setOutTradeNo(outTradeNo); 
        param.setTransactionId(transactionId); 


        
        param.setOutRefundNo(outRefundNo);
        param.setTotalFee(totalFee); 
        param.setRefundFee(refundFee); 
        param.setOpUserId(wecatPayConfig.getMchId());

        
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); 
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); 
        data.put(PayOrderField.SIGN.getField(), param.getSign()); 

        
        ValidateUtil.validate(PayRefundField.values(), data);

        
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);
        
        String resultStr = null;
        try {
            resultStr = WeixinUtil.postXmlWithKey(orderUrl, xml, wecatPayConfig.getKeyInput(), wecatPayConfig.getMchId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("发送请求失败");
        }
        System.out.println("result=" + resultStr);
        Map<String, Object> resultMap = WecatSignUtil.doVerifySign(resultStr, wecatPayConfig.getApiKey());


        PayRefundResult result = BeanUtil.map2Object(PayRefundResult.class, resultMap);

        return result;
    }

}

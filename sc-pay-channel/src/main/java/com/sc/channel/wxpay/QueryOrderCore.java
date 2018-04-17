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
import com.sc.utils.utils.xml.XmlUtil;

import java.util.Map;


public class QueryOrderCore extends  OrderCore {


    public QueryOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    
    public PayQueryResult queryOrderByOutTradeNo(String outTradeNo, String orderUrl){
        return  queryOrder(outTradeNo,null, orderUrl);
    }

    
    public PayQueryResult queryOrderByTransactionId(String transactionId, String orderUrl){
        return  queryOrder(null,transactionId, orderUrl);
    }

    

    private PayQueryResult queryOrder(String outTradeNo, String transactionId, String orderUrl) {
        PayQueryParam param = new PayQueryParam();
        
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());

        param.setOutTradeNo(outTradeNo); 
        param.setTransactionId(transactionId); 

        
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); 
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); 
        data.put(PayOrderField.SIGN.getField(), param.getSign()); 

        
        ValidateUtil.validate(PayQueryField.values(), data);

        
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);
        
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

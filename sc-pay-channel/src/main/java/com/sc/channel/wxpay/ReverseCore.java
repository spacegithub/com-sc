package com.sc.channel.wxpay;

import com.sc.channel.wxpay.model.enums.PayOrderField;
import com.sc.channel.wxpay.request.PayReverseParm;
import com.sc.channel.wxpay.response.PayReverseResult;
import com.sc.channel.wxpay.utils.EncryptUtil;
import com.sc.channel.wxpay.utils.WecatSignUtil;
import com.sc.channel.wxpay.utils.WeixinUtil;
import com.sc.utils.beanmap.BeanUtil;
import com.sc.utils.utils.xml.XmlUtil;

import java.util.Map;


public class ReverseCore extends  OrderCore {
    public ReverseCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    
    public PayReverseResult reverse(String outTradeNo, String orderUrl){
        PayReverseParm param = new PayReverseParm();
        
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());
        param.setOutTradeNo(outTradeNo); 
        param.setTransactionId(null); 


        
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); 
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); 
        data.put(PayOrderField.SIGN.getField(), param.getSign()); 



        
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


        PayReverseResult result = BeanUtil.map2Object(PayReverseResult.class, resultMap);

        return result;

    }
}

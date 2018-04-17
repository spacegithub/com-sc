package com.sc.channel.wxpay;


import com.sc.channel.wxpay.model.enums.PayBillField;
import com.sc.channel.wxpay.model.enums.PayOrderField;
import com.sc.channel.wxpay.request.PayBillParam;
import com.sc.channel.wxpay.utils.EncryptUtil;
import com.sc.channel.wxpay.utils.ValidateUtil;
import com.sc.channel.wxpay.utils.WecatSignUtil;
import com.sc.channel.wxpay.utils.WeixinUtil;
import com.sc.utils.beanmap.BeanUtil;
import com.sc.utils.utils.xml.XmlUtil;

import java.util.Map;


public class DownloadBillOrderCore extends OrderCore {
    public DownloadBillOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    
    public String downloadBill(String billDate,
                               String billType, String orderUrl) {

        PayBillParam param = new PayBillParam();
        
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());

        
        param.setBillDate(billDate);
        param.setBill_type(billType);


        
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); 
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); 
        data.put(PayOrderField.SIGN.getField(), param.getSign()); 

        
        ValidateUtil.validate(PayBillField.values(), data);

        
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);

        WecatSignUtil.doVerifySign(xml, wecatPayConfig.getApiKey());


        
        String resultStr = WeixinUtil.postXml(orderUrl, xml);
        System.out.println("result=" + resultStr);

        return resultStr;
    }
}

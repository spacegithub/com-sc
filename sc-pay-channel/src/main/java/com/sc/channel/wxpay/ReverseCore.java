package com.sc.channel.wxpay;

import com.sc.channel.wxpay.model.enums.PayOrderField;
import com.sc.channel.wxpay.request.PayReverseParm;
import com.sc.channel.wxpay.response.PayReverseResult;
import com.sc.channel.wxpay.utils.EncryptUtil;
import com.sc.channel.wxpay.utils.WecatSignUtil;
import com.sc.channel.wxpay.utils.WeixinUtil;
import com.sc.utils.utils.BeanUtil;
import com.sc.utils.utils.XmlUtil;

import java.util.Map;


public class ReverseCore extends  OrderCore {
    public ReverseCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    /**
     * 撤销订单
     * @param outTradeNo  商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
     * @param orderUrl
     * @return
     */
    public PayReverseResult reverse(String outTradeNo, String orderUrl){
        PayReverseParm param = new PayReverseParm();
        // 基本信息
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());
        param.setOutTradeNo(outTradeNo); // 客户订单号
        param.setTransactionId(null); //微信订单号


        //签名
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); // 参数列表
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); // 计算sign
        data.put(PayOrderField.SIGN.getField(), param.getSign()); // sign放到map中，为后续转xml



        // 转成xml格式
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);
        // 发送支付请求
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

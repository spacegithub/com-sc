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
import com.sc.utils.utils.XmlUtil;

import java.util.Map;

/**
 * 微信退款

 */
public class RefundOrderCore extends OrderCore {
    public RefundOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    /**
     * @param outTradeNo  商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
     * @param totalFee    支付訂單金額
     * @param refundFee   退款金額
     * @param outRefundNo 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     * @param orderUrl
     * @return
     */
    public PayRefundResult refundOrderByOutTradeNo(String outTradeNo, Long totalFee, Long refundFee, String outRefundNo, String orderUrl) {
        return refundOrder(outTradeNo, null, totalFee, refundFee, outRefundNo, orderUrl);
    }

    /**
     * @param transactionId 微信生成的订单号，在支付通知中有返回
     * @param totalFee      支付訂單金額
     * @param refundFee     退款金額
     * @param outRefundNo   商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     * @param orderUrl
     * @return
     */
    public PayRefundResult refundOrderByTransactionId(String transactionId, Long totalFee, Long refundFee, String outRefundNo, String orderUrl) {
        return refundOrder(null, transactionId, totalFee, refundFee, outRefundNo, orderUrl);
    }

    /**
     * @param outTradeNo    商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
     * @param transactionId 微信生成的订单号，在支付通知中有返回
     * @param totalFee      支付訂單金額
     * @param refundFee     退款金額
     * @param outRefundNo   商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     * @param orderUrl
     * @return
     */
    private PayRefundResult refundOrder(String outTradeNo, String transactionId, Long totalFee, Long refundFee, String outRefundNo, String orderUrl) {
        PayRefundParam param = new PayRefundParam();
        // 基本信息
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());

        param.setOutTradeNo(outTradeNo); // 客户订单号
        param.setTransactionId(transactionId); //微信订单号


        // 业务信息
        param.setOutRefundNo(outRefundNo);
        param.setTotalFee(totalFee); // 总金额
        param.setRefundFee(refundFee); // 退款总金额
        param.setOpUserId(wecatPayConfig.getMchId());

        //签名
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); // 参数列表
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); // 计算sign
        data.put(PayOrderField.SIGN.getField(), param.getSign()); // sign放到map中，为后续转xml

        // 校验参数是否齐全
        ValidateUtil.validate(PayRefundField.values(), data);

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


        PayRefundResult result = BeanUtil.map2Object(PayRefundResult.class, resultMap);

        return result;
    }

}

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

/**
 * 退款查询
 */
public class RefundQueryOrderCore extends OrderCore {
    public RefundQueryOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    /**
     * @param outTradeNo 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
     *                   refund_id、out_refund_no、out_trade_no、transaction_id四个参数必填一个，如果同时存在优先级为：
     *                   <p>
     *                   >out_refund_no>transaction_id>out_trade_no
     * @param orderUrl
     * @return
     */
    public PayRefundQueryResult refundQueryByOutTradeNo(String outTradeNo, String orderUrl) {
        return refundQuery(outTradeNo, null, null, null, orderUrl);
    }

    /**
     * @param transactionId 微信订单号
     *                      refund_id、out_refund_no、out_trade_no、transaction_id四个参数必填一个，如果同时存在优先级为：
     *                      <p>
     *                      >out_refund_no>transaction_id>out_trade_no
     * @param orderUrl
     * @return
     */
    public PayRefundQueryResult refundQueryByTransactionId(String transactionId, String orderUrl) {
        return refundQuery(null, transactionId, null, null, orderUrl);
    }

    /**
     * @param outRefundNo 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     *                    refund_id、out_refund_no、out_trade_no、transaction_id四个参数必填一个，如果同时存在优先级为：
     *                    <p>
     *                    >out_refund_no>transaction_id>out_trade_no
     * @param orderUrl
     * @return
     */
    public PayRefundQueryResult refundQueryByoutRefundNo(String outRefundNo, String orderUrl) {
        return refundQuery(null, null, outRefundNo, null, orderUrl);
    }

    /**
     * @param refundId 微信退款单号
     *                 refund_id、out_refund_no、out_trade_no、transaction_id四个参数必填一个，如果同时存在优先级为：
     *                 <p>
     *                 >out_refund_no>transaction_id>out_trade_no
     * @param orderUrl
     * @return
     */
    public PayRefundQueryResult refundQueryByrefundId(String refundId, String orderUrl) {
        return refundQuery(null, null, null, refundId, orderUrl);
    }

    /**
     * @param outTradeNo    商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
     * @param transactionId 微信订单号
     * @param outRefundNo   商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     * @param refundId      微信退款单号
     *                      refund_id、out_refund_no、out_trade_no、transaction_id四个参数必填一个，如果同时存在优先级为：
     *                      <p>
     *                      >out_refund_no>transaction_id>out_trade_no
     * @param orderUrl
     * @return
     */
    private PayRefundQueryResult refundQuery(String outTradeNo, String transactionId, String outRefundNo, String refundId, String orderUrl) {
        PayRefundQueryParam param = new PayRefundQueryParam();
        // 基本信息
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());

        param.setOutTradeNo(outTradeNo); // 客户订单号
        param.setTransactionId(transactionId); //微信订单号


        // 业务信息
        param.setOutRefundNo(outRefundNo);
        param.setRefundId(refundId);

        //签名
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); // 参数列表
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); // 计算sign
        data.put(PayOrderField.SIGN.getField(), param.getSign()); // sign放到map中，为后续转xml

        // 校验参数是否齐全
        ValidateUtil.validate(PayRefundQueryField.values(), data);

        // 转成xml格式
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);
        // 发送支付请求
        String resultStr = WeixinUtil.postXml(orderUrl, xml);

        System.out.println("result=" + resultStr);

        Map<String, Object> resultMap = WecatSignUtil.doVerifySign(resultStr, wecatPayConfig.getApiKey());


        PayRefundQueryResult result = BeanUtil.map2Object(PayRefundQueryResult.class, resultMap);

        return result;
    }
}

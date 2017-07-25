package com.sc.channel.wxpay;


import com.sc.channel.wxpay.model.enums.PayBillField;
import com.sc.channel.wxpay.model.enums.PayOrderField;
import com.sc.channel.wxpay.request.PayBillParam;
import com.sc.channel.wxpay.utils.EncryptUtil;
import com.sc.channel.wxpay.utils.ValidateUtil;
import com.sc.channel.wxpay.utils.WecatSignUtil;
import com.sc.channel.wxpay.utils.WeixinUtil;
import com.sc.utils.utils.BeanUtil;
import com.sc.utils.utils.XmlUtil;

import java.util.Map;

/**
 * 下载对账单
 */
public class DownloadBillOrderCore extends OrderCore {
    public DownloadBillOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }

    /**
     * @param billDate 下载对账单的日期，格式：20140603
     * @param billType ALL，返回当日所有订单信息，默认值
     *                 <p>
     *                 SUCCESS，返回当日成功支付的订单
     *                 <p>
     *                 REFUND，返回当日退款订单
     *                 <p>
     *                 RECHARGE_REFUND，返回当日充值退款订单（相比其他对账单多一栏“返还手续费”）
     * @param orderUrl
     * @return
     */
    public String downloadBill(String billDate,
                               String billType, String orderUrl) {

        PayBillParam param = new PayBillParam();
        // 基本信息
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());

        //业务信息
        param.setBillDate(billDate);
        param.setBill_type(billType);


        //签名
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); // 参数列表
        param.setSign(WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); // 计算sign
        data.put(PayOrderField.SIGN.getField(), param.getSign()); // sign放到map中，为后续转xml

        // 校验参数是否齐全
        ValidateUtil.validate(PayBillField.values(), data);

        // 转成xml格式
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);

        WecatSignUtil.doVerifySign(xml, wecatPayConfig.getApiKey());


        // 发送支付请求
        String resultStr = WeixinUtil.postXml(orderUrl, xml);
        System.out.println("result=" + resultStr);

        return resultStr;
    }
}

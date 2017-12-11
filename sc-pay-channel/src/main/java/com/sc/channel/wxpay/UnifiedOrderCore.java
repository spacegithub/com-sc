package com.sc.channel.wxpay;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sc.channel.wxpay.base.model.PayResult;
import com.sc.channel.wxpay.base.model.enums.ResultCode;
import com.sc.channel.wxpay.model.enums.PayOrderField;
import com.sc.channel.wxpay.request.PayOrderParam;
import com.sc.channel.wxpay.response.AppUnifiedOrderResult;
import com.sc.channel.wxpay.response.H5UnifiedOrderResult;
import com.sc.channel.wxpay.response.OfficialAccountsUnifiedOrderResult;
import com.sc.channel.wxpay.response.PayOrderResult;
import com.sc.channel.wxpay.response.PunchCardUnifiedOrderResult;
import com.sc.channel.wxpay.response.ScanUnifiedOrderResult;
import com.sc.channel.wxpay.response.UnifiedOrderResult;
import com.sc.channel.wxpay.utils.EncryptUtil;
import com.sc.channel.wxpay.utils.ValidateUtil;
import com.sc.channel.wxpay.utils.WecatSignUtil;
import com.sc.channel.wxpay.utils.WeixinUtil;
import com.sc.utils.beanmap.BeanUtil;
import com.sc.utils.utils.xml.XmlUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * 微信下订单
 *
 * @auth:huzhongying
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class UnifiedOrderCore extends OrderCore {


    public UnifiedOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }


    /**
     * app下订单
     *
     * @param total     交易金额默认为人民币交易，接口中参数支付金额单位为【分】，参数值不能带小数。对账单中的交易金额单位为【元】。
     *                  外币交易的支付金额精确到币种的最小单位，参数值不能带小数点。
     * @param outTradeNo
     * @param attach    非必填
     *                  附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     * @param body      body字段格式
     *                  使用场景 	支付模式 	商品字段规则 	样例 	备注
     *                  PC网站 	扫码支付 	浏览器打开的网站主页title名 -商品概述 	腾讯充值中心-QQ会员充值
     *                  微信浏览器 	公众号支付 	商家名称-销售商品类目 	腾讯-游戏 	线上电商，商家名称必须为实际销售商品的商家
     *                  门店扫码 	公众号支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店扫码 	扫码支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店刷卡 	刷卡支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  第三方手机浏览器 	H5支付 	浏览器打开的移动网页的主页title名-商品概述 	腾讯充值中心-QQ会员充值
     *                  第三方APP 	APP支付 	应用市场上的APP名字-商品概述 	天天爱消除-游戏充值
     * @param sceneInfo 该字段用于上报支付的场景信息,针对H5支付有以下三种场景,请根据对应场景上报,H5支付不建议在APP端使用，针对场景1，2请接入APP支付，不然可能会出现兼容性问题
     *                  <p>
     *                  1，IOS移动应用
     *                  {"h5_info": //h5支付固定传"h5_info"
     *                  {"type": "",  //场景类型
     *                  "app_name": "",  //应用名
     *                  "bundle_id": ""  //bundle_id
     *                  }
     *                  }
     *                  <p>
     *                  2，安卓移动应用
     *                  {"h5_info": //h5支付固定传"h5_info"
     *                  {"type": "",  //场景类型
     *                  "app_name": "",  //应用名
     *                  "package_name": ""  //包名
     *                  }
     *                  }
     *                  <p>
     *                  3，WAP网站应用
     *                  {"h5_info": //h5支付固定传"h5_info"
     *                  {"type": "",  //场景类型
     *                  "wap_url": "",//WAP网站URL地址
     *                  "wap_name": ""  //WAP 网站名
     *                  }
     * @param spbillCreateIp
     * @param orderUrl
     * @param opebId
     * @return
     */
    public H5UnifiedOrderResult unifiedOrderByH5(Long total, String outTradeNo, String attach, String body, String sceneInfo, String spbillCreateIp, String orderUrl, String opebId) {
        H5UnifiedOrderResult h5UnifiedOrderResponse = new H5UnifiedOrderResult();
        String tradeType = "MWEB";

        Map<String, Object> data;

        // 校验参数是否齐全
        try {
            data = getPostObjectMap(total, outTradeNo, attach, body, tradeType, spbillCreateIp, opebId);
            data.put("scene_info", sceneInfo);
            h5UnifiedOrderResponse.setMwebUrl(sendPost(h5UnifiedOrderResponse, data, wecatPayConfig.getKeyInput(), wecatPayConfig.getMchId(), orderUrl).getMwebUrl());

        } catch (Exception e) {
            e.printStackTrace();
            h5UnifiedOrderResponse.setReturnCode(ResultCode.FAIL.getCode());
            return h5UnifiedOrderResponse;
        }


        return h5UnifiedOrderResponse;
    }


    /**
     * app下订单
     *
     * @param total     交易金额默认为人民币交易，接口中参数支付金额单位为【分】，参数值不能带小数。对账单中的交易金额单位为【元】。
     *                  外币交易的支付金额精确到币种的最小单位，参数值不能带小数点。
     * @param outTradeNo 订单号
     * @param attach    非必填
     *                  附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     * @param body      body字段格式
     *                  使用场景 	支付模式 	商品字段规则 	样例 	备注
     *                  PC网站 	扫码支付 	浏览器打开的网站主页title名 -商品概述 	腾讯充值中心-QQ会员充值
     *                  微信浏览器 	公众号支付 	商家名称-销售商品类目 	腾讯-游戏 	线上电商，商家名称必须为实际销售商品的商家
     *                  门店扫码 	公众号支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店扫码 	扫码支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店刷卡 	刷卡支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  第三方手机浏览器 	H5支付 	浏览器打开的移动网页的主页title名-商品概述 	腾讯充值中心-QQ会员充值
     *                  第三方APP 	APP支付 	应用市场上的APP名字-商品概述 	天天爱消除-游戏充值
     * @param spbillCreateIp
     * @param orderUrl
     * @return
     */
    public AppUnifiedOrderResult unifiedOrderByAPP(Long total, String outTradeNo, String attach, String body, String spbillCreateIp, String orderUrl) {
        AppUnifiedOrderResult appUnifiedOrderResponse = new AppUnifiedOrderResult();
        String tradeType = "APP";

        Map<String, Object> data;

        // 校验参数是否齐全
        try {
            data = getPostObjectMap(total, outTradeNo, attach, body, tradeType, spbillCreateIp, null);
            data.remove("openid");
            sendPost(appUnifiedOrderResponse, data, wecatPayConfig.getKeyInput(), wecatPayConfig.getMchId(), orderUrl);
        } catch (Exception e) {
            appUnifiedOrderResponse.setReturnCode(ResultCode.FAIL.getCode());
            return appUnifiedOrderResponse;
        }


        return appUnifiedOrderResponse;
    }

    /**
     * 刷卡下订单
     *
     * @param total     交易金额默认为人民币交易，接口中参数支付金额单位为【分】，参数值不能带小数。对账单中的交易金额单位为【元】。
     *                  外币交易的支付金额精确到币种的最小单位，参数值不能带小数点。
     * @param outTradeNo 订单号
     * @param attach    非必填
     *                  附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     * @param body      body字段格式
     *                  使用场景 	支付模式 	商品字段规则 	样例 	备注
     *                  PC网站 	扫码支付 	浏览器打开的网站主页title名 -商品概述 	腾讯充值中心-QQ会员充值
     *                  微信浏览器 	公众号支付 	商家名称-销售商品类目 	腾讯-游戏 	线上电商，商家名称必须为实际销售商品的商家
     *                  门店扫码 	公众号支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店扫码 	扫码支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店刷卡 	刷卡支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  第三方手机浏览器 	H5支付 	浏览器打开的移动网页的主页title名-商品概述 	腾讯充值中心-QQ会员充值
     *                  第三方APP 	APP支付 	应用市场上的APP名字-商品概述 	天天爱消除-游戏充值
     * @param spbillCreateIp
     * @param orderUrl
     * @return
     */
    public PunchCardUnifiedOrderResult unifiedOrderByPunchCard(Long total, String outTradeNo, String attach, String body, String authCode, String spbillCreateIp, String orderUrl) {
        PunchCardUnifiedOrderResult punchCardUnifiedOrderResult = new PunchCardUnifiedOrderResult();

        Map<String, Object> data;

        // 校验参数是否齐全
        try {
            data = getPostObjectMap(total, outTradeNo, attach, body, null, spbillCreateIp, null);
            data.put("auth_code",authCode);
            data.remove("notify_url");
            data.remove("openid");
            sendPost(punchCardUnifiedOrderResult, data, wecatPayConfig.getKeyInput(), wecatPayConfig.getMchId(), orderUrl);
        } catch (Exception e) {
            punchCardUnifiedOrderResult.setReturnCode(ResultCode.FAIL.getCode());
            return punchCardUnifiedOrderResult;
        }

        return punchCardUnifiedOrderResult;
    }
    /**
     * 微信公衆號下订单
     *
     * @param total     交易金额默认为人民币交易，接口中参数支付金额单位为【分】，参数值不能带小数。对账单中的交易金额单位为【元】。
     *                  外币交易的支付金额精确到币种的最小单位，参数值不能带小数点。
     * @param outTradeNo
     * @param attach    非必填
     *                  附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     * @param body      body字段格式
     *                  使用场景 	支付模式 	商品字段规则 	样例 	备注
     *                  PC网站 	扫码支付 	浏览器打开的网站主页title名 -商品概述 	腾讯充值中心-QQ会员充值
     *                  微信浏览器 	公众号支付 	商家名称-销售商品类目 	腾讯-游戏 	线上电商，商家名称必须为实际销售商品的商家
     *                  门店扫码 	公众号支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店扫码 	扫码支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店刷卡 	刷卡支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  第三方手机浏览器 	H5支付 	浏览器打开的移动网页的主页title名-商品概述 	腾讯充值中心-QQ会员充值
     *                  第三方APP 	APP支付 	应用市场上的APP名字-商品概述 	天天爱消除-游戏充值
     * @param spbillCreateIp
     * @param orderUrl
     * @param opebId
     * @return
     */
    public OfficialAccountsUnifiedOrderResult unifiedOrderByOfficialAccounts(Long total, String outTradeNo, String attach, String body, String spbillCreateIp, String orderUrl, String opebId) {
        OfficialAccountsUnifiedOrderResult officialAccountsUnifiedOrderResponse = new OfficialAccountsUnifiedOrderResult();
        String tradeType = "JSAPI";

        Map<String, Object> data;

        // 校验参数是否齐全
        try {
            data = getPostObjectMap(total, outTradeNo, attach, body, tradeType, spbillCreateIp, opebId);
            PayOrderResult payOrderResult = sendPost(officialAccountsUnifiedOrderResponse, data, wecatPayConfig.getKeyInput(), wecatPayConfig.getMchId(), orderUrl);
            System.out.println(payOrderResult.getResultCode());
        } catch (Exception e) {
            officialAccountsUnifiedOrderResponse.setReturnCode(ResultCode.FAIL.getCode());
            return officialAccountsUnifiedOrderResponse;
        }


        return officialAccountsUnifiedOrderResponse;
    }

    /**
     * 微信公衆號下订单
     *
     * @param total     交易金额默认为人民币交易，接口中参数支付金额单位为【分】，参数值不能带小数。对账单中的交易金额单位为【元】。
     *                  外币交易的支付金额精确到币种的最小单位，参数值不能带小数点。
     * @param outTradeNo
     * @param attach    非必填
     *                  附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     * @param body      body字段格式
     *                  使用场景 	支付模式 	商品字段规则 	样例 	备注
     *                  PC网站 	扫码支付 	浏览器打开的网站主页title名 -商品概述 	腾讯充值中心-QQ会员充值
     *                  微信浏览器 	公众号支付 	商家名称-销售商品类目 	腾讯-游戏 	线上电商，商家名称必须为实际销售商品的商家
     *                  门店扫码 	公众号支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店扫码 	扫码支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  门店刷卡 	刷卡支付 	店名-销售商品类目 	小张南山店-超市 	线下门店支付
     *                  第三方手机浏览器 	H5支付 	浏览器打开的移动网页的主页title名-商品概述 	腾讯充值中心-QQ会员充值
     *                  第三方APP 	APP支付 	应用市场上的APP名字-商品概述 	天天爱消除-游戏充值
     * @param spbillCreateIp
     * @param orderUrl
     * @return
     */
    public ScanUnifiedOrderResult unifiedOrderByScan(Long total, String outTradeNo, String attach, String body, String spbillCreateIp, String orderUrl) {
        ScanUnifiedOrderResult scanUnifiedOrderResult = new ScanUnifiedOrderResult();
        String tradeType = "NATIVE";

        Map<String, Object> data;

        // 校验参数是否齐全
        try {
            data = getPostObjectMap(total, outTradeNo, attach, body, tradeType, spbillCreateIp, null);


            scanUnifiedOrderResult.setCodeUrl(sendPost(scanUnifiedOrderResult, data, wecatPayConfig.getKeyInput(), wecatPayConfig.getMchId(), orderUrl).getCodeUrl());
            System.out.println(scanUnifiedOrderResult.getResultCode());
        } catch (Exception e) {
            scanUnifiedOrderResult.setReturnCode(ResultCode.FAIL.getCode());
            return scanUnifiedOrderResult;
        }


        return scanUnifiedOrderResult;
    }

    protected PayOrderResult sendPost(UnifiedOrderResult unifiedOrderResult, Map<String, Object> data, InputStream in, String mchId, String orderUrl) throws Exception {

        data.put(PayOrderField.SIGN.getField(), WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); // sign放到map中，为后续转xml
        ValidateUtil.validate(PayOrderField.values(), data);

        // 转成xml格式
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);
        // 发送支付请求
        String resultStr = WeixinUtil.postXmlWithKey(orderUrl, xml, in, mchId);
        System.out.println("result=" + resultStr);

        // 校验返回结果 签名
        Map<String, Object> resultMap = WecatSignUtil.doVerifySign(resultStr, wecatPayConfig.getApiKey());


        PayOrderResult result = BeanUtil.map2Object(PayOrderResult.class, resultMap);

        if (ResultCode.SUCCESS.getCode().equals(result.getReturnCode())
                && ResultCode.SUCCESS.getCode().equals(result.getResultCode())) {
            unifiedOrderResult.setResultCode(ResultCode.SUCCESS.getCode());
            unifiedOrderResult.setReturnCode(ResultCode.SUCCESS.getCode());
        } else {
            unifiedOrderResult.setResultCode(ResultCode.FAIL.getCode());
            unifiedOrderResult.setReturnCode(ResultCode.FAIL.getCode());

        }
        unifiedOrderResult.setReturnMsg(result.getReturnMsg());
        unifiedOrderResult.setErrCode(result.getErrCode());
        unifiedOrderResult.setErrCodeDes(result.getErrCodeDes());
        unifiedOrderResult.setPrepayId(result.getPrepayId());
        unifiedOrderResult.setSign(result.getSign());
        unifiedOrderResult.setReturnMsg(result.getReturnMsg());
        unifiedOrderResult.setTradeType(result.getTradeType());
        unifiedOrderResult.setMchId(result.getMchId());
        unifiedOrderResult.setAppId(result.getAppid());
        unifiedOrderResult.setTimeEnd(result.getTimeEnd());
        return result;
    }

    protected Map<String, Object> getPostObjectMap(Long total, String outTradeNo, String attach, String body, String tradeType, String spbillCreateIp, String opebId) {
        PayOrderParam param = new PayOrderParam();
        // 基本信息
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());
        param.setTradeType(tradeType); // 公众号支付
        if (opebId != null) {
            param.setOpenid(opebId); // openId!!

        }
        param.setSpbillCreateIp(spbillCreateIp);
        //param.setLimitPay("no_credit"); // 禁止用信用卡

        param.setNotifyUrl(wecatPayConfig.getNotifyUrl()); // 支付成功回调url

        // 业务相关参数

        param.setAttach(attach);
        param.setBody(body);
        param.setTotalFee(total);
        param.setOutTradeNo(outTradeNo); // 客户订单号

        //签名
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); // 参数列表

        return data;
    }

    private JSONObject genJsParam(PayResult payResult) {
        long timestamp = System.currentTimeMillis() / 1000;
        String nonceStr = EncryptUtil.random();

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("appId", wecatPayConfig.getAppId());
        data.put("timeStamp", timestamp);
        data.put("nonceStr", nonceStr);
        data.put("package", "prepay_id=" + payResult.getPrepayId());
        data.put("signType", "MD5");

        data.put("paySign", WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); // 计算sign

        JSONObject ret = JSONObject.parseObject(JSON.toJSONString(data));
        return ret;
    }
}

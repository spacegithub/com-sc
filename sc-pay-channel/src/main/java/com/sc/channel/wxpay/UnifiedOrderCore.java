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



public class UnifiedOrderCore extends OrderCore {


    public UnifiedOrderCore(WecatPayConfig wecatPayConfig) {
        super(wecatPayConfig);
    }


    
    public H5UnifiedOrderResult unifiedOrderByH5(Long total, String outTradeNo, String attach, String body, String sceneInfo, String spbillCreateIp, String orderUrl, String opebId) {
        H5UnifiedOrderResult h5UnifiedOrderResponse = new H5UnifiedOrderResult();
        String tradeType = "MWEB";

        Map<String, Object> data;

        
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


    
    public AppUnifiedOrderResult unifiedOrderByAPP(Long total, String outTradeNo, String attach, String body, String spbillCreateIp, String orderUrl) {
        AppUnifiedOrderResult appUnifiedOrderResponse = new AppUnifiedOrderResult();
        String tradeType = "APP";

        Map<String, Object> data;

        
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

    
    public PunchCardUnifiedOrderResult unifiedOrderByPunchCard(Long total, String outTradeNo, String attach, String body, String authCode, String spbillCreateIp, String orderUrl) {
        PunchCardUnifiedOrderResult punchCardUnifiedOrderResult = new PunchCardUnifiedOrderResult();

        Map<String, Object> data;

        
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
    
    public OfficialAccountsUnifiedOrderResult unifiedOrderByOfficialAccounts(Long total, String outTradeNo, String attach, String body, String spbillCreateIp, String orderUrl, String opebId) {
        OfficialAccountsUnifiedOrderResult officialAccountsUnifiedOrderResponse = new OfficialAccountsUnifiedOrderResult();
        String tradeType = "JSAPI";

        Map<String, Object> data;

        
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

    
    public ScanUnifiedOrderResult unifiedOrderByScan(Long total, String outTradeNo, String attach, String body, String spbillCreateIp, String orderUrl) {
        ScanUnifiedOrderResult scanUnifiedOrderResult = new ScanUnifiedOrderResult();
        String tradeType = "NATIVE";

        Map<String, Object> data;

        
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

        data.put(PayOrderField.SIGN.getField(), WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); 
        ValidateUtil.validate(PayOrderField.values(), data);

        
        String xml = XmlUtil.toXml(data);
        System.out.println("post.xml=" + xml);
        
        String resultStr = WeixinUtil.postXmlWithKey(orderUrl, xml, in, mchId);
        System.out.println("result=" + resultStr);

        
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
        
        param.setAppid(wecatPayConfig.getAppId());
        param.setMchId(wecatPayConfig.getMchId());
        param.setTradeType(tradeType); 
        if (opebId != null) {
            param.setOpenid(opebId); 

        }
        param.setSpbillCreateIp(spbillCreateIp);
        

        param.setNotifyUrl(wecatPayConfig.getNotifyUrl()); 

        

        param.setAttach(attach);
        param.setBody(body);
        param.setTotalFee(total);
        param.setOutTradeNo(outTradeNo); 

        
        param.setNonceStr(EncryptUtil.random());
        Map<String, Object> data = BeanUtil.object2Map(param); 

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

        data.put("paySign", WecatSignUtil.sign(data, wecatPayConfig.getApiKey())); 

        JSONObject ret = JSONObject.parseObject(JSON.toJSONString(data));
        return ret;
    }
}

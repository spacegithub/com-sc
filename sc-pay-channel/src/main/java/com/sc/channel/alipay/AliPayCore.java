package com.sc.channel.alipay;

import com.sc.utils.rest.httpClient.HttpProtocolHandler;
import com.sc.utils.rest.httpClient.HttpRequest;
import com.sc.utils.rest.httpClient.HttpResponse;
import com.sc.utils.rest.httpClient.HttpResultType;
import com.sc.utils.encrypt.MD5;
import com.sc.utils.encrypt.RSATools;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class AliPayCore {
    private AliPayConf aliPayConf;

    public AliPayCore(AliPayConf aliPayConf) {
        this.aliPayConf = aliPayConf;
    }

    
    private String buildRequestMysign(Map<String, String> sPara) {
        String prestr = createLinkString(sPara); 
        String mysign = "";
        if ("MD5".equals(aliPayConf.getSign_type())) {
            mysign = MD5.sign(prestr, aliPayConf.getKey(), aliPayConf.getInput_charset());
        } else {
            mysign = RSATools.sign(prestr, aliPayConf.getKey(), aliPayConf.getInput_charset());
        }
        return mysign;
    }


    
    private Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
        
        Map<String, String> sPara = paraFilter(sParaTemp);
        
        String mysign = buildRequestMysign(sPara);

        
        sPara.put("sign", mysign);
        sPara.put("sign_type", aliPayConf.getSign_type());

        return sPara;
    }

    
    public String buildRequest(String strParaFileName, String strFilePath, Map<String, String> sParaTemp) throws Exception {
        sParaTemp.put("partner",aliPayConf.getPartner());
        sParaTemp.put("_input_charset",aliPayConf.getInput_charset());
        sParaTemp.put("service",aliPayConf.getServiceName());
        
        Map<String, String> sPara = buildRequestPara(sParaTemp);

        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();

        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        
        request.setCharset(aliPayConf.getInput_charset());

        request.setParameters(generatNameValuePair(sPara));
        request.setUrl(aliPayConf.getAlipayGateway() );

        HttpResponse response = httpProtocolHandler.execute(request, strParaFileName, strFilePath);
        if (response == null) {
            return null;
        }

        String strResult = response.getStringResult();

        return strResult;
    }

    
    public String buildRequest(Map<String, String> sParaTemp) throws Exception {
        return buildRequest("", "", sParaTemp);
    }

    public String buildRequestUrl(Map<String, String> sParaTemp)throws Exception {
        sParaTemp.put("partner", aliPayConf.getPartner());
        sParaTemp.put("_input_charset", aliPayConf.getInput_charset());
        sParaTemp.put("service", aliPayConf.getServiceName());
        
        Map<String, String> sPara = buildRequestPara(sParaTemp);
        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        
        request.setCharset(aliPayConf.getInput_charset());
        request.setParameters(generatNameValuePair(sPara));
        request.setUrl(aliPayConf.getAlipayGateway());
        StringBuilder sb = new StringBuilder();
        for (NameValuePair parameters : request.getParameters()) {
            sb.append(parameters.getName() + "=" + URLEncoder.encode(parameters.getValue(), "UTF-8")+ "&");
        }
        String param = StringUtils.removeEnd(sb.toString(), "&");
        System.out.println("-->" +param);
        return request.getUrl() + param;
    }
    
    private NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return nameValuePair;
    }

    
    private Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    
    private String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
}

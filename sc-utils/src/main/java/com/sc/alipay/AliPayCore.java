package com.sc.alipay;

import com.sc.rest.httpClient.HttpProtocolHandler;
import com.sc.rest.httpClient.HttpRequest;
import com.sc.rest.httpClient.HttpResponse;
import com.sc.rest.httpClient.HttpResultType;
import com.sc.utils.MD5;
import com.sc.utils.RSA;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class AliPayCore {
    private AliPayConf aliPayConf;

    public AliPayCore(AliPayConf aliPayConf) {
        this.aliPayConf = aliPayConf;
    }

    /**
     * 生成签名结果
     *
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
    private String buildRequestMysign(Map<String, String> sPara) {
        String prestr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if ("MD5".equals(aliPayConf.getSign_type())) {
            mysign = MD5.sign(prestr, aliPayConf.getKey(), aliPayConf.getInput_charset());
        } else {
            mysign = RSA.signAlipay(prestr, aliPayConf.getKey(), aliPayConf.getInput_charset());
        }
        return mysign;
    }


    /**
     * 生成要请求给支付宝的参数数组
     *
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    private Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
        //除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        //生成签名结果
        String mysign = buildRequestMysign(sPara);

        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", aliPayConf.getSign_type());

        return sPara;
    }

    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果
     * 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值
     * 如：buildRequest("", "",sParaTemp)
     *
     * @param strParaFileName 文件类型的参数名
     * @param strFilePath     文件路径
     * @param sParaTemp       请求参数数组
     * @return 支付宝处理结果
     */
    public String buildRequest(String strParaFileName, String strFilePath, Map<String, String> sParaTemp) throws Exception {
        sParaTemp.put("partner",aliPayConf.getPartner());
        sParaTemp.put("_input_charset",aliPayConf.getInput_charset());
        sParaTemp.put("service",aliPayConf.getServiceName());
        //待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp);

        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();

        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        //设置编码集
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

    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果
     * 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值
     * 如：buildRequest("", "",sParaTemp)
     *
     * @param sParaTemp 请求参数数组
     * @return 支付宝处理结果
     */
    public String buildRequest(Map<String, String> sParaTemp) throws Exception {
        return buildRequest("", "", sParaTemp);
    }

    public String buildRequestUrl(Map<String, String> sParaTemp)throws Exception {
        sParaTemp.put("partner", aliPayConf.getPartner());
        sParaTemp.put("_input_charset", aliPayConf.getInput_charset());
        sParaTemp.put("service", aliPayConf.getServiceName());
        //待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp);
        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        //设置编码集
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
    /**
     * MAP类型数组转换成NameValuePair类型
     *
     * @param properties MAP类型数组
     * @return NameValuePair类型数组
     */
    private NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return nameValuePair;
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
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

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
}

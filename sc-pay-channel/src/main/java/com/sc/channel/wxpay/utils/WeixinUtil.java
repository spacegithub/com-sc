package com.sc.channel.wxpay.utils;

import com.sc.channel.wxpay.base.model.enums.ResultCode;
import com.sc.utils.utils.xml.XmlUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;


public class WeixinUtil {
    public static final Log logger = LogFactory.getLog(WeixinUtil.class);
    public static final String UTF_8 = "UTF-8";

    
    public static String postXml2(String url, String xml) {
        CloseableHttpClient client = HttpClients.createDefault();
        
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

        String responseBody = null;
        try {
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            httpPost.setHeader(HTTP.CONTENT_ENCODING, "utf-8");

            StringEntity payload = new StringEntity(xml, UTF_8);
            httpPost.setEntity(payload);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = client.execute(httpPost, responseHandler);
        } catch (ClientProtocolException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            client.getConnectionManager().shutdown();
        }
        return responseBody;
    }

    
    public static String postXml(String url, String xml) {
        String result="";
        HttpPost httpPost = new HttpPost(url);
        HttpEntity entity = null;
        logger.info("Weixin post xml :"+xml);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
        try {

            HttpClient client = new DefaultHttpClient();
            StringEntity payload = new StringEntity(xml, "UTF-8");
            httpPost.setEntity(payload);
            HttpResponse response = client.execute(httpPost);
              entity = response.getEntity();
              result = EntityUtils.toString(entity,UTF_8);
            logger.info("Weixin response xml :"+result);

        } catch (Exception e) {
            logger.error("与[" + url + "]通信过程中发生异常,堆栈信息如下", e.getCause());
        } finally {

            try {
                EntityUtils.consume(entity);
            } catch (IOException ex) {
                ex.printStackTrace();
                logger.error("net io exception");
            }
        }
        return result;
    }

    
    public static String postXmlWithKey(String url, String xml, InputStream in, String mchId) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try {
            keyStore.load(in, mchId.toCharArray());
        } finally {
            in.close();
        }
        logger.info("Weixin post xml :"+xml);

        
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, mchId.toCharArray())
                .build();
        
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient client = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();

        StringBuilder sb = new StringBuilder();
        HttpPost httpPost = new HttpPost(url);
        HttpEntity entity = null;

        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
        try {

            StringEntity payload = new StringEntity(xml, "UTF-8");
            httpPost.setEntity(payload);
            HttpResponse response = client.execute(httpPost);
            entity = response.getEntity();
            String text;
            if (entity != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
                while ((text = bufferedReader.readLine()) != null) {
                    sb.append(text);
                }

            }
            logger.info("Weixin response xml :"+sb.toString());

        } catch (Exception e) {
            logger.error("与[" + url + "]通信过程中发生异常,堆栈信息如下", e.getCause());
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException ex) {
                ex.printStackTrace();
                logger.error("net io exception");
            }
        }
        return sb.toString();
    }

    public static String getResult(ResultCode resultCode, String returnMsg) {
        Map<String, Object> data = new HashMap<String, Object>(2);
        data.put("return_code", resultCode.getCode());
        data.put("return_msg", returnMsg);
        return XmlUtil.toXml(data);
    }
}

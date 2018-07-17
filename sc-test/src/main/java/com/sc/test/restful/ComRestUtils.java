package com.sc.test.restful;

import com.alibaba.fastjson.JSON;
import com.sc.test.URLExec;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * <一句话功能简述>
 * <功能详细描述>
 */
public class ComRestUtils {
    private static RestTemplate restTemplate = new RestTemplate();

    static {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        List<MediaType> mimeTypes = new ArrayList<MediaType>();
        mimeTypes.add(MediaType.ALL);
        stringHttpMessageConverter.setSupportedMediaTypes(mimeTypes);
        List<HttpMessageConverter<?>> ls = new ArrayList<HttpMessageConverter<?>>();
        ls.add(stringHttpMessageConverter);
        restTemplate.setMessageConverters(ls);
    }


    public static String postUrl(URLExec urlUtils, String requestBody) {
        return postUrl(urlUtils, requestBody, "");
    }


    public static String postUrl(URLExec urlUtils, Object requestBody) {
        return postUrl(urlUtils, requestBody, "");
    }

    public static String postUrl(URLExec urlUtils, Object requestBody, String userToken) {
        System.out.println("请求头:");
        System.out.println("请求体:" + requestBody);
        Long start = System.currentTimeMillis();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        if (!StringUtils.isEmpty(userToken)) {
            headers.add("userToken", userToken);
            headers.add("header", "{\"applicationCode\":\"H5-PAY\",\"clientId\":\"09f320913f464885b64a35d51910b324\",\"sourceId\":\"M0000009\",\"channel\":\"OP\",\"subChannel\":\"H5\",\"version\":\"1.0.0\",\"userToken\":\"" + userToken + "\"}");
        }
        if (!(requestBody instanceof String)) {
            requestBody = JSON.toJSONString(requestBody);
        }
        HttpEntity<Object> formEntity = new HttpEntity<Object>(requestBody, headers);
        String reMsg = restTemplate.postForObject(urlUtils.toString(), formEntity, String.class);
        Long end = System.currentTimeMillis();
        System.out.println("响应体:" + reMsg);
        System.out.println("总耗时:" + (end - start));
        return reMsg;
    }

}

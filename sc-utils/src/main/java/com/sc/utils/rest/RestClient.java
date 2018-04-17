package com.sc.utils.rest;

import com.alibaba.fastjson.JSONObject;

import org.springframework.http.HttpMethod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;


public class RestClient {
    private int connectionTimout = 30000;
    private int readTimeout = 30000;

    public JSONObject doPostObject(String url, Map<String, String> header, Object request) throws Exception {
        String requestJson = JSONObject.toJSONString(request);
        String responseJson = doPostJson(url, header, requestJson);
        JSONObject response = JSONObject.parseObject(responseJson);
        return response;
    }

    public String doPostJson(String url, Map<String, String> header, String requestJson) throws Exception {
        return doHttpJson(url, header, requestJson, HttpMethod.POST.toString());
    }

    public JSONObject doGetJsonObject(String url, Map<String, String> header) throws Exception {
        String responseJson = doGetJson(url, header);
        JSONObject response = JSONObject.parseObject(responseJson);
        return response;
    }

    
    public String doGetJson(String url, Map<String, String> header) throws Exception {
        return doHttpJson(url, header, null, HttpMethod.GET.toString());
    }

    
    private String doHttpJson(String url, Map<String, String> header, String requestJson, String requestType) throws Exception {
        String response;
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(connectionTimout);
        conn.setReadTimeout(readTimeout);
        if (HttpMethod.POST.matches(requestType)) {
            conn.setRequestMethod(HttpMethod.POST.toString());
            conn.setDoOutput(true);
        } else {
            conn.setRequestMethod(HttpMethod.GET.toString());
            conn.setDoOutput(false);
        }
        conn.setRequestProperty("content-type", "application/json");
        if (header != null) {
            for (String key : header.keySet()) {
                String value = header.get(key);
                conn.setRequestProperty(key, value);
            }
        }
        conn.setRequestProperty("Charset", "UTF-8");
        if (HttpMethod.POST.matches(requestType)) {
            byte[] data = requestJson.getBytes(Charset.forName("utf-8"));
            conn.getOutputStream().write(data);
        }
        int resultCode = conn.getResponseCode();
        if (HttpURLConnection.HTTP_OK == resultCode) {
            StringBuilder sb = new StringBuilder();
            String readLine = new String();
            try (BufferedReader responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
            }
            response = sb.toString();
            conn.disconnect();
        } else {
            conn.disconnect();
            throw new Exception("Http Status Code:" + resultCode);
        }
        return response;
    }
}

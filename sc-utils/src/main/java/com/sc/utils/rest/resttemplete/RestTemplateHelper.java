package com.sc.utils.rest.resttemplete;

import com.sc.utils.mapper.JsonMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RestTemplateHelper {
    private static Log logger = LogFactory.getLog(RestTemplateHelper.class);

    private static RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    private String path;
    private Object requestDate;

    public static RestTemplateHelper init() {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        List<MediaType> mimeTypes = new ArrayList<MediaType>();
        mimeTypes.add(MediaType.ALL);
        stringHttpMessageConverter.setSupportedMediaTypes(mimeTypes);
        List<HttpMessageConverter<?>> ls = new ArrayList<HttpMessageConverter<?>>();
        ls.add(stringHttpMessageConverter);
        restTemplate.setMessageConverters(ls);
        return new RestTemplateHelper();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    
    public RestTemplateHelper build(String path, Object requestDate) {
        SSLUtil.turnOnSslChecking();
        restTemplate = new RestTemplate();
        this.path = path;
        this.requestDate = requestDate;
        return this;
    }

    
    public RestTemplateHelper buildNonHttps(String path, Object requestDate) {
        SSLUtil.turnOffSslChecking();
        CustomSimpleClientHttpRequestFactory factory = new CustomSimpleClientHttpRequestFactory(new NoopHostnameVerifier());
        restTemplate.setRequestFactory(factory);
        this.path = path;
        this.requestDate = requestDate;
        return this;
    }

    
    public RestTemplateHelper buildHttps(String path, Object requestDate) {
        SSLUtil.turnOnSslChecking();
        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(requestFactory);
        this.path = path;
        this.requestDate = requestDate;
        return this;
    }

    public RestTemplateHelper authHeader(Map<String, String> headerMap) {

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    
    public String call() {
        return call("application/json; charset=UTF-8");
    }

    
    public String call(String mediaType) {
        MediaType type = MediaType.parseMediaType(mediaType);
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        if (!(requestDate instanceof String)) {
            requestDate = JsonMapper.nonEmptyMapper().toJson(requestDate);
        }


        HttpEntity<Object> formEntity = new HttpEntity<Object>(requestDate, headers);
        try {
            return restTemplate.postForObject(path, formEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            if (e instanceof HttpServerErrorException) {
                HttpServerErrorException he = (HttpServerErrorException) e;
                logger.error(he.getResponseBodyAsString());
            }
            throw e;
        }
    }
}

package com.sc.utils.web;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class MessageResponseBody<T> {
    private static final String SUCCESS = "0";
    private static final String ERROR = "1";
    @JsonIgnore
    private String resultCode;
    private T result;

    private MessageResponseBody(String statusCode) {
        this.resultCode = statusCode;
    }

    public static MessageResponseBody success() {
        return new MessageResponseBody(SUCCESS);
    }

    public static MessageResponseBody error() {
        return new MessageResponseBody(ERROR);
    }

    public static MessageResponseBody error(String errorCode) {
        return new MessageResponseBody(errorCode);
    }

    public MessageResponseBody setResultCode(String resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public String getResultCode() {
        return resultCode;
    }

    public T getResult() {
        return result;
    }

    public MessageResponseBody setResult(T result) {
        this.result = result;
        return this;
    }

}

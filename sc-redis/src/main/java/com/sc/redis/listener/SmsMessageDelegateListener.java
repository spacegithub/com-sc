package com.sc.redis.listener;


import java.io.Serializable;

/**
 * redis 简单队列
 * 1.必须有handleMessage
 * 2.方法中必须是Serializable的实现类
 * 3.发送端使用redisTemplate.convertAndSend("sms_queue_web_online", smsMessageVo);进行广播消息
 */
public class SmsMessageDelegateListener {
    //监听Redis消息必须有handleMessage 且获取到的必须是实现Serializable
    public void handleMessage(Serializable message) {

        System.out.println("-->监听到数据" + message);

    }
}

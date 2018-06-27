package com.sc.messagequeue.rabbitmq.service.provider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

public interface ProviderService
{

    /**
     * rabbitMq生产者发布
     * @param object 用于传递的消息体(泛型)
     * @param topic 主题
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws TimeoutException
     */
    public void send(Object object, String topic) throws UnsupportedEncodingException, IOException, TimeoutException;
    
    /**
     * rabbitMq生产者发布
     * @param object 用于传递的消息体(泛型)
     * @param exchangeName 交换机
     * @param topic 主题
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws TimeoutException
     */
    public void send(Object object, String exchangeName, String topic) throws UnsupportedEncodingException, IOException, TimeoutException;

}
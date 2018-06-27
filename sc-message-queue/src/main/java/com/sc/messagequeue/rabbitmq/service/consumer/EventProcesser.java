package com.sc.messagequeue.rabbitmq.service.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP.BasicProperties;


public interface EventProcesser {
	
    /**
     * 监听的队列里有消息则触发该事件处理
     * @param properties 可以取得非业务的mq相关信息
     * @param jsonObject 用于传递的消息体
     */
    public void process(BasicProperties properties, JSON jsonObject);
    
}
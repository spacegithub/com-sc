package com.sc.messagequeue.rabbitmq.service.provider.impl;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.sc.messagequeue.rabbitmq.service.provider.ProviderService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;


@Service("rabbitmqProviderService")
public class ProviderServiceImpl implements ProviderService {

    private static Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);
    private static String EXCHANGE_DEFAULT = "sc.exchange.topic";

    private ConnectionFactory factory;
    
    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.port}")
    private String port;
    @Value("${rabbitmq.username}")
    private String username;
    @Value("${rabbitmq.password}")
    private String password;
    @Value("${rabbitmq.virtualHost}")
    private String virtualHost;
    @Value("${rabbitmq.threadPoolMaxActive}")
    private String threadPoolMaxActive;
    @Value("${rabbitmq.connectionTimeout}")
    private String connectionTimeout;
    @Value("${rabbitmq.requestedHeartbeat}")
    private String requestedHeartbeat;
    @Value("${rabbitmq.definitions}")
    private String definitions;
    
    @PostConstruct //初始化方法的注解方式  等同与init-method=init
    private void init() {
    	factory = new ConnectionFactory();
    	factory.setHost(StringUtils.trim(host));  
    	factory.setPort(Integer.parseInt(StringUtils.trim(port)));
    	factory.setUsername(StringUtils.trim(username));
    	factory.setPassword(StringUtils.trim(password));
    	factory.setVirtualHost(StringUtils.trim(virtualHost));
       	// 关键所在，指定线程池  
    	ExecutorService service = Executors.newFixedThreadPool(Integer.parseInt(StringUtils.trim(threadPoolMaxActive)));
    	factory.setSharedExecutor(service);  
    	factory.setConnectionTimeout(Integer.parseInt(StringUtils.trim(connectionTimeout)));
    	factory.setRequestedHeartbeat(Integer.parseInt(StringUtils.trim(requestedHeartbeat)));
    	factory.setAutomaticRecoveryEnabled(true);
    }
    
	@Override
	public void send(Object object, String topic) throws UnsupportedEncodingException, IOException, TimeoutException {
		sendCommon(object, EXCHANGE_DEFAULT, topic);
	}

	private void sendCommon(Object object, String exchangeName, String topic) throws IOException, TimeoutException {
		Connection connection = null;
		Channel channel = null;
		try {
			connection = factory.newConnection();
        	channel = connection.createChannel();
			//topic模式
			channel.exchangeDeclare(exchangeName, "topic", true, false, null);  
			channel.confirmSelect();  
			Builder builder = new Builder();  
			builder.deliveryMode(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode());
			String corrId = UUID.randomUUID().toString();
			builder.messageId(corrId);
			builder.correlationId(corrId);
			builder.timestamp(new Date());
			builder.type(topic);
			String j = JSON.toJSONString(object);
	        channel.basicPublish(exchangeName, topic, builder.build(), j.getBytes("UTF-8"));  
	        logger.info("生产者消息已发送，topic:"+topic+"，correlationId="+corrId+"，消息体="+j);
		} catch (Exception e) {
			logger.error("生产者消息发送异常！", e);
		} finally {
			channel.close();
		    connection.close();  
		}
	}

	@Override
	public void send(Object object, String exchangeName, String topic)
			throws UnsupportedEncodingException, IOException, TimeoutException {
		sendCommon(object, exchangeName, topic);
	}

}

package com.sc.messagequeue.rabbitmq.service.consumer.impl;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sc.messagequeue.rabbitmq.exception.MessagequeueException;
import com.sc.messagequeue.rabbitmq.service.consumer.ConsumerService;
import com.sc.messagequeue.rabbitmq.service.consumer.EventProcesser;
import com.sc.messagequeue.rabbitmq.service.consumer.bo.Consumerbo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;


@Service("rabbitmqConsumerService")
@Lazy(false)
public class ConsumerServiceImpl implements ConsumerService {

    private static Logger logger = LoggerFactory.getLogger(ConsumerServiceImpl.class);
    
    private ConnectionFactory factory;
    
    @Autowired(required = false)
    private EventProcesser eventProcesser;
    
    private static final String DEAD_LETTER_FLG = "deadletter";
    
    private static final String SEPARATOR = ".";
    
    private static boolean isStart = false;

    @Value("${rabbitmq.exchangeName}")
    private String exchangeName;
    @Value("${rabbitmq.topics}")
    private String topics;
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
    @Value("${rabbitmq.isConsumer}")
    private String isConsumer;
    @Value("${rabbitmq.queueNames}")
    private String queueNames;
    @Value("${rabbitmq.threadPoolMaxActive}")
    private String threadPoolMaxActive;
    @Value("${rabbitmq.connectionTimeout}")
    private String connectionTimeout;
    @Value("${rabbitmq.requestedHeartbeat}")
    private String requestedHeartbeat;
    @Value("${rabbitmq.deadletterRetryTTL}")
    private String deadletterRetryTTL;
    @Value("${rabbitmq.qos}")
    private String qos;
    @Value("${rabbitmq.delayMillisecond}")
    private String delayMillisecond;
    @Value("${rabbitmq.definitions}")
    private String definitions;
    
    @PostConstruct //初始化方法的注解方式  等同与init-method=init
    private void init() {
    	if((isConsumer != null && isConsumer.equals("0")) || isStart)
    		return;
    	isStart = true;
        factory = new ConnectionFactory();  
    	factory.setHost(StringUtils.trim(host));  
    	factory.setPort(Integer.parseInt(StringUtils.trim(port)));
    	factory.setUsername(StringUtils.trim(username));
    	factory.setPassword(StringUtils.trim(password));
    	factory.setVirtualHost(StringUtils.trim(virtualHost));
    	// 关键所在，指定线程池  
    	ExecutorService service = Executors.newFixedThreadPool(Integer.parseInt(threadPoolMaxActive));
    	factory.setSharedExecutor(service);  
    	factory.setConnectionTimeout(Integer.parseInt(StringUtils.trim(connectionTimeout)));// 15秒
    	factory.setRequestedHeartbeat(Integer.parseInt(StringUtils.trim(requestedHeartbeat)));  // 5秒
    	factory.setAutomaticRecoveryEnabled(true);

    	List<Consumerbo> consumerbos = new ArrayList<Consumerbo>();
    	//1.1.1版本以后的配置，cjia.exchange.topic={product.ModifyRMStatus:ota.product.ModifyRMStatus:[20000,50,0]}，多个queue逗号隔开，多个exchangeName分号隔开
    	if(StringUtils.isNotEmpty(definitions) && !StringUtils.contains(definitions, "rabbitmq.definitions")){
    		String[] exchangeArr = StringUtils.split(definitions, "#");
    		for(int i=0;i<exchangeArr.length;i++){
    			String[] arr = StringUtils.split(exchangeArr[i], "@");
    			String[] queueArr = StringUtils.split(StringUtils.replace(StringUtils.replace(arr[1], "{", ""), "}", ""), ";");
    			for(int j=0;j<queueArr.length;j++){
    				Consumerbo consumerbo = new Consumerbo();
    				consumerbo.setExchangeName(arr[0]);
    				String[] queueValueArr = StringUtils.split(StringUtils.replace(StringUtils.replace(queueArr[j], "{", ""), "}", ""), ":");
        			consumerbo.setTopic(queueValueArr[0]);
        			consumerbo.setQueueName(queueValueArr[1]);
        			String[] paramArr = StringUtils.split(StringUtils.replace(StringUtils.replace(queueValueArr[2], "[", ""), "]", ""), ",");
        			consumerbo.setDeadletterRetryTTL(Long.parseLong(paramArr[0]));
        			consumerbo.setQos(Integer.parseInt(paramArr[1]));
        			consumerbo.setDelayMillisecond(Integer.parseInt(paramArr[2]));
        			consumerbos.add(consumerbo);
    			}
    		}
    	}
    	//1.1.1版本之前的配置
    	else{
    		String[] topicArr = StringUtils.trim(topics).split(",");
    		String[] queueArr = StringUtils.trim(queueNames).split(",");
    		for(int i=0;i<topicArr.length;i++){
    			Consumerbo consumerbo = new Consumerbo();
    			consumerbo.setExchangeName(exchangeName);
    			consumerbo.setTopic(topicArr[i]);
    			consumerbo.setQueueName(queueArr[i]);
    			consumerbo.setDeadletterRetryTTL(Long.parseLong(deadletterRetryTTL));
    			consumerbo.setQos(Integer.parseInt(qos));
    			consumerbo.setDelayMillisecond((delayMillisecond == null || StringUtils.contains(delayMillisecond, "delayMillisecond")) ? 0 : Integer.parseInt(delayMillisecond));
    			consumerbos.add(consumerbo);
    		}
    	}
    	
		Connection connection = null;
		for(final Consumerbo consumerbo : consumerbos){
			logger.info("消费者监听器已启动：" + JSON.toJSONString(consumerbo));
			final Channel channel;
			try {
				connection = factory.newConnection();  
				channel = connection.createChannel(); 
				//创建topic模式死信交换机
				channel.exchangeDeclare(consumerbo.getExchangeName() + SEPARATOR + DEAD_LETTER_FLG, "topic", true, false, null);  
				//创建死信工作队列  
				String queueNameDeadletter = DEAD_LETTER_FLG + SEPARATOR + consumerbo.getTopic();
	        	Map<String, Object> propsDeadletter = new Hashtable<String, Object>();
	        	propsDeadletter.put("x-dead-letter-exchange", consumerbo.getExchangeName());
	        	propsDeadletter.put("x-dead-letter-routing-key", consumerbo.getTopic());
	        	propsDeadletter.put("x-message-ttl", consumerbo.getDeadletterRetryTTL());
	        	//ha
	        	propsDeadletter.put("x-ha-prolicy", "all");
//	        	propsDeadletter.put("x-max-length", MAX_LENGTH_DEAD_LETTER);
				channel.queueDeclare(queueNameDeadletter, true, false, false, propsDeadletter);  
				//把死信队列绑定到死信交换机
				channel.queueBind(queueNameDeadletter, consumerbo.getExchangeName() + SEPARATOR + DEAD_LETTER_FLG, consumerbo.getTopic());
				
				//创建topic模式工作交换机
				channel.exchangeDeclare(consumerbo.getExchangeName(), "topic", true, false, null);  
				//创建工作队列  
	        	Map<String, Object> props = new Hashtable<String, Object>();
	        	props.put("x-dead-letter-exchange", consumerbo.getExchangeName() + SEPARATOR + DEAD_LETTER_FLG);
	        	props.put("x-dead-letter-routing-key", consumerbo.getTopic());
	        	//ha
	        	props.put("x-ha-prolicy", "all");
				channel.queueDeclare(consumerbo.getQueueName(), true, false, false, props);  
				//把工作队列绑定到工作交换机
				channel.queueBind(consumerbo.getQueueName(), consumerbo.getExchangeName(), consumerbo.getTopic());
				
				//在消息确认之前不接受其他消息
				channel.basicQos(consumerbo.getQos());
				Consumer consumer = new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) throws IOException {
						try {
							while(eventProcesser == null || ContextLoader.getCurrentWebApplicationContext() == null){
//					    		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
//					    		eventProcesser = (EventProcesser) wac.getBean("eventProcesser");
//					    		if(eventProcesser == null){
					    			logger.error("在等待spring容器的批量实例化完成和Web容器上下文的初始化完成，或者应用并未实例化EventProcesser接口的实现类...");
					    			Thread.sleep(5000);
//					    		}
							}
				            String j = new String(body, "UTF-8");
							eventProcesser.process(properties, JSON.parseObject(j));
							channel.basicAck(envelope.getDeliveryTag(), false);
							logger.info("订阅消息成功，消息体："+new String(body, "UTF-8")+"；properties："+ JSON.toJSONString(properties));
						} catch (Exception e) {
							logger.error("订阅消息失败异常："+e.getMessage()+"；消息体："+new String(body, "UTF-8")+"；properties："+ JSON.toJSONString(properties), e);
							//当客户端发生错误，调用basic.reject命令拒绝某一个消息时，可以设置一个requeue的属性，如果为true，
							//则消息服务器会重传该消息给下一个订阅者；如果为false，则会直接删除该消息，投递到死信队列。
							channel.basicReject(envelope.getDeliveryTag(), false);
						}
						try {
							//延迟消费
							Thread.sleep(consumerbo.getDelayMillisecond());
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				channel.basicConsume(consumerbo.getQueueName(), false, consumer);  
			} catch (IOException e) {
				logger.error("订阅消息失败！", e);
				throw new MessagequeueException(e);
			} catch (TimeoutException e) {
				logger.error("订阅消息失败！", e);
				throw new MessagequeueException(e);
			}
		}
	}
    
}

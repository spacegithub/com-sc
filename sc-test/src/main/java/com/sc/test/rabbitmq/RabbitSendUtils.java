package com.sc.test.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RabbitSendUtils {
    private RabbitSendUtils() {
    }

    private static ConnectionFactory factory = new ConnectionFactory();

    private static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static RabbitSendUtils init(String host, String port, String username, String password, String virtualHost) {
        factory.setHost(trim(host));
        factory.setPort(Integer.parseInt(trim(port)));
        factory.setUsername(trim(username));
        factory.setPassword(trim(password));
        factory.setVirtualHost(trim(virtualHost));
        // 关键所在，指定线程池
        ExecutorService service = new ThreadPoolExecutor(2, 5, 5, TimeUnit.SECONDS, new LinkedBlockingDeque(10));
        factory.setSharedExecutor(service);
        factory.setConnectionTimeout(5000);
        factory.setRequestedHeartbeat(5000);
        factory.setAutomaticRecoveryEnabled(true);
        return new RabbitSendUtils();
    }

    public void send(String exchangeName, String topic, Object object) throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            //topic模式
            channel.exchangeDeclare(exchangeName, "topic", true, false, null);
            channel.confirmSelect();
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.deliveryMode(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode());
            String corrId = UUID.randomUUID().toString();
            builder.messageId(corrId);
            builder.correlationId(corrId);
            builder.timestamp(new Date());
            builder.type(topic);
            String j = JSON.toJSONString(object);
            channel.basicPublish(exchangeName, topic, builder.build(), j.getBytes("UTF-8"));
            System.out.println("生产者消息已发送，topic:" + topic + "，correlationId=" + corrId + "，消息体=" + j);
        } catch (Exception e) {
            System.out.println("生产者消息发送异常！");
            e.printStackTrace();
        } finally {
            channel.close();
            connection.close();
        }

    }

}

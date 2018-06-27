package com.sc.messagequeue.rabbitmq.service.consumer.bo;

public class Consumerbo {

	private String exchangeName;
	private Long deadletterRetryTTL;
	private Integer qos;
	private Integer delayMillisecond;
	private String topic;
	private String queueName;
	
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	public Long getDeadletterRetryTTL() {
		return deadletterRetryTTL;
	}
	public void setDeadletterRetryTTL(Long deadletterRetryTTL) {
		this.deadletterRetryTTL = deadletterRetryTTL;
	}
	public Integer getQos() {
		return qos;
	}
	public void setQos(Integer qos) {
		this.qos = qos;
	}
	public Integer getDelayMillisecond() {
		return delayMillisecond;
	}
	public void setDelayMillisecond(Integer delayMillisecond) {
		this.delayMillisecond = delayMillisecond;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
}

package com.xxx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 *  消息队列RabbitMQ配置（从rabbitmq.properties配置文件获取各个参数项）
 * */
public class RabbitMQConfig {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 自动引入连接工厂
	 * */
	//@Autowired
	private ConnectionFactory connectionFactory;

	//@Bean 
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		// 确认回调机制
		template.setConfirmCallback(new RabbitTemplate.ConfirmCallback(){
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				String id = correlationData.getId();
				if(ack){
					logger.info("消息："+id+"被成功消费");
				} else {
					logger.error("消息："+id+"被消费失败，原因："+cause);
				}
			}
		});
		// 返回回调机制
		template.setReturnCallback(new ReturnCallback(){
			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
				
			}
		});
		template.setMandatory(true);
		return template;
	}
	
}

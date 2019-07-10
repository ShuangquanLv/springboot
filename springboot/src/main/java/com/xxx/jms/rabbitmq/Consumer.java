package com.xxx.jms.rabbitmq;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;


/**
 * RabbitMQ 消息消费监听者
 *	1、RabbitListener既可以注解到类上，也可注解到方法上
 * */
@Service("rabbitMQConsumer")
//@RabbitListener(queues="queue.default")
public class Consumer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public SimpleMessageListenerContainer simpleMessageListenerContainer(){
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(rabbitTemplate.getConnectionFactory());
		container.setExposeListenerChannel(true);
		container.setPrefetchCount(2); // 设置每个消费者获取的最大的消息数量
		container.setConcurrentConsumers(3); //并发消费者个数
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 确认模式为手工确认
		container.setMessageListener(new ChannelAwareMessageListener(){
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				String payload = new String(message.getBody());
				logger.info("监听端收到消息：{}", payload);
				//处理业务逻辑
				try{
					Map<String, String> map = (Map<String, String>) JSON.parse(payload);
					int count = Integer.parseInt(map.get("count"));
					if(count%3==0){
						//确认消费成功
						channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
					} else if(count%3==1){
						//可重试的失败处理
						channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
					} else {
						//确认消费失败  
						channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false); 
					}
				} catch (Exception e){
					e.printStackTrace();
					//确认消费失败  
					channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false); 
				}
			}
		});
		
		return container;
	}
	
	@RabbitListener(queues = "queue.default")
	public void processDirect(String message){
		logger.info("消费监听端收到消息：{}", message);
	}
	
	@RabbitListener(queues = {
			"topic.queue"
//			,"topic.queue.default"
//			,"topic.queue.A" 
//			,"topic.queue.B" 
	})
	public void processTopic(String message){
		logger.info("消费监听端Topic收到消息：{}", message);
	}
	
	@RabbitListener(queues = "topic.queue.A")
	public void processTopicA(String message){
		logger.info("消费监听端Topic-A收到消息：{}", message);
	}
	
	@RabbitListener(queues = "topic.queue.B")
	public void processTopicB(String message){
		logger.info("消费监听端Topic-B收到消息：{}", message);
	}
	
	
	
	@RabbitListener(queues = { "fanout.queue.A" })
	public void processFanoutA(String message){
		logger.info("消费监听端Fanout-A收到消息：{}", message);
	}
	
	@RabbitListener(queues = "fanout.queue.B")
	public void processFanoutB(String message){
		logger.info("消费监听端Fanout-B收到消息：{}", message);
	}
	
	@RabbitListener(queues = "fanout.queue.C")
	public void processFanoutC(String message){
		logger.info("消费监听端Fanout-C收到消息：{}", message);
	}
}

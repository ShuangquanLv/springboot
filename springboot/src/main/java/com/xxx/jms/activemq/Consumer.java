package com.xxx.jms.activemq;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.annotation.JmsListeners;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

/**
 * ActiveMQ 消息消费者
 * */
@Service("activeMQConsumer")
public class Consumer {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * JmsListener是spring-jms提供的一个注解，会实例化一个Jms的消息监听实例，也就是一个异步的消费者
	 * SendTo将返回的经过业务处理的结果，再次向另一个终点destination发送，实现消息链
	 */
	@JmsListener(destination = "queue.default")
	@SendTo("queue.default2")
	public String receiveQueue(String text) {
		logger.info("收到Queue类型消息："+text);
		//TODO 处理业务逻辑
		return text;
	}
	
	@JmsListener(destination = "queue.default2")
	public void receiveQueue2(String text) {
		logger.info("再次收到Queue类型消息："+text);
	}
	
	//需要给topic定义独立的JmsListenerContainer
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(activeMQConnectionFactory);
        return bean;
    }

    
    //@JmsListener如果不指定独立的containerFactory只能消费queue消息，如果要同时接收topic消息，需要增加containerFactory配置
    @JmsListener(destination = "topic.default", containerFactory="jmsListenerContainerTopic")
    public void receiveTopic(String text) {
    	logger.info("收到Topic类型消息："+text);
    	//TODO 处理业务逻辑
    }
}

package com.xxx.jms.activemq;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * ActiveMQ 消息生产者
 * 		1、消息即时发送投递
 * 		2、消息定时和延迟发送投递
 * */
@Service("activeMQProducer") 
@Profile("prod")
public class Producer {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
	@Autowired
	private JmsTemplate jmsTemplate;
	
	/**
	 * 发送消息(利用JmsMessagingTemplate发送)
	 * @param destination 发送终点
	 * @param message 消息体
	 * */
    private void sendMessage(Destination destination, final String message){
        jmsMessagingTemplate.convertAndSend(destination, message);
    }
    
    /**
	 * 发送消息(利用JmsTemplate发送)
	 * @param destination 发送终点
	 * @param message 消息体
	 * @param messageProperties 定时、延时参数，空值表示即时发送
	 * */
    private void sendMessage(Destination destination, final Serializable message, final Map<String, Object> messageProperties){
    	MessageCreator creator = new MessageCreator(){
			@Override
			public Message createMessage(Session session) throws JMSException {
				Message msg;
				if(message instanceof String){
					msg = session.createTextMessage(message.toString());
				} else {
					msg = session.createObjectMessage(message);
				}
				if(MapUtils.isNotEmpty(messageProperties)){
					for(Map.Entry<String, Object> entry: messageProperties.entrySet()){
						String key = entry.getKey();
						Object val = entry.getValue();
						if(val instanceof String){
							String val_ = val.toString();
							if(StringUtils.isNotEmpty(val_)){
								msg.setStringProperty(key, val_);
							}
						} else if (val instanceof Long){
							Long val_ = (Long)val;
							if(val_>0){
								msg.setLongProperty(key, val_);
							}
						} else if(val instanceof Integer){
							Integer val_ = (Integer)val;
							if(val_>0){
								msg.setIntProperty(key, val_);
							}
						} else {
							logger.error("错误的键值数据类型");
						}
					}
				}
				return msg;
			}
    	};
    	jmsTemplate.send(destination, creator);
    }
    
    /**
	 * 发送定时和延迟消息(需要在activemq.xml文件配置<broker schedulerSupport="true">)
	 * @param destination 发送终点
	 * @param message 消息体
	 * @param cronExpr 调度corn表达式
	 * @param delay 延迟时间（毫秒）
	 * @param period 发送间隔 （毫秒）
	 * @param repeat 发送次数 
	 * */
    private void sendMessage(Destination destination, final String message, String cronExpr, long delay, long period, int repeat){
    	ConnectionFactory factory = jmsMessagingTemplate.getConnectionFactory();
    	Connection connection = null;
    	Session session = null;
    	MessageProducer producer = null;
    	try{
	    	// 通过工厂创建一个连接
    		connection = factory.createConnection();
	    	// 启动连接 
    		connection.start();
    		// 创建一个会话   事务型消息    自动确认
	    	session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
	    	// 创建生产者
	    	producer = session.createProducer(destination);
	    	// 传递模式
	    	//DeliveryMode.PERSISTENT 当activemq关闭的时候，队列数据将会被保存
	    	//DeliveryMode.NON_PERSISTENT 当activemq关闭的时候，队列里面的数据将会被清空
	    	producer.setDeliveryMode(DeliveryMode.PERSISTENT);
	    	// 消息
	    	TextMessage msg = session.createTextMessage(message);
	    	// 设置消息调度频率
	    	if(StringUtils.isNotEmpty(cronExpr)){
	    		msg.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, cronExpr);
	    	}
	    	if (delay>0 && period>0 && repeat>0) {
	    		msg.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
	    		msg.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
	    		msg.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);
	    	}
	        // 发送消息
	    	producer.send(msg);
	    	// 提交会话
	        session.commit(); 
    	} catch (Exception e){
    		e.printStackTrace();
    	} finally{// 关闭连接
    		if(producer!=null){
    			try {
					producer.close();
				} catch (JMSException e) {
					e.printStackTrace();
				} 
    		}
            if(session!=null){
            	try {
					session.close();
				} catch (JMSException e) {
					e.printStackTrace();
				} 
            } 
            if(connection!=null){
            	try {
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
            }
    	}
    }
    
    /**
	 * 发送消息（生产/消费模式）
	 * @param queueName 发送终点（queue）名称
	 * @param message 消息体
	 * */
	public void sendQueueMessage(String queueName, final String message) {
		logger.info("向终点（queue）：" + queueName + "发送即时消息：" + message);
		Destination destination = new ActiveMQQueue(queueName);
		sendMessage(destination, message);
	}
	
	/**
	 * 发送消息（生产/消费模式，定时、延迟投递，一次生产多次消费）
	 * 将消息体暂存到MQ服务器上，后续再由MQ服务器自动唤醒消费监听者。Cron定时投递，当前受理生产者的消息不会立即调用，下次满足条件再调；延迟投递，当前立即执行一次，后续再执行repeat次。
	 * AMQ_SCHEDULED_CRON只支持6个占位，各个位含义与quartz不同?
	 * */
	public void sendQueueMessage(String queueName, final String message, String cronExpr, long delay, long period, int repeat){
		logger.info("向终点（queue）：" + queueName + "发送定时、延迟消息：" + message);
		Destination destination = new ActiveMQQueue(queueName);
		// 第一种方式：原生态
//		sendMessage(destination, message, cronExpr, delay, period, repeat);
		// 第二种方式：模板
		Map<String, Object> messageProperties = new HashMap<String, Object>();
		messageProperties.put(ScheduledMessage.AMQ_SCHEDULED_CRON, cronExpr);
		messageProperties.put(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
		messageProperties.put(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
		messageProperties.put(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);
		sendMessage(destination, message, messageProperties);
	}
    
    /**
	 * 发送消息（发布/订阅模式）
	 * @param queueName 发送终点（topic）名称
	 * @param message 消息体
	 * */
	public void sendTopicMessage(String topicName, final String message) {
		logger.info("向终点（topic）：" + topicName + "发送消息：" + message);
		Destination destination = new ActiveMQTopic(topicName);
		sendMessage(destination, message);
	}
}

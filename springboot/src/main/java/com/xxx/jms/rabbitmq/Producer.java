package com.xxx.jms.rabbitmq;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ 消息生产者
 * 		1、三种Exchange模式(Fanout, Direct, Topic)实现
 * 		2、消息即时投递，定时和延迟投递
 * 		3、消息确认机制(Auto, Manual)
 * */
@Service("rabbitMQProducer") 
public class Producer {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	//@Autowired
    //private RabbitTemplate rabbitTemplate;
	
	// direct模式
	private static final String default_queue = "queue.default";
	// topic模式
	private static final String default_topic_queue = "topic.queue.default";
	private static final String default_topic_exchange = "topic.exchange.default";
	// fanout模式
	private static final String default_fanout_exchange = "fanout.exchange.default";
	
	// 持久化：服务重启后配置的queue是否清零
	private static final boolean durable = true;
	
	/**
	 * 发送消息(使用AmqpTemplate)
	 * @param model 发送模式
	 * @param message 消息体
	 * */
	public void sendMessage(String model, String message){
		logger.info("消息生成者发送{}模式的消息：{}", model, message);
		if("direct".equalsIgnoreCase(model)){
			sendMessage(message);
		} else if ("topic".equalsIgnoreCase(model)){
			sendTopicMessage(message);
		} else if("fanout".equalsIgnoreCase(model)) {
			sendFanoutMessage(message);
		}
	}
	
	// direct模式： 点对点发送，单个生产者到单个消费者的消息投递
	@Bean
    public Queue defaultQueue() {
        return new Queue(default_queue, durable);
    }
	
	/**
	 * 发送消息(使用AmqpTemplate)
	 * @param message 消息体
	 * */
	public void sendMessage(String message){
		logger.info("消息生成者发送消息：{}", message);
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
		// direct模式下，利用系统默认的exchange，只需要指定单对单的queue名称
		amqpTemplate.convertAndSend(default_queue, message);
	}
	
	// topic模式： 根据binding_key模糊匹配绑定不同的队列
	@Bean(name = "topicQueue")
	public Queue topicQueue(){
		return new Queue("topic.queue");
	}
	@Bean(name = "topicQueueA")
	public Queue topicQueueA(){
		return new Queue("topic.queue.A");
	}
	@Bean(name = "topicQueueB")
	public Queue topicQueueB(){
		return new Queue("topic.queue.B");
	}
	@Bean(name = "topicExchange")
	TopicExchange topicExchange(){
		return new TopicExchange(default_topic_exchange);
	}
	@Bean(name = "bindingTopicExchange")
	Binding bindingTopicExchange(@Qualifier("topicQueue") Queue topicQueue, @Qualifier("topicExchange") TopicExchange topicExchange) {
		return BindingBuilder.bind(topicQueue).to(topicExchange).with("topic.queue"); // 匹配表达式，精准匹配
    }
	@Bean(name = "bindingTopicExchangeA")
	Binding bindingTopicExchangeA(@Qualifier("topicQueueA") Queue topicQueueA, @Qualifier("topicExchange") TopicExchange topicExchange) {
		return BindingBuilder.bind(topicQueueA).to(topicExchange).with("topic.queue.#"); // 匹配表达式，模糊匹配，#表示任意个词，*表示一个词
    }
	@Bean(name = "bindingTopicExchangeB")
	Binding bindingTopicExchangeB(@Qualifier("topicQueueB") Queue topicQueueB, @Qualifier("topicExchange") TopicExchange topicExchange) {
		return BindingBuilder.bind(topicQueueB).to(topicExchange).with("topic.queue.#");
    }
	
	/**
	 * 发送消息(使用AmqpTemplate)
	 * @param message 消息体
	 * */
	public void sendTopicMessage(String message){
		logger.info("消息生成者发送Topic模式消息：{}", message);
		// 第一个参数exchange指定交换机名称，第二个参数routingKey指定模糊匹配规则
		//amqpTemplate.convertAndSend(default_topic_exchange, "topic.queue.#", message);
		// 当routingKey非模糊匹配时，就精准投递消息
		amqpTemplate.convertAndSend(default_topic_exchange, "topic.queue.abc", message);
	}
	
	// fanout模式： 单点广播，多点订阅
	@Bean(name = "fanoutQueueA")
	public Queue fanoutQueueA(){
		return new Queue("fanout.queue.A");
	}
	@Bean(name = "fanoutQueueB")
	public Queue fanoutQueueB(){
		return new Queue("fanout.queue.B");
	}
	@Bean(name = "fanoutQueueC")
	public Queue fanoutQueueC(){
		return new Queue("fanout.queue.C");
	}
	@Bean(name = "fanoutExchange")
	FanoutExchange fanoutExchange(){
		return new FanoutExchange(default_fanout_exchange);
	}
	@Bean(name = "bindingFanoutExchangeA")
	Binding bindingFanoutExchangeA(@Qualifier("fanoutQueueA") Queue fanoutQueueA, @Qualifier("fanoutExchange") FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(fanoutQueueA).to(fanoutExchange);
    }
	@Bean(name = "bindingFanoutExchangeB")
	Binding bindingFanoutExchangeB(@Qualifier("fanoutQueueB") Queue fanoutQueueB, @Qualifier("fanoutExchange") FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(fanoutQueueB).to(fanoutExchange);
    }
	@Bean(name = "bindingFanoutExchangeC")
	Binding bindingFanoutExchangeC(@Qualifier("fanoutQueueC") Queue fanoutQueueC, @Qualifier("fanoutExchange") FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(fanoutQueueC).to(fanoutExchange);
    }
	
	/**
	 * 发送消息(使用AmqpTemplate)
	 * @param message 消息体
	 * */
	public void sendFanoutMessage(String message){
		logger.info("消息生成者发送Fanout模式消息：{}", message);
		// fanout模式下只需要指定exchange名称，忽略routingKey，生产者会自动向所有与exchange绑定的queues发送消息
		amqpTemplate.convertAndSend(default_fanout_exchange, "", message);
	}
	
}

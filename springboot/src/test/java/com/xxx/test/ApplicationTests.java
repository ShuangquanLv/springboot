package com.xxx.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.xxx.Application;
import com.xxx.entity.Status;
import com.xxx.jms.activemq.Producer;
import com.xxx.service.JedisService;
import com.xxx.util.StringUtil;

/**
 * 测试运行方式：右击 --> Run As --> JUnit Test
 * 新版Spring Boot取消了SpringApplicationConfiguration注解，而用SpringBootTest代替
 * WebAppConfiguration
 * */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
//@WebAppConfiguration
public class ApplicationTests {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 单机环境springboot自动引入StringRedisTemplate服务
	//@Autowired
    private StringRedisTemplate stringRedisTemplate;
	// 集群环境springboot则自动引入RedisTemplate服务
	@Autowired
    private RedisTemplate redisTemplate;
	@Autowired 
	private Producer activeMQProducer;
	@Autowired
	private JedisService jedisService;
	@Autowired
	com.xxx.jms.rabbitmq.Producer rabbitMQProducer;
    
	/**
     * 测试Redis缓存
     * */
	@Test
    public void testRedis() throws Exception {
		ValueOperations<String, String> vos = stringRedisTemplate.opsForValue();
        // 保存key-value键值对(默认永久有效)
		vos.set("a", "123");
        // 设置缓存有效期
        //vos.set("b", "abc", 2, TimeUnit.DAYS);
		vos.set("b", "abc");
        stringRedisTemplate.expire("b", 2, TimeUnit.DAYS);
        // 获取指定key的value值
        Assert.assertEquals("123", vos.get("a"));
        // 获取指定key的过期时间
        Assert.assertEquals(1, stringRedisTemplate.getExpire("b", TimeUnit.DAYS).longValue());
        // 删除key
        stringRedisTemplate.delete("a");
        Assert.assertEquals(false, stringRedisTemplate.hasKey("a").booleanValue());
        
    }
	
	/**
     * 测试Redis客户端Jedis
     * */
	@Ignore
	public void testJedis(){
		// 通过jedis设值与取值
//		jedisService.set("abc", "123");
//		String val = jedisService.get("abc");
//		logger.info("the value is "+ val);
		
		//利用jedis实现分布式锁
		String key = "Lock02";
		String client1 = StringUtil.uuid();
		String client2 = StringUtil.uuid();
		if (jedisService.lock(key, client1, 2L)) {
			logger.info("客户端1：获取锁资源成功");
			try {
				Thread.sleep(3*1000); //超时
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(jedisService.lock(key, client2, 2L)){
				logger.info("客户端2：获取锁资源成功");
				if(jedisService.unlock(key, client2)){
					logger.info("客户端2：释放锁资源成功");
				} else {
					logger.error("客户端2：释放锁资源失败");
				}
			} else {
				logger.error("客户端2：获取锁资源失败");
			}
			if(jedisService.unlock(key, client1)){
				logger.info("客户端1：释放锁资源成功");
			} else {
				logger.error("客户端1：释放锁资源失败");
			}
		} else {
			logger.error("客户端1：获取锁资源失败");
		}
	}
	
	/**
     * 测试集群Redis缓存
     * */
	@Ignore
	public void testClusterRedis(){
		ValueOperations<String, String> vos = redisTemplate.opsForValue();
		//设值
		for(int i=0; i<26; i++){
			char c = (char)(97+i);
			String s = String.valueOf(c);
			String val = vos.get(s);
			if(StringUtils.isEmpty(val)){
				if(i%2==0){
					vos.set(s, String.valueOf(i));
				} else {
					vos.set(s, String.valueOf(i), 2, TimeUnit.HOURS);
				}
			}
		}
		//取值
		for(int i=0; i<26; i++){
			char c = (char)(97+i);
			String s = String.valueOf(c);
			String val = vos.get(s);
			logger.info("the value of "+s+" is "+ val);
		}
		
	}
    
    @Ignore
    public void testEnum() throws Exception {
    	String code = Status.Success.getCode();
    	String msg = Status.Success.getMsg();
    	Assert.assertEquals("0000", code);
    	Assert.assertEquals("成功", msg);
    	Assert.assertEquals(code, Status.getCodeByMsg(msg));
    	Assert.assertEquals(msg, Status.getMsgByCode(code));
    }
    
    /**
     * 测试ActiveMQ
     * */
    @Ignore
    public void testActiveMQ(){
    	Map<String, String> map = new HashMap<String, String>();
    	//for(int i=0; i<10; i++){
    		map.put("count", new Integer(1).toString());
        	String message = JSON.toJSONString(map);
    		//activeMQProducer.sendQueueMessage("queue.default", message);
        	//activeMQProducer.sendTopicMessage("topic.default", message);
        	// 每小时都会发生，消息被投递10次，延迟1秒开始，每次间隔1秒
        	activeMQProducer.sendQueueMessage("queue.default", message, "0 * * * *", 1000, 1000, 9);
        	// 每1秒执行一次，无次数、延迟和间隔限制
        	activeMQProducer.sendQueueMessage("queue.default", message, "0/1 * * * *", 0, 0, 0);
    	//}
    	try {
    		//让主线程存活10秒
			Thread.sleep(100*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 测试RabbitMQ
     * */
    @Ignore
    public void testRabbitMQ(){
    	String exchange = "direct.exchange.default";
    	String routingKey = "routing.default";
    	String queue = "queue.default";
    	Map<String, String> map = new HashMap<String, String>();
    	for(int i=0; i<10; i++){
    		map.put("count", new Integer(i).toString());
    		String payload = JSON.toJSONString(map);
    		rabbitMQProducer.sendMessage(payload);
    		//rabbitMQProducer.sendTopicMessage(payload);
    		//rabbitMQProducer.sendFanoutMessage(payload);
    	}
    	try {
    		//让主线程存活10秒
			Thread.sleep(30*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

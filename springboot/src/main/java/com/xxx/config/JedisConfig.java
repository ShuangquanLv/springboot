package com.xxx.config;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis客户端Jedis配置（从jedis.properties配置文件获取各个参数项）
 * */
@Configuration
public class JedisConfig {
	
	private static Logger logger = LoggerFactory.getLogger(JedisConfig.class);
	private static JedisPool jedisPool = null;
	
	static{
		jedisPool = getJedisPool();
		logger.info("init the jedisPool successfully!");
	}
	
	@Bean(name="Jedis")
	public Jedis getJedis(){
		if(jedisPool==null){
			jedisPool = getJedisPool();
		}
		return jedisPool.getResource();
	}
	
	/**
	 * 实例化Jedis连接池
	 * */
	private static JedisPool getJedisPool(){
		JedisPool jedisPool = null;
		try {
			Properties pro = getProperties();
			int maxIdle = Integer.parseInt(pro.getProperty("jedis.pool.config.maxIdle"));
			int maxTotal = Integer.parseInt(pro.getProperty("jedis.pool.config.maxTotal"));
			long maxWaitMillis =Long.parseLong(pro.getProperty("jedis.pool.config.maxWaitMillis"));
			int minIdle = Integer.parseInt(pro.getProperty("jedis.pool.config.minIdle"));
			boolean testOnBorrow = Boolean.parseBoolean(pro.getProperty("jedis.pool.config.testOnBorrow"));
			String host = pro.getProperty("jedis.pool.host");
			int port = Integer.parseInt(pro.getProperty("jedis.pool.port"));
			int timeout = Integer.parseInt(pro.getProperty("jedis.pool.timeout"));
			String password =  pro.getProperty("jedis.pool.password");
			JedisPoolConfig config = new JedisPoolConfig(); 
			config.setMaxIdle(maxIdle);
			config.setMaxTotal(maxTotal);
			config.setMaxWaitMillis(maxWaitMillis);
			config.setMinIdle(minIdle);
			config.setTestOnBorrow(testOnBorrow);
			jedisPool = new JedisPool(config, host, port, timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jedisPool;
	}
	
	/**
	 * 从jedis.properties配置文件中读取参数并返回Properties对象
	 * */
    private static Properties getProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("com/xxx/config/jedis.properties"));
        //在jedis.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}

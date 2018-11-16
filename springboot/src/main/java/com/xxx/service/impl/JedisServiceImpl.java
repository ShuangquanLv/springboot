package com.xxx.service.impl;

import java.util.Collections;

import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.xxx.service.JedisService;

import redis.clients.jedis.Jedis;
/**
 * 与springboot自动配置的StringRedisTemplate相比，使用Jedis有更多操作功能
 * 例如，分布式锁
 * */
@Service(value = "jedisService")
public class JedisServiceImpl implements JedisService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_IF_EXIST = "XX";
	private static final String EXPIRE_TIME_UNIT_SECOND = "EX";
	private static final String EXPIRE_TIME_UNIT_MILLISECONDS = "PX";
	
	//加入Qulifier注解，通过名称注入bean
    @Autowired @Qualifier("Jedis")
    private Jedis jedis;
    
    @Autowired @Qualifier("Redisson")
    private RedissonClient redisson;

	@Override
	public void set(String key, String value) {
		jedis.set(key, value);
	}

	@Override
	public String get(String key) {
		return jedis.get(key);
	}

	/**
	 * 获取分布式锁（key值不存在的情况下，存入value指定时间；否则什么也不做）
	 * @param key 锁名称（所有争夺同一资源的客户端共享）
	 * @param value 客户端随机产生的唯一标识符
	 * @param time 锁定时间（单位秒）
	 * */
	@Override
	public boolean lock(String key, String value, long time) {
		String status = jedis.set(key, value, SET_IF_NOT_EXIST, EXPIRE_TIME_UNIT_SECOND, time);
		if("OK".equals(status)){
			return true;
		}
		return false;
	}

	/**
	 * 释放分布式锁（使用lua脚本保证操作原子性）
	 * @param key 锁名称（所有争夺同一资源的客户端共享） 
	 * @param value 客户端随机产生的唯一标识符
	 * */
	@Override
	public boolean unlock(String key, String value) {
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
        if (Long.parseLong(result.toString()) == 1L) {
            return true;
        }
        return false;
	}
    
}

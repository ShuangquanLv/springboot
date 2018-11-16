package com.xxx.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.xxx.dao.UserMapper;
import com.xxx.service.SimpleProviderService;

/**
 * Dubbo生产者服务，当项目启动时，将当前服务注册到注册中心zookeeper等待消费
 * 和原来SpringBoot服务一样，但是Service注解由dubbo提供
 * 当作为生产者服务时，需要将SimpleConsumerServiceImpl去掉，并单独启动一个新application
 * */
//@Service(version = "1.0.0")
public class SimpleProviderServiceImpl implements SimpleProviderService{

	//@Autowired
	private UserMapper userMapper;
	/**
	 * 基于条件查询用户信息
	 * */ 
	@Override
	public List<Map<Object, Object>> getUsers(Map<Object, Object> params) throws Exception {
		return userMapper.getUsers(params);
	}

}

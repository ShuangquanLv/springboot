package com.xxx.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xxx.service.SimpleConsumerService;
import com.xxx.service.SimpleProviderService;
/**
 * Dubbo消费者服务，当项目启动时，从远端注册中心zookeeper获取异地服务
 * */
@Service(value = "simpleConsumerService")
public class SimpleConsumerServiceImpl implements SimpleConsumerService {

	// 使用@Reference注解引入服务，Reference引入成功,需要SimpleProviderServiceImpl在另一个Application中注册到Zookeeper上
	@Reference(version = "1.0.0")
	SimpleProviderService simpleProviderService;
	
	@Override
	public List<Map<Object, Object>> getUsers(Map<Object, Object> params) throws Exception {
		return simpleProviderService.getUsers(params);
	}

}

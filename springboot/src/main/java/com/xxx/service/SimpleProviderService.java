package com.xxx.service;

import java.util.List;
import java.util.Map;

/**
 * Dubbo生产者服务
 * */
public interface SimpleProviderService {
	
	// 基于条件查询用户信息 
	public List<Map<Object, Object>> getUsers(Map<Object, Object> params) throws Exception;
	
}

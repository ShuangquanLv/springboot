package com.xxx.service;

import java.util.Map;

public interface JedisService {
	
	//添加值
	public void set(String key, String value);
	
	//获取值
	public String get(String key);
	
	//加锁
	public boolean lock(String key, String value, long time);
	
	//解锁
	public boolean unlock(String key, String value);
	
}

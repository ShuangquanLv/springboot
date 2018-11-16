package com.xxx.service;

import java.util.List;
import java.util.Map;

public interface JobService {

	//添加新任务
	public void addJob(Map<Object, Object> params) throws Exception;
	
	//删除任务
	public void deleteJob(Map<Object, Object> params) throws Exception;
	
	//暂停任务
	public void pausejob(Map<Object, Object> params) throws Exception;
	
	//恢复任务
	public void resumejob(Map<Object, Object> params) throws Exception;
	
	//更新任务
	public void rescheduleJob(Map<Object, Object> params) throws Exception;
	
	//立即执行任务
	public void triggerJob(Map<Object, Object> params) throws Exception;
	
	//查询任务
	public List<Map<Object, Object>> searchJob(Map<Object, Object> params) throws Exception;
	
}

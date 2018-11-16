package com.xxx.controller;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xxx.entity.ResultBean;
import com.xxx.entity.ResultFailBean;
import com.xxx.entity.ResultSuccessBean;
import com.xxx.service.JobService;

/**
 * quartz定时任务控制器
 * */
@RestController
@RequestMapping(value = "/job")
public class JobController {

	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	@Autowired
	private JobService jobService;
	
	/**
	 * 利用反射几种执行某个对象的某个方法
	 * @param obj  服务对象
	 * @param methodName 服务方法名称
	 * @param 请求参数
	 * */
	private Object doInvokeService(Object obj, String methodName, Object params) throws Exception{
		// 统一所有方法的入参都是Map
		Method method = obj.getClass().getDeclaredMethod(methodName, new Class[]{Map.class});
		Object retObj = method.invoke(obj, new Object[]{params});
		return retObj;
	}
	
	/**
	 * 将request请求参数封装到map
	 * */
	private Map<Object, Object> request2Map(HttpServletRequest request){
		Map<Object, Object> map = new HashMap<Object, Object>();
		Enumeration<String> keys = request.getParameterNames();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = request.getParameter(key);
			map.put(key, value);
		}
		return map;
	}
	/**
	 * 执行与定时任务相关的操作
	 * 增加：http://localhost:8080/job/exec?action=addJob&jobName=job01&jobClassName=com.xxx.schedule.SimpleTask&jobGroupName=group01&jobDataJson={"name":"tom"}&cronExpression=0/1 * * * * ?
	 *     http://localhost:8080/job/exec?action=addJob&jobName=job01&jobClassName=com.xxx.schedule.SimpleTask&jobGroupName=group01&jobDataJson=%7B%22name%22%3A%22tom%22%7D&cronExpression=0/1 * * * * ?   URL特殊字符ASCII转义
	 * 删除：http://localhost:8080/job/exec?action=deleteJob&jobName=job01&jobGroupName=group01
	 * 暂停：http://localhost:8080/job/exec?action=pausejob&jobName=job01&jobGroupName=group01
	 * 恢复：http://localhost:8080/job/exec?action=resumejob&jobName=job01&jobGroupName=group01
	 * 更新：http://localhost:8080/job/exec?action=rescheduleJob&jobName=job01&jobGroupName=group01&cronExpression=0/10 * * * * ?
	 * 立即执行：http://localhost:8080/job/exec?action=triggerJob&jobName=job01&jobGroupName=group01
	 * 查询：http://localhost:8080/job/exec?action=searchJob
	 * */
	@ResponseBody
	@RequestMapping(value = "/exec", method = {RequestMethod.GET, RequestMethod.POST}, produces = {"application/json;charset=UTF-8"})
	public Object execJob(Model modal, HttpServletRequest request, HttpServletResponse response){
		ResultBean result = new ResultSuccessBean();
		result.setMessage("执行定时任务成功");
		String action = request.getParameter("action");
		Map<Object, Object> params = request2Map(request);
		try {
			Object obj = doInvokeService(jobService, action, params);
			result.setData(obj);
		} catch (Exception e) {
			result = new ResultFailBean();
			result.setMessage("执行与定时任务失败");
			e.printStackTrace();
		}
		return result;
	}
}

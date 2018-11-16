package com.xxx.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * 由quartz管理的定时任务
 * DisallowConcurrentExecution quartz2.0之后引入，在规定时间间隔内一个任务没有执行完毕，下一次执行就处于挂起等待状态，从而保证同一任务同一时间不被多次执行。
 * */
@DisallowConcurrentExecution
public class SimpleTask implements BaseTask{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 无参构造器
	 * */
	public SimpleTask(){
		
	}
	
	/**
	 * 执行任务，调用实际的业务服务
	 * */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Date fireTime = context.getFireTime();
		String jobGroup = context.getJobDetail().getKey().getGroup();
		String jobName = context.getJobDetail().getKey().getName();
		String jobClass = context.getJobDetail().getJobClass().getName();
		JobDataMap jdm = context.getJobDetail().getJobDataMap();
		for(Map.Entry<String, Object> entry: jdm.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue().toString();
		}
		logger.info("Current task: "+jobGroup+"-"+jobName+"-"+jobClass+" fire time: "+dateFormat.format(fireTime));
		logger.info("Get data map from JobDetail: "+JSON.toJSONString(jdm));
		// TODO 调用实际业务服务
		
//		try {
//			Thread.sleep(5*1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

}

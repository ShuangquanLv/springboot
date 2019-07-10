package com.xxx.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.xxx.dao.QuartzConfigMapper;
import com.xxx.schedule.BaseTask;
import com.xxx.service.JobService;
import com.xxx.util.StringUtil;

@Service(value = "jobService")
public class JobServiceImpl implements JobService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	@Autowired
	private QuartzConfigMapper quartzConfigMapper;
	
	//springboot2.x开始自动注入scheduler
    @Autowired
    private Scheduler scheduler;

	
    /**
	 * 添加新任务
	 * @param jobName
	 * @param jobClassName
	 * @param jobGroupName
	 * @param cronExpression 
	 * */
    @Override
	public void addJob(Map<Object, Object> params) throws Exception {
    	String jobName = StringUtil.toString(params.get("jobName"));
    	String jobClassName = params.get("jobClassName").toString();
		String jobGroupName =  params.get("jobGroupName").toString();
		String cronExpression = params.get("cronExpression").toString();
		String jobDataJson = StringUtil.toString(params.get("jobDataJson"));
		if(StringUtils.isEmpty(jobName)){
			jobName = jobClassName;
		}
    	logger.info("开始添加新任务："+jobName);
    	if(scheduler.checkExists(JobKey.jobKey(jobName, jobGroupName))){
    		logger.error("任务："+jobGroupName+":"+jobName+"已经存在！");
    		return;
    	}
    	if(!scheduler.isStarted()){
    		scheduler.start();
    	}
    	
    	Class<BaseTask> cls = (Class<BaseTask>) Class.forName(jobClassName);
    	JobBuilder jb = JobBuilder
    			.newJob(cls)
    			.withIdentity(jobName, jobGroupName);
    	if(StringUtils.isNotEmpty(jobDataJson)){
    		Map<String, String> map = (Map<String, String>) JSON.parse(jobDataJson);
    		JobDataMap jdm = new JobDataMap();
    		for(Map.Entry<String, String> entry: map.entrySet()){
    			jdm.put(entry.getKey(), entry.getValue());
    		}
    		if(!jdm.isEmpty()){
    			jb.setJobData(jdm);	
    		}
    	}
    	JobDetail jobDetail = jb.build();
    	
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        CronTrigger trigger = TriggerBuilder
        		.newTrigger()
        		.withIdentity(jobName, jobGroupName)
        		.withSchedule(scheduleBuilder)
        		.build();
        scheduler.scheduleJob(jobDetail, trigger);
	}
    
    /**
	 * 删除任务
	 * @param jobName
	 * @param jobGroupName
	 * */
	@Override
	public void deleteJob(Map<Object, Object> params) throws Exception {
		String jobName = params.get("jobName").toString();
		String jobGroupName =  params.get("jobGroupName").toString();
		logger.info("开始删除任务："+jobName);
		scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroupName));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroupName));
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
	}

	/**
	 * 暂停任务
	 * @param jobName
	 * @param jobGroupName
	 * */
	@Override
	public void pausejob(Map<Object, Object> params) throws Exception {
		String jobName = params.get("jobName").toString();
		String jobGroupName =  params.get("jobGroupName").toString();
		logger.info("开始暂停任务："+jobName);
		scheduler.pauseJob(JobKey.jobKey(jobName, jobGroupName));
	}

	/**
	 * 恢复任务
	 * @param jobName
	 * @param jobGroupName
	 * */
	@Override
	public void resumejob(Map<Object, Object> params) throws Exception {
		String jobName = params.get("jobName").toString();
		String jobGroupName =  params.get("jobGroupName").toString();
		logger.info("开始恢复任务："+jobName);
		scheduler.resumeJob(JobKey.jobKey(jobName, jobGroupName));
	}

	/**
	 * 更新任务
	 * @param jobName
	 * @param jobGroupName
	 * @param cronExpression
	 * */
	@Override
	public void rescheduleJob(Map<Object, Object> params) throws Exception {
		String jobName = params.get("jobName").toString();
		String jobGroupName =  params.get("jobGroupName").toString();
		String cronExpression = params.get("cronExpression").toString();
		logger.info("开始更新任务："+jobName);
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if(!trigger.getCronExpression().equals(cronExpression)){
        	// 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        }
	}

	/**
	 * 立即执行任务
	 * @param jobName
	 * @param jobGroupName
	 * */
	@Override
	public void triggerJob(Map<Object, Object> params) throws Exception {
		String jobName = params.get("jobName").toString();
		String jobGroupName =  params.get("jobGroupName").toString();
		logger.info("开始立即执行任务："+jobName);
		JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
		scheduler.triggerJob(jobKey);
	}

	/**
	 * 查询任务
	 * */
	@Override
	public List<Map<Object, Object>> searchJob(Map<Object, Object> params) throws Exception {
		logger.info("开始查询任务");
		List<Map<Object, Object>> jobs = new ArrayList<Map<Object, Object>>();
		//TODO 从上下文环境中获取所有任务信息
		
		//遍历所有trigger
//		List<String> triggerGroups = scheduler.getTriggerGroupNames();
//		for(String group: triggerGroups){
//			Set<TriggerKey> keys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(group));
//			for(TriggerKey key: keys){
//				String tGroup = key.getGroup();
//				String tName = key.getName();
//			}
//		}
		//遍历所有job
		List<String> jobGroups = scheduler.getJobGroupNames();
		for(String group: jobGroups){// 遍历所有job组
			Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group));
			for(JobKey key: keys){// 遍历每个组里的jobKey
				String jobGroup = key.getGroup();
				String jobName = key.getName();
				JobDetail jobDetail = scheduler.getJobDetail(key);
				String jobClass = jobDetail.getJobClass().toString();
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);
				for(Trigger trigger: triggers){// 遍历每个jobKey绑定的trigger
					TriggerKey triggerKey = trigger.getKey();
					String triggerGroup = triggerKey.getGroup();
					String triggerName = triggerKey.getName();
					String cronExpr = "";
					if(trigger instanceof CronTrigger){
						CronTrigger ct = (CronTrigger) trigger;
						cronExpr = ct.getCronExpression();
					}
					Map<Object, Object> map = new HashMap<Object, Object>();
					map.put("jobGroup", jobGroup);
					map.put("jobName", jobName);
					map.put("jobClass", jobClass);
					map.put("triggerGroup", triggerGroup);
					map.put("triggerName", triggerName);
					map.put("cronExpr", cronExpr);
					jobs.add(map);
				}
			}
		}
		//TODO 从数据库中获取所有任务信息
		
		return jobs;
	}

}

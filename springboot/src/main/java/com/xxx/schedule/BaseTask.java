package com.xxx.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 所有由quartz管理的定时任务的基类，其他quartz定时任务子类都继承该类
 * */

public interface BaseTask extends Job {
	
	/**
	 * 执行任务(一个task中只能有一个execute)
	 * @param context 任务上下文环境
	 * */
	public void execute(JobExecutionContext context) throws JobExecutionException;
}

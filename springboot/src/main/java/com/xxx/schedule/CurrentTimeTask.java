package com.xxx.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 利用Spring Boot创建定时任务
 * 主类中加入@EnableScheduling注解，启用定时任务的配置
 * 对于@Scheduled的使用可以总结如下几种方式:
 * 		@Scheduled(fixedRate = 5000) ：上一次开始执行时间点之后5秒再执行
 * 		@Scheduled(fixedDelay = 5000) ：上一次执行完毕时间点之后5秒再执行
 * 		@Scheduled(initialDelay=1000, fixedRate=5000) ：第一次延迟1秒后执行，之后按fixedRate的规则每5秒执行一次
 * 		@Scheduled(cron="") ：通过cron表达式定义规则 
 * */
@Component
public class CurrentTimeTask {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Scheduled(cron="*/30 * 12 * * *")
	public void reportCurrentTime() {
		logger.info("Current time: "+dateFormat.format(new Date()));
	}

}

package com.xxx.config;

import java.io.IOException;
import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 定时任务配置（从quartz.properties配置文件获取各个参数项）
 * Springboot2.x已集成quartz，无需自定义quartz.properties，已将参数迁移到appliation.yml
 * */

//@Configuration
public class QuartzConfig {
	
	/**
	 * 将Properties对象赋值到SchedulerFactoryBean并返回
	 * */
	//@Bean(name="SchedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }
	
	/**
	 * 从quartz.properties配置文件中读取参数并返回Properties对象
	 * */
	//@Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("com/xxx/config/quartz.properties"));
        //在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
	
	/**
     * quartz初始化监听器
     */
    //@Bean
    public QuartzInitializerListener executorListener() {
       return new QuartzInitializerListener();
    }
    
    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    //@Bean(name="Scheduler")
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }

}

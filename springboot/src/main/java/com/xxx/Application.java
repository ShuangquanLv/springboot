package com.xxx;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Boot主要配置类、启动类（建议配置到根包下）
 * RestController 表明当前是Web应用，等同于同时添加@Controller和@ResponseBody
 * EnableAutoConfiguration Spring Boot会自动根据jar包的依赖来自动配置项目（Spring Boot建议只要一个类带有该注解）
 * EnableTransactionManagement 开启事务管理
 * EnableScheduling 启用定时任务的配置
 * EnableJms 启动JMS（消息队列）
 * EnableRabbit 启用RabbitMQ（消息队列）
 * */
@SpringBootApplication
@MapperScan("com.xxx.dao")
@EnableTransactionManagement
@EnableScheduling
@EnableJms
@EnableRabbit
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		// 也可参数配置化启动Spring boot项目
//		new SpringApplicationBuilder()
//			.showBanner(true)
//			.sources(Application.class)
//			.run(args);
	}

}

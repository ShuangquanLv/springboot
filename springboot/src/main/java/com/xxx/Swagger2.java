package com.xxx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 与入口类Application.java同级创建Swagger2的配置类
 * */
@Configuration
@EnableSwagger2
public class Swagger2 {

	@Bean
	public Docket createRestApi(){
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				// Swagger2会扫描该包下所以controller定义的API，并产生对应文档（除被@ApiIgnore注解）
				.apis(RequestHandlerSelectors.basePackage("com.xxx.controller"))
				.paths(PathSelectors.any())
				.build();
	}
	
	/**
	 * 创建API的基本信息
	 * */
	private ApiInfo apiInfo(){
		return new ApiInfoBuilder()
				.title("Restful APIs")
				.description("使用Swagger2构建Restful APIs文档")
				.termsOfServiceUrl("http://www.33lend.com")
				.contact("小吕")
				.version("1.0")
				.build();
	}
}

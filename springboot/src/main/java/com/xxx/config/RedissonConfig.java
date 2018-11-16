package com.xxx.config;

import java.io.IOException;
import java.util.Properties;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 分布式的Redis客户端Redisson配置（从redisson.properties配置文件获取各个参数项）
 * 连接模式：Single-单机模式、MasterSlave-主从模式、Sentinel-哨兵模式、Cluster-集群模式、Replicated-云拖管模式
 * */
@Configuration
public class RedissonConfig {

	private static Logger logger = LoggerFactory.getLogger(RedissonConfig.class);
	
	@Bean("Redisson")
	public RedissonClient getRedissonClient(){
		Properties pro = null;
		try {
			pro = getProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String mode = pro.getProperty("redisson.mode");
		String address = pro.getProperty("redisson.address");
		Config config = new Config();
		// 单机模式
		if("Single".equalsIgnoreCase(mode)){
			config.useSingleServer()
				.setAddress(address)
				; 
		// 集群模式
		} else if("Cluster".equalsIgnoreCase(mode)){
			config.useClusterServers()
				.setScanInterval(2*1000)	// 集群状态扫描间隔时间，单位是毫秒
				.addNodeAddress("redis://192.168.3.64:7000", "redis://192.168.3.64:7002", "redis://192.168.3.64:7003")	// 各个节点地址
				;
		// 主从模式
		} else if("MasterSlave".equalsIgnoreCase(mode)){
			config.useMasterSlaveServers()
			.setMasterAddress("redis://192.168.3.64:7000")	// 主节点地址
			.addSlaveAddress("redis://192.168.3.64:7002", "redis://192.168.3.64:7003") // 所有从节点地址
			;
		}
		return Redisson.create(config);
	}
	
	/**
	 * 从redisson.properties配置文件中读取参数并返回Properties对象
	 * */
    private static Properties getProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("com/xxx/config/redisson.properties"));
        //在jedis.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}

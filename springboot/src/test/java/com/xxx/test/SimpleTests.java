package com.xxx.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import com.xxx.util.WechatRedPacket;

/**
 * 测试运行方式：右击 --> Run As --> JUnit Test
 * 新版Spring Boot取消了SpringApplicationConfiguration注解，而用SpringBootTest代替
 * WebAppConfiguration
 * */
@RunWith(SpringRunner.class)
public class SimpleTests {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 测试微信发红包
	 * */
	@Test
	public void testWechat(){
		WechatRedPacket wechat = new WechatRedPacket();
		for(int i=0; i<100; i++){
			List<Float> list = wechat.split(300, 15);
			logger.info(list.toString());
			float total = 0;
			for(float f: list){
				total += f;
			}
			total = wechat.floor(total);
			logger.info("total="+total);
		}
	}

}

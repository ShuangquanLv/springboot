package com.xxx.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

/**
 * WebSocket通信协议公共控制器
 *	1，接受客户端请求
 *	2，向客户端消息推送
 **/
@Controller
//@RequestMapping(value = "/ws")
public class WebSocketController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;    //Spring WebSocket消息发送模板
	
	//发送广播通知
	@MessageMapping("/sendMsg")			//接收客户端发来的消息，客户端发送消息地址为：/app/sendMsg
	@SendTo("/topic/subscribeMsg")		//向客户端发送广播消息，客户端订阅消息地址为：/topic/subscribeMsg
	public String send(String message){
		logger.info("服务端收到消息：{}", message);
		Map<Object, Object> result = getResult("0", "SUCCESS", "服务端成功收到消息:"+message);
		return JSON.toJSONString(result);
	}
	
	//发送点对点消息
	@MessageMapping("/sendSelf")		//接收客户端发来的消息，客户端发送消息地址为：/app/sendSelf
	@SendToUser("/queue/sendSelf")	//向当前发消息的客户端（自己）发送消息的发送结果，客户端订阅消息地址为：/user/queue/sendSelf
	public String sendSelf(String message){
		logger.info("服务端收到消息：{}", message);
		Map<Object, Object> result = getResult("0", "SUCCESS", "服务端成功收到消息:"+message);
		return JSON.toJSONString(result);
	}
	
	private Map<Object, Object> getResult(String code, String status, String msg){
		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put("code", code);
		result.put("status", status);
		result.put("msg", msg);
		return result;
	}
}

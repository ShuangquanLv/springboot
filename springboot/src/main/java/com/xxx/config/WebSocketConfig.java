package com.xxx.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
/**
 * WebSocket通信配置
 * */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 注册stomp端点
	 * */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 允许使用SocketJS跨域访问WebSocketServer，通过http://localhost:8092/ws访问服务器
		registry.addEndpoint("/ws")	// 注册一个/ws节点
		.addInterceptors(getHandshakeInterceptor())		// 添加握手拦截器（可选项）
//		.setHandshakeHandler(getHandshakeHandler()) 	// 注册握手处理器（可选项）
		.setAllowedOrigins("*")	// 允许可跨域的域名
		.withSockJS();			// 使用SockJS协议
	}
	
	@Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
		//设置客户端接收消息地址的前缀（可不设置）
		registry.enableSimpleBroker(
				"/queue", 	//点对点消息前缀
				"/topic"	//广播消息前缀
		);
		 //设置客户端向服务器发送消息的地址前缀（可不设置）
		registry.setApplicationDestinationPrefixes("/app");
		//设置客户端接收点对点消息地址的前缀，默认为 /user
		registry.setUserDestinationPrefix("/user");
	}
	
	/**
	 * WebSocket 握手拦截器
	 * connect和send时，可做一些用户认证拦截处理
	 * */
	@SuppressWarnings(value={"unused"})
	private HandshakeInterceptor getHandshakeInterceptor(){
		return new HandshakeInterceptor(){

			/**
			 * 握手之前
			 * @return 返回是否同意握手
			 * */
			@Override
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//				ServletServerHttpRequest req = (ServletServerHttpRequest) request;
//				String token = req.getServletRequest().getParameter("token");
//				HttpHeaders headers = request.getHeaders();
//				String token = headers.get("token").get(0);
//				if("=D5J490".equalsIgnoreCase(token)){	//与数据库存储值进行比对，验证客户端是否允许连接
//					logger.info("token验证通过，握手成功！");
//					return true;
//				} else {
//					logger.error("token未通过验证，握手失败！");
//					return false;
//				}
				return true;
			}

			/**
			 * 握手之后
			 * */
			@Override
			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Exception exception) {
				// TODO Auto-generated method stub
				
			}};
	}
	
	/**
	 * WebSocket 握手处理器
	 * */
	private DefaultHandshakeHandler getHandshakeHandler(){
		return new DefaultHandshakeHandler(){
			
		};
	}

}

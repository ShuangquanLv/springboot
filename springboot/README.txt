Spring Boot

1, Spring Boot默认日志框架Logback
	1.1, 每日滚动输出到文件(logback-spring.xml) --- Done
	1.2, 多环境输出   --- Done
	1.3, 整合其他日志框架log4j, log4j2

2, Spring Boot与数据库
	1.1, 集成整合mybatis（Spring Boot版本非常关键） --- Done
	1.2, 多数据源连接 
	1.3, 事务控制(@EnableTransactionManagement, @Transactional) --- Done
	1.4, 分页查询插件pagehelper --- Done
	1.5, 多事务控制
	1.6, controller接受各种参数请求方式(CommonController)  --- Done
	1.7, 文件上传、下载、ftp传输  --- Done
	1.8, 模板引擎thymeleaf(其他模板引擎FreeMarker, Velocity)，前端ajax请求
	1.9, 获取多环境下自定义配置参数的值(@Value， application-{profile}.yml) --- Done
	1.10, 使用Swagger2构建Restful API 文档（以及Swagger2用法详解） --- Done
	1.11, 使用Actuator优雅启、停应用服务（http://blog.csdn.net/chinrui/article/details/78685032） --- Done
	1.12, 使用Spring Security实现权限控制 --- Done
	1.13, 项目jar打包，并在linux环境命令启动(cd到项目根目录，即与pom.xml同一级，mvn clean先清空再mvn package打包jar到/target目录下，java -jar springboot.jar开启)
	1.14, Spring Boot整合ActiveMQ，ActiveMQ连接池，ActiveMQ集群，ActiveMQ消息延迟定时投递(Producer, Consumer) --- Done
	1.15, Spring Boot整合quartz，quartz连接池，持久化到数据库，quartz集群，服务器时钟同步  --- Done
	
3, Spring Boot创建定时任务(@EnableScheduling, @Scheduled)  --- Done

4, Spring Boot整合缓存框架
	4.1, 整合Redis (StringRedisTemplate) --- Done
	4.2, 分布式Redis集群 cluster(RedisTemplate) --- Done
 	4.3, 分布式锁(用Jedis客户端实现，JedisServiceImpl) --- Done
 	4.4, 缓存与数据库一致性
 	4.5, lua脚本
 	4.6, Jedis(Redis客户端java版实现) --- Done
 	4.7, Redisson(分布式操作的Redis客户端java版实现) --- None
 	4.8, Redis哨兵sentinel --- Done

5, Spring Boot整合Shiro安全框架

6, Spring Boot整合JPA
	6.1, 提供者Spring Data JPA

7, 分布式服务框架Dubbo+分布式应用程序协调服务Zookeeper
	7.1, Dubbox (Dubbo升级Restful版)
	7.1, 分布式服务框架RPC
	7.2, 资源调度和治理中心SOA, MSA
	7.3, 整合Zookeeper生产者和消费者(SimpleConsumerService, SimpleProviderService) --- Done
	7.4, Zookeeper服务监控台(Dubbo admin) --- Done
	7.5, 伪集群
	7.6, 分布式锁
	
	
8, Spring Boot整合Netty5(基于Java NIO的网络应用框架)

9, 大数据
	9.1、Hadoop
	9.2、Hbase：构建在HDFS上的分布式、面向列的存储系统
	9.3、创建MapReduce(分布式计算框架)工程
	9.4、HDFS(Hadoop Distributed File System分布式文件系统)
	9.5、Yarn(Yet Another Resource Negotiator)集群资源的统一管理和调度系统

10, 支付宝柔性事务-SOA分布式事务控制
	1、DTS 分布式事务服务是基于两阶段提交（2 phase commit = 2pc）原理
	2、atomikos

11, 消息队列
	11.1、ActiveMQ消息事务
	11.2、RabbitMQ分别整合SpringMVC, SpringBoot
	11.3、RocketMQ
	11.4、ZeroMQ
	11.5、Kafka

12, 微信小程序	

13, WebSocket消息推送（浏览器与服务器全双工长连接通信）
	13.1, web领域实时消息推送（WebSocketConfig, WebSocketController --- Done）
	13.2, Socket.io.js和Node.js
	13.3, 七种WebSocket框架（Netty, Undertow, Jetty, Vert.x, Grizzly, spray-websocket, Nodejs-websocket, Go）
	
14, 对外开放https和http请求api服务
	14.1、利用keytool生成证书
	14.2、添加https配置
	14.3、http访问自动跳转到https	

5, eclipse安装插件
	3.1, mybatis generator
	3.2, google浏览器报文模拟器Postman --- Done
	3.3, Markdown编辑器插件
	
6, curl命令（基于URL语法传输数据的命令行工具和库） --- Done
	6.1, 下载安装（https://curl.haxx.se/download.html, E:\Soft\curl-7.58.0\src\curl.exe）
	6.2, 发送shutdown信号： curl -X POST host:port/shutdown
	6.3, 权限控制下发送shutdown信号： curl -u username:password -X POST url:port/XXXXXX/shutdown	

Spring Cloud --- 微服务架构（http://blog.csdn.net/forezp/article/details/70148833）
	Eureka --- 负责服务的注册与发现，将服务连接起来
	Ribbon, Feign --- 负载均衡客户端
	Hystrix --- 断路器，负责监控服务之间的调用情况，连续多次失败进行熔断保护
	Hystrix Dashboard, Turbine --- 断路器仪表盘，负责监控Hystrix的熔断情况，并给予图形化显示
	Spring Cloud Config --- 提供统一的配置中心服务
	Spring Cloud Bus --- 当配置文件发生变化时，负责通知各个服务去获取最新的配置信息
	Zuul --- 转发所有对外的请求和服务，起到API网关的作用
	Sleuth+Zipkin --- 为服务之间调用提供链路追踪，方便后续分析
	Actuator --- 管理端点的服务，负责服务优雅下线（独立部署的服务）
	Consul --- 服务的发现和配置的工具（分布式、高可用性、高扩展性）
	
Vert.x	

Node.js+Express
	
		
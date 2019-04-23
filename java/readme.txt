eas是大行政企业管理系统

模块说明：
	common:公共模块，包含多数据源、全局异常处理、全局404处理代码
	eureka-server:所有服务注册中心，所有服务提供者会自动向该注册中心注册服务,端口号8000
	config-server:分布式配置文件，不同模块有对应分布式配置文件,端口号8010，例如：service-dfs对应的分布式文件名是service-dfs-dev.yml/service-dfs-dev.properties（开发环境），service-dfs-prod.yml/service-dfs-prod.properties（生产环境）
	service-gateway:服务网关，用户授权认证在这里执行，端口号8020
	service-bpmx:流程引擎中心服务，端口号8030
	service-buc:用户中心服务器，端口号8050
	service-oa:OA业务服务，端口号8070
	service-assetm:资产管理服务，端口号8090
    service-dfs:分布式文件系统服务，端口号8110
	web-admin:后台管理系统，端口号8130
	service-push:阿里云推送调用中心--->调用方式查看buc-TestPushController类，端口号8093

项目启动说明：
    1、启动eureka-server注册中心
    2、启动config-server分布式配置中心
    3、启动其他服务service-dfs/service-ucenter等
    4、启动service-gateway路由网关
	
	
	
	
	
	service-bpmx：kaoqin包，oa下面的broadcast/conference都是考勤的，可删
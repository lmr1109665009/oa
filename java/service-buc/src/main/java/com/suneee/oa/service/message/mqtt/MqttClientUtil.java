package com.suneee.oa.service.message.mqtt;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;  
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;  
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.ucp.base.common.Constants;
  
public class MqttClientUtil { 
	private static final Logger LOGGER = LoggerFactory.getLogger("mqttlog");
    private static final String HOST = AppConfigUtil.get(Constants.MQTT_HOST, "tcp://172.19.6.104:61613");  
    private static final String TOPIC = AppConfigUtil.get(Constants.MQTT_TOPIC, "usertopic");  
    private static final String CLIENTID = AppConfigUtil.get(Constants.MQTT_CLIENTID, "OA");//接入端唯一标志   
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";
    
    private static MqttClient client;  
    private static MqttConnectOptions options; 
    
    private static ScheduledExecutorService scheduler;
    private static long SCHEDULE_PERIOD = AppConfigUtil.getInt(Constants.MQTT_SCHEDULE_PERIOD, 60);
    /** 
     * 启动MQTT客户端重连定时任务：
     * 1）MQTT客户端未启动，则启动MQTT客户端
     * 2）MQTT客户端已启动但连接异常，则重新连接
     */
    public static void startReconnect(){
    	scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {  
            public void run() { 
            	if(LOGGER.isDebugEnabled()){
            		LOGGER.debug("定时器检查MQTT客户端连接是否正常........");
            	}
            	// MQTT客户端未启动，则启动MQTT客户端
                if (client == null ) {  
                	try {
						MqttClientUtil.start();
						if(LOGGER.isInfoEnabled()){
                        	LOGGER.info("MQTT消息客户端启动成功！");
                        }
					} catch (MqttException e) {
						LOGGER.error("MQTT消息客户端重新连接失败：" + e.getMessage(), e);
					}
                }  else{
                	if(client.isConnected()){
                		if(LOGGER.isDebugEnabled()){
                			LOGGER.debug("MQTT客户端连接正常........");
                		}
                	}else{
                		// MQTT客户端已启动，但是连接异常，则重新连接
                		try {  
                            client.connect(options);  
                            //订阅消息  
                            int[] Qos  = {1};  
                            String[] topic1 = {TOPIC};  
                            client.subscribe(topic1, Qos); 
                            if(LOGGER.isInfoEnabled()){
                            	LOGGER.info("MQTT消息客户端重新连接成功！");
                            }
                        } catch (MqttException e) {  
                        	LOGGER.error("MQTT消息客户端重新连接失败：" + e.getMessage(), e);
                        }  
                	}
                }
            }  
        }, 0 * 1000, SCHEDULE_PERIOD * 1000, TimeUnit.MILLISECONDS);
    }
  
    /** 
     * 启动MQTT消息客户端
     * @throws MqttException
     */
    private static void start() throws MqttException{  
        try {
			// host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存  
			client = new MqttClient(HOST, CLIENTID, new MemoryPersistence());  
			// MQTT的连接设置  
			options = new MqttConnectOptions();  
			// 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接  
			options.setCleanSession(false);  
			// 设置连接的用户名  
			options.setUserName(USERNAME);  
			// 设置连接的密码  
			options.setPassword(PASSWORD.toCharArray());  
			// 设置超时时间 单位为秒  
			options.setConnectionTimeout(10);  
			// 设置会话心跳时间 单位为秒 服务器会每隔30秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制  
			options.setKeepAliveInterval(30);  
			
			// 设置回调  
			PushCallback callbakc = AppUtil.getBean(PushCallback.class);
			client.setCallback(callbakc);  
			MqttTopic topic = client.getTopic(TOPIC);  
			//setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息    
			options.setWill(topic, "close".getBytes(), 2, true);  
			options.isCleanSession();
			if(!client.isConnected()){
				client.connect(options); 
			}  
			
			//订阅消息  
			int[] Qos  = {1};  
			String[] topic1 = {TOPIC};  
			client.subscribe(topic1, Qos);
		} catch (Exception e) {
			throw new MqttException(e);
		} 
    }  
}
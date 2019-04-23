package com.suneee.oa.service.message.mqtt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suneee.core.util.AppConfigUtil;
import com.suneee.ucp.base.common.Constants;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 发布消息的回调类
 *
 * 必须实现MqttCallback的接口并实现对应的相关接口方法CallBack 类将实现 MqttCallBack。
 * 每个客户机标识都需要一个回调实例。在此示例中，构造函数传递客户机标识以另存为实例数据。
 * 在回调中，将它用来标识已经启动了该回调的哪个实例。
 * 必须在回调类中实现三个方法：
 *
 *  public void messageArrived(MqttTopic topic, MqttMessage message)接收已经预订的发布。
 *
 *  public void connectionLost(Throwable cause)在断开连接时调用。
 *
 *  public void deliveryComplete(MqttDeliveryToken token))
 *  接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用。
 *  由 MqttClient.connect 激活此回调。
 *
 *  @author xiongxianyun
 *
 */
public class PushCallback implements MqttCallback {
	private static final  Logger LOGGER = LoggerFactory.getLogger("mqttlog");
	private static final String SYSTEM = AppConfigUtil.get(Constants.UC_SYSTEM, "OA");
	private Map<String, IMqttMessageHandler>  mqttMessageHandlerMap= new HashMap<String, IMqttMessageHandler>();

	public Map<String, IMqttMessageHandler> getMqttMessageHandlerMap() {
		return mqttMessageHandlerMap;
	}

	public void setMqttMessageHandlerMap(Map<String, IMqttMessageHandler> mqttMessageHandlerMap) {
		this.mqttMessageHandlerMap = mqttMessageHandlerMap;
	}

	@Override
	public void connectionLost(Throwable cause) {
		// 连接丢失后，一般在这里面进行重连
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("与消息服务器连接断开，等待定时器重连......");
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("向Apollo服务器发送消息成功！");
		}
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// 获取消息内容
		String messageStr = new String(message.getPayload(),"utf-8");
		// 客户端掉线通知消息
		if("close".equals(messageStr)){
			if(LOGGER.isInfoEnabled()){
				LOGGER.info("客户端掉线通知消息不予处理：" + messageStr + "");
			}
			return;
		}

		// 处理消息
		try {
			JSONObject jsonMessage = JSONObject.fromObject(messageStr);
//			IMqttMessageHandler messageHandler = mqttMessageHandlerMap.get(jsonMessage.getString("type"));
			IMqttMessageHandler messageHandler = mqttMessageHandlerMap.get("user");
			if(messageHandler == null || SYSTEM.equals(jsonMessage.getString("system"))){
				if(LOGGER.isInfoEnabled()){
					LOGGER.info("消息类型：" + jsonMessage.getString("type") + "，操作类型：" + jsonMessage.getString("operation")
							+ "，不处理此条消息：" + messageStr);
				}
			} else {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("开始处理消息：" + messageStr);
				}
				try {
					messageHandler.handleMessage(jsonMessage.getJSONArray("message"), jsonMessage.getString("operation"));
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("消息处理结束.");
					}
				} catch (Exception e) {
					LOGGER.error("消息处理异常：" + e.getMessage(), e);
				}
			}
		} catch (JSONException e) {
			LOGGER.error("消息格式异常，异常消息：" + messageStr, e);
		}
	}
}
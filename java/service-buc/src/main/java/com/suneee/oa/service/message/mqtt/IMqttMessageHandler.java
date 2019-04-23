/**
 * 
 */
package com.suneee.oa.service.message.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;

/**
 * mqtt消息处理接口
 * 
 * @author xiongxianyun
 *
 */
public abstract class IMqttMessageHandler {
	protected static final Logger LOGGER = LoggerFactory.getLogger(IMqttMessageHandler.class);
	
	protected static final String OPERATION_ADD = "insert";
	protected static final String OPERATION_DEL = "delete";
	protected static final String OPERATION_UPD = "update";
	
	public abstract void handleMessage(JSONArray messagejsonArr, String operation) throws Exception;
}

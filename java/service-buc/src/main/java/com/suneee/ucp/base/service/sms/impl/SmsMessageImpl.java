/**
 * @Title: SmsMessageImpl.java 
 * @Package com.suneee.ucp.base.service.sms.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.ucp.base.service.sms.impl;

import com.suneee.core.sms.IShortMessage;
import com.suneee.core.sms.impl.NoneMessageImpl;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.util.HttpUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName: SmsMessageImpl 
 * @Description: 发送短信消息 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-03-13 16:07:39 
 *
 */
public class SmsMessageImpl implements IShortMessage{
	public static SmsMessageImpl instance;
	protected Logger logger = LoggerFactory.getLogger(NoneMessageImpl.class);
	private static Lock lock = new ReentrantLock();
	private String url = AppConfigUtil.get(Constants.MESSAGE_SMS_URL);
	private String prefix = AppConfigUtil.get(Constants.MESSAGE_SMS_PREFIX);
	private String signature = AppConfigUtil.get(Constants.MESSAGE_SMS_SIGNATURE);
	private String typeCode = AppConfigUtil.get(Constants.MESSAGE_SMS_TYPECODE);
	
	public boolean sendSms(List<String> mobiles, String message) {
		try {
			// 获取请求参数
			String requestData = this.handleData(mobiles, message);
			
			// 请求地址
			String reqUrl = url;
			if(!reqUrl.endsWith(Constants.SEPARATOR_BACK_SLANT)){
				reqUrl = reqUrl + Constants.SEPARATOR_BACK_SLANT;
			}
			reqUrl = reqUrl + Constants.MESSAGE_SMS_API;
			
			// 设置请求头
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json;charset=" + Constants.CHARSET_UTF8);
			// 发送请求
			String result = HttpUtils.sendData(reqUrl, requestData, headers, Constants.CHARSET_UTF8);
			JSONObject jsonObj = JSONObject.fromObject(result);
			
			// 接口返回成功标识，表示短信发送成功
			if("1".equals(jsonObj.getString("status"))){
				return true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return false;
	}
	
	/**
	 * 获取单例对象
	 * 
	 * @return
	 */
	public static SmsMessageImpl getInstance() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null)
					instance = new SmsMessageImpl();

			} finally {
				lock.unlock();
			}
		}
		return instance;
	}

	/** 
	 * 构造接口请求数据
	 * @param mobiles 接收短信手机号
	 * @param message 短信内容
	 * @return
	 */
	private String handleData(List<String> mobiles, String message){
		if(mobiles == null || mobiles.size() == 0 || StringUtils.isBlank(message)){
			return null;
		}
		
		// 上下文，解析模板使用
		JSONObject context = new JSONObject();
		// 短信前缀
		context.put("prefix", prefix);
		// 短信签名
		context.put("signature", signature);
		// 短信内容
		context.put("content", message);
		
		// 	通道类型
		List<String> channels = new ArrayList<String>();
		channels.add("SMS");
		
		// 接收人
		List<JSONObject> receivers = new ArrayList<JSONObject>();
		JSONObject user = null;
		for(String mobile : mobiles){
			user = new JSONObject();
			user.put("mobile", mobile);
			receivers.add(user);
		}
		
		JSONObject data = new JSONObject();
		data.put("typeCode", typeCode);
		data.put("channels", channels);
		data.put("title", message);
		data.put("context", context);
		data.put("receivers", receivers);
		
		return data.toString();
	}
}

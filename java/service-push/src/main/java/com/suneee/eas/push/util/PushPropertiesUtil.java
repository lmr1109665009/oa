package com.suneee.eas.push.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云推送调用自定义配置文件类
 * @author liuhai
 * @Date 2018/06/21
 */
@Component
@ConfigurationProperties(prefix="pushproperties")
public class PushPropertiesUtil {
	
	private String messagesUrl;

	public String getMessagesUrl() {
		return messagesUrl;
	}

	public void setMessagesUrl(String messagesUrl) {
		this.messagesUrl = messagesUrl;
	}
	

}

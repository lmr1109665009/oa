/**
 * 
 */
package com.suneee.ucp.base.event.listener;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.suneee.core.util.AppUtil;
import com.suneee.platform.model.system.MessageLog;
import com.suneee.platform.service.system.MessageLogService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.event.def.ApolloMessage;
import com.suneee.ucp.base.event.def.ApolloMessageEvent;
import com.suneee.ucp.base.service.system.MessageService;

/**
 * @author Administrator
 *
 */
@Service
@Async
public class ApolloMessageEventListener implements ApplicationListener<ApolloMessageEvent>{
	@Resource 
	private MessageService msgService;
	
	@Override
	public void onApplicationEvent(ApolloMessageEvent event) {
		// 发送消息
		Integer state =  MessageLog.STATE_FAIL;
		ApolloMessage message = event.getApolloMessage();
		boolean isSuccess = msgService.sendMessage(message);
		if(isSuccess){
			state = MessageLog.STATE_SUCCESS;
		}
		
		// 获取消息接收人
		List<Map<String, Object>> targets = message.getTarget();
		String emails = "";
		for(Map<String, Object> target : targets){
			if(!StringUtils.isBlank(emails)){
				emails = emails + Constants.SEPARATOR_COMMA;
			}
			emails = emails + target.get("email");
		}
		
		// 保存发送消息日志
		MessageLogService messageLogService = (MessageLogService) AppUtil.getBean(MessageLogService.class);
		messageLogService.addMessageLog(message.getData().toString(), emails, MessageLog.APOLLO_TYPE, state);
	}

}

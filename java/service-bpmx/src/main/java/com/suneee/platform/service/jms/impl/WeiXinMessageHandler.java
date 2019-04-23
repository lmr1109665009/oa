package com.suneee.platform.service.jms.impl;

import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.model.MessageModel;
import com.suneee.platform.model.system.SysUser;
import com.suneee.weixin.model.msg.impl.TextMessage;
import com.suneee.weixin.util.WeiXinUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.model.MessageModel;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.util.MessageUtil;
import com.suneee.weixin.model.msg.impl.TextMessage;
import com.suneee.weixin.util.WeiXinUtil;

public class WeiXinMessageHandler implements IMessageHandler {
	private final Log logger = LogFactory.getLog(WeiXinMessageHandler.class);

	@Override
	public String getTitle() { 
		return "微信消息";
	}

	@Override
	public void handMessage(MessageModel model) {
		SysUser recever = (SysUser) model.getReceiveUser();
		if ( model.getSendUser() == null) return;
		// 没有微信号就不发了
		if(recever == null || recever.getHasSyncToWx()==0) return; 
		sendMessage(MessageUtil.getContent(model,false,false),recever.getAccount());
	}
	
	private void sendMessage(String messageContent,String weiXinId){
		TextMessage message=new TextMessage(weiXinId,"",messageContent);
		try { 
			String resultJson = WeiXinUtil.sendTextMessage(message) ;
			JSONObject result = JSONObject.parseObject(resultJson);
			String errcode = result.getString("errcode");
			if("0".equals(errcode)) return;
			throw new RuntimeException(result.getString("errmsg"));
		} catch (Exception e) {
			logger.error("向”"+weiXinId+"“发送微信消息失败！"+e.getMessage());
		}
	}

	@Override
	public boolean getIsDefaultChecked() {
		return false;
	}

}

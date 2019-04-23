package com.suneee.platform.service.jms.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.model.MessageModel;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.system.SysUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.model.MessageModel;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.MessageEngine;
import com.suneee.platform.service.system.SysTemplateService;
import com.suneee.platform.service.util.MessageUtil;

public class SmsMessageHandler implements IMessageHandler {

	private final Log logger = LogFactory.getLog(SmsMessageHandler.class);
	@Resource
	private MessageEngine messageEngine;
	@Resource
	SysTemplateService sysTemplateService;

	@Override
	public String getTitle() {
		return "短信";
		//return ContextUtil.getMessages("message.sms");
	}

	@Override
	public void handMessage(MessageModel model) {
		String strMobile = "";
		if (model.getReceiveUser() != null)
			strMobile =((SysUser) model.getReceiveUser()).getMobile();
		//手机号为空或不是手机号，直接返回
		if (StringUtil.isEmpty(strMobile) || !StringUtil.isMobile(strMobile)) {
			logger.info("手机号："+strMobile+"为空或不是手机号");
			return;
		}
		List<String> mobiles = new ArrayList<String>();
		mobiles.add(strMobile);
		messageEngine.sendSms(mobiles, MessageUtil.getContent(model,false,true));
		logger.debug("Sms");
	}

	

	@Override
	public boolean getIsDefaultChecked() {
		return false;
	}

}

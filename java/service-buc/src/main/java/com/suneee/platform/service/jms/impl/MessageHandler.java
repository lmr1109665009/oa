package com.suneee.platform.service.jms.impl;

import javax.annotation.Resource;

import com.suneee.core.jms.IJmsHandler;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.jms.MessageHandlerContainer;
import com.suneee.core.model.MessageModel;
import com.suneee.core.jms.IJmsHandler;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.jms.MessageHandlerContainer;
import com.suneee.core.model.MessageModel;

public class MessageHandler implements IJmsHandler {
	
	@Resource
    MessageHandlerContainer messageHandlerContainer;

	@Override
	public void handMessage(Object model) {
		MessageModel msgModel=(MessageModel)model;
		String type=msgModel.getInformType();
		IMessageHandler handler= messageHandlerContainer.getHandler(type);
		handler.handMessage(msgModel);

	}

}

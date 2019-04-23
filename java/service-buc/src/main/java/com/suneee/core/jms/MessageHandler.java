package com.suneee.core.jms;

import javax.annotation.Resource;

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

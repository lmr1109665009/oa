package com.suneee.platform.service.jms.impl;

import java.util.Date;

import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.model.MessageModel;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.MessageSend;
import com.suneee.platform.model.system.SysUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.model.MessageModel;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.MessageSend;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.util.MessageUtil;

public class OpenfireMessageHandler implements IMessageHandler {
	private final Log logger = LogFactory.getLog(OpenfireMessageHandler.class);
	XMPPConnection openfireConnection = null;
	ChatManager systemNotityChatManager = null;
	private String serviceName;

	@Override
	public String getTitle() {
		return " 即时消息";
	}

	@Override
	public void handMessage(MessageModel model) {
		MessageSend messageSend = new MessageSend();

		if (model.getReceiveUser() == null || model.getSendUser() == null)	return;
		messageSend.setId(UniqueIdUtil.genId());
		messageSend.setUserName(MessageUtil.getSendUserName(model));// 发送人姓名
		messageSend.setUserId(model.getSendUser().getUserId());// 发送人ID
		messageSend.setSendTime(model.getSendDate());
		messageSend.setMessageType(MessageSend.MESSAGETYPE_FLOWTASK);
		messageSend.setContent(MessageUtil.getContent(model,false,false));
		messageSend.setSubject(MessageUtil.getSubject(model));
		messageSend.setCreateBy(model.getSendUser().getUserId());// 创建人ID
		messageSend.setRid(model.getReceiveUser().getUserId());// 接收人ID
//		messageSend.setReceiverName(model.getReceiveUser().getFullname());// 接收人姓名
		messageSend.setReceiverName(ContextSupportUtil.getUsername((SysUser) model.getReceiveUser()));// 接收人姓名
		messageSend.setCreatetime(model.getSendDate() == null ? new Date() : model.getSendDate());
		sendMessage(messageSend);
	}

	private void sendMessage(MessageSend messageSend) {
		if (openfireConnection == null && !hasLogined)
			getSystemNotityChatManager();
		if (openfireConnection == null)
			return;

		//获取收信人的账号。
		SysUser user = AppUtil.getBean(SysUserService.class).getById(messageSend.getRid());
		if (user == null)
			return;
		//获取消息
		Message msg = getMessageByMessageSend(messageSend);
		try {
			//发送消息
			if (systemNotityChatManager == null)
				return;

			systemNotityChatManager.createChat(user.getAccount() + "@" + serviceName, new MessageListener() {
				public void processMessage(Chat chat, Message message) {/* 不做回执消息监听器 */
				}
			}).sendMessage(msg);

		} catch (Exception e) {
			logger.warn("连接异常，发送消息失败！");
			e.printStackTrace();
		}
	}

	private Message getMessageByMessageSend(MessageSend messageSend) {
		Message msg = new Message();
		msg.setSubject(messageSend.getSubject());
		msg.setBody(messageSend.getContent());
		return msg;
	}

	/***
	 * 获取系统通知账号连接信息
	 */
	private boolean hasLogined = false;//无论连接是否失败

	private void getSystemNotityChatManager() {
		hasLogined = true;
		String systemAccount = PropertyUtil.getByAlias("systemNotification.account", "admin");
		String systemPassword = PropertyUtil.getByAlias("systemNotification.password", "1");
		serviceName = PropertyUtil.getByAlias("openfire.serviceName", "vansai");

		openfireConnection = null;
		try {
			openfireConnection = new XMPPConnection(serviceName);
			openfireConnection.connect();
			openfireConnection.login(systemAccount, systemPassword);
			systemNotityChatManager = openfireConnection.getChatManager();
		} catch (Exception e) { //XMPPException
			e.printStackTrace();
			openfireConnection = null;
			logger.error("连接Openfire服务器失败！");
		}
	}

	

	@Override
	public boolean getIsDefaultChecked() {
		return false;
	}

	public static void main(String[] args) throws Exception {
		XMPPConnection con = new XMPPConnection("vansai");
		con.connect();
		con.login("admin", "1");
		ChatManager chatManager = con.getChatManager();
		chatManager.createChat("zhangs@vansai", new MessageListener() {

			@Override
			public void processMessage(Chat chat, Message message) {

			}
		}).sendMessage("sss");
		;

	}

	

}

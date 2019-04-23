package com.suneee.platform.service.jms.impl;

import com.hotent.core.mail.model.Mail;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.model.MessageModel;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.NodeMsgTemplateService;
import com.suneee.platform.service.system.MessageEngine;
import com.suneee.platform.service.system.SysTemplateService;
import com.suneee.platform.service.util.MessageUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Resource;

public class MailMessageHandler implements IMessageHandler {

	private final Log logger = LogFactory.getLog(MailMessageHandler.class);
	@Resource
	private MessageEngine messageEngine;
	@Resource
	SysTemplateService sysTemplateService;
	@Resource
	NodeMsgTemplateService nodeMsgTemplateService;
	@Resource
    FreemarkEngine freemarkEngine;

	@Override
	public String getTitle() {
		return "邮件";
	}

	@Override
	public void handMessage(MessageModel model) {
		Mail mail=new Mail();
		String subject=MessageUtil.getSubject(model);
		String content=MessageUtil.getContent(model,true,false);
		mail.setSubject(subject);
		mail.setContent(content);
		
		String[] toAddress=model.getTo();
		String[] bcc=model.getBcc();
		String[] cc=model.getCc();

		if (model.getTo() != null && model.getTo().length > 0) {
			mail.setReceiverAddresses(StringUtils.join(toAddress, ","));
			if(BeanUtils.isNotEmpty(bcc))
				mail.setBcCAddresses(StringUtils.join(bcc, ","));
			if(BeanUtils.isNotEmpty(cc))
				mail.setCopyToAddresses(StringUtils.join(cc, ","));
		} else {
			String eamilStr = "";
			if (model.getReceiveUser() != null)
				eamilStr = ((SysUser) model.getReceiveUser()).getEmail();
			if (StringUtil.isEmpty(eamilStr) || !StringUtil.isEmail(eamilStr))
				return;// 判断一下邮箱是否为空或不是邮箱，则直接返回
			mail.setReceiverAddresses(eamilStr);
		}
		messageEngine.sendMail(mail);
		logger.debug("MailModel");
	}



	/**
	 * 默认不勾选邮件
	 */
	@Override
	public boolean getIsDefaultChecked() {
		return false;
	}

}

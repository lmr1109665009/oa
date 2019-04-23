package com.suneee.platform.service.jms.impl;

import com.hotent.core.mail.MailUtil;
import com.hotent.core.mail.model.MailSeting;
import com.suneee.core.jms.IJmsHandler;
import com.suneee.platform.model.mail.OutMailUserSeting;
import com.suneee.platform.service.jms.SysMailModel;
import com.suneee.platform.service.mail.OutMailUserSetingService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Resource;

public class SysMailHandler implements IJmsHandler {
	
	@Resource
	private OutMailUserSetingService outMailUserSetingService;
	private final Log logger = LogFactory.getLog(SysMailHandler.class);

	@Override
	public void handMessage(Object model) {
		try {
			if(!(model instanceof SysMailModel)) return;
			SysMailModel sysMailModel = (SysMailModel)model;
			Long outMailUserSetingId = sysMailModel.getOutMailUserSetingId();
			if(outMailUserSetingId == null || outMailUserSetingId < 0) return;
			OutMailUserSeting outMailUserSeting = outMailUserSetingService.getById(outMailUserSetingId);
			MailSeting mailSetting = OutMailUserSetingService.getByOutMailUserSeting(outMailUserSeting);
			MailUtil util = new MailUtil(mailSetting);
			util.send(sysMailModel.getMail());
			logger.debug("out mail process success.");
		} catch (Exception e) {
			logger.error("out mail process error " + ExceptionUtils.getRootCauseMessage(e));
			e.printStackTrace();
		}
	}

}

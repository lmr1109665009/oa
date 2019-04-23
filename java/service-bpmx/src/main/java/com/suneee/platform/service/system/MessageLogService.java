package com.suneee.platform.service.system;

import java.util.Date;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.MessageLogDao;
import com.suneee.platform.model.system.MessageLog;
import com.suneee.platform.model.system.MessageSend;
import com.suneee.platform.model.system.SysUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.MessageLogDao;
import com.suneee.platform.model.system.MessageLog;

/**
 * <pre>
 * 对象功能:消息日志 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2012-11-29 16:24:35
 * </pre>
 */
@Service
public class MessageLogService extends BaseService<MessageLog> {
	@Resource
	private MessageLogDao dao;

	public MessageLogService() {
	}

	@Override
	protected IEntityDao<MessageLog, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 新增消息日志
	 * @param subject 主题
	 * @param receiver 接受者 ，多个人 ","分隔
	 * @param messageType 消息类型
	 * @param state 状态
	 */
	public void addMessageLog(String subject, String receiver,
							  Integer messageType, Integer state) {
		MessageLog messageLog = new MessageLog();
		messageLog.setId(UniqueIdUtil.genId());
		messageLog.setSubject(StringUtils.substring(subject, 0, 200));
		messageLog.setReceiver(receiver);
		messageLog.setMessageType(messageType);
		messageLog.setState(state);
		messageLog.setSendTime(new Date());
		dao.add(messageLog);
	}

	/**
	 * 新增站内消息日志
	 * @param subject 主题
	 * @param receiver 接受者 ，多个人 ","分隔
	 * @param messageType 消息类型
	 * @param state 状态
	 * @param messageSend
	 */
	public void addInnerMessageLog(String subject, String receiver,
								   Integer messageType, Integer state,MessageSend messageSend) {
		MessageLog messageLog = new MessageLog();
		messageLog.setId(UniqueIdUtil.genId());
		messageLog.setSubject(StringUtils.substring(subject, 0, 200));
		messageLog.setReceiver(receiver);
		messageLog.setMessageType(messageType);
		messageLog.setState(state);
		messageLog.setSendTime(new Date());

		messageLog.setCreatetime(messageSend.getCreatetime());
		messageLog.setCreateBy(messageSend.getCreateBy());
		messageLog.setUpdateBy(messageSend.getCreateBy());
		dao.add(messageLog);
	}

	/**
	 * 新增站内消息日志
	 * @param subject 主题
	 * @param receiver 接受者 ，多个人 ","分隔
	 * @param messageType 消息类型
	 * @param state 状态
	 * @param sysUser 当前用户
	 */
	public void addPushMessageLog(String subject, String receiver,
								  Integer messageType, Integer state, SysUser sysUser) {
		MessageLog messageLog = new MessageLog();
		messageLog.setId(UniqueIdUtil.genId());
		messageLog.setSubject(StringUtils.substring(subject, 0, 200));
		messageLog.setReceiver(receiver);
		messageLog.setMessageType(messageType);
		messageLog.setState(state);
		messageLog.setSendTime(new Date());

		messageLog.setCreatetime(new Date());
		messageLog.setCreateBy(sysUser.getUserId());
		messageLog.setUpdatetime(new Date());
		messageLog.setUpdateBy(sysUser.getUserId());
		dao.add(messageLog);
	}
}

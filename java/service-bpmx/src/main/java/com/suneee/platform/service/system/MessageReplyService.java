package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.MessageReplyDao;
import com.suneee.platform.model.system.MessageReply;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 对象功能:消息回复 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:pkq
 * 创建时间:2012-01-14 15:15:43
 */
@Service
public class MessageReplyService extends BaseService<MessageReply>
{
	@Resource
	private MessageReplyDao dao;
	
	public MessageReplyService()
	{
	}
	
	@Override
	protected IEntityDao<MessageReply, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 保存 MessageReply
	 * @param messageReply
	 * @param sysUser
	 */
	public void saveReply(MessageReply messageReply, SysUser sysUser) throws Exception{
		messageReply.setId(UniqueIdUtil.genId());
		messageReply.setReplyId(sysUser.getUserId());
		messageReply.setReply(sysUser.getFullname());
		Date now = new Date();
		messageReply.setReplyTime(now);
		add(messageReply);
	}
	
	/**
	 * 获得已回复此消息的人员
	 * @param messageId
	 * @return
	 */
	public List<MessageReply> getReplyByMsgId(Long messageId)
	{
		return dao.getReplyByMsgId(messageId);
	}
	
}

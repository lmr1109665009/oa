package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.MessageReadDao;
import com.suneee.platform.model.system.MessageRead;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 对象功能:接收状态 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-01-14 15:14:52
 */
@Service
public class MessageReadService extends BaseService<MessageRead>
{
	@Resource
	private MessageReadDao dao;
	
	public MessageReadService()
	{
	}
	
	@Override
	protected IEntityDao<MessageRead, Long> getEntityDao()
	{
		return dao;
	}	
	
	/**
	 * 添加数据MessageRead
	 * @param messageId
	 * @param sysUser
	 * @throws Exception
	 */
	public void addMessageRead(Long messageId, SysUser sysUser) throws Exception{
		
		MessageRead msgRead = dao.getReadByUser(messageId, sysUser.getUserId());
		if(msgRead==null){
			Date now = new Date();
			MessageRead messageRead = new MessageRead();
			messageRead.setId(UniqueIdUtil.genId());
			messageRead.setMessageId(messageId);
			messageRead.setReceiverId(sysUser.getUserId());
			messageRead.setReceiver(sysUser.getFullname());
			messageRead.setReceiveTime(now);
			add(messageRead);
		}
	}
	
	/**
	 * 获得已读此消息的人员
	 * @param messageId
	 * @return
	 */
	public List<MessageRead> getReadByMsgId(Long messageId)
	{
		return dao.getReadByMsgId(messageId);
	}
	
}

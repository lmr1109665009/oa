/**
 * 对象功能:消息回复 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-01-14 15:15:43
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.system.MessageReply;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageReplyDao extends BaseDao<MessageReply>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return MessageReply.class;
	}
	
	/**
	 * 获得已回复此消息的人员
	 * @param messageId
	 * @return
	 */
	public List<MessageReply> getReplyByMsgId(Long messageId)
	{
		return this.getBySqlKey("getReplyByMsgId", messageId);
	}
	
	/**
	 * 根据消息ID删除回复
	 * @param messageId
	 */
	public int delReplyByMsgId(Long messageId){
		return delBySqlKey("delByMessageId", messageId);
	}
	
	/**
	 * 根据消息ID删除回复
	 * @param messageId
	 */
	public int delReplyByMsgIds(Long[] messageIds){
		int delCount = 0;
		if(BeanUtils.isEmpty(messageIds))
			return 0;
		for(Long messageId:messageIds){
			int i=delReplyByMsgId(messageId);
			delCount+=i;
		}
		return delCount;
	}
}
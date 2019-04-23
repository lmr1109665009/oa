/**
 * 对象功能:发送消息 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-01-14 15:10:58
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.MessageSend;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MessageSendDao extends BaseDao<MessageSend>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return MessageSend.class;
	}

	
	/**
	 * 查询某个用户的接收消息
	 * @param queryFilter
	 * @return
	 */
	public List<MessageSend> getReceiverByUser(QueryFilter queryFilter)
	{
		return this.getBySqlKey("getReceiverByUser", queryFilter);
	}
	

	public Integer getCountReceiverByUser(Long userId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("receiverId", userId);
		return (Integer) this.getOne("getCountReceiverByUser", params);
	}	
	
	/**
	 * 获取单条个人未读信息，按发送时间排序
	 * @param userId
	 * @return
	 */
	public List<MessageSend> getNotReadMsg(Long receiverId)
	{
//		String statment=this.getIbatisMapperNamespace() + ".getNotReadMsg_"+this.getDbType();
//		return this.getBySqlKey(statment, receiverId);
		return getBySqlKey("getNotReadMsgByUserId",receiverId);
	}
	
	public Integer getCountNotReadMsg(Long userId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("receiverId", userId);
		return (Integer) this.getOne("getCountNotReadMsg", params);
	}	
	
	/**
	 * 获取个人未读信息，分页
	 * @param userId
	 * @param pb
	 * @return
	 */
	public List<MessageSend> getNotReadMsgByUserId(long userId,PageBean pb) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("receiverId", userId);
		return getBySqlKey("getNotReadMsgByUserId",params,pb);
	}

}
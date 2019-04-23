package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.MessageLog;

/**
 * <pre>
 * 对象功能:消息日志 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2012-11-29 16:24:35
 * </pre>
 */
@Repository
public class MessageLogDao extends BaseDao<MessageLog> {
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return MessageLog.class;
	}

}
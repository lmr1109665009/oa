package com.suneee.platform.dao.system;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.MessageLinkmanGroup;
/**
 *<pre>
 * 对象功能:常用联系人组 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-07-29 10:29:57
 *</pre>
 */
@Repository
public class MessageLinkmanGroupDao extends BaseDao<MessageLinkmanGroup>
{
	@Override
	public Class<?> getEntityClass()
	{
		return MessageLinkmanGroup.class;
	}

	
	
	
	
}
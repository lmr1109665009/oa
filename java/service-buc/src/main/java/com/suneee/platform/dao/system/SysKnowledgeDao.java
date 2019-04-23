package com.suneee.platform.dao.system;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysKnowledge;
/**
 *<pre>
 * 对象功能:知识库 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-28 10:15:59
 *</pre>
 */
@Repository
public class SysKnowledgeDao extends BaseDao<SysKnowledge>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysKnowledge.class;
	}

	public void delByType(Long typeId) {
		this.delBySqlKey("delByType", typeId);
	}

	
	
	
	
}
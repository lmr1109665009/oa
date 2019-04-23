package com.suneee.platform.dao.system;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysKnowMarkRel;
/**
 *<pre>
 * 对象功能:SYS_KNOW_MARK_REL Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-12-29 14:25:15
 *</pre>
 */
@Repository
public class SysKnowMarkRelDao extends BaseDao<SysKnowMarkRel>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysKnowMarkRel.class;
	}

	public void deleteByKnow(Long knowId) {
		delBySqlKey("deleteByKnow", knowId);
	}

	
	
	
	
}
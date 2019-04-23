package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysOrgTactic;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:组织策略 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:hugh
 * 创建时间:2015-03-31 13:43:14
 *</pre>
 */
@SuppressWarnings("unchecked")
@Repository
public class SysOrgTacticDao extends BaseDao<SysOrgTactic>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysOrgTactic.class;
	}

	
	
	
	
}
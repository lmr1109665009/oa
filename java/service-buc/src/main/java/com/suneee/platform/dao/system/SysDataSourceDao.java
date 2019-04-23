package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysDataSource;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:SYS_DATA_SOURCE Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:Aschs
 * 创建时间:2014-08-21 10:50:18
 *</pre>
 */
@Repository
public class SysDataSourceDao extends BaseDao<SysDataSource>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysDataSource.class;
	}

}
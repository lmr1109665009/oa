package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysReport;
/**
 *<pre>
 * 对象功能:系统条件脚本 Dao类
 * 开发公司:hotent
 * 开发人员:heyifan
 * 创建时间:2013-04-05 11:34:56
 *</pre>
 */
@Repository
public class SysReportDao extends BaseDao<SysReport>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysReport.class;
	}

}
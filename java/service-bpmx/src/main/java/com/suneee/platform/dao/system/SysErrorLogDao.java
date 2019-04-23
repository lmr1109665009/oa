package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysErrorLog;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:系统错误日志 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2013-07-12 16:42:11
 *</pre>
 */
@Repository
public class SysErrorLogDao extends BaseDao<SysErrorLog>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysErrorLog.class;
	}

}
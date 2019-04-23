package com.suneee.platform.dao.system;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysWsLog;
/**
 *<pre>
 * 对象功能:SYS_WS_LOG Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:cjj
 * 创建时间:2013-05-31 10:41:48
 *</pre>
 */
@Repository
public class SysWsLogDao extends BaseDao<SysWsLog>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysWsLog.class;
	}

}
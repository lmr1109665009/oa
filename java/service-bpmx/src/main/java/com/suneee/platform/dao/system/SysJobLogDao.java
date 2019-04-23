/**
 * 对象功能:SYS_JOBLOG Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:phl
 * 创建时间:2011-12-28 17:01:51
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysJobLog;

@Repository
public class SysJobLogDao extends BaseDao<SysJobLog>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return SysJobLog.class;
	}
}
package com.suneee.ucp.mh.dao.attendance;

import org.springframework.stereotype.Repository;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.attendance.VacationLog;
/**
 *<pre>
 * 对象功能:结余调整日志 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:37:20
 *</pre>
 */
@Repository("ucpVacationLogDao")
public class VacationLogDao extends UcpBaseDao<VacationLog>
{
	@Override
	public Class<?> getEntityClass()
	{
		return VacationLog.class;
	}
}
package com.suneee.ucp.mh.dao.attendance;

import org.springframework.stereotype.Repository;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.attendance.VacationRemain;
/**
 *<pre>
 * 对象功能:假期结余 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:32:12
 *</pre>
 */
@Repository("ucpVacationRemainDao")
public class VacationRemainDao extends UcpBaseDao<VacationRemain>
{
	@Override
	public Class<?> getEntityClass()
	{
		return VacationRemain.class;
	}
}
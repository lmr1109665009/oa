package com.suneee.ucp.mh.dao.attendance;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.attendance.AttendanceShift;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:考勤班次表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:03:50
 *</pre>
 */
@Repository("ucpAttendanceShiftDao")
public class AttendanceShiftDao extends UcpBaseDao<AttendanceShift>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AttendanceShift.class;
	}

	public List<AttendanceShift> getShiftUserList(QueryFilter filter){
		return this.getBySqlKey("getShiftUserList",filter);
	}
	
}
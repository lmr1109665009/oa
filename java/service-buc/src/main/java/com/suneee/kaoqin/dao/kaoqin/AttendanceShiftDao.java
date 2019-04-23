package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.model.kaoqin.AttendanceShift;
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
@Repository
public class AttendanceShiftDao extends BaseDao<AttendanceShift>
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
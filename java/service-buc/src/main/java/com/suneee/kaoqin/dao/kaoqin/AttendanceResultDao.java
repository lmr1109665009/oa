package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.AttendanceResult;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:考勤结果表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-04 10:48:13
 *</pre>
 */
@Repository
public class AttendanceResultDao extends BaseDao<AttendanceResult>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AttendanceResult.class;
	}

	
	
	
	
}
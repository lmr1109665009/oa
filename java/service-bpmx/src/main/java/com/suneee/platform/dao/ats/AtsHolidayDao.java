package com.suneee.platform.dao.ats;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsHoliday;
/**
 *<pre>
 * 对象功能:考勤请假单 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-07-15 14:59:52
 *</pre>
 */
@Repository
public class AtsHolidayDao extends BaseDao<AtsHoliday>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsHoliday.class;
	}

	
	
	
	
}
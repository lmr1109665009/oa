package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsHolidayType;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:假期类型 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-16 20:47:17
 *</pre>
 */
@Repository
public class AtsHolidayTypeDao extends BaseDao<AtsHolidayType>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsHolidayType.class;
	}

	
}
package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsOverTime;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:考勤加班单 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-07-15 15:00:33
 *</pre>
 */
@Repository
public class AtsOverTimeDao extends BaseDao<AtsOverTime>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsOverTime.class;
	}

	
	
	
	
}
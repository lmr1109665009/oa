package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsAttenceGroup;
/**
 *<pre>
 * 对象功能:考勤组 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-26 10:07:27
 *</pre>
 */
@Repository
public class AtsAttenceGroupDao extends BaseDao<AtsAttenceGroup>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsAttenceGroup.class;
	}
	
}
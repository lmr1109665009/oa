package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.MonGroup;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:监控分组 Dao类
 * 开发公司:广州宏天软件
 * 开发人员:zyp
 * 创建时间:2013-06-08 11:14:50
 *</pre>
 */
@Repository
public class MonGroupDao extends BaseDao<MonGroup>
{
	@Override
	public Class<?> getEntityClass()
	{
		return MonGroup.class;
	}

}
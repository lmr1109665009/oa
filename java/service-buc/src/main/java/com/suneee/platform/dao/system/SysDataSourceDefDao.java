package com.suneee.platform.dao.system;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysDataSourceDef;
/**
 *<pre>
 * 对象功能:SYS_DATA_SOURCE_DEF Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:Aschs
 * 创建时间:2014-08-20 11:10:07
 *</pre>
 */
@Repository
public class SysDataSourceDefDao extends BaseDao<SysDataSourceDef>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysDataSourceDef.class;
	}

}
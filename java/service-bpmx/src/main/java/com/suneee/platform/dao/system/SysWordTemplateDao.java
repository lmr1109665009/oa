package com.suneee.platform.dao.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysWordTemplate;
/**
 *<pre>
 * 对象功能:word套打模板 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:guojh
 * 创建时间:2015-03-23 10:48:07
 *</pre>
 */
@Repository
public class SysWordTemplateDao extends BaseDao<SysWordTemplate>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysWordTemplate.class;
	}

	public SysWordTemplate getByAlias(String alias) {
		return getUnique("getByAlias", alias);
	}

	public List<SysWordTemplate> getAllTemplate() {
		return getBySqlKey("getAllTemplate");
	}
	
}
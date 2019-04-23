package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.SysUrlPermission;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:URL地址拦截管理 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:wdr
 * 创建时间:2014-03-27 16:32:01
 *</pre>
 */
@Repository
public class SysUrlPermissionDao extends BaseDao<SysUrlPermission>
{
	@Override
	public Class<?> getEntityClass()
	{
		return SysUrlPermission.class;
	}

}
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.system.AuthRole;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:SYS_AUTH_ROLE Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miao
 * 创建时间:2014-08-08 10:16:21
 *</pre>
 */
@Repository
public class AuthRoleDao extends BaseDao<AuthRole>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AuthRole.class;
	}
	
	public void delByAuthId(Long authId) {
		this.delBySqlKey("delByAuthId", authId);
	}

}
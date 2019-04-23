package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.MonOrgRole;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:监控组权限分配 Dao类
 * 开发公司:广州宏天软件
 * 开发人员:zyp
 * 创建时间:2013-06-17 18:38:16
 *</pre>
 */
@Repository
public class MonOrgRoleDao extends BaseDao<MonOrgRole>
{
	@Override
	public Class<?> getEntityClass()
	{
		return MonOrgRole.class;
	}
	
	/**
	 * 根据组ID删除配置的权限。
	 * @param groupId
	 */
	public void delByGroupId(Long groupId){
		this.delBySqlKey("delByGroupId", groupId);
	}

}
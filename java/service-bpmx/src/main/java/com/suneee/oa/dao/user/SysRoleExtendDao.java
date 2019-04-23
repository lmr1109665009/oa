/**
 * @Title: SysRoleExtendDao.java 
 * @Package com.suneee.oa.dao.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.dao.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.platform.model.system.SysRole;
import com.suneee.ucp.base.dao.UcpBaseDao;

/**
 * @ClassName: SysRoleExtendDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-09 15:43:56 
 *
 */
@Repository
public class SysRoleExtendDao extends UcpBaseDao<SysRole>{

	/** (non-Javadoc)
	 * @Title: getEntityClass 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see com.suneee.core.db.GenericDao#getEntityClass()
	 */
	@Override
	public Class<SysRole> getEntityClass() {
		// TODO Auto-generated method stub
		return SysRole.class;
	}
	
	/** 
	 * 根据角色名称查询角色信息
	 * @param roleName 角色名称
	 * @param roleId 角色ID
	 * @param systemId 子系统ID
	 * @param enterpriseCode 所属企业
	 * @return
	 */
	public SysRole getByRoleName(String roleName, Long roleId, Long systemId, String enterpriseCode){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleName", roleName);
		params.put("roleId", roleId);
		params.put("systemId", systemId);
		params.put("enterpriseCode", enterpriseCode);
		return (SysRole)this.getOne("getByRoleName", params);
	}
	
	/** 
	 * 用于新增/更新角色信息时，查询角色别名是否已经存在
	 * @param alias
	 * @param roleId
	 * @return
	 */
	public SysRole getByAlias(String alias, Long roleId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("alias", alias);
		params.put("roleId", roleId);
		return this.getUnique("getByRoleAlias",params);
	}
	
	/** 
	 * 角色禁用/启用
	 * @param roleId
	 * @param enabled
	 * @return
	 */
	public int updEnabled(Long roleId, Short enabled){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId", roleId);
		params.put("enabled", enabled);
		return this.update("updEnabled", params);
	}

}

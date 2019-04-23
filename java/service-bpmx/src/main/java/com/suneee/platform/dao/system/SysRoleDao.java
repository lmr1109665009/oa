/**
 * 对象功能:系统角色表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-11-18 16:24:10
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.SysRole;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Repository
public class SysRoleDao extends BaseDao<SysRole>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return SysRole.class;
	}
	
	/**
	 * 按角色别名取得角色实体
	 * @param roleAlias
	 * @return
	 */
	public SysRole getByRoleAlias(String roleAlias){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("alias", roleAlias);
		return this.getUnique("getByRoleAlias",params);
	}
	
	/**
	 * 根据组获取角色列表。
	 * @param groupId
	 * @return
	 */
	public List<SysRole> getByOrgMonGroup(Long groupId){
		return this.getBySqlKey("getByOrgMonGroup", groupId);
	}
	/**
	 * 获取参数
	 * @param systemId
	 * @param pb
	 * @return
	 */
	public List<SysRole> getBySystemId(Long systemId,PageBean pb)
	{
		return getBySqlKey("getBySystemId", systemId, pb);
	}
	/**
	 * 判断角色名称是否存在。
	 * @param roleName
	 * @return
	 */
    public boolean isExistRoleAlias(String alias){
    	Integer count=(Integer)this.getOne("isExistRoleAlias", alias);
    	return count>0;
    }
    
    /**
     * 根据别名和角色id判断是否重复。
     * @param alias 角色别名
     * @param roleId	角色id
     * @return
     */
    @SuppressWarnings("unchecked")
	public boolean isExistRoleAliasForUpd(String alias,Long roleId){
    	Map map=new HashMap();
    	map.put("alias", alias);
    	map.put("roleId", roleId);
    	Integer count=(Integer)this.getOne("isExistRoleAliasForUpd", map);
    	return count>0;
    }
    
    /**
     * 查询所有角色及子系统
     * @param queryFilter
     * @return
     */
	public List<SysRole> getRole(QueryFilter queryFilter){
		return getBySqlKey("getRole", queryFilter);
	}

	/**
	 * 根据UserId取得系统角色
	 * @param userId
	 * @return
	 */
	public List<SysRole> getByUserId(Long userId)
	{
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		return getBySqlKey("getByUserId", params);
	}
	
	/**
	 * 根据组织ID获取角色
	 * @param orgId
	 * @return
	 */
	public List<SysRole> getByOrgId(Long orgId){
		return getBySqlKey("getByOrgId",orgId);
	}
	
	/**
     * 获取所有子系统的角色
     * @param queryFilter
     * @return
     */
	public List<SysRole> getRoleTree(QueryFilter queryFilter)
	{
		String sqlKey="getRoleTree_"+getDbType();
		return getBySqlKey(sqlKey, queryFilter);
	}
	
	
	/**
	 * 根据系统id查询系统的角色。
	 * @param systemId
	 * @return
	 */
	public List<SysRole> getBySystemId(Long systemId){
		List<SysRole> list=this.getBySqlKey("getBySystemId", systemId);
		return list;
	}	
	
	/**
	 * 根据系统id和角色名称查询子系统角色列表。
	 * @param systemId
	 * @param roleName
	 * @return
	 */
	public List<SysRole> getRoleBySystemId(Long systemId,String roleName){
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("systemId", systemId);
		map.put("roleName", StringUtils.isNotEmpty(roleName)?("%"+roleName+"%"):"" );
		List<SysRole> list=this.getBySqlKey("getRoleBySystemId", map);
		return list;
	}	
	
	
	
	/**
	 * 根据组织ID查询可以授权的组织。
	 * @param orgId
	 * @return
	 */
	public List<SysRole> getManageRolesByOrgId(Long orgId){
		List<SysRole> list=this.getBySqlKey("getManageRolesByOrgId", orgId);
		return list;
	}
	
	/**
	 * 根据角色名称查询角色
	 * @param roleName
	 * @return
	 */
	public  List<SysRole> getByRoleName(String roleName) {
		Map<String, Object> params = new HashMap<String, Object>();
		return this.getBySqlKey("getByRoleName", params);
	}
	
	public List<SysRole> loadSecurityRoleByAlias(String alias,String roleName){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("defaultUrl", alias);
		map.put("roleName", roleName);
		return (List<SysRole>)this.getBySqlKey("loadSecurityRoleByAlias", map);
	}
	
	/**
	 * 通过分级授权ID获取 角色
	 * @param authId
	 * @return
	 */
	public List<SysRole> getByAuthId(Long authId) {
		return this.getBySqlKey("getByAuthId", authId);
	}
	/**
	 * 获取某人的可分配角色
	 * @param params
	 * @return
	 */
	public List<SysRole> getUserAssignRole(Map params){
		return this.getBySqlKey("getUserAssignRole", params);
	}
	
	
}
/**
 * 对象功能:用户所属组织或部门 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:pkq
 * 创建时间:2011-12-07 18:23:24
 */
package com.suneee.platform.dao.system;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import org.springframework.stereotype.Repository;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.SysUserOrg;
import com.suneee.platform.model.system.UserPosition;

@Repository
public class SysUserOrgDao extends BaseDao<SysUserOrg>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return SysUserOrg.class;
	}
	
	@Override
	public int delById(Long userPosId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userPosId", userPosId);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		params.put("delFrom", UserPosition.DELFROM_USER_POS_DEL);
		return this.update("delById", params);
	}
	
	/**
	 * 对象功能：查找该条件的用户组织的实体
	 * 开发公司:广州宏天软件有限公司
	 * 开发人员:pkq
     * 创建时间:2011-11-08 12:04:22 
	 */
	public SysUserOrg getUserOrgModel(Long userId,Long orgId)	
	{
		Map param =new HashMap();
		param.put("userId", userId);
		param.put("orgId", orgId);
		SysUserOrg sysUserOrg=(SysUserOrg)getUnique("getUserOrgModel", param);
		return sysUserOrg;
	}	
	
	/**
	 *根据组织id查询userid列表
	 */
	public List<SysUserOrg> getByOrgId(Long orgId)	
	{
		return this.getBySqlKey("getByOrgId", orgId);
	}	
	

	/**
	 * 根据组织id获取组织负责人。
	 * @param orgId
	 * @return
	 */
	public List<SysUserOrg> getChargeByOrgId(Long orgId)	{
		return this.getBySqlKey("getChargeByOrgId", orgId);
	}	
	
	
	
	
	/**
	 * 返回某个用户所在所在的组织。
	 * @param userId
	 * @return
	 */
	public List<SysUserOrg> getOrgByUserId(Long userId){
		List<SysUserOrg> sysUserOrg=this.getBySqlKey("getOrgByUserId", userId);
		return sysUserOrg;
	}
	
	/**
	 * 返回该负责人的组织。
	 * @param userId
	 * @return
	 */
	public List<SysUserOrg> getChargeByUserId(Long userId)
	{
		return this.getBySqlKey("getChargeByUserId",userId);
	}
	
	
	/**
	 * 根据用户ID删除组织用户关系。
	 * @param userId
	 * @param delFrom 删除源头
	 */
	public void delByUserId(Long userId, String delFrom){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("updatetime", new Date());
		params.put("updateBy",  ContextUtil.getCurrentUserId());
		params.put("delFrom", delFrom);
		this.delBySqlKey("delByUserId",params);
	}
	
	/**
	 * 根据组织ID删除组织用户关系。
	 * @param orgId
	 */
	public void delByOrgId(Long orgId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgId", orgId);
		params.put("updatetime", new Date());
		params.put("updateBy",  ContextUtil.getCurrentUserId());
		params.put("delFrom", UserPosition.DELFROM_ORG_DEL);
		 this.delBySqlKey("delByOrgId",params);
	}
	
	/**
	 * 更新用户组织为非主组织。
	 * @param userId
	 */
	public void updNotPrimaryByUserId(Long userId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("updatetime", new Date());
		params.put("updateBy",  ContextUtil.getCurrentUserId());
		this.update("updNotPrimaryByUserId", params);
	}
	
	/**
	 * 根据组织ID返回用户列表。
	 * @param filter
	 */
	public List<SysUserOrg> getUserByOrgId(QueryFilter filter){
		
		return  this.getBySqlKey("getUserByOrgId", filter);
	}
	
	/**
	 * 根据用户ID获取可以管理的组织。
	 * @param userId	用户ID。
	 */
	public List<SysUserOrg> getManageOrgByUserId(Long userId){
		return  this.getBySqlKey("getManageOrgByUserId", userId);
	}
	
	public List<SysUserOrg> getUserByOrgIds(String orgIds){
		Map param =new HashMap();
		param.put("orgIds", orgIds);
		return  this.getBySqlKey("getUserByOrgIds", param);
	}
	
	
	/**
	 * 获取用户的主组织(没有主组织则返回任一组织)
	 * 
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public SysUserOrg getPrimaryByUserId(Long userId) {
		return this.getUnique("getPrimaryByUserId", userId);
	}
	
	
	
}
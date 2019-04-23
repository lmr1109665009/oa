/**
 * 
 */
package com.suneee.ucp.base.dao.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
@Repository
public class SysUserExtDao extends UcpBaseDao<SysUser>{

	@Override
	public Class<SysUser> getEntityClass() {
		return SysUser.class;
	}
	
	/**
	 * 根据查询条件获取用户信息
	 * @param params
	 * @return
	 */
	public List<SysUser> getAll(Map<String, Object> params){
		String statement = getIbatisMapperNamespace() + ".getAll";
		return this.getList(statement, params);
	}
	
	/**
	 * 根据用户ID获取用户信息
	 * @param sysUserIds
	 * @return
	 */
	public List<SysUser> getSysUserByIds(List<Long> sysUserIds){
		if(null == sysUserIds || 0 == sysUserIds.size()){
			return null;
		}
		return getBySqlKey("getSysUserByIds", sysUserIds);
	}
	
	/**
	 * 根据用户中心用户ID获取用户信息
	 * @param ucUserid
	 * @return
	 */
	public SysUser getByUcUserid(Long ucUserid){
		if(null == ucUserid || 0 == ucUserid){
			return null;
		}
		return getUnique("getByUcUserid", ucUserid);
	}
	
	/**
	 * 更新用户同步信息
	 * @param sysUser
	 */
	public void updateSyncInfo(SysUser sysUser){
		if(null == sysUser){
			return;
		}
		sysUser.setUpdatetime(new Date());
		sysUser.setUpdateBy(ContextUtil.getCurrentUserId());
		update("updateSyncInfo", sysUser);
	}
	
	/**
	 * 更新用户区域信息
	 * @param params
	 */
	public void updateRegion(Map<String, Object> params){
		if(params == null){
			return;
		}
		params.put("updatetime", new Date());
		params.put("updateby", ContextUtil.getCurrentUserId());
		update("updateRegion", params);
	}

	public List<SysUser> getUserByOrgPath(Map<String, Object> params){
		return this.getBySqlKey("getUserByOrgPath", params);
	}
	/**
	 * 根据用户中心用户ID更新用户信息
	 * @param sysUser
	 */
	public void updateByUcUserid(SysUser sysUser){
		update("updateByUcUserid", sysUser);
	}
}

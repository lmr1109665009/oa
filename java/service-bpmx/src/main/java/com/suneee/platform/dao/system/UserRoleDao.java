/**
 * 对象功能:用户角色映射表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-16 15:47:55
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.UserRole;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRoleDao extends BaseDao<UserRole>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return UserRole.class;
	}
	
	/**
	 * 对象功能：查找该条件的用户角色的实体
	 */
	public UserRole getUserRoleModel(Long userId,Long roleId)	
	{
		Map param=new HashMap();
		param.put("userId", userId);
		param.put("roleId", roleId);
		UserRole userRole=(UserRole)getUnique("getUserRoleModel", param);
		return userRole;
	}	
	
	/**
	 * 根据用户id和角色id进行删除。
	 * @param userId	用户id
	 * @param roleId	角色id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int delUserRoleByIds(Long userId,Long roleId)
	{
		Map params=new HashMap();
		params.put("userId", userId);
		params.put("roleId", roleId);
	
		int affectCount= this.delBySqlKey("delUserRoleByIds", params);
		return affectCount;
	}
	
	/**
	 * 获取某个角色下的所有用户ID
	 * @param roleId
	 * @return
	 */
	public List<Long> getUserIdsByRoleId(Long roleId)
	{
		List list=getBySqlKey("getUserIdsByRoleId", roleId);
		return list;
	}
	/**
	 * 获取某个角色下所有的用户信息
	 * @param roleId
	 * @return
	 */
	public List<UserRole> getUserRoleByRoleId(Long roleId){
		return getBySqlKey("getUserRoleByRoleId", roleId);
	}
	
	/**
	 * 根据角色删除关联关系。
	 * @param roleIds
	 */
    public void delByRoleId(Long roleId){
    	delBySqlKey("delByRoleId", roleId);
    }
    
    /**
     * 根据用户删除用户和角色的映射关系。
     * @param userId
     */
    public void delByUserId(Long userId){
    	delBySqlKey("delByUserId", userId);
    }
    /**
     * 根据用户id查询对应的角色。
     * @param userId
     * @return
     */
    public List<UserRole> getByUserId(Long userId){
    	return this.getByUserIdAndEnterpriseCode(userId, ContextSupportUtil.getCurrentEnterpriseCode());
    }
    
    public List<UserRole> getByUserIdAndEnterpriseCode(Long userId, String enterpriseCode){
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("userId", userId);
    	params.put("enterpriseCode", enterpriseCode);
    	return getBySqlKey("getByUserId", params);
    }
    
    /**
     * 根据多个角色取得用户数据。
     * @param roleIds
     * @return
     */
    public List<UserRole> getUserByRoleIds(String roleIds){
    	Map params=new HashMap();
		params.put("roleIds", roleIds);
		return getBySqlKey("getUserByRoleIds", params);
    }
    
    /** 
     * 删除用户在指定企业下的用户角色关系
     * @param userId
     * @param enterpriseCode
     * @return
     */
    public int delByUserIdAndEnterpriseCode(Long userId, String enterpriseCode){
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("userId", userId);
    	params.put("enterpriseCode", enterpriseCode);
    	return this.delBySqlKey("delByUserIdAndEnterpriseCode", params);
    }
}
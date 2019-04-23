/**
 * @Title: UserEnterpriseDao.java 
 * @Package com.suneee.oa.dao.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.dao.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.oa.model.user.UserEnterprise;
import com.suneee.ucp.base.dao.UcpBaseDao;

/**
 * @ClassName: UserEnterpriseDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-07 15:33:11 
 *
 */
@Repository
public class UserEnterpriseDao extends UcpBaseDao<UserEnterprise>{

	/** (non-Javadoc)
	 * @Title: getEntityClass 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see com.suneee.core.db.GenericDao#getEntityClass()
	 */
	@Override
	public Class<UserEnterprise> getEntityClass() {
		// TODO Auto-generated method stub
		return UserEnterprise.class;
	}
	
	/** 
	 * 根据用户ID获取用户企业关系
	 * @param userId 用户ID
	 * @return
	 */
	public List<UserEnterprise> getByUserId(Long userId){
		return this.getBySqlKey("getByUserId", userId);
	}
	
	/** 
	 * 根据用户ID获取已删除的用户企业关系
	 * @param userId
	 * @return
	 */
	public List<UserEnterprise> getDelByUserId(Long userId){
		return this.getBySqlKey("getDelByUserId", userId);
	}
	
	/** 
	 * 根据用户ID删除用户企业关系
	 * @param userId 用户ID
	 * @return
	 */
	public int delByUserId(Long userId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		return this.update("delByUserId", params);
	}
	
	@Override
	public int delById(Long userEnterpriseId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userEnterpriseId", userEnterpriseId);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		return this.update("delById", params);
	}

	/** 
	 * 根据用户ID和企业编码获取用户企业关系信息
	 * @param userId
	 * @param enterpriseCode
	 * @return
	 */
	public UserEnterprise getByUserIdAndCode(Long userId, String enterpriseCode){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("enterpriseCode", enterpriseCode);
		return this.getUnique("getByUserIdAndCode", params);
	}
}

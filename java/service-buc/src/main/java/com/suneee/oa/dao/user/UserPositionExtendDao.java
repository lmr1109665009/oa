package com.suneee.oa.dao.user;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户岗位关系UserPosition扩展DAO类
 * @author xiongxianyun
 *
 */
@Repository
public class UserPositionExtendDao extends UcpBaseDao<UserPosition> {
	@Override
	public Class<UserPosition> getEntityClass() {
		return UserPosition.class;
	}

	/**
	 * 获取用户岗位信息
	 * @param userId 用户ID
	 * @param posId 岗位ID
	 * @param jobId 职务ID
	 * @param orgId 组织ID
	 * @return
	 */
	public UserPosition getByIds(Long userId, Long posId, Long jobId, Long orgId) {
	    Map<String, Long> params = new HashMap<String, Long>();
	    params.put("userId", userId);
	    params.put("posId", posId);
	    params.put("orgId", orgId);
	    params.put("jobId", jobId);
	    return getUnique("getByIds", params);
	}
	
	/** 
	 * 获取已删除的用户岗位关系
	 * @param userId
	 * @return
	 */
	public List<UserPosition> getDelByUserId(Long userId){
		return getBySqlKey("getDelByUserId", userId);
	}
	
	/** 
	 * 根据用户ID获取用户的岗位信息
	 * (该方法用于外部系统查询使用，不用实现多企业查询)
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPositonByUserId(Long userId){
		return this.getBySqlKeyGenericity("getPositonByUserId", userId);
	}
	
	public int delByUserIdAndEnterpriseCode(Long userId, String enterpriseCode, String delFrom){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("enterpriseCode", enterpriseCode);
		params.put("updatetime", new Date());
		params.put("updateBy", ContextUtil.getCurrentUserId());
		params.put("delFrom", delFrom);
		return this.update("delByUserIdAndEnterpriseCode", params);
	}
}
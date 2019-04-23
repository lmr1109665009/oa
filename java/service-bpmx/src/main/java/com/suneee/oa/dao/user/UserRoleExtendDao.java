package com.suneee.oa.dao.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.UserRole;
import com.suneee.ucp.base.dao.UcpBaseDao;

/**
 * 用户角色关系UserRole扩展DAO类
 * @author xiongxianyun
 *
 */
@Repository
public class UserRoleExtendDao extends UcpBaseDao<UserRole> {
	@Override
	public Class<UserRole> getEntityClass() {
		return UserRole.class;
	}

	/**
	 * 根据用户ID和角色ID获取用户角色关系信息
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public UserRole getByUserIdAndRoleId(Long userId, Long roleId)  {
	    Map<String, Long> params = new HashMap<String, Long>();
	    params.put("userId", userId);
	    params.put("roleId", roleId);
	    return getUnique("getByUserIdAndRoleId", params);
	}
}
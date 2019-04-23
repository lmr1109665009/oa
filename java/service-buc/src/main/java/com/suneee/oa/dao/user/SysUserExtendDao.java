package com.suneee.oa.dao.user;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息SysUser扩展DAO类
 * @author xiongxianyun
 *
 */
@Repository
public class SysUserExtendDao extends UcpBaseDao<SysUser> {
	@Override
	public Class<SysUser> getEntityClass() {
		return SysUser.class;
	}
	
	/**
	 * 根据查询条件获取用户信息列表
	 * @param filter
	 * @return
	 */
	public List<SysUser> getAllUser(QueryFilter filter) {
		return getBySqlKey("getAllUser", filter);
	}
	
	public List<SysUser> getAllDelUser(QueryFilter filter) {
		return getBySqlKey("getAllDelUser", filter);
	}

	/**
	 * 根据用户ID和账号查询账号数量
	 * @param account 账号
	 * @param userId 用户ID
	 * @return
	 */
	public Integer getAccountCount(String account, Long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("account", account);
	    params.put("userId", userId);
	    return (Integer)getOne("getAccountCount", params);
	}

	/**
	 * 根据用户ID和邮箱查询邮箱数量
	 * @param email 邮箱
	 * @param userId 用户ID
	 * @return
	 */
	public Integer getEmailCount(String email, Long userId) {
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("email", email);
	    params.put("userId", userId);
	    return (Integer)getOne("getEmailCount", params);
	}

	/**
	 * 根据用户ID和手机号查询手机号数量
	 * @param mobile 手机号
	 * @param userId 用户ID
	 * @return
	 */
	public Integer getMobileCount(String mobile, Long userId)
	{
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("mobile", mobile);
	    params.put("userId", userId);
	    return (Integer)getOne("getMobileCount", params);
	}
	
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUser(SysUser user){
		user.setUpdateBy(ContextUtil.getCurrentUserId());
		user.setUpdatetime(new Date());
		return this.update("updateUser", user);
	}

	public List<SysUser> getUserListByIds(Long[] ids){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", ids);
		return this.getBySqlKey("getUserListByIds", params);
	}
}
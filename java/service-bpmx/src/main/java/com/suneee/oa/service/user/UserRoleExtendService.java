package com.suneee.oa.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.UserRole;
import com.suneee.platform.service.system.UserRoleService;
import com.suneee.oa.dao.user.UserRoleExtendDao;
import com.suneee.ucp.base.service.UcpBaseService;

/**
 * 用户角色关系UserRole扩展Service类
 * @author xiongxianyun
 *
 */
@Service
public class UserRoleExtendService extends UcpBaseService<UserRole> {

	@Resource
	private UserRoleExtendDao userRoleExtendDao;

	@Resource
	private UserRoleService userRoleService;

	@Override
	protected IEntityDao<UserRole, Long> getEntityDao()  {
		return userRoleExtendDao;
	}

	/**
	 * 保存用户角色关系
	 * @param userId
	 * @param roleIds
	 */
	public void saveUserRole(Long userId, Long[] roleIds) {
		if ((userId == null) || (roleIds == null) || (roleIds.length == 0)) {
			return;
		}

		// 根据用户ID获取用户角色关系
	    List<UserRole> userRoleList = this.userRoleService.getByUserId(userId);
	    // 已经存在的用户角色关系ID集合
	    List<Long> userRoleIds = new ArrayList<Long>();
	    UserRole userRole = null;
	    for (Long roleId : roleIds) {
	    	// 根据用户ID和角色ID获取用户角色关系
	    	userRole = this.getByUserIdAndRoleId(userId, roleId);
	    	// 用户关系角色不存在则新建用户角色关系
	    	if (userRole == null) {
		        this.add(userId, roleId);
	    	} 
	    	// 用户角色关系存在则加入用户角色关系ID集合
	    	else {
	    		userRoleIds.add(userRole.getUserRoleId());
	    	}

	    }

	    // 移除在用户角色关系ID集合中不存在的用户角色关系
	    for (UserRole userRoleDb : userRoleList) {
	    	if (!(userRoleIds.contains(userRoleDb.getUserRoleId()))){
	    		this.delById(userRoleDb.getUserRoleId());
	    	}
	    }
	}
	
	/** 
	 * 保存用户角色关系
	 * @param roleId
	 * @param userIds
	 */
	public void save(Long roleId, Long[] userIds){
		if ((roleId == null) || (userIds == null) || (userIds.length == 0)) {
			return;
		}
		
		UserRole userRole = null;
		for(Long userId : userIds){
			// 根据用户ID和角色ID查询用户角色关系信息
			userRole = this.getByUserIdAndRoleId(userId, roleId);
			// 当用户角色关系不存在时，则保存用户角色关系
			if(userRole == null){
				this.add(userId, roleId);
			}
		}
	}
	
	/** 
	 * 新增用户角色关系
	 * @param userId 用户ID
	 * @param roleId 角色ID
	 */
	public void add(Long userId, Long roleId){
		if(userId == null || roleId == null){
			return;
		}
		UserRole userRole = new UserRole();
		userRole.setUserRoleId(UniqueIdUtil.genId());
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		this.add(userRole);
	}

	/**
	 * @param userId
	 * @param roleId 
	 * @return
	 */
	public UserRole getByUserIdAndRoleId(Long userId, Long roleId) {
		return this.userRoleExtendDao.getByUserIdAndRoleId(userId, roleId);
	}
	
	/** 
	 * 根据角色ID查询用户角色信息
	 * @param roleId
	 * @return
	 */
	public List<UserRole> getByRoleId(Long roleId){
		return userRoleService.getUserRoleByRoleId(roleId);
	}
}
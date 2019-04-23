package com.suneee.platform.service.system;

import com.suneee.platform.model.system.SysUser;

import java.util.List;

public interface IUserUnderService {
	
	/**
	 * 根据用户ID获取下属。
	 * @param userId
	 * @return
	 */
	List<SysUser> getMyUnderUser(Long userId);

}

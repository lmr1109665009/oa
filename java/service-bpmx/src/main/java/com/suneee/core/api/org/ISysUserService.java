package com.suneee.core.api.org;

import com.suneee.core.api.org.model.ISysUser;

import java.util.List;

public interface ISysUserService {
	
	
	ISysUser getById(Long userId);
	
	ISysUser getByAccount(String account);
	
	List<ISysUser> getByGroup(Long groupId, String groupType);
}

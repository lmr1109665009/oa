package com.suneee.platform.service.system.impl.curuser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.model.CurrentUser;
import com.suneee.platform.model.system.UserRole;
import com.suneee.platform.service.system.ICurUserService;
import com.suneee.platform.service.system.UserRoleService;
import com.suneee.core.model.CurrentUser;
import com.suneee.platform.model.system.UserRole;
import com.suneee.platform.service.system.ICurUserService;
import com.suneee.platform.service.system.UserRoleService;

public class RoleUserService implements ICurUserService {

	@Resource
    UserRoleService userRoleService;
	
	@Override
	public List<Long> getByCurUser(CurrentUser currentUser) {
		List<UserRole> list= userRoleService.getByUserId(currentUser.getUserId());
		List<Long> roleList=new ArrayList<Long>();
		for(UserRole userRole:list){
			roleList.add(userRole.getRoleId());
		}
		return roleList;
	}

	@Override
	public String getKey() {
		return "role";
	}

	@Override
	public String getTitle() {
		return "角色授权";
	}


}

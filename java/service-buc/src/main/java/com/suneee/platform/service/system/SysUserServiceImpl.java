package com.suneee.platform.service.system;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.api.org.ISysUserService;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;

import com.suneee.core.api.org.ISysUserService;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.system.SysUser;

@Service
public class SysUserServiceImpl implements ISysUserService {
	
	@Resource
    SysUserDao sysUserDao;

	@Override
	public ISysUser getById(Long userId) {
		return sysUserDao.getById(userId);
	}

	@Override
	public ISysUser getByAccount(String account) {
		return sysUserDao.getByAccount(account);
	}

	@Override
	public List<ISysUser> getByGroup(Long groupId, String groupType) {
		List<SysUser> users = new ArrayList<SysUser>();
		if ("role".equals(groupType)) {
			users = sysUserDao.getByRoleId(groupId);
		} else if ("org".equals(groupType)) {
			users = sysUserDao.getByOrgId(groupId);
		} else if ("pos".equals(groupType)) {
			users = sysUserDao.getByPosId(groupId);
		}
		List<ISysUser> iusers = new ArrayList<ISysUser>();
		for (SysUser user : users) {
			iusers.add(user);
		}
		return iusers;
	}

}

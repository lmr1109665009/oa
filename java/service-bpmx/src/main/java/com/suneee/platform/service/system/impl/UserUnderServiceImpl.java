package com.suneee.platform.service.system.impl;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.IUserUnderService;
import org.springframework.stereotype.Service;

import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.IUserUnderService;

@Service
public class UserUnderServiceImpl implements IUserUnderService {

	@Resource
	private SysUserDao sysUserDao;
	
	@Override
	public List<SysUser> getMyUnderUser(Long userId) {
		return sysUserDao.getUnderUserByUserId(userId);
	}

}

package com.suneee.platform.service.ldap;

import java.util.Date;
import java.util.List;

import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SysUser;


public interface SysUserSyncServiceMBean {
	
	public Date getLastSyncTime();
	
	public Long getLastSyncTakeTime() ;

	public List<SysUser> getNewFromLdapUserList();

	public List<SysUser> getDeleteLocalUserList() ;

	public List<SysUser> getUpdateLocalUserList();
	
	void reset();
}

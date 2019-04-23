package com.suneee.weixin.api;

import com.suneee.platform.model.system.SysUser;

import java.util.List;

public interface IUserService {
	/**
	 * 新增用户
	 * @param org
	 * @throws Exception 
	 */
	public void create(SysUser sysUser);
	/**
	 * 更新用户
	 * @param org
	 * @throws Exception 
	 */
	public void update(SysUser sysUser);
	
	/**
	 * 删除用户
	 * @param orgId
	 */
	public void delete(String userId);
	/**
	 * 批量删除用户
	 * @param userIds
	 * @throws Exception
	 */
	void deleteAll(String userIds) ;
	/**
	 * 批量同步
	 * 已经存在、尚未绑定微信号的忽略
	 * @param sysUserList
	 * @throws Exception
	 */
	void addAll(List<SysUser> sysUserList);
}

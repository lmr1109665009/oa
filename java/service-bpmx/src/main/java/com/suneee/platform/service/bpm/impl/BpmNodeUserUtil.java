package com.suneee.platform.service.bpm.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.system.SysUser;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.system.SysUser;

public class BpmNodeUserUtil {
	
	/**
	 * 根据用户返回执行人员。
	 * @param sysUsers
	 * @param extraceUser
	 * @return
	 */
	public static Set<TaskExecutor> getExcutorsByUsers(List<SysUser> sysUsers, int extraceUser){
		Set<TaskExecutor> uIdSet = new LinkedHashSet<TaskExecutor>();
		if(BeanUtils.isEmpty(sysUsers))
			return uIdSet;
		if(sysUsers.size()==0)
			return uIdSet;
		if(sysUsers.size()==1){
			SysUser sysUser=sysUsers.get(0);
//			uIdSet.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(),sysUser.getFullname()));
			uIdSet.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(), ContextSupportUtil.getUsername(sysUser)));
			return uIdSet;
		}
		//用户组
		if(extraceUser==TaskExecutor.EXACT_USER_GROUP){
			String userIds="";
			String userNames="";
			for(int i=0;i<sysUsers.size();i++){
				if(i>0){
					userIds+=",";
					userNames+=",";
				}
				userIds+=sysUsers.get(i).getUserId();
//				userNames+=sysUsers.get(i).getFullname();
				SysUser sysUser1 = sysUsers.get(i);
				userNames+=ContextSupportUtil.getUsername(sysUser1);
			}
			TaskExecutor executor=TaskExecutor.getTaskUserGroup(userIds, userNames);
			uIdSet.add(executor);
		}
		else{
			for (SysUser sysUser : sysUsers) {
//				uIdSet.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(),sysUser.getFullname()));
				uIdSet.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(),ContextSupportUtil.getUsername(sysUser)));
			}
		}
		return uIdSet;
	}
	
	/**
	 * 根据taskExecutor 获取执行人员。
	 * @param taskExecutor
	 * @return
	 */
	public static List<SysUser> getUserListByExecutor(TaskExecutor taskExecutor){
		SysUserDao sysUserDao=(SysUserDao) AppUtil.getBean(SysUserDao.class);
		List<SysUser> userList=null;
		if(TaskExecutor.USER_TYPE_ROLE.equals(taskExecutor.getType())){
			userList= sysUserDao.getByRoleId(Long.parseLong(taskExecutor.getExecuteId()));
		}
		else if(TaskExecutor.USER_TYPE_ORG.equals(taskExecutor.getType())){
			userList= sysUserDao.getByOrgId(Long.parseLong(taskExecutor.getExecuteId()));
		}
		else if(TaskExecutor.USER_TYPE_POS.equals(taskExecutor.getType())){
			userList= sysUserDao.getByPosId(Long.parseLong(taskExecutor.getExecuteId()));
		}
		
		return userList;
	}
	
	

}

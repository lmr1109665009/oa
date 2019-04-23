package com.suneee.platform.service.bpm.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.StringUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserRole;
import com.suneee.platform.service.bpm.CalcVars;
import com.suneee.platform.service.bpm.IBpmNodeUserCalculation;
import com.suneee.platform.service.system.UserRoleService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.system.SysUser;

/**
 * 根据节点用户设置为“角色”，计算执行人员。
 * 
 * @author Raise
 */
public class BpmNodeUserCalculationJob implements IBpmNodeUserCalculation {
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private SysUserDao sysUserDao;

	@Override
	public List<SysUser> getExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		List<SysUser> userList = new ArrayList<SysUser>();
		String jobIds = bpmNodeUser.getCmpIds();
		if (StringUtil.isEmpty(jobIds)) {
			return userList;
		}
		List<Long> list =  ServiceUtil.getListByStr(jobIds);
		userList=sysUserDao.getByJobIds(list);
		return userList;
	}

	@Override
	public String getTitle() {
		return "职务";
	}

	
	@Override
	public Set<TaskExecutor> getTaskExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		int extractUser=bpmNodeUser.getExtractUser();
		Set<TaskExecutor> userIdSet = new LinkedHashSet<TaskExecutor>();
		String uids=bpmNodeUser.getCmpIds();
		if(StringUtil.isEmpty(uids)){
			return userIdSet;
		}
		String jobIdStr = bpmNodeUser.getCmpIds();
		List<Long> list =  ServiceUtil.getListByStr(jobIdStr);
		List<SysUser> userList=sysUserDao.getByJobIds(list);
		
		//不抽取
		switch (extractUser) {
			case TaskExecutor.EXACT_NOEXACT:
				String[] jobIds = bpmNodeUser.getCmpIds().split("[,]");
				String[] jobNames = bpmNodeUser.getCmpNames().split("[,]");
				for (int i = 0; i < jobIds.length; i++) {
					TaskExecutor taskExecutor=TaskExecutor.getTaskJob(jobIds[i],jobNames[i]) ;
					userIdSet.add(taskExecutor);
				}
				break;
			//一次抽取	
			case TaskExecutor.EXACT_EXACT_USER:
				//List<UserRole> userList = userRoleService.getUserByRoleIds(bpmNodeUser.getCmpIds());
				for (SysUser user : userList) {
//					TaskExecutor taskExecutor=TaskExecutor.getTaskUser(user.getUserId().toString(),user.getFullname()) ;
					TaskExecutor taskExecutor=TaskExecutor.getTaskUser(user.getUserId().toString(), ContextSupportUtil.getUsername(user)) ;
					userIdSet.add(taskExecutor);
				}
				break;
			//二次抽取	
			case TaskExecutor.EXACT_EXACT_SECOND:
				String[] aryRoleIds = bpmNodeUser.getCmpIds().split("[,]");
				String[] aryRoleNames = bpmNodeUser.getCmpNames().split("[,]");
				for (int i = 0; i < aryRoleIds.length; i++) {
					TaskExecutor taskExecutor=TaskExecutor.getTaskJob(aryRoleIds[i],aryRoleNames[i]) ;
					taskExecutor.setExactType(extractUser);
					userIdSet.add(taskExecutor);
				}
				break;
			
		}
		return userIdSet;
	}

	@Override
	public boolean supportMockModel() {
		
		return true;
	}

	@Override
	public List<PreViewModel> getMockModel(BpmNodeUser bpmNodeUser) {
		
		return null;
	}
	
	@Override
	public boolean supportPreView() {
		return true;
	}

	

}

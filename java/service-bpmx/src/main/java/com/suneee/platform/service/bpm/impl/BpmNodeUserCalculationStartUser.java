package com.suneee.platform.service.bpm.impl;

import com.suneee.core.model.TaskExecutor;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.CalcVars;
import com.suneee.platform.service.bpm.IBpmNodeUserCalculation;
import com.suneee.platform.service.system.SysUserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 根据节点用户设置为“发起人”，计算执行人员。
 * 
 * @author Raise
 */
public class BpmNodeUserCalculationStartUser implements IBpmNodeUserCalculation {

	@Resource
	private SysUserService sysUserService;

	@Override
	public List<SysUser> getExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		Long startUserId = vars.getStartUserId();
		if(startUserId==null || startUserId.intValue()==0) return new ArrayList<SysUser>();
		
		List<SysUser> sysUsers = new ArrayList<SysUser>();
		SysUser sysUser = sysUserService.getById(startUserId);
		sysUsers.add(sysUser);
		return sysUsers;
	}

	@Override
	public String getTitle() {
		return "发起人";
	}

	@Override
	public Set<TaskExecutor> getTaskExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		Set<TaskExecutor> uIdSet = new LinkedHashSet<TaskExecutor>();
		List<SysUser> sysUsers = this.getExecutor(bpmNodeUser, vars);
		for (SysUser sysUser : sysUsers) {
//			uIdSet.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(),sysUser.getFullname()));
			uIdSet.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(), ContextSupportUtil.getUsername(sysUser)));
		}
		return uIdSet;
	}

	@Override
	public boolean supportMockModel() {
		return true;
	}

	@Override
	public List<PreViewModel> getMockModel(BpmNodeUser bpmNodeUser) {
		List< PreViewModel> list=new ArrayList<PreViewModel>();
		PreViewModel preViewModel=new PreViewModel();
		preViewModel.setType(PreViewModel.START_USER);
		
		list.add(preViewModel);
		
		return list;
	}

	@Override
	public boolean supportPreView() {
		return true;
	}

	
	

}

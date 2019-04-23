package com.suneee.platform.service.bpm.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SysUserOrg;
import com.suneee.platform.service.bpm.CalcVars;
import com.suneee.platform.service.bpm.IBpmNodeUserCalculation;
import com.suneee.platform.service.system.SysUserOrgService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SysUserOrg;

/**
 * 根据节点用户设置为“组织”，计算执行人员。
 * 
 * @author Raise
 */
public class BpmNodeUserCalculationOrg implements IBpmNodeUserCalculation {
	@Resource
	private SysUserDao sysUserDao;
	@Resource
	private SysUserOrgService sysUserOrgService;

	@Override
	public List<SysUser> getExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		List<SysUser> users = new ArrayList<SysUser>();
		String uids = bpmNodeUser.getCmpIds();
		if (StringUtil.isEmpty(uids)) {
			return users;
		}
		List<Long> list =  ServiceUtil.getListByStr(uids);
		List<SysUser> userList=sysUserDao.getByOrgIds(list);
		return userList;
	}

	@Override
	public String getTitle() {
		//组织
		return "组织";
	}
	@Override
	public Set<TaskExecutor> getTaskExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		int extractUser=bpmNodeUser.getExtractUser();
		
		Set<TaskExecutor> userIdSet = new LinkedHashSet<TaskExecutor>();
		
		String uids=bpmNodeUser.getCmpIds();
		if(StringUtil.isEmpty(uids)){
			return userIdSet;
		}
		switch (extractUser) {
			//不抽取的情况
			case TaskExecutor.EXACT_NOEXACT:
				String[] orgIds = bpmNodeUser.getCmpIds().split("[,]");
				String[] orgNames = bpmNodeUser.getCmpNames().split("[,]");
				for (int i = 0; i < orgIds.length; i++) {
					TaskExecutor taskExecutor=TaskExecutor.getTaskOrg(orgIds[i].toString(),orgNames[i]) ;
					
					userIdSet.add(taskExecutor);
				}
				break;
			//抽取用户
			case TaskExecutor.EXACT_EXACT_USER:
				List<SysUserOrg> userOrgList= sysUserOrgService.getUserByOrgIds(bpmNodeUser.getCmpIds());
				for (SysUserOrg sysUserOrg : userOrgList) {
					TaskExecutor taskExecutor=TaskExecutor.getTaskUser(sysUserOrg.getUserId().toString(),sysUserOrg.getUserName()) ;
					userIdSet.add(taskExecutor);
				}
				break;
			//二次抽取
			case TaskExecutor.EXACT_EXACT_SECOND:
				String[] aryOrgIds = bpmNodeUser.getCmpIds().split("[,]");
				String[] aryOrgNames = bpmNodeUser.getCmpNames().split("[,]");
				for (int i = 0; i < aryOrgIds.length; i++) {
					TaskExecutor taskExecutor=TaskExecutor.getTaskOrg(aryOrgIds[i].toString(),aryOrgNames[i]) ;
					taskExecutor.setExactType(TaskExecutor.EXACT_EXACT_SECOND);
					userIdSet.add(taskExecutor);
				}
				break;
		}
		return userIdSet;
	}

	@Override
	public boolean supportMockModel() {
		return false;
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

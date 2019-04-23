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
import com.suneee.platform.service.bpm.CalcVars;
import com.suneee.platform.service.bpm.IBpmNodeUserCalculation;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.system.SysUser;

/**
 * 根据节点用户设置为“组织负责人”，计算执行人员。
 * <pre>
 * 测试OK RAY
 * </pre>
 * @author Raise
 */
public class BpmNodeUserCalculationOrgCharge implements IBpmNodeUserCalculation {
	@Resource
	private SysUserDao sysUserDao;

	@Override
	public List<SysUser> getExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		List<SysUser> users = new ArrayList<SysUser>();

		String uids = bpmNodeUser.getCmpIds();
		if (StringUtil.isEmpty(uids)) {
			return users;
		}
		List<Long> list =  ServiceUtil.getListByStr(uids);
		List<SysUser> userList= sysUserDao.getMgrByOrgIds(list);
		return userList;
	}

	@Override
	public String getTitle() {
		//return "组织负责人";
		return "组织负责人";
	}

	@Override
	public Set<TaskExecutor> getTaskExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		int extraceUser=bpmNodeUser.getExtractUser();
		List<SysUser> sysUsers = this.getExecutor(bpmNodeUser, vars);
		Set<TaskExecutor> uIdSet=BpmNodeUserUtil.getExcutorsByUsers(sysUsers, extraceUser);
		return uIdSet;
	}

	@Override
	public boolean supportMockModel() {
		return false;
	}

	@Override
	public List< PreViewModel> getMockModel(BpmNodeUser bpmNodeUser) {
		
		return null;
	}
	
	@Override
	public boolean supportPreView() {
		return true;
	}

	


}

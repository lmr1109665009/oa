package com.suneee.platform.service.bpm.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.CalcVars;
import com.suneee.platform.service.bpm.IBpmNodeUserCalculation;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.CalcVars;

/**
 * 根据节点用户设置为“与其他节点相同执行人”，计算执行人员部门。
 * 
 * @author Raise
 */
public class BpmNodeUserCalculationSameNodeDepartment implements IBpmNodeUserCalculation {
	@Resource
	private SysUserService sysUserService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private SysUserDao sysUserDao;

	@Override
	public List<SysUser> getExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		String actInstId = vars.getActInstId();
		List<SysUser> users = new ArrayList<SysUser>();
		String nodeId = bpmNodeUser.getCmpIds();
		TaskOpinion taskOpinion = taskOpinionService.getLatestTaskOpinion(new Long( actInstId), nodeId);
		if (taskOpinion != null) {
			if(taskOpinion.getExeUserId()!=null){
				SysUser user = sysUserService.getById(taskOpinion.getExeUserId());
				users.add(user);
			}
		}
		return users;
	}

	@Override
	public String getTitle() {
		return "与已执行节点执行人相同部门";
	}

	@Override
	public Set<TaskExecutor> getTaskExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		Set<TaskExecutor> uIdSet = new LinkedHashSet<TaskExecutor>();
		List<SysUser> sysUsers = this.getExecutor(bpmNodeUser, vars);
		int extractUser=bpmNodeUser.getExtractUser();
		if (BeanUtils.isNotEmpty(sysUsers)) {
			for (SysUser sysUser : sysUsers) {
				if (BeanUtils.isEmpty(sysUser))
					continue;
				SysOrg sysOrg = sysOrgService.getDefaultOrgByUserId(sysUser.getUserId());
				if (BeanUtils.isEmpty(sysOrg))
					continue;
				Long orgId = sysOrg.getOrgId();
				switch (extractUser) {
				// 不抽取
				case TaskExecutor.EXACT_NOEXACT:
					TaskExecutor executor = TaskExecutor.getTaskOrg(orgId.toString(), sysOrg.getOrgName());
					uIdSet.add(executor);
					break;
				// 抽取
				case TaskExecutor.EXACT_EXACT_USER:
					List<SysUser> list = sysUserDao.getByOrgId(orgId);
					for (SysUser sysUsertemp : list) {
						TaskExecutor taskExcutor = TaskExecutor.getTaskUser(sysUsertemp.getUserId().toString(), sysUsertemp.getFullname());
						uIdSet.add(taskExcutor);
					}
					break;
				// 二次抽取
				case TaskExecutor.EXACT_EXACT_SECOND:
					TaskExecutor taskexecutor = TaskExecutor.getTaskOrg(orgId.toString(), sysOrg.getOrgName());
					taskexecutor.setExactType(extractUser);
					uIdSet.add(taskexecutor);
					break;
				}

			}
		}
		
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
		return false;
	}

}

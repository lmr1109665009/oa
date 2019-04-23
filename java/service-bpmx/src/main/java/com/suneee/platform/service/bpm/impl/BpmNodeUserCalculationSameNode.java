package com.suneee.platform.service.bpm.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.dao.bpm.HistoryActivityDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.system.SysUser;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;

import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.bpm.HistoryActivityDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.CalcVars;
import com.suneee.platform.service.bpm.IBpmNodeUserCalculation;
import com.suneee.platform.service.system.SysUserService;

/**
 * 根据节点用户设置为“与其他节点相同执行人”，计算执行人员。
 * 
 * @author Raise
 */
public class BpmNodeUserCalculationSameNode implements IBpmNodeUserCalculation {
	@Resource
	SysUserService sysUserService;
	@Resource
    HistoryActivityDao historyActivityDao;

	@Override
	public List<SysUser> getExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		
		List<SysUser> users = new ArrayList<SysUser>();
		String nodeId = bpmNodeUser.getCmpIds();
		
		String executionId= (String) vars.getVariable(BpmConst.EXECUTION_ID_);
		
		if(StringUtil.isEmpty(executionId))
			return users;
		
		List<HistoricActivityInstanceEntity> hisList=  historyActivityDao.getByExecutionId(executionId, nodeId);
		
		if(BeanUtils.isEmpty(hisList)) {//如果找不到，然后对流程实例到找一次
			String actInstId= vars.getActInstId();
			if(StringUtil.isEmpty(actInstId))
				return users;
			hisList=  historyActivityDao.getByActInstId(actInstId, nodeId);
		}
		
		if(BeanUtils.isEmpty(hisList)) 
			return users;
		HistoricActivityInstanceEntity hiEnt = hisList.get(0);
		
		if(StringUtil.isNotEmpty(hiEnt.getAssignee())){
			SysUser user = sysUserService.getById(new Long(hiEnt.getAssignee()));
			users.add(user);
		}
		
		
		return users;
	}

	@Override
	public String getTitle() {
		//return "与已执行节点相同执行人";
		return "与已执行节点相同执行人";
	}

	@Override
	public Set<TaskExecutor> getTaskExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		Set<TaskExecutor> uIdSet = new LinkedHashSet<TaskExecutor>();
		List<SysUser> sysUsers = this.getExecutor(bpmNodeUser, vars);
		if(BeanUtils.isNotEmpty(sysUsers)){
			for (SysUser sysUser : sysUsers) {
				if(BeanUtils.isNotEmpty(sysUser)){
					uIdSet.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(), ContextSupportUtil.getUsername(sysUser)));
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

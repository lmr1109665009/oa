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
import com.suneee.platform.service.bpm.CalcVars;
import com.suneee.platform.service.bpm.IBpmNodeUserCalculation;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.system.SysUser;

/**
 * 根据节点用户设置为“岗位”，计算执行人员。
 * 
 * @author Raise
 */
public class BpmNodeUserCalculationPosition implements IBpmNodeUserCalculation {
	@Resource
	private SysUserDao sysUserDao;
	
	@Override
	public List<SysUser> getExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		List<SysUser> users = new ArrayList<SysUser>();
		String uids = bpmNodeUser.getCmpIds();
		if (StringUtil.isEmpty(uids)) {
			return users;
		}
		return getByCmpIds(uids);
	}
	
	private List<SysUser> getByCmpIds(String cmpIds){
		List<Long> list=ServiceUtil.getListByStr(cmpIds);
		List<SysUser> sysUsers = sysUserDao.getByPos(list);
		
		List<SysUser> sysUserList = new ArrayList<SysUser>();
		// sysUserList去重
		for (SysUser user : sysUsers) {
			if (!sysUserList.contains(user)) {
				sysUserList.add(user);
			}
		}
		return sysUserList;
	}

	@Override
	public String getTitle() {
		//return "岗位";
		return "岗位";
	}

	@Override
	public Set<TaskExecutor> getTaskExecutor(BpmNodeUser bpmNodeUser, CalcVars vars) {
		int extractUser=bpmNodeUser.getExtractUser().intValue();
		
		Set<TaskExecutor> userIdSet = new LinkedHashSet<TaskExecutor>();
		String uids=bpmNodeUser.getCmpIds();
		if(StringUtil.isEmpty(uids)){
			return userIdSet;
		}
		switch (extractUser) {
			case TaskExecutor.EXACT_NOEXACT:
				String[] aryId = bpmNodeUser.getCmpIds().split("[,]");
				String[] aryName = bpmNodeUser.getCmpNames().split("[,]");
				for (int i = 0; i < aryId.length; i++) {
					TaskExecutor taskExecutor=TaskExecutor.getTaskPos(aryId[i].toString(),aryName[i]) ;
					userIdSet.add(taskExecutor);
				}
				break;
			case TaskExecutor.EXACT_EXACT_USER:
				List<SysUser> userList= getByCmpIds(uids);
				for (SysUser user : userList) {
//					TaskExecutor taskExecutor=TaskExecutor.getTaskUser(user.getUserId().toString(),user.getFullname()) ;
					TaskExecutor taskExecutor=TaskExecutor.getTaskUser(user.getUserId().toString(), ContextSupportUtil.getUsername(user)) ;
					userIdSet.add(taskExecutor);
				}
				break;
			case TaskExecutor.EXACT_EXACT_SECOND:
				String[] aryPosId = bpmNodeUser.getCmpIds().split("[,]");
				String[] aryPosName = bpmNodeUser.getCmpNames().split("[,]");
				for (int i = 0; i < aryPosId.length; i++) {
					TaskExecutor taskExecutor=TaskExecutor.getTaskPos(aryPosId[i].toString(),aryPosName[i]) ;
					taskExecutor.setExactType(extractUser);
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
	public List< PreViewModel> getMockModel(BpmNodeUser bpmNodeUser) {
	
		return null;
	}
	
	@Override
	public boolean supportPreView() {
		return true;
	}

	


}

package com.suneee.platform.service.bpm.skipimpl;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.setting.ISkipCondition;
import com.suneee.platform.model.system.SysUser;
import org.activiti.engine.task.Task;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.setting.ISkipCondition;
import com.suneee.platform.model.system.SysUser;

/**
 * 相同执行人跳过。
 * @author ray
 */
public class SameUserSkipCondition implements ISkipCondition {

	@Override
	public boolean canSkip(Task task) {
		SysUser sysUser=(SysUser) ContextUtil.getCurrentUser();
		String assignee=task.getAssignee();
		String curUserId=sysUser.getUserId().toString();
		if(curUserId.equals(assignee)){
			return true;
		}
		return false;
	}

	@Override
	public String getTitle() {
		return "相同执行人跳过";
	}

	@Override
	public ISysUser getExecutor() {
		ISysUser user=ContextUtil.getCurrentUser();
		return user;
	}

}

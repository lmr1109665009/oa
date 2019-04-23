package com.suneee.platform.service.bpm.skipimpl;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.api.org.ISysUserService;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.setting.ISkipCondition;
import com.suneee.platform.model.bpm.TaskOpinion;
import org.activiti.engine.task.Task;

import com.suneee.core.api.org.ISysUserService;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.setting.ISkipCondition;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.thread.TaskThreadService;


/**
 * 如果之前进行过审批人和当前任务执行人相同，那么可以跳过。
 * @author Administrator
 *
 */
public class ApproveUserSkipCondition implements ISkipCondition {
	
	@Resource
	TaskOpinionService taskOpinionService;
	@Resource
    ISysUserService sysUserServiceImpl;

	@Override
	public boolean canSkip(Task task) {
		String assignee=task.getAssignee();
		
		ISysUser user= sysUserServiceImpl.getById(new Long(assignee));
		
		ProcessCmd cmd=TaskThreadService.getProcessCmd();
		cmd.addTransientVar("appproveUser", user);
		
		//获取之前的审批人，从审批历史中获取。
		List<TaskOpinion> list= taskOpinionService.getByActInstId(task.getProcessInstanceId());
		for(TaskOpinion opinion:list){
			Short status=opinion.getCheckStatus();
			if(status.equals(TaskOpinion.STATUS_AGREE)
					|| status.equals(TaskOpinion.STATUS_ABANDON)
					|| status.equals(TaskOpinion.STATUS_REFUSE)){
				if(assignee.equals(opinion.getExeUserId().toString())){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public ISysUser getExecutor() {
		ProcessCmd cmd=TaskThreadService.getProcessCmd();
		ISysUser user =(ISysUser) cmd.getTransientVar("appproveUser");
		return user;
	}
	
	

	@Override
	public String getTitle() {
		return "之前审批过后跳过";
	}





	

}

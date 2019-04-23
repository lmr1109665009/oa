package com.suneee.platform.service.bpm.skipimpl;

import com.suneee.core.api.org.ISysUserService;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.setting.ISkipCondition;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.thread.TaskThreadService;
import com.suneee.core.api.org.ISysUserService;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.setting.ISkipCondition;
import com.suneee.core.util.StringUtil;
import org.activiti.engine.task.Task;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 如果之前进行过审批人和当前任务执行人相同，那么可以跳过。
 * @author Administrator
 *
 */
public class IsEmptyUserSkipCondition implements ISkipCondition {
	
	@Resource
	TaskOpinionService taskOpinionService;
	@Resource
    ISysUserService sysUserServiceImpl;

	@Override
	public boolean canSkip(Task task) {
		//获取流程变量是否标记了执行人为空跳过。
		ProcessCmd cmd = TaskThreadService.getProcessCmd();
		Map<String, Object> var = cmd.getVariables();
		//系统原有核心代码，解决同步里某个节点选不到人时会在共用对象processCmd.variable添加taskUserIsNull标识，会影响其他节点判断
		//String taskUserIsNull = (String)var.get("taskUserIsNull");
		//修改后代码，增加节点ID作为字段前缀，避免各个节点使用同一个变量进行判断
		String taskUserIsNull = (String)var.get(task.getTaskDefinitionKey()+"_taskUserIsNull");
		if(StringUtil.isNotEmpty(taskUserIsNull) && taskUserIsNull.equals("Y")){
			return true;
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
		return "执行人为空跳过";
	}





	

}

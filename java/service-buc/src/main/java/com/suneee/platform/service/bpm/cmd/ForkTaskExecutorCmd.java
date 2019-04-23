package com.suneee.platform.service.bpm.cmd;

import java.util.ArrayList;
import java.util.List;

import com.suneee.core.model.ForkTaskReject;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.bpm.ExecutionStack;
import com.suneee.platform.model.bpm.TaskFork;
import com.suneee.platform.model.system.SysUser;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import com.suneee.core.model.ForkTaskReject;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.bpm.ExecutionStack;
import com.suneee.platform.model.bpm.TaskFork;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.ExecutionStackService;
import com.suneee.platform.service.system.SysUserService;

/**
 * 获取汇总节点前一个分发节点的任务处理人
 * @author heyifan
 *
 */
public class ForkTaskExecutorCmd implements Command<List<ForkTaskReject>>{
	
	private String taskId="";
	
	public ForkTaskExecutorCmd(String taskId){
		this.taskId=taskId;
	}
	
	@Override
	public List<ForkTaskReject> execute(CommandContext context) {
		List<ForkTaskReject> list = new ArrayList<ForkTaskReject>();
		BpmNodeSetService bpmNodeSetService= AppUtil.getBean(BpmNodeSetService.class);
		TaskEntity taskEntity=context.getTaskEntityManager().findTaskById(taskId);
		if(BeanUtils.isEmpty(taskEntity)) return list;
		ExecutionEntity executionEntity=taskEntity.getExecution();
		if(executionEntity==null) return list;
		String taskId = taskEntity.getId();
		String actDefId=taskEntity.getProcessDefinitionId();	
		String nodeId=taskEntity.getTaskDefinitionKey();
		String processInstanceId = taskEntity.getProcessInstanceId();

		//节点为汇总节点
		BpmNodeSet bpmNodeSet =bpmNodeSetService.getByActDefIdJoinTaskKey(actDefId, nodeId);
		if(BeanUtils.isEmpty(bpmNodeSet)){
			return list;
		}
		//获取汇总节点所对应的分发节点
		String forkNodeId = bpmNodeSet.getNodeId();
		String forkNodeName = bpmNodeSet.getNodeName();
		if(StringUtil.isEmpty(forkNodeId)){
			return list;
		}
		ExecutionStackService executionStackService = AppUtil.getBean(ExecutionStackService.class);
		TaskService taskService = AppUtil.getBean(TaskService.class);
		String taskToken = (String)taskService.getVariableLocal(taskId, TaskFork.TAKEN_VAR_NAME);
		List<ExecutionStack> forkExeStacks = executionStackService.getByActInstIdNodeIdLikeToken(processInstanceId, forkNodeId, taskToken);
		SysUserService sysUserService = AppUtil.getBean(SysUserService.class);
		for (ExecutionStack executionStack : forkExeStacks) {
			String assignees = executionStack.getAssignees();
			Long userId = Long.parseLong(assignees);
			SysUser user = sysUserService.getById(userId);
			String token = executionStack.getTaskToken();
			ForkTaskReject forkTaskReject = new ForkTaskReject(userId, user.getFullname(), token, forkNodeId, forkNodeName);
			if(!list.contains(forkTaskReject)){
				list.add(forkTaskReject);
			}
		}
		return list;
	}
}

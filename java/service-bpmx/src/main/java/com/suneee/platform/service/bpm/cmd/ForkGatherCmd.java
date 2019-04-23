package com.suneee.platform.service.bpm.cmd;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.bpm.BpmNodeSet;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.service.bpm.BpmNodeSetService;

/**
 * 获取是否为汇总节点
 * @author heyifan
 *
 */
public class ForkGatherCmd implements Command<Boolean>{
	
	private String taskId="";
	
	public ForkGatherCmd(String taskId){
		this.taskId=taskId;
	}
	
	@Override
	public Boolean execute(CommandContext context) {
		BpmNodeSetService bpmNodeSetService= AppUtil.getBean(BpmNodeSetService.class);
		TaskEntity taskEntity=context.getTaskEntityManager().findTaskById(taskId);
		ExecutionEntity executionEntity=taskEntity.getExecution();
		if(executionEntity==null) return false;
		String actDefId=taskEntity.getProcessDefinitionId();
		String nodeId=taskEntity.getTaskDefinitionKey();
		
		//节点为汇总节点
		BpmNodeSet bpmNodeSet =bpmNodeSetService.getByActDefIdJoinTaskKey(actDefId, nodeId);
		if(BeanUtils.isEmpty(bpmNodeSet)){
			return false;
		}
		return true;
	}
}

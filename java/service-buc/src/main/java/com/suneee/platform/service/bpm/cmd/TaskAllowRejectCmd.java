package com.suneee.platform.service.bpm.cmd;

import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * 流程任务是否允许退回。
 * <pre>
 * 1.当前节点为分发节点不允许退回。
 * 2.当前节点为汇总节点不允许退回。
 * 3.如果当前节点为流程定义的第一个节点并且不是外部子流程实例不允许退回。
 * 4.前面节点不是userTask或者exclusiveGateway不允许退回。
 * </pre>
 * @author ray
 *
 */
public class TaskAllowRejectCmd implements Command<Boolean>{
	
	private String taskId="";
	
	public TaskAllowRejectCmd(String taskId){
		this.taskId=taskId;
	}
	
	private ExecutionEntity getSuperExecutionEnt(ExecutionEntity executionEntity){
		ExecutionEntity superEnt=executionEntity.getSuperExecution();
		while(!superEnt.isScope()){
			superEnt=superEnt.getParent();
		}
		return superEnt;
	}

	@Override
	public Boolean execute(CommandContext context) {
		
		BpmNodeSetService bpmNodeSetService= AppUtil.getBean(BpmNodeSetService.class);
		
		TaskEntity taskEnt=context.getTaskEntityManager().findTaskById(taskId);
		
		//修复BUG：如果是加签任务就没有
		ExecutionEntity executionEntity=taskEnt.getExecution();
		if(executionEntity==null) return false;
		String actDefId=taskEnt.getProcessDefinitionId();
		String nodeId=taskEnt.getTaskDefinitionKey();
		
		
		//获取当前节点的配置，判断是否是分发节点，是则不能有退回功能
		/*BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId,"");
		if (BeanUtils.isNotEmpty(bpmNodeSet)) {
			if (BeanUtils.isNotEmpty(bpmNodeSet.getNodeType()) && bpmNodeSet.getNodeType()==1) {
				return false;
			}
		}*/
		//节点为汇总节点
		//流程由退回上一个节点修改为自由退回功能，不存在汇总节点无法退回上一节点的问题，直接在就在退回节点列表进行过滤掉分发节点就行了，so这个代码需要注释掉
		/*bpmNodeSet =bpmNodeSetService.getByActDefIdJoinTaskKey(actDefId, nodeId);
		if(BeanUtils.isNotEmpty(bpmNodeSet)){
			return false;
		}*/
		
		boolean rtn= NodeCache.isFirstNode(actDefId, nodeId);
		//当前节点为流程第一个节点，并且流程实例非子流程实例。
		if(rtn ){
			if(StringUtil.isEmpty(executionEntity.getSuperExecutionId()) ){
				return false;
			}
			else{
				ExecutionEntity superEnt=getSuperExecutionEnt(executionEntity);
				nodeId=superEnt.getActivityId();
				actDefId=superEnt.getProcessDefinitionId();
			}
		}

		//原来的逻辑：上一个节点非任务节点就无法退回，现在逻辑：自由退回是不管上一个节点是不是非任务节点都是可以退回到前面的节点(除分发节点、会签节点)，so这个代码要注释掉
		/*Map<String, FlowNode> map = NodeCache.getByActDefId(actDefId);
		FlowNode flowNode = map.get(nodeId);
		List<FlowNode> preFlowNodeList = flowNode.getPreFlowNodes();
		for (FlowNode preNode : preFlowNodeList) {
			if(!"userTask".equals(preNode.getNodeType()) && !"exclusiveGateway".equals(preNode.getNodeType())){
				return false;
			}
		}*/

		return true;
	}

}

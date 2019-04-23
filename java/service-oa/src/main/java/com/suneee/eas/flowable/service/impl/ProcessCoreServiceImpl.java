package com.suneee.eas.flowable.service.impl;

import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.flowable.service.ProcessCoreService;
import com.suneee.eas.oa.service.car.CarApplyService;
import com.suneee.eas.oa.service.car.CarAuditService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @user 子华
 * @created 2018/9/26
 */
@Service
public class ProcessCoreServiceImpl implements ProcessCoreService {
    private static Logger log= LogManager.getLogger(ProcessCoreService.class);
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private CarApplyService carApplyService;
    @Autowired
    private CarAuditService carAuditService;

    /**
     * 驳回到发起人
     *  思路:
     *  1、获取当前任务所在的节点
     *  2、获取所在节点的流出方向
     *  3、记录所在节点的流出方向，并将所在节点的流出方向清空
     *  4、获取目标节点
     *  5、创建新的方向
     *  6、将新的方向set到所在节点的流出方向
     *  7、完成当前任务
     *  8、还原所在节点的流出方向
     * @param instanceId
     */
    @Override
    public Map<String, Object> jumpToFirst(String instanceId){

        HistoricTaskInstance firstNodeTask = findFirstNodeTask(instanceId);

        BpmnModel bpmnModel = getBpmnModel(instanceId, firstNodeTask);

        //获取最初节点
        FlowNode firstFlowNode = findFirstNode(instanceId, firstNodeTask);
        //获取当前节点
        FlowNode currentFlowNode = findCurrentNode(instanceId, bpmnModel);

        //记录当前节点的活动方向
        List<SequenceFlow> curSeqFlowList = new ArrayList<>();
        curSeqFlowList.addAll(currentFlowNode.getOutgoingFlows());

        //清理当前节点活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //建立新活动方向
        List<SequenceFlow> newSeqFlowList = new ArrayList<>();
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(IdGeneratorUtil.getNextId().toString());
        sequenceFlow.setSourceFlowElement(currentFlowNode);
        sequenceFlow.setTargetFlowElement(firstFlowNode);
        newSeqFlowList.add(sequenceFlow);
        currentFlowNode.setOutgoingFlows(newSeqFlowList);
        Map<String, Object> param = new HashMap<>();
        param.put("currentFlowNode", currentFlowNode);
        param.put("currentFlows", curSeqFlowList);
        return param;
    }

    //获取第一个节点
    @Override
    public FlowNode findFirstNode(String instanceId, HistoricTaskInstance firstNodeTask) {
        String processDefinitionId  = firstNodeTask.getProcessDefinitionId();
//        ProcessDefinitionEntity pde = (ProcessDefinitionEntity)repositoryService.createProcessDefinitionQuery()
//                                        .processDefinitionId(processDefinitionId)
//                                        .singleResult();
        BpmnModel bpmnModel = getBpmnModel(processDefinitionId, firstNodeTask);

        //获取历史activity实例
        List<HistoricActivityInstance> actHisList = historyService.createHistoricActivityInstanceQuery()
                .executionId(firstNodeTask.getExecutionId()).finished().list();
        HistoricActivityInstance hisActInstance = null;
        for(HistoricActivityInstance hai:actHisList){
            if(firstNodeTask.getId().equals(hai.getTaskId())){
                hisActInstance = hai;
            }
        }

        //获取最初节点的活动方向
        FlowNode firstFlowNode = (FlowNode)bpmnModel.getMainProcess().getFlowElement(hisActInstance.getActivityId());
        return firstFlowNode;
    }

    //找到当前活动节点
    @Override
    public FlowNode findCurrentNode(String instanceId, BpmnModel bpmnModel){
        Task currentTask = taskService.createTaskQuery().processInstanceId(instanceId).singleResult();

        //获取当前节点的活动方向
        Execution execution = runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();
        FlowNode currentFlowNode = (FlowNode)bpmnModel.getMainProcess().getFlowElement(execution.getActivityId());
        return currentFlowNode;
    }

    //找到模型定义
    @Override
    public BpmnModel getBpmnModel(String instanceId, HistoricTaskInstance firstNodeTask){
        String processDefinitionId  = firstNodeTask.getProcessDefinitionId();
        return repositoryService.getBpmnModel(processDefinitionId);
    }

    //找到第一个任务节点
    @Override
    public HistoricTaskInstance findFirstNodeTask(String instanceId){
        //获取该流程实例的历史任务列表
        List<HistoricTaskInstance> taskHList = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(instanceId)
                .orderByTaskCreateTime().asc().list();
        if(null == taskHList || taskHList.size() == 0){
            throw new RuntimeException("该流程还未有历史任务节点。");
        }

        //获取第一个历史任务
        return taskHList.get(0);
    }

    //恢复活动方向
    @Override
    public void jumpToCurrent(FlowNode currentFlowNode, List<SequenceFlow> curSeqFlowList){
        currentFlowNode.setOutgoingFlows(curSeqFlowList);
    }

}

package com.suneee.eas.flowable.service;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.List;
import java.util.Map;

/**
 * 流程核心功能
 * @user 子华
 * @created 2018/9/26
 */
public interface ProcessCoreService {
    /**
     * 驳回到发起人
     */
    public Map<String, Object> jumpToFirst(String instantceId);

    /**
     * 获得第一个节点.
     */
    public FlowNode findFirstNode(String instantceId, HistoricTaskInstance firstNodeTask);

    /**
     * 获取当前节点
     * @param instanceId
     * @param bpmnModel
     * @return
     */
    public FlowNode findCurrentNode(String instanceId, BpmnModel bpmnModel);

    /**
     * 获取流程定义
     * @param instanceId
     * @return
     */
    public BpmnModel getBpmnModel(String instanceId, HistoricTaskInstance firstNodeTask);

    /**
     * 获取第一条任务节点
     * @param instanceId
     * @return
     */
    public HistoricTaskInstance findFirstNodeTask(String instanceId);

    /**
     * 恢复活动方向
     * @param currentFlowNode
     * @param curSeqFlowList
     */
    public void jumpToCurrent(FlowNode currentFlowNode, List<SequenceFlow> curSeqFlowList);
}

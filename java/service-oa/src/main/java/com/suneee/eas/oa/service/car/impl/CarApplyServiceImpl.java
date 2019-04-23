package com.suneee.eas.oa.service.car.impl;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.flowable.service.ProcessCoreService;
import com.suneee.eas.oa.dao.car.CarApplyDao;
import com.suneee.eas.oa.model.car.*;
import com.suneee.eas.oa.service.car.*;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 车辆申请service
 * @user 子华
 * @created 2018/9/4
 */
@Service
public class CarApplyServiceImpl extends BaseServiceImpl<CarApply> implements CarApplyService {
    private CarApplyDao applyDao;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private CarArrangeService carArrangeService;

    @Autowired
    private CarTrendsService carTrendsService;

    @Autowired
    private ArrangeCarDriverService arrangeCarDriverService;

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private ProcessCoreService processCoreService;

    @Autowired
    private CarAuditHistoryService carAuditHistoryService;

    @Autowired
    public void setApplyDao(CarApplyDao applyDao) {
        this.applyDao = applyDao;
        setBaseDao(applyDao);
    }

    @Override
    public int save(CarApply model) {
        model.setStatus(CarApply.STATUS_AUDIT_AUDITING);
        model.setApplyId(IdGeneratorUtil.getNextId());
        model.setCreateTime(new Date());
        model.setCreateBy(ContextSupportUtil.getCurrentUserId());
        model.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
        model.setIsDelete(CarApply.DELETE_NO);
        Map<String,Object> variables=new HashMap<>();
        String userId=String.valueOf(ContextSupportUtil.getCurrentUserId());
        variables.put("form",model);
        variables.put("userId",userId);
        variables.put("startUserId",userId);
        ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(CarApply.BPMN_KEY_CAR,variables);
        model.setProcInstId(processInstance.getProcessInstanceId());
        int count=super.save(model);
        Task task =taskService.createTaskQuery().processInstanceId(model.getProcInstId()).taskCandidateOrAssigned(String.valueOf(ContextSupportUtil.getCurrentUserId())).singleResult();
        String auditorId = RequestUtil.getString(ContextUtil.getRequest(), "userId");
        Map<String,Object> values = new HashMap<>();
        values.put("auditorId", auditorId);
        taskService.complete(task.getId(), values);
        return count;
    }

    @Override
    public Pager<CarApply> getPageBySqlKey(QueryFilter filter) {
        filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
        return super.getPageBySqlKey(filter);
    }

    /**
     * 派车需要自动审批车辆申请
     * @param model
     * @return
     */
    @Override
    public int autoSave(CarApply model) {
       return super.save(model);
    }

    /**
     * 更新状态
     * @param applyId
     * @param status
     */
    @Override
    public void updateStatus(Long applyId, int status){
        applyDao.updateStatus(applyId, status);
    }

    /**
     * 根据流程实例ID来获取车辆申请信息
     * @param procInstId
     * @return
     */
    @Override
    public CarApply findByProcInstId(String procInstId) {
        return applyDao.findByProcInstId(procInstId);
    }

    @Override
    public Map<String, Object> getDetails(Long applyId){
        CarApply carApply = this.findById(applyId);
        CarArrange carArrange = carArrangeService.findByApplyId(applyId);

        CarTrends carTrends = carTrendsService.findByApplyId(applyId);
        List<CarTrends> carTrendsList = new ArrayList<>();
        if(null != carTrends){
            carTrendsList.add(carTrends);
        }
        List<CarAuditHistory> carAuditList = carAuditHistoryService.findListByApplyId(applyId);
        if(null == carAuditList){
            carAuditList = new ArrayList<>();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("carApply", carApply==null?"":carApply);
        result.put("carArrange", carArrange==null?"":carArrange);
        result.put("carTrends", carTrendsList);
        result.put("carAudit", carAuditList);
        return result;
    }

    /**
     * 撤回操作时执行删除工作
     * @param applyId
     */
    @Override
    public void deleteBatch(Long applyId){
        CarArrange carArrange = carArrangeService.findByApplyId(applyId);
        if(null != carArrange){
            //删除派车/司机/车辆 关联表
            arrangeCarDriverService.deleteByArrId(carArrange.getArrId());
            //如果已派车则解除车辆锁定
            if(null != carArrange.getCarId()){
                CarInfo carInfo = carInfoService.findById(carArrange.getCarId());
                if(null != carInfo){
                    carInfo.setIsLock(CarInfo.CAR_ISLOCK_FALSE);
                    carInfoService.update(carInfo);
                }
            }
            //删除派车表
            carArrangeService.deleteByApplyId(applyId);
        }
        CarTrends carTrends = carTrendsService.findByApplyId(applyId);
        if(null != carTrends){
            carTrendsService.deleteById(carTrends.getId());
        }
    }

    @Override
    public void recover(String procInstId, int status){
        Task currentTask = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        //车辆申请回复初始状态
        CarApply carApply = taskService.getVariable(currentTask.getId(), "form", CarApply.class);
        String node = currentTask.getTaskDefinitionKey();
        //节点为还车节点 或者 状态为 不出车 无法撤回操作
        if(CarApply.NODE_CAR_BACK.equals(node) || CarApply.STATUS_CAR_DRIVE_FAIL == carApply.getStatus()){
            throw new RuntimeException("司机已出车，无法撤回");
        }
        if(currentTask==null) {
            throw new RuntimeException("流程未启动或已执行完成，无法撤回");
        }
        //流程返回到一个节点
        Map<String, Object> param = processCoreService.jumpToFirst(procInstId);

        //清理执行的历史数据
        this.cleanData(currentTask, carApply, status, "true", "撤回");

        FlowNode currentFlowNode = (FlowNode)param.get("currentFlowNode");
        List<SequenceFlow> currentFlows = (List<SequenceFlow>)param.get("currentFlows");

        //恢复正常流程
        processCoreService.jumpToCurrent(currentFlowNode, currentFlows);

    }

    @Override
    public void cleanData(Task currentTask, CarApply carApply, int status, String isRecover, String message){
        taskService.addComment(currentTask.getId(), currentTask.getProcessInstanceId(), message);
        Map<String,Object> currentVariables = new HashMap<>();
        currentVariables.put("opinion", ContextSupportUtil.getCurrentUsername()+"执行了"+message+"操作。");
        currentVariables.put("userId", ContextSupportUtil.getCurrentUserId().toString());
        currentVariables.put("isRecover", isRecover);

        //删除出车前的数据
        this.deleteBatch(carApply.getApplyId());

        //更新车辆申请状态
        this.updateStatus(carApply.getApplyId(), status);

        //完成任务
        taskService.complete(currentTask.getId(), currentVariables);
    }
}
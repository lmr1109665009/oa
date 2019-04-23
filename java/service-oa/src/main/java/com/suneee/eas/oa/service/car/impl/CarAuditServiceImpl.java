package com.suneee.eas.oa.service.car.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.flowable.service.ProcessCoreService;
import com.suneee.eas.oa.dao.car.CarApplyDao;
import com.suneee.eas.oa.dao.car.CarAuditDao;
import com.suneee.eas.oa.model.car.CarApply;
import com.suneee.eas.oa.model.car.CarAudit;
import com.suneee.eas.oa.model.car.CarAuditHistory;
import com.suneee.eas.oa.service.car.CarApplyService;
import com.suneee.eas.oa.service.car.CarAuditHistoryService;
import com.suneee.eas.oa.service.car.CarAuditService;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆审批service
 * @user 子华
 * @created 2018/9/4
 */
@Service
public class CarAuditServiceImpl extends BaseServiceImpl<CarAudit> implements CarAuditService {
    @Autowired
    private CarAuditDao auditDao;
    @Autowired
    private TaskService taskService;
    @Autowired
    private CarApplyDao applyDao;
    @Autowired
    private ProcessCoreService processCoreService;
    @Autowired
    private CarApplyService carApplyService;
    @Autowired
    private CarAuditHistoryService carAuditHistoryService;

    @Autowired
    public void setAuditDao(CarAuditDao auditDao) {
        this.auditDao = auditDao;
        setBaseDao(auditDao);
    }

    /**
     * 普通流程审批
     * @param request
     */
    @Override
    public void doAudit(HttpServletRequest request) {
        String taskId= RequestUtil.getString(request,"taskId");
        taskService.complete(taskId);
    }

    @Override
    public void deleteByTargetId(Long targetId) {
        auditDao.deleteByTargetId(targetId);
    }

    @Override
    public void deleteByInstId(String processInstanceId) {
        auditDao.deleteByInstId(processInstanceId);
    }

    /**
     * 车辆管理员审批
     * @param taskId
     * @param driverId
     */
    @Override
    public void doCarAdminAudit(String taskId,String driverId){
        CarApply carApply = (CarApply) taskService.getVariable(taskId,"form");
        carApply.setStatus(CarApply.STATUS_CAR_DRIVE_PENDDING);
        Map<String,Object> variable=new HashMap<>();
        variable.put("form",carApply);
        variable.put("userId",driverId);
        applyDao.updateStatus(carApply.getApplyId(),CarApply.STATUS_CAR_DRIVE_PENDDING);
        HttpServletRequest request= ContextUtil.getRequest();
        request.setAttribute("opinion", "车辆管理员"+ContextSupportUtil.getCurrentUsername() +"已同意");
        request.setAttribute("auditStatus",CarAudit.STATUS_YES);
        taskService.complete(taskId,variable);
    }

    /**
     * 驾驶员出车审批
     * @param taskId
     */
    @Override
    public void doCarDriverOutAudit(String taskId) {
        HttpServletRequest request= ContextUtil.getRequest();
        request.setAttribute("opinion","司机"+ContextSupportUtil.getCurrentUsername()+"已出车");
        request.setAttribute("auditStatus",CarAudit.STATUS_YES);
        Map<String,Object> variable=new HashMap<>();
        CarApply carApply= (CarApply) taskService.getVariable(taskId,"form");
        carApply.setStatus(CarApply.STATUS_CAR_BACK_PENDING);
        variable.put("form",carApply);
        variable.put("userId",ContextSupportUtil.getCurrentUserId().toString());
        applyDao.updateStatus(carApply.getApplyId(),CarApply.STATUS_CAR_BACK_PENDING);
        taskService.complete(taskId,variable);
    }

    /**
     * 驾驶员还车审批
     * @param taskId
     */
    @Override
    public void doCarDriverBackAudit(String taskId) {
        HttpServletRequest request= ContextUtil.getRequest();
        request.setAttribute("opinion","司机"+ContextSupportUtil.getCurrentUsername()+"已还车");
        request.setAttribute("auditStatus",CarAudit.STATUS_YES);
        Map<String,Object> variable=new HashMap<>();
        CarApply carApply= (CarApply) taskService.getVariable(taskId,"form");
        carApply.setStatus(CarApply.STATUS_CAR_BACK_DONE);
        variable.put("form",carApply);
        variable.put("userId", ContextSupportUtil.getCurrentUserId().toString());
        applyDao.updateStatus(carApply.getApplyId(),CarApply.STATUS_CAR_BACK_DONE);
        taskService.complete(taskId,variable);
    }

    /**
     *
     * @param applyId
     * @return
     */
    @Override
    public CarAudit findByApplyId(Long applyId){
        return auditDao.findByApplyId(applyId);
    }





    /**
     * 审批不同意
     */
    @Override
    public void auditDisAgree(String taskId, int status){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Task currentTask = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        //车辆申请回复初始状态
        CarApply carApply = taskService.getVariable(currentTask.getId(), "form", CarApply.class);
        CarAudit carAudit = auditDao.findByApplyId(carApply.getApplyId());
        if(currentTask==null) {
            throw new RuntimeException("流程未启动或已执行完成，操作失败");
        }
        //流程返回到一个节点
        Map<String, Object> param = processCoreService.jumpToFirst(task.getProcessInstanceId());

        //清理执行的历史数据
        carApplyService.cleanData(currentTask, carApply, status, "true", "审批拒绝");

        carApply.setStatus(status);
        carAudit.setAuditStatus(CarAudit.STATUS_NO);
        carAudit.setEndTime(new Date());
        saveAuditHistory(carAudit, carApply);

        FlowNode currentFlowNode = (FlowNode)param.get("currentFlowNode");
        List<SequenceFlow> currentFlows = (List<SequenceFlow>)param.get("currentFlows");

        //恢复正常流程
        processCoreService.jumpToCurrent(currentFlowNode, currentFlows);
    }

    /**
     * 保存审批记录
     */
    private void saveAuditHistory(CarAudit carAudit, CarApply carApply){
        CarAuditHistory history = new CarAuditHistory();
        history.setId(IdGeneratorUtil.getNextId());
        history.setDestination(carApply.getDestination());
        history.setExpMileage(carApply.getExpMileage());
        history.setContent(carApply.getContent());
        history.setStatus(carApply.getStatus());
        history.setEnterpriseCode(carApply.getEnterpriseCode());
        history.setProcInstId(carApply.getProcInstId());
        history.setTaskId(carApply.getTaskId());
        history.setCurrentNode(carAudit.getNodeName());
        history.setAssigneeId(carAudit.getAuditorId());
        history.setAssigneeName(carAudit.getAuditorName());
        history.setAuditStatus(carAudit.getAuditStatus());

        history.setApplyId(carApply.getApplyId());
        history.setApplicantId(carApply.getApplicantId());
        history.setApplicantName(carApply.getApplicantName());
        history.setMobile(carApply.getMobile());
        history.setIsSelfDrive(carApply.getIsSelfDrive());
        history.setPassengerCount(carApply.getPassengerCount());
        history.setPassengerIds(carApply.getPassengerIds());
        history.setPassengerNames(carApply.getPassengerNames());
        history.setInformType(carApply.getInformType());
        history.setStartTime(carAudit.getStartTime());
        history.setEndTime(carAudit.getEndTime());
        history.setOrigin(carApply.getOrigin());
        history.setOpinion(carAudit.getOpinion());
        history.setCreateTime(new Date());
        history.setCarApplyTime(carApply.getCreateTime());
        carAuditHistoryService.save(history);
    }

}

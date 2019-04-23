package com.suneee.eas.oa.listener;

import com.suneee.eas.common.utils.*;
import com.suneee.eas.oa.dao.car.CarApplyDao;
import com.suneee.eas.oa.dao.car.CarAuditDao;
import com.suneee.eas.oa.model.car.CarApply;
import com.suneee.eas.oa.model.car.CarAudit;
import com.suneee.eas.oa.model.car.CarAuditHistory;
import com.suneee.eas.oa.service.car.CarAuditHistoryService;
import com.suneee.eas.oa.service.user.UserService;
import com.suneee.platform.model.system.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 车辆审批监听器
 * @user 子华
 * @created 2018/9/26
 */
@Component
public class CarAuditTaskListener implements TaskListener {
    @Autowired
    private CarAuditDao auditDao;
    @Autowired
    private CarApplyDao applyDao;
    @Autowired
    private UserService userService;
    @Autowired
    private CarAuditHistoryService carAuditHistoryService;

    @Override
    public void notify(DelegateTask delegateTask) {
        //如果是撤回操作，则不做任何处理
        String isRecover = delegateTask.getVariable("isRecover", String.class);
        if("true".equals(isRecover)){
            return;
        }
        //任务被创建时，新增审批记录
        if (TaskListener.EVENTNAME_CREATE.equals(delegateTask.getEventName())){
            try {
                createNotify(delegateTask);
            } catch (Exception e) {
                throw new RuntimeException("创建审批记录出错："+e.getMessage(),e);
            }
        }else if(TaskListener.EVENTNAME_COMPLETE.equals(delegateTask.getEventName())){
            //任务执行完成，更新审批记录
            completeNotify(delegateTask);
        }else if (TaskListener.EVENTNAME_DELETE.equals(delegateTask.getEventName())){
            //任务被删除（包含驳回，驳回到发起人）
            deleteNotify(delegateTask);
        }
    }

    /**
     * 任务创建通知
     * @param delegateTask
     */
    private void createNotify(DelegateTask delegateTask) throws Exception{
        CarApply carApply= (CarApply) delegateTask.getVariable("form");
        carApply.setTaskId(delegateTask.getId());
        applyDao.update(carApply);
        Integer autoProcess= (Integer) delegateTask.getVariable("autoProcess");
        if (autoProcess!=null&&autoProcess==1){
            return;
        }
        CarAudit carAudit = auditDao.findByApplyId(carApply.getApplyId());
        boolean isUpdate = true;
        if(null == carAudit){
            isUpdate = false;
            carAudit = new CarAudit();
            carAudit.setAuditId(IdGeneratorUtil.getNextId());
        }

        //获取回填审批人
        String userId = (String)delegateTask.getVariable("auditorId");
        if(StringUtils.isNotEmpty(userId)){
            SysUser user = userService.getUserDetails(Long.valueOf(userId));
            carAudit.setAuditorId(Long.valueOf(userId));
            carAudit.setAuditorName(ContextSupportUtil.getUsername(user));
        }
        carAudit.setStartTime(new Date());
        carAudit.setIsRead(CarAudit.READ_NO);
        carAudit.setNodeName(delegateTask.getName());
        carAudit.setTargetId(Long.valueOf(delegateTask.getId()));
        carAudit.setProcInstId(delegateTask.getProcessInstanceId());
        if(isUpdate){
            auditDao.update(carAudit);
        }else{
            auditDao.save(carAudit);
        }
        delegateTask.setVariable("auditId",carAudit.getAuditId());
    }

    /**
     * 任务执行完成通知
     * @param delegateTask
     */
    private void completeNotify(DelegateTask delegateTask) {
        Long auditId= (Long) delegateTask.getVariable("auditId");
        HttpServletRequest request= ContextUtil.getRequest();
        CarAudit carAudit=auditDao.findById(auditId);
        carAudit.setEndTime(new Date());
        String opinion= (String) request.getAttribute("opinion");
        if (StringUtil.isEmpty(opinion)){
            opinion=RequestUtil.getString(request,"opinion");
        }
        carAudit.setOpinion(opinion);
        long duration=carAudit.getEndTime().getTime()-carAudit.getStartTime().getTime();
        carAudit.setDuration(duration);
        carAudit.setAuditorId(ContextSupportUtil.getCurrentUserId());
        carAudit.setAuditorName(ContextSupportUtil.getCurrentUsername());
        Integer status= (Integer) request.getAttribute("auditStatus");
        if (status==null){
            status=RequestUtil.getInt(request,"auditStatus");
        }
        carAudit.setAuditStatus(status);
        auditDao.update(carAudit);

        //保存审批记录
        CarApply carApply= (CarApply) delegateTask.getVariable("form");
        carApply.setStatus(CarApply.STATUS_CAR_ARRANGE_PENDING);
        saveAuditHistory(carAudit, carApply);
    }

    /**
     * 保存审批记录
     */
    private void saveAuditHistory(CarAudit carAudit, CarApply carApply){
        CarAuditHistory carAuditHistory = new CarAuditHistory();
        carAuditHistory.setId(IdGeneratorUtil.getNextId());
        carAuditHistory.setApplyId(carApply.getApplyId());
        carAuditHistory.setApplicantId(carApply.getApplicantId());
        carAuditHistory.setApplicantName(carApply.getApplicantName());
        carAuditHistory.setMobile(carApply.getMobile());
        carAuditHistory.setIsSelfDrive(carApply.getIsSelfDrive());
        carAuditHistory.setPassengerCount(carApply.getPassengerCount());
        carAuditHistory.setPassengerIds(carApply.getPassengerIds());
        carAuditHistory.setPassengerNames(carApply.getPassengerNames());
        carAuditHistory.setInformType(carApply.getInformType());
        carAuditHistory.setStartTime(carAudit.getStartTime());
        carAuditHistory.setEndTime(carAudit.getEndTime());
        carAuditHistory.setOrigin(carApply.getOrigin());
        carAuditHistory.setDestination(carApply.getDestination());
        carAuditHistory.setExpMileage(carApply.getExpMileage());
        carAuditHistory.setContent(carApply.getContent());
        carAuditHistory.setStatus(carApply.getStatus());
        carAuditHistory.setEnterpriseCode(carApply.getEnterpriseCode());
        carAuditHistory.setProcInstId(carApply.getProcInstId());
        carAuditHistory.setTaskId(carApply.getTaskId());
        carAuditHistory.setCurrentNode(carAudit.getNodeName());
        carAuditHistory.setAssigneeId(carAudit.getAuditorId());
        carAuditHistory.setAssigneeName(carAudit.getAuditorName());
        carAuditHistory.setAuditStatus(carAudit.getAuditStatus());
        carAuditHistory.setOpinion(carAudit.getOpinion());
        carAuditHistory.setCreateTime(new Date());
        carAuditHistory.setCarApplyTime(carApply.getCreateTime());
        carAuditHistoryService.save(carAuditHistory);
    }

    /**
     * 任务被删除通知
     * @param delegateTask
     */
    private void deleteNotify(DelegateTask delegateTask) {
//        Long auditId= (Long) delegateTask.getVariable("auditId");
//        HttpServletRequest request= ContextUtil.getRequest();
//        CarAudit carAudit=auditDao.findById(auditId);
//        CarAudit updateAudit=new CarAudit();
//        updateAudit.setAuditId(auditId);
//        updateAudit.setEndTime(new Date());
//        updateAudit.setOpinion(RequestUtil.getString(request,"opinion"));
//        long duration=updateAudit.getEndTime().getTime()-carAudit.getStartTime().getTime();
//        updateAudit.setDuration(duration);
//        updateAudit.setAuditorId(ContextSupportUtil.getCurrentUserId());
//        updateAudit.setAuditorName(ContextSupportUtil.getCurrentUsername());
//        updateAudit.setAuditStatus(CarAudit.STATUS_NO);
//        auditDao.update(updateAudit);
    }
}

package com.suneee.platform.service.bpm.listener;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.dao.bpm.*;
import com.suneee.platform.model.bpm.ExecutionStack;
import com.suneee.platform.model.bpm.TaskFork;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SystemConst;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.thread.TaskThreadService;
import com.suneee.platform.service.form.BpmFormDataLogService;
import com.suneee.platform.service.worktime.CalendarAssignService;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.bpm.*;
import com.suneee.platform.model.bpm.ExecutionStack;
import com.suneee.platform.model.bpm.TaskFork;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SystemConst;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.thread.TaskThreadService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务完成时执行的事件。
 *
 * @author ray
 */
public class TaskCompleteListener extends BaseTaskListener {

    @Resource
    private TaskOpinionService taskOpinionService;
    @Resource
    private BpmProStatusDao bpmProStatusDao;
    @Resource
    private ExecutionStackDao executionStackDao;
    @Resource
    private TaskDao taskDao;
    @Resource
    private HistoryActivityDao historyActivityDao;
    @Resource
    private CalendarAssignService calendarAssignService;
    @Resource
    private TaskService taskService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskReadDao taskReadDao;
    @Resource
    private BpmFormDataLogService bpmFormDataLogService;
    @Resource
    private BpmService bpmService;

    @Override
    protected void execute(DelegateTask delegateTask, String actDefId, String nodeId) {
        //获取当前任务的分发的令牌, value as T_1 or T_1_2
        String token = (String) delegateTask.getVariableLocal(TaskFork.TAKEN_VAR_NAME);
        if (token != null) {
            //放置会签线程，以使得在后续产生的任务中使用
            TaskThreadService.setToken(token);
        }

        //为了解决在任务自由跳转或回退时，若流程实例有多个相同Key的任务，会把相同的任务删除。
        ProcessCmd processCmd = TaskThreadService.getProcessCmd();
        if (processCmd != null && (processCmd.isBack() > 0 || StringUtils.isNotEmpty(processCmd.getDestTask()))) {
            taskDao.updateNewTaskDefKeyByInstIdNodeId(delegateTask.getTaskDefinitionKey() + "_1", delegateTask.getTaskDefinitionKey(), delegateTask.getProcessInstanceId());
        }
        String taskId = delegateTask.getId();
        //更新执行堆栈里的执行人员及完成时间等
        updateExecutionStack(delegateTask.getProcessInstanceId(), delegateTask.getTaskDefinitionKey(), taskId, token);
        //更新任务意见。
        updOpinion(delegateTask);
        //更新流程节点状态。
        updNodeStatus(nodeId, delegateTask);
        //更新历史节点
        setActHisAssignee(delegateTask);
        String id = taskId;
        //删除任务已读记录
        taskReadDao.delByTaskId(new Long(id));
        //插入表单数据历史记录
        if (processCmd != null && processCmd.getBusinessKey() != null && processCmd.getFormData() != null
                && PropertyUtil.getBooleanByAlias("recordFormData", false)) {
            bpmFormDataLogService.insertFormDataLog(processCmd.getBusinessKey(), processCmd.getFormData(), actDefId, nodeId);
        }
    }

    /**
     * 更新执行堆栈里的执行人员及完成时间等
     *
     * @param instanceId 流程实例ID
     * @param nodeId     节点IDeas
     * @param token      　令牌
     */
    private void updateExecutionStack(String instanceId, String nodeId, String taskId, String token) {
        List<ExecutionStack> executionStacks = executionStackDao.getByActInstIdNodeIdToken(instanceId, nodeId, token);
        if (BeanUtils.isEmpty(executionStacks)) return;
        // 默认获取第一条记录
        ExecutionStack executionStack = executionStacks.get(0);
        // 如果取出来的堆栈记录超过一条，则通过任务ID来找到对应的记录
        if (executionStacks.size() > 1 && StringUtil.isNotEmpty(taskId)) {
            for (ExecutionStack stack : executionStacks) {
                if (taskId.equals(stack.getTaskIds())) {
                    executionStack = stack;
                    break;
                }
            }
        }
        if (executionStack != null) {
            SysUser curUser = (SysUser) ContextUtil.getCurrentUser();
            String userId = "";
            if (curUser != null) {
                userId = curUser.getUserId().toString();
            } else {
                userId = SystemConst.SYSTEMUSERID.toString();
            }
            executionStack.setAssignees(userId);
            executionStack.setEndTime(new Date());
            executionStackDao.update(executionStack);
        }
    }

    /**
     * 根据流程节点的状态。
     *
     * @param nodeId
     * @param delegateTask
     */
    private void updNodeStatus(String nodeId, DelegateTask delegateTask) {
        boolean isMuliti = BpmUtil.isMultiTask(delegateTask);
        //非会签节点,更新节点的状态。
        if (!isMuliti) {
            Map<String, Object> map = runtimeService.getVariables(delegateTask.getProcessInstanceId());
            String actInstanceId = delegateTask.getProcessInstanceId();
            //更新节点状态。
            if (map.get(BpmConst.NODE_APPROVAL_STATUS + "_" + delegateTask.getTaskDefinitionKey()) != null) {
                Short approvalStatus = (Short) map.get(BpmConst.NODE_APPROVAL_STATUS + "_" + delegateTask.getTaskDefinitionKey());
                bpmProStatusDao.updStatus(new Long(actInstanceId), nodeId, approvalStatus);
            }
        }
    }

    /**
     * 修改当前任务意见。
     *
     * @param delegateTask
     */
    private Long updOpinion(DelegateTask delegateTask) {
        TaskOpinion taskOpinion = taskOpinionService.getByTaskId(new Long(delegateTask.getId()));
        // 如果设置了执行人为空跳过配置，则不进行审批历史的记录
        ProcessCmd processCmd = TaskThreadService.getProcessCmd();
        Map<String, Object> var = processCmd.getVariables();
        //系统原有核心代码，解决同步里某个节点选不到人时会在共用对象processCmd.variable添加taskUserIsNull标识，会影响其他同步节点判断
        //String taskUserIsNull = (String) var.get("taskUserIsNull");
        //修改后代码，增加节点ID作为字段前缀，避免各个节点使用同一个变量进行判断
        String taskUserIsNull = (String) var.get(delegateTask.getTaskDefinitionKey()+"_taskUserIsNull");
        //处理完之后就删除标记
        //系统原有核心代码，解决同步里某个节点选不到人时会在共用对象processCmd.variable添加taskUserIsNull标识，会影响其他同步节点判断
        //var.remove("taskUserIsNull");
        //修改后代码，增加节点ID作为字段前缀，避免各个节点使用同一个变量进行判断
        var.remove(delegateTask.getTaskDefinitionKey()+"_taskUserIsNull");
        if (!(StringUtil.isNotEmpty(taskUserIsNull) && taskUserIsNull.equals("Y"))) {
            if (taskOpinion == null)
                return 0L;

            SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
            Long userId = SystemConst.SYSTEMUSERID;
            String userName = SystemConst.SYSTEMUSERNAME;
            if (sysUser != null) {
                ProcessCmd cmd = TaskThreadService.getProcessCmd();
                // 如果跳过不更改意见执行人。
                if (!cmd.isSkip()) {
                    userId = sysUser.getUserId();
                    userName = ContextSupportUtil.getUsername(sysUser);
                    taskOpinion.setExeUserId(userId);
                    taskOpinion.setExeFullname(userName);
                    String approvalContent = cmd.getVoteContent();
                    taskOpinion.setOpinion(approvalContent);
                } else {
                    taskOpinion.setOpinion("同意(自动审批)");
                }
            }

            ProcessCmd cmd = TaskThreadService.getProcessCmd();

            // 获取流程意见。
            Short status = getStatus(cmd);
            taskOpinion.setCheckStatus(status);
            String fieldName = cmd.getVoteFieldName();
            if (StringUtil.isNotEmpty(fieldName)) {
                taskOpinion.setFieldName(fieldName);
            }
            taskOpinion.setEndTime(new Date());
            Long duration = calendarAssignService.getRealWorkTime(
                    taskOpinion.getStartTime(), taskOpinion.getEndTime(),
                    taskOpinion.getExeUserId());
            taskOpinion.setDurTime(duration);
            taskOpinionService.update(taskOpinion);
            return duration;
        }
        taskOpinionService.delById(taskOpinion.getOpinionId());
        return null;
    }

    /**
     * 获取审批状态。
     *
     * @param cmd
     * @return
     */
    private Short getStatus(ProcessCmd cmd) {
        Short status = TaskOpinion.STATUS_AGREE;
        /**
         * 0，正常跳转。 1，退回 2，退回到发起人。
         */
        int isBack = cmd.isBack();
        boolean isRevover = cmd.isRecover();
        /*
		 * 0=弃权,1=同意,2=反对,3=退回, 4=追回, 5=会签通过, 6=会签不通过, 33=提交,34=再提交, 40=代提交
		 */
        int vote = cmd.getVoteAgree();
        switch (isBack) {
            //正常
            case 0:
                switch (vote) {
                    case 0:
                        status = TaskOpinion.STATUS_ABANDON;
                        break;
                    case 1:
                        status = TaskOpinion.STATUS_AGREE;
                        break;
                    case 2:
                        status = TaskOpinion.STATUS_REFUSE;
                        break;
                    case 5:
                        status = TaskOpinion.STATUS_PASSED;
                        break;
                    case 6:
                        status = TaskOpinion.STATUS_NOT_PASSED;
                        break;
                    case 33:
                        status = TaskOpinion.STATUS_SUBMIT;
                        break;
                    case 34:
                        status = TaskOpinion.STATUS_RESUBMIT;
                        break;
                    case 40:
                        status = TaskOpinion.STATUS_REPLACE_SUBMIT;
                        break;
                }
                break;
            //退回（追回)
            case 1:
                if (isRevover) {
                    status = TaskOpinion.STATUS_RECOVER;
                } else {
                    status = TaskOpinion.STATUS_REJECT;
                }
                break;
            //退回（追回)到发起人。
            case 2:
                if (isRevover) {
                    status = TaskOpinion.STATUS_RECOVER_TOSTART;
                } else {
                    status = TaskOpinion.STATUS_REJECT_TOSTART;
                }
                break;
            case 3:
                //自由退回
                status = TaskOpinion.STATUS_REJECT_TOANYNODE;
                break;
        }
        return status;
    }

    @Override
    protected int getScriptType() {
        return BpmConst.EndScript;
    }

    private void setActHisAssignee(DelegateTask delegateTask) {
        ExecutionExtDao executionExtDao = (ExecutionExtDao) AppUtil.getBean(ExecutionExtDao.class);
        ProcessCmd processCmd = TaskThreadService.getProcessCmd();
        DelegateExecution delegateExecution = delegateTask.getExecution();
        String parentId = delegateExecution.getParentId();

        //		String executionId=delegateTask.getExecutionId();
        String nodeId = delegateTask.getTaskDefinitionKey();

        //			List<HistoricActivityInstanceEntity> hisList = historyActivityDao.getByExecutionId(executionId, nodeId);
        //			hisList = historyActivityDao.getByExecutionId(parentId, nodeId);
        List<HistoricActivityInstanceEntity> hisList = null;
        DelegateExecution execution = delegateExecution;
        while (execution != null) {
            hisList = historyActivityDao.getByExecutionId(execution.getId(), nodeId);
            if (BeanUtils.isNotEmpty(hisList)) {
                break;
            }
            parentId = execution.getParentId();
            if (StringUtil.isEmpty(parentId)) {
                execution = null;
            } else {
                execution = executionExtDao.getById(parentId);
            }

        }
        if (BeanUtils.isEmpty(hisList)) {
            return;
        }

        SysUser curUser = (SysUser) ContextUtil.getCurrentUser();
        if (curUser == null) {
            return;
        }
        String assignee = curUser.getUserId().toString();
        for (HistoricActivityInstanceEntity hisActInst : hisList) {
            //流转任务正常流转，即无别人干预
            if (TaskOpinion.STATUS_COMMON_TRANSTO.toString().equals(delegateTask.getDescription())) {
                taskService.setAssignee(delegateTask.getId(), delegateTask.getAssignee());
                hisActInst.setAssignee(delegateTask.getAssignee());
            } else {
                //任务执行人为空或者 执行人和当前人不一致，设置当前人为任务执行人。如果任务已结束（endTime不为空），那么就不需要更新记录了
                if ((StringUtil.isEmpty(hisActInst.getAssignee()) || !hisActInst.getAssignee().equals(assignee)) && hisActInst.getEndTime() == null) {
                    taskService.setAssignee(delegateTask.getId(), assignee);
                    hisActInst.setAssignee(assignee);
                }
            }
            int fromMobile = processCmd.isFromMobile() ? 1 : 0;
            historyActivityDao.updateParams(hisActInst, fromMobile);
        }
    }

    @Override
    protected int getBeforeScriptType() {
        return BpmConst.EndBeforeScript;
    }
}

package com.suneee.rs.controller;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.constant.UserConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.controller.bpm.TaskController;
import com.suneee.platform.dao.bpm.ProcessRunDao;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.bpm.TaskRead;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.TaskReadService;
import com.suneee.platform.service.system.SysErrorLogService;
import com.suneee.platform.service.system.SysFileService;
import com.suneee.platform.service.util.ServiceUtil;
import org.activiti.engine.ActivitiInclusiveGateWayException;
import org.activiti.engine.ActivitiVarNotFoundException;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.suneee.ucp.base.vo.ResultVo;
@Controller
@RequestMapping("/platform/bpm/task/")
public class TaskRejectController {
    protected Logger logger = LoggerFactory.getLogger(TaskController.class);
    @Resource
    private BpmService bpmService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private ProcessRunService processRunService;
    @Resource
    private TaskReadService taskReadService;
    @Resource
    private SysFileService sysFileService;
    @Resource
    private TaskOpinionService taskOpinionService;
    @Resource
    private SysErrorLogService sysErrorLogService;
    @Resource
    ProcessRunDao processRunDao;
    /**
     * 完成任务
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "completed")
    @ResponseBody
    public ResultVo complete(HttpServletRequest request) throws Exception {
        String account = request.getParameter("account");
        String account1 = RequestUtil.getString(request, "account");
        if (account == null) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户账号不能为空！");
        }
        //SysUser newUser = sysUserService.getByAccount(account);
        SysUser newUser = processRunService.getUseridByAccount(account);
        //根据userId对用户进行判断
        if (newUser == null) {
            newUser = processRunService.getUseridByAliasname(account);
            if(newUser==null){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户账号不存在");
            }
        }
        com.suneee.eas.common.utils.ContextUtil.getSession().setAttribute(UserConstant.SESSION_USER_KEY,newUser);

        String runId = request.getParameter("runId");
        ProcessRun processRun= processRunDao.getByRunId(runId);
        TaskOpinion taskOpinion = taskOpinionService.getByActInstIds(processRun.getActInstId());
        String taskId = taskOpinion.getTaskId().toString();

        logger.debug("任务完成跳转....");
        int fromMobile = RequestUtil.getInt(request,"fromMobile");
        SysUser curUser = (SysUser) ContextSupportUtil.getCurrentUser();
        ResultVo resultVo =new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
        //String taskId = RequestUtil.getString(request, "taskId");
        TaskEntity task = bpmService.getTask(taskId);
        if (task == null) {
            resultVo.setMessage("此任务已经执行了!");
            return resultVo;
        }
        String actDefId = task.getProcessDefinitionId();
        BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
        if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus())) {
            resultVo.setMessage("流程实例已经禁止，该任务不能办理！");
            return resultVo;
        }
        Long userId = curUser.getUserId();
        ProcessCmd taskCmd = BpmUtil.getProcessCmds(request,taskId);

        //String[] lastDestTaskIds = RequestUtil.getStringAryByStr(request, "lastDestTaskId");
        String lastDestTaskIds = request.getParameter("lastDestTaskId");
        if(lastDestTaskIds!=null){
            String[] destTaskIds = lastDestTaskIds.toString().split(",");
            if (destTaskIds != null) {
                taskCmd.setLastDestTaskIds(destTaskIds);
                String[] destTaskUserIds = new String[destTaskIds.length];
                for (int i = 0; i < destTaskIds.length; i++) {
                    Object userIds = request.getParameter(destTaskIds[i]+"_userId");
                    if(userIds!=null){
                        SysUser nextUser = processRunService.getUseridByAccount(userIds.toString());                        //人事系统这个userIds传的是用户账号
                        if(null!=nextUser){
                            userIds= "user^"+nextUser.getUserId()+"^"+nextUser.getFullname();
                        }
                        userIds=userIds.toString().replace(",","#");
                    }
                    else{
                        userIds="";
                    }
                    destTaskUserIds[i]=userIds.toString();
                }
                taskCmd.setLastDestTaskUids(destTaskUserIds);
            }
        }

        //测试用，验证是否可以自由退回
//		taskCmd.setStartNode("UserTask4");
//		taskCmd.setBack(3);
//		System.out.println(taskCmd.getStartNode()+"..................."+taskCmd.isBack()+"..........."+taskCmd.isRecover());
        taskCmd.setCurrentUserId(userId.toString());
        String assignee = task.getAssignee();
        // 非管理员,并且没有任务的权限。
        boolean isAdmin = taskCmd.getIsManage().shortValue() == 1;
        if (!isAdmin) {
            boolean rtn = processRunService.getHasRightsByTask(new Long(taskId), userId);
            if (!rtn) {
                resultVo.setMessage("对不起,你不是这个任务的执行人,不能处理此任务!");
                return resultVo;
            }
        }
        // 记录日志。
        logger.info(taskCmd.toString());
        if (ServiceUtil.isAssigneeNotEmpty(assignee) && !task.getAssignee().equals(userId.toString()) && !isAdmin) {
            resultVo.setMessage("该任务已被其他人锁定!");
        } else {
            String errorUrl = RequestUtil.getErrorUrl(request);
            String ip = RequestUtil.getIpAddr(request);
            try {
                TaskRead taskRead = taskReadService.getByTaskId(Long.valueOf(taskId));
                processRunService.nextProcess(taskCmd);
                if(taskRead != null){
                    taskReadService.delByActInstId(taskRead.getActinstid());
                }
                //需要删除的附件id
                Long[] delFileIds = RequestUtil.getLongAryByStr(request, "delFileIds");
                //删除附件
                if (null != delFileIds && delFileIds.length > 0) {
                    sysFileService.delSysFileByIds(delFileIds);
                }
                List<TaskOpinion> lists = taskOpinionService.getByRunId(Long.valueOf(runId));
                List<TaskOpinion> newlist = new ArrayList<>();
                for (TaskOpinion taskopin:lists
                        ) {
                    if(taskopin.getEndTime()==null){
                        newlist.add(taskopin);
                    }
                }
                String str = "";
                if(newlist.size()==1) {
                    if(task.getName().equals(newlist.get(0).getTaskName())){
                        str="等待相同节点其他审批人审批";
                        if(fromMobile==1){
                            resultVo.setMessage(str);
                        }else {
                            resultVo.setMessage("<p style='color:red'>" + str + "</p>");
                        }
                    }else {
                        str = newlist.get(0).getTaskName();
                        if(fromMobile==1){
                            resultVo.setMessage("成功流转至" + str + "节点");
                        }else {
                            resultVo.setMessage("成功流转至<p style='color:red'>" + str + "</p>节点");
                        }
                    }
                    resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
                }else if(newlist.size()>1){
                    boolean hasdif = hasDifNode(newlist);
                    if(hasdif) {
                        str = "流转至多任务节点审批";
                        if(fromMobile==1) {
                            resultVo.setMessage(str);
                        }else{
                            resultVo.setMessage("<p style='color:red'>" + str + "</p>");
                        }
                    }else{
                        if(!task.getName().equals(newlist.get(0).getTaskName())){
                            str = newlist.get(0).getTaskName();
                            resultVo.setMessage("成功流转至<p style='color:red'>" + str + "</p>节点");
                        }else {
                            str="等待相同节点其他审批人审批";
                            if(fromMobile==1) {
                                resultVo.setMessage(str);
                            }else{
                                resultVo.setMessage("<p style='color:red'>"+str+"</p>");
                            }
                        }
                    }
                    resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
                }else{
                    str="审批成功，流程结束。";
                    resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
                    resultVo.setMessage(str);
                }
            } catch (ActivitiVarNotFoundException ex) {
                resultVo.setMessage("请检查变量是否存在:"+ ex.getMessage());
                // 添加错误消息到日志
                sysErrorLogService.addError(curUser.getAccount(), ip, ex.getMessage(), errorUrl);
            } catch (ActivitiInclusiveGateWayException ex) {
                resultVo.setMessage(ex.getMessage());
                // 添加错误消息到日志
                sysErrorLogService.addError(curUser.getAccount(), ip, ex.getMessage(), errorUrl);
            } catch (Exception ex) {
                ex.printStackTrace();
                String str = MessageUtil.getMessage();
                if (StringUtil.isNotEmpty(str)) {
                    //兼容手机版没有选择人员时，返回给用户的提示语改为“请选择签批人”
                    if(RequestUtil.getLong(request,"fromMobile")==1&&str.contains(",没有设置执行人")){
                        str="请选择签批人";
                    }
                    resultVo.setMessage(str);
                    // 添加错误消息到日志
                    sysErrorLogService.addError(curUser.getAccount(), ip, str, errorUrl);
                } else {
                    String message = ExceptionUtil.getExceptionMessage(ex);
                    resultVo.setMessage("提交失败！");
                    // 添加错误消息到日志
                    sysErrorLogService.addError(curUser.getAccount(), ip, message, errorUrl);
                }
            }
        }
        return resultVo;
    }


    public boolean hasDifNode(List<TaskOpinion> list)throws Exception{
        if(list.size()==0||list==null){
            return false;
        }
        String nodeName = list.get(0).getTaskName();
        for(int i=1;i<=list.size()-1;i++){
            if(!list.get(i).getTaskName().equals(nodeName)){
                return true;
            }
        }
        return false;
    }
}

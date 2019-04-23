package com.suneee.rs.controller;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.suneee.core.api.bpm.model.IProcessRun;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.eas.common.constant.UserConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONArray;
import com.suneee.oa.controller.bpm.TaskApiController;
import com.suneee.ucp.mh.dao.codeTable.ApiTaskHistoryDao;
import com.suneee.ucp.mh.model.codeTable.ApiTaskHistory;
@Controller
@RequestMapping("/platform/bpm/restartrun/")
public class ReStartProcessController {
    protected Logger logger = LoggerFactory.getLogger(TaskApiController.class);
    @Resource
    private ProcessRunService processRunService;
    @Autowired
    private ApiTaskHistoryDao apiTaskHistoryDao;
    /**
     * 外部调用该接口来新建流程
     * 外部调用该接口启动流程
     */
    @RequestMapping(value = "startFlow")
    @ResponseBody
    public Object startFlow(@RequestBody Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> resultMap = new HashMap<>();
        //Long runId = RequestUtil.getLong(request, "runId");
        Object runId = map.get("runId");
        Object isMobile = map.get("isMobile");
        String account = (String) map.get("account");
        if (account == null) {
            return new ResultMessage(ResultMessage.Fail, "用户账号不能为空！");
        }
        SysUser currentUser = processRunService.getUseridByAccount(account);
        //ISysUser currentUser = ContextSupportUtil.getCurrentUser();

        //根据userId对用户进行判断
        if (currentUser == null) {
            currentUser = processRunService.getUseridByAliasname(account);
            if(currentUser==null){
                return new ResultMessage(ResultMessage.Fail, "用户不存在！");
            }
        }
        com.suneee.eas.common.utils.ContextUtil.getSession().setAttribute(UserConstant.SESSION_USER_KEY,currentUser);

        try {
            ProcessCmd processCmd = BpmUtil.getProcessCmd(request);
            Object lastDestTaskIds = map.get("lastDestTaskId");
            if(lastDestTaskIds!=null){
                String[] destTaskIds = lastDestTaskIds.toString().split(",");
                if (destTaskIds != null) {
                    processCmd.setLastDestTaskIds(destTaskIds);
                    String[] destTaskUserIds = new String[destTaskIds.length];
                    for (int i = 0; i < destTaskIds.length; i++) {
                        Object userIds = map.get(destTaskIds[i]+"_userId");
                        if(userIds!=null){
                            SysUser nextUser = processRunService.getUseridByAccount(userIds.toString());
                            //人事系统这个userIds传的是用户账号
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
                    processCmd.setLastDestTaskUids(destTaskUserIds);
                }
            }
            if(map.get("destTask")!=null){
                processCmd.setDestTask(map.get("destTask").toString());
            }
            processCmd.setCurrentUserId(ContextSupportUtil.getCurrentUserId().toString());
            processCmd.setActDefId( map.get("actDefId").toString());
            processCmd.setFormData(JSONArray.toJSONString(map.get("formData")));
            if (runId != null) {
                ProcessRun processRun = processRunService.getById(Long.parseLong(runId.toString()));
                if (BeanUtils.isEmpty(processRun)) {
                    return new ResultMessage(ResultMessage.Fail, "流程草稿不存在或已被清除");
                }
                processCmd.setProcessRun((IProcessRun) processRun);
            }
            ProcessRun processRun = processRunService.startProcess(processCmd,account);
            //Object comeFrom = map.get("comeFrom");
            Integer isStart = (Integer) map.get("isStart");
            if(isStart == 0){
                ApiTaskHistory apiTaskHistory = new ApiTaskHistory();
                apiTaskHistory.setId(UniqueIdUtil.genId());
                apiTaskHistory.setRunId(processRun.getRunId());
                apiTaskHistory.setProcessName(processRun.getSubject());
                apiTaskHistory.setComeFrom(isStart.toString());
                apiTaskHistory.setCreateBy(ContextSupportUtil.getCurrentUserId());
                apiTaskHistory.setCreateDate(new Date());
                apiTaskHistoryDao.add(apiTaskHistory);
            }
            ResultMessage resultMessage = new ResultMessage(ResultMessage.Success, "启动流程成功!");
            ContextUtil.setCurrentUser(currentUser);
            resultMap.put("resultMessage",resultMessage);
            resultMap.put("processRun",processRun);
            return resultMap;
        } catch (Exception ex) {
            logger.debug("startFlow:" + ex.getMessage());
            ex.printStackTrace();
            String str = MessageUtil.getMessage();
            if (StringUtil.isNotEmpty(str)) {
                ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "启动流程失败:\r\n" + str);
                return resultMessage;
            } else {
                String message = ex.getMessage();
                if (StringUtil.isEmpty(message)) {
                    Throwable tt = ex.getCause();
                    message = tt.getMessage();
                }
                ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
                return resultMessage;
            }
        }
    }


}

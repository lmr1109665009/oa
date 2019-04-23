package com.suneee.rs.controller;
import java.io.PrintWriter;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.constant.UserConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/platform/bpm/revocationrun/")
public class RevocationProcessController extends BaseController {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProcessRunService processRunService;

    /**
     * 任务追回,检查当前正在运行的任务是否允许进行追回。
     *
     * <pre>
     * 需要传入的参数：
     * runId:任务执行Id。
     * backToStart:追回到发起人。
     * memo:追回原因。
     *  任务能够被追回的条件：
     *  1.流程实例没有结束。
     *
     * 	任务追回包括两种情况。
     *  1.追回到发起人。
     *  4.如果这个流程实例有多个流程实例的情况，那么第一个跳转到退回节点，其他的只完成当前任务，不进行跳转。
     *
     * </pre>
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("recover")
    public void recover(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter writer = response.getWriter();
        Long runId = RequestUtil.getLong(request, "runId");
        ISysUser currentUser = ContextSupportUtil.getCurrentUser();
        String account = RequestUtil.getString(request, "username");
        ResultMessage resultMessage = null;
        if (account == null) {
            resultMessage = new ResultMessage(ResultMessage.Fail, "用户账号不能为空！");
            writer.print(resultMessage);
        }
        SysUser newUser = sysUserService.getByAccount(account);
        //SysUser newUser = processRunService.getUseridByAccount(account);
        //根据userId对用户进行判断
        if (newUser == null) {
            newUser = processRunService.getUseridByAliasname(account);
            if(newUser==null){
                resultMessage = new ResultMessage(ResultMessage.Fail, "用户不存在！");
            }
            writer.print(resultMessage);
        }
        com.suneee.eas.common.utils.ContextUtil.getSession().setAttribute(UserConstant.SESSION_USER_KEY,newUser);
        String informType = "3";
        String memo = "1";
        int backToStart = 1;

        try {
            // 追回到发起人
            resultMessage = processRunService.redos(runId, informType, memo);

        } catch (Exception ex) {
            String str = MessageUtil.getMessage();
            if (StringUtil.isNotEmpty(str)) {
                resultMessage = new ResultMessage(ResultMessage.Fail, str);
            } else {
                String message = ExceptionUtil.getExceptionMessage(ex);
                resultMessage = new ResultMessage(ResultMessage.Fail, message);
            }
        }
        //com.suneee.eas.common.utils.ContextUtil.getSession().setAttribute(UserConstant.SESSION_USER_KEY,currentUser);
        writer.print(resultMessage);
    }
}

package com.suneee.rs.controller;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.service.bpm.ProcessRunService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Controller
@RequestMapping("/platform/bpm/proRun/")
public class DelProcessController {

    @Resource
    private ProcessRunService processRunService;
    /**
     * 删除流程实例扩展
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    public ResultMessage del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultMessage message = null;
        try {
            Long[] lAryId = RequestUtil.getLongAryByStr(request, "runId");
            processRunService.delByIds(lAryId);
            message = new ResultMessage(ResultMessage.Success, "删除流程实例成功");
        } catch (Exception e) {
            message = new ResultMessage(ResultMessage.Fail, "删除流程实例失败");
        }
        return message;
    }
}

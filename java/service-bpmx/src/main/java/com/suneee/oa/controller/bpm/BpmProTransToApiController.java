package com.suneee.oa.controller.bpm;

import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmProTransTo;
import com.suneee.platform.model.bpm.BpmProTransToAssignee;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmProTransToService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * <pre>
 * 对象功能:加签（转办） 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-1-10 17:06:00
 * </pre>
 */
@Controller
@RequestMapping("/api/bpm/bpmProTransTo/")
@Action(ownermodel=SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmProTransToApiController extends BaseController {
	@Resource
	private BpmProTransToService bpmProTransToService;

	@Resource
	private ProcessRunService processRunService;



	/**
	 * 查看流转人
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("showAssignee")
	@ResponseBody
	@Action(description = "查看流转人")
	public ResultVo showAssignee(HttpServletRequest request,
								 HttpServletResponse response) throws Exception {
		String taskId  = RequestUtil.getString(request, "taskId");
		BpmProTransTo bpmProTransTo = bpmProTransToService.getByTaskId(new Long(taskId));
		if(BeanUtils.isEmpty(bpmProTransTo)){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"流转过程已结束,毋须再添加流转人!");
		}
		List<BpmProTransToAssignee> list = bpmProTransToService.getAssignee(taskId, bpmProTransTo.getAssignee());
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流转人信息成功！",list);
	}

	/**
	 * 取消流转任务
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("cancel")
	@ResponseBody
	@Action(description = "取消流转任务")
	public void cancel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out=response.getWriter();
		ResultVo resultMessage=null;
		String taskId = RequestUtil.getString(request, "taskId");
		String opinion = RequestUtil.getString(request, "opinion");
		String userId = RequestUtil.getString(request, "userId");
		String informType = RequestUtil.getString(request, "informType");
		try{
			ProcessTask processTask = processRunService.getTaskByParentIdAndUser(taskId, userId);
			if(processTask==null){
				resultMessage=new ResultVo(ResultVo.COMMON_STATUS_FAILED, "此流转任务已被审批!");
			}else{
				bpmProTransToService.cancel(processTask, opinion, informType);
				resultMessage=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "取消流转任务成功!");
			}
		}catch(Exception e){
			resultMessage=new ResultVo(ResultVo.COMMON_STATUS_FAILED, "取消流转任务失败!");
			e.printStackTrace();
		}
		out.print(resultMessage);
	}
}
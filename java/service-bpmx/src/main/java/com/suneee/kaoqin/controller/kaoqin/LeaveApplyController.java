package com.suneee.kaoqin.controller.kaoqin;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.util.SqlUtil;

import org.springframework.web.servlet.ModelAndView;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.model.kaoqin.AttendanceVacation;
import com.suneee.kaoqin.model.kaoqin.LeaveApply;
import com.suneee.kaoqin.service.kaoqin.AttendanceVacationService;
import com.suneee.kaoqin.service.kaoqin.LeaveApplyService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:请假申请 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-11 09:35:17
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/leaveApply/")
public class LeaveApplyController extends BaseController
{
	@Resource
	private LeaveApplyService leaveApplyService;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private AttendanceVacationService attendanceVacationService;
	
	/**
	 * 添加或更新请假申请。
	 * @param request
	 * @param response
	 * @param leaveApply 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新请假申请")
	public void save(HttpServletRequest request, HttpServletResponse response,LeaveApply leaveApply) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			leaveApplyService.save(leaveApply);
			if(id==0){
				resultMsg=getText("添加成功","请假申请");
			}else{
				resultMsg=getText("更新成功","请假申请");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得请假申请分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看请假申请分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<LeaveApply> list = new ArrayList<LeaveApply>();
		QueryFilter filter = new QueryFilter(request,"leaveApplyItem");
		filter.addFilter("AllSql", SqlUtil.getAll("w_leave_apply"));
		list = leaveApplyService.getAllApply(filter);
		ModelAndView mv=this.getAutoView().addObject("leaveApplyList",list);
		return mv;
	}
	
	/**
	 * 删除请假申请
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除请假申请")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			leaveApplyService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除请假申请成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑请假申请
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑请假申请")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		LeaveApply leaveApply=leaveApplyService.getById(id);
		
		return getAutoView().addObject("leaveApply",leaveApply)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得请假申请明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看请假申请明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		String instanceId = RequestUtil.getString(request, "instanceId");
		LeaveApply leaveApply = leaveApplyService.getById(id);
		List<TaskOpinion> taskOpinionList = taskOpinionService.getByActInstId(instanceId);
		return getAutoView().addObject("leaveApply", leaveApply).addObject("taskOpinionList", taskOpinionList);
	}
	
	@RequestMapping("getVacationList")
	@Action(description="请假类型")
	@ResponseBody
	public List<AttendanceVacation> getVacationList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return attendanceVacationService.getAll();
	}
	
	@RequestMapping("getVacation")
	@Action(description="请假类型")
	@ResponseBody
	public AttendanceVacation getVacation(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String id = request.getParameter("vacationId");
		return attendanceVacationService.getById(Long.parseLong(id));
	}
}


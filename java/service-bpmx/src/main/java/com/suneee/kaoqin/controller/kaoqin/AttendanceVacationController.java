package com.suneee.kaoqin.controller.kaoqin;

import java.util.Date;
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
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.kaoqin.model.kaoqin.AttendanceVacation;
import com.suneee.kaoqin.service.kaoqin.AttendanceVacationService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.service.util.CurrentContext;
/**
 *<pre>
 * 对象功能:假期类型 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 10:43:17
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/attendanceVacation/")
public class AttendanceVacationController extends BaseController
{
	@Resource
	private AttendanceVacationService attendanceVacationService;
	@Resource
	private CurrentContext currentContext;
	
	
	/**
	 * 添加或更新假期类型。
	 * @param request
	 * @param response
	 * @param attendanceVacation 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新假期类型")
	public void save(HttpServletRequest request, HttpServletResponse response,AttendanceVacation attendanceVacation) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			if(id==0){
				attendanceVacation.setCreatetime(new Date());
				attendanceVacation.setCreateby(currentContext.getCurrentUserId());
				resultMsg=getText("添加成功","假期类型");
			}else{
				resultMsg=getText("更新成功","假期类型");
			}
			attendanceVacationService.save(attendanceVacation);
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得假期类型分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看假期类型分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<AttendanceVacation> list=attendanceVacationService.getAll(new QueryFilter(request,"attendanceVacationItem"));
		ModelAndView mv=this.getAutoView().addObject("attendanceVacationList",list);
		return mv;
	}
	
	/**
	 * 删除假期类型
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除假期类型")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			attendanceVacationService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除假期类型成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑假期类型
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑假期类型")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		AttendanceVacation attendanceVacation=attendanceVacationService.getById(id);
		
		return getAutoView().addObject("attendanceVacation",attendanceVacation)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得假期类型明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看假期类型明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		AttendanceVacation attendanceVacation = attendanceVacationService.getById(id);	
		return getAutoView().addObject("attendanceVacation", attendanceVacation);
	}
	
	
	/**
	 * 添加或更新假期类型。
	 * @param request
	 * @param response
	 * @param attendanceVacation 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("checkRepeat")
	@Action(description="检查假期名称是否重复")
	public void checkRepeat(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");	
		String subject = RequestUtil.getString(request, "subject");		
		Boolean flag = attendanceVacationService.checkRepeat(id, subject);
		if(flag == true){
			resultMsg = "假期名称重复";
		}
		writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
		
	}
}


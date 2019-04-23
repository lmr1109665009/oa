package com.suneee.kaoqin.controller.kaoqin;

import java.util.HashMap;
import java.util.Map;
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
import com.suneee.platform.annotion.Action;
import org.springframework.web.servlet.ModelAndView;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.util.StringUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import com.suneee.platform.model.system.SysUser;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import com.suneee.core.util.StringUtil;

import com.suneee.kaoqin.model.kaoqin.WorkingCalendar;
import com.suneee.kaoqin.service.kaoqin.WorkingCalendarService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:工作日历 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:12:02
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/workingCalendar/")
public class WorkingCalendarController extends BaseController
{
	@Resource
	private WorkingCalendarService workingCalendarService;
	
	
	/**
	 * 添加或更新工作日历。
	 * @param request
	 * @param response
	 * @param workingCalendar 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新工作日历")
	public void save(HttpServletRequest request, HttpServletResponse response,WorkingCalendar workingCalendar) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			workingCalendarService.save(workingCalendar);
			if(id==0){
				resultMsg=getText("添加成功","工作日历");
			}else{
				resultMsg=getText("更新成功","工作日历");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得工作日历分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看工作日历分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<WorkingCalendar> list=workingCalendarService.getAll(new QueryFilter(request,"workingCalendarItem"));
		ModelAndView mv=this.getAutoView().addObject("workingCalendarList",list);
		return mv;
	}
	
	/**
	 * 删除工作日历
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除工作日历")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			workingCalendarService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除工作日历成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑工作日历
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑工作日历")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		WorkingCalendar workingCalendar=workingCalendarService.getById(id);
		
		return getAutoView().addObject("workingCalendar",workingCalendar)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得工作日历明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看工作日历明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		WorkingCalendar workingCalendar = workingCalendarService.getById(id);	
		return getAutoView().addObject("workingCalendar", workingCalendar);
	}
	
}


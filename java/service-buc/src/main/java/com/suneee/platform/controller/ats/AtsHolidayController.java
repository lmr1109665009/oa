package com.suneee.platform.controller.ats;

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

import com.suneee.platform.model.ats.AtsHoliday;
import com.suneee.platform.service.ats.AtsHolidayService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:考勤请假单 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-07-15 14:59:52
 *</pre>
 */
@Controller
@RequestMapping("/platform/ats/atsHoliday/")
public class AtsHolidayController extends BaseController
{
	@Resource
	private AtsHolidayService atsHolidayService;
	
	
	/**
	 * 添加或更新考勤请假单。
	 * @param request
	 * @param response
	 * @param atsHoliday 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新考勤请假单")
	public void save(HttpServletRequest request, HttpServletResponse response,AtsHoliday atsHoliday) throws Exception
	{
		String resultMsg=null;		
		try{
			if(atsHoliday.getId()==null||atsHoliday.getId()==0){
				atsHolidayService.save(atsHoliday);
				resultMsg=getText("添加","考勤请假单");
			}else{
			    atsHolidayService.save(atsHoliday);
				resultMsg=getText("更新","考勤请假单");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得考勤请假单分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看考勤请假单分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<AtsHoliday> list=atsHolidayService.getAll(new QueryFilter(request,"atsHolidayItem"));
		ModelAndView mv=this.getAutoView().addObject("atsHolidayList",list);
		return mv;
	}
	
	/**
	 * 删除考勤请假单
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除考勤请假单")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			atsHolidayService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除考勤请假单成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑考勤请假单
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑考勤请假单")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		AtsHoliday atsHoliday=atsHolidayService.getById(id);
		
		return getAutoView().addObject("atsHoliday",atsHoliday)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得考勤请假单明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看考勤请假单明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		AtsHoliday atsHoliday = atsHolidayService.getById(id);	
		return getAutoView().addObject("atsHoliday", atsHoliday);
	}
	
}


package com.suneee.kaoqin.controller.kaoqin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
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
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.util.CurrentContext;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.kaoqin.model.kaoqin.AttendanceVacation;
import com.suneee.kaoqin.model.kaoqin.VacationLog;
import com.suneee.kaoqin.model.kaoqin.VacationRemain;
import com.suneee.kaoqin.service.kaoqin.AttendanceVacationService;
import com.suneee.kaoqin.service.kaoqin.VacationLogService;
import com.suneee.kaoqin.service.kaoqin.VacationRemainService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:假期结余 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:32:12
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/vacationRemain/")
public class VacationRemainController extends BaseController
{
	@Resource
	private VacationRemainService vacationRemainService;
	@Resource
	private SysUserService userService;
	@Resource
	private AttendanceVacationService attendanceVacationService;
	@Resource
	private CurrentContext currentContext;
	@Resource
	private VacationLogService vacationLogService;
	
	/**
	 * 添加或更新假期结余。
	 * @param request
	 * @param response
	 * @param vacationRemain 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新假期结余")
	public void save(HttpServletRequest request, HttpServletResponse response,VacationRemain vacationRemain) throws Exception
	{
		String resultMsg=null;
		long id = RequestUtil.getLong(request, "id");
		Date date = new Date();
		try{
			VacationRemain oldVacationRemain = vacationRemainService.getById(id);
			VacationLog vacationLog = new VacationLog();
			vacationLog.setVacationType(vacationRemain.getVacationType());
			vacationLog.setUserId(vacationRemain.getUserId());
			vacationLog.setChangeType(VacationLog.TYPE_MODIFY);
			vacationLog.setChangeValue(vacationRemain.getChangeValue());
			vacationLog.setMemo(vacationRemain.getMemo());
			vacationLog.setStatus((short)0);
			vacationLog.setUpdateBy(currentContext.getCurrentUserId());
			vacationLog.setUpdatetime(date);
			if(oldVacationRemain == null){
				vacationLog.setBeforeValue(0L);
			}else{
				vacationLog.setBeforeValue(oldVacationRemain.getRemained());
			}
			if(id == 0){
				resultMsg=getText("添加成功","假期结余");
				vacationRemain.setRemained(vacationRemain.getChangeValue());
				vacationRemainService.save(vacationRemain);
				vacationLog.setAfterValue(vacationRemain.getRemained());
			}else{
				resultMsg=getText("更新成功","假期结余");
				oldVacationRemain.setRemained(oldVacationRemain.getRemained() + vacationRemain.getChangeValue());
				if (vacationRemain.getValidDate() != null) {
					oldVacationRemain.setValidDate(vacationRemain.getValidDate());
				}
				vacationRemainService.save(oldVacationRemain);
				vacationLog.setAfterValue(oldVacationRemain.getRemained());
			}
			if (vacationRemain.getValidDate() != null) {
				vacationLog.setValidDate(vacationRemain.getValidDate());
			}
			vacationLogService.save(vacationLog);
			vacationRemain.setUpdatetime(date);
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得假期结余分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看假期结余分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<VacationRemain> list=vacationRemainService.getAll(new QueryFilter(request,"vacationRemainItem"));
		for (VacationRemain vacationRemain : list) {
			String[] remainedTimes = vacationRemain.getRemainedTime().split(",");
			for (String rt : remainedTimes) {
				vacationRemain.getRemainedTimes().add(rt);
			}
		}
		List<AttendanceVacation> attendanceVacationList =  attendanceVacationService.getAllByRemain();
		ModelAndView mv=this.getAutoView();
		mv.addObject("vacationRemainList",list);
		mv.addObject("attendanceVacationList", attendanceVacationList);
		addQueryFieldToView(mv, request);
		return mv;
	}
	
	/**
	 * 删除假期结余
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除假期结余")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			vacationRemainService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除假期结余成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑假期结余
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑假期结余")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long userId = RequestUtil.getLong(request,"userId",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		SysUser user = userService.getById(userId);
		List<AttendanceVacation> attendanceVacationList = attendanceVacationService.getAll();
		return getAutoView().addObject("user",user)
							.addObject("attendanceVacationList", attendanceVacationList)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得假期结余明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看假期结余明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long userId=RequestUtil.getLong(request,"userId");
		// 获取用户的姓名
		SysUser user = userService.getById(userId);
		List<VacationRemain> vacationRemainList = vacationRemainService.getByUserId(userId);
		Calendar end = Calendar.getInstance();
		end.set(Calendar.DAY_OF_YEAR, end.getActualMaximum(Calendar.DAY_OF_YEAR));
		for (VacationRemain remain : vacationRemainList) {
			if (remain.getValidDate() == null) {
				remain.setValidDate(end.getTime());
			}
		}
		return getAutoView().addObject("vacationRemainList", vacationRemainList)
							.addObject("user", user);
	}
	
	/**
	 * 通过userId和假期类型获取假期结余
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("queryRemain")
	@Action(description="获取假期结余")
	@ResponseBody
	public VacationRemain queryRemain(HttpServletRequest request, HttpServletResponse response)  throws Exception{
		long userId = RequestUtil.getLong(request, "userId");
		long vacationTypeId = RequestUtil.getLong(request, "vacationTypeId");
		VacationRemain vacationRemain = vacationRemainService.getByUserIdAndType(userId,vacationTypeId);
		return vacationRemain;
	}
	
	protected void addQueryFieldToView(ModelAndView view, HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements()) {
			String name = names.nextElement();
			view.addObject(name, request.getParameter(name));
		}
	}
}


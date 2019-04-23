package com.suneee.kaoqin.controller.kaoqin;

import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.DateUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.kaoqin.model.kaoqin.AttendanceResult;
import com.suneee.kaoqin.model.kaoqin.ViewModel;
import com.suneee.kaoqin.service.kaoqin.AttendanceResultService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.SysOrgService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
/**
 *<pre>
 * 对象功能:考勤结果表 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-04 10:48:13
 *</pre>
 */
@Controller
@RequestMapping("/kaoqin/kaoqin/attendanceResult/")
public class AttendanceResultController extends BaseController
{
	@Resource
	private AttendanceResultService attendanceResultService;
	@Resource
	private SysOrgService orgService;
	
	
	/**
	 * 添加或更新考勤结果表。
	 * @param request
	 * @param response
	 * @param attendanceResult 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新考勤结果表")
	public void save(HttpServletRequest request, HttpServletResponse response,AttendanceResult attendanceResult) throws Exception
	{
		String resultMsg=null;
		Long resultId = RequestUtil.getLong(request, "resultId");
		try{
			attendanceResultService.save(attendanceResult);
			if(resultId==0){
				resultMsg=getText("添加成功","考勤结果表");
			}else{
				resultMsg=getText("更新成功","考勤结果表");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得考勤结果表分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看考勤结果表分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<AttendanceResult> list=attendanceResultService.getAll(new QueryFilter(request,"attendanceResultItem"));
		ModelAndView mv=this.getAutoView().addObject("attendanceResultList",list);
		return mv;
	}
	
	/**
	 * 删除考勤结果表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除考勤结果表")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "resultId");
			attendanceResultService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除考勤结果表成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑考勤结果表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑考勤结果表")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long resultId=RequestUtil.getLong(request,"resultId",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		AttendanceResult attendanceResult=attendanceResultService.getById(resultId);
		
		return getAutoView().addObject("attendanceResult",attendanceResult)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得考勤结果表明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看考勤结果表明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long resultId=RequestUtil.getLong(request,"resultId");
		AttendanceResult attendanceResult = attendanceResultService.getById(resultId);	
		return getAutoView().addObject("attendanceResult", attendanceResult);
	}
	
	@RequestMapping("dayList")
	@Action(description="查看打卡记录列表")
	public ModelAndView dayList(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<AttendanceResult> list=attendanceResultService.getDayList(new QueryFilter(request,"attendanceResultItem"));
		for (AttendanceResult result : list) {
			if (result.getCheckTime() == null) {
				continue;
			}
			String times[] = result.getCheckTime().split(",");
			for (int i=0;i<times.length;i++) {
				String time = times[i];
				String vs[] = time.split("_");
				ViewModel vm = new ViewModel();
				vm.setValue(vs[0]);
				vm.setType(i%2);
				vm.setState(Integer.valueOf(vs[1]));
				if (vm.getState() == 3) {
					vm.setValue("未登记");
				}
				result.getCheckTimes().add(vm);
			}
		}
		ModelAndView mv=this.getAutoView();
		mv.addObject("attendanceResultList",list);
		addQueryFieldToView(mv, request);
		return mv;
	}
	
	@RequestMapping("daySummaryList")
	@Action(description="查看考勤日统计列表")
	public ModelAndView daySummaryList(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(request,"attendanceResultItem");
		List<AttendanceResult> list=attendanceResultService.getSummaryList(filter);
		List<SysOrg> orgs = orgService.getOrgsByDemIdOrAll(0L);
		ModelAndView mv=this.getAutoView();
		mv.addObject("attendanceResultList",list);
		mv.addObject("orgs",orgs);
		addQueryFieldToView(mv, request);
		return mv;
	}
	
	@RequestMapping("userDetailList")
	@Action(description="查看用户考勤统计列表")
	public ModelAndView userDetailList(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(request,"attendanceResultItem");
		List<AttendanceResult> list=attendanceResultService.getUserDetailList(filter);
		int maxTime = 1; // 一天最多的打卡时间段
		for (AttendanceResult result : list) {
			int stage = result.getTimes() / 2;
			if (stage > maxTime) {
				maxTime = stage;
			}
			if (result.getCheckTime() == null) {
				continue;
			}
			String times[] = result.getCheckTime().split(",");
			for (int i=0;i<times.length;i++) {
				String time = times[i];
				String vs[] = time.split("_");
				ViewModel vm = new ViewModel();
				vm.setValue(vs[0]);
				vm.setType(i%2);
				vm.setState(Integer.valueOf(vs[1]));
				result.getCheckTimes().add(vm);
			}
		}
		ModelAndView mv=this.getAutoView();
		mv.addObject("maxTime", maxTime);
		mv.addObject("attendanceResultList",list);
		return mv;
	}
	
	@RequestMapping("overtimeList")
	@Action(description="查看加班记录列表")
	public ModelAndView overtimeList(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(request,"attendanceResultItem");
		List<AttendanceResult> list=attendanceResultService.getOvertimeList(filter);
		for (AttendanceResult result : list) {
			ViewModel vm1 = new ViewModel();
			result.getCheckTimes().add(vm1);
			ViewModel vm2 = new ViewModel();
			result.getCheckTimes().add(vm2);
			if (result.getCheckTime() != null) {
				String times[] = result.getCheckTime().split(",");
				Date start = null;
				if (times.length > 0) {
					if (StringUtils.isNotEmpty(times[0])) {
						start = DateFormatUtil.parse(times[0], "yyyy-MM-dd HH:mm");
						vm1.setValue(times[0]);
					}
				}
				if (times.length > 1) {
					if (StringUtils.isNotEmpty(times[1])) {
						Date end = DateFormatUtil.parse(times[1], "yyyy-MM-dd HH:mm");
						vm2.setValue(times[1]);
						if (start != null) {
							Double hours = DateUtil.betweenHour(start, end);
							long baseTime = 8;
							if (result.getOverTime() != null) {
								baseTime = result.getOverTime();
							}
							if(hours < baseTime) {
								result.setOverTime(hours.longValue());
							}
						}
					}
				}
			}
		}
		ModelAndView mv=this.getAutoView();
		mv.addObject("attendanceResultList",list);
		addQueryFieldToView(mv, request);
		return mv;
	}
	
	protected void addQueryFieldToView(ModelAndView view, HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements()) {
			String name = names.nextElement();
			view.addObject(name, request.getParameter(name));
		}
	}
	
}


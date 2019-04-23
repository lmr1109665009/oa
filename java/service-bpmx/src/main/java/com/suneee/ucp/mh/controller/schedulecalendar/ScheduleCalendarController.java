package com.suneee.ucp.mh.controller.schedulecalendar;

import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.schedulecalendar.ScheduleCalendar;
import com.suneee.ucp.mh.service.schedulecalendar.ScheduleCalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 *<pre>
 * 对象功能:日程表 控制器类
 * 开发公司:深圳象翌微链股份有限公司
 * 开发人员:xiongxianyun
 * 创建时间:2017-06-26 13:36:34
 *</pre>
 */
@Controller
@RequestMapping("/mh/schedulecalendar/scheduleCalendar/")
public class ScheduleCalendarController extends UcpBaseController
{
	@Resource
	private ScheduleCalendarService scheduleCalendarService;
	
	
	/**
	 * 添加或更新日程表。
	 * @param request
	 * @param response
	 * @param scheduleCalendar 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新日程表")
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response,ScheduleCalendar scheduleCalendar) throws Exception
	{
		try {
			scheduleCalendarService.save(scheduleCalendar);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "日程信息保存成功！", scheduleCalendar.getId());
		} catch (Exception e) {
			logger.error("保存日程信息失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "日程信息保存失败！", scheduleCalendar.getId());
		}
	}
	
	
	/**
	 * 取得日程表分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看日程表列表")
	@ResponseBody
	public ResultVo list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		// 请求参数校验
		Date startDate = RequestUtil.getDate(request, "startDate");
		Date endDate = RequestUtil.getDate(request, "endDate");
		if(startDate == null || endDate == null){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误");
		}
		
		try {
			// 查询日程列表
			List<ScheduleCalendar> list = scheduleCalendarService.getListBy(startDate, endDate);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "查询日程列表成功", list);
		} catch (Exception e) {
			logger.error("获取日程列表失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "查询日程列表失败");
		}
	}
	
	/**
	 * 删除日程表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除日程表")
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// 请求参数校验
		Long id = RequestUtil.getLong(request, "scheduleId", 0L);
		if(id == 0){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误");
		}
		
		try{
			// 根据日程ID删除日程信息
			scheduleCalendarService.delBy(id);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除日程成功!");
		}catch(Exception ex){
			logger.error("删除日程失败", ex);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除日程失败！");
		}
	}

	/**
	 * 取得日程表明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看日程表明细")
	@ResponseBody
	public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// 请求参数校验
		Long id = RequestUtil.getLong(request, "id", 0L);
		if(id == 0){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误");
		}
		try {
			// 根据日程ID获取日程详情
			ScheduleCalendar scheduleCalendar = scheduleCalendarService.getBy(id);	
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取日程详情成功！", scheduleCalendar);
		} catch (Exception e) {
			logger.error("获取日程详情失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取日程详情失败！");
		}
	}
	
}


/**
 * @Title: ConferenceReserveApiController.java
 * @Package com.suneee.eas.oa.controller.conference
 * @Description: TODO(用一句话描述该文件做什么)
 */
package com.suneee.eas.oa.controller.conference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.DateUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.exception.conference.AlreadyEndedException;
import com.suneee.eas.oa.exception.conference.AlreadyStartedException;
import com.suneee.eas.oa.exception.conference.ConferenceDeviceNotAvailableException;
import com.suneee.eas.oa.exception.conference.ConferenceTimeConflictException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.NotStartedException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.AccessRecord;
import com.suneee.eas.oa.model.conference.ConferenceReserve;
import com.suneee.eas.oa.model.conference.ConferenceRoom;
import com.suneee.eas.oa.service.conference.AccessRecordService;
import com.suneee.eas.oa.service.conference.ConferenceDeviceService;
import com.suneee.eas.oa.service.conference.ConferenceReserveDeviceService;
import com.suneee.eas.oa.service.conference.ConferenceReserveService;
import com.suneee.eas.oa.service.conference.ConferenceRoomService;
import com.suneee.platform.calendar.util.CalendarUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.thread.MessageUtil;

/**
 * @ClassName: ConferenceReserveApiController
 * @Description: 会议预定模块相关功能
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 13:38:49
 *
 */
@RestController
@RequestMapping((ModuleConstant.CONFERENCE_MODULE + FunctionConstant.CONFERENCE_RESERVE))
public class ConferenceReserveApiController {
	private static final Logger LOGGER = LogManager.getLogger(ConferenceReserveApiController.class);
	@Autowired
	private ConferenceReserveService conferenceReserveService;
	@Autowired
	private AccessRecordService accessRecordService;
	@Autowired
	private ConferenceRoomService conferenceRoomService;
	@Autowired
	private ConferenceReserveDeviceService conferenceReserveDeviceService;
	@Autowired
	private ConferenceDeviceService conferenceDeviceService;

	/** 会议室预定
	 * @param request
	 * @param response
	 * @param conferenceReserve
	 * @return
	 */
	@RequestMapping("reserve")
	public ResponseMessage reserve(HttpServletRequest request, HttpServletResponse response, ConferenceReserve conferenceReserve){
			String message = null;
			JSONObject data = null;
			try {
				Date startDate = RequestUtil.getDate(request, "sdate", DateUtil.FORMAT_DATE);
				Date endDate = RequestUtil.getDate(request, "edate", DateUtil.FORMAT_DATE);
				String startTime = RequestUtil.getString(request, "stime", null);
				String endTime = RequestUtil.getString(request, "etime", null);
				Byte cycType = RequestUtil.getByte(request, "cycType", null);
				Boolean isIgnoreConflict = RequestUtil.getBoolean(request, "isIgnoreConflict", false);
				// 非周期性
				if(ConferenceReserve.CYC_NO.equals(cycType)){
					// 预定会议室
					conferenceReserveService.reserve(conferenceReserve, startDate, startTime, endTime, isIgnoreConflict);
				}
				// 按周
				else if(ConferenceReserve.CYC_WEEK.equals(cycType)){
					// 获取开始日期和结束日期之间指定星期的日期
					String weeks = RequestUtil.getString(request, "weeks");
					List<Date> dateList = CalendarUtil.getDateOfWeek(startDate, endDate, weeks);
					// 预定会议室
					conferenceReserveService.reserve(conferenceReserve, dateList, startTime, endTime, isIgnoreConflict);
				}
				// 按月
				else if(ConferenceReserve.CYC_MONTH.equals(cycType)){
					// 获取开始日期和结束日期之间指定的日期
					String dates = RequestUtil.getString(request, "dates");
					List<Date> dateList = CalendarUtil.getDateOfMonth(startDate, endDate, dates);
					// 预定会议室
					conferenceReserveService.reserve(conferenceReserve, dateList, startTime, endTime, isIgnoreConflict);
				} else {
					return ResponseMessage.fail("预定会议室失败：不支持的周期类型" + cycType);
				}
				return ResponseMessage.success("预定会议室成功！");
			} catch (ParseException e) {
				LOGGER.error("预定会议室失败：" + e.getMessage(), e);
				message = "会议日期或时间格式错误！";
			} catch (ConferenceTimeConflictException e) {
				LOGGER.error("预定会议室失败：" + e.getMessage(), e);
				message = "会议时间冲突！";
				data = new JSONObject();
				List<String> msgs = MessageUtil.getMsg();
				data.put("cycType", conferenceReserve.getCycType());
				data.put("startTime", msgs.get(0));
				data.put("endTime", msgs.get(1));
			} catch (ConferenceDeviceNotAvailableException e) {
				LOGGER.error("预定会议室失败：" + e.getMessage(), e);
				message = "选择会议设备可用库存不足！";
			} catch(IllegalArgumentException e){
				LOGGER.error("保存会议纪要失败：" + e.getMessage(), e);
				message = "接口请求参数错误！";
			} catch (Exception e) {
				LOGGER.error("预定会议室失败：" + e.getMessage(), e);
				message = "系统内部错误！";
			}
			return ResponseMessage.fail("预定会议室失败：" + message, data);

	}

	/** 编辑会议信息
	 * @param request
	 * @param response
	 * @param conferenceReserve
	 * @return
	 */
	@RequestMapping("edit")
	public ResponseMessage editConference(HttpServletRequest request, HttpServletResponse response, ConferenceReserve conferenceReserve){
		String message = null;
		JSONObject data = null;
		try {
			Date startDate = RequestUtil.getDate(request, "sdate", DateUtil.FORMAT_DATE);
			String startTime = RequestUtil.getString(request, "stime", null);
			String endTime = RequestUtil.getString(request, "etime", null);
			conferenceReserveService.update(conferenceReserve, startDate, startTime, endTime);
			return ResponseMessage.success("修改会议信息成功！");
		} catch (ParseException e) {
			LOGGER.error("修改会议信息失败：" + e.getMessage(), e);
			message = "会议日期或时间格式错误！";
		}  catch (IllegalStatusException e) {
			LOGGER.error("修改会议信息失败：" + e.getMessage(), e);
			message = "当前状态下不可修改！";
		} catch (UserPermissionException e) {
			LOGGER.error("修改会议信息失败：" + e.getMessage(), e);
			message = "您不是会议发起人，不可修改会议！";
		} catch (ConferenceTimeConflictException e) {
			LOGGER.error("修改会议信息失败：" + e.getMessage(), e);
			message = "会议时间冲突！";
			data = new JSONObject();
			List<String> msgs = MessageUtil.getMsg();
			data.put("cycType", conferenceReserve.getCycType());
			data.put("startTime", msgs.get(0));
			data.put("endTime", msgs.get(1));
		} catch (ConferenceDeviceNotAvailableException e) {
			LOGGER.error("修改会议信息失败：" + e.getMessage(), e);
			message = "选择会议设备可用库存不足！";
		} catch (AlreadyStartedException e) {
			LOGGER.error("修改会议信息失败：" + e.getMessage(), e);
			message = "会议已开始！";
		}catch(IllegalArgumentException e){
			LOGGER.error("修改会议信息失败：" + e.getMessage(), e);
			message = "接口请求参数错误！";
		} catch (Exception e) {
			LOGGER.error("修改会议信息失败：" + e.getMessage(), e);
			message = "系统内部错误！";
		}
		return ResponseMessage.fail("修改会议信息失败：" + message, data);
	}

	/** 获取会议详情
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("details")
	public ResponseMessage details(HttpServletRequest request, HttpServletResponse response){
		try {
			Long conferenceId = RequestUtil.getLong(request, "conferenceId", null);
			if(conferenceId == null){
				LOGGER.error("获取会议详情失败：会议ID为空");
				return ResponseMessage.fail("获取会议详情失败：请求参数错误！");
			}

			SysUser user = ContextSupportUtil.getCurrentUser();
			accessRecordService.save(user.getUserId(), ContextSupportUtil.getCurrentUsername(), conferenceId, AccessRecord.TARGET_TYPE_CONFERENCE);
			Map<String, Object> conferenceReserve = conferenceReserveService.getReserveDetails(conferenceId);
			return ResponseMessage.success("获取会议详情成功！", conferenceReserve);
		} catch (Exception e) {
			LOGGER.error("获取会议详情失败：" + e, e);
			return ResponseMessage.fail("获取会议详情失败：系统内部错误");
		}
	}

	/** 获取可预订会议室信息列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("availableRoomList")
	public ResponseMessage availableRoomList(HttpServletRequest request, HttpServletResponse response){
		String message = null;
		try {
			Long region = RequestUtil.getLong(request, "region", null);
			List<ConferenceRoom> roomList = conferenceRoomService.getAvailableRoom(region);
			return ResponseMessage.success("获取可预订会议室列表成功！", roomList);
		} catch(IllegalArgumentException e) {
			LOGGER.error("获取可预订会议室列表失败：" + e.getMessage(), e);
			message = "请求参数错误！";
		} catch (Exception e) {
			LOGGER.error("获取可预订会议室列表失败：" + e.getMessage(), e);
			message = "系统内部错误！";
		}
		return ResponseMessage.fail("获取可预订会议室列表失败：" + message);
	}

	/** 获取可预定会议设备类型列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("availableDeviceTypeList")
	public ResponseMessage availableDeviceTypeList(HttpServletRequest request, HttpServletResponse response){
		String message = null;
		try{
			Long region = RequestUtil.getLong(request, "region", null);
			Date startTime = RequestUtil.getDate(request, "startTime", DateUtil.FORMAT_DATETIME);
			Date endTime = RequestUtil.getDate(request, "endTime", DateUtil.FORMAT_DATETIME);
			Long conferenceId = RequestUtil.getLong(request, "conferenceId", null);
			List<Map<String, Object>> deviceTypeList = conferenceReserveDeviceService.getAvailableDeviceNumber(conferenceId, region, startTime, endTime);
			return ResponseMessage.success("获取可预定会议设备类型列表成功！", deviceTypeList);
		} catch(ParseException e) {
			LOGGER.error("获取可预定会议设备类型列表失败：" + e.getMessage(), e);
			message = "请求参数格式错误！";
		} catch (IllegalArgumentException e) {
			LOGGER.error("获取可预定会议设备类型列表失败：" + e.getMessage(), e);
			message = "请求参数错误！";
		} catch(Exception e){
			LOGGER.error("获取可预定会议设备类型列表失败：" + e.getMessage(), e);
			message = "系统内部错误！";
		}
		return ResponseMessage.fail("获取可预定会议设备类型列表失败：" + message);
	}
	
	/** 获取系统会议设备类型列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("deviceTypeList")
	public ResponseMessage deviceTypeList(HttpServletRequest request, HttpServletResponse response){
		try {
			Long region = RequestUtil.getLong(request, "region", null);
			List<Map<String, Object>> deviceTypeList = conferenceDeviceService.countAvailableDeviceNumber(region);
			return ResponseMessage.success("获取会议设备类型列表成功！", deviceTypeList);
		} catch (Exception e) {
			LOGGER.error("获取会议设备类型列表失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取会议设备类型列表失败：系统内部错误");
		}
	}
	
	/** 取消会议
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("cancel")
	public ResponseMessage cancel(HttpServletRequest request, HttpServletResponse response){
		String message = null;
		try {
			Long conferenceId = RequestUtil.getLong(request, "conferenceId", null);
			this.conferenceReserveService.cancel(conferenceId);
			return ResponseMessage.success("取消会议成功！");
		} catch (AlreadyStartedException e) {
			LOGGER.error("取消会议失败：" + e.getMessage(), e);
			message = "会议已开始！";
		} catch (UserPermissionException e) {
			LOGGER.error("取消会议失败：" + e.getMessage(), e);
			message = "您不是会议发起人，不能取消会议！";
		} catch (IllegalArgumentException e) {
			LOGGER.error("取消会议失败：" + e.getMessage(), e);
			message = "请求参数错误！";
		}catch (Exception e) {
			LOGGER.error("取消会议失败：" + e.getMessage(), e);
			message = "系统内部错误！";
		}
		return ResponseMessage.fail("取消会议失败：" + message);
	}
	
	/** 提前结束会议
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("finish")
	public ResponseMessage finish(HttpServletRequest request, HttpServletResponse response){
		String message = null;
		try {
			Long conferenceId = RequestUtil.getLong(request, "conferenceId", null);
			this.conferenceReserveService.finish(conferenceId);
			return ResponseMessage.success("提前结束会议成功！");
		} catch (NotStartedException e) {
			LOGGER.error("提前结束会议失败：" + e.getMessage(), e);
			message = "会议还未开始！";
		} catch (AlreadyEndedException e) {
			LOGGER.error("提前结束会议失败：" + e.getMessage(), e);
			message = "会议已经结束";
		} catch (UserPermissionException e) {
			LOGGER.error("提前结束会议失败：" + e.getMessage(), e);
			message = "您不是会议发起人！";
		} catch (Exception e) {
			LOGGER.error("提前结束会议失败：" + e.getMessage(), e);
			message = "系统内部错误！";
		}
		return ResponseMessage.fail("提前结束会议失败：" + message);
	}
	
	/** 获取会议预定列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("list")
	public ResponseMessage list(HttpServletRequest request, HttpServletResponse response){
		String message = null;
		try {
			String regions = RequestUtil.getString(request, "regions");
			Date startDate = RequestUtil.getDate(request, "startDate","yyyy-MM-dd");
			Date endDate = RequestUtil.getDate(request, "endDate","yyyy-MM-dd");
			Date startTime = CalendarUtil.getDateBeginTime(startDate);
			Date endTime = CalendarUtil.getDateEndTime(endDate);
			if(BeanUtils.isEmpty(regions)){
				return ResponseMessage.fail("请先选择会议区域！");
			}
			//所选区域的ID集合
			String[] regionList = regions.split(",");
			//用于存放用户所能使用会议室
			ArrayList<ConferenceRoom> roomList = new ArrayList<>();
			//获取当前用户所能使用会议室
			for(String region:regionList){
				List<ConferenceRoom> availableRoom = conferenceRoomService.getAvailableRoom(Long.valueOf(region));
				for(ConferenceRoom cr:availableRoom){
					roomList.add(cr);
				}
			}
			//查询所有会议预定记录
			List<Map<String,Object>> resultList = new ArrayList<>();
			//创建查询对象
			QueryFilter queryFilter = new QueryFilter("listAllConRes");
			//封装查询参数
			Map<String,Object> params = new HashMap<>();
			params.put("startTime",startTime);
			params.put("endTime",endTime);
			params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			//遍历所有会议室，查询对应会议预定记录
			for(ConferenceRoom cr:roomList){
				params.put("roomId",cr.getRoomId());
				queryFilter.setFilters(params);
				//查询当前会议室符合条件的所有会议预定记录
				List<ConferenceReserve> conferenceReserveList = conferenceReserveService.getListBySqlKey(queryFilter);
				Map<String,Object> crMap = new HashMap<>();
				crMap.put("room",cr);
				crMap.put("conferenceReserveList",conferenceReserveList);
				resultList.add(crMap);
			}
			return ResponseMessage.success("获取会议预定列表成功！", resultList);
		} catch (ParseException e) {
			LOGGER.error("获取会议预定列表失败：" + e.getMessage(), e);
			message = "请求参数格式错误！";
		} catch (IllegalArgumentException e) {
			LOGGER.error("获取会议预定列表失败：" + e.getMessage(), e);
			message = "请求参数错误！";
		} catch (Exception e) {
			LOGGER.error("获取会议预定列表失败：" + e.getMessage(), e);
			message = "系统内部错误！";
		}
		return ResponseMessage.fail("获取会议预定列表失败：" + message);
	}

	/**
	 * 我的预定
	 * @param request
	 * @return
	 */
	@RequestMapping("myReserve")
	public ResponseMessage myReserve(HttpServletRequest request){
		QueryFilter queryFilter = new QueryFilter("myReserve",request);
		try {
			Map<String, Object> filters = queryFilter.getFilters();
			filters.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			filters.put("userId", ContextSupportUtil.getCurrentUserId());
			//用户会议状态高级搜索使用
			if(BeanUtils.isNotEmpty(filters.get("conStatus"))){
				filters.put("currentDate",new Date());
			}
			Pager<ConferenceReserve> page = conferenceReserveService.getPageBySqlKey(queryFilter);
			return ResponseMessage.success("获取我的预定列表数据成功！",page);
		}catch (Exception ex){
			LOGGER.error("获取我的预定列表数据失败:"+ex.getMessage());
			return ResponseMessage.fail("获取我的预定列表数据失败！");
		}
	}

	/** 查询会议
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("pageListAll")
	public ResponseMessage pageListAll(HttpServletRequest request, HttpServletResponse response){
		int status = RequestUtil.getInt(request, "status");
		QueryFilter queryFilter = new QueryFilter("pageListAll",request);
		String message = null;
		try {
			switch (status){
				case 0:
					message = "全部会议查询成功！";
					break;
				case 2:
					message = "待审批会议查询成功！";
					break;
				case 4:
					message = "已批准会议查询成功！";
					break;
				case 5:
					message = "未批准会议查询成功！";
					break;
			}
			if(status != 0){
				queryFilter.getFilters().put("auditStatus",status);
			}
			queryFilter.getFilters().put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			Pager<ConferenceReserve> page = conferenceReserveService.getPageBySqlKey(queryFilter);
			return ResponseMessage.success(message, page);
		}catch (Exception ex){
			switch (status){
				case 0:
					message = "全部会议查询失败！";
					break;
				case 2:
					message = "待审批会议查询失败！";
					break;
				case 4:
					message = "已批准会议查询失败！";
					break;
				case 5:
					message = "未批准会议查询失败！";
					break;
			}
			LOGGER.error(message+","+ex.getMessage());
			return ResponseMessage.fail(message);
		}
	}

	/** 我的会议
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("myPageList")
	public ResponseMessage myPageList(HttpServletRequest request, HttpServletResponse response){
		int hasOpen = RequestUtil.getInt(request,"hasOpen");
		QueryFilter queryFilter = new QueryFilter("myPageList",request);
		String message = null;
		try {
			Map<String, Object> filters = queryFilter.getFilters();
			filters.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			filters.put("userId", ContextSupportUtil.getCurrentUserId());
			//用户会议状态高级搜索使用
			Date date = new Date();
			if(BeanUtils.isNotEmpty(filters.get("conStatus"))){
				filters.put("currentDate",date);
			}
			if(hasOpen == 0){
				//待开会议
				filters.put("readyOpenTime",date);
				message = "获取待开会议列表成功！";
			}else if(hasOpen == 1) {
				//已开会议
				filters.put("hasOpenTime",date);
				message = "获取已开会议列表成功!";
			}else if (hasOpen == 2){
				//全部会议
				message = "获取全部会议列表成功!";
			}
			if(hasOpen != 2 ){
				Pager<ConferenceReserve> page = conferenceReserveService.getPageBySqlKey(queryFilter);
				return ResponseMessage.success(message,page);
			}else {
				List<ConferenceReserve> list = conferenceReserveService.getListBySqlKey(queryFilter);
				return ResponseMessage.success(message,list);
			}
		}catch (Exception ex){
			if(hasOpen == 0){
				message = "获取待开会议列表失败！";
			}else if(hasOpen == 1) {
				message = "获取已开会议列表成功!";
			}else if (hasOpen == 2){
				message = "获取全部会议列表成功!";
			}
			LOGGER.error(message+","+ex.getMessage());
			return ResponseMessage.fail(message);
		}
	}

	/**
	 * 我的审批（会议）
	 * @param request
	 * @return
	 */
	@RequestMapping("myApprovedConference")
	public ResponseMessage myApproveConference(HttpServletRequest request){
		QueryFilter queryFilter = new QueryFilter("myApproveConference",request);
		int status = RequestUtil.getInt(request, "status");
		String message = null;
		try {
			if(status == 2 ){ //待审批
				message = "待审批会议列表数据查询成功！";
			}else if (status == 4){ //已审批
				message = "已审批会议列表数据查询成功！";
			}
			Map<String, Object> filters = queryFilter.getFilters();
			filters.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			filters.put("userId", ContextSupportUtil.getCurrentUserId());
			Pager<ConferenceReserve> page = conferenceReserveService.getPageBySqlKey(queryFilter);
			return ResponseMessage.success(message,page);
		}catch (Exception ex){
			if(status == 2 ){ //待审批
				message = "待审批会议列表数据查询失败！";
			}else if (status == 4){ //已审批
				message = "已审批会议列表数据查询失败！";
			}
			LOGGER.error(message+","+ex.getMessage());
			return ResponseMessage.fail(message);
		}
	}
}

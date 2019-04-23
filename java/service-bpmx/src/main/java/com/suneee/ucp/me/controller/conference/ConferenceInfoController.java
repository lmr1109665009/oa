package com.suneee.ucp.me.controller.conference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.event.def.ApolloMessage;
import com.suneee.ucp.base.service.system.MessageService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.me.model.conference.ConferenceDevice;
import com.suneee.ucp.me.model.conference.ConferenceInfo;
import com.suneee.ucp.me.service.conference.ConferenceInfoService;
import com.suneee.ucp.me.service.conference.ConferenceRoomService;

import net.sf.json.JSONObject;

/**
 *<pre>
 * 对象功能:会议信息 控制器类
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-05-05 13:21:21
 *</pre>
 */
@Controller
@RequestMapping("/me/conference/conferenceInfo/")
public class ConferenceInfoController extends UcpBaseController
{
	@Resource
	private ConferenceInfoService conferenceInfoService;
    @Resource
    private ConferenceRoomService roomService;
	@Resource
	private SysUserService userService;
	@Resource 
	private MessageService msgService;
	/**
	 * 会议室预订。
	 * @param request
	 * @param response
	 * @param conferenceInfo 添加的实体
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "会议室预订")
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, ConferenceInfo conferenceInfo)
	{      
		try{
			//将当前用户添加到参会人员中
			String currentUserId = "" + ContextUtil.getCurrentUserId();
			String participants = conferenceInfo.getParticipants();
			if(participants == null || "".equals(participants)){
				participants = currentUserId;
			}else{
				if(!participants.contains(currentUserId)){
					participants += ","+currentUserId;
				}
			}
			conferenceInfo.setParticipants(participants);

			boolean isReverse = conferenceInfoService.isReserve(conferenceInfo);
			if(isReverse){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "会议室预订失败，请刷新页面重新选择预定时间!");
			}
			conferenceInfoService.add(conferenceInfo);
//			conferenceInfoService.sendMessage(conferenceInfo, ApolloMessage.STATUS_CONFERENCE_ADD);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "会议室预订成功!", conferenceInfo.getConferenceId());
		}catch(Exception e){
			logger.error("会议室预定失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "会议室预订失败!");
		}
	}
	
	
	/**
	 * 会议室预订信息分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看会议室预订信息分页列表")
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response)
	{	
		// 构造查询条件
		JSONObject json = new JSONObject();
		json.accumulate("currentPage", RequestUtil.getString(request, "currentPage", "1"));
		json.accumulate("pageSize", RequestUtil.getString(request, "pageSize", "20"));
		json.accumulate("createBy", ContextUtil.getCurrentUserId());
		json.accumulate("orderField", "create_time");
		json.accumulate("orderSeq", "desc");

		try {
			// 查询当前用户的会议室预定信息列表
			PageList<ConferenceInfo> list = (PageList<ConferenceInfo>)conferenceInfoService.getAll(new QueryFilter(json));
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取会议室预订信息列表成功!", PageUtil.getPageVo(list));
		} catch (Exception e) {
			logger.error("获取会议室预订信息分页列表失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取会议室预订信息列表失败!");
		}
	}

	/**
	 * 当前登录用户所需参加会议信息分页列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("listAboutCurUser")
	@Action(description = "查看当前登录用户所需参加会议信息分页列表")
	@ResponseBody
	public ResultVo listAboutCurUser(HttpServletRequest request, HttpServletResponse response){
		try{
			// 构造查询条件
			JSONObject json = new JSONObject();
			json.accumulate("currentPage", RequestUtil.getString(request, "currentPage", "1"));
			json.accumulate("pageSize", RequestUtil.getString(request, "pageSize", "20"));
			json.accumulate("currentUserId", ContextSupportUtil.getCurrentUserId());
			json.accumulate("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			//获取当前日期并将格式从“yyyy-MM-dd HH-mm-ss”转换为数据库对应格式“yyyy-MM-dd”
			String date = RequestUtil.getString(request,"currentDate");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			//判断是否需要查询当天会议信息
			if(date != null && !"".equals(date)){
				String currentDate = dateFormat.format(dateFormat.parse(date));
				json.accumulate("currentDate", currentDate);
			}
			//查询当前用户所需参加会议信息分页列表
			PageList<ConferenceInfo> list = (PageList<ConferenceInfo>)conferenceInfoService.getAllAboutCurUser(new QueryFilter(json));
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取当前登录用户所需参加会议信息分页列表成功!", PageUtil.getPageVo(list));
		}catch(Exception e){
			logger.error("获取当前登录用户所需参加会议信息分页列表失败!", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取当前登录用户所需参加会议信息分页列表失败!");
		}
	}

	/**
	 * 删除会议信息表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除会议信息表")
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response)
	{
		// 获取请求参数
		Long conferenceId = RequestUtil.getLong(request, "conferenceId");
		if(null == conferenceId || 0 == conferenceId){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误!");
		}
		
		try{
//			ConferenceInfo conferenceInfo = conferenceInfoService.getById(conferenceId);
			conferenceInfoService.delById(conferenceId);
//			conferenceInfoService.sendMessage(conferenceInfo, ApolloMessage.STATUS_CONFERENCE_DEL);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除会议信息成功!");
		}catch(Exception ex){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除会议信息失败!");
		}
	}

	/**
	 * 取消会议信息
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("cancel")
	@Action(description = "取消会议信息")
	@ResponseBody
	public ResultVo cancel(HttpServletRequest request)
	{
		// 获取请求参数
		Long conferenceId = RequestUtil.getLong(request, "conferenceId");
		if(null == conferenceId || 0 == conferenceId){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误!");
		}

		try{
			SysUser user = (SysUser) ContextUtil.getCurrentUser();
			ConferenceInfo conferenceInfo = conferenceInfoService.getById(conferenceId);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			Date meetingStartDate=sdf.parse(dateFormat.format(conferenceInfo.getConvokeDate())+" "+conferenceInfo.getBeginTime());
			Date dateNow=new Date();
			if (dateNow.compareTo(meetingStartDate)<=0){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "会议已过期，无法取消!");
			}
			conferenceInfo.setStatus(ConferenceInfo.STATUS_CANCEL);
			conferenceInfo.setUpdateBy(user.getUserId());
			conferenceInfo.setUpdatetime(dateNow);
			conferenceInfoService.update(conferenceInfo);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "取消会议成功!");
		}catch(Exception ex){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "取消会议失败!");
		}
	}

	/**
	 * 取得会议信息明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看会议信息明细")
	@ResponseBody
	public ResultVo get(HttpServletRequest request, HttpServletResponse response)
	{
		// 获取请求参数
		Long conferenceId = RequestUtil.getLong(request, "conferenceId");
		if(null == conferenceId || 0 == conferenceId){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误！");
		}
		try {
			ConferenceInfo conferenceInfo = conferenceInfoService.getById(conferenceId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "查看会议预定明细成功！", conferenceInfo);
		} catch (Exception e) {
			logger.error("查看会议预定信息明细失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "查看会议预定明细失败！");
		}
	}
	
	/**
	 * 获取会议预定记录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getReserveList")
	@ResponseBody
	public ResultVo getReserveList(HttpServletRequest request, HttpServletResponse response){
		// 获取会议室ID
		Long roomId = RequestUtil.getLong(request, "roomId");
		if(null == roomId || 0 == roomId){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误！");
		}
		
		// 根据会议室ID获取该会议室一周内的预订记录
		try {
			List<ConferenceInfo> list = conferenceInfoService.getByRoomId(roomId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取会议预订记录成功！", list);
		} catch (Exception e) {
			logger.error("获取会议预定记录失败！", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取会议预定记录失败！");
		}
	}
	
	/**
	 * 获取可选设备列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getAvailableDevice")
	@ResponseBody
	public ResultVo getAvailableDevice(HttpServletRequest request, HttpServletResponse response){
		try {
			// 获取请求参数并校验
			Date convokeDate = RequestUtil.getDate(request, "convokeDate");
			String beginTime = RequestUtil.getString(request, "beginTime");
			String endTime = RequestUtil.getString(request, "endTime");
			Long region = RequestUtil.getLong(request, "region");
			if(null == convokeDate || StringUtils.isBlank(beginTime) || StringUtils.isBlank(endTime)
					|| null == region || 0 == region){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数错误！");
			}
			
			// 获取可用设备
			List<ConferenceDevice> list = conferenceInfoService.getAvailableDevice(convokeDate, beginTime, endTime, region);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取可用设备成功！", list);
		} catch (Exception e) {
			logger.error("获取可用设备失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取可用设备失败！");
		}
		
	}
}


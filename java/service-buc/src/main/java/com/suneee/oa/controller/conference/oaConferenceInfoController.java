package com.suneee.oa.controller.conference;


import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.oa.service.scheduleCalendar.OaScheduleCalendarService;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.me.model.conference.ConferenceDevice;
import com.suneee.ucp.me.model.conference.ConferenceInfo;
import com.suneee.ucp.me.service.conference.ConferenceDeviceService;
import com.suneee.ucp.me.service.conference.ConferenceInfoService;
import com.suneee.ucp.mh.service.schedulecalendar.ScheduleCalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *<pre>
 * 对象功能:会议信息 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2017-12-19 15:34:00
 *</pre>
 */
@Controller
@RequestMapping("/oa/oaConferenceInfo/")
public class oaConferenceInfoController extends UcpBaseController {

	@Resource
	private ConferenceInfoService conferenceInfoService;
	@Resource
	private ConferenceDeviceService conferenceDeviceService;

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
				return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "会议室预订失败，请刷新页面重新选择预定时间!");
			}
			//添加会议室默认设备
			List<ConferenceDevice> devicesList = conferenceDeviceService.getConferenceDevicesByRoomId(conferenceInfo.getRoomId());
			String devices = conferenceInfo.getDevices();
			if(devicesList.size() > 0){
				for(ConferenceDevice cd:devicesList){
					if(StringUtil.isEmpty(devices)){
						devices += cd.getDeviceId() + ",";
					}else{
						devices += "," + cd.getDeviceId();
					}
				}
			}
			if(devices.endsWith(",")){
				String newDevices = devices.substring(0, devices.length() - 1);
				conferenceInfo.setDevices(newDevices);
			}else {
				conferenceInfo.setDevices(devices);
			}
			//预定会议室
			conferenceInfoService.addConferenceAndScheduleCalendar(conferenceInfo);
			return new ResultVo(ResultConst.COMMON_STATUS_SUCCESS, "会议室预订成功!", conferenceInfo.getConferenceId());
		}catch(Exception e){
			logger.error("会议室预定失败", e);
			return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "会议室预订失败!");
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
			return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "请求参数错误!");
		}
		try{
			conferenceInfoService.delConAndSchById(conferenceId);
			return new ResultVo(ResultConst.COMMON_STATUS_SUCCESS, "删除会议信息成功!");
		}catch(Exception ex){
			return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "删除会议信息失败!");
		}
	}
}

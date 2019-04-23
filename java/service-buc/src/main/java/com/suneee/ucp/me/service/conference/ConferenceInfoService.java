package com.suneee.ucp.me.service.conference;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.oa.service.scheduleCalendar.OaScheduleCalendarService;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.event.def.ApolloMessage;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.service.system.SysUserExtService;
import com.suneee.ucp.base.util.DateUtils;
import com.suneee.ucp.me.dao.conference.ConferenceInfoDao;
import com.suneee.ucp.me.model.conference.ConferenceDevice;
import com.suneee.ucp.me.model.conference.ConferenceInfo;
import com.suneee.ucp.mh.model.schedulecalendar.ScheduleCalendar;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *<pre>
 * 对象功能:会议信息 Service类
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-05-05 13:21:21
 *</pre>
 */
@Service
public class ConferenceInfoService extends  UcpBaseService<ConferenceInfo>
{
	@Resource
	private ConferenceInfoDao dao;
	@Resource
	private ConferenceDeviceService conferenceDeviceService;
	@Resource
	private SysUserExtService sysUserExtService;
	@Resource
	private ConferenceRoomService conferenceRoomService;
	@Resource
	private OaScheduleCalendarService oaScheduleCalendarService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;


	@Override
	protected IEntityDao<ConferenceInfo, Long> getEntityDao() 
	{
		return dao;
	}
	
	@Override
	public void add(ConferenceInfo conferenceInfo){
		conferenceInfo.setConferenceId(UniqueIdUtil.genId());
		Long userId = ContextUtil.getCurrentUserId();
		Date date = new Date();
		conferenceInfo.setCreateBy(userId);
		conferenceInfo.setCreatetime(date);
		conferenceInfo.setUpdateBy(userId);
		conferenceInfo.setUpdatetime(date);
		dao.add(conferenceInfo);
	}
	
	/**
	 * 获取会议信息列表
	 * @param queryFilter
	 * @return
	 */
	@Override
	public List<ConferenceInfo> getAll(QueryFilter queryFilter){
		List<ConferenceInfo> list = dao.getAll(queryFilter);
		for(ConferenceInfo conferenceInfo : list){
			getConferenceInfoRef(conferenceInfo);
		}
		return list;
	}

	/**
	 * 获取当前用户所需参加的会议信息列表
	 * @param queryFilter
	 * @return
	 */
	public List<ConferenceInfo> getAllAboutCurUser(QueryFilter queryFilter){
		List<ConferenceInfo> list = dao.getAllAboutCurUser(queryFilter);
		for(ConferenceInfo conferenceInfo : list){
			getConferenceInfoRef(conferenceInfo);
		}
		return list;
	}

	/**
	 * 根据会议预订ID获取会议预定信息
	 * @param conferenceId
	 * @return
	 */
	@Override
	public ConferenceInfo getById(Long conferenceId){
		ConferenceInfo conferenceInfo = dao.getById(conferenceId);
		getConferenceInfoRef(conferenceInfo);
		return conferenceInfo;
	}
	
	/**
	 * 根据会议室ID获取该会议室一周内的预定记录
	 * @param roomId
	 * @return
	 */
	public List<ConferenceInfo> getByRoomId(Long roomId){
		Date[] dateArr = DateUtils.getDaysBetween(new Date(), 7, true);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roomId", roomId);
		params.put("beginConvokeDate", dateArr[0]);
		params.put("endConvokeDate", dateArr[dateArr.length - 1]);
		params.put("orderField", "convoke_date");
		params.put("orderSeq", "DESC");
		return dao.getByCondition(params);
	}
	
	/**
	 * 获取可选设备列表
	 * @param convokeDate
	 * @param beginTime
	 * @param endTime
	 * @param region
	 * @return
	 */
	public List<ConferenceDevice> getAvailableDevice(Date convokeDate, String beginTime, String endTime, Long region){
		// 查询当前选中时间段内各会议室的预订记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("convokeDate", convokeDate);
		params.put("beginBeginTime", endTime);
		params.put("endEndTime", beginTime);
		params.put("region", region);
		List<ConferenceInfo> list = dao.getByCondition(params);
		
		// 获取各预订记录中选择的会议设备
		StringBuilder devices = new StringBuilder();
		for(ConferenceInfo conferenceInfo : list){
			if(StringUtil.isNotEmpty(conferenceInfo.getDevices())){
				devices.append(conferenceInfo.getDevices()+Constants.SEPARATOR_COMMA);
			}
		}
		return conferenceDeviceService.getConferenceDevicesNotIn(devices.toString(), region);
	}
	
	/**
	 * 发送会议室预定/取消消息
	 * @param conferenceInfo
	 * @param status
	 */
	public void sendMessage(ConferenceInfo conferenceInfo, int status){
		if(StringUtils.isBlank(conferenceInfo.getParticipants())){
			return;
		}
		Map<String, Object> businessData = new HashMap<String, Object>();
		// 会议主题
		businessData.put("theme", conferenceInfo.getTheme());
		// 会议描述
		businessData.put("description", conferenceInfo.getDescription());
		
		String date = DateFormatUtil.format(conferenceInfo.getConvokeDate(), DateUtils.FORMAT_DATE);
		// 会议开始时间
		String startTime = date + Constants.SEPARATOR_BLANK + conferenceInfo.getBeginTime();
		businessData.put("startTime", startTime);
		// 会议结束时间
		String endTime = date + Constants.SEPARATOR_BLANK + conferenceInfo.getEndTime();
		businessData.put("endTime", endTime);
		// 会议室名称
		String roomName = conferenceRoomService.getById(conferenceInfo.getRoomId()).getRoomName();
		businessData.put("roomName", roomName);
		
		List<SysUser> userList = sysUserExtService.getSysUserByIds(conferenceInfo.getParticipants());
		// 消息目标对象
		List<Map<String, Object>> targets = new ArrayList<Map<String, Object>>();
		// 与会者姓名
		List<String> userNames = new ArrayList<String>();
		String  username = null;
		Map<String, Object> target = null;
		Set<String> enterpriseCodes = null;
		for(SysUser user : userList){
			// 获取用户关联的企业编码
			enterpriseCodes = enterpriseinfoService.getCodeSetByUserId(user.getUserId());
			if(!enterpriseCodes.isEmpty()){
				enterpriseCodes.add("");
			}
			for(String enterpriseCode : enterpriseCodes){
				target = new HashMap<String, Object>();
				target.put("email", user.getEmail());
				target.put("enterpriseCode", enterpriseCode);
				targets.add(target);
			}
			username = user.getFullname();
			if(StringUtils.isNotBlank(user.getAliasName())){
				username += "（" + user.getAliasName() + "）";
			}
			userNames.add(username);
		}
		businessData.put("participants", StringUtils.join(userNames, Constants.SEPARATOR_COMMA));
		
		// 发布定子链会议室消息
		EventUtil.publishApolloMessageEvent(ApolloMessage.MSG_TYPE_CONFERENCE, targets, 
				ApolloMessage.TARGET_TYPE_PRIVATE, status, businessData);
	}
	
	/**
	 * 判断会议室在指定的时间段内是否被预订
	 * @param conferenceInfo
	 * @return
	 */
	public boolean isReserve(ConferenceInfo conferenceInfo){
		List<ConferenceInfo> conferenceInfoList = dao.isReverse(conferenceInfo);
		if(conferenceInfoList.isEmpty()){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取会议预订用于展示的关联信息
	 * @param conferenceInfo
	 */
	private void getConferenceInfoRef(ConferenceInfo conferenceInfo){
		if(null != conferenceInfo){
			if(StringUtil.isNotEmpty(conferenceInfo.getDevices())){
				// 获取会议选取的所有设备的名称
				List<ConferenceDevice> deviceList = conferenceDeviceService.getConferenceDevicesByIds(conferenceInfo.getDevices());
				StringBuilder deviceNames = new StringBuilder();
				for(ConferenceDevice device : deviceList){
					deviceNames.append(device.getDeviceName()).append( Constants.SEPARATOR_COMMA);
				}
				if(deviceNames.toString().length() > 0){
					conferenceInfo.setDevicesText(deviceNames.toString().substring(0,deviceNames.toString().length()-1));
				}
			}

			if(StringUtil.isNotEmpty(conferenceInfo.getParticipants())){
				// 获取会议出席人员的姓名
				List<SysUser> userList = sysUserExtService.getSysUserByIds(conferenceInfo.getParticipants());
				StringBuilder participantNames = new StringBuilder();
				for(SysUser user : userList){
					participantNames.append(user.getFullname()).append(Constants.SEPARATOR_COMMA);
				}
				if(participantNames.toString().length() > 0){
					conferenceInfo.setParticipantsText(participantNames.toString().substring(0,participantNames.toString().length()-1));
				}
			}
		}
	}

	/**
	 * 添加会议信息，同时添加日程信息
	 * @param conferenceInfo
	 */
	public void addConferenceAndScheduleCalendar(ConferenceInfo conferenceInfo) throws ParseException {
		//添加会议信息
		conferenceInfo.setConferenceId(UniqueIdUtil.genId());
		Long userId = ContextUtil.getCurrentUserId();
		Date date = new Date();
		conferenceInfo.setCreateBy(userId);
		conferenceInfo.setCreatetime(date);
		conferenceInfo.setUpdateBy(userId);
		conferenceInfo.setUpdatetime(date);
		dao.add(conferenceInfo);
		//添加该会议记录到参会人员日程记录中
		//创建scheduleCalendar用于封装数据
		ScheduleCalendar scheduleCalendar = new ScheduleCalendar();
		//设置标题
		scheduleCalendar.setTitle(conferenceInfo.getTheme());
		Date convokeDate = conferenceInfo.getConvokeDate();
		String beginTime = conferenceInfo.getBeginTime();
		String endTime = conferenceInfo.getEndTime();
		//设置开始、结束时间
		scheduleCalendar.setStartTime(mergeDateFormat(convokeDate,beginTime));
		scheduleCalendar.setEndTime(mergeDateFormat(convokeDate,endTime));
		scheduleCalendar.setSourceId(conferenceInfo.getConferenceId());
		scheduleCalendar.setAllDay((short) 0);
		//添加日程
		oaScheduleCalendarService.save(scheduleCalendar,conferenceInfo.getParticipants());
	}

	/**
	 *合并日期格式（Date:2017-12-19 00:00:00 + String:"16:14" 合并成 Date：2017-12-19 16:14:00）
	 * @return
	 * @throws ParseException
	 */
	private Date mergeDateFormat(Date date,String hour) throws ParseException {
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		String date1 = dateFormat1.format(date);
		String dates = date1+" "+hour+":00";
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date resultDate = dateFormat2.parse(dates);
		return resultDate;
	}

	/**
	 * 根据会议Id删除会议信息和日程信息
	 * @param conferenceId
	 */
	public void delConAndSchById(Long conferenceId){
		//删除会议信息
		dao.delById(conferenceId);
		//删除日程信息
		oaScheduleCalendarService.delBySourceId(conferenceId);
	}
}

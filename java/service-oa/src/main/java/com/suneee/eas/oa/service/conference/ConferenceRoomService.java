package com.suneee.eas.oa.service.conference;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.conference.ConferenceRoom;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description:
 * @Author: kaize
 * @Date: 2018/7/31 17:51
 */
public interface ConferenceRoomService extends BaseService<ConferenceRoom> {

	//保存会议室和授权信息
	void saveRoomAndAuthority(HttpServletRequest request, ConferenceRoom conferenceRoom);
	//更新会议室和授权信息
	void updateRoomAndAuthority(HttpServletRequest request, ConferenceRoom conferenceRoom);
	//批量删除会议室
	void deleteByIds(String roomIds);
	//根据地区获取会议室
	List<ConferenceRoom> getRoomByRegion(Long region);
	/** 根据地区查询当前用户在当前企业下可查看的会议室信息列表
	 * @param region
	 * @return
	 */
	List<ConferenceRoom> getAvailableRoom(Long region);

	/**
	 * 判断同一区域会议室名称是否重复（新增）
	 * @param conferenceRoom
	 * @return
	 */
	boolean isRoomNameRepeatForAdd(ConferenceRoom conferenceRoom);

	/**
	 * 判断同一区域会议室名称是否重复（更新）
	 * @param conferenceRoom
	 * @return
	 */
	boolean isRoomNameRepeatForUpdate(ConferenceRoom conferenceRoom);
}

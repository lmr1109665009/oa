package com.suneee.eas.oa.service.conference.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.dao.conference.ConferenceRoomDao;
import com.suneee.eas.oa.model.conference.AuthorityInfo;
import com.suneee.eas.oa.model.conference.ConferenceRoom;
import com.suneee.eas.oa.service.conference.AuthorityInfoService;
import com.suneee.eas.oa.service.conference.ConferenceRoomService;
import com.suneee.eas.oa.service.user.UserService;
import com.suneee.platform.model.system.SysOrg;

/**
 * @Description:
 * @Author: kaize
 * @Date: 2018/7/31 17:53
 */
@Service
public class ConferenceRoomServiceImpl extends BaseServiceImpl<ConferenceRoom> implements ConferenceRoomService {


	private ConferenceRoomDao conferenceRoomDao;
	@Autowired
	private AuthorityInfoService authorityInfoService;
	@Autowired 
	private UserService userService;

	@Autowired
	public void setConferenceRoomDao(ConferenceRoomDao conferenceRoomDao) {
		this.conferenceRoomDao = conferenceRoomDao;
		setBaseDao(conferenceRoomDao);
	}

	@Override
	public int save(ConferenceRoom conferenceRoom) {
		//生成唯一主键Id
		Long id = IdGeneratorUtil.getNextId();
		conferenceRoom.setRoomId(id);
		conferenceRoom.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
		//获取当前用户ID
		Long currrentUserId = ContextSupportUtil.getCurrentUserId();
		String fullName = ContextSupportUtil.getCurrentUsername();
		conferenceRoom.setCreateBy(currrentUserId);
		conferenceRoom.setUpdateBy(currrentUserId);
		conferenceRoom.setCreateByName(fullName);
		conferenceRoom.setUpdateByName(fullName);
		//获取当前时间
		Date date = new Date();
		conferenceRoom.setCreateTime(date);
		conferenceRoom.setUpdateTime(date);
		return conferenceRoomDao.save(conferenceRoom);
	}

	@Override
	public int update(ConferenceRoom conferenceRoom) {
		//设置更新人ID
		conferenceRoom.setUpdateBy(ContextSupportUtil.getCurrentUserId());
		conferenceRoom.setUpdateByName(ContextSupportUtil.getCurrentUsername());
		//设置更新时间
		conferenceRoom.setUpdateTime(new Date());
		return conferenceRoomDao.update(conferenceRoom);
	}

	@Override
	public int deleteById(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("updateBy", ContextSupportUtil.getCurrentUserId());
		params.put("updateByName", ContextSupportUtil.getCurrentUsername());
		params.put("updateTime",new Date());
		params.put("roomId",id);
		return conferenceRoomDao.deleteById(params);
	}

	/**
	 * 保存会议室及对应授权权限
	 * @param request
	 * @param conferenceRoom
	 */
	public void saveRoomAndAuthority(HttpServletRequest request,ConferenceRoom conferenceRoom){
		//保存会议室
		save(conferenceRoom);
		//处理授权信息
		//获取授权信组织信息
		String orgIds = RequestUtil.getString(request, "orgIds");
		String orgNames = RequestUtil.getString(request, "orgNames");
		String userIds = RequestUtil.getString(request, "userIds");
		String userNames = RequestUtil.getString(request, "userNames");
		if(BeanUtils.isNotEmpty(orgIds)){
			//保存组织授权信息
			authorityInfoService.save(conferenceRoom.getRoomId(),AuthorityInfo.AUTHTYPE_CONFERENCE_ROOM,orgIds,orgNames,AuthorityInfo.OWNERTYPE_ORG);
		}
		//获取授权人员信息
		if(BeanUtils.isNotEmpty(userIds)){
			//保存人员授权信息
			authorityInfoService.save(conferenceRoom.getRoomId(),AuthorityInfo.AUTHTYPE_CONFERENCE_ROOM,userIds,userNames,AuthorityInfo.OWNERTYPE_USER);
		}
	}

	/**
	 * 更新会议室及对应授权权限
	 * @param request
	 * @param conferenceRoom
	 */
	@Override
	public void updateRoomAndAuthority(HttpServletRequest request, ConferenceRoom conferenceRoom) {
		//更新会议室
		update(conferenceRoom);
		//处理授权信息
		//获取授权信组织信息
		String orgIds = RequestUtil.getString(request, "orgIds");
		String orgNames = RequestUtil.getString(request, "orgNames");
		//获取授权人员信息
		String userIds = RequestUtil.getString(request, "userIds");
		String userNames = RequestUtil.getString(request, "userNames");
		//更新组织授权信息
		authorityInfoService.update(conferenceRoom.getRoomId(),AuthorityInfo.AUTHTYPE_CONFERENCE_ROOM,orgIds,orgNames,AuthorityInfo.OWNERTYPE_ORG);
		//更新人员授权信息
		authorityInfoService.update(conferenceRoom.getRoomId(),AuthorityInfo.AUTHTYPE_CONFERENCE_ROOM,userIds,userNames,AuthorityInfo.OWNERTYPE_USER);
	}

	/**
	 * 批量删除会议室
	 * @param roomIds
	 */
	@Override
	public void deleteByIds(String roomIds) {
		String[] roomIdList = roomIds.split(",");
		for(String roomId: roomIdList){
			Long id = Long.valueOf(roomId);
			//删除会议室
			deleteById(id);
			//删除会议室授权信息
			authorityInfoService.deleteById(id);
		}
	}

	/**
	 * 根据地区获取会议室
	 * @param region
	 * @return
	 */
	@Override
	public List<ConferenceRoom> getRoomByRegion(Long region) {
		Map<String,Object> params = new HashMap<>();
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		params.put("region",region);
		return conferenceRoomDao.getRoomByRegion(params);
	}
	
	/** 
	 * @Title: getAvailableRoom 
	 * @Description: 根据地区查询当前用户在当前企业下可查看的会议室信息列表
	 * @param region
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceRoomService#getAvailableRoom(java.lang.Long)
	 */
	public List<ConferenceRoom> getAvailableRoom(Long region) {
		if(region == null){
			throw new IllegalArgumentException("region must not be null.");
		}
		Long userId = ContextSupportUtil.getCurrentUserId();
		// 查询当前用户所属的组织
		List<SysOrg> orgList = userService.getOrgListByUserId(userId);
		List<Long> orgIds = null;
		if(orgList.size() > 0){
			orgIds = new ArrayList<Long>();
			for(SysOrg org : orgList){
				String[] orgIdArr = org.getPath().split("\\.");
				for(String orgIdStr : orgIdArr){
					orgIds.add(Long.parseLong(orgIdStr));
				}
			}
		}
		Map<String,Object> params = new HashMap<>();
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		params.put("region",region);
		params.put("userId", userId);
		params.put("orgIds", orgIds);
		params.put("authType", AuthorityInfo.AUTHTYPE_CONFERENCE_ROOM);
		return conferenceRoomDao.getAvailableRoom(params);
	}

	/**
	 * 判断同一区域会议室名称是否重复（新增）
	 * @param conferenceRoom
	 * @return
	 */
	@Override
	public boolean isRoomNameRepeatForAdd(ConferenceRoom conferenceRoom){
		Map<String,Object> params = new HashMap<>();
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		params.put("region",conferenceRoom.getRegion());
		params.put("roomName",conferenceRoom.getRoomName());
		int i = conferenceRoomDao.isRoomNameRepeatForAdd(params);
		if(i > 0){
			return true;
		}
		return false;
	}

	/**
	 * 判断同一区域会议室名称是否重复（更新）
	 * @param conferenceRoom
	 * @return
	 */
	@Override
	public boolean isRoomNameRepeatForUpdate(ConferenceRoom conferenceRoom){
		Map<String,Object> params = new HashMap<>();
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		params.put("region",conferenceRoom.getRegion());
		params.put("roomId",conferenceRoom.getRoomId());
		params.put("roomName",conferenceRoom.getRoomName());
		int i = conferenceRoomDao.isRoomNameRepeatForUpdate(params);
		if(i > 0){
			return true;
		}
		return false;
	}
}

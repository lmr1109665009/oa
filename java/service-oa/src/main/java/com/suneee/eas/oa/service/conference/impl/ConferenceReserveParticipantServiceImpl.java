/**
 * @Title: ConferenceReserveParticipantServiceImpl.java 
 * @Package com.suneee.eas.oa.service.conference.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.oa.dao.conference.ConferenceReserveParticipantDao;
import com.suneee.eas.oa.model.conference.ConferenceReserveParticipant;
import com.suneee.eas.oa.service.conference.ConferenceReserveParticipantService;

/**
 * @ClassName: ConferenceReserveParticipantServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 13:56:07 
 *
 */
@Service
public class ConferenceReserveParticipantServiceImpl extends BaseServiceImpl<ConferenceReserveParticipant> implements ConferenceReserveParticipantService{
	private ConferenceReserveParticipantDao conferenceReserveParticipantDao;
	@Autowired
	public void setConferenceReserveParticipantDao(ConferenceReserveParticipantDao conferenceReserveParticipantDao){
		this.conferenceReserveParticipantDao = conferenceReserveParticipantDao;
		setBaseDao(conferenceReserveParticipantDao);
	}
	
	/** 
	 * @Title: save 
	 * @Description: 批量保存与会人员信息
	 * @param conferenceId
	 * @param type
	 * @param userIds
	 * @param userNames 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveParticipantService#save(java.lang.Long, java.lang.Byte, java.lang.String, java.lang.String)
	 */
	@Override
	public void save(Long conferenceId, Byte type, String userIds, String userNames){
		// 入参校验
		if(StringUtils.isBlank(userIds) || StringUtils.isBlank(userNames)){
			throw new IllegalArgumentException("userIds and userNames must not be blank.");
		}
		// 将用户ID字符串转换为用户ID集合
		String[] userIdArr = userIds.split(",");
		List<Long> userIdList = new ArrayList<Long>();
		for(String userId : userIdArr){
			userIdList.add(Long.parseLong(userId));
		}
		String[] userNameArr = userNames.split(",");
		List<String> userNameList = Arrays.asList(userNameArr);
		this.batchSave(conferenceId, type, userIdList, userNameList);
	}
	
	/** 
	 * @param conferenceId
	 * @param type
	 * @param userIds
	 * @param userNames
	 */
	public void batchSave(Long conferenceId, Byte type, List<Long> userIds, List<String> userNames){
		if(conferenceId == null || type == null || userIds == null || userNames == null){
			throw new IllegalArgumentException("conferenceId, type, userIds and userNames must not be null.");
		}
		
		if(userIds.size() != userNames.size()){
			throw new IllegalArgumentException("the length of 'userIds' is not accordance with 'userNames'.");
		}
		
		// 构建与会人员信息对象
		List<ConferenceReserveParticipant> list = new ArrayList<ConferenceReserveParticipant>();
		ConferenceReserveParticipant participant = null;
		for(int i = 0; i < userIds.size(); i++){
			participant = new ConferenceReserveParticipant();
			participant.setConferenceId(conferenceId);
			participant.setType(type);
			participant.setUserId(userIds.get(i));
			participant.setUserName(userNames.get(i));
			list.add(participant);
		}
		
		// 批量保存
		conferenceReserveParticipantDao.batchSave(list);
	}
	
	/** 
	 * @Title: update 
	 * @Description: 更新与会人员信息
	 * @param conferenceId
	 * @param type
	 * @param userIds
	 * @param userNames 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveParticipantService#update(java.lang.Long, java.lang.Byte, java.lang.String, java.lang.String)
	 */
	public void update(Long conferenceId, Byte type, String userIds, String userNames){
		// 删除已经存在的与会人员信息
		this.deleteByConferenceIdAndType(conferenceId, type);
		// 保存新的与会人员信息
		this.save(conferenceId, type, userIds, userNames);
	}
	
	/** 
	 * 删除与会人员信息
	 * @param conferenceId
	 * @param type
	 */
	public void deleteByConferenceIdAndType(Long conferenceId, Byte type){
		conferenceReserveParticipantDao.deleteByConferenceIdAndType(conferenceId, type);
	}
	
	/** 
	 * 获取与会人员信息
	 * @param conferenceId
	 * @param type
	 * @return
	 */
	public List<ConferenceReserveParticipant> getByConferenceIdAndType(Long conferenceId, Byte type){
		return conferenceReserveParticipantDao.getByConferenceIdAndType(conferenceId, type);
	}

	/** 
	 * @Title: isParticipant 
	 * @Description: 判断指定用户是否是参会人员
	 * @param conferenceId
	 * @param userId
	 * @param type
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveParticipantService#isParticipant(java.lang.Long, java.lang.Long, java.lang.Byte)
	 */
	@Override
	public boolean isParticipant(Long conferenceId, Long userId, Byte type) {
		ConferenceReserveParticipant participant = conferenceReserveParticipantDao.getConferenceReserveParticipant(conferenceId, userId, type);
		if(participant == null){
			return false;
		}
		return true;
	}
}

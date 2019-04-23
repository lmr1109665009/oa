/**
 * @Title: ConferenceReserveParticipantService.java 
 * @Package com.suneee.eas.oa.service.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference;

import java.util.List;

import com.suneee.eas.oa.model.conference.ConferenceReserveParticipant;

/**
 * @ClassName: ConferenceReserveParticipantService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 13:55:38 
 *
 */
public interface ConferenceReserveParticipantService{
	/** 
	 * 保存与会人员信息
	 * @param conferenceId
	 * @param type
	 * @param userIds
	 * @param userNames
	 */
	public void save(Long conferenceId, Byte type, String userIds, String userNames);
	
	/** 
	 * 批量保存与会人员信息
	 * @param conferenceId
	 * @param type
	 * @param userIds
	 * @param userNames
	 */
	public void batchSave(Long conferenceId, Byte type, List<Long> userIds, List<String> userNames);
	
	/** 
	 * 更新与会人员信息
	 * @param conferenceId
	 * @param type
	 * @param userIds
	 * @param userNames
	 */
	public void update(Long conferenceId, Byte type, String userIds, String userNames);
	
	/** 
	 * 删除与会人员信息
	 * @param conferenceId
	 * @param type
	 */
	public void deleteByConferenceIdAndType(Long conferenceId, Byte type);
	
	/** 
	 * 获取与会人员信息
	 * @param conferenceId
	 * @param type
	 * @return
	 */
	public List<ConferenceReserveParticipant> getByConferenceIdAndType(Long conferenceId, Byte type);
	
	/** 判断指定用户是否是参会人员
	 * @param conferenceId
	 * @param userId
	 * @param type
	 * @return
	 */
	public boolean isParticipant(Long conferenceId, Long userId, Byte type);
}

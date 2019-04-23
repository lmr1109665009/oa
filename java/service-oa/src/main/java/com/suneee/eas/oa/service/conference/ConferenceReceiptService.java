/**
 * @Title: ConferenceReceiptService.java 
 * @Package com.suneee.eas.oa.service.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference;

import java.util.List;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.exception.conference.AlreadyHandledException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.NotParticipantException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.ConferenceReceipt;

/**
 * @ClassName: ConferenceReceiptService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-09 11:17:29 
 *
 */
public interface ConferenceReceiptService extends BaseService<ConferenceReceipt>{
	/** 保存回执信息
	 * @param conferenceId
	 * @param status
	 * @param remark
	 * @throws NotParticipantException
	 * @throws AlreadyHandledException
	 * @throws IllegalStatusException
	 */
	public void save(Long conferenceId, Byte status, String remark) 
			throws UserPermissionException, NotParticipantException, AlreadyHandledException, IllegalStatusException;
	
	/** 获取会议回执信息列表
	 * @param conferenceId
	 * @return
	 */
	public List<ConferenceReceipt> getByConferenceId(Long conferenceId);
	
	/** 获取会议回执信息
	 * @param conferenceId
	 * @param userId
	 * @return
	 */
	public ConferenceReceipt getConferenceReceipt(Long conferenceId, Long userId);
	
	/** 删除会议回执信息
	 * @param conferenceId
	 */
	public void deleteByConferenceId(Long conferenceId);
}

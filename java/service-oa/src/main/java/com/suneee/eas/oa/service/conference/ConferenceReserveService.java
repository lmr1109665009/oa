/**
 * @Title: ConferenceReserveService.java 
 * @Package com.suneee.eas.oa.service 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.exception.conference.AlreadyEndedException;
import com.suneee.eas.oa.exception.conference.AlreadyStartedException;
import com.suneee.eas.oa.exception.conference.ConferenceDeviceNotAvailableException;
import com.suneee.eas.oa.exception.conference.ConferenceTimeConflictException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.IllegalTargetTypeException;
import com.suneee.eas.oa.exception.conference.NotStartedException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.ConferenceAudit;
import com.suneee.eas.oa.model.conference.ConferenceReserve;

/**
 * @ClassName: ConferenceReserveService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 13:49:03 
 *
 */
public interface ConferenceReserveService extends BaseService<ConferenceReserve>{
	/** 周期性预定会议室
	 * @param conferenceReserve 会议预定信息
	 * @param dateList 预定日期
	 * @param startTime 会议开始时间
	 * @param endTime 会议结束时间
	 * @param isIgnoreConflict 是否忽略冲突
	 * @throws ConferenceTimeConflictException, ConferenceDeviceNotAvailableException
	 */
	public void reserve(ConferenceReserve conferenceReserve, List<Date> dateList, String startTime, String endTime, Boolean isIgnoreConflict)
			throws ConferenceTimeConflictException, ConferenceDeviceNotAvailableException, UnsupportedEncodingException;
	/**非周期性预定会议室
	 * @param conferenceReserve 会议预定信息
	 * @param date 预定日期
	 * @param startTime 会议开始时间
	 * @param endTime 会议结束时间
	 * @param isIgnoreConflict 是否忽略冲突
	 * @throws ConferenceTimeConflictException, ConferenceDeviceNotAvailableException
	 */
	public void reserve(ConferenceReserve conferenceReserve, Date date, String startTime, String endTime, Boolean isIgnoreConflict)
            throws ConferenceTimeConflictException, ConferenceDeviceNotAvailableException, UnsupportedEncodingException;
	/** 更新会议信息
	 * @param conferenceReserve
	 * @param startDate
	 * @param startTime
	 * @param endTime
	 */
	public void update(ConferenceReserve conferenceReserve, Date date, String startTime, String endTime)
			throws ConferenceTimeConflictException, ConferenceDeviceNotAvailableException, AlreadyStartedException, UserPermissionException, IllegalStatusException, UnsupportedEncodingException;
	/** 更新会议纪要状态
	 * @param conferenceId
	 * @param status
	 */
	public void updateNoteStatus(Long conferenceId, Byte status);
	/** 更新会议状态
	 * @param conferenceId
	 * @param status
	 */
	public void updateAuditStatus(Long conferenceId, Byte status);
	/** 会议审批
	 * @param conferenceAudit
	 * @param opinion
	 * @throws IllegalStatusException
	 * @throws IllegalTargetTypeException
	 */
	public void audit(ConferenceAudit conferenceAudit, Byte status) throws IllegalStatusException, IllegalTargetTypeException, UnsupportedEncodingException;
	/** 取消会议
	 * @param conferenceId
	 * @throws AlreadyStartedException
	 * @throws UserPermissionException
	 */
	public void cancel(Long conferenceId) throws AlreadyStartedException, UserPermissionException;
	/** 提前结束会议
	 * @param conferenceId
	 * @throws NotStartedException
	 * @throws AlreadyEndedException
	 * @throws UserPermissionException
	 */
	public void finish(Long conferenceId) throws NotStartedException, AlreadyEndedException, UserPermissionException;
	
	/** 获取会议详情
	 * @param conferenceId
	 * @return
	 */
	public Map<String, Object> getReserveDetails(Long conferenceId);
	/** 查询指定时间段内的会议室预定情况
	 * @param roomId
	 * @param startTime
	 * @param endTime
	 * @param conferenceId
	 * @return
	 */
	public List<ConferenceReserve> listAll(Long roomId, Date startTime, Date endTime, Long conferenceId);
	/** 条件查询会议信息
	 * @param params
	 * @return
	 */
	public List<ConferenceReserve> listAll(Map<String, Object> params);
}

/**
 * @Title: ConferenceReserveServiceImpl.java 
 * @Package com.suneee.eas.oa.service.conference.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.core.util.DateFormatUtil;
import com.suneee.eas.common.constant.StatusConstant;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.conference.ConferenceReserveDao;
import com.suneee.eas.oa.exception.common.NotExistInDatabaseException;
import com.suneee.eas.oa.exception.conference.AlreadyEndedException;
import com.suneee.eas.oa.exception.conference.AlreadyStartedException;
import com.suneee.eas.oa.exception.conference.ConferenceDeviceNotAvailableException;
import com.suneee.eas.oa.exception.conference.ConferenceTimeConflictException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.IllegalTargetTypeException;
import com.suneee.eas.oa.exception.conference.NotStartedException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.AccessRecord;
import com.suneee.eas.oa.model.conference.AuthorityInfo;
import com.suneee.eas.oa.model.conference.ConferenceAudit;
import com.suneee.eas.oa.model.conference.ConferenceNote;
import com.suneee.eas.oa.model.conference.ConferenceReceipt;
import com.suneee.eas.oa.model.conference.ConferenceReserve;
import com.suneee.eas.oa.model.conference.ConferenceReserveDevice;
import com.suneee.eas.oa.model.conference.ConferenceReserveParticipant;
import com.suneee.eas.oa.model.conference.ConferenceRoom;
import com.suneee.eas.oa.model.system.SysFile;
import com.suneee.eas.oa.service.conference.AccessRecordService;
import com.suneee.eas.oa.service.conference.AuthorityInfoService;
import com.suneee.eas.oa.service.conference.ConferenceAuditService;
import com.suneee.eas.oa.service.conference.ConferenceNoteService;
import com.suneee.eas.oa.service.conference.ConferenceReceiptService;
import com.suneee.eas.oa.service.conference.ConferenceReserveDeviceService;
import com.suneee.eas.oa.service.conference.ConferenceReserveParticipantService;
import com.suneee.eas.oa.service.conference.ConferenceReserveService;
import com.suneee.eas.oa.service.conference.ConferenceRoomService;
import com.suneee.eas.oa.service.system.SysFileService;
import com.suneee.platform.calendar.util.CalendarUtil;
import com.suneee.platform.service.bpm.thread.MessageUtil;

/**
 * @ClassName: ConferenceReserveServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 13:52:50 
 *
 */
@Service
public class ConferenceReserveServiceImpl extends BaseServiceImpl<ConferenceReserve> implements ConferenceReserveService{
	private ConferenceReserveDao conferenceReserveDao;
	@Autowired
	private ConferenceReserveParticipantService conferenceReserveParticipantService;
	@Autowired
	private ConferenceReserveDeviceService conferenceReserveDeviceService;
	@Autowired
	private ConferenceRoomService conferenceRoomService;
	@Autowired
	private ConferenceAuditService conferenceAuditService;
	@Autowired
	private AuthorityInfoService authorityInfoService;
	@Autowired
	private ConferenceReceiptService conferenceReceiptService;
	@Autowired
	private ConferenceNoteService conferenceNoteService;
	@Autowired
	private AccessRecordService accessRecordService;
	@Autowired
	private SysFileService sysFileService;
	
	@Autowired
    public void setConferenceReserveDao(ConferenceReserveDao conferenceReserveDao) {
        this.conferenceReserveDao = conferenceReserveDao;
        setBaseDao(conferenceReserveDao);
    }
	
	/** (non-Javadoc)
	 * @Title: reserve 
	 * @Description: 周期性预定会议室 
	 * @param conferenceReserve
	 * @param dateList
	 * @param startTime
	 * @param endTime 
	 * @param isIgnoreConflict
	 * @throws ConferenceTimeConflictException, ConferenceDeviceNotAvailableException
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveService#reserve(com.suneee.eas.oa.model.conference.ConferenceReserve, java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public void reserve(ConferenceReserve conferenceReserve, List<Date> dateList, String startTime, String endTime, Boolean isIgnoreConflict)
			throws ConferenceTimeConflictException, ConferenceDeviceNotAvailableException, UnsupportedEncodingException {
		if(conferenceReserve == null || dateList == null){
			throw new IllegalArgumentException("conferenceReserve and dateList must not be null.");
		}
		Long cycNo = IdGeneratorUtil.getNextId();
		conferenceReserve.setCycNo(cycNo);
		for(Date date : dateList){
			reserve(conferenceReserve, date, startTime, endTime, isIgnoreConflict);
		}
	}
	
	/** (non-Javadoc)
	 * @Title: reserve 
	 * @Description: 非周期性预定会议室 
	 * @param conferenceReserve
	 * @param date
	 * @param startTime
	 * @param endTime 
	 * @param isIgnoreConflict
	 * @throws ConferenceTimeConflictException, ConferenceDeviceNotAvailableException
	 * @throws ConferenceDeviceNotAvailableException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveService#reserve(com.suneee.eas.oa.model.conference.ConferenceReserve, java.util.Date, java.lang.String, java.lang.String)
	 */
	@Override
	public void reserve(ConferenceReserve conferenceReserve, Date date, String startTime, String endTime, Boolean isIgnoreConflict)
			throws ConferenceTimeConflictException, ConferenceDeviceNotAvailableException, UnsupportedEncodingException {
		if(conferenceReserve == null || conferenceReserve.getParticipantIds() == null 
				|| conferenceReserve.getParticipantNames() == null || conferenceReserve.getOrganizer() == null
				|| conferenceReserve.getOrganizerName() == null || conferenceReserve.getRecorder() == null
				|| conferenceReserve.getRecorderName() == null){
			throw new IllegalArgumentException("conferenceReserve,  conferenceReserve.participantIds, conferenceReserve.participantNames, conferenceReserve.organizer, conferenceReserve.organizerName, conferenceReserve.recorder and conferenceReserve.recorderName must not be null.");
		}
		// 开始时间
		Date startDate = CalendarUtil.getDate(date, startTime);
		// 结束时间
		Date endDate = CalendarUtil.getDate(date, endTime);
		
		// 查询在开始时间和结束时间内，会议室是否存在冲突
		List<ConferenceReserve> reserveList = this.listAll(conferenceReserve.getRoomId(), startDate, endDate, null);
		if(reserveList.size() > 0){
			// 非周期性会议存在会议时间冲突时，直接抛出异常
			if(ConferenceReserve.CYC_NO.equals(conferenceReserve.getCycType())){
				MessageUtil.addMsg(DateFormatUtil.formaDatetTime(startDate));
				MessageUtil.addMsg(DateFormatUtil.formaDatetTime(endDate));
				throw new ConferenceTimeConflictException("there are other conferences between " 
						+ DateFormatUtil.formaDatetTime(startDate) + " and " + DateFormatUtil.formaDatetTime(endDate));
			} 
			// 周期性会议存在会议时间冲突时，需要根据是否忽略冲突字段来判断后续处理
			else {
				// 若忽略冲突，则不生成冲突时间段内的会议预定记录，直接返回
				if(isIgnoreConflict){
					return;
				} else {
					MessageUtil.addMsg(DateFormatUtil.formaDatetTime(startDate));
					MessageUtil.addMsg(DateFormatUtil.formaDatetTime(endDate));
					throw new ConferenceTimeConflictException("there are other conferences between " 
							+ DateFormatUtil.formaDatetTime(startDate) + " and " + DateFormatUtil.formaDatetTime(endDate));
				}
			}
		}
		
		// 会议ID
		Long conferenceId = IdGeneratorUtil.getNextId();
		// 保存预定会议设备信息
		conferenceReserveDeviceService.save(conferenceId, conferenceReserve.getRegion(), startDate, 
				endDate, conferenceReserve.getDeviceTypeIds(), conferenceReserve.getDeviceTypeNames());
		
		// 保存与会人员信息
		Map<String, String> participants = this.handleParticipants(conferenceReserve);
		conferenceReserveParticipantService.save(conferenceId, ConferenceReserveParticipant.TYPE_PENDING, 
				participants.get("participantIds"), participants.get("participantNames"));
		
		// 保存可下载附件用户
		authorityInfoService.save(conferenceId, AuthorityInfo.AUTHTYPE_CONFERENCE_ATTACH, 
				conferenceReserve.getDownloadUserIds(), conferenceReserve.getDownloadUserNames(), AuthorityInfo.OWNERTYPE_USER);
		
		// 查询会议室信息
		ConferenceRoom room = conferenceRoomService.findById(conferenceReserve.getRoomId());
		// 若会议室设置为需要审核，则审核记录状态、会议状态设置为待审批；否则审核记录状态设置为、会议状态设置为已审核
		Byte auditRecordStatus, auditStatus;
		if(ConferenceRoom.AUDIT_YES.equals(room.getNeedAudit())){
			auditRecordStatus = StatusConstant.STATUS_PENDING;
			auditStatus = StatusConstant.STATUS_PENDING;
		} else {
			auditRecordStatus = StatusConstant.STATUS_PASS;
			auditStatus = StatusConstant.STATUS_PASS;
		}
		// 保存审核记录
		String[] auditors = room.getManager().split(",");
		conferenceAuditService.save(conferenceId, ConferenceAudit.TARGET_TYPE_CONFERENCE, 
				Long.parseLong(auditors[0]), auditRecordStatus, (byte)0, null);
		
		// 保存会议信息
		conferenceReserve.setConferenceId(conferenceId);
		conferenceReserve.setBeginTime(startDate);
		conferenceReserve.setEndTime(endDate);
		conferenceReserve.setNoteStatus(StatusConstant.STATUS_NOT_SUBMIT);
		conferenceReserve.setAuditStatus(auditStatus);
		conferenceReserve.setCreateBy(ContextSupportUtil.getCurrentUserId());
		conferenceReserve.setCreateByName(ContextSupportUtil.getCurrentUsername());
		conferenceReserve.setCreateTime(new Date());
		conferenceReserve.setUpdateBy(ContextSupportUtil.getCurrentUserId());
		conferenceReserve.setUpdateByName(ContextSupportUtil.getCurrentUsername());
		conferenceReserve.setUpdateTime(new Date());
		conferenceReserve.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
		conferenceReserve.setIsDelete(StatusConstant.DELETE_NO);
		this.save(conferenceReserve);
		
		// 发送会议通知消息
		// 发送审批通知消息
		
	}
	
	/** 
	 * @Title: update 
	 * @Description: 更新会议信息
	 * @param conferenceReserve
	 * @param startDate
	 * @param startTime
	 * @param endTime 
	 * @throws ConferenceTimeConflictException
	 * @throws ConferenceDeviceNotAvailableException 
	 * @throws AlreadyStartedException 
	 * @throws UserPermissionException 
	 * @throws IllegalStatusException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveService#update(com.suneee.eas.oa.model.conference.ConferenceReserve, java.util.Date, java.lang.String, java.lang.String)
	 */
	@Override
	public void update(ConferenceReserve conferenceReserve, Date date, String startTime, String endTime) throws ConferenceTimeConflictException,
			ConferenceDeviceNotAvailableException, AlreadyStartedException, UserPermissionException, IllegalStatusException, UnsupportedEncodingException {
		if(conferenceReserve == null || conferenceReserve.getParticipantIds() == null 
				|| conferenceReserve.getParticipantNames() == null || conferenceReserve.getOrganizer() == null
				|| conferenceReserve.getOrganizerName() == null || conferenceReserve.getRecorder() == null
				|| conferenceReserve.getRecorderName() == null){
			throw new IllegalArgumentException("conferenceReserve,  conferenceReserve.participantIds, conferenceReserve.participantNames, conferenceReserve.organizer, conferenceReserve.organizerName, conferenceReserve.recorder and conferenceReserve.recorderName must not be null.");
		}
		// 会议ID
		Long conferenceId = conferenceReserve.getConferenceId();
		// 获取会议信息
		ConferenceReserve reserveDb = this.findById(conferenceId);
		if(reserveDb == null){
			throw new NotExistInDatabaseException("CofnerenceReserve record of " + conferenceId + " is not exist in database.");
		}
		
		// 当前用户不是会议发起人不可修改
		if(!reserveDb.getCreateBy().equals(ContextSupportUtil.getCurrentUserId())){
			throw new UserPermissionException("the user " + ContextSupportUtil.getCurrentUserId() + " is not the start user of conference " + conferenceId);
		}
		
		// 非待审批状态的会议不可编辑
		if(reserveDb.getAuditStatus() != StatusConstant.STATUS_PENDING){
			throw new IllegalStatusException("the conferece status is " + reserveDb.getAuditStatus() + ", cannot be modified.");
		}
		
		// 会议已经开始，不可编辑
		if(reserveDb.getBeginTime().compareTo(new Date()) <= 0){
			throw new AlreadyStartedException("conference " + conferenceId + " is already started.");
		}
		
		// 开始时间
		Date startDate = CalendarUtil.getDate(date, startTime);
		// 结束时间
		Date endDate = CalendarUtil.getDate(date, endTime);
		
		// 查询在开始时间和结束时间内，会议室是否存在冲突
		List<ConferenceReserve> reserveList = this.listAll(conferenceReserve.getRoomId(), startDate, endDate, conferenceId);
		if(reserveList.size() > 0){
			MessageUtil.addMsg(DateFormatUtil.formaDatetTime(startDate));
			MessageUtil.addMsg(DateFormatUtil.formaDatetTime(endDate));
			throw new ConferenceTimeConflictException("there are other conferences between " 
					+ DateFormatUtil.formaDatetTime(startDate) + " and " + DateFormatUtil.formaDatetTime(endDate));
		}
		
		// 更新预定会议设备信息
		conferenceReserveDeviceService.update(conferenceId, conferenceReserve.getRegion(), startDate, 
				endDate, conferenceReserve.getDeviceTypeIds(), conferenceReserve.getDeviceTypeNames());
		
		// 更新与会人员信息
		Map<String, String> participants = this.handleParticipants(conferenceReserve);
		conferenceReserveParticipantService.update(conferenceId, ConferenceReserveParticipant.TYPE_PENDING, 
				participants.get("participantIds"), participants.get("participantNames"));
		
		// 更新可下载附件用户
		authorityInfoService.update(conferenceId, AuthorityInfo.AUTHTYPE_CONFERENCE_ATTACH, 
				conferenceReserve.getDownloadUserIds(), conferenceReserve.getDownloadUserNames(), AuthorityInfo.OWNERTYPE_USER);
		
		// 查询会议室信息
		ConferenceRoom room = conferenceRoomService.findById(conferenceReserve.getRoomId());
		// 若会议室设置为需要审核，则审核记录状态、会议状态设置为待审批；否则审核记录状态设置为、会议状态设置为已审核
		Byte auditRecordStatus, auditStatus;
		if(room.getNeedAudit() == ConferenceRoom.AUDIT_YES){
			auditRecordStatus = StatusConstant.STATUS_PENDING;
			auditStatus = StatusConstant.STATUS_PENDING;
		} else {
			auditRecordStatus = StatusConstant.STATUS_PASS;
			auditStatus = StatusConstant.STATUS_PASS;
		}
		// 更新审核记录
		String[] auditors = room.getManager().split(",");
		conferenceAuditService.update(conferenceId, 
				ConferenceAudit.TARGET_TYPE_CONFERENCE, Long.parseLong(auditors[0]), auditRecordStatus);
		
		// 更新会议信息
		conferenceReserve.setBeginTime(startDate);
		conferenceReserve.setEndTime(endDate);
		conferenceReserve.setNoteStatus(StatusConstant.STATUS_NOT_SUBMIT);
		conferenceReserve.setAuditStatus(auditStatus);
		conferenceReserve.setUpdateBy(ContextSupportUtil.getCurrentUserId());
		conferenceReserve.setUpdateByName(ContextSupportUtil.getCurrentUsername());
		conferenceReserve.setUpdateTime(new Date());
		this.update(conferenceReserve);
	}

	/** 
	 * @Title: updateNoteStatus 
	 * @Description: 更新会议纪要状态
	 * @param conferenceId
	 * @param status 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveService#updateNoteStatus(java.lang.Long, java.lang.Byte)
	 */
	@Override
	public void updateNoteStatus(Long conferenceId, Byte status){
		conferenceReserveDao.updateNoteStatus(conferenceId, status);
	}
	
	/** 
	 * @Title: updateAuditStatus 
	 * @Description: 更新会议记录
	 * @param conferenceId
	 * @param status 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveService#updateAuditStatus(java.lang.Long, java.lang.Byte)
	 */
	@Override
	public void updateAuditStatus(Long conferenceId, Byte status){
		conferenceReserveDao.updateAuditStatus(conferenceId, status);
	}
	
	/**  
	 * @Title: audit 
	 * @Description: 会议审批
	 * @param conferenceAudit
	 * @param status
	 * @throws IllegalStatusException
	 * @throws IllegalTargetTypeException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveService#audit(java.lang.Byte, java.lang.String)
	 */
	@Override
	public void audit(ConferenceAudit conferenceAudit, Byte status)
			throws IllegalStatusException, IllegalTargetTypeException, UnsupportedEncodingException {
		if(conferenceAudit == null){
			throw new IllegalArgumentException("conferenceAudit must not be null.");
		}
		
		// 判断是否是会议审批记录
		if(conferenceAudit.getTargetType() == ConferenceAudit.TARGET_TYPE_CONFERENCE){
			throw new IllegalTargetTypeException("ConferenceAudit record's targetType must be conference in this situation");
		}
		
		// 会议ID
		Long conferenceId = conferenceAudit.getTargetId();
		// 查询会议信息
		ConferenceReserve reserve = this.findById(conferenceId);
		if(reserve == null){
			throw new NotExistInDatabaseException("ConferenceReserve record of " + conferenceId + " is not exist in database.");
		}
		
		// 查询会议室信息
		Long roomId = reserve.getRoomId();
		ConferenceRoom room = conferenceRoomService.findById(roomId);
		if(room == null){
			throw new NotExistInDatabaseException("ConferenceRoom record of " + roomId + " is not exist in database.");
		}
		
		// 获取会议的审批状态
		Byte auditStatus = null;
		// 审批通过
		if(status == StatusConstant.STATUS_PASS){
			// 获取下一步审批人
			Long nextCheker = conferenceAuditService.getNextChecker(conferenceAudit.getStep(), room.getManager());
			// 如果下一步审批人为空，则说明会议纪要审批结束，会议纪要审批状态为审批通过
			if(nextCheker == null){
				auditStatus = StatusConstant.STATUS_PASS;
			} 
			// 如果 下一步审批人不为空，则会议纪要审批状态为审批中，同时保存下一步的审批记录
			else {
				auditStatus = StatusConstant.STATUS_PROCESSING;
				conferenceAuditService.save(conferenceId, ConferenceAudit.TARGET_TYPE_CONFERENCE, nextCheker, StatusConstant.STATUS_PENDING, 
						(byte)(conferenceAudit.getStep() + 1), conferenceAudit.getStepPath());
			}
		} 
		// 驳回
		else if (status == StatusConstant.STATUS_REJECT){
			auditStatus = StatusConstant.STATUS_REJECT;
		} else {
			throw new IllegalStatusException("audit status " + status + " is not supported.");
		}
		
		// 更新会议状态
		this.updateAuditStatus(conferenceId, auditStatus);
	}
	
	/** 
	 * @Title: cancel 
	 * @Description: 取消会议
	 * @param conferenceId
	 * @throws AlreadyStartedException
	 * @throws UserPermissionException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveService#cancel(java.lang.Long)
	 */
	@Override
	public void cancel(Long conferenceId) throws AlreadyStartedException, UserPermissionException{
		if(conferenceId == null){
			throw new IllegalArgumentException("conferenceId must not be null.");
		}
		
		// 查询会议信息
		ConferenceReserve reserveDb = this.findById(conferenceId);
		if(reserveDb == null){
			throw new NotExistInDatabaseException("ConferenceReserve record of " + conferenceId + " is not exist in database." );
		}
		
		// 如果会议已经开始，则不可取消
		if(reserveDb.getBeginTime().compareTo(new Date()) <= 0){
			throw new AlreadyStartedException("conference " + conferenceId + " is already started.");
		}
		
		// 当前用户不是会议申请人，不可取消
		if(!reserveDb.getCreateBy().equals(ContextSupportUtil.getCurrentUserId())){
			throw new UserPermissionException("the user " + ContextSupportUtil.getCurrentUserId() + " is not the start user.");
		}
		
		// 删除会议设备
		conferenceReserveDeviceService.deleteByConferenceId(conferenceId);
		
		// 删除与会人员
		conferenceReserveParticipantService.deleteByConferenceIdAndType(conferenceId, ConferenceReserveParticipant.TYPE_PENDING);
		
		// 删除附件权限信息
		authorityInfoService.deleteBy(conferenceId, AuthorityInfo.AUTHTYPE_CONFERENCE_ATTACH, AuthorityInfo.OWNERTYPE_USER);
		
		// 删除审批信息
		conferenceAuditService.deleteByTargetIdAndType(conferenceId, ConferenceAudit.TARGET_TYPE_CONFERENCE);
		
		// 删除会议回执信息
		conferenceReceiptService.deleteByConferenceId(conferenceId);
		// 删除会议信息
		this.deleteById(conferenceId);
	}
	
	/** 
	 * @Title: finish 
	 * @Description: 提前结束会议
	 * @param conferenceId
	 * @throws NotStartedException
	 * @throws AlreadyEndedException
	 * @throws UserPermissionException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveService#finish(java.lang.Long)
	 */
	@Override
	public void finish(Long conferenceId) throws NotStartedException, AlreadyEndedException, UserPermissionException{
		if(conferenceId == null){
			throw new IllegalArgumentException("conferenceId must not be null.");
		}
		
		// 查询会议信息
		ConferenceReserve reserveDb = this.findById(conferenceId);
		if(reserveDb == null){
			throw new NotExistInDatabaseException("ConferenceReserve record of " + conferenceId + " is not exist in database." );
		}
		
		// 如果会议未开始，则不可执行提前结束操作
		if(reserveDb.getBeginTime().compareTo(new Date()) > 0){
			throw new NotStartedException("the conference " + conferenceId + " has not yet begun.");
		}
		
		// 会议已结束，则不可执行提前结束操作
		if(reserveDb.getEndTime().compareTo(new Date()) < 0){
			throw new AlreadyEndedException("the conference " + conferenceId + " already ended.");
		}
		
		// 不是会议发起人，不可执行提前结束操作
		if(!reserveDb.getCreateBy().equals(ContextSupportUtil.getCurrentUserId())){
			throw new UserPermissionException("the user " + ContextSupportUtil.getCurrentUserId() + " is not the start user of the conference " + conferenceId);
		}
		
		// 更新会议的结束时间为当前时间
		conferenceReserveDao.updateEndTime(conferenceId);
	}
	
	/** 
	 * @Title: findById 
	 * @Description: 根据会议ID获取会议信息
	 * @param conferenceId
	 * @return 
	 * @see com.suneee.eas.common.service.impl.BaseServiceImpl#findById(java.lang.Long)
	 */
	@Override
	public ConferenceReserve findById(Long conferenceId){
		ConferenceReserve reserve = this.conferenceReserveDao.findById(conferenceId);
		if(reserve == null){
			throw new NotExistInDatabaseException("CofnerenceReserve record of " + conferenceId + " is not exist in database.");
		}
		// 查询出席人员
		List<ConferenceReserveParticipant> participantList = this.conferenceReserveParticipantService.getByConferenceIdAndType(conferenceId, ConferenceReserveParticipant.TYPE_PENDING);
		List<Long> participantIds = new ArrayList<>();
		List<String> participantNames = new ArrayList<>();
		int participantSize = participantList.size();
		for(int i = 0; i < participantSize; i++){
			participantIds.add(participantList.get(i).getUserId());
			participantNames.add(participantList.get(i).getUserName());
		}
		reserve.setParticipantIds(StringUtils.join(participantIds, ","));
		reserve.setParticipantNames(StringUtils.join(participantNames, ","));
		
		// 查询预定会议设备
		List<ConferenceReserveDevice> deviceList = this.conferenceReserveDeviceService.getByConferenceId(conferenceId);
		List<Long> deviceTypeIds = new ArrayList<>();
		List<String> deviceTypeNames = new ArrayList<>();
		int deviceSize = deviceList.size();
		for(int i = 0; i < deviceSize; i++){
			deviceTypeIds.add(deviceList.get(i).getDeviceType());
			deviceTypeNames.add(deviceList.get(i).getDeviceTypeName());
		}
		reserve.setDeviceTypeIds(StringUtils.join(deviceTypeIds, ","));
		reserve.setDeviceTypeNames(StringUtils.join(deviceTypeNames, ","));
		
		// 查询可下载附件用户
		List<AuthorityInfo> authList = authorityInfoService.getBy(conferenceId, 
				AuthorityInfo.AUTHTYPE_CONFERENCE_ATTACH, AuthorityInfo.OWNERTYPE_USER);
		List<Long> downloadUserIds = new ArrayList<>();
		List<String> downloadUserNames = new ArrayList<>();
		int authSize = authList.size();
		for(int i = 0; i < authSize; i++){
			downloadUserIds.add(authList.get(i).getOwnerId());
			downloadUserNames.add(authList.get(i).getOwnerName());
		}
		reserve.setDownloadUserIds(StringUtils.join(downloadUserIds, ","));
		reserve.setDownloadUserNames(StringUtils.join(downloadUserNames, ","));
		
		// 当没有设置附件下载权限或者当前用户有权限下载附件时，查询附件下载地址
		if(downloadUserIds.size() == 0 || downloadUserIds.contains(ContextSupportUtil.getCurrentUserId())){
			String attachmentIds = reserve.getAttachmentIds();
			if(StringUtils.isNotBlank(attachmentIds)){
				// 获取附件下载地址
				String[] attachmentIdArr = attachmentIds.split(",");
				List<String> downloadUrls = new ArrayList<>();
				SysFile sysFile = null;
				for(String attachmentId : attachmentIdArr){
					sysFile = sysFileService.findById(Long.parseLong(attachmentId));
					downloadUrls.add(FileUtil.getDownloadUrl(sysFile.getPath()));
				}
				
				// 设置附件下载地址
				reserve.setAttachmentUrl(downloadUrls);
			}
		}
		return reserve;
	}
	
	/** 
	 * @Title: getReserveDetails 
	 * @Description: 获取会议详细信息
	 * @param conferenceId
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveService#getReserveDetails(java.lang.Long)
	 */
	public Map<String, Object> getReserveDetails(Long conferenceId){
		// 获取会议基本信息
		ConferenceReserve reserve = this.findById(conferenceId);
		// 查询会议室信息
		ConferenceRoom room = conferenceRoomService.findById(reserve.getRoomId());
		// 获取会议纪要基本信息
		ConferenceNote note = this.conferenceNoteService.getByConferenceId(conferenceId);
		// 获取会议纪要的审批记录
		List<ConferenceAudit> noteAudits = null;
		if(note != null){
			// 会议纪要审批记录
			noteAudits = this.conferenceAuditService.getByTargetIdAndType(note.getNoteId(), ConferenceAudit.TARGET_TYPE_CONFERENCE_NOTE);
			
			// 当前用户不在指定读者内或者会议纪要的状态不为发布，不能查看会议纪要信息
			if((StringUtils.isNotBlank(note.getReaderIds()) 
					&& !note.getReaderIds().contains(ContextSupportUtil.getCurrentUserId().toString()))
					|| !StatusConstant.STATUS_PASS.equals(reserve.getNoteStatus())){
				note = null;
			}
		}
		// 获取回执信息
		List<ConferenceReceipt> receipts = this.conferenceReceiptService.getByConferenceId(conferenceId);
		// 获取会议审批意见
		List<ConferenceAudit> reserveAudits = this.conferenceAuditService.getByTargetIdAndType(conferenceId, ConferenceAudit.TARGET_TYPE_CONFERENCE);
		// 获取访问记录
		List<AccessRecord> accessRecords = this.accessRecordService.getByTargetIdAndType(conferenceId, AccessRecord.TARGET_TYPE_CONFERENCE);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("conferenceReserve", reserve);
		result.put("conferenceRoom", room);
		result.put("conferenceNote", note);
		result.put("conferenceReceipts", receipts);
		result.put("reserveAudits", reserveAudits);
		result.put("noteAudits", noteAudits);
		result.put("accessRecords", accessRecords);
		return result;
	}
	/** 
	 * 查询指定时间段内指定会议室的预定情况
	 * @param roomId
	 * @param startTime
	 * @param endTime
	 * @param cofnerenceId
	 * @return
	 */
	@Override
	public List<ConferenceReserve> listAll(Long roomId, Date startTime, Date endTime, Long conferenceId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roomId", roomId);
		params.put("beginTime", startTime);
		params.put("endTime", endTime);
		params.put("conferenceId", conferenceId);
		return this.listAll(params);
	}
	/** 
	 * 条件查询会议预定信息
	 * @param params
	 * @return
	 */
	@Override
	public List<ConferenceReserve> listAll(Map<String, Object> params){
		return conferenceReserveDao.listAll(params);
	}
	
	/** 
	 * @param reserve
	 * @return
	 */
	private Map<String, String> handleParticipants(ConferenceReserve reserve){
		String participantIds = reserve.getParticipantIds();
		String participantNames = reserve.getParticipantNames();
		List<String> participantId = Arrays.asList(participantIds.split(","));
		// 如果与会人员不包含会议组织人，则将会议组织人加入与会人员中
		if(!participantId.contains(reserve.getOrganizer().toString())){
			participantIds = participantIds + "," + reserve.getOrganizer();
			participantNames = participantNames + "," + reserve.getOrganizerName();
		}
		// 如果与会人员不包含会议纪要员，则将会议纪要员加入与会人员中
		if(!participantId.contains(reserve.getRecorder().toString()) && !reserve.getOrganizer().equals(reserve.getRecorder())){
			participantIds = participantIds + "," + reserve.getRecorder();
			participantNames = participantNames + "," + reserve.getRecorderName();
		}
		Map<String, String> participant = new HashMap<String, String>();
		participant.put("participantIds", participantIds);
		participant.put("participantNames", participantNames);
		return participant;
	}
}

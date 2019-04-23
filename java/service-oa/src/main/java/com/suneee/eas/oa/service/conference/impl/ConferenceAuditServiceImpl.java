/**
 * @Title: ConferenceAuditServiceImpl.java 
 * @Package com.suneee.eas.oa.service.conference.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference.impl;

import com.suneee.eas.common.constant.StatusConstant;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.conference.ConferenceAuditDao;
import com.suneee.eas.oa.exception.common.NotExistInDatabaseException;
import com.suneee.eas.oa.exception.conference.AlreadyHandledException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.IllegalTargetTypeException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.ConferenceAudit;
import com.suneee.eas.oa.service.conference.ConferenceAuditService;
import com.suneee.eas.oa.service.conference.ConferenceNoteService;
import com.suneee.eas.oa.service.conference.ConferenceReserveService;
import com.suneee.eas.oa.service.user.UserService;
import com.suneee.platform.model.system.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ConferenceAuditServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 15:05:22 
 *
 */
@Service
public class ConferenceAuditServiceImpl extends BaseServiceImpl<ConferenceAudit> implements ConferenceAuditService{
	private ConferenceAuditDao conferenceAuditDao;
	@Autowired
	private ConferenceNoteService conferenceNoteService;
	@Autowired
	private ConferenceReserveService conferenceReserveService;
	@Autowired
	private UserService userService;
	@Autowired
	public void setConferenceAuditDao(ConferenceAuditDao conferenceAuditDao){
		this.conferenceAuditDao = conferenceAuditDao;
		setBaseDao(conferenceAuditDao);
	}
	
	/** 
	 * @Title: save 
	 * @Description: 保存审批意见 
	 * @param targetId
	 * @param targetType
	 * @param auditor
	 * @param status 
	 * @param step
	 * @param preStepPath
	 * @see com.suneee.eas.oa.service.conference.ConferenceAuditService#save(java.lang.Long, java.lang.String, java.lang.Long, java.lang.Byte)
	 */
	@Override
	public void save(Long targetId, String targetType, Long auditor, Byte status, Byte step, String preStepPath) throws UnsupportedEncodingException {
		if(targetId == null || targetType == null || auditor == null || status == null){
			throw new IllegalArgumentException("targetId, targetType, auditor and status must not be null.");
		}
		// 从BUC获取审批人信息
		SysUser user = userService.getUserDetails(auditor);
		
		// 构造审核对象ID
		ConferenceAudit conferenceAudit = new ConferenceAudit();
		Long auditId = IdGeneratorUtil.getNextId();
		conferenceAudit.setAuditId(auditId);
		conferenceAudit.setAuditor(auditor);
		conferenceAudit.setAuditorName(ContextSupportUtil.getUsername(user));
		conferenceAudit.setAuditStatus(status);
		conferenceAudit.setBeginTime(new Date());
		conferenceAudit.setIsRead(ConferenceAudit.READ_NO);
		conferenceAudit.setTargetId(targetId);
		conferenceAudit.setTargetType(targetType);
		conferenceAudit.setStep(step);
		String stepPath = auditId + ".";
		if(preStepPath != null){
			stepPath = preStepPath + stepPath;
		}
		conferenceAudit.setStepPath(stepPath);
		// 如果是已通过，则需要设置审批结束时间、审批持续时长、以及审批意见
		if(StatusConstant.STATUS_PASS == status){
			conferenceAudit.setEndTime(new Date());
			conferenceAudit.setDuration(conferenceAudit.getEndTime().getTime() - conferenceAudit.getBeginTime().getTime());
			conferenceAudit.setOpinion("同意（系统自动审批）");
		}
		this.save(conferenceAudit);
	}
	/** 
	 * @Title: update 
	 * @Description: 更新审批记录
	 * @param targetId
	 * @param targetType
	 * @param auditor
	 * @param status 
	 * @see com.suneee.eas.oa.service.conference.ConferenceAuditService#update(java.lang.Long, java.lang.String, java.lang.Long, java.lang.Byte)
	 */
	@Override
	public void update(Long targetId, String targetType, Long auditor, Byte status) throws UnsupportedEncodingException {
		// 删除已经存在的审批记录
		this.deleteByTargetIdAndType(targetId, targetType);
		// 保存新的审批记录
		this.save(targetId, targetType, auditor, status, (byte)0, null);
	}
	/** 
	 * @Title: update 
	 * @Description: 更新审批信息
	 * @param auditId
	 * @param status
	 * @param opinion 
	 * @see com.suneee.eas.oa.service.conference.ConferenceAuditService#update(java.lang.Long, java.lang.Byte, java.lang.String)
	 */
	public void update(Long auditId, Byte status, String opinion){
		if(auditId == null || status == null){
			throw new IllegalArgumentException("auditId and status must not be null");
		}
		ConferenceAudit conferenceAudit = this.findById(auditId);
		if(conferenceAudit == null){
			throw new NotExistInDatabaseException("ConferenceAudit record of " + auditId + " is not exist in database.");
		}
		
		// 更新审批信息
		conferenceAudit.setIsRead(ConferenceAudit.READ_YES);
		conferenceAudit.setAuditStatus(status);
		conferenceAudit.setOpinion(opinion);
		conferenceAudit.setEndTime(new Date());
		long surplus = conferenceAudit.getEndTime().getTime() - conferenceAudit.getBeginTime().getTime();
		conferenceAudit.setDuration(surplus);
		this.update(conferenceAudit);
	}
	/** 
	 * @Title: deleteByTargetIdAndType 
	 * @Description:  删除审批记录
	 * @param targetId
	 * @param targetType 
	 * @see com.suneee.eas.oa.service.conference.ConferenceAuditService#deleteByTargetIdAndType(java.lang.Long, java.lang.String)
	 */
	public void deleteByTargetIdAndType(Long targetId, String targetType){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("targetId", targetId);
		params.put("targetType", targetType);
		conferenceAuditDao.deleteByTargetIdAndType(params);
	}
	
	/** 
	 * @Title: audit 
	 * @Description: 审批
	 * @param auditId
	 * @param status
	 * @param opinion
	 * @throws UserPermissionException 
	 * @throws IllegalStatusException
	 * @throws IllegalTargetTypeException 
	 * @throws AlreadyHandledException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceAuditService#audit(java.lang.Long, java.lang.Byte, java.lang.String)
	 */
	public void audit(Long auditId, Byte status, String opinion)
			throws UserPermissionException, IllegalStatusException, IllegalTargetTypeException, AlreadyHandledException, UnsupportedEncodingException {
		if(auditId == null || status == null){
			throw new IllegalArgumentException("auditId and status must not be null.");
		}
		// 查询审批记录
		ConferenceAudit audit = this.findById(auditId);
		if(audit == null){
			throw new NotExistInDatabaseException("ConferenceAudit record of " + auditId + " is not exist in database.");
		}
		
		// 判断当前用户是否是审批人
		Long userId = ContextSupportUtil.getCurrentUserId();
		if(!userId.equals(audit.getAuditor())){
			throw new UserPermissionException("the current user " + userId + " is not the specified auditor " + audit.getAuditor() + ".");
		}
		
		// 如果审批记录的状态不是待审批，则不能审批
		if(!StatusConstant.STATUS_PENDING.equals(audit.getAuditStatus())){
			throw new AlreadyHandledException("ConferenceAudit record of " + auditId + " was already handled.");
		}
		
		// 更新审批记录状态
		this.update(auditId, status, opinion);
		
		// 会议审批
		if(ConferenceAudit.TARGET_TYPE_CONFERENCE.equals(audit.getTargetType())){
			conferenceReserveService.audit(audit, status);
		}
		// 会议纪要审批
		else if(ConferenceAudit.TARGET_TYPE_CONFERENCE_NOTE.equals(audit.getTargetType())){
			conferenceNoteService.audit(audit, status);
		}
	}
	
	/** 
	 * 获取下一个审批人
	 * @param currentStep 当前步骤
	 * @param checkers 所有审批人Id字符串
	 * @return
	 */
	public Long getNextChecker(Byte currentStep, String checkers){
		if(currentStep == null || checkers == null){
			throw new IllegalArgumentException("currentStep and checkers must not be null.");
		}
		
		Long nextChecker = null;
		String[] checkerArr = checkers.split(",");
		if(currentStep < (checkerArr.length - 1)){
			nextChecker = Long.parseLong(checkerArr[currentStep + 1]);
		}
		return nextChecker;
	}
	
	/** 
	 * @Title: getByTargetIdAndType 
	 * @Description: 获取审批记录
	 * @param targetId
	 * @param targetType
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceAuditService#getByTargetIdAndType(java.lang.Long, java.lang.String)
	 */
	public List<ConferenceAudit> getByTargetIdAndType(Long targetId, String targetType){
		return this.conferenceAuditDao.getByTargetIdAndType(targetId, targetType);
	}
}

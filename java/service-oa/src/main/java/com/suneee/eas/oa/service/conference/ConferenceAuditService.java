/**
 * @Title: ConferenceAuditService.java 
 * @Package com.suneee.eas.oa.service.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.exception.conference.AlreadyHandledException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.IllegalTargetTypeException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.ConferenceAudit;

/**
 * @ClassName: ConferenceAuditService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 15:04:53 
 *
 */
public interface ConferenceAuditService extends BaseService<ConferenceAudit>{
	/** 
	 * 保存审批意见
	 * @param targetId 
	 * @param targetType
	 * @param auditor
	 * @param status
	 * @param step
	 * @param preStepPath
	 */
	public void save(Long targetId, String targetType, Long auditor, Byte status, Byte step, String preStepPath) throws UnsupportedEncodingException;
	
	/** 更新审批意见
	 * @param targetId
	 * @param targetType
	 * @param auditor
	 * @param status
	 */
	public void update(Long targetId, String targetType, Long auditor, Byte status) throws UnsupportedEncodingException;
	
	/** 根据审批记录更新审批意见
	 * @param auditId
	 * @param status
	 * @param opinion
	 */
	public void update(Long auditId, Byte status, String opinion);
	
	/** 删除审批记录
	 * @param targetId
	 * @param targetType
	 */
	public void deleteByTargetIdAndType(Long targetId, String targetType);
	
	/** 审批
	 * @param auditId
	 * @param status
	 * @param opinion
	 * @throws UserPermissionException
	 * @throws IllegalStatusException
	 * @throws IllegalTargetTypeException
	 * @throws AlreadyHandledException
	 */
	public void audit(Long auditId, Byte status, String opinion)
			throws UserPermissionException, IllegalStatusException, IllegalTargetTypeException, AlreadyHandledException, UnsupportedEncodingException;
	
	/** 获取下一步审批人
	 * @param currentStep
	 * @param checkers
	 * @return
	 */
	public Long getNextChecker(Byte currentStep, String checkers);
	
	/** 获取审批记录
	 * @param targetId
	 * @param targetType
	 * @return
	 */
	List<ConferenceAudit> getByTargetIdAndType(Long targetId, String targetType);
}

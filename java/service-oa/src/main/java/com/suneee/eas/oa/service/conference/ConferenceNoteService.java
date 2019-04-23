/**
 * @Title: ConferenceNoteService.java 
 * @Package com.suneee.eas.oa.service.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.exception.conference.AlreadyExistException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.IllegalTargetTypeException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.ConferenceAudit;
import com.suneee.eas.oa.model.conference.ConferenceNote;
import com.suneee.eas.oa.model.conference.ConferenceReserve;

import java.io.UnsupportedEncodingException;

/**
 * @ClassName: ConferenceNoteService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 09:21:42 
 *
 */
public interface ConferenceNoteService extends BaseService<ConferenceNote>{
	
	/** 添加会议纪要
	 * @param conferenceNote
	 * @throws UserPermissionException
	 */
	public void saveNote(ConferenceNote conferenceNote) throws UserPermissionException, AlreadyExistException, UnsupportedEncodingException;
	
	/** 更新会议纪要
	 * @param conferenceNote
	 * @throws UserPermissionException
	 * @throws IllegalStatusException
	 */
	public void updateNote(ConferenceNote conferenceNote) throws UserPermissionException, IllegalStatusException, UnsupportedEncodingException;
	/** 会议纪要审批
	 * @param conferenceAudit
	 * @param status
	 * @throws IllegalTargetTypeException
	 * @throws IllegalStatusException
	 */
	public void audit(ConferenceAudit conferenceAudit, Byte status) 
			throws IllegalTargetTypeException, IllegalStatusException;
	/** 更新会议纪要状态
	 * @param noteId
	 * @param status
	 */
	public void updateStatus(Long noteId, Byte status);
	
	/** 获取指定会议的会议纪要
	 * @param conferenceId
	 * @return
	 */
	public ConferenceNote getByConferenceId(Long conferenceId);
	
	/** 根据会议ID获取会议纪要详情
	 * @param conferenceId
	 * @return
	 */
	public ConferenceReserve getNoteDetails(Long conferenceId);
	
	/** 获取会议纪要分页列表
	 * @param filter
	 * @return
	 */
	public Pager<ConferenceReserve> getAll(QueryFilter filter);
}

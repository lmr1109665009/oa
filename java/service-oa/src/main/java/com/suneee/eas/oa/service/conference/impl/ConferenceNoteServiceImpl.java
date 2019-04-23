/**
 * @Title: ConferenceNoteServiceImpl.java 
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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.constant.StatusConstant;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.conference.ConferenceNoteDao;
import com.suneee.eas.oa.exception.common.NotExistInDatabaseException;
import com.suneee.eas.oa.exception.conference.AlreadyExistException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.IllegalTargetTypeException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.AuthorityInfo;
import com.suneee.eas.oa.model.conference.ConferenceAudit;
import com.suneee.eas.oa.model.conference.ConferenceNote;
import com.suneee.eas.oa.model.conference.ConferenceReserve;
import com.suneee.eas.oa.model.conference.ConferenceReserveParticipant;
import com.suneee.eas.oa.model.system.SysFile;
import com.suneee.eas.oa.service.conference.AuthorityInfoService;
import com.suneee.eas.oa.service.conference.ConferenceAuditService;
import com.suneee.eas.oa.service.conference.ConferenceNoteService;
import com.suneee.eas.oa.service.conference.ConferenceReserveParticipantService;
import com.suneee.eas.oa.service.conference.ConferenceReserveService;
import com.suneee.eas.oa.service.system.SysFileService;

/**
 * @ClassName: ConferenceNoteServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 09:22:16 
 *
 */
@Service
public class ConferenceNoteServiceImpl extends BaseServiceImpl<ConferenceNote> implements ConferenceNoteService{
	private ConferenceNoteDao conferenceNoteDao;
	@Autowired
	private AuthorityInfoService authorityInfoService;
	@Autowired
	private ConferenceReserveParticipantService conferenceReserveParticipantService;
	@Autowired
	private ConferenceAuditService conferenceAuditService;
	@Autowired
	private ConferenceReserveService conferenceReserveService;
	@Autowired
	private SysFileService sysFileService;
	@Autowired
	public void setConferenceNoteDao(ConferenceNoteDao conferenceNoteDao){
		this.conferenceNoteDao = conferenceNoteDao;
		setBaseDao(conferenceNoteDao);
	}
	
	/** 
	 * @Title: saveNote 
	 * @Description: 添加会议纪要
	 * @param conferenceNote
	 * @throws UserPermissionException
	 * @throws AlreadyExistException 
	 * @throws UnsupportedEncodingException
	 * @see com.suneee.eas.oa.service.conference.ConferenceNoteService#saveNote(com.suneee.eas.oa.model.conference.ConferenceNote)
	 */
	public void saveNote(ConferenceNote conferenceNote) throws UserPermissionException, AlreadyExistException, UnsupportedEncodingException {
		if(conferenceNote == null || conferenceNote.getConferenceId() == null){
			throw new IllegalArgumentException("conferenceNote and conferenceNote.conferenceId must not be null.");
		}

		// 根据会议ID获取会议纪要详情
		Long conferenceId = conferenceNote.getConferenceId();
		ConferenceReserve conferenceReserve = this.getNoteDetails(conferenceId);
		if(conferenceReserve == null){
			throw new NotExistInDatabaseException("ConferenceReserve record of " + conferenceId + " is not exist in database.");
		}
		
		// 如果当前用户不是会议指定的会议纪要员，则不可保存会议纪要
		Long recorder = conferenceReserve.getRecorder();
		if(!recorder.equals(ContextSupportUtil.getCurrentUserId())){
			throw new UserPermissionException("the current user " + ContextSupportUtil.getCurrentUserId() + " is not the specified recorder of conference " + conferenceId);
		}
		
		// 如果会议的会议纪要已经存在，不可重复添加
		if(conferenceReserve.getConferenceNote() != null){
			throw new AlreadyExistException("the conference note for conference " + conferenceId + " has already existed.");
		}
		
		Long noteId = IdGeneratorUtil.getNextId();
		// 保存会议纪要指定读者信息
		Map<String, String> readers = this.handleReader(conferenceNote.getReaderIds(), conferenceNote.getReaderNames(), conferenceReserve);
		authorityInfoService.save(noteId, AuthorityInfo.AUTHTYPE_CONFERENCE_NOTE, 
				readers.get("readerIds"), readers.get("readerNames"), AuthorityInfo.OWNERTYPE_USER);
		
		// 保存实际与会人员
		conferenceReserveParticipantService.save(conferenceNote.getConferenceId(), 
				ConferenceReserveParticipant.TYPE_ATTENDED, conferenceNote.getParticipantIds(), conferenceNote.getParticipantNames());
		
		// 获取会议组织人
		Long organizer = conferenceReserve.getOrganizer();
		Byte status = null;
		// 如果会议组织人与会议纪要员是同一个人，则会议纪要状态为审批通过
		if(organizer.equals(recorder)){
			status = StatusConstant.STATUS_PASS;
		} else {
			status = StatusConstant.STATUS_PENDING;
		}
		// 保存会议纪要审批信息
		conferenceAuditService.save(noteId, ConferenceAudit.TARGET_TYPE_CONFERENCE_NOTE, 
				organizer, status, (byte)0, null);
		
		// 更新会议记录的会议纪要状态
		conferenceReserveService.updateNoteStatus(conferenceNote.getConferenceId(), status);
		
		// 保存会议纪要
		conferenceNote.setNoteId(noteId);
		conferenceNote.setCreateBy(ContextSupportUtil.getCurrentUserId());
		conferenceNote.setCreateByName(ContextSupportUtil.getCurrentUsername());
		conferenceNote.setCreateTime(new Date());
		conferenceNote.setUpdateBy(ContextSupportUtil.getCurrentUserId());
		conferenceNote.setUpdateByName(ContextSupportUtil.getCurrentUsername());
		conferenceNote.setUpdateTime(new Date());
		conferenceNote.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
		conferenceNote.setStatus(status);
		conferenceNote.setIsDelete(StatusConstant.DELETE_NO);
		this.save(conferenceNote);
	}
	
	public void updateNote(ConferenceNote conferenceNote) throws UserPermissionException, IllegalStatusException, UnsupportedEncodingException {
		if(conferenceNote == null || conferenceNote.getNoteId() == null){
			throw new IllegalArgumentException("conferenceNote and conferenceNote.noteId must not be null.");
		}
		// 会议纪要ID
		Long noteId = conferenceNote.getNoteId();
		// 会议ID
		Long conferenceId = conferenceNote.getConferenceId();
		
		// 查询会议纪要信息
		ConferenceNote noteDb = this.findById(noteId);
		if(noteDb == null){
			throw new NotExistInDatabaseException("ConferenceNote record of " + noteId + " is not exist in database.");
		}
		
		// 当前用户不是会议纪要创建人不能修改
		Long userId = ContextSupportUtil.getCurrentUserId();
		if(!noteDb.getCreateBy().equals(userId)){
			throw new UserPermissionException("the current user " + userId + " is not the user who created the conference note " + noteId);
		}
		
		// 仅在待审批和驳回状态下能更新会议纪要
		if(noteDb.getStatus() != StatusConstant.STATUS_PENDING && noteDb.getStatus() != StatusConstant.STATUS_REJECT){
			throw new IllegalStatusException("the conference note status is " + noteDb.getStatus() + ", cannot be modified.");
		}
		
		// 获取会议记录信息
		ConferenceReserve reserve = conferenceReserveService.findById(conferenceId);
		if(reserve == null){
			throw new NotExistInDatabaseException("ConferenceReserve record of " + conferenceId + " is not exist in database.");
		}
		
		// 更新会议纪要指定读者信息
		Map<String, String> readers = this.handleReader(conferenceNote.getReaderIds(), conferenceNote.getReaderNames(), reserve);
		authorityInfoService.update(noteId, AuthorityInfo.AUTHTYPE_CONFERENCE_NOTE, 
				readers.get("readerIds"), readers.get("readerNames"), AuthorityInfo.OWNERTYPE_USER);
		
		// 更新实际与会人员
		conferenceReserveParticipantService.update(conferenceNote.getConferenceId(), 
				ConferenceReserveParticipant.TYPE_ATTENDED, conferenceNote.getParticipantIds(), conferenceNote.getParticipantNames());
		
		// 如果会议纪要的状态为驳回，需要添加待审批的审批记录
		if(noteDb.getStatus() == StatusConstant.STATUS_REJECT){
			conferenceAuditService.save(noteId, ConferenceAudit.TARGET_TYPE_CONFERENCE_NOTE, 
					reserve.getOrganizer(), StatusConstant.STATUS_PENDING, (byte)0, null);
		}
		
		// 更新会议记录的会议纪要状态为待审批
		conferenceReserveService.updateNoteStatus(conferenceId, StatusConstant.STATUS_PENDING);
		
		// 保存会议纪要
		conferenceNote.setUpdateBy(ContextSupportUtil.getCurrentUserId());
		conferenceNote.setUpdateByName(ContextSupportUtil.getCurrentUsername());
		conferenceNote.setUpdateTime(new Date());
		conferenceNote.setStatus(StatusConstant.STATUS_PENDING);
		this.update(conferenceNote);
	}
	
	/** 
	 * @Title: updateStatus 
	 * @Description: 更新会议纪要状态
	 * @param noteId
	 * @param status 
	 * @see com.suneee.eas.oa.service.conference.ConferenceNoteService#updateStatus(java.lang.Long, java.lang.Byte)
	 */
	public void updateStatus(Long noteId, Byte status){
		conferenceNoteDao.updateStatus(noteId, status);
	}
	
	/** 
	 * @Title: audit 
	 * @Description: 会议纪要审批
	 * @param conferenceAudit
	 * @param status
	 * @throws IllegalTargetTypeException
	 * @throws IllegalStatusException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceNoteService#audit(com.suneee.eas.oa.model.conference.ConferenceAudit, java.lang.Byte)
	 */
	public void audit(ConferenceAudit conferenceAudit, Byte status) 
			throws IllegalTargetTypeException, IllegalStatusException{
		if(conferenceAudit == null || status == null){
			throw new IllegalArgumentException("conferenceAudit and status must not be null.");
		}
		
		// 判断审批类型
		if(conferenceAudit.getTargetType() == ConferenceAudit.TARGET_TYPE_CONFERENCE_NOTE){
			throw new IllegalTargetTypeException("ConferenceAudit record's targetType must be conference-note");
		}
		
		// 判断审批状态是否合法
		if(status != StatusConstant.STATUS_PASS && status != StatusConstant.STATUS_REJECT){
			throw new IllegalStatusException("audit status " + status + " is not supported.");
		}
		
		// 会议纪要ID
		Long noteId = conferenceAudit.getTargetId();
		// 查询会议纪要信息
		ConferenceNote conferenceNote = this.findById(noteId);
		if(conferenceNote == null){
			throw new NotExistInDatabaseException("ConferenceNote record of " + noteId + " is not exist in database.");
		}
		
		// 更新会议信息的会议纪要状态
		conferenceReserveService.updateNoteStatus(conferenceNote.getConferenceId(), status);
		
		// 更新会议纪要的状态
		this.updateStatus(noteId, status);
	}
	
	/** 
	 * @Title: findById 
	 * @Description: 根据会议纪要ID获取会议纪要详情
	 * @param noteId
	 * @return 
	 * @see com.suneee.eas.common.service.impl.BaseServiceImpl#findById(java.lang.Long)
	 */
	public ConferenceNote findById(Long noteId){
		ConferenceNote note = this.conferenceNoteDao.findById(noteId);
		this.getNoteOtherinfo(note);
		return note;
	}
	
	/** 
	 * @Title: getByConferenceId 
	 * @Description: 获取指定会议的会议纪要详情
	 * @param conferenceId
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceNoteService#getByConferenceId(java.lang.Long)
	 */
	public ConferenceNote getByConferenceId(Long conferenceId){
		ConferenceNote note = this.conferenceNoteDao.getByConferenceId(conferenceId);
		this.getNoteOtherinfo(note);
		return note;
	}
	
	/** 
	 * @Title: getNoteDetails 
	 * @Description: 根据会议ID获取会议纪要详情
	 * @param conferenceId
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceNoteService#getNoteDetails(java.lang.Long)
	 */
	public ConferenceReserve getNoteDetails(Long conferenceId){
		// 获取会议详情
		ConferenceReserve reserve = conferenceReserveService.findById(conferenceId);
		if(reserve == null){
			throw new NotExistInDatabaseException("ConferenceReserve record of " + conferenceId + " is not exist in database.");
		}
		// 获取会议纪要详情
		ConferenceNote note = this.getByConferenceId(conferenceId);
		if(note != null){
			// 未设置指定读者或者当前用户在指定读者内才能查看会议纪要信息
			if(StringUtils.isBlank(note.getReaderIds()) 
					|| note.getReaderIds().contains(ContextSupportUtil.getCurrentUserId().toString())){
				reserve.setConferenceNote(note);
			}
			// 获取会议纪要审批记录
			List<ConferenceAudit> noteAudits = this.conferenceAuditService.getByTargetIdAndType(note.getNoteId(), 
					ConferenceAudit.TARGET_TYPE_CONFERENCE_NOTE);
			note.setNoteAuditList(noteAudits);
		}
		return reserve;
	}
	
	/** 
	 * @Title: getAll 
	 * @Description: 获取会议纪要分页列表 
	 * @param filter
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceNoteService#getAll(com.suneee.eas.common.component.QueryFilter)
	 */
	public Pager<ConferenceReserve> getAll(QueryFilter filter){
		Integer pageNum= 1;
		Integer pageSize= 20;
		if(filter != null){
			 if (filter.getFilters().get("pageNum") != null){
	            pageNum = Integer.parseInt((String)filter.getFilters().get("pageNum"));
	        }
	        if ((String) filter.getFilters().get("pageSize") != null){
	            pageSize=Integer.parseInt((String) filter.getFilters().get("pageSize"));
	        }
		}
        Page<ConferenceReserve> pageObj= PageHelper.startPage(pageNum,pageSize);
        conferenceNoteDao.getAll(filter);
        return this.toPager(pageObj);
	}
	
	/** 处理指定读者数据
	 * @param readerIds
	 * @param readerNames
	 * @return
	 */
	private Map<String, String> handleReader(String readerIds, String readerNames, ConferenceReserve conferenceReserve){
		Map<String, String> readers = new HashMap<>();
		if(StringUtils.isNotBlank(readerIds) && StringUtils.isNotBlank(readerNames)){
			List<String> readerIdList = Arrays.asList(readerIds.split(","));
			// 纪要人员（当前用户）不在制定读者中时，将纪要人员加入指定读者中
			if(!readerIdList.contains(ContextSupportUtil.getCurrentUserId().toString())){
				readerIds = readerIds + "," + ContextSupportUtil.getCurrentUserId();
				readerNames = readerNames + "," + ContextSupportUtil.getCurrentUsername();
			}
			
			// 纪要审批人不在指定读者中时，将纪要审批人加入指定读者中
			if(!readerIdList.contains(conferenceReserve.getOrganizer())){
				readerIds = readerIds + "," + conferenceReserve.getOrganizer();
				readerNames = readerNames + "," + conferenceReserve.getOrganizerName();
			}
			readers.put("readerIds", readerIds);
			readers.put("readerNames", readerNames);
		}
		return readers;
	}
	
	/** 查询会议纪要存储在其他表的信息
	 * @param note
	 */
	private void getNoteOtherinfo(ConferenceNote note){
		if(note == null){
			return;
		}
		// 查询指定读者
		List<AuthorityInfo> authInfoList = authorityInfoService.getBy(note.getNoteId(), 
				AuthorityInfo.AUTHTYPE_CONFERENCE_NOTE, AuthorityInfo.OWNERTYPE_USER);
		List<Long> readerIds = new ArrayList<>();
		List<String> readerNames = new ArrayList<>();
		int authSize = authInfoList.size();
		for(int i = 0; i < authSize; i++){
			readerIds.add(authInfoList.get(i).getOwnerId());
			readerNames.add(authInfoList.get(i).getOwnerName());
		}
		note.setReaderIds(StringUtils.join(readerIds, ","));
		note.setReaderNames(StringUtils.join(readerNames, ","));
		
		// 查询实际参会人员
		List<ConferenceReserveParticipant> participantList = 
				conferenceReserveParticipantService.getByConferenceIdAndType(note.getConferenceId(), ConferenceReserveParticipant.TYPE_ATTENDED);
		List<Long> participantIds = new ArrayList<>();
		List<String> participantNames = new ArrayList<>();
		int participantSize = participantList.size();
		for(int i = 0; i < participantSize; i++){
			participantIds.add(participantList.get(i).getUserId());
			participantNames.add(participantList.get(i).getUserName());
		}
		note.setParticipantIds(StringUtils.join(participantIds, ","));
		note.setParticipantNames(StringUtils.join(participantNames, ","));
		
		// 查询附件下载地址
		String attachmentIds = note.getAttachmentIds();
		if(StringUtils.isNotBlank(attachmentIds)){
			// 获取附件下载地址
			String[] attachmentIdArr = attachmentIds.split(",");
			List<String> downloadUrls = new ArrayList<>();
			for(String attachmentId : attachmentIdArr){
				SysFile sysFile = sysFileService.findById(Long.parseLong(attachmentId));
				downloadUrls.add(FileUtil.getDownloadUrl(sysFile.getPath()));
			}
			
			note.setAttachmentUrl(downloadUrls);
		}
	}
	
	/** 转换分页信息
	 * @param pageObj
	 * @return
	 */
	private Pager<ConferenceReserve> toPager(Page<ConferenceReserve> pageObj){
		  Pager<ConferenceReserve> pager=new Pager<ConferenceReserve>();
	        pager.setTotal(pageObj.getTotal());
	        pager.setPages(pageObj.getPages());
	        pager.setPageNum(pageObj.getPageNum());
	        pager.setPageSize(pageObj.getPageSize());
	        pager.setStartRow(pageObj.getStartRow());
	        pager.setEndRow(pageObj.getEndRow());
	        pager.setList(pageObj.getResult());
	        return pager;
	}
}

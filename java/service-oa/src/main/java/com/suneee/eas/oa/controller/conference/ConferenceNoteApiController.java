/**
 * @Title: ConferenceNoteApiController.java 
 * @Package com.suneee.eas.oa.controller.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.controller.conference;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.exception.conference.AlreadyExistException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.AccessRecord;
import com.suneee.eas.oa.model.conference.ConferenceNote;
import com.suneee.eas.oa.model.conference.ConferenceReserve;
import com.suneee.eas.oa.service.conference.AccessRecordService;
import com.suneee.eas.oa.service.conference.ConferenceNoteService;
import com.suneee.platform.model.system.SysUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @ClassName: ConferenceNoteApiController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-03 10:38:02 
 *
 */
@RestController
@RequestMapping(ModuleConstant.CONFERENCE_MODULE + FunctionConstant.CONFERENCE_NOTE)
public class ConferenceNoteApiController {
	private static final Logger LOGGER = LogManager.getLogger(ConferenceNoteApiController.class);
	@Autowired
	private ConferenceNoteService conferenceNoteService;
	@Autowired
	private AccessRecordService accessRecordService;
	/** 
	 * 保存会议纪要
	 * @param request
	 * @param response
	 * @param conferenceNote
	 * @return
	 */
	@RequestMapping("save")
	public ResponseMessage save(HttpServletRequest request, HttpServletResponse response, @RequestBody ConferenceNote conferenceNote){
		String message = null;
		try {
			if(conferenceNote.getNoteId() == null){
				conferenceNoteService.saveNote(conferenceNote);
			} else {
				conferenceNoteService.updateNote(conferenceNote);
			}
			
			return ResponseMessage.success("保存会议纪要成功");
		} catch (UserPermissionException e) {
			LOGGER.error("保存会议纪要失败：" + e.getMessage(), e);
			message = "您不是指定的会议纪要员！";
		} catch (AlreadyExistException e) {
			LOGGER.error("保存会议纪要失败：" + e.getMessage(), e);
			message = "会议纪要已经存在，不可重复添加！";
		} catch (IllegalStatusException e) {
			LOGGER.error("保存会议纪要失败：" + e.getMessage(), e);
			message = "仅待审批、已驳回状态的会议纪要可修改！";
		} catch(IllegalArgumentException e){
			LOGGER.error("保存会议纪要失败：" + e.getMessage(), e);
			message = "接口请求参数错误！";
		} catch (Exception e) {
			LOGGER.error("保存会议纪要失败：" + e.getMessage(), e);
			message = "系统内部错误";
		}
		return ResponseMessage.fail("保存会议纪要失败：" + message);
	}
	
	/** 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("details")
	public ResponseMessage details(HttpServletRequest request, HttpServletResponse response){
		Long conferenceId = RequestUtil.getLong(request, "conferenceId", null);
		if(conferenceId == null){
			LOGGER.error("获取会议纪要详情失败：会议ID为空！");
			return ResponseMessage.fail("获取会议纪要详情失败：请求参数错误！");
		}
		try {
			ConferenceReserve conferenceReserve = conferenceNoteService.getNoteDetails(conferenceId);
			// 当会议纪要不为空时，记录会议纪要的访问记录
			if(conferenceReserve.getConferenceNote() != null){
				SysUser user = (SysUser) ContextSupportUtil.getCurrentUser();
				accessRecordService.save(user.getUserId(), ContextSupportUtil.getCurrentUsername(),
						conferenceReserve.getConferenceNote().getNoteId(), AccessRecord.TARGET_TYPE_CONFERENCE_NOTE);
			}
			return ResponseMessage.success("获取会议纪要详情成功！", conferenceReserve);
		} catch (Exception e) {
			LOGGER.error("获取会议纪要详情失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取会议纪要详情失败：系统内部错误！");
		}
	}
	
	/** 获取我的纪要分页列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("recorderPageList")
	public ResponseMessage recorderPageList(HttpServletRequest request, HttpServletResponse response){
		try {
			QueryFilter filter = new QueryFilter("getAllRecorder", request);
			filter.addFilter("recorder", ContextSupportUtil.getCurrentUserId());
			filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			filter.addFilter("nowDate", new Date());
			Pager<ConferenceReserve> list = conferenceNoteService.getAll(filter);
			return ResponseMessage.success("获取我的纪要列表成功！", list);
		} catch (Exception e) {
			LOGGER.error("获取我的纪要列表失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取我的纪要列表失败：系统内部错误！");
		}	
	}
	
	/** 获取纪要审批分页列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("auditPageList")
	public ResponseMessage auditPageList(HttpServletRequest request, HttpServletResponse response){
		try {
			QueryFilter filter = new QueryFilter("getAllAudit", request);
			filter.addFilter("organizer", ContextSupportUtil.getCurrentUserId());
			filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			Pager<ConferenceReserve> list = conferenceNoteService.getAll(filter);
			return ResponseMessage.success("获取纪要审批列表成功！", list);
		} catch (Exception e) {
			LOGGER.error("获取纪要审批列表失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取纪要审批失败：系统内部错误！");
		}	
	}
}

/**
 * @Title: ConferenceAuditApiController.java 
 * @Package com.suneee.eas.oa.controller.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.controller.conference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.exception.conference.AlreadyHandledException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.service.conference.ConferenceAuditService;

/**
 * @ClassName: ConferenceAuditApiController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-06 15:50:14 
 *
 */
@RestController
@RequestMapping(ModuleConstant.CONFERENCE_MODULE + FunctionConstant.CONFERENCE_AUDIT)
public class ConferenceAuditApiController {
	private static final Logger LOGGER = LogManager.getLogger(ConferenceAuditApiController.class);
	@Autowired
	private ConferenceAuditService conferenceAuditService;
	/** 会议审批
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("audit")
	public ResponseMessage audit(HttpServletRequest request, HttpServletResponse response){
		String message = null;
		try {
			Long auditId = RequestUtil.getLong(request, "auditId", null);
			Byte status = RequestUtil.getByte(request, "status", null);
			String opinion = RequestUtil.getString(request, "opinion", null);
			conferenceAuditService.audit(auditId, status, opinion);
			return ResponseMessage.success("审批成功！");
		} catch (UserPermissionException e) {
			LOGGER.error("审批失败：" + e, e);
			message = "您不是当前任务的审批人！";
		} catch(IllegalStatusException e){
			LOGGER.error("审批失败：" + e, e);
			message = "系统不支持此审批状态！";
		} catch (AlreadyHandledException e) {
			LOGGER.error("审批失败：" + e, e);
			message = "该审批任务已经处理！";
		} catch(IllegalArgumentException e){
			LOGGER.error("审批失败：" + e, e);
			message = "请求参数错误！";
		} catch (Exception e) {
			LOGGER.error("审批失败：" + e, e);
			message = "系统内部错误！";
		}
		return ResponseMessage.fail("审批失败：" + message);
	}
}

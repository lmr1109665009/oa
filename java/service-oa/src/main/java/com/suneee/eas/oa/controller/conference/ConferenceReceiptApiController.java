/**
 * @Title: ConferenceReceiptApiController.java 
 * @Package com.suneee.eas.oa.controller.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.controller.conference;

import java.util.List;

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
import com.suneee.eas.oa.exception.conference.NotParticipantException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.ConferenceReceipt;
import com.suneee.eas.oa.service.conference.ConferenceReceiptService;

/**
 * @ClassName: ConferenceReceiptApiController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-09 13:58:01 
 *
 */
@RestController
@RequestMapping(ModuleConstant.CONFERENCE_MODULE + FunctionConstant.CONFERENCE_RECEIPT)
public class ConferenceReceiptApiController {
	private static final Logger LOGGER = LogManager.getLogger(ConferenceReceiptApiController.class);
	@Autowired
	private ConferenceReceiptService conferenceReceiptService;
	
	/** 发送会议回执
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("sendReceipt")
	public ResponseMessage sendReceipt(HttpServletRequest request, HttpServletResponse response){
		String message = null;
		try {
			Long conferenceId = RequestUtil.getLong(request, "conferenceId", null);
			Byte status = RequestUtil.getByte(request, "status", null);
			String remark = RequestUtil.getString(request, "remark", null);
			conferenceReceiptService.save(conferenceId, status, remark);
			return ResponseMessage.success("发送回执成功！");
		} catch (UserPermissionException e) {
			LOGGER.error("发送回执失败：" + e.getMessage(), e);
			message = "您是会议申请人，无需发送会议回执！";
		} catch (NotParticipantException e) {
			LOGGER.error("发送回执失败：" + e.getMessage(), e);
			message = "您不用参与此次会议，无需发送会议回执！";
		} catch (AlreadyHandledException e) {
			LOGGER.error("发送回执失败：" + e.getMessage(), e);
			message = "您已发送过会议回执，无需再次发送！";
		} catch (IllegalStatusException e) {
			LOGGER.error("发送回执失败：" + e.getMessage(), e);
			message = "系统不支持当前回执状态！";
		} catch (IllegalArgumentException e) {
			LOGGER.error("发送回执失败：" + e.getMessage(), e);
			message = "请求参数错误！";
		} catch (Exception e) {
			LOGGER.error("发送回执失败：" + e.getMessage(), e);
			message = "系统内部错误！";
		}
		return ResponseMessage.fail("发送回执失败：" + message);
	}
	
	/** 获取会议回执信息列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getConferenceReceipts")
	public ResponseMessage getConferenceReceipts(HttpServletRequest request, HttpServletResponse response){
		String message = null;
		try {
			Long conferenceId = RequestUtil.getLong(request, "conferenceId", null);
			List<ConferenceReceipt> receiptList = this.conferenceReceiptService.getByConferenceId(conferenceId);
			return ResponseMessage.success("获取会议回执成功！", receiptList);
		} catch (IllegalArgumentException e) {
			LOGGER.error("获取会议回执失败：" + e.getMessage(), e);
			message = "请求参数错误！";
		} catch (Exception e) {
			LOGGER.error("获取会议回执失败：" + e.getMessage(), e);
			message = "系统内部错误！";
		}
		return ResponseMessage.fail("获取会议回执失败：" + message);
	}
}

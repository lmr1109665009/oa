package com.suneee.platform.controller.worktime;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.worktime.WorkTimeSetting;
import com.suneee.platform.service.worktime.WorkTimeService;
import com.suneee.platform.service.worktime.WorkTimeSettingService;
import com.suneee.core.log.SysAuditThreadLocalHolder;

/**
 * 对象功能:班次设置 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-20 14:57:31
 */
@Controller
@RequestMapping("/platform/worktime/workTimeSetting/")
@Action(ownermodel=SysAuditModelType.WORK_CALENDAR)
public class WorkTimeSettingFormController extends BaseFormController
{
	@Resource
	private WorkTimeSettingService workTimeSettingService;
	
	@Resource
	private WorkTimeService workTimeService;
	
	/**
	 * 添加或更新班次设置。
	 * @param request
	 * @param response
	 * @param workTimeSetting 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(
			description="添加或更新班次设置",
			execOrder=ActionExecOrder.AFTER,
			detail="<#if isAdd>添加<#else>更新</#if>新班次设置：" +
					"【${SysAuditLinkService.getWorkTimeSettingLink(Long.valueOf(id))}】"
	)
	public void save(HttpServletRequest request, HttpServletResponse response, WorkTimeSetting workTimeSetting,BindingResult bindResult) throws Exception
	{
		
		ResultMessage resultMessage=validForm("workTimeSetting", workTimeSetting, bindResult, request);
		//add your custom validation rule here such as below code:
		//bindResult.rejectValue("name","errors.exist.student",new Object[]{"jason"},"重复姓名");
		if(resultMessage.getResult()==ResultMessage.Fail)
		{
			writeResultMessage(response.getWriter(),resultMessage);
			return;
		}
		String resultMsg=null;
		boolean isadd=workTimeSetting.getId()==null;
		Long settingId;
		if(workTimeSetting.getId()==null){
			settingId = UniqueIdUtil.genId();
			workTimeSetting.setId(settingId);
			workTimeSettingService.add(workTimeSetting);
			resultMsg="班次设置添加成功";
		}else{
			settingId = workTimeSetting.getId();
			workTimeSettingService.update(workTimeSetting);
			resultMsg="班次设置更新成功";
		}
		
		String[] startTime = (String[])request.getParameterValues("startTime");
		String[] endTime = (String[])request.getParameterValues("endTime");
		String[] memo = (String[])request.getParameterValues("desc");
		
		// 班次时间添加
		workTimeService.workTimeAdd(settingId, startTime, endTime, memo);
		
		writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
		//添加系统日志信息 -B
		try {
			SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
			SysAuditThreadLocalHolder.putParamerter("id", workTimeSetting.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param id
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected WorkTimeSetting getFormObject(@RequestParam("id") Long id,Model model) throws Exception {
		logger.debug("enter WorkTimeSetting getFormObject here....");
		WorkTimeSetting workTimeSetting=null;
		if(id!=null){
			workTimeSetting=workTimeSettingService.getById(id);
		}else{
			workTimeSetting= new WorkTimeSetting();
		}
		return workTimeSetting;
    }

}

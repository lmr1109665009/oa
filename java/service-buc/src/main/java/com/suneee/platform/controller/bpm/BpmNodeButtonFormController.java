package com.suneee.platform.controller.bpm;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;

import com.suneee.platform.model.bpm.BpmNodeButton;
import com.suneee.platform.service.bpm.BpmNodeButtonService;

/**
 * 对象功能:自定义工具条 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-07-25 18:26:13
 */
@Controller
@RequestMapping("/platform/bpm/bpmNodeButton/")
public class BpmNodeButtonFormController extends BaseFormController
{
	@Resource
	private BpmNodeButtonService bpmNodeButtonService;
	
	/**
	 * 添加或更新自定义工具条。
	 * @param request
	 * @param response
	 * @param bpmNodeButton 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新自定义工具条")
	public void save(HttpServletRequest request, HttpServletResponse response, BpmNodeButton bpmNodeButton,BindingResult bindResult) throws Exception
	{
		
		ResultMessage resultMessage=validForm("bpmNodeButton", bpmNodeButton, bindResult, request);
		//add your custom validation rule here such as below code:
		//bindResult.rejectValue("name","errors.exist.student",new Object[]{"jason"},"重复姓名");
		if(resultMessage.getResult()==ResultMessage.Fail){
			writeResultMessage(response.getWriter(),resultMessage);
			return;
		}
		String resultMsg=null;
		if(bpmNodeButton.getId()==0){
			boolean rtn=bpmNodeButtonService.isOperatorExist(bpmNodeButton);
			if(rtn){
				resultMsg="该操作类型已定义,不能重复添加!";
				writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Fail);
				return;
			}
			bpmNodeButton.setId(UniqueIdUtil.genId());
			bpmNodeButton.setSn(bpmNodeButton.getId());
			bpmNodeButtonService.add(bpmNodeButton);
			resultMsg="添加自定义工具条成功";
		}else{
			boolean rtn=bpmNodeButtonService.isOperatorExistForUpd(bpmNodeButton);
			if(rtn){
				resultMsg="该操作类型已定义,不能重复添加!";
				writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
				return;
			}
			bpmNodeButtonService.update(bpmNodeButton);
			resultMsg="更新自定义工具条成功";
		}
		writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
	}
	
	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param ID
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected BpmNodeButton getFormObject(@RequestParam("id") Long id,Model model) throws Exception {
		logger.debug("enter BpmNodeButton getFormObject here....");
		BpmNodeButton bpmNodeButton=null;
		if(id!=0){
			bpmNodeButton=bpmNodeButtonService.getById(id);
		}else{
			bpmNodeButton= new BpmNodeButton();
		}
		return bpmNodeButton;
    }

}

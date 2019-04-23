package com.suneee.platform.controller.system;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Script;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.ScriptService;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对象功能:脚本管理 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-01-05 12:01:20
 */
@Controller
@RequestMapping("/platform/system/script/")
@Action(ownermodel=SysAuditModelType.PROCESS_AUXILIARY)
public class ScriptFormController extends BaseFormController
{
	@Resource
	private ScriptService scriptService;
	
	/**
	 * 添加或更新脚本管理。
	 * @param request
	 * @param response
	 * @param script 添加或更新的实体
	 * @param bindResult
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新脚本管理",
	detail="<#if isAdd>添加新<#else>更新</#if>脚本：" +
			"【${SysAuditLinkService.getScriptLink(Long.valueOf(id))}】")
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, Script script, BindingResult bindResult) throws Exception
	{
		
		ResultMessage resultMessage=validForm("script", script, bindResult, request);
		//add your custom validation rule here such as below code:
		//bindResult.rejectValue("name","errors.exist.student",new Object[]{"jason"},"重复姓名");
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		if(resultMessage.getResult()==ResultMessage.Fail)
		{
			result.setMessage(resultMessage.getMessage());
			return result;
		}
		String resultMsg=null;
		boolean isadd=true;
		if(script.getId()==null){
			script.setId(UniqueIdUtil.genId());
			scriptService.add(script);
			resultMsg="添加脚本管理成功";
		}else{
			scriptService.update(script);
			resultMsg="更新脚本管理成功";
			isadd=false;
		}
		SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
		SysAuditThreadLocalHolder.putParamerter("id", script.getId().toString());
		result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
		result.setMessage(resultMsg);
		return result;
	}
	
	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param id
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected Script getFormObject(@RequestParam("id") Long id,Model model) throws Exception {
		logger.debug("enter Script getFormObject here....");
		Script script=null;
		if(id!=null){
			script=scriptService.getById(id);
		}else{
			script= new Script();
		}
		return script;
    }

}

/**update 2013-1-1**/
package com.suneee.platform.controller.form;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.eas.common.constant.HttpConstant;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmPrintTemplate;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.form.BpmPrintTemplateService;
import com.suneee.platform.service.form.FormUtil;

/**
 * 对象功能:表单模板 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-01-09 16:35:25
 */
@Controller
@RequestMapping("/platform/form/bpmPrintTemplate/")
public class BpmPrintTemplateFormController extends BaseFormController {
	@Resource
	private BpmPrintTemplateService  bpmPrintTemplateService;
	@Resource
	private BpmFormTableService  bpmFormTableService;
	
	/**
	 * 添加或更新表单模板。
	 * @param request
	 * @param response
	 * @param bpmFormTemplate 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新表单模板")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		response.setContentType(HttpConstant.CONTENT_TYPE_JSON);
		String data = request.getParameter("data");
		BpmPrintTemplate bpmPrintTemplate = (BpmPrintTemplate) JSONObject.toBean(JSONObject.fromObject(data), BpmPrintTemplate.class);
		String resultMsg=null;
		String html=bpmPrintTemplate.getHtml();
		String template = "";
		Long tableId = bpmPrintTemplate.getTableId();
		BpmFormTable bpmFormTable = bpmFormTableService.getById(tableId);
		String tableName = bpmFormTable.getTableName();
		String tableComment = bpmFormTable.getTableDesc();
		template = FormUtil.getPrintFreeMarkerTemplate(html,tableName,tableComment);
		bpmPrintTemplate.setTemplate(template);
		//判断别名是否存在。
		boolean isExist = bpmPrintTemplateService.isExistAlias(bpmPrintTemplate.getAlias(),bpmPrintTemplate.getId());
		if(isExist){
			resultMsg = bpmPrintTemplate.getAlias()+"别名已经存在了" ;
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Fail);
			return ;
		}
		if(bpmPrintTemplate.getId()==null){
			bpmPrintTemplate.setId(UniqueIdUtil.genId());
			int count=bpmPrintTemplateService.getCountByFormKey(bpmPrintTemplate.getFormKey());
			if(count==0){
				bpmPrintTemplate.setIsDefault((short)1);
			}else{
				bpmPrintTemplate.setIsDefault((short)0);
			}
			bpmPrintTemplateService.add(bpmPrintTemplate);
			resultMsg=getText(getText("controller.bpmPrintTemplate"));
		}else{
			bpmPrintTemplateService.update(bpmPrintTemplate);
			resultMsg=getText(getText("controller.bpmPrintTemplate"));
		}
		writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
	}

}

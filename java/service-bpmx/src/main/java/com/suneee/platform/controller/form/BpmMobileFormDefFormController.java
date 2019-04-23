package com.suneee.platform.controller.form;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmMobileFormDef;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.form.BpmMobileFormDefService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
/**
 *<pre>
 * 对象功能:手机表单 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miao
 * 创建时间:2015-10-28 10:52:49
 *</pre>
 */
@Controller
@RequestMapping("/platform/form/bpmMobileFormDef/")
public class BpmMobileFormDefFormController extends BaseController
{
	@Resource
	private BpmMobileFormDefService bpmMobileFormDefService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	
	
	/**
	 * 添加或更新手机表单。
	 * @param request
	 * @param response
	 * @param formDef 添加或更新的实体
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新手机表单")
	@ResponseBody
	public ResponseMessage save(HttpServletRequest request, HttpServletResponse response, BpmMobileFormDef formDef) throws Exception
	{
		String resultMsg=null;
		ResponseMessage responseMessage = new ResponseMessage();
		try{
			if(formDef.getId()==null||formDef.getId()==0){
				Long id = UniqueIdUtil.genId();
				if(StringUtil.isEmpty(formDef.getFormKey())) throw new RuntimeException("没有填写表单KEY");
				BpmFormTable bpmFormTable=bpmFormTableService.getById(formDef.getTableId());
				formDef.setId(id);
				formDef.setIsDefault(1);
				formDef.setVersion(1);
				SysUser user = (SysUser) ContextUtil.getCurrentUser();
				formDef.setCreateBy(user.getUserId());
				formDef.setCreator(user.getFullname());
				formDef.setCreateTime(new Date()); 
				formDef.setTableName(bpmFormTable.getTableName());
				bpmMobileFormDefService.add(formDef);
				resultMsg=id.toString();
				responseMessage.setMessage(getText("添加手机表单成功!","手机表单"));
				responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
			}else{
				String str[] = formDef.getFormHtml().split("components");
				String components = str[1];
				String formTable[] = components.split("\\[");
				if(formTable.length > 2){
					bpmMobileFormDefService.update(formDef);
					responseMessage.setMessage(getText("更新手机表单成功!","手机表单"));
					responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
				}else {
					responseMessage.setMessage(getText("请填写手机表单数据!","手机表单"));
					responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
				}
			}
			return responseMessage;
		}catch(Exception e){
			responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
			responseMessage.setMessage(resultMsg+","+e.getMessage());
			return responseMessage;
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
    protected BpmMobileFormDef getFormObject(@RequestParam("id") Long id,Model model) throws Exception {
		logger.debug("enter BpmFormRule getFormObject here....");
		BpmMobileFormDef bpmMobileForm=null;
		if(id!=null){
			bpmMobileForm=bpmMobileFormDefService.getById(id);
		}else{
			bpmMobileForm= new BpmMobileFormDef();
			
			
		}
		return bpmMobileForm;
    }
}


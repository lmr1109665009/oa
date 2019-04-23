package com.suneee.platform.controller.form;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.encrypt.Base64;
import com.suneee.core.encrypt.Base64;
import com.suneee.core.exception.BusDataException;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import org.apache.commons.lang.CharSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.exception.BusDataException;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.PkValue;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormHandlerService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.form.FormDataUtil;
import com.suneee.platform.service.form.FormUtil;
import com.suneee.platform.service.form.ParseReult;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 对象功能:自定义表单数据处理
 * 开发公司:广州宏天软件有限公司
 * 开发人员:xwy 
 * 创建时间:2011-12-22 11:07:56
 */
@Controller
@RequestMapping("/platform/form/bpmFormHandler/")
public class BpmFormHandlerController extends BaseController {
	@Resource
	private BpmFormHandlerService service;

	@Resource
	private BpmFormDefService bpmFormDefService;
	
	@Resource
	private BpmFormTableService bpmFormTableService;
	

	/**
	 * 表单预览
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "表单预览")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long formDefId = RequestUtil.getLong(request, "formDefId");
		String formKey= RequestUtil.getString(request, "formKey");
		String pkValue = request.getParameter("pkValue");
		String returnUrl = RequestUtil.getPrePage(request);
		String ctxPath=request.getContextPath();
		BpmFormDef bpmFormDef = null;

		if (formDefId != 0 || StringUtil.isNotEmpty(formKey)) {
			if(formDefId>0){
				bpmFormDef = bpmFormDefService.getById(formDefId);	
			}
			else{
				bpmFormDef = bpmFormDefService.getDefaultPublishedByFormKey(formKey);	
			}
			//表单预览不需要控件解析，表单终极预览才需要控件解析，下面注释同理
//			String html = service.obtainHtml(bpmFormDef,  pkValue, "", "", "",ctxPath, "",false,true,false,(short)0);
//			bpmFormDef.setHtml(html);
		} else {
			String html = request.getParameter("html");
			if (StringUtil.isNotEmpty(html)){
				html= Base64.getFromBase64(html);
			}
			Long tableId = RequestUtil.getLong(request, "tableId");
			String title = RequestUtil.getString(request, "title");
			bpmFormDef = new BpmFormDef();
			bpmFormDef.setSubject(RequestUtil.getString(request, "name"));
			bpmFormDef.setTabTitle(title);
			bpmFormDef.setFormDesc(RequestUtil.getString(request, "comment"));
			if(tableId>0){				
				// 读取表。
				BpmFormTable bpmFormTable = bpmFormTableService.getTableById(tableId);
				ParseReult result = new ParseReult();
				result.setBpmFormTable(bpmFormTable);
				String template = FormUtil.getFreeMarkerTemplate(html, tableId);
				result.setTemplate(template);
//				html = service.obtainHtml(title, result, null,true);
				bpmFormDef.setHtml(html);
			}
			
		}		
		return getAutoView().addObject("bpmFormDef", bpmFormDef).addObject("returnUrl", returnUrl);
	}
	
	
	
	/**
	 * 删除数据
	 * <pre>
	 * 1.输入表单key。
	 * 2.输入主键。
	 * </pre>
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除数据。" )
	public void del(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String returnUrl=RequestUtil.getPrePage(request);
		String formKey = RequestUtil.getString(request, "formKey");
		String id = request.getParameter("id");
		BpmFormDef bpmFormDef = null;
		try{
			if (StringUtil.isNotEmpty(formKey) ) {
				bpmFormDef = bpmFormDefService.getDefaultVersionByFormKey(formKey);
				BpmFormTable bpmFormTable= bpmFormTableService.getById(bpmFormDef.getTableId());			
				service.delById(id,bpmFormTable);
				addMessage(new ResultMessage(ResultMessage.Success, "删除业务数据成功"), request);
			}else{
				addMessage(new ResultMessage(ResultMessage.Fail, "删除业务数据失败,没有取得表名"), request);
			}			
		}
		catch(Exception ex){
			ex.printStackTrace();
			addMessage(new ResultMessage(ResultMessage.Fail, "删除业务数据失败"), request);
		}
		response.sendRedirect(returnUrl);
	}
	

	/**
	 * 保存业务数据。
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新" )
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String data = request.getParameter("formData");
		Long tableId= RequestUtil.getLong(request, "tableId");
		String alias= RequestUtil.getString(request, "alias");
		BpmFormTable bpmFormTable=bpmFormTableService.getByTableId(tableId, 1);
		String id = request.getParameter("pkField");
		try{
			if(StringUtil.isEmpty(id)){
				BpmFormData bpmFormData=FormDataUtil.parseJson(data,bpmFormTable);
				service.handFormData(bpmFormData,alias,data);
			}
			else{
				PkValue pkValue=new PkValue(bpmFormTable.getPkField(), id);
				pkValue.setIsAdd(false);
				System.out.println("");
				BpmFormData bpmFormData=FormDataUtil.parseJson(data,pkValue,bpmFormTable);
				service.handFormData(bpmFormData,alias,data);
			}
			writeResultMessage(response.getWriter(), "保存表单数据成功!", ResultMessage.Success);
		}
		catch(BusDataException ex){
			ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail,"保存表单数据失败:" + ex.getMessage());
			response.getWriter().print(resultMessage);
		}
		catch(Exception ex){
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail,"保存表单数据失败:" + str);
				response.getWriter().print(resultMessage);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
				response.getWriter().print(resultMessage);
			}
		}
	}
	
	

}

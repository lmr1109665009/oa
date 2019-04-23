package com.suneee.platform.controller.form;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmFormTemplate;
import com.suneee.platform.model.form.BpmMobileFormDef;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmFormFieldService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.form.BpmFormTemplateService;
import com.suneee.platform.service.form.BpmMobileFormDefService;
import com.suneee.platform.service.system.GlobalTypeService;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
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
public class BpmMobileFormDefController extends BaseController
{
	@Resource
	private BpmMobileFormDefService bpmMobileFormDefService;
	
	@Resource
	private BpmFormTemplateService bpmFormTemplateService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	
	@Resource
	private BpmFormFieldService bpmFormFieldService;
	@Resource
	private FreemarkEngine freemarkEngine;

	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	@Resource
	private GlobalTypeService globalTypeService;
	
	/**
	 * 取得手机表单分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看手机表单分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Long categoryId  = RequestUtil.getLong(request, "categoryId");
		QueryFilter filter = new QueryFilter(request,"bpmMobileFormDefItem");
		filter.addFilter("categoryid", categoryId);
		if(categoryId == 0){
			List<Long> typeIdList=globalTypeService.getTypeIdsByEcode(ContextSupportUtil.getCurrentEnterpriseCode());
			BpmUtil.typeIdFilter(typeIdList);
			filter.addFilter("typeIdList", typeIdList);
		}
		List<BpmMobileFormDef> list=bpmMobileFormDefService.getAll(filter);
		
		return this.getAutoView()
				.addObject("bpmMobileFormDefList",list)
				.addObject("categoryId", categoryId);
	}
	/**
	 * 
	 */
	@RequestMapping("versions")
	@Action(description="查看手机表单分页列表")
	public ModelAndView versions(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		String formKey = RequestUtil.getString(request, "formKey");
		
		List<BpmMobileFormDef> list=bpmMobileFormDefService.getByFormKey(formKey);
		
		return this.getAutoView().addObject("bpmMobileFormDefList",list);
	}
	
	/**
	 * 删除手机表单
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除手机表单")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			bpmMobileFormDefService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除手机表单成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑手机表单
	 * @param request
	 * @param formDef
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑手机表单")
	public ModelAndView edit(HttpServletRequest request,BpmMobileFormDef formDef) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		if(id.equals(0L)){
			String[] templates=request.getParameterValues("template");
			String mobileHtml = bpmMobileFormDefService.getDefaultMobileHtml(formDef,templates);
			formDef.setFormHtml(mobileHtml);
		}
		else{
			formDef = bpmMobileFormDefService.getById(id);
		}
		return getAutoView().addObject("bpmMobileFormDef",formDef);
	}
	
	
	@ResponseBody
	@RequestMapping("getControls")
	@Action(description = "获取表单控件")
	public Map<String, String> getControls(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "templateAlias") String templateAlias,
			@RequestParam(value = "tableId") Long tableId)
			throws TemplateException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		BpmFormTemplate template = bpmFormTemplateService
				.getByTemplateAlias(templateAlias);
		if (template != null) {
			template = bpmFormTemplateService.getByTemplateAlias(template
					.getMacroTemplateAlias());
			String macro = template.getHtml();
			BpmFormTable table = bpmFormTableService.getById(tableId);
			List<BpmFormField> fields = bpmFormFieldService
					.getByTableId(tableId);
			for (BpmFormField field : fields) {
				String fieldname = field.getFieldName();
				// 字段命名规则
				// 表类型(m:主表，s:子表) +":" + 表名 +“：” + 字段名称
//				field.setFieldName((table.getIsMain() == 1 ? "m:" : "s:")
//						+ table.getTableName() + ":" + field.getFieldName());
				String tableName=table.getTableName();
				int type=(table.getIsMain() == 1)? 1 :2;
				String input="<@input field=field type="+type+" tableName=\""+tableName+"\"/>";
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("field", field);
				map.put(fieldname, freemarkEngine.parseByStringTemplate(data, macro+ input));
			}
		}
		return map;
	}
	
	
	@RequestMapping("selectTemplate")
	@Action(description = "选择模板")
	public ModelAndView selectTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		Long tableId = RequestUtil.getLong(request, "tableId");
		
		ModelAndView mv = this.getAutoView();
		BpmFormTable table = bpmFormTableService.getById(tableId);

		if (table.getIsMain() == 1) {
			List<BpmFormTable> subTables = bpmFormTableService.getSubTableByMainTableId(tableId);
			List<BpmFormTemplate> mainTableTemplates = bpmFormTemplateService.getTemplateType(BpmFormTemplate.MAIN_MOBILE_TABLE);
			List<BpmFormTemplate> subTableTemplates = bpmFormTemplateService.getTemplateType(BpmFormTemplate.SUB_MOBILE_TABLE);
			mv.addObject("mainTable", table)
					.addObject("subTables", subTables)
					.addObject("mainTableTemplates", mainTableTemplates)
					.addObject("subTableTemplates", subTableTemplates);

		} else {
			List<BpmFormTable> subTables = new ArrayList<BpmFormTable>();
			subTables.add(table);
			List<BpmFormTemplate> subTableTemplates = bpmFormTemplateService.getTemplateType(BpmFormTemplate.SUB_MOBILE_TABLE);
			mv.addObject("subTables", subTables)
					.addObject("subTableTemplates",subTableTemplates);
		}
	

		return mv;
	}
	
	
	/**
	 * 根据模板产生html。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("genByTemplate")
	public void genByTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long[] templateTableId = RequestUtil.getLongAryByStr(request,
				"templateTableId");
		String[] templateAlias = RequestUtil.getStringAryByStr(request, "templateAlias");
		PrintWriter out = response.getWriter();
		String html = bpmMobileFormDefService.genHtml(templateTableId, templateAlias) ;
		out.println(html);
	}
	
	
	/**
	 * 	编辑手机表单
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("new")
	@Action(description="新建手机表单")
	public ModelAndView newForm(HttpServletRequest request) throws Exception
	{	
		Long categoryId  = RequestUtil.getLong(request, "categoryId", 0L);
		List<BpmFormTemplate> subTableTemplateList = bpmFormTemplateService.getTemplateType("mobileSub");
		List<BpmFormTemplate> mainTableTemplateList = bpmFormTemplateService.getTemplateType("mobileMain");
		
		return getAutoView().addObject("categoryId",categoryId)
				.addObject("subTableTemplateList",subTableTemplateList)
				.addObject("mainTableTemplateList",mainTableTemplateList);
	}

	/**
	 * 取得手机表单明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看手机表单明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		BpmMobileFormDef bpmMobileFormDef = bpmMobileFormDefService.getById(id);	
		return getAutoView().addObject("bpmMobileFormDef", bpmMobileFormDef);
	}
	
	
	/**
	 * 根据formKey查询是否存在别名。
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getCountByFormKey")
	@ResponseBody
	public Integer getCountByFormKey(HttpServletRequest request, HttpServletResponse response){
		String formKey=RequestUtil.getString(request, "formKey");
		return bpmMobileFormDefService.getCountByFormKey(formKey);
	}
	
	@RequestMapping("getMainSubTables")
	@ResponseBody
	public BpmFormTable getMainSubTables(HttpServletRequest request, HttpServletResponse response){
		Long tableId=RequestUtil.getLong(request, "tableId");
		BpmFormTable mainTable = bpmFormTableService.getTableById(tableId,0);
		return mainTable;
	}
	
	
	/**
	 * 发布手机表单
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("publish")
	@Action(description="发布手机表单")
	public void publish(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long id =RequestUtil.getLong(request, "id");
			BpmMobileFormDef def = bpmMobileFormDefService.getById(id);
			def.setIsPublished(1);
			SysUser user =(SysUser) ContextUtil.getCurrentUser();
			def.setPublishBy(user.getUserId());
//			def.setPublisher(user.getFullname());
			def.setPublisher(ContextSupportUtil.getUsername(user));
			def.setPublishTime(new Date());
			bpmMobileFormDefService.update(def); 
			message=new ResultMessage(ResultMessage.Success, "发布手机表单成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "发布手机表单失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 新建版本
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("newVersion")
	@Action(description="新建版本")
	public void newVersion(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long id =RequestUtil.getLong(request, "id");
			BpmMobileFormDef def = bpmMobileFormDefService.getById(id);
			int maxVersion =  0;
			List<BpmMobileFormDef> formdefList =bpmMobileFormDefService.getByFormKey(def.getFormKey());
			for(BpmMobileFormDef formDef : formdefList) {
				if(formDef.getVersion()>maxVersion)maxVersion =formDef.getVersion();
			}
			def.setVersion(maxVersion+1);
			def.setIsDefault(0);
			def.setIsPublished(0);
			SysUser user =(SysUser) ContextUtil.getCurrentUser();
			def.setCreateBy(user.getUserId());
			def.setCreateTime(new Date());
//			def.setCreator(user.getFullname());
			def.setCreator(ContextSupportUtil.getUsername(user));
			def.setPublishBy(null);
			def.setPublisher(null); 
			def.setId(UniqueIdUtil.genId());
			bpmMobileFormDefService.add(def);
			message=new ResultMessage(ResultMessage.Success, "新建版本成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "新建版本失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 新建版本
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("setDefaultVersion")
	@Action(description="设置默认版本")
	public void setDefaultVersion(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long id =RequestUtil.getLong(request, "id");
			bpmMobileFormDefService.setDefaultVersion(id);
			message=new ResultMessage(ResultMessage.Success, "设置默认成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "设置默认失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	

	@RequestMapping("preview")
	@Action(description="添加或更新手机表单")
	public ModelAndView preview(HttpServletRequest request, HttpServletResponse response,BpmMobileFormDef formDef) throws Exception
	{
		
		Long mobileFormId = RequestUtil.getLong(request, "mobileFormId",0L);
		String formKey=RequestUtil.getString(request, "formKey","");
		if(mobileFormId!=0) {
			formDef = bpmMobileFormDefService.getById(mobileFormId);
		}
		if(StringUtil.isNotEmpty(formKey)){
			formDef = bpmMobileFormDefService.getDefaultByFormKey(formKey);
		}
		
		BpmFormTable bpmFormTable=bpmFormTableService.getById(formDef.getTableId());
		formDef.setTableName(bpmFormTable.getTableName());
		
		formDef.setFormHtml(formDef.getFormHtml().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
		
		return new ModelAndView("/platform/form/bpmMobileformPreview").addObject("formDef", formDef);
	}
	
}


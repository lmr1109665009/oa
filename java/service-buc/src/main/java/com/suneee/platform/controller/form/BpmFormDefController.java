package com.suneee.platform.controller.form;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.encrypt.Base64;
import com.suneee.core.util.*;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.encrypt.Base64;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.*;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.oa.component.BpmImportHolder;
import com.suneee.oa.component.GlobalTypeHolder;
import com.suneee.oa.service.user.SysOrgExtendService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmFormTemplate;
import com.suneee.platform.model.form.TeamModel;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.BpmDataTemplateService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormFieldService;
import com.suneee.platform.service.form.BpmFormHandlerService;
import com.suneee.platform.service.form.BpmFormRightsService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.form.BpmFormTemplateService;
import com.suneee.platform.service.form.FormUtil;
import com.suneee.platform.service.form.ParseReult;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.ParserParam;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.xml.constant.XmlConstant;
import com.suneee.platform.xml.util.MsgUtil;

import freemarker.template.TemplateException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 对象功能:制作表单 控制器类 开发公司:广州宏天软件有限公司 开发人员:xwy 创建时间:2011-12-22 11:07:56
 */
@Controller
@RequestMapping("/platform/form/bpmFormDef/")
@Action(ownermodel = SysAuditModelType.FORM_MANAGEMENT)
public class BpmFormDefController extends BaseController {

	@Resource
	private BpmFormDefService service;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmFormFieldService bpmFormFieldService;
	@Resource
	private BpmFormTemplateService bpmFormTemplateService;

	@Resource
	private BpmFormRightsService bpmFormRightsService;
	@Resource
	private FreemarkEngine freemarkEngine;
	@Resource
	private BpmService bpmService;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	@Resource
	private GlobalTypeService globalTypeService;

	@Resource
	private BpmDataTemplateService bpmDataTemplateService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmFormDefService bpmFormDefService;

	@RequestMapping("manage")
	@Action(description = "制作表单管理页面")
	public ModelAndView manage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();

		return mv;
	}

	/**
	 * 取得制作表单分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 *            请求页数
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看制作表单分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "page", defaultValue = "1") int page) throws Exception {
		Long categoryId = RequestUtil.getLong(request, "categoryId");
		QueryFilter filter = new QueryFilter(request, "bpmFormDefItem");
		List<BpmFormDef> list = service.getAll(filter);
		Map<Long, Integer> publishedCounts = new HashMap<Long, Integer>();
		Map<Long, Integer> dataTemplateCounts = new HashMap<Long, Integer>();

		for (int i = 0; i < list.size(); i++) {
			BpmFormDef formDef = list.get(i);
			String formKey = formDef.getFormKey();
			Long formId = formDef.getFormDefId();
			Integer publishedCount = service.getCountByFormKey(formKey);
			publishedCounts.put(formId, publishedCount);

			Integer dataTemplateCount = bpmDataTemplateService.getCountByFormKey(formKey);
			dataTemplateCounts.put(formId, dataTemplateCount);
		}

		ModelAndView mv = this.getAutoView().addObject("bpmFormDefList", list).addObject("publishedCounts", publishedCounts).addObject("dataTemplateCounts", dataTemplateCounts).addObject("categoryId", categoryId);
		return mv;
	}

	@RequestMapping("newVersion")
	@Action(description = "新建表单版本", detail = "新建表单版本【${SysAuditLinkService.getBpmFormDefLink(Long.valueof(formDefId))}】")
	public void newVersion(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String preUrl = RequestUtil.getPrePage(request);
		Long formDefId = RequestUtil.getLong(request, "formDefId");
		ResultMessage msg;
		try {
			service.newVersion(formDefId);
			msg = new ResultMessage(ResultMessage.Success, "新建表单版本成功!");
		} catch (Exception ex) {
			msg = new ResultMessage(ResultMessage.Fail, "新建表单版本失败!");
		}
		addMessage(msg, request);
		response.sendRedirect(preUrl);
	}

	@ResponseBody
	@RequestMapping("getTableInfo")
	@Action(description = "获取对应自定义表信息")
	public List<BpmFormTable> getTableInfo(HttpServletRequest request, HttpServletResponse response) {
		Long formDefId = RequestUtil.getLong(request, "formDefId", 0L);
		if (formDefId == 0L)
			return null;
		List<BpmFormTable> bpmFormTableList = new ArrayList<BpmFormTable>();
		try {
			BpmFormDef bpmFormDef = service.getById(formDefId);
			Long tableId = bpmFormDef.getTableId();
			BpmFormTable bpmFormTable = bpmFormTableService.getById(tableId);
			bpmFormTableList.add(bpmFormTable);
			List<BpmFormTable> subTableList = bpmFormTableService.getSubTableByMainTableId(tableId);
			if (BeanUtils.isNotEmpty(subTableList)) {
				bpmFormTableList.addAll(subTableList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bpmFormTableList;

	}

	/**
	 * 收集信息，为添加表单做准备
	 * 
	 * @param request
	 * @param response
	 * @param page
	 *            请求页数
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("gatherInfo")
	@Action(description = "收集信息")
	public ModelAndView gatherInfo(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "page", defaultValue = "1") int page) throws Exception {
		long categoryId = RequestUtil.getLong(request, "categoryId");
		String subject = RequestUtil.getString(request, "subject");
		String formDesc = RequestUtil.getString(request, "formDesc");
		int designType = RequestUtil.getInt(request, "designType", 0);

		return this.getAutoView().addObject("categoryId", categoryId).addObject("subject", subject).addObject("formDesc", formDesc).addObject("designType", designType);
	}

	/**
	 * 选择模板
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selectTemplate")
	@Action(description = "选择模板")
	public ModelAndView selectTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String subject = RequestUtil.getString(request, "subject");
		Long categoryId = RequestUtil.getLong(request, "categoryId");
		String formDesc = RequestUtil.getString(request, "formDesc");
		Long tableId = RequestUtil.getLong(request, "tableId");
		int isSimple = RequestUtil.getInt(request, "isSimple", 0);
		String templatesId = RequestUtil.getString(request, "templatesId");
		String formKey = RequestUtil.getString(request, "formKey");

		ModelAndView mv = this.getAutoView();
		BpmFormTable table = bpmFormTableService.getById(tableId);

		if (table.getIsMain() == 1) {
			List<BpmFormTable> subTables = bpmFormTableService.getSubTableByMainTableId(tableId);
			List<BpmFormTemplate> mainTableTemplates = bpmFormTemplateService.getAllMainTableTemplate();
			List<BpmFormTemplate> subTableTemplates = bpmFormTemplateService.getAllSubTableTemplate();
			mv.addObject("mainTable", table).addObject("subTables", subTables).addObject("mainTableTemplates", mainTableTemplates).addObject("subTableTemplates", subTableTemplates);

		} else {
			List<BpmFormTable> subTables = new ArrayList<BpmFormTable>();
			subTables.add(table);
			List<BpmFormTemplate> subTableTemplates = bpmFormTemplateService.getAllSubTableTemplate();
			mv.addObject("subTables", subTables).addObject("subTableTemplates", subTableTemplates);
		}
		mv.addObject("subject", subject).addObject("categoryId", categoryId).addObject("tableId", tableId).addObject("formDesc", formDesc).addObject("isSimple", isSimple).addObject("formKey", formKey).addObject("templatesId", templatesId);

		return mv;
	}

	/**
	 * 加载编辑器设计模式的模板列表
	 */
	@RequestMapping("chooseDesignTemplate")
	@Action(description = "选择编辑器设计模板")
	public ModelAndView chooseDesignTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		String subject = RequestUtil.getString(request, "subject");
		String formKey = RequestUtil.getString(request, "formKey");
		Long categoryId = RequestUtil.getLong(request, "categoryId");
		String formDesc = RequestUtil.getString(request, "formDesc");
		int isSimple = RequestUtil.getInt(request, "isSimple", 0);

		String templatePath = FormUtil.getDesignTemplatePath();
		String xml = FileUtil.readFile(templatePath + "designtemps.xml");
		Document document = Dom4jUtil.loadXml(xml);
		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> list = root.elements();
		String reStr = "[";
		for (Element element : list) {
			String alias = element.attributeValue("alias");
			String name = element.attributeValue("name");
			String templateDesc = element.attributeValue("templateDesc");
			if (!reStr.equals("["))
				reStr += ",";
			reStr += "{name:'" + name + "',alias:'" + alias + "',templateDesc:'" + templateDesc + "'}";
		}
		reStr += "]";
		mv.addObject("subject", subject).addObject("categoryId", categoryId).addObject("formDesc", formDesc).addObject("temps", reStr).addObject("isSimple", isSimple).addObject("formKey", formKey);
		return mv;
	}

	/**
	 * 编辑器设计表单
	 * 
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("designEdit")
	@Action(description = "编辑器设计表单")
	public ModelAndView designEdit(HttpServletRequest request) throws Exception {
		Long formDefId = RequestUtil.getLong(request, "formDefId", 0);
		int isBack = RequestUtil.getInt(request, "isBack", 0);

		boolean isPublish = false;

		ModelAndView mv = getAutoView();
		BpmFormDef bpmFormDef = null;
		if (formDefId == 0) {
			bpmFormDef = new BpmFormDef();
			bpmFormDef.setCategoryId(RequestUtil.getLong(request, "categoryId"));
			bpmFormDef.setFormDesc(RequestUtil.getString(request, "formDesc"));
			bpmFormDef.setSubject(RequestUtil.getString(request, "subject"));
			bpmFormDef.setFormKey(RequestUtil.getString(request, "formKey"));
			bpmFormDef.setDesignType(BpmFormDef.DesignType_CustomDesign);
			String tempalias = RequestUtil.getString(request, "tempalias");
			String templatePath = FormUtil.getDesignTemplatePath();
			String reult = FileUtil.readFile(templatePath + tempalias + ".html");
			bpmFormDef.setHtml(reult);

			mv.addObject("canEditColumnNameAndType", true);
		} else {
			boolean canEditColumnNameAndType = true;
			bpmFormDef = service.getById(formDefId);
			Long tableId = bpmFormDef.getTableId();

			if (tableId > 0) {
				canEditColumnNameAndType = bpmFormTableService.getCanEditColumnNameTypeByTableId(bpmFormDef.getTableId());
				BpmFormTable bpmFormTable = bpmFormTableService.getById(tableId);
				mv.addObject("bpmFormTable", bpmFormTable);
				isPublish = true;
			}

			mv.addObject("canEditColumnNameAndType", canEditColumnNameAndType);
		}
		// 回退到编辑页面。
		if (isBack > 0) {
			Long categoryId = RequestUtil.getLong(request, "categoryId");
			String subject = RequestUtil.getString(request, "subject");
			String formDesc = RequestUtil.getString(request, "formDesc");
			String formKey = RequestUtil.getString(request, "formKey");
			String title = RequestUtil.getString(request, "tabTitle");
			String html = request.getParameter("html");

			bpmFormDef.setCategoryId(categoryId);
			bpmFormDef.setFormDesc(formDesc);
			bpmFormDef.setSubject(subject);
			bpmFormDef.setFormKey(formKey);
			bpmFormDef.setDesignType(BpmFormDef.DesignType_CustomDesign);
			bpmFormDef.setHtml(html);
			bpmFormDef.setTabTitle(title);
		}
		mv.addObject("bpmFormDef", bpmFormDef);
		mv.addObject("isPublish", isPublish);

		return mv;
	}

	/**
	 * 删除制作表单。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("delByFormKey")
	public void delByFormKey(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] aryFormKey = RequestUtil.getStringAryByStr(request, "formKey");
		ResultMessage msg = null;
		JSONArray delArray = new JSONArray();
		JSONArray notDelArray = new JSONArray();
		for (String formKey : aryFormKey) {
			List<BpmDefinition> defList = bpmDefinitionService.getByFormKey(formKey);
			BpmFormDef bpmFormDef = service.getDefaultVersionByFormKey(formKey);
			if (bpmFormDef==null){
				continue;
			}
			JSONObject jsonObj = new JSONObject();
			jsonObj.accumulate("formName", bpmFormDef.getSubject());
			if (defList.size() > 0) {
				String subject = "";
				/*for (BpmDefinition def : defList) {
					subject += "<li>" + def.getSubject() + "</li>";
				}*/
				int len=0;
				for (BpmDefinition def : defList) {
					if (len==defList.size()-1){
						subject += def.getSubject();
					}else {
						subject += def.getSubject() + "、";
					}
					len++;
				}
				jsonObj.accumulate("defName", subject);
				notDelArray.add(jsonObj);
			} else {
				service.delByFormKey(formKey);
				delArray.add(jsonObj);
			}
		}
		String str = "";
		/*if (delArray.size() > 0) {
			str += "成功删除:";
			for (Object obj : delArray) {
				JSONObject jsonObj = (JSONObject) obj;
				str += jsonObj.getString("formName") + "<br/>";
			}
		}
		if (notDelArray.size() > 0) {
			str += "以下表单已和流程进行关联,不能删除:<br>";
			for (Object obj : notDelArray) {
				JSONObject jsonObj = (JSONObject) obj;
				str += "表单名称:" + jsonObj.getString("formName") + "<br>关联流程：<ul>" + jsonObj.getString("defName") + "</ul>";
			}
		}*/

		int status=ResultMessage.Success;
		if (aryFormKey.length==1){
			if (delArray.size()>0){
				str+="删除成功！";
			}else {
				str+="该表单已关联流程，请先取消对应关联！";
				status=ResultMessage.Fail;
			}
		}else {
			if (notDelArray.size() > 0) {
				if (delArray.size()==0){
					str += "所选表单已关联流程，请先取消对应关联！";
					status=ResultMessage.Fail;
				}else {
					int counter=0;
					for (Object obj : notDelArray) {
						JSONObject jsonObj = (JSONObject) obj;
						if (counter==notDelArray.size()-1){
							str+=jsonObj.getString("formName");
						}else {
							str+=jsonObj.getString("formName")+"、";
						}
						counter++;
					}
					str+="表单已关联流程，请先取消对应关联！";
					status=ResultMessage.Success;
				}
			}
		}

		if (notDelArray.size() == 0) {
			msg = new ResultMessage(ResultMessage.Success, "删除成功!");
		} else {
			msg = new ResultMessage(status, str);
		}
		writeResultMessage(response.getWriter(), msg);
	}

	/**
	 * 编辑制作表单。
	 * 
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑制作表单")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		ModelAndView mv = getAutoView();

		Long formDefId = RequestUtil.getLong(request, "formDefId");
		String returnUrl = RequestUtil.getPrePage(request);
		BpmFormDef bpmFormDef = null;
		if (formDefId != 0) {
			bpmFormDef = service.getById(formDefId);
		} else {
			bpmFormDef = new BpmFormDef();
			bpmFormDef.setTableId(RequestUtil.getLong(request, "tableId"));
			bpmFormDef.setCategoryId(RequestUtil.getLong(request, "categoryId"));
			bpmFormDef.setFormDesc(RequestUtil.getString(request, "formDesc"));
			bpmFormDef.setSubject(RequestUtil.getString(request, "subject"));
			bpmFormDef.setFormKey(RequestUtil.getString(request, "formKey"));

			Long[] templateTableId = RequestUtil.getLongAryByStr(request, "templateTableId");

			String[] templateAlias = RequestUtil.getStringAryByStr(request, "templateAlias");
			String templatesId = getTemplateId(templateTableId, templateAlias);
			String reult = this.genTemplate(templateTableId, templateAlias);
			bpmFormDef.setHtml(reult);
			bpmFormDef.setTemplatesId(templatesId);
		}

		mv.addObject("bpmFormDef", bpmFormDef).addObject("returnUrl", returnUrl);

		return mv;
	}

	/**
	 * 取得制作表单明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看制作表单明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long id = RequestUtil.getLong(request, "formDefId");
		long canReturn = RequestUtil.getLong(request, "canReturn", 0);
		String preUrl = RequestUtil.getPrePage(request);
		BpmFormDef bpmFormDef = service.getById(id);
		return getAutoView().addObject("bpmFormDef", bpmFormDef).addObject("returnUrl", preUrl).addObject("canReturn", canReturn);
	}

	/**
	 * 取得表fields, 如果是主表，同时取所有子表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getAllFieldsByTableId")
	public String getAllFieldsByTableId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long tableId = RequestUtil.getLong(request, "tableId", 0);
		Long formDefId = RequestUtil.getLong(request, "formDefId", 0);
		if (tableId.longValue() == 0) {
			if (formDefId.longValue() > 0) {
				BpmFormDef bpmFormDef = service.getById(formDefId);
				tableId = bpmFormDef.getTableId();
			}
		}
		if (tableId.longValue() == 0) {
			return "";
		}
		return com.alibaba.fastjson.JSONObject.toJSONString(service.getAllFieldsByTableId(tableId));
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
	public void genByTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long[] templateTableId = RequestUtil.getLongAryByStr(request, "templateTableId");
		String[] templateAlias = RequestUtil.getStringAryByStr(request, "templateAlias");
		PrintWriter out = response.getWriter();
		String html = genTemplate(templateTableId, templateAlias);
		out.println(html);
	}

	/**
	 * 根据模板别名返回 编辑器设计模板
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getHtmlByAlias")
	public void getHtmlByAlias(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String tempalias = RequestUtil.getString(request, "tempalias");
		PrintWriter out = response.getWriter();
		String templatePath = FormUtil.getDesignTemplatePath();
		String reult = FileUtil.readFile(templatePath + tempalias + ".html");
		out.println(reult);
	}

	/**
	 * 发布表单
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("publish")
	@Action(description = "发布表单", detail = "发布表单【${SysAuditLinkService.getBpmFormDefLink(Long.valueOf(formDefId))}】")
	public void publish(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long formDefId = RequestUtil.getLong(request, "formDefId");
		String prevPage = RequestUtil.getPrePage(request);
		ResultMessage resultObj = null;
		try {
			service.publish(formDefId, ContextUtil.getCurrentUser().getFullname());
			resultObj = new ResultMessage(ResultMessage.Success, "发布版本成功!");
		} catch (Exception e) {
			e.printStackTrace();
			resultObj = new ResultMessage(ResultMessage.Fail, e.getCause().toString());
		}
		addMessage(resultObj, request);
		response.sendRedirect(prevPage);
	}

	/**
	 * 查看版本
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("versions")
	@Action(description = "查看版本")
	public ModelAndView versions(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView result = getAutoView();

		String formKey = request.getParameter("formKey");

		// 版本信息
		List<BpmFormDef> versions = service.getByFormKey(formKey);
		result.addObject("versions", versions).addObject("formName", versions.get(0).getSubject());
		return result;
	}

	/**
	 * 设置默认版本
	 * 
	 * @param request
	 * @param response
	 * @param formDefId
	 * @param formDefId
	 * @throws Exception
	 */
	@RequestMapping("setDefaultVersion")
	@Action(description = "设置默认版本", detail = "设置表单【${SysAuditLinkService.getBpmFormDefLink(formDefId)}： ${formDefId}】为默认版本")
	public void setDefaultVersion(HttpServletRequest request, HttpServletResponse response, @RequestParam("formDefId") Long formDefId, @RequestParam("formKey") String formKey) throws Exception {
		ResultMessage resultObj = new ResultMessage(ResultMessage.Success, "设置默认版本成功!");
		String preUrl = RequestUtil.getPrePage(request);
		service.setDefaultVersion(formDefId, formKey);
		addMessage(resultObj, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 选择器
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@Action(description = "选择器")
	public ModelAndView selector(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter queryFilter = new QueryFilter(request, "bpmFormDefItem");
		boolean dataTemplate = RequestUtil.getBoolean(request, "dataTemplate");
		boolean isMutl = RequestUtil.getBoolean(request, "isMutl");
		List<BpmFormDef> list = service.getPublished(queryFilter);

		ModelAndView mv = this.getAutoView().addObject("bpmFormDefList", list).addObject("isMutl", isMutl);
		return mv;
	}

	/**
	 * 根据表单定义id获取是否可以删除。
	 * 
	 * @param formDefId
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getFlowUsed")
	public void getFlowUsed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String formKey = RequestUtil.getString(request, "formKey");
		int rtn = service.getFlowUsed(formKey);
		out.println(rtn);
	}

	/**
	 * 设计表。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "designTable", method = RequestMethod.POST)
	public ModelAndView designTable(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String formKey = request.getParameter("formKey");
		String content = request.getParameter("content");
		Long formDefId = RequestUtil.getLong(request, "formDefId");
		Long categoryId = RequestUtil.getLong(request, "categoryId");

		String subject = RequestUtil.getString(request, "subject");
		String formDesc = RequestUtil.getString(request, "formDesc");
		String tabTitle = RequestUtil.getString(request, "tabTitle");

		ParseReult result = FormUtil.parseHtmlNoTable(content, "", "");

		String tableName = RequestUtil.getString(request, "tableName");
		String tableComment = RequestUtil.getString(request, "tableComment");

		boolean canEditTableName = true;

		if (formDefId > 0) {
			BpmFormDef bpmFormDef = service.getById(formDefId);
			Long tableId = bpmFormDef.getTableId();
			if (tableId > 0) {
				canEditTableName = bpmFormTableService.getCanEditColumnNameTypeByTableId(tableId);
			}
		}

		ModelAndView mv = this.getAutoView();
		mv.addObject("result", result).addObject("content", content).addObject("formDefId", formDefId).addObject("subject", subject).addObject("formKey", formKey).addObject("categoryId", categoryId).addObject("formDesc", formDesc).addObject("tabTitle", tabTitle).addObject("tableName", tableName).addObject("tableComment", tableComment).addObject("canEditTableName", canEditTableName);
		return mv;
	}

	/**
	 * 对表单的html进行验证，验证html是否合法。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "validDesign", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> validDesign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		Long formDefId = RequestUtil.getLong(request, "formDefId");
		String html = request.getParameter("html");
		//html=html.replace("&amp;#39;", "'");
		String tableName = RequestUtil.getString(request, "tableName");
		String tableComment = RequestUtil.getString(request, "tableComment");
		Boolean incHide = RequestUtil.getBoolean(request, "incHide", false);
		// boolean isTableExist=false;
		Long tableId = 0L;
		if (formDefId > 0) {
			BpmFormDef bpmFormDef = service.getById(formDefId);
			tableId = bpmFormDef.getTableId();
		}
		// 输入了主表名。
		// 验证主表名称。
		if (StringUtil.isNotEmpty(tableName)) {
			if (tableId > 0) {
				BpmFormTable bpmFormTable = bpmFormTableService.getById(tableId);
				// 输入的表名和原来的表名不一致的情况。
				if (!tableName.equalsIgnoreCase(bpmFormTable.getTableName())) {
					boolean isTableExist = bpmFormTableService.isTableNameExisted(tableName);
					if (isTableExist) {
						map.put("valid", false);
						map.put("errorMsg", "表:" + tableName + ",在系统中已经存在!");
						return map;
					}
				}
			} else {
				boolean isTableExist = bpmFormTableService.isTableNameExisted(tableName);
				if (isTableExist) {
					map.put("valid", false);
					map.put("errorMsg", "表:" + tableName + ",在系统中已经存在!");
					return map;
				}
			}
		}

		ParseReult result = FormUtil.parseHtmlNoTable(html, tableName, tableComment);
		BpmFormTable bpmFormTable = result.getBpmFormTable();

		// 表单生成表情况。没有表名。
		if (StringUtil.isEmpty(tableName) && tableId > 0) {
			BpmFormTable formTable = bpmFormTableService.getById(tableId);
			result.getBpmFormTable().setTableName(formTable.getTableName());
		}

		// 验证子表。
		String strValid = validSubTable(bpmFormTable, tableId);

		if (StringUtil.isNotEmpty(strValid)) {
			map.put("valid", false);
			map.put("errorMsg", strValid);
			return map;
		}
		// 验证表单是否有错。
		boolean rtn = result.hasErrors();
		if (rtn) {
			map.put("valid", false);
			map.put("errorMsg", result.getError());
		} else {
			map.put("valid", true);
			map.put("table", result.getBpmFormTable());
			if (incHide) {//需要添加隐藏的控件ID
				addHiddenFiled(bpmFormTable);
				List<BpmFormTable> subList = bpmFormTable.getSubTableList();
				for (BpmFormTable sub : subList) {
					addHiddenFiled(sub);
				}
				map.put("table", bpmFormTable);
			}
		}
		return map;

	}

	/**
	 * 根据传入的bpmFormTable，复制并添加控件对应的隐藏ID字段信息到传入的bpmFormTable中
	 * 
	 * @param bpmFormTable
	 */
	private void addHiddenFiled(BpmFormTable bpmFormTable) {
		List<BpmFormField> list = bpmFormTable.getFieldList();
		BpmFormField hiddenField = null;
		int listSize = list.size();
		for (int i = 0; i < listSize; i++) {
			BpmFormField field = list.get(i);
			if (BpmFormTableService.isExecutorSelector(field.getControlType())) {
				hiddenField = (BpmFormField) field.clone();
				hiddenField.setIsHidden(BpmFormField.HIDDEN);
				hiddenField.setFieldName(field.getFieldName() + BpmFormField.FIELD_HIDDEN);
				hiddenField.setFieldDesc(field.getFieldDesc() + BpmFormField.FIELD_HIDDEN);
				bpmFormTable.addField(hiddenField);
			}
		}
	}

	/**
	 * 验证子表系统中是否存在。
	 * 
	 * <pre>
	 * 	对表单的子表循环。
	 * 		1.表单还没有生成子表的情。
	 * 			验证子表表名系统中是否存在。
	 * 		2.已经生成子表。
	 * 			判断当前子表是否在原子表的列表中，如果不存在，则表示该子表为新添加的表，需要验证子表系统中是否已经存在。
	 * 
	 * </pre>
	 * 
	 * @param bpmFormTable
	 * @param tableId
	 * @return
	 */
	private String validSubTable(BpmFormTable bpmFormTable, Long tableId) {

		List<BpmFormTable> subTableList = bpmFormTable.getSubTableList();
		if (BeanUtils.isEmpty(subTableList))
			return "";
		String str = "";
		for (BpmFormTable subTable : subTableList) {
			String tableName = subTable.getTableName().toLowerCase();
			// 还没有生成表的情况。
			if (tableId == 0) {
				boolean isTableExist = bpmFormTableService.isTableNameExisted(tableName);
				if (isTableExist) {
					str += "子表:【" + tableName + "】系统中已经存在!\r\n";
				}
			}
			// 表已经生成的情况。
			else {
				BpmFormTable orginTable = bpmFormTableService.getTableById(tableId);
				List<BpmFormTable> orginSubTableList = orginTable.getSubTableList();
				Map<String, BpmFormTable> mapSubTable = new HashMap<String, BpmFormTable>();
				for (BpmFormTable table : orginSubTableList) {
					mapSubTable.put(table.getTableName().toLowerCase(), table);
				}
				// 原子表中不存在该表，表示该表为新添加的表。
				if (!mapSubTable.containsKey(tableName)) {
					boolean isTableExist = bpmFormTableService.isTableNameExisted(tableName);
					if (isTableExist) {
						str += "子表:【" + tableName + "】系统中已经存在!\r\n";
					}
				}
			}
		}
		return str;
	}

	/**
	 * 设计时对表单进行预览。
	 * 
	 * <pre>
	 * 	步骤：
	 * 	1.获取设计的html。
	 *  2.对设计的html解析。
	 *  3.生成相应的freemaker模版。
	 *  4.解析html模版输入实际的html。
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("preview")
	@Action(description = "编辑器设计表单预览")
	public ModelAndView preview(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long formDefId = RequestUtil.getLong(request, "formDefId", 0);
		String html = "";
		String name = "";
		String comment = "";
		String title = "";
		// 在列表页面点击预览
		Map<String, Object> permission = null;
		if (formDefId > 0) {
			BpmFormDef bpmFormDef = service.getById(formDefId);
			BpmFormTable bpmFormTable = bpmFormDef.getBpmFormTable();
			html = bpmFormDef.getHtml();
			name = bpmFormTable.getTableName();
			comment = bpmFormDef.getFormDesc();
			title = bpmFormDef.getTabTitle();
			permission = bpmFormRightsService.getByFormKeyAndUserId(bpmFormDef, "", "", "", false);
		}
		// 在编辑页面预览
		else {
			html = request.getParameter("html");
			name = RequestUtil.getString(request, "name");
			title = RequestUtil.getString(request, "title");
			comment = RequestUtil.getString(request, "comment");
		}
		ParseReult result = FormUtil.parseHtmlNoTable(html, name, comment);

		//--->测试新解释器
		//result.setTemplate(StringUtil.unescapeHtml(html));
		//<---测试新解释器

		String outHtml = bpmFormHandlerService.obtainHtml(title, result, permission, true);
		ModelAndView mv = this.getAutoView();
		mv.addObject("html", outHtml);
		return mv;
	}

	@RequestMapping("parse")
	@Action(description = "表单预览")
	public ModelAndView parse(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String outHtml = "";

		Long formDefId = RequestUtil.getLong(request, "formDefId");
		String formKey = RequestUtil.getString(request, "formKey");

		BpmFormDef bpmFormDef = new BpmFormDef();
		BpmFormData data=null;
		if (formDefId != 0 || StringUtil.isNotEmpty(formKey)) {
			if (formDefId > 0) {
				bpmFormDef = bpmFormDefService.getById(formDefId);
			} else {
				bpmFormDef = bpmFormDefService.getDefaultPublishedByFormKey(formKey);
			}
			if(bpmFormDef.getDesignType()==BpmFormDef.DesignType_CustomDesign){
				bpmFormDef.setHtml(StringUtil.unescapeHtml(bpmFormDef.getHtml()));
			}
			data = bpmFormHandlerService.getByFormTable(bpmFormDef.getBpmFormTable(), true);
			
		} else {
			String html=RequestUtil.getString(request, "html");
			if (StringUtil.isNotEmpty(html)){
				html= Base64.getFromBase64(html);
			}
			String name=RequestUtil.getString(request, "name");
			String comment=RequestUtil.getString(request, "comment");
			Long tableId = RequestUtil.getLong(request, "tableId");

			bpmFormDef.setSubject(name);
			bpmFormDef.setTabTitle(RequestUtil.getString(request, "title"));
			bpmFormDef.setFormDesc(comment);
			bpmFormDef.setHtml(html);
			
			BpmFormTable bpmFormTable = bpmFormTableService.getTableById(tableId);
			if(bpmFormTable==null){//找不到表就说明是编辑器那种
				ParseReult result = FormUtil.parseHtmlNoTable(html, name, comment);
				bpmFormTable=result.getBpmFormTable();
				bpmFormDef.setHtml(StringUtil.unescapeHtml(html));
			}
			
			data = bpmFormHandlerService.getFormData(bpmFormTable);
			bpmFormDef.setBpmFormTable(bpmFormTable);
		}
		Map<String, Object> permission = bpmFormRightsService.getByFormKeyAndUserId(bpmFormDef, "", "", "", false);
		// 处理分页情况
		outHtml = bpmFormHandlerService.getTabHtml(bpmFormDef.getTabTitle(), bpmFormDef.getHtml(),permission);
		ParserParam param= new ParserParam(data, permission,null,null);
		//先处理分组显示情况
		outHtml = BpmFormDefHtmlParser.parseTeam(outHtml, param);
		//再处理字段权限
		outHtml = BpmFormDefHtmlParser.parse(outHtml, param);

		ModelAndView mv = this.getAutoView();
		mv.addObject("html", outHtml);
		return mv;
	}

	/**
	 * 保存表单。
	 * 
	 * <pre>
	 * 	1.新建表单的情况，不发布。
	 * 		1.添加表单定义。
	 * 		2.添加表定义。
	 * 	2.新建表单发，发布。
	 * 		1.添加表单定义。
	 * 		2.添加表定义。
	 *  3.编辑表单，未发布的情况。
	 *  	1.编辑表单定义。
	 *  	2.删除表定义重新添加。
	 *  4.编辑表单，已经发布。
	 *  	1.编辑表单定义
	 *  	2.是否有多个版本，或者已经有数据。
	 *  		1.是只对表进行编辑。
	 *  		2.否则删除表重建。
	 * 
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "saveForm", method = RequestMethod.POST)
	@Action(description = "保存表单", detail = "保存表单【${SysAuditLinkService.getBpmFormDefLink(Long.valueOf(formDefId))}】")
	public void saveForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();

		Long formDefId = RequestUtil.getLong(request, "formDefId");
		Long categoryId = RequestUtil.getLong(request, "categoryId");
		String formKey = RequestUtil.getString(request, "formKey");
		String subject = RequestUtil.getString(request, "subject");
		String formDesc = RequestUtil.getString(request, "formDesc");
		String tableName = RequestUtil.getString(request, "tableName");
		String tableComment = RequestUtil.getString(request, "tableComment");
		String title = RequestUtil.getString(request, "tabTitle");
		String html = request.getParameter("html");
		int publish = RequestUtil.getInt(request, "publish", 0);
		String json = request.getParameter("json");

		html = html.replace("？", "");
		html = html.replace("<p>﻿</p>", "");

		if (StringUtil.isNotEmpty(json)) {
			//根据json更新html
			html = FormUtil.changeHtml(html, json, tableName);
		}
		ParseReult result = FormUtil.parseHtmlNoTable(html, tableName, tableComment);

		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		String userName = sysUser.getFullname();
		BpmFormDef bpmFormDef = null;

		//判断表单别名是否重复。
		ResultMessage resultMessage = isFormkeyExist(formDefId, formKey);
		if (resultMessage.getResult() == ResultMessage.Fail) {
			out.print(resultMessage);
			return;
		}

		if (formDefId == 0) {
			bpmFormDef = new BpmFormDef();
			bpmFormDef.setCategoryId(categoryId);
			bpmFormDef.setFormKey(formKey);
		} else {
			bpmFormDef = service.getById(formDefId);
		}
		Long tableId = bpmFormDef.getTableId();

		String message = null;

		// 输入了主表名。
		// 验证主表名称。
		boolean isTableExist = isTableExists(tableName, tableId);

		if (isTableExist) {
			resultMessage = new ResultMessage(ResultMessage.Fail, "表【" + tableName + "】在数据库中已存在!");
			out.print(resultMessage);
			return;
		}

		bpmFormDef.setSubject(subject);
		bpmFormDef.setFormDesc(formDesc);
		bpmFormDef.setTabTitle(title);
		bpmFormDef.setHtml(html);
		bpmFormDef.setTemplate(result.getTemplate());

		bpmFormDef.setPublishedBy(userName);

		message = (publish == 1) ? "表单发布成功!" : "保存表单成功!";

		resultMessage = new ResultMessage(ResultMessage.Success, message);
		try {
			// 是否发布
			boolean isPublish = (publish == 1);
			// 通过解析后的到的表对象。
			BpmFormTable bpmFormTable = result.getBpmFormTable();
			service.saveForm(bpmFormDef, bpmFormTable, isPublish);
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = new ResultMessage(ResultMessage.Fail, "保存表单失败:" + str);
			} else {
				message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = new ResultMessage(ResultMessage.Fail, message);
			}
		}
		out.print(resultMessage);
	}

	private boolean isTableExists(String tableName, Long tableId) {
		boolean isTableExist = false;
		if (StringUtil.isNotEmpty(tableName)) {
			if (tableId > 0) {
				BpmFormTable bpmFormTable = bpmFormTableService.getById(tableId);
				// 输入的表名和原来的表名不一致的情况。
				if (!tableName.equalsIgnoreCase(bpmFormTable.getTableName())) {
					isTableExist = bpmFormTableService.isTableNameExisted(tableName);
				}
			} else {
				isTableExist = bpmFormTableService.isTableNameExisted(tableName);
			}
		}
		return isTableExist;
	}

	private ResultMessage isFormkeyExist(Long formDefId, String formKey) {
		ResultMessage resultMessage = new ResultMessage(ResultMessage.Success, "");
		if (formDefId == 0) {
			int rtn = service.getCountByFormKey(formKey);
			if (rtn > 0) {
				resultMessage = new ResultMessage(ResultMessage.Fail, "指定别名已经存在请重新表单别名!");
			}
		}
		return resultMessage;
	}

	/**
	 * 复制表单（克隆表单）
	 * 
	 * <pre>
	 * 1、获取要复制的表单对象；
	 * 2、重新生成表单ID（formDefId）、表单key（formKey）；
	 * 3、打开复制表单的设置页面，让用户设置重新设置『表单标题』、『表单分类』、『表单描述』；
	 * 4、保存以后，复制的表单和原表单使用同样的表，表可以添加字段和修改字段（修改的字段不能修改字段名和类型）。
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("copy")
	@Action(description = "复制表单")
	public ModelAndView copy(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long formDefId = RequestUtil.getLong(request, "formDefId", 0);
		BpmFormDef bpmFormDef = null;

		if (formDefId > 0) {
			bpmFormDef = service.getById(formDefId);
		}
		Long cateId = bpmFormDef.getCategoryId();
		if (cateId != null && cateId != 0) {
			GlobalType globalType = globalTypeService.getById(bpmFormDef.getCategoryId());
			bpmFormDef.setCategoryName(globalType.getTypeName());
		}
		ModelAndView mv = this.getAutoView();
		mv.addObject("bpmFormDef", bpmFormDef);
		return mv;
	}

	/**
	 * 保存克隆
	 * 
	 * @param request
	 * @param response
	 * @param po
	 * @throws Exception
	 */
	@RequestMapping("saveCopy")
	@Action(description = "保存克隆", detail = "保存表单【${SysAuditLinkService.getBpmFormDefLink(Long.valueOf(formDefId))}】的克隆")
	public void saveCopy(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		long formDefId = RequestUtil.getLong(request, "formDefId");
		String formName = RequestUtil.getString(request, "formName");
		String formKey = RequestUtil.getString(request, "formKey");
		Long typeId = RequestUtil.getLong(request, "typeId");
		String formDesc = RequestUtil.getString(request, "formDesc");

		BpmFormDef bpmFormDef = service.getById(formDefId);

		int rtn = service.getCountByFormKey(formKey);
		if (rtn > 0) {
			writeResultMessage(writer, "表单别名重复,请重新输入别名!", ResultMessage.Fail);
			return;
		}

		String oldFormKey = bpmFormDef.getFormKey();
		if (bpmFormDef != null) {
			if (!StringUtil.isEmpty(formName)) {
				bpmFormDef.setSubject(formName);
			}
			if (typeId > 0) {
				bpmFormDef.setCategoryId(typeId);
			}
			if (!StringUtil.isEmpty(formDesc)) {
				bpmFormDef.setFormDesc(formDesc);
			}
			long id = UniqueIdUtil.genId();
			bpmFormDef.setFormDefId(id);
			bpmFormDef.setFormKey(formKey);
			bpmFormDef.setIsPublished((short) 0);
			bpmFormDef.setIsDefault((short) 1);
			bpmFormDef.setVersionNo(1);
			try {
				service.copyForm(bpmFormDef, oldFormKey);
				writeResultMessage(writer, "复制表单成功!", ResultMessage.Success);
			} catch (Exception ex) {
				String str = MessageUtil.getMessage();
				if (StringUtil.isNotEmpty(str)) {
					ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "复制表单失败:" + str);
					response.getWriter().print(resultMessage);
				} else {
					String message = ExceptionUtil.getExceptionMessage(ex);
					ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
					response.getWriter().print(resultMessage);
				}
			}
		} else {
			writeResultMessage(writer, "未能获取到要复制的表单", ResultMessage.Fail);
			return;
		}
	}

	/**
	 * 导入表单源文件
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importForm")
	@Action(description = "导入表单源文件")
	public void importForm(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartFile fileLoad = request.getFile("importInput");
		String str = FileUtil.inputStream2String(fileLoad.getInputStream());
		response.getWriter().print(str);
	}

	/**
	 * 导出表单源文件
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportForm")
	@Action(description = "导出表单")
	public void exportForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String title = RequestUtil.getString(request, "title");
		String html = request.getParameter("html");
		if (StringUtil.isNotEmpty(title)) {
			html += "#content-title#" + title;
		}
		String subject = request.getParameter("subject") + ".html";
		response.setContentType("APPLICATION/OCTET-STREAM");
		String filedisplay = StringUtil.encodingString(subject, "GBK", "ISO-8859-1");
		response.addHeader("Content-Disposition", "attachment;filename=" + filedisplay);
		response.getWriter().write(html);
		response.getWriter().flush();
		response.getWriter().close();
	}

	/**
	 * 判断是否有子表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("isSubTable")
	public String isSubTable(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StringBuffer sb = new StringBuffer();
		String formKey = RequestUtil.getString(request, "formKey");
		BpmFormDef bpmFormDef = service.getDefaultPublishedByFormKey(formKey);
		if (BeanUtils.isNotEmpty(bpmFormDef) && BeanUtils.isNotIncZeroEmpty(bpmFormDef.getTableId())) {
			List<BpmFormTable> list = bpmFormTableService.getSubTableByMainTableId(bpmFormDef.getTableId());
			if (BeanUtils.isNotEmpty(list)) {
				sb.append("{success:true,tableId:").append(bpmFormDef.getTableId()).append("}");
			} else {
				sb.append("{success:false,msg:'该表单没有子表,不需要设置子表数据权限！'}");
			}
		} else {
			sb.append("{success:false,msg:'未获得表单信息！'}");
		}

		return sb.toString();
	}

	/**
	 * 流程表单子表授权
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("subRightsDialog")
	@Action(description = "流程表单子表授权")
	public ModelAndView subRightsDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String actDefId = RequestUtil.getString(request, "actDefId");
		String nodeId = RequestUtil.getString(request, "nodeId");
		String formKey = RequestUtil.getString(request, "formKey");

		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");

		BpmFormDef formDef = bpmFormDefService.getDefaultPublishedByFormKey(formKey);

		ModelAndView mv = this.getAutoView();
		mv.addObject("actDefId", actDefId);
		mv.addObject("nodeId", nodeId);
		mv.addObject("formKey", formKey);
		mv.addObject("tableId", formDef.getTableId());
		mv.addObject("parentActDefId", parentActDefId);
		return mv;
	}

	/**
	 * 导出xml窗口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("export")
	@Action(description = " 导出xml窗口")
	public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String formDefIds = RequestUtil.getString(request, "formDefIds");

		ModelAndView mv = this.getAutoView();
		mv.addObject("formDefIds", formDefIds);
		return mv;
	}

	/**
	 * 导出制作表单XML
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportXml")
	@Action(description = "导出制作表单XML", execOrder = ActionExecOrder.AFTER, detail = "导出制作表单:" + "<#list StringUtils.split(formDefIds,\",\") as item>" + "<#assign entity=bpmFormDefService.getById(Long.valueOf(item))/>" + "【${entity.subject}】" + "</#list>")
	public void exportXml(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long[] formDefIds = RequestUtil.getLongAryByStr(request, "formDefIds");

		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put(XmlConstant.BPM_FORM_DEF, true);
		map.put("bpmFormTable", RequestUtil.getBoolean(request, "bpmFormTable"));
		map.put("bpmFormDefOther", RequestUtil.getBoolean(request, "bpmFormDefOther"));
		map.put("bpmFormRights", RequestUtil.getBoolean(request, "bpmFormRights"));
		map.put("bpmTableTemplate", RequestUtil.getBoolean(request, "bpmTableTemplate"));
		map.put("sysBusEvent", RequestUtil.getBoolean(request, "sysBusEvent"));
		map.put("formDefTree", RequestUtil.getBoolean(request, "formDefTree"));

		if (BeanUtils.isEmpty(formDefIds))
			return;
		try {

			String fileName = DateFormatUtil.getNowByString("yyyyMMddHHmmdd") + ".xml";
			if (formDefIds.length == 1) {
				BpmFormDef formDef = service.getById(formDefIds[0]);
				fileName = formDef.getSubject() + "_" + fileName;
			} else
				fileName = "多条表单记录_" + fileName;

			String strXml = service.exportXml(formDefIds, map);

			FileUtil.downLoad(request, response, strXml, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 导入制作表单的XML。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importXml")
	@Action(description = "导入制作表单")
	public void importXml(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartFile fileLoad = request.getFile("xmlFile");
		ResultMessage message = null;
		try {
			initGlobalTypeHolder();
			service.importXml(fileLoad.getInputStream());
			message = new ResultMessage(ResultMessage.Success, MsgUtil.getMessage());
		}catch (RuntimeException e){
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, "导入出错了，请检查导入格式是否正确或者导入的数据是否有问题！");
		}finally {
			releaseGlobalTypeHolder();
		}
		writeResultMessage(response.getWriter(), message);
	}

	/**
	 * 初始化分类Holder
	 * @throws IOException
	 */
	private void initGlobalTypeHolder() throws IOException {
		String enterpriseCode= CookieUitl.getCurrentEnterpriseCode();
		if (StringUtil.isEmpty(enterpriseCode)){
			throw new RuntimeException("当前用户没有挂任何企业，无法进行导入操作！");
		}

		GlobalTypeHolder formHolder=new GlobalTypeHolder(enterpriseCode,GlobalType.CAT_FORM,globalTypeService);
		BpmImportHolder.getFormDefaultTypeHolder().set(formHolder);
		GlobalTypeHolder tableHolder=new GlobalTypeHolder(enterpriseCode,GlobalType.CAT_FORM_TABLE,globalTypeService);
		BpmImportHolder.getTableDefaultTypeHolder().set(tableHolder);
	}

	/**
	 * 释放分类Holder
	 */
	private void releaseGlobalTypeHolder(){
		BpmImportHolder.getFormDefaultTypeHolder().remove();
	}

	@RequestMapping("setCategory")
	@Action(description = "设置分类", detail = "设置表单" + "<#list StringUtils.split(formKeys,\",\") as item>" + " 【${SysAuditLinkService.getBpmFormDefLink(item)}】 " + "</#list>" + "的分类为" + "${SysAuditLinkService.getGlobalTypeLink(Long.valueOf(categoryId))}")
	public void setCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		Long categoryId = RequestUtil.getLong(request, "categoryId", 0);
		String formKeys = RequestUtil.getString(request, "formKeys");
		String[] aryFormKey = formKeys.split(",");

		if (categoryId == 0L) {
			// writer
			writeResultMessage(writer, new ResultMessage(ResultMessage.Fail, getText("controller.bpmFormDef.setCategory.notCategoryId")));
			return;
		}

		if (StringUtil.isEmpty(formKeys)) {
			writeResultMessage(writer, new ResultMessage(ResultMessage.Fail, getText("controller.bpmFormDef.setCategory.notSelectForm")));
			return;
		}

		List<String> list = new ArrayList<String>();

		for (String formKey : aryFormKey) {
			list.add(formKey);
		}
		try {
			service.updCategory(categoryId, list);
			writeResultMessage(writer, new ResultMessage(ResultMessage.Success, getText("controller.bpmFormDef.setCategory.success")));
		} catch (Exception ex) {
			String msg = ExceptionUtil.getExceptionMessage(ex);
			writeResultMessage(writer, new ResultMessage(ResultMessage.Fail, msg));
		}

	}

	/**
	 * 获得模板与表的对应关系
	 * 
	 * @param templateTablesId
	 * @param templateAlias
	 * @return
	 */
	private String getTemplateId(Long[] templateTablesId, String[] templateAlias) {
		StringBuffer sb = new StringBuffer();
		if (BeanUtils.isEmpty(templateTablesId) || BeanUtils.isEmpty(templateAlias))
			return sb.toString();
		for (int i = 0; i < templateTablesId.length; i++) {
			for (int j = 0; j < templateAlias.length; j++) {
				if (i == j) {
					sb.append(templateTablesId[i]).append(",").append(templateAlias[j]);
					break;
				}
			}
			sb.append(";");
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * 根据表和指定的html生成表单。
	 * 
	 * @param tableIds
	 * @param templateAlias
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	private String genTemplate(Long[] tableIds, String[] templateAlias) throws TemplateException, IOException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tableIds.length; i++) {
			// 表
			Map<String, Object> fieldsMap = new HashMap<String, Object>();
			BpmFormTable table = bpmFormTableService.getById(tableIds[i]);
			List<BpmFormField> fields = bpmFormFieldService.getByTableId(tableIds[i]);
			//删除主键外键字段。
			removeField(table, fields);
			fieldsMap.put("table", table);
			fieldsMap.put("fields", fields);
			// 设置主表和子表分组
			this.setTeamFields(fieldsMap, table, fields);

			this.setBpmFormFieldName(table, fields);
			// 模板
			BpmFormTemplate tableTemplate = bpmFormTemplateService.getByTemplateAlias(templateAlias[i]);
			BpmFormTemplate macroTemplate = bpmFormTemplateService.getByTemplateAlias(tableTemplate.getMacroTemplateAlias());
			String macroHtml = "";
			if (macroTemplate != null) {
				macroHtml = macroTemplate.getHtml();
			}
			String result = freemarkEngine.parseByStringTemplate(fieldsMap, macroHtml + tableTemplate.getHtml());
			if (table.getIsMain() == 1) {
				sb.append(result);
			} else {
				sb.append("<div type=\"subtable\" tableName=\"");
				sb.append(table.getTableName()+"\"");
				sb.append(" tabledesc=\""+table.getTableDesc()+"\"");
				if(tableTemplate.getAlias().contains("blockTemplate")){//块模式
					sb.append(" parser=\"piecemodetable\"");
				}
				if(tableTemplate.getAlias().contains("openWindow")){//弹出窗口模式
					sb.append(" parser=\"windowdialogmodetable\"");
				}
				if(tableTemplate.getAlias().contains("inTable")){//表内编辑模式
					sb.append(" parser=\"tableineditmodetable\"");
				}
				
				sb.append(">\n");
				sb.append(result);
				sb.append("</div>\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 删除外部表的主键外键字段。
	 * 
	 * @param table
	 * @param fields
	 */
	private void removeField(BpmFormTable table, List<BpmFormField> fields) {
		if (!table.isExtTable())
			return;
		String pk = table.getPkField();
		String fk = table.getRelation();
		for (Iterator<BpmFormField> it = fields.iterator(); it.hasNext();) {
			BpmFormField field = it.next();
			if (field.getFieldName().equalsIgnoreCase(fk)) {
				it.remove();
				continue;
			}
			if (field.getFieldName().equalsIgnoreCase(pk)) {
				it.remove();
			}
		}
	}

	/**
	 * 设置分组字段
	 * 
	 * @param fieldsMap
	 * @param table
	 * @param fields
	 * @param fieldList
	 * @return
	 */
	private List<TeamModel> setTeamFields(Map<String, Object> fieldsMap, BpmFormTable table, List<BpmFormField> fields) {
		if (StringUtil.isEmpty(table.getTeam()))
			return null;
		List<TeamModel> list = new ArrayList<TeamModel>();
		JSONObject json = JSONObject.fromObject(table.getTeam());
		fieldsMap.put("isShow", json.get("isShow"));
		fieldsMap.put("showPosition", json.get("showPosition"));

		JSONArray teamJson = JSONArray.fromObject(json.get("team"));
		for (Object obj : teamJson) {
			TeamModel teamModel = new TeamModel();
			JSONObject jsonObj = (JSONObject) obj;
			String teamName = (String) jsonObj.get("teamName");
			teamModel.setTeamName(teamName);
			String teamTitle = (String) jsonObj.get("teamTitle");
			teamModel.setTeamTitle(teamTitle);
			// 获取字段
			JSONArray jArray = (JSONArray) jsonObj.get("teamField");
			List<BpmFormField> teamFields = new ArrayList<BpmFormField>();
			for (Object object : jArray) {
				JSONObject fieldObj = (JSONObject) object;
				String fieldName = (String) fieldObj.get("fieldName");
				BpmFormField bpmFormField = this.getTeamField(fields, fieldName);
				if (BeanUtils.isNotEmpty(bpmFormField)) {
					fields.remove(bpmFormField);
					teamFields.add(bpmFormField);
				}
			}
			this.setBpmFormFieldName(table, teamFields);
			teamModel.setTeamFields(teamFields);
			list.add(teamModel);
		}
		fieldsMap.put("teamFields", list);
		return list;
	}

	/**
	 * 设置字段名字
	 * 
	 * @param table
	 * @param fields
	 */
	private void setBpmFormFieldName(BpmFormTable table, List<BpmFormField> fields) {
		for (BpmFormField field : fields) {
			field.setFieldName((table.getIsMain().shortValue() == BpmFormTable.IS_MAIN.shortValue() ? "m:" : "s:") + table.getTableName() + ":" + field.getFieldName());
		}

	}

	/**
	 * 获取分组字段
	 * 
	 * @param fields
	 * @param fieldName
	 * @return
	 */
	private BpmFormField getTeamField(List<BpmFormField> fields, String fieldName) {
		for (BpmFormField bpmFormField : fields) {
			if (bpmFormField.getFieldName().equals(fieldName))
				return bpmFormField;
		}
		return null;
	}

	/**
	 * 根据表Id和模版Id获取所有的控件定义。
	 * 
	 * @param request
	 * @param response
	 * @param templateAlias
	 *            表单模板别名
	 * @param tableId
	 *            自定义表Id
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("getControls")
	@Action(description = "获取表单控件")
	public Map<String, String> getControls(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "templateAlias") String templateAlias, @RequestParam(value = "tableId") Long tableId) throws TemplateException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		BpmFormTemplate template = bpmFormTemplateService.getByTemplateAlias(templateAlias);
		if (template != null) {
			template = bpmFormTemplateService.getByTemplateAlias(template.getMacroTemplateAlias());
			String macro = template.getHtml();
			BpmFormTable table = bpmFormTableService.getById(tableId);
			List<BpmFormField> fields = bpmFormFieldService.getByTableId(tableId);
			for (BpmFormField field : fields) {
				String fieldname = field.getFieldName();
				// 字段命名规则
				// 表类型(m:主表，s:子表) +":" + 表名 +“：” + 字段名称
				field.setFieldName((table.getIsMain() == 1 ? "m:" : "s:") + table.getTableName() + ":" + field.getFieldName());
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("field", field);
				map.put(fieldname, freemarkEngine.parseByStringTemplate(data, macro + "<@input field=field/>"));
			}
		}
		return map;
	}

	/**
	 * OFFICE控件菜单权限设置页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *             ModelAndView
	 * @exception
	 * @since 1.0.0
	 */
	@Action(description = "OFFICE控件菜单权限设置页面")
	public ModelAndView selectOfficeMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		return mv;
	}

	/**
	 * 根据表的Id返回表和字段对象，只获取全局表单和主表的字段
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("fieldDialog")
	public ModelAndView fieldDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String formKey = RequestUtil.getString(request, "formKey");
		BpmFormDef bpmFormDef = service.getDefaultPublishedByFormKey(formKey);
		Long tableId = bpmFormDef.getTableId();
		List<BpmFormField> fieldList = bpmFormFieldService.getByTableIdWithSuggest(tableId);
		return getAutoView().addObject("bpmFormFieldList", fieldList);
	}

	/**
	 * 根据formKey查询是否存在别名。
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getCountByFormKey")
	@ResponseBody
	public Integer getCountByFormKey(HttpServletRequest request, HttpServletResponse response) {
		String formKey = RequestUtil.getString(request, "formKey");
		return service.getCountByFormKey(formKey);
	}
}
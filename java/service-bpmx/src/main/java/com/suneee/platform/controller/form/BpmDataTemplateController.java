package com.suneee.platform.controller.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.excel.util.ExcelUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.HttpUtil;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.excel.util.ExcelUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.form.BpmDataTemplate;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmFormTemplate;
import com.suneee.platform.model.form.BpmPrintTemplate;
import com.suneee.platform.model.form.CommonVar;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysBusEvent;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.form.BpmDataTemplateService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormHandlerService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.form.BpmFormTemplateService;
import com.suneee.platform.service.form.BpmPrintTemplateService;
import com.suneee.platform.service.system.SysBusEventService;

/**
 * <pre>
 * 对象功能:业务数据模板 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2013-09-05 14:14:50
 * </pre>
 */
@Controller
@RequestMapping("/platform/form/bpmDataTemplate/")
@Action(ownermodel = SysAuditModelType.FORM_MANAGEMENT)
public class BpmDataTemplateController extends BaseController {
	@Resource
	private BpmDataTemplateService bpmDataTemplateService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmFormTemplateService bpmFormTemplateService;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private SysBusEventService sysBusEventService;
	@Resource
	private BpmPrintTemplateService bpmPrintTemplateService;

	/**
	 * 添加或更新业务数据模板。
	 * 
	 * @param request
	 * @param response
	 * @param bpmDataTemplate
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新业务数据模板", execOrder = ActionExecOrder.AFTER, detail = "<#if isAdd>添加<#else>更新</#if>业务数据模板:【${SysAuditLinkService.getBpmDataTemplateLink(Long.valueOf(tempId))}】")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resultMsg = null;
		BpmDataTemplate bpmDataTemplate = getFormObject(request);//这个html模板只传了templateAlias，TemplateHtml=null
		Integer rtn = bpmDataTemplateService.getCountByFormKey(bpmDataTemplate);
		
		//是否初始化模板（是在编辑情况下，是否再初始化模板）
		String json = RequestUtil.getString(request, "json", false);
		boolean resetTemp=false;
		if (!StringUtil.isEmpty(json)){
			JSONObject obj = JSONObject.fromObject(json);
			String str = obj.getString("resetTemp");
			if(str.equals("1")){
				resetTemp=true;
			}
			
		}
		
		if (rtn > 0) {
			HttpUtil.writeResponseJson(response, ResponseMessage.fail("别名已经存在!"));
			return;
		}
		try {
			boolean flag = bpmDataTemplate.getId() == null || bpmDataTemplate.getId() == 0;

			bpmDataTemplateService.save(bpmDataTemplate,resetTemp);//在这个过程中进行了第一次模板解释，然后复制到templateHtml上
			resultMsg = flag ? "添加业务数据模板成功" : "更新业务数据模板成功";

			SysAuditThreadLocalHolder.putParamerter("isAdd", flag);
			SysAuditThreadLocalHolder.putParamerter("tempId", bpmDataTemplate.getId().toString());
			response.setCharacterEncoding("UTF-8");
			ResultMessage resultMessage = new ResultMessage(ResultMessage.Success, resultMsg, bpmDataTemplate.getId().toString());
			writeResultMessage(response.getWriter(), resultMessage);
		} catch (Exception e) {
			e.printStackTrace();
			HttpUtil.writeResponseJson(response, ResponseMessage.fail(resultMsg + "," + e.getMessage()));
		}
	}

	/**
	 * 取得 BpmDataTemplate 实体
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected BpmDataTemplate getFormObject(HttpServletRequest request) throws Exception {

		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));

		String json = RequestUtil.getString(request, "json", false);
		if (StringUtil.isEmpty(json))
			return null;
		JSONObject obj = JSONObject.fromObject(json);

		String displayField = obj.getString("displayField");
		String conditionField = obj.getString("conditionField");
		String sortField = obj.getString("sortField");
		String filterField = obj.getString("filterField");
		String manageField = obj.getString("manageField");
		String exportField = obj.getString("exportField");

		obj.remove("displayField");
		obj.remove("conditionField");
		obj.remove("sortField");
		obj.remove("filterField");
		obj.remove("manageField");

		BpmDataTemplate bpmDataTemplate = (BpmDataTemplate) JSONObject.toBean(obj, BpmDataTemplate.class);

		bpmDataTemplate.setDisplayField(displayField);
		bpmDataTemplate.setConditionField(conditionField);
		bpmDataTemplate.setSortField(sortField);
		bpmDataTemplate.setFilterField(filterField);
		bpmDataTemplate.setManageField(manageField);
		bpmDataTemplate.setExportField(exportField);

		return bpmDataTemplate;
	}

	/**
	 * 取得业务数据模板分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看业务数据模板分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<BpmDataTemplate> list = bpmDataTemplateService.getAll(new QueryFilter(request, "bpmDataTemplateItem"));
		ModelAndView mv = this.getAutoView().addObject("bpmDataTemplateList", list);

		return mv;
	}

	/**
	 * 删除业务数据模板
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除业务数据模板", execOrder = ActionExecOrder.BEFORE, detail = "删除业务数据模板：" + "<#list StringUtils.split(id,\",\") as item>" + "<#assign entity=bpmDataTemplateService.getById(Long.valueOf(item))/>" + "【${entity.name}】" + "</#list>")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			bpmDataTemplateService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除业务数据模板成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 编辑业务数据模板
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑业务数据模板")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long tableId = RequestUtil.getLong(request, "tableId");
		String formKey = RequestUtil.getString(request, "formKey");

		BpmFormTable bpmFormTable = bpmFormTableService.getByTableId(tableId, 0);
		List<BpmFormTemplate> templates = bpmFormTemplateService.getDataTemplate();
		BpmDataTemplate bpmDataTemplate = bpmDataTemplateService.getByFormKey(formKey);

		if (BeanUtils.isEmpty(bpmDataTemplate)) {
			bpmDataTemplate = new BpmDataTemplate();
			bpmDataTemplate.setFormKey(formKey);
			bpmDataTemplate.setTableId(tableId);
			bpmDataTemplate.setDisplayField(this.getDisplayField(bpmFormTable, ""));

			bpmDataTemplate.setExportField(this.getExportField(bpmFormTable, ""));
		} else {
			Long defId = bpmDataTemplate.getDefId();
			if (BeanUtils.isNotIncZeroEmpty(defId)) {
				BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);
				if (BeanUtils.isNotEmpty(bpmDefinition))
					bpmDataTemplate.setSubject(bpmDefinition.getSubject());
			}
			bpmDataTemplate.setDisplayField(this.getDisplayField(bpmFormTable, bpmDataTemplate.getDisplayField()));

			bpmDataTemplate.setExportField(this.getExportField(bpmFormTable, bpmDataTemplate.getExportField()));
		}
		bpmDataTemplate.setSource((bpmFormTable.getIsExternal() == BpmFormTable.NOT_EXTERNAL) ? BpmDataTemplate.SOURCE_CUSTOM_TABLE : BpmDataTemplate.SOURCE_OTHER_TABLE);

		JSONObject jsonObject=tidyJson(bpmDataTemplate);
		
		return this.getAutoView().addObject("bpmFormTableJSON", JSONObject.fromObject(bpmFormTable))
				.addObject("DataRightsJson", jsonObject).addObject("bpmDataTemplate", bpmDataTemplate).addObject("templates", templates);
	}
	
	private JSONObject tidyJson(BpmDataTemplate template){
		template.setTemplateHtml("");
		JSONObject jsonObject= JSONObject.fromObject(template);
		jsonObject.remove("pageBean");
		jsonObject.remove("createBy");
		jsonObject.remove("createtime");
		jsonObject.remove("updateBy");
		jsonObject.remove("updatetime");
		
		return jsonObject;
	}

	//显示字段	
	private String getDisplayField(BpmFormTable bpmFormTable, String displayField) {
		Map<String, String> map = getDisplayFieldRight(displayField);
		Map<String, String> descMap = getDisplayFieldDesc(displayField);
		if (BeanUtils.isNotEmpty(bpmFormTable)) {
			List<BpmFormField> fieldList = bpmFormTable.getFieldList();
			JSONArray jsonAry = new JSONArray();
			for (BpmFormField bpmFormField : fieldList) {
				JSONObject json = new JSONObject();
				json.accumulate("name", bpmFormField.getFieldName());
				String desc = bpmFormField.getFieldDesc();
				if (BeanUtils.isNotEmpty(map) && map.containsKey(bpmFormField.getFieldName())) {
					desc = descMap.get(bpmFormField.getFieldName());
				}
				json.accumulate("desc", desc);
				json.accumulate("type", bpmFormField.getFieldType());
				json.accumulate("style", bpmFormField.getDatefmt());
				String right = "";
				if (BeanUtils.isNotEmpty(map))
					right = map.get(bpmFormField.getFieldName());
				if (StringUtils.isEmpty(right))
					right = getDefaultDisplayFieldRight();

				json.accumulate("right", right);
				jsonAry.add(json);
			}
			displayField = jsonAry.toString();
		}
		return displayField;
	}

	private Map<String, String> getDisplayFieldDesc(String displayField) {
		if (StringUtil.isEmpty(displayField))
			return null;
		Map<String, String> map = new HashMap<String, String>();
		JSONArray jsonAry = JSONArray.fromObject(displayField);

		for (Object obj : jsonAry) {
			JSONObject json = JSONObject.fromObject(obj);
			String name = (String) json.get("name");
			String desc = (String) json.get("desc");
			map.put(name, desc);
		}
		return map;
	}

	private Map<String, String> getDisplayFieldRight(String displayField) {
		if (StringUtil.isEmpty(displayField))
			return null;
		Map<String, String> map = new HashMap<String, String>();
		JSONArray jsonAry = JSONArray.fromObject(displayField);

		for (Object obj : jsonAry) {
			JSONObject json = JSONObject.fromObject(obj);
			String name = (String) json.get("name");
			JSONArray right = (JSONArray) json.get("right");
			map.put(name, right.toString());
		}
		return map;
	}

	private String getDefaultDisplayFieldRight() {
		JSONArray array=new JSONArray();
		JSONObject json = new JSONObject();
		json.accumulate("type", "everyone");
		json.accumulate("id", "");
		json.accumulate("name", "");
		json.accumulate("script", "");
		array.add(json);
		return array.toString();
	}

	//====end 显示字段	
	//==== 导出字段	
	private String getExportField(BpmFormTable bpmFormTable, String field) {
		Map<String, String> map = getExportFieldRight(field);
		Map<String, String> descMap = getExportFieldDesc(field);
		if (BeanUtils.isEmpty(bpmFormTable))
			return field;

		JSONArray jsonAry = new JSONArray();

		List<BpmFormField> fieldList = bpmFormTable.getFieldList();
		jsonAry.add(getTableField(bpmFormTable, fieldList, map, descMap));
		List<BpmFormTable> subTableList = bpmFormTable.getSubTableList();
		for (BpmFormTable subTable : subTableList) {
			jsonAry.add(getTableField(subTable, subTable.getFieldList(), map, descMap));
		}

		return jsonAry.toString();
	}

	private JSONObject getTableField(BpmFormTable bpmFormTable, List<BpmFormField> fieldList, Map<String, String> map, Map<String, String> descMap) {
		JSONObject tableJson = new JSONObject();
		tableJson.element("tableName", bpmFormTable.getTableName());
		tableJson.element("tableDesc", bpmFormTable.getTableDesc());
		tableJson.element("isMain", bpmFormTable.getIsMain());
		JSONArray jsonAry = new JSONArray();
		for (BpmFormField bpmFormField : fieldList) {
			JSONObject json = new JSONObject();
			String key = bpmFormTable.getTableName() + bpmFormField.getFieldName();
			json.element("tableName", bpmFormTable.getTableName());
			json.element("isMain", bpmFormTable.getIsMain());
			json.element("name", bpmFormField.getFieldName());
			String desc = bpmFormField.getFieldDesc();
			if (BeanUtils.isNotEmpty(map) && map.containsKey(bpmFormField.getFieldName())) {
				desc = descMap.get(key);
			}
			json.element("desc", desc);
			json.element("type", bpmFormField.getFieldType());
			json.element("style", bpmFormField.getDatefmt());
			String right = "";
			if (BeanUtils.isNotEmpty(map))
				right = map.get(key);
			if (StringUtils.isEmpty(right))
				right = getDefaultExportFieldRight();
			json.element("right", right);
			jsonAry.add(json);
		}
		tableJson.element("fields", jsonAry.toArray());
		return tableJson;

	}

	private Map<String, String> getExportFieldDesc(String field) {
		if (StringUtil.isEmpty(field))
			return null;
		Map<String, String> map = new HashMap<String, String>();
		JSONArray jsonAry = JSONArray.fromObject(field);
		for (Object obj : jsonAry) {
			JSONObject json = JSONObject.fromObject(obj);
			JSONArray fields = (JSONArray) json.get("fields");
			for (Object obj1 : fields) {
				JSONObject json1 = JSONObject.fromObject(obj1);
				String name = (String) json1.get("name");
				String tableName = (String) json1.get("tableName");
				String desc = (String) json1.get("desc");
				map.put(tableName + name, desc);
			}
		}
		return map;
	}

	private Map<String, String> getExportFieldRight(String field) {
		if (StringUtil.isEmpty(field))
			return null;
		Map<String, String> map = new HashMap<String, String>();
		JSONArray jsonAry = JSONArray.fromObject(field);

		for (Object obj : jsonAry) {
			JSONObject json = JSONObject.fromObject(obj);
			JSONArray fields = (JSONArray) json.get("fields");
			for (Object obj1 : fields) {
				JSONObject json1 = JSONObject.fromObject(obj1);
				String name = (String) json1.get("name");
				String tableName = (String) json1.get("tableName");
				JSONArray right = (JSONArray) json1.get("right");
				map.put(tableName + name, right.toString());
			}
		}
		return map;
	}

	private String getDefaultExportFieldRight() {
		JSONArray jsonAry = new JSONArray();
		JSONObject json = new JSONObject();
		json.accumulate("type", "none");
		json.accumulate("id", "");
		json.accumulate("name", "");
		json.accumulate("script", "");
		jsonAry.add(json);
		return jsonAry.toString();
	}

	//==== end 导出字段	
	/**
	 * 取得业务数据模板明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看业务数据模板明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long id = RequestUtil.getLong(request, "id");
		BpmDataTemplate bpmDataTemplate = bpmDataTemplateService.getById(id);
		return getAutoView().addObject("bpmDataTemplate", bpmDataTemplate);
	}

	/**
	 * 数据列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("dataList_{alias}")
	public ModelAndView dataList(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "alias") String alias) throws Exception {
		//取得当前页URL,如有参数则带参数
		String url = "dataList_" + alias + ".ht";
		String toReplaceUrl = "getDisplay_" + alias + ".ht";
		String __baseURL = request.getRequestURI().replace(url, toReplaceUrl);
		Map<String, Object> params = RequestUtil.getQueryMap(request);
		Map<String, Object> queryParams = RequestUtil.getQueryParams(request);
		//取得传入参数ID
		params.put("__baseURL", __baseURL);
		params.put(BpmDataTemplate.PARAMS_KEY_CTX, request.getContextPath());
		params.put("__tic", "bpmDataTemplate");
		String html = bpmDataTemplateService.getDisplay(alias, params, queryParams);
		ModelAndView mv = new ModelAndView();
		mv.addObject("html", html);
		mv.setViewName("/platform/form/bpmDataTemplatePreview");
		return mv;
	}

	/**
	 * 展示数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getDisplay_{alias}")
	public Map<String, Object> getDisplay(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "alias") String alias) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		try {
			Map<String, Object> params = RequestUtil.getQueryMap(request);
			Map<String, Object> queryParams = RequestUtil.getQueryParams(request);

			String __baseURL = request.getRequestURI();
			params.put("__baseURL", __baseURL);
			params.put("__ctx", request.getContextPath());
			params.put("__displayId__", alias);
			params.put(BpmDataTemplate.PARAMS_KEY_FILTERKEY, RequestUtil.getString(request, BpmDataTemplate.PARAMS_KEY_FILTERKEY));
			params.put(BpmDataTemplate.PARAMS_KEY_ISQUERYDATA, true);
			params.put("__tic", "bpmDataTemplate");

			String html = bpmDataTemplateService.getDisplay(alias, params, queryParams);
			map.put("html", html);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", e.getMessage());
		}
		return map;
	}

	/**
	 * 编辑模板
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("editTemplate")
	public ModelAndView editTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		BpmDataTemplate bpmDataTemplate = bpmDataTemplateService.getById(id);
		return getAutoView().addObject("bpmDataTemplate", bpmDataTemplate);
	}

	/**
	 * 保存模板
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("saveTemplate")
	public void saveTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resultMsg = "";
		Long id = RequestUtil.getLong(request, "id");
		String templateHtml = RequestUtil.getString(request, "templateHtml");

		templateHtml = templateHtml.replace("''", "'");
		BpmDataTemplate bpmDataTemplate = bpmDataTemplateService.getById(id);
		bpmDataTemplate.setTemplateHtml(templateHtml);
		bpmDataTemplateService.update(bpmDataTemplate);
		resultMsg = "更新自定义表管理显示模板成功";
		HttpUtil.writeResponseJson(response, ResponseMessage.success(resultMsg));
	}

	/**
	 * 过滤条件窗口
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("filterDialog")
	public ModelAndView filterDialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long tableId = RequestUtil.getLong(request, "tableId");
		BpmFormTable bpmFormTable = bpmDataTemplateService.getFieldListByTableId(tableId);
		String source = (bpmFormTable.getIsExternal() == BpmFormTable.NOT_EXTERNAL) ? BpmDataTemplate.SOURCE_CUSTOM_TABLE : BpmDataTemplate.SOURCE_OTHER_TABLE;

		List<CommonVar> commonVars = CommonVar.getCurrentVars(false);
		return this.getAutoView().addObject("commonVars", commonVars).addObject("bpmFormTable", bpmFormTable).addObject("tableId", tableId).addObject("source", source);
	}

	/**
	 * 脚本
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("script")
	public ModelAndView script(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long tableId = RequestUtil.getLong(request, "tableId");
		BpmFormTable bpmFormTable = bpmDataTemplateService.getFieldListByTableId(tableId);
		List<CommonVar> commonVars = CommonVar.getCurrentVars(false);
		String source = (bpmFormTable.getIsExternal() == BpmFormTable.NOT_EXTERNAL) ? BpmDataTemplate.SOURCE_CUSTOM_TABLE : BpmDataTemplate.SOURCE_OTHER_TABLE;

		return this.getAutoView().addObject("commonVars", commonVars).addObject("bpmFormTable", bpmFormTable).addObject("tableId", tableId).addObject("source", source);
	}

	/**
	 * 编辑数据。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("editData_{alias}")
	@Action(description = "编辑业务数据模板数据", detail = "编辑业务数据模板数据")
	public ModelAndView editData(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "alias") String alias) throws Exception {

		String pk = RequestUtil.getString(request, "__pk__");

		boolean hasPk = StringUtil.isNotEmpty(pk);
		String returnUrl = RequestUtil.getPrePage(request);
		String ctxPath = request.getContextPath();
		BpmFormDef bpmFormDef = null;
		Long tableId = 0L;
		String tableName = "";
		String pkField = "";
		SysBusEvent sysBusEvent = null;
		if (StringUtil.isNotEmpty(alias)) {
			bpmFormDef = bpmFormDefService.getDefaultVersionByFormKey(alias);
			BpmFormTable bpmFormTable=bpmFormDef.getBpmFormTable();
			tableId = bpmFormDef.getTableId();
			tableName = bpmFormTable.getTableName();
			boolean isReCalcuate=StringUtil.isEmpty(pk);
			//String html = bpmFormHandlerService.obtainHtml(bpmFormDef, pk, "", "", "", ctxPath, "", isReCalcuate,false,false);
			String html =bpmFormDefService.parseHtml(bpmFormDef, pk, "", "", "", "", isReCalcuate,false,false,false,(short) 0);
			pkField = bpmFormTable.getPkField();
			bpmFormDef.setHtml(html); 

			sysBusEvent = sysBusEventService.getByFormKey(alias);
		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/form/bpmDataTemplateEditData");
		return mv.addObject("bpmFormDef", bpmFormDef)
				.addObject("id", pk).addObject("pkField", pkField)
				.addObject("tableId", tableId).addObject("alias", alias)
				.addObject("tableName", tableName).addObject("returnUrl", returnUrl)
				.addObject("sysBusEvent", sysBusEvent).addObject("hasPk", hasPk);
	}

	/**
	 * 明细数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("detailData_{alias}")
	@Action(description = "查看业务数据模板明细数据", detail = "查看业务数据模板明细数据")
	public ModelAndView detailData(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "alias") String alias) throws Exception {

		String pk = RequestUtil.getString(request, "__pk__");
		String ctx=request.getContextPath();
		String form = bpmDataTemplateService.getForm(alias, pk,ctx);
		// 流程结束后的打印模板
		//		List<BpmPrintTemplate> printTemplateList = bpmPrintTemplateService.getAllByFormKeyAndForm(alias);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/form/bpmDataTemplateDetailData");
		return mv.addObject("form", form).addObject("id", pk).addObject("formKey", alias);
		//			.addObject("printTemplateList",printTemplateList);	
	}

	/**
	 * 删除数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	//	@Action(description = "删除业务数据模板数据",
	//			detail="删除业务数据模板：" +
	//					"<#assign entity=bpmDataTemplateService.getById(Long.valueOf(__displayId__))/>" +
	//					"【${entity.name}】的数据"
	//					)
	@RequestMapping("deleteData_{alias}")
	public void deleteData(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "alias") String alias) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;

		String pk = RequestUtil.getString(request, "__pk__");
		try {
			bpmDataTemplateService.deleteData(alias, pk);

			message = new ResultMessage(ResultMessage.Success, "删除成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败:" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 导出数据
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportData_{alias}")
	@Action(description = "导出业务数据模板数据", detail = "导出业务数据模板数据")
	public void exportData(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "alias") String alias) throws Exception {
		Map<String, Object> params = RequestUtil.getQueryMap(request);

		String[] exportIds = RequestUtil.getStringAryByStr(request, "__exportIds__");
		int exportType = RequestUtil.getInt(request, "__exportType__");

		HSSFWorkbook wb = bpmDataTemplateService.export(alias, exportIds, exportType, params);
		String fileName = "DataTemplate_" + DateFormatUtil.getNowByString("yyyyMMddHHmmdd");
		ExcelUtil.downloadExcel(wb, fileName, response);
	}

	/**
	 * 导入业务数据模板数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("importData_{alias}")
	@Action(description = "导入业务数据模板数据", detail = "导入业务数据模板数据")
	public ModelAndView importData(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "alias") String alias) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/form/bpmDataTemplateImportData");
		return mv.addObject("alias", alias);

	}

	/**
	 * 导入数据保存
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importSave")
	@Action(description = "导入数据保存", detail = "导入业务数据模板数据")
	public void importSave(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		String alias = RequestUtil.getString(request, "alias");
		String importType = RequestUtil.getString(request, "importType");
		MultipartFile file = request.getFile("file");
		ResultMessage message = null;
		try {
			bpmDataTemplateService.importFile(file.getInputStream(), alias, importType);
			message = new ResultMessage(ResultMessage.Success, "导入成功!");
		} catch (Exception e) {
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, "导入出错了，请检查导入格式是否正确或者导入的数据是否有问题！");
		}
		writeResultMessage(response.getWriter(), message);

	}

}

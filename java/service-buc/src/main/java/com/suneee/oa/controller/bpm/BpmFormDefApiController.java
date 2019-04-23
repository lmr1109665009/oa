package com.suneee.oa.controller.bpm;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.*;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.controller.mobile.MobileBaseController;
import com.suneee.platform.model.form.*;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.*;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.ShareService;
import com.suneee.ucp.base.vo.ResultVo;
import freemarker.template.TemplateException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;


/**
 * <pre>
 * 对象功能:流程定义接口控制器类
 * 开发公司:深圳象翌
 * 开发人员:子华
 * 创建时间:2017-12-26 10:56:13
 * </pre>
 */
@Controller
@RequestMapping("/api/form/bpmFormDef/")
@Action(ownermodel = SysAuditModelType.FORM_MANAGEMENT)
public class BpmFormDefApiController extends MobileBaseController {

	@Resource
	private BpmFormDefService service;
	@Resource
	private BpmDataTemplateService bpmDataTemplateService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmFormFieldService bpmFormFieldService;
	@Resource
	private BpmFormTemplateService bpmFormTemplateService;
	@Resource
	private FreemarkEngine freemarkEngine;
	@Resource
	private ShareService shareService;
	@Resource
	private GlobalTypeService globalTypeService;

	/**
	 * 取得自定义表单分页列表
	 *
	 * @param request
	 * @param response
	 * @param page
	 *            请求页数
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description = "查看自定义表单分页列表")
	public ResultVo list(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "page", defaultValue = "1") int page) throws Exception {
		Long categoryId = RequestUtil.getLong(request, "categoryId");
		QueryFilter filter = new QueryFilter(request, true);

		if(categoryId==0){
			List<Long> typeIdList=globalTypeService.getTypeIdsByEcode(CookieUitl.getCurrentEnterpriseCode());
			BpmUtil.typeIdFilter(typeIdList);
			filter.addFilter("typeIdList", typeIdList);
		}else {
			GlobalType globalType = globalTypeService.getById(categoryId);
			List<GlobalType> globalTypeList = globalTypeService.getByNodePath(globalType.getNodePath());
			List<Long> list = new ArrayList<>();
			for(GlobalType gt:globalTypeList){
				if(gt.getTypeId() != null){
					list.add(gt.getTypeId());
				}
			}
			filter.addFilter("typeIdList",list);
			filter.getFilters().remove("categoryId");
		}
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

		com.alibaba.fastjson.JSONObject json=new com.alibaba.fastjson.JSONObject();
		com.alibaba.fastjson.JSONObject pageJson=new com.alibaba.fastjson.JSONObject();
		pageJson.put("totalCounts",filter.getPageBean().getTotalCount());
		com.alibaba.fastjson.JSONArray results=new com.alibaba.fastjson.JSONArray();
		for (BpmFormDef bpmFormDef:list){
			com.alibaba.fastjson.JSONObject item=new com.alibaba.fastjson.JSONObject();
			item.put("categoryId",bpmFormDef.getCategoryId());
			item.put("categoryName",bpmFormDef.getCategoryName());
			item.put("createtime",bpmFormDef.getCreatetime());
			item.put("designType",bpmFormDef.getDesignType());
			item.put("formDefId",bpmFormDef.getFormDefId());
			item.put("formDesc",bpmFormDef.getFormDesc());
			item.put("formKey",bpmFormDef.getFormKey());
			item.put("isDefault",bpmFormDef.getIsDefault());
			item.put("isMain",bpmFormDef.getIsMain());
			item.put("isPublished",bpmFormDef.getIsPublished());
			item.put("publishTime",bpmFormDef.getPublishTime());
			item.put("publishedBy",bpmFormDef.getPublishedBy());
			item.put("subject",bpmFormDef.getSubject());
			item.put("tabTitle",bpmFormDef.getTabTitle());
			item.put("tableId",bpmFormDef.getTableId());
			item.put("tableName",bpmFormDef.getTableName());
			item.put("tableDesc",bpmFormDef.getTableDesc());
			item.put("template",bpmFormDef.getTemplate());
			item.put("templatesId",bpmFormDef.getTemplatesId());
			item.put("versionNo",bpmFormDef.getVersionNo());
			com.alibaba.fastjson.JSONObject formTable=new com.alibaba.fastjson.JSONObject();
			if (bpmFormDef.getBpmFormTable()!=null){
				formTable.put("categoryId",bpmFormDef.getBpmFormTable().getCategoryId());
				formTable.put("categoryName",bpmFormDef.getBpmFormTable().getCategoryName());
				formTable.put("createBy",bpmFormDef.getBpmFormTable().getCreateBy());
				formTable.put("creator",bpmFormDef.getBpmFormTable().getCreator());
				formTable.put("dbTableName",bpmFormDef.getBpmFormTable().getDbTableName());
				formTable.put("detailTemplate",bpmFormDef.getBpmFormTable().getDetailTemplate());
				formTable.put("dsAlias",bpmFormDef.getBpmFormTable().getDsAlias());
				formTable.put("dsName",bpmFormDef.getBpmFormTable().getDsName());
				formTable.put("extTable",bpmFormDef.getBpmFormTable().getCategoryId());
				formTable.put("factTableName",bpmFormDef.getBpmFormTable().getFactTableName());
			}else {
				formTable.put("dbTableName","");
			}
			item.put("bpmFormTable",formTable);
			results.add(item);
		}
		pageJson.put("results",results);
		json.put("bpmFormDef",pageJson);
		json.put("publishedCounts",publishedCounts);
		json.put("dataTemplateCounts",dataTemplateCounts);
		json.put("categoryId",categoryId);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取自定义表单分页列表成功！",json);
	}

	/**
	 * 编辑自定义表单。
	 *
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description = "编辑自定义表单")
	public ResultVo edit(HttpServletRequest request) throws Exception {
		Long formDefId = RequestUtil.getLong(request, "formDefId");
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
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取编辑自定义表单成功",bpmFormDef);
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
	 * 发布表单
	 *
	 * @param request
	 */
	@RequestMapping("publish")
	@ResponseBody
	@Action(description = "发布表单", detail = "发布表单【${SysAuditLinkService.getBpmFormDefLink(Long.valueOf(formDefId))}】")
	public ResultVo publish(HttpServletRequest request){
		Long formDefId = RequestUtil.getLong(request, "formDefId");
		ResultVo resultObj = null;
		try {
			service.publish(formDefId, ContextUtil.getCurrentUser().getFullname());
			resultObj = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "发布版本成功!");
		} catch (Exception e) {
			e.printStackTrace();
			resultObj = new ResultVo(ResultVo.COMMON_STATUS_FAILED, e.getCause().toString());
		}
		return resultObj;
	}

	/**
	 * 查看版本
	 * @param request
	 */
	@RequestMapping("versions")
	@ResponseBody
	@Action(description = "查看版本")
	public ResultVo versions(HttpServletRequest request){
		String formKey = request.getParameter("formKey");
		// 版本信息
		List<BpmFormDef> versions = service.getByFormKey(formKey);
		Map<String,Object> data=new HashMap<String, Object>();
		data.put("versions",versions);
		data.put("formName",versions.get(0).getSubject());
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取更多版本成功",data);
	}


	/**
	 * 新建新版本
	 * @param request
	 * @return
	 */
	@RequestMapping("newVersion")
	@Action(description = "新建表单版本", detail = "新建表单版本【${SysAuditLinkService.getBpmFormDefLink(Long.valueof(formDefId))}】")
	public ResultVo newVersion(HttpServletRequest request){
		Long formDefId = RequestUtil.getLong(request, "formDefId");
		ResultVo msg;
		try {
			service.newVersion(formDefId);
			msg = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "新建表单版本成功!");
		} catch (Exception ex) {
			msg = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "新建表单版本失败!");
		}
		return msg;
	}


	/**
	 * 保存复制表单
	 *
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("saveCopy")
	@ResponseBody
	@Action(description = "保存克隆", detail = "保存表单【${SysAuditLinkService.getBpmFormDefLink(Long.valueOf(formDefId))}】的克隆")
	public ResultVo saveCopy(HttpServletRequest request) throws UnsupportedEncodingException {
		long formDefId = RequestUtil.getLong(request, "formDefId");
		String subject=RequestUtil.getString(request,"subject");
		String formDesc=RequestUtil.getString(request,"formDesc");
		Long typeId=RequestUtil.getLong(request,"typeId");
		BpmFormDef bpmFormDef = service.getById(formDefId);

		if (bpmFormDef != null) {
			String oldFormKey = bpmFormDef.getFormKey();
			if (StringUtil.isNotEmpty(subject)){
				bpmFormDef.setSubject(subject);
			}
			if (typeId!=null){
				bpmFormDef.setCategoryId(typeId);
			}
			if (StringUtil.isNotEmpty(formDesc)){
				bpmFormDef.setFormDesc(formDesc);
			}
			bpmFormDef.setFormKey(getCopyFormKey(bpmFormDef.getSubject(),0));
			long id = UniqueIdUtil.genId();
			bpmFormDef.setFormDefId(id);
			bpmFormDef.setIsPublished((short) 0);
			bpmFormDef.setIsDefault((short) 1);
			bpmFormDef.setVersionNo(1);
			try {
				service.copyForm(bpmFormDef, oldFormKey);
				return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"复制表单成功");
			} catch (Exception ex) {
				String str = MessageUtil.getMessage();
				ResultVo resultMessage=null;
				if (StringUtil.isNotEmpty(str)) {
					resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "复制表单失败:" + str);
				} else {
					String message = ExceptionUtil.getExceptionMessage(ex);
					resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
				}
				return resultMessage;
			}
		} else {
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"未能获取到要复制的表单");
		}
	}

	/**
	 * 获取复制表后的信息
	 * @return
	 */
	public Map<String,Object> getCopyFormDefInfo(BpmFormDef formDef,int pos){
		String suffix="_copy\\d+$";
		String formKey=formDef.getFormKey();
		if (Pattern.matches(suffix,formKey)){
			formKey=formKey.replaceFirst(suffix,formKey);
		}
		String newFormDefName=formKey+"_copy"+pos;
		if (service.getCountByFormKey(newFormDefName)>0){
			return getCopyFormDefInfo(formDef,pos++);
		}
		Map<String,Object> info=new HashMap<String, Object>();
		info.put("formKey",newFormDefName);
		String subject=formDef.getSubject()+"_复制"+pos;
		info.put("subject",subject);
		return info;
	}

	/**
	 * 获取复制后key
	 * @param subject
	 * @param pos
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getCopyFormKey(String subject,int pos) throws UnsupportedEncodingException {
		String subjectTitle=subject;
		if (pos>0){
			subjectTitle=subject+pos;
		}
		String newFormDefName=shareService.getPingyin(subjectTitle);
		if (service.getCountByFormKey(newFormDefName)>0){
			return getCopyFormKey(subject,++pos);
		}
		return newFormDefName;
	}

	/**
	 * 选择模板
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selectTemplate")
	@ResponseBody
	@Action(description = "选择模板")
	public ResultVo selectTemplate(HttpServletRequest request) throws Exception {
		Long tableId = RequestUtil.getLong(request, "tableId");
		Map<String,Object> data=new HashMap<String, Object>();
		BpmFormTable table = bpmFormTableService.getById(tableId);

		if (table.getIsMain() == 1) {
			List<BpmFormTable> subTables = bpmFormTableService.getSubTableByMainTableId(tableId);
			List<BpmFormTemplate> mainTableTemplates = bpmFormTemplateService.getAllMainTableTemplate();
			List<BpmFormTemplate> subTableTemplates = bpmFormTemplateService.getAllSubTableTemplate();
			data.put("mainTable", table);
			data.put("subTables", subTables);
			data.put("mainTableTemplates", mainTableTemplates);
			data.put("subTableTemplates", subTableTemplates);
		} else {
			List<BpmFormTable> subTables = new ArrayList<BpmFormTable>();
			subTables.add(table);
			List<BpmFormTemplate> subTableTemplates = bpmFormTemplateService.getAllSubTableTemplate();
			data.put("subTables", subTables);
			data.put("subTableTemplates", subTableTemplates);
		}
		data.put("tableId", tableId);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取模板数据",data);
	}

	/**
	 * 加载编辑器设计模式的模板列表
	 */
	@RequestMapping("chooseDesignTemplate")
	@ResponseBody
	@Action(description = "选择编辑器设计模板")
	public ResultVo chooseDesignTemplate(HttpServletRequest request) throws Exception {
		String templatePath = FormUtil.getDesignTemplatePath();
		String xml = FileUtil.readFile(templatePath + "designtemps.xml");
		Document document = Dom4jUtil.loadXml(xml);
		Element root = document.getRootElement();
		List<Element> list = root.elements();
		com.alibaba.fastjson.JSONArray jsonList=new com.alibaba.fastjson.JSONArray();
		for (Element element : list) {
			com.alibaba.fastjson.JSONObject json=new com.alibaba.fastjson.JSONObject();
			json.put("name",element.attributeValue("name"));
			json.put("alias",element.attributeValue("alias"));
			json.put("templateDesc",element.attributeValue("templateDesc"));
			jsonList.add(json);
		}
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"",jsonList);
	}
}
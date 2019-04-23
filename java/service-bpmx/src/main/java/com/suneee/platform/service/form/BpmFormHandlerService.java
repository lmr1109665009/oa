package com.suneee.platform.service.form;

import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.form.BpmFormDefDao;
import com.suneee.platform.dao.form.BpmFormFieldDao;
import com.suneee.platform.dao.form.BpmFormHandlerDao;
import com.suneee.platform.dao.form.BpmFormTableDao;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.form.*;
import com.suneee.platform.model.system.SysBusEvent;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.platform.service.system.SysBusEventService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;
import freemarker.template.TemplateException;
import net.sf.json.JSONObject;
import org.activiti.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对象功能:自定义表单数据处理Service类 开发公司:广州宏天软件有限公司 开发人员:xwy 创建时间:2011-12-22 11:07:56
 */
@Service
public class BpmFormHandlerService {
	protected Logger logger = LoggerFactory.getLogger(BpmFormHandlerService.class);
	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource
	private BpmFormHandlerDao dao;
	@Resource
	private IdentityService identityService;
	@Resource
	private BpmFormRightsService bpmFormRightsService;
	@Resource
	private BpmFormControlService bpmFormControlService;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private FreemarkEngine freemarkEngine;
	@Resource
	private BpmFormFieldDao bpmFormFieldDao;
	@Resource
	private BpmFormTableDao bpmFormTableDao;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private BpmFormTableService bpmFormTableService;

	@Resource
	private SysBusEventService sysBusEventService;
	@Resource
	private GroovyScriptEngine groovyScriptEngine;
	@Resource
	private TaskService taskService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private BpmService bpmService;
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private BpmFormRunService bpmFormRunService;
	@Resource
	private BpmFormDefDao bpmFormDefDao;
	@Resource
	private IFormDataCalculate formDataCalculate;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private SysUserService sysUserService;
	
	// 流程实例分隔符
	private String INSTANCEID_SPLITOR = "#instanceId#";
	// 流程示意图替换符
	private String FLOW_CHART_SPLITOR = "(?s)<div[^>]*?\\s+name=\"editable-input\"\\s+class=\"flowchart\">(.*?)</div>";
	private String FLOW_CHART_SPLITOR_IE = "(?s)<div[^>]*?\\s+class=\"flowchart\"\\s+name=\"editable-input\">(.*?)</div>";

	/**
	 * 根据表单获取生成html。
	 * 
	 * @param bpmFormDef
	 *            表单定义，需要注入BpmFormTable实例数据。
	 * @param userId
	 *            用户id
	 * @param pkValue
	 *            主键
	 * @param instanceId
	 *            流程实例(获取实例意见)
	 * @param actDefId
	 *            流程定义
	 * @param nodeId
	 *            流程节点
	 * @param ctxPath
	 *            上下文路径
	 * @param parentActDefId
	 *            父流程定义ID
	 * @param isReCalcute
	 *            是否重新计算表单数据，如果为true则重新计算表单数据。
	 * @param isPreView
	 *            是否预览
	 * @param isReadOnly
	 *            是否只读，如果设为true 那么如果有写权限的就会认为是只读，否则按照权限设定来走。
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String obtainHtml(BpmFormDef bpmFormDef, String pkValue, String instanceId, String actDefId, String nodeId, String ctxPath, String parentActDefId, boolean isReCalcute, boolean isPreView, boolean isReadOnly, short status) throws Exception {
		// 实例化BpmFormData数据
		BpmFormData data = getBpmFormData(bpmFormDef.getBpmFormTable(), pkValue, instanceId, actDefId, nodeId, isReCalcute, isPreView,false,status);
 
		Map<String, Object> permission = bpmFormRightsService.getByFormKeyAndUserId(bpmFormDef, actDefId, nodeId, parentActDefId, isReadOnly);
		List<TaskOpinion> taskOpinionList = new ArrayList<TaskOpinion>();
		if(StringUtil.isNotEmpty(instanceId)){
			taskOpinionList = taskOpinionService.getByActInstId(instanceId);
		}
		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
		ParserParam param = new ParserParam(data, permission,taskOpinionList, bpmDefinition);
		param.putVar("instanceId", instanceId);
		param.putVar("actDefId", actDefId);
		
		String output = BpmFormDefHtmlParser.parse(StringUtil.unescapeHtml(bpmFormDef.getHtml()), param);
		
		if(bpmFormDef.getTabTitle().indexOf(BpmFormDef.PageSplitor) > -1){
			output = getTabHtml(bpmFormDef.getTabTitle(), output,permission);
		}
		
		//<---测试新解释器
		return output;
	}

	/***
	 * 获取自定义表流程实例的业务数据 JSON ,Number类型不格式化
	 * 
	 * @param pkValue
	 *            PK 不可为空
	 * @param instance
	 *            可为空
	 * @param nodeId
	 *            可为空
	 * @return jsonData
	 * @throws Exception
	 */
	public String getBpmFormDataJson(ProcessRun run, String pkValue, String nodeId) throws Exception {
		if (StringUtil.isEmpty(pkValue))
			throw new RuntimeException("获取业务json PK 不能为空");
		if (run == null)
			run = processRunService.getByBusinessKey(pkValue);

		BpmFormDef bpmFormDef = bpmFormDefDao.getById(run.getFormDefId());
		BpmFormTable bpmFormTable = bpmFormTableService.getByTableId(bpmFormDef.getTableId(), 1);

		BpmFormData data = dao.getByKey(bpmFormTable, pkValue, run.getActDefId(), nodeId, true);

		Map<String, Map> model = new HashMap<String, Map>();
		model.put("main", data.getMainFields());
		model.put("opinion", data.getOptions());
		model.put("sub", data.getSubTableMap());

		return com.alibaba.fastjson.JSONObject.toJSONString(model);
	}

	/***
	 * 获取自定义表JSON数据 ,Number类型不格式化
	 * 
	 * @param id
	 * @return jsonData
	 * @throws Exception
	 * */
	public String getFormDataJson(String id, long tableId) throws Exception {
		if (StringUtil.isEmpty(id) || tableId < 1)
			throw new RuntimeException("业务数据获取失败缺失必要参数 id or tableId");

		BpmFormTable bpmFormTable = bpmFormTableService.getByTableId(tableId, 1);

		BpmFormData data = dao.getByKey(bpmFormTable, id, null, null, true);

		Map<String, Map> model = new HashMap<String, Map>();
		model.put("main", data.getMainFields());
		model.put("sub", data.getSubTableMap());
		return com.alibaba.fastjson.JSONObject.toJSONString(model);
	}

	/**
	 * 根据主键值，流程实例Id 获取bpmFormData 实例
	 * 
	 * @param pkValue
	 * @param nodeId
	 * @return
	 * @throws Exception
	 */
	public BpmFormData getBpmFormData(String pkValue, String nodeId) throws Exception {
		if (StringUtil.isEmpty(pkValue))
			throw new RuntimeException("获取业务json PK 不能为空");
		ProcessRun run = processRunService.getByBusinessKey(pkValue);

		BpmFormDef bpmFormDef = bpmFormDefDao.getById(run.getFormDefId());

		BpmFormTable bpmFormTable = bpmFormDef.getBpmFormTable();

		BpmFormData data = dao.getByKey(bpmFormTable, pkValue, run.getActDefId(), nodeId, true);

		return data;
	}

	/**
	 * 
	 * 根据表ID，主键值，流程实例Id 获取bpmFormData 实例
	 * @param formTable
	 * @param pkValue
	 * @param instanceId
	 * @param actDefId
	 * @param nodeId
	 * @param isReCaculate
	 * @param isPreView
	 * @param isCopy
	 * @return
	 * @throws Exception 
	 * BpmFormData
	 * @exception 
	 * @since  1.0.0
	 */
	public BpmFormData getBpmFormData(BpmFormTable formTable, String pkValue, String instanceId, String actDefId, String nodeId, boolean isReCaculate, boolean isPreView,boolean isCopy,short status) throws Exception {
		BpmFormData data = null;
		if (StringUtil.isNotEmpty(pkValue)) {
			// 根据主键和表取出数据填充BpmFormData。
			data = dao.getByKey(formTable, pkValue, actDefId, nodeId, true);
			// 获取表单的意见。
			if (StringUtil.isNotEmpty(instanceId)) {
				Map<String, String> formOptions = getFormOptionsByInstance(instanceId);
				if (BeanUtils.isNotEmpty(formOptions)) {
					data.setOptions(formOptions);
				}
			}
			calcData(formTable, data, isPreView, isReCaculate,status);
			
			if(isCopy){//如果是复制需要特殊处理
				//清除主表id
				data.setMainField(data.getBpmFormTable().getPkField().toLowerCase(), "");
				// 清除意见字段数据
				Map<String, Object> resultMap = data.getMainFields();
				List<String> opinionFieldList = bpmNodeSetService.getOpinionFields(actDefId);
				for (String field : opinionFieldList) {
					resultMap.put(field.toLowerCase(), "");
				}
				// 清除子表的id
				for (SubTable subTable : data.getSubTableList()) {
					String subPk = subTable.getPkName();
					if (StringUtil.isEmpty(subPk))
						continue;
					for (Map<String, Object> map : subTable.getDataList()) {
						map.put(subPk.toLowerCase(), "");
					}
				}
			}
		} else {
			data = getByFormTable(formTable, isPreView);
		}
		return data;
	}

	/**
	 * 重新计算表单数据。
	 * 
	 * @param formTable
	 * @param data
	 * @param isPreView
	 */
	public void calcData(BpmFormTable formTable, BpmFormData data, boolean isPreView, boolean isReCalucate,short status) {
		if (isReCalucate) {
			// 获取流水号和脚本计算结果
			List<BpmFormField> list = formTable.getFieldList();
			Map<String, Object> resultMap =new HashMap<>();
			//草稿或者子流程不需要计算脚本结果
			if(status!=4&&status!=40){
			resultMap = this.getCalculateDataMap(list, isPreView, false);
			}
			Map<String, Object> mainFields = data.getMainFields();
			mainFields.putAll(resultMap);
			//计算子表初始化数据
			if (data.getSubTableList().size() > 0) {
				Map<String, BpmFormTable> tableMap = convertMap(formTable.getSubTableList());
				for (SubTable subTable : data.getSubTableList()) {
					BpmFormTable table = tableMap.get(subTable.getTableName());
					Map<String, Object> rowData = this.getCalculateDataMap(table.getFieldList(), false, true);
					subTable.setInitData(rowData);
				}
			}
		}
	}

	/**
	 * 根据表单元数据获取数据。
	 * 
	 * @param formTable
	 * @param isPreView
	 * @return
	 */
	public BpmFormData getByFormTable(BpmFormTable formTable, boolean isPreView) {
		BpmFormData data = new BpmFormData();
		// 获取流水号和脚本计算结果
		List<BpmFormField> list = formTable.getFieldList();
		Map<String, Object> resultMap = this.getCalculateDataMap(list, isPreView, true);
		// 将流水号和脚本计算结果加入data
		data.setMainFields(resultMap);

		List<BpmFormTable> listTable = formTable.getSubTableList();
		for (BpmFormTable table : listTable) {
			List<BpmFormField> fields = table.getFieldList();
			Map<String, Object> rowData = this.getCalculateDataMap(fields, isPreView, true);
			SubTable subTable = new SubTable();

			String fk = table.getRelation();
			String subPk = table.getPkField();

			subTable.setTableName(table.getTableName());
			subTable.setInitData(rowData);
			subTable.setFkName(fk);
			subTable.setPkName(subPk);
			data.addSubTable(subTable);
		}
		data.setBpmFormTable(formTable);
		return data;
	}

	private Map<String, BpmFormTable> convertMap(List<BpmFormTable> listTable) {
		Map<String, BpmFormTable> map = new HashMap<String, BpmFormTable>();
		for (BpmFormTable table : listTable) {
			map.put(table.getTableName(), table);
		}
		return map;
	}

	/**
	 * 获取流水号和脚本计算结果
	 * 
	 * @param list
	 * @param data
	 * @param actDefId
	 * @return Map<String,Object>
	 */
	public Map<String, Object> getCalculateDataMap(List<BpmFormField> list, boolean isPreView, boolean allField) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (BpmFormField field : list) {
			String fieldName = field.getFieldName();
			Object rtn = formDataCalculate.calcShowForm(field, isPreView);
			//不是要求全部字段并且字段没有经过计算的不添加到结果中。
			if (!allField && !field.isCalculated())
				continue;
			resultMap.put(fieldName, rtn);
		}
		return resultMap;
	}

	/**
	 * 对设计的表单进行解析。
	 * 
	 * <pre>
	 * 生成实际的表单html。
	 * </pre>
	 * 
	 * @param title
	 * @param parseResult
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String obtainHtml(String title, ParseReult parseResult, Map<String, Object> permission, Boolean isPreview) throws Exception {
		String template = parseResult.getTemplate();

		BpmFormTable bpmFormTable = parseResult.getBpmFormTable();

		if (BeanUtils.isEmpty(permission)) {
			BpmFormDef bpmFormDef = new BpmFormDef();
			bpmFormDef.setBpmFormTable(bpmFormTable);
			permission = bpmFormRightsService.getByFormKeyAndUserId(bpmFormDef, "", "", "", false);
		}
		BpmFormData data = getFormData(bpmFormTable);
		
		Map<String, Map> model = new HashMap<String, Map>();
		model.put("main", data.getMainFields());
		model.put("opinion", data.getOptions());
		model.put("sub", data.getSubTableMap());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("model", model);
		// 传入控制器的service，用于在模版中解析字段。
		map.put("service", bpmFormControlService);
		// 传入字段的权限。
		map.put("permission", permission);
		// 兼容之前生成的模版。
		map.put("table", new HashMap());

		map.put("sub", data.getSubTableMap());

		String output = freemarkEngine.parseByStringTemplate(map, template);
		// 解析子表的默认内容
		// 有分页的情况。
		if (title.indexOf(BpmFormDef.PageSplitor) > -1) {
			output = getTabHtml(title, output,permission);
		}

		String permissionHtml = "{}";
		if (BeanUtils.isNotEmpty(permission)) {
			permissionHtml = JSONObject.fromObject(permission).toString();
		}
		output += "<script type=\"text/javascript\" > var permission = " + permissionHtml + " </script>";

		//--->测试新解释器
		/*Map<String, Object> param = new HashMap<String, Object>();
		data.setBpmFormTable(bpmFormTable);
		param.put("data", data);
		param.put("permission", permission);
		output = BpmFormDefHtmlParser.parse(parseResult.getTemplate(), param);*/
		//<---测试新解释器

		return output;
	}

	public BpmFormData getFormData(BpmFormTable bpmFormTable) {
		BpmFormData data = new BpmFormData();

		List<BpmFormField> list = bpmFormTable.getFieldList();
		// 获取流水号和脚本计算结果
		Map<String, Object> resultMap = getCalculateDataMap(list, true, true);
		// 将流水号和脚本计算结果加入data
		data.setMainFields(resultMap);

		List<BpmFormTable> tableList = bpmFormTable.getSubTableList();
		for (BpmFormTable table : tableList) {
			List<BpmFormField> fields = table.getFieldList();
			Map<String, Object> initMap = getCalculateDataMap(fields, true, true);

			String fk = table.getRelation();
			String subPk = table.getPkField();

			SubTable subTable = new SubTable();
			subTable.setTableName(table.getTableName().toLowerCase());
			subTable.setInitData(initMap);
			subTable.setFkName(fk);
			subTable.setPkName(subPk);

			data.addSubTable(subTable);
		}
		data.setBpmFormTable(bpmFormTable);//设置
		return data;
	}

	/**
	 * 根据流程实例取得流程的意见。
	 * 
	 * @param instanceId
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	public Map<String, String> getFormOptionsByInstance(String instanceId) throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();

		List<TaskOpinion> list = taskOpinionService.getByActInstId(instanceId);
		for (TaskOpinion option : list) {
			if (StringUtil.isEmpty(option.getFieldName()))
				continue;
			String fieldName = option.getFieldName().toLowerCase();
			generateOpinion(option, fieldName, map);
		}
		return map;
	}

	private void generateOpinion(TaskOpinion opinion, String fieldName, Map<String, String> map) throws IOException, TemplateException {
		String resultOpinion = "";
		if (map.containsKey(fieldName))
			resultOpinion = map.get(fieldName);
		//获取组织信息
		SysOrg sysOrg = sysOrgService.getByUserId(opinion.getExeUserId());
		opinion.setSysOrg(sysOrg);
		// 获取用户签章
		SysUser user = sysUserService.getById(opinion.getExeUserId());
		opinion.setWebSignUrl(user.getWebSignUrl());
		resultOpinion += TaskOpinionService.getOpinion(opinion, true);
		map.put(fieldName, resultOpinion);
	}

	/**
	 * 获取tab的html。
	 * 
	 * @param tabTitle
	 * @param html
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	public String getTabHtml(String tabTitle, String html) throws TemplateException, IOException {
		String[] aryTitle = tabTitle.split(BpmFormDef.PageSplitor);
		String[] aryHtml = html.split(BpmFormDef.PageSplitor);
		if(aryHtml.length<=1) return html;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < aryTitle.length; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("title", aryTitle[i]);
			map.put("html", aryHtml[i]);
			list.add(map);
		}
		String formPath = BpmFormTemplateService.getFormTemplatePath() + "tab.ftl";
		String tabTemplate = FileUtil.readFile(formPath);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tabList", list);
		String output = freemarkEngine.parseByStringTemplate(map, tabTemplate);

		// 分时把js移到tab外面防止ligerTab二次加载js
		Pattern pattern = Pattern.compile("<script([\\s\\S]*?)script>");
		Matcher matcher = pattern.matcher(output);
		while (matcher.find()) {
			String str = matcher.group();
			output = output.replace(str, "");
			output = str + "\n" + output;
		}

		return output;
	}
	
	/**
	 * 获取tab的html。加入权限控制
	 * 
	 * @param tabTitle
	 * @param html
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	public String getTabHtml(String tabTitle, String html,Map<String, Object> permission) throws TemplateException, IOException {
		String[] aryTitle = tabTitle.split(BpmFormDef.PageSplitor);
		String[] aryHtml = html.split(BpmFormDef.PageSplitor);
		Map<String,Object> formTabPermission = new HashMap<String,Object>();
		Object o =  permission.get("formTabPermission");
		if(o != null){
			formTabPermission = (Map<String,Object>) o;
		}
		if(aryHtml.length<=1) return html;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < aryTitle.length; i++) {
			if(!formTabPermission.isEmpty()){
				if(!formTabPermission.containsKey(aryTitle[i].toLowerCase())){
					continue;
				}else if(!formTabPermission.get(aryTitle[i].toLowerCase()).equals(FieldRight.R.getVal().toLowerCase())){
					continue;
				}
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("title", aryTitle[i]);
			map.put("html", aryHtml[i]);
			list.add(map);
		}
		String formPath = BpmFormTemplateService.getFormTemplatePath() + "tab.ftl";
		String tabTemplate = FileUtil.readFile(formPath);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tabList", list);
		String output = freemarkEngine.parseByStringTemplate(map, tabTemplate);

		// 分时把js移到tab外面防止ligerTab二次加载js
		Pattern pattern = Pattern.compile("<script([\\s\\S]*?)script>");
		Matcher matcher = pattern.matcher(output);
		while (matcher.find()) {
			String str = matcher.group();
			output = output.replace(str, "");
			output = str + "\n" + output;
		}

		return output;
	}

	/**
	 * 处理动态表单数据
	 * 
	 * @param bpmFormData
	 * @throws Exception
	 */
	public void handFormData(BpmFormData bpmFormData, String formKey, String jsonData) throws Exception {
		SysBusEvent busEvent = sysBusEventService.getByFormKey(formKey);
		handEvent(busEvent, true, bpmFormData, jsonData);
		dao.handFormData(bpmFormData);
		handEvent(busEvent, false, bpmFormData, jsonData);
	}

	/**
	 * 处理后台脚本。
	 * 
	 * @param busEvent
	 * @param isBefore
	 * @param bpmFormData
	 */
	private void handEvent(SysBusEvent busEvent, boolean isBefore, BpmFormData bpmFormData, String jsonData) {
		if (busEvent == null)
			return;
		String script = "";
		if (isBefore) {
			script = busEvent.getPreScript();
		} else {
			script = busEvent.getAfterScript();
		}
		if (StringUtil.isEmpty(script))
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("data", bpmFormData);
		params.put("jsonData", jsonData);
		groovyScriptEngine.execute(script, params);
	}

	/**
	 * 根据主键查询列表数据。
	 * 
	 * @param formTable
	 *            表对象
	 * @param pkValue
	 * @return
	 * @throws Exception
	 */
	public BpmFormData getByKey(BpmFormTable formTable, String pkValue) throws Exception {
		return dao.getByKey(formTable, pkValue, true);
	}

	/**
	 * 根据主键ID查询表单数据。
	 * 
	 * @param tableId
	 *            表ID
	 * @param pkValue
	 * @return
	 * @throws Exception
	 */
	public BpmFormData getByTableIdPk(Long tableId, String pkValue) throws Exception {
		BpmFormTable formTable = bpmFormTableService.getByTableId(tableId, 1);
		return dao.getByKey(formTable, pkValue, true);
	}

	/**
	 * 删除业务数据。
	 * 
	 * @param id
	 * @param tableName
	 * @throws Exception
	 */
	public void delById(String pkValue, BpmFormTable bpmFormTable) throws Exception {
		if (bpmFormTable.isExtTable()) {
			String sql = "delete from " + bpmFormTable.getTableName() + " where " + bpmFormTable.getPkField() + "=" + pkValue;
			JdbcTemplateUtil.execute(bpmFormTable.getDsAlias(), sql);
		} else {
			String sql = "delete from " + TableModel.CUSTOMER_TABLE_PREFIX + bpmFormTable.getTableName() + " where id=" + pkValue;
			JdbcTemplateUtil.execute(sql);
		}
	}

	/**
	 * 判断指定的表数据是否存在。
	 * 
	 * @param dsName
	 * @param tableName
	 * @param pkValue
	 * @return
	 * @throws Exception
	 */
	public boolean isExistsData(String dsName, String tableName, String pkName, String pkValue) throws Exception {

		Map<String, Object> data = dao.getByKey(dsName, tableName, pkName, pkValue);
		if (BeanUtils.isEmpty(data)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 删除业务数据。
	 * 
	 * @param id
	 * @param tableName
	 */
	public void delByIdTableName(String id, String tableName) {
		final Long lId = new Long(id);
		String sql = "delete from " + tableName + " where id=?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement pps) throws SQLException {
				pps.setLong(1, lId);
			}
		});
	}

	/**
	 * 根据表名和主键是否有数据。
	 * 
	 * @param tableName
	 * @param pk
	 * @return
	 */
	public boolean isHasDataByPk(String tableName, Long pk) {
		return dao.isHasDataByPk(tableName, pk);
	}

	/**
	 * 删除表数据。
	 * 
	 * @param dsAlias
	 *            数据源别名
	 * @param fullTableName
	 *            表名
	 * @param pk
	 *            主键
	 * @throws Exception
	 */
	public void delByDsAliasAndTableName(String dsAlias, String fullTableName, String pk) throws Exception {
		String tableName = fullTableName;
		if (fullTableName.startsWith(TableModel.CUSTOMER_TABLE_PREFIX)) {
			tableName = tableName.replaceFirst(TableModel.CUSTOMER_TABLE_PREFIX, "");
		} else {
			fullTableName = TableModel.CUSTOMER_TABLE_PREFIX + tableName;
		}
		BpmFormTable bpmFormTable = bpmFormTableService.getByAliasTableName(dsAlias, tableName);
		String sql = "delete from " + fullTableName + " where " + bpmFormTable.getPkField() + "=" + pk;
		JdbcTemplateUtil.execute(dsAlias, sql);
	}

	public Map<String, Object> handMap(BpmFormTable bpmFormTable, Map<String, BpmFormField> mainFieldDescMap,
			Map<String, Object> map) {
		return dao.handMap(bpmFormTable,mainFieldDescMap,map);
	}
}

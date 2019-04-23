package com.suneee.platform.service.form;

import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.model.CurrentUser;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.form.BpmFormRightsDao;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormRights;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.service.system.CurrentUserService;
import com.suneee.platform.service.util.ServiceUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;

/**
 * <pre>
 * 对象功能:表单权限 Service类。 存储表单字段，子表，意见等权限。 
 * 开发公司:广州宏天软件有限公司 
 * 开发人员:xwy 
 * 创建时间:2012-02-10 10:48:16
 * </pre>
 */
@Service
public class BpmFormRightsService {
	@Resource
	private BpmFormRightsDao bpmFormRightsDao;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private CurrentUserService currentUserService;
	@Resource
	private GroovyScriptEngine groovyScriptEngine;

	/**
	 * 获取默认的权限数据。
	 * 
	 * 这个type真恶心
	 * 
	 * @param title
	 * @param memo
	 * @param type
	 * @return
	 */
	public JSONObject getPermissionJson(String title, String memo, int type) {
		String defJson = "{type:'everyone',id:'', fullname:''}";
		JSONObject json = new JSONObject();
		json.element("title", title);
		json.element("memo", memo);
		if (type == 4) {
			json.element("show", "{addBtn:'true'}");
		}else {
			json.element("read", defJson);
			json.element("write", defJson);
			json.element("required", defJson);
		}

		return json;
	}

	/**
	 * 保存表单权限。
	 * 
	 * @param formDefId
	 *            表单KEY
	 * @param permission
	 *            权限JSON数据。
	 * @throws Exception
	 */
	public void save(String formKey, JSONObject permission) throws Exception {
		save(null, null, formKey, permission, "");
	}

	/**
	 * 保存任务节点权限。
	 * 
	 * @param actDefId
	 *            流程定义id
	 * @param formDefId
	 *            表单定义id
	 * @param permission
	 *            权限JSON数据。
	 * @throws Exception
	 */
	public void save(String actDefId, String formKey, JSONObject permission, String parentActDefId) throws Exception {
		save(actDefId, null, formKey, permission, parentActDefId);
	}

	/**
	 * 构建BpmFormRights 对象。
	 * 
	 * @param jsonObj
	 * @param formKey
	 * @param actDefId
	 * @param nodeId
	 * @param parentActDefId
	 * @param type
	 * @return
	 */
	private BpmFormRights getBpmFormRights(JSONObject jsonObj, String formKey, String actDefId, String nodeId, String parentActDefId, short type) {
		String name = (String) jsonObj.get("title");
		String json = jsonObj.toString();
		BpmFormRights bpmFormRights = new BpmFormRights(UniqueIdUtil.genId(), formKey, name, json, type);
		bpmFormRights.setActDefId(actDefId);
		bpmFormRights.setNodeId(nodeId);
		bpmFormRights.setParentActDefId(parentActDefId);
		return bpmFormRights;
	}

	/**
	 * 保存任务节点权限。
	 * 
	 * @param actDefId
	 *            流程定义id
	 * @param nodeId
	 *            流程节点id
	 * @param formDefId
	 *            表单定义id
	 * @param permission
	 *            权限JSON数据。
	 * @throws Exception
	 */
	public void save(String actDefId, String nodeId, String formKey, JSONObject permission, String parentActDefId) throws Exception {
		JSONArray fieldPermissions = permission.getJSONArray("field");
		JSONArray tablePermissions = permission.getJSONArray("subtable");
		JSONArray opinionPermissions = permission.getJSONArray("opinion");
		JSONArray subTableShows = permission.getJSONArray("subTableShows");
		JSONArray formTab = permission.getJSONArray("formTab");
		JSONArray teamTable = permission.getJSONArray("teamTable");
		List<BpmFormRights> list = new ArrayList<BpmFormRights>();

		// 表单字段权限。
		delFormRights(formKey, actDefId, nodeId, parentActDefId);
		//主表字段
		if (BeanUtils.isNotEmpty(fieldPermissions)) {
			for (Object obj : fieldPermissions) {
				JSONObject jsonObj = (JSONObject) obj;
				BpmFormRights bpmFormRights = getBpmFormRights(jsonObj, formKey, actDefId, nodeId, parentActDefId, BpmFormRights.FieldRights);
				list.add(bpmFormRights);
			}
		}
		// 子表权限。
		if (BeanUtils.isNotEmpty(tablePermissions)) {
			for (Object obj : tablePermissions) {
				JSONObject jsonObj = (JSONObject) obj;
				BpmFormRights bpmFormRights = getBpmFormRights(jsonObj, formKey, actDefId, nodeId, parentActDefId, BpmFormRights.TableRights);
				list.add(bpmFormRights);
			}
		}
		// 表单意见权限。
		if (BeanUtils.isNotEmpty(opinionPermissions)) {
			for (Object obj : opinionPermissions) {
				JSONObject jsonObj = (JSONObject) obj;
				BpmFormRights bpmFormRights = getBpmFormRights(jsonObj, formKey, actDefId, nodeId, parentActDefId, BpmFormRights.OpinionRights);
				list.add(bpmFormRights);
			}
		}
		//子表的显示与否
		if (BeanUtils.isNotEmpty(subTableShows)) {
			for (Object obj : subTableShows) {
				JSONObject jsonObj = (JSONObject) obj;
				BpmFormRights bpmFormRights = getBpmFormRights(jsonObj, formKey, actDefId, nodeId, parentActDefId, BpmFormRights.TableShowRights);
				list.add(bpmFormRights);
			}
		}
		//表单标签权限
		if (BeanUtils.isNotEmpty(formTab)) {
			for (Object obj : formTab) {
				JSONObject jsonObj = (JSONObject) obj;
				BpmFormRights bpmFormRights = getBpmFormRights(jsonObj, formKey, actDefId, nodeId, parentActDefId, BpmFormRights.FormTabShowRights);
				list.add(bpmFormRights);
			}
		}
		//分页权限
		if(BeanUtils.isNotEmpty(teamTable)){
			for (Object obj : teamTable) {
				JSONObject jsonObj = (JSONObject) obj;
				BpmFormRights bpmFormRights = getBpmFormRights(jsonObj, formKey, actDefId, nodeId, parentActDefId, BpmFormRights.TeamTableShowRights);
				list.add(bpmFormRights);
			}
		}

		for (BpmFormRights right : list) {
			bpmFormRightsDao.add(right);
		}
	}

	/**
	 * 删除权限。
	 * 
	 * @param actDefId
	 * @param nodeId
	 * @param formKey
	 * @param parentActDefId
	 */
	public void delFormRights(String formKey, String actDefId, String nodeId, String parentActDefId) {
		if (StringUtil.isNotEmpty(nodeId)) {
			bpmFormRightsDao.delByActDefNodeId(actDefId, nodeId, parentActDefId);
		} else if (StringUtil.isNotEmpty(actDefId)) {
			bpmFormRightsDao.delByActDefId(actDefId, parentActDefId);
		} else {
			bpmFormRightsDao.delByFormKey(formKey, false);
		}
	}

	/**
	 * 根据流程定义id，任务节点id和表单id获取权限数据。
	 * 
	 * @param formKey
	 * @param actDefId
	 * @param nodeId
	 * @param parentActDefId
	 * @param allowEmptyTry
	 *            如果权限为空的情况是否继续。
	 * @return
	 */
	public Map<String, List<JSONObject>> getPermission(String formKey, String actDefId, String nodeId, String parentActDefId, boolean allowEmptyTry) {
		Map<String, List<JSONObject>> map = new HashMap<String, List<JSONObject>>();
		List<BpmFormRights> rightList = getFormRights(formKey, actDefId, nodeId, parentActDefId, allowEmptyTry);
		BpmFormDef bpmFormDef = bpmFormDefService.getDefaultVersionByFormKey(formKey);
		if (BeanUtils.isEmpty(rightList)) {
			map = getPermissionJson(bpmFormDef);
		} else {
			map = getPermissionJson(rightList, bpmFormDef);
		}
		return map;
	}
	
	/**
	 * 初始化表分组权限
	 * @param model
	 * @return
	 */
	public JSONObject getTeamTablePermission(JSONObject team){
		String defJson = "{type:'everyone',id:'',fullname:''}";
		JSONObject json = new JSONObject();
		json.element("title", team.get("teamTitle"));
		json.element("memo", team.get("teamName"));
		json.element("read", defJson);
		json.element("write", defJson);
		json.element("required", defJson);
		json.element("teamField", team.get("teamField"));
		return json;
	}
	
	
	
	/**
	 * 根据表ID获取表表单的权限。
	 * 
	 * @param tableId
	 *            数据表id
	 * @return
	 */
	public Map<String, List<JSONObject>> getPermissionByTableId(BpmFormDef bpmFormDef) {
		Map<String, List<JSONObject>> map = new HashMap<String, List<JSONObject>>();
		Long tableId = bpmFormDef.getTableId();
		BpmFormTable bpmFormTable = bpmFormTableService.getTableById(tableId);
		
		//获取表单上有展示的字段
		Map<String,Set<String>> formViewFiledMap =  FormUtil.getBpmFormViewField(bpmFormDef,bpmFormTable);
		
		//1. 主表字段权限
		
		Set<String> mainSet = formViewFiledMap.get(bpmFormTable.getTableName());
		List<BpmFormField> fieldList = bpmFormTable.getFieldList();
		List<JSONObject> fieldJsonList = new ArrayList<JSONObject>();
		for (BpmFormField field : fieldList) {
			if(!mainSet.contains(field.getFieldName())){
				continue;
			}
			JSONObject permission = getPermissionJson(field.getFieldName(), field.getFieldDesc(), 1);
			//增加字段的控件类型
			permission.put("controlType", field.getControlType());
			permission.put("tableName", bpmFormTable.getTableName());

			fieldJsonList.add(permission);
		}
		map.put("field", fieldJsonList);
		//2. 子表权限。
		List<JSONObject> tableJsonList = new ArrayList<JSONObject>();
		List<BpmFormTable> tableList = bpmFormTable.getSubTableList();
		
		for (BpmFormTable table : tableList) {
			// 子表整个表的权限
			JSONObject permission = getPermissionJson(table.getTableName(), table.getTableDesc(), 2);
			//每个子表中的每个字段
			List<BpmFormField> subFieldList = table.getFieldList();
			List<JSONObject> subFieldJsonList = new ArrayList<JSONObject>();
			
			Set<String> subFieldSet = formViewFiledMap.get(table.getTableName());
			
			for (BpmFormField field : subFieldList) {
				if(!subFieldSet.contains(field.getFieldName())){
					continue;
				}
				JSONObject subPermission = getPermissionJson(field.getFieldName(), field.getFieldDesc(), 1);
				//增加字段的控件类型
				subPermission.put("controlType", field.getControlType());
				subPermission.put("tableName", table.getTableName());
				subFieldJsonList.add(subPermission);
			}
			if(!subFieldJsonList.isEmpty()){
				permission.put("subField", subFieldJsonList);
				tableJsonList.add(permission);
			}
		}
		map.put("table", tableJsonList);

		//3.子表显示与否
		List<JSONObject> tableShowJsonList = new ArrayList<JSONObject>();
		Set<String> subTableSet = formViewFiledMap.get("subTable");
		for (BpmFormTable table : tableList) {
			if(!subTableSet.contains(table.getTableName())){
				continue;
			}
			JSONObject permission = getPermissionJson(table.getTableName(), table.getTableDesc(), 4);
			tableShowJsonList.add(permission);
		}
		map.put("tableShow", tableShowJsonList);
		
		return map;
	}

	/**
	 * 获取通过表单设定定义表单的权限数据。
	 * 
	 * <pre>
	 * 通过设计定义的表单，
	 * 如果没有还没有设置过权限，那么权限信息可以通过解析表单获取。
	 * </pre>
	 * 
	 * @param bpmFormDef
	 * @return
	 */
	private Map<String, List<JSONObject>> getPermissionJson(BpmFormDef bpmFormDef) {
		// 获取模版。
		String html = bpmFormDef.getHtml();

		Map<String, List<JSONObject>> map = getPermissionByTableId(bpmFormDef);
		// 意见权限。
		List<JSONObject> opinionJsonList = new ArrayList<JSONObject>();
		Map<String, String> opinionMap = FormUtil.parseOpinion(html);
		Set<Entry<String, String>> opinionSet = opinionMap.entrySet();
		for (Iterator<Entry<String, String>> opinionIt = opinionSet.iterator(); opinionIt.hasNext();) {
			Entry<String, String> tmp = opinionIt.next();
			JSONObject permission = getPermissionJson(tmp.getKey(), tmp.getValue(), 3);
			opinionJsonList.add(permission);
		}
		map.put("opinion", opinionJsonList);
		
		//表单标签权限
		List<JSONObject> formTabJsonList = new ArrayList<JSONObject>();
		String formTab = bpmFormDef.getTabTitle();
		String[] tabs = formTab.split(BpmFormDef.PageSplitor);
		for(String tab : tabs){
			JSONObject permission = getPermissionJson(tab, "表单标签权限", 5);
			permission.put("tableName", bpmFormDef.getFormDesc());
			formTabJsonList.add(permission);
		}
		map.put("formTab", formTabJsonList);
		
		//表单分组权限
		List<JSONObject> teamTableJsonList = getTeamTableJson(bpmFormDef);
		map.put("teamTable", teamTableJsonList);
		
		return map;
	}

	/**
	 * 获取表单权限列表。
	 * 
	 * @param formKey
	 * @param actDefId
	 * @param nodeId
	 * @param parentActDefId
	 * @return
	 */
	private List<BpmFormRights> getFormRights(String formKey, String actDefId, String nodeId, String parentActDefId, boolean allowEmptyTry) {
		List<BpmFormRights> rightList = null;
		if (StringUtil.isNotEmpty(nodeId)) {
			rightList = bpmFormRightsDao.getByActDefNodeId(actDefId, nodeId, parentActDefId);
			if (BeanUtils.isEmpty(rightList) && !allowEmptyTry) {
				return rightList;
			}
		}
		if (StringUtil.isNotEmpty(actDefId) && BeanUtils.isEmpty(rightList)) {
			rightList = bpmFormRightsDao.getByActDefId(actDefId, parentActDefId);
			if (BeanUtils.isEmpty(rightList) && !allowEmptyTry) {
				return rightList;
			}
		}
		if (StringUtil.isNotEmpty(formKey) && BeanUtils.isEmpty(rightList)) {
			rightList = bpmFormRightsDao.getByFormKey(formKey, false);

		}
		return rightList;
	}

	/**
	 * { "main": { "field1": "w", "field2": "r" }, 
	 * "table": { "table1": { "hidden": true }, 
	 * "table2": "{hidden:false,require: true,add: true,del: false}" },
	 *  "fields": { "table1": { "field1": "w", "field2": "r" }, 
	 *  "table2": { "field1": "w", "field2": "w" } } }
	 **/

	public JSONObject getByFormKeyAndUserId(String formKey, BpmFormTable formTable, String actDefId, String nodeId, String parentActDefId, boolean isReadOnly) {
		List<BpmFormRights> rightList = getFormRights(formKey, actDefId, nodeId, parentActDefId, true);

		if (BeanUtils.isEmpty(rightList)) {
			JSONObject permissionMap = getDefaultJson(formTable, isReadOnly);
			return permissionMap;
		}

		//主表
		JSONObject mainJson = new JSONObject();
		//表权限
		JSONObject tableJson = new JSONObject();
		//子表字段权限。
		JSONObject fieldJson = new JSONObject();

		String right = "w";

		CurrentUser currentUser = ServiceUtil.getCurrentUser();
		Map<String, List<Long>> relationMap = currentUserService.getUserRelation(currentUser, "formPermissionList");
		Long userId = currentUser.getUserId();

		boolean hasSubTable = false;

		for (BpmFormRights rights : rightList) {
			JSONObject permission = JSONObject.fromObject(rights.getPermission());
			int type = rights.getType();
			String title = permission.getString("title");

			right = calcRights(isReadOnly, right, relationMap, userId, permission);

			if (StringUtil.isEmpty(right)) {
				right = "n";
			}

			switch (type) {
			//主表
			case BpmFormRights.FieldRights:
				mainJson.accumulate(title, right);
				break;
			//子表字段
			case BpmFormRights.TableRights:
				String tableName = permission.getString("tableName");
				handSubTableFields(fieldJson, tableName, title, right);
				break;
			//表权限
			case BpmFormRights.TableShowRights:
				handTableRights(permission, tableJson, isReadOnly);
				hasSubTable = true;
				break;
			}
		}
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("main", mainJson);
		if (hasSubTable) {
			jsonObj.put("table", tableJson);
			jsonObj.put("fields", fieldJson);
		}

		return jsonObj;
	}

	/**
	 * 计算表单权限。
	 * 
	 * @param isReadOnly
	 * @param right
	 * @param relationMap
	 * @param userId
	 * @param permission
	 * @return
	 */
	private String calcRights(boolean isReadOnly, String right, Map<String, List<Long>> relationMap, Long userId, JSONObject permission) {
		if (BeanUtils.isNotIncZeroEmpty(userId)) {
			right = getRight(permission, relationMap, userId);
		}
		if (isReadOnly && (right.equals("w") || right.equals("b"))) {
			right = "r";
		}

		if (right.equals("r") && permission.containsKey("rpost")) {
			boolean rpost = permission.getBoolean("rpost");
			if (rpost)
				right = "rp";
		}
		return right;
	}

	/**
	 * 处理子表字段。
	 * 
	 * @param fieldJson
	 * @param tableName
	 * @param fieldName
	 * @param right
	 */
	private void handSubTableFields(JSONObject fieldJson, String tableName, String fieldName, String right) {
		if (fieldJson.containsKey(tableName)) {
			JSONObject jsonObject = fieldJson.getJSONObject(tableName);
			jsonObject.accumulate(fieldName, right);
		} else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(fieldName, right);
			fieldJson.accumulate(tableName, jsonObject);
		}

	}

	/**
	 * 获取子表是否显示权限。
	 * 
	 * @param permission
	 *            格式如下:{"title":"jsxx","memo":"家属信息","tableName":"jsxx","show":{"y":"false","b":"false","addRow":"true","del":"false"}};
	 * @param tableJson
	 *            表权限格式。
	 */
	private void handTableRights(JSONObject permission, JSONObject tableJson, boolean isReadonly) {

		JSONObject json = permission.getJSONObject("show");
		String tableName = permission.getString("title");
		String hidden = json.getString("y");
		String str = "";

		//隐藏
		if ("true".equals(hidden)) {
			str = "{hidden:true}";
		} else {
			String require = json.getString("b");
			String addRow = json.getString("addRow");
			String del = json.getString("del");

			String tmpRequire = "true".equals(require) ? "true" : "false";
			String tmpAdd = "true".equals(addRow) ? "true" : "false";
			String tmpDel = "true".equals(del) ? "true" : "false";

			if (isReadonly) {
				str = "{hidden:false,require:false,add:false,del:false}";
			} else {
				str = "{hidden:false,require:" + tmpRequire + ",add:" + tmpAdd + ",del:" + tmpDel + "}";
			}

		}
		tableJson.accumulate(tableName, str);
	}

	/**
	 * 获取默认的权限。
	 * 
	 * @param formTable
	 * @param isReadonly
	 * @return
	 */
	private JSONObject getDefaultJson(BpmFormTable formTable, boolean isReadonly) {

		String right = isReadonly ? "r" : "w";

		JSONObject jsonObj = new JSONObject();
		//子表
		JSONObject mainJson = new JSONObject();

		List<BpmFormField> formFields = formTable.getFieldList();
		for (BpmFormField field : formFields) {
			mainJson.accumulate(field.getFieldName(), right);
		}

		jsonObj.put("main", mainJson);

		List<BpmFormTable> tables = formTable.getSubTableList();

		if (BeanUtils.isNotEmpty(tables)) {
			//子表权限
			JSONObject tableJson = new JSONObject();

			for (BpmFormTable table : tables) {
				String tbJson = getTableJson(isReadonly);
				tableJson.accumulate(table.getTableName(), tbJson);
			}
			jsonObj.accumulate("table", tableJson);

			//子表字段权限
			JSONObject fieldsJson = new JSONObject();
			for (BpmFormTable table : tables) {
				List<BpmFormField> fields = table.getFieldList();
				JSONObject tbJson = new JSONObject();
				for (BpmFormField field : fields) {
					tbJson.accumulate(field.getFieldName(), right);
				}
				fieldsJson.accumulate(table.getTableName(), tbJson);
			}
			jsonObj.accumulate("fields", fieldsJson);
		}

		return jsonObj;
	}

	/**
	 * 获取表权限。
	 * 
	 * @param isReadOnly
	 * @return
	 */
	private String getTableJson(boolean isReadOnly) {
		String str = "";
		if (isReadOnly) {
			str = "{hidden:false,require:false,add:false,del:false}";
		} else {
			str = "{hidden:false,require:false,add:true,del:true}";
		}
		return str;
	}

	/**
	 * 取得节点的默认权限，当表单没有配置权限时，使用此方法获取。
	 * 
	 * @param formKey
	 * @param isReadonly
	 * @return
	 */
	private Map<String, Object> getDefaultPermissionByFormKey(BpmFormDef bpmFormDef, boolean isReadonly) {
		String right = isReadonly ? "r" : "w";

		Map<String, Object> permissions = new HashMap<String, Object>();

		Map<String, String> fieldPermission = new HashMap<String, String>();
		Map<String, String> opinionPermission = new HashMap<String, String>();
		Map<String, String> tablePermission = new HashMap<String, String>();
		Map<String, String> menuRightPermission = new HashMap<String, String>();
		Map<String, String> teamPermission = new HashMap<String,String>();
		JSONObject subTables = new JSONObject();
		JSONObject subTableShows = new JSONObject();

		BpmFormTable bpmFormTable = bpmFormDef.getBpmFormTable();
		//1.主表
		List<BpmFormField> formFields = bpmFormTable.getFieldList();
		for (BpmFormField field : formFields) {
			fieldPermission.put(field.getFieldName(), right);
		}
		//2.子表字段
		List<BpmFormTable> tables = bpmFormTable.getSubTableList();
		for (BpmFormTable formTable : tables) {
			List<BpmFormField> subFields = formTable.getFieldList();
			for (BpmFormField field : subFields) {
				String tableName = formTable.getTableName();
				String fieldName = field.getFieldName();
				String power = right;
				JSONObject subTable = subTables.getJSONObject(tableName);
				if (subTable.isNullObject()) {
					subTable = new JSONObject();
					subTable.accumulate(fieldName, power);
					subTables.accumulate(tableName, subTable);
				} else {
					subTable.accumulate(fieldName, power);
				}
			}
		}
		//3.意见
		Map<String, String> opinionMap = FormUtil.parseOpinion(bpmFormDef.getHtml());
		Set<Entry<String, String>> opinionSet = opinionMap.entrySet();
		for (Iterator<Entry<String, String>> opinionIt = opinionSet.iterator(); opinionIt.hasNext();) {
			Entry<String, String> tmp = opinionIt.next();
			opinionPermission.put(tmp.getKey(), right);
		}

		//4.子表显示
		for (BpmFormTable formTable : tables) {
			String memo = formTable.getTableDesc();

			String show = "{\"y\":\"false\",\"b\":\"false\",\"addRow\":\"true\",\"del\":\"false\"}}";
			if (isReadonly) {
				show = "{\"y\":\"false\",\"b\":\"false\",\"addRow\":\"false\",\"del\":\"false\"}}";
			}

			JSONObject subTableShow = JSONObject.fromObject(show);
			subTableShow.accumulate("memo", memo);
			subTableShows.accumulate(formTable.getTableName(), subTableShow);
		}

		//5分组默认权限
		List<JSONObject> teamPermissionJson = getTeamTableJson(bpmFormDef);
		for(JSONObject jsonObject : teamPermissionJson){
			teamPermission.put((String) jsonObject.get("title"), right);
		}
		permissions.put("field", fieldPermission);
		permissions.put("opinion", opinionPermission);
		permissions.put("table", tablePermission);
		permissions.put("subFieldJson", subTables);
		permissions.put("subTableShow", subTableShows);
		permissions.put("menuRight", menuRightPermission);
		permissions.put("teamPermission", teamPermission);
		return permissions;
	}

	/**
	 * 计算表单字段权限。
	 * 
	 * @param formDefId
	 * @param userId
	 * @return
	 * @throws DocumentException
	 */
	public Map<String, Object> getByFormKeyAndUserId(BpmFormDef formDef, String actDefId, String nodeId, String parentActDefId, boolean isReadOnly) {
		String formKey = formDef.getFormKey();
		List<BpmFormRights> rightList = getFormRights(formKey, actDefId, nodeId, parentActDefId, true);

		if (BeanUtils.isEmpty(rightList)) {
			Map<String, Object> permissionMap = getDefaultPermissionByFormKey(formDef, isReadOnly);
			//若节点取的是默认权限，给一个权限类型标志
			permissionMap.put("permissionType", "default");
			return permissionMap;
		}

		Map<String, Object> permissions = new HashMap<String, Object>();

		Map<String, String> fieldPermission = new HashMap<String, String>();
		Map<String, String> opinionPermission = new HashMap<String, String>();
		Map<String, String> tablePermission = new HashMap<String, String>();
		Map<String, String> menuRightPermission = new HashMap<String, String>();
		Map<String, String> formTabPermission = new HashMap<String, String>();
		Map<String, String> teamPermission = new HashMap<String, String>();
		JSONObject subTables = new JSONObject();
		JSONObject subTableShows = new JSONObject();

		String right = "w";

		CurrentUser currentUser = ServiceUtil.getCurrentUser();
		Map<String, List<Long>> relationMap = currentUserService.getUserRelation(currentUser, "formPermissionList");
		Long userId = currentUser.getUserId();

		for (BpmFormRights rights : rightList) {
			JSONObject permission = JSONObject.fromObject(rights.getPermission());
			int type = rights.getType();
			String title = permission.getString("title");

			right = calcRights(isReadOnly, right, relationMap, userId, permission);

			if (permission.containsKey("menuRight")) {
				JSONObject menuRight = permission.getJSONObject("menuRight");
				menuRightPermission.put(title, menuRight.toString());
			}
			switch (type) {
			//主表
			case BpmFormRights.FieldRights:
				fieldPermission.put(title, right);
				break;
			//子表字段
			case BpmFormRights.TableRights:
				String tableName = permission.getString("tableName");
				String fieldName = title;
				String power = right;
				JSONObject subTable = subTables.getJSONObject(tableName);
				if (subTable.isNullObject()) {
					subTable = new JSONObject();
					subTable.accumulate(fieldName, power);
					subTables.accumulate(tableName, subTable);
				} else {
					subTable.accumulate(fieldName, power);
				}

				break;
			//意见字段
			case BpmFormRights.OpinionRights:
				if(permission.getBoolean("showOpinion")){
					right+="_show";
				}else{
					right+="_hide";
				}
				opinionPermission.put(title, right);
				break;
			//表单子标签
			case BpmFormRights.FormTabShowRights:
				formTabPermission.put(title, right);
				break;
			case BpmFormRights.TeamTableShowRights:
				teamPermission.put(title, right);
				break;
			default:
				String memo = permission.getString("memo");
				String show = permission.getString("show");

				JSONObject subTableShow = JSONObject.fromObject(show);
				if(isReadOnly){//只读的话这两个应该设为false
					subTableShow.put("addRow", "false");
					subTableShow.put("del", "false");
				}
				subTableShow.accumulate("memo", memo);
				subTableShows.accumulate(title, subTableShow);
				break;
			}
		}

		permissions.put("field", fieldPermission);
		permissions.put("opinion", opinionPermission);
		permissions.put("table", tablePermission);
		permissions.put("subFieldJson", subTables);
		permissions.put("subTableShow", subTableShows);
		permissions.put("menuRight", menuRightPermission);
		permissions.put("formTabPermission", formTabPermission);
		permissions.put("teamPermission", teamPermission);
		return permissions;
	}

	/**
	 * 获取权限。
	 * 
	 * @param jsonObject
	 *            数据格式为：{ 'title':'orderId','memo':'订单ID','read':{'type':'everyone','id':'', 'fullname':''},'write':{'type':'everyone','id':'','fullname':''}}
	 * @param relationMap
	 *            用户对应的的关系。
	 * @param userId
	 *            用户ID
	 * @return
	 */
	private String getRight(JSONObject jsonObject, Map<String, List<Long>> relationMap, Long userId) {
		String right = "";
		if (hasRight(jsonObject, relationMap, "required", userId)) {
			right = "b";
		} else if (hasRight(jsonObject, relationMap, "write", userId)) {
			right = "w";
		} else if (hasRight(jsonObject, relationMap, "read", userId)) {
			right = "r";
		}
		return right;
	}

	/**
	 * 判断是否有权限。
	 * 
	 * @param permission
	 *            权限JSONOjbect
	 * @param mode
	 *            读或写 (write,read,required)
	 * @param userId
	 *            用户ID
	 * @return
	 */
	private boolean hasRight(JSONObject permission, Map<String, List<Long>> relationMap, String mode, Long userId) {
		JSONObject node = permission.getJSONObject(mode);
		if (JSONUtils.isNull(node))
			return false;
		if (BeanUtils.isEmpty(node.get("type")))
			return false;
		String type = node.get("type").toString();
		String id = node.get("id").toString();
		if ("none".equals(type)) {
			return false;
		}
		if ("everyone".equals(type)) {
			return true;
		}
		if ("script".equals(type)) {
			String script = node.getString("fullname");
			//脚本返回人员ID
			Set<String> users = (Set<String>) groovyScriptEngine.executeObject(script, null);
			boolean rtn = users.contains(userId.toString());
			return rtn;
		}
		if (relationMap.containsKey(type)) {
			List<Long> list = relationMap.get(type);
			boolean rtn = contain(id, list);
			return rtn;
		}
		return false;

	}

	private boolean contain(String settings, List<Long> list) {
		if (StringUtil.isEmpty(settings))
			return false;
		String[] aryId = settings.split(",");
		for (String id : aryId) {
			boolean rtn = isContain(id, list);
			if (rtn)
				return true;
		}
		return false;
	}

	private boolean isContain(String id, List<Long> list) {
		for (Long tmp : list) {
			if (id.equals(tmp.toString()))
				return true;
		}
		return false;
	}

	/**
	 * 数据转换。
	 * 
	 * @param formRightsList
	 * @return
	 */
	private Map<String, BpmFormRights> convertToMap(List<BpmFormRights> formRightsList) {
		Map<String, BpmFormRights> map = new HashMap<String, BpmFormRights>();
		for (BpmFormRights rights : formRightsList) {
			map.put(rights.getName(), rights);
		}
		return map;
	}

	/**
	 * 将子表数据转换为map。
	 * 
	 * @param formRightsList
	 * @return
	 */
	private Map<String, Map<String, BpmFormRights>> convertSubFieldRights(List<BpmFormRights> formRightsList) {
		Map<String, Map<String, BpmFormRights>> map = new HashMap<String, Map<String, BpmFormRights>>();
		for (BpmFormRights rights : formRightsList) {
			JSONObject permission = JSONObject.fromObject(rights.getPermission());
			String tableName = permission.getString("tableName");
			String title = permission.getString("title");
			if (map.containsKey(tableName)) {
				Map<String, BpmFormRights> fieldMap = map.get(tableName);
				fieldMap.put(title, rights);
			} else {
				Map<String, BpmFormRights> fieldMap = new HashMap<String, BpmFormRights>();
				fieldMap.put(title, rights);
				map.put(tableName, fieldMap);
			}
		}
		return map;
	}

	/**
	 * 获取通过表单设定定义表单的权限数据。
	 * 
	 * <pre>
	 * 通过设计定义的表单，
	 * 如果有设置过权限，那么权限信息可以通过设置过权限与解析表单匹配获取。
	 * </pre>
	 * 
	 * @param rightList
	 * @param formKey
	 * @return
	 */
	private Map<String, List<JSONObject>> getPermissionJson(List<BpmFormRights> rightList, BpmFormDef bpmFormDef) {
		Map<String, List<JSONObject>> map = new HashMap<String, List<JSONObject>>();
		if (BeanUtils.isEmpty(bpmFormDef)) {
			return map;
		}
		List<BpmFormRights> formRightsList = new ArrayList<BpmFormRights>();
		List<BpmFormRights> sbFieldRightsList = new ArrayList<BpmFormRights>();
		List<BpmFormRights> opinionRightsList = new ArrayList<BpmFormRights>();
		List<BpmFormRights> tableShowRightsList = new ArrayList<BpmFormRights>(); //子表显示与否
		List<BpmFormRights> formTabRightsList = new ArrayList<BpmFormRights>(); //表单子标签显示权限
		List<BpmFormRights> teamTableRightsList = new ArrayList<BpmFormRights>();
		for (BpmFormRights bpmFormRights : rightList) {
			if (bpmFormRights.getType() == BpmFormRights.FieldRights) {
				formRightsList.add(bpmFormRights);
			} else if (bpmFormRights.getType() == BpmFormRights.TableRights) {
				sbFieldRightsList.add(bpmFormRights);
			} else if (bpmFormRights.getType() == BpmFormRights.TableShowRights) {
				tableShowRightsList.add(bpmFormRights);
			} else if(bpmFormRights.getType() == BpmFormRights.FormTabShowRights){
				formTabRightsList.add(bpmFormRights);
			}else if(bpmFormRights.getType()== BpmFormRights.TeamTableShowRights){
				teamTableRightsList.add(bpmFormRights);
			}else{
				opinionRightsList.add(bpmFormRights);
			}
		}
		BpmFormTable bpmFormTable = bpmFormTableService.getTableById(bpmFormDef.getTableId());
		if (BeanUtils.isEmpty(bpmFormTable)) {
			return map;
		}
		//获取表单上有展示的字段
		Map<String,Set<String>> formViewFiledMap =  FormUtil.getBpmFormViewField(bpmFormDef,bpmFormTable);
		
		//1.主表字段权限
		List<JSONObject> fieldJsonList = getMainFieldJsonList(formRightsList, bpmFormTable,formViewFiledMap);
		map.put("field", fieldJsonList);

		//2.子表字段权限
		List<BpmFormTable> tableList = bpmFormTable.getSubTableList();
		List<JSONObject> tableJsonList = getTableJsonList(sbFieldRightsList, tableList,formViewFiledMap);
		map.put("table", tableJsonList);

		//3.处理其它  意见权限。
		String html = bpmFormDef.getHtml(); // 获取模版。
		List<JSONObject> opinionJsonList = getOpinionJsonList(opinionRightsList, html);
		map.put("opinion", opinionJsonList);

		//4.子表显示与否
		List<JSONObject> tableShowJsonList = getTableShowJonList(tableShowRightsList, tableList,formViewFiledMap);
		map.put("tableShow", tableShowJsonList);
		
		//5.表单子标签权限
		List<JSONObject> formTabJsonList = getFormTabJonList(formTabRightsList, bpmFormDef);
		map.put("formTab", formTabJsonList);
		
		//6.表单字段分组权限
		List<JSONObject> teamTableJsonList=getTeamJsonList(teamTableRightsList,bpmFormDef);
		map.put("teamTable", teamTableJsonList);
		return map;
	}

	/**
	 * 
	 */
	private List<JSONObject> getTeamJsonList(List<BpmFormRights> teamTableRightsList,BpmFormDef bpmFormDef){
		List<JSONObject> teamJsonList = new ArrayList<JSONObject>();
		Map<String, BpmFormRights> mainMaps = convertToMap(teamTableRightsList);
		BpmFormTable bpmFormTable = bpmFormTableService.getById(bpmFormDef.getTableId());
		String teamJsonStr = bpmFormTable.getTeam();
		if(StringUtils.isBlank(teamJsonStr)){
			return teamJsonList;
		}
		JSONObject jsonObject = JSONObject.fromObject(teamJsonStr);
		JSONArray array = jsonObject.getJSONArray("team");
		for(Object object : array){
			JSONObject team = JSONObject.fromObject(object);
			JSONObject permission = new JSONObject();
			if(mainMaps.containsKey(team.get("teamTitle"))){
				BpmFormRights bpmFormRights = mainMaps.get(team.get("teamTitle"));
				permission = JSONObject.fromObject(bpmFormRights.getPermission());
			}else{
				permission = getTeamTablePermission(team);
			}
			permission.put("tableName", bpmFormDef.getSubject());
			teamJsonList.add(permission);
		}
		return teamJsonList;
	}
	
	
	/**
	 * 获取表单子标签的权限
	 * @param formTabRightsList
	 * @param bpmFormDef
	 * @return
	 */
	private List<JSONObject> getFormTabJonList(List<BpmFormRights> formTabRightsList, BpmFormDef bpmFormDef) {
		String formDefTab = bpmFormDef.getTabTitle();
		List<JSONObject> formTabJsonList = new ArrayList<JSONObject>();
		String [] formDefTabs = formDefTab.split(BpmFormDef.PageSplitor);
		Map<String, BpmFormRights> mainMaps = convertToMap(formTabRightsList);
		for(String tabTitle : formDefTabs){
			JSONObject permission = new JSONObject();
			tabTitle = tabTitle;
			if(mainMaps.containsKey(tabTitle)){
				BpmFormRights bpmFormRights = mainMaps.get(tabTitle);
				permission = JSONObject.fromObject(bpmFormRights.getPermission());
			}else{
				permission = getPermissionJson(tabTitle, "表单标签权限", 5);
			}
			permission.put("tableName", bpmFormDef.getSubject());
			formTabJsonList.add(permission);
		}
		
		return formTabJsonList;
	}

	/**
	 * 获取子表权限json数据。
	 * 
	 * @param tableShowRightsList
	 * @param tableList
	 * @return
	 */
	private List<JSONObject> getTableShowJonList(List<BpmFormRights> tableShowRightsList, List<BpmFormTable> tableList,Map<String,Set<String>> formViewFiledMap) {
		Map<String, BpmFormRights> tableShowMaps = convertToMap(tableShowRightsList);
		List<JSONObject> tableShowJsonList = new ArrayList<JSONObject>();
		Set<String> subTableSet = formViewFiledMap.get("subTable");
		for (BpmFormTable table : tableList) {
			if(!subTableSet.contains(table.getTableName())){
				continue;
			}
			if (tableShowMaps.containsKey(table.getTableName())) {
				BpmFormRights bpmFormRights = tableShowMaps.get(table.getTableName());
				JSONObject permission = JSONObject.fromObject(bpmFormRights.getPermission());
				tableShowJsonList.add(permission);
			} else {
				JSONObject permission = getPermissionJson(table.getTableName(), table.getTableDesc(), 4);
				tableShowJsonList.add(permission);
			}
		}
		return tableShowJsonList;
	}

	/**
	 * 获取主表json权限字段。
	 * 
	 * @param formRightsList
	 * @param bpmFormTable
	 * @return
	 */
	private List<JSONObject> getMainFieldJsonList(List<BpmFormRights> formRightsList, BpmFormTable bpmFormTable,Map<String,Set<String>> formViewFiledMap) {
		List<BpmFormField> fieldList = bpmFormTable.getFieldList();

		List<JSONObject> fieldJsonList = new ArrayList<JSONObject>();
		Map<String, BpmFormRights> mainMaps = convertToMap(formRightsList);
		
		Set<String> mainSet = formViewFiledMap.get(bpmFormTable.getTableName());
		
		for (BpmFormField bpmFormField : fieldList) {
			String name = bpmFormField.getFieldName();
			if(!mainSet.contains(name)){
				continue;
			}
			if (mainMaps.containsKey(name)) {
				BpmFormRights bpmFormRights = mainMaps.get(name);
				JSONObject permission = JSONObject.fromObject(bpmFormRights.getPermission());
				permission.put("controlType", bpmFormField.getControlType());
				permission.put("tableName", bpmFormTable.getTableName());
				fieldJsonList.add(permission);
			} else {
				JSONObject permission = getPermissionJson(bpmFormField.getFieldName(), bpmFormField.getFieldDesc(), 1);
				permission.put("tableName", bpmFormTable.getTableName());
				permission.put("controlType", bpmFormField.getControlType());
				fieldJsonList.add(permission);
			}
		}
		return fieldJsonList;
	}

	/**
	 * 获取子表JSON数据。
	 * 
	 * @param sbFieldRightsList
	 * @param tableList
	 * @return
	 */
	private List<JSONObject> getTableJsonList(List<BpmFormRights> sbFieldRightsList, List<BpmFormTable> tableList,Map<String,Set<String>> formViewFiledMap) {
		Map<String, Map<String, BpmFormRights>> subFieldsMap = convertSubFieldRights(sbFieldRightsList);
		List<JSONObject> tableJsonList = new ArrayList<JSONObject>();
		for (BpmFormTable table : tableList) {
			// 子表整个表的权限
			JSONObject permission = getPermissionJson(table.getTableName(), table.getTableDesc(), 2);
			//每个子表中的每个字段
			List<BpmFormField> subFieldList = table.getFieldList();
			List<JSONObject> subFieldJsonList = new ArrayList<JSONObject>();

			String tableName = table.getTableName();
			Set<String> subSet  = formViewFiledMap.get(tableName);
			Map<String, BpmFormRights> fieldsMap = subFieldsMap.get(tableName);

			for (BpmFormField field : subFieldList) {
				String fieldName = field.getFieldName();
				if(!subSet.contains(fieldName)){
					continue;
				}
				if (fieldsMap != null && fieldsMap.containsKey(fieldName)) {
					BpmFormRights formRights = fieldsMap.get(fieldName);
					JSONObject subPermission = JSONObject.fromObject(formRights.getPermission());
					subPermission.put("controlType", field.getControlType());
					subPermission.put("tableName", table.getTableName());
					subFieldJsonList.add(subPermission);
				} else {
					JSONObject subPermission = getPermissionJson(field.getFieldName(), field.getFieldDesc(), 1);
					subPermission.put("controlType", field.getControlType());
					subPermission.put("tableName", table.getTableName());
					subFieldJsonList.add(subPermission);
				}
			}
			//子表整个表的权限是重新生成的，所以不用判断
			if(!subFieldJsonList.isEmpty()){
				permission.put("tableName", table.getTableName());
				permission.put("subField", subFieldJsonList);
				tableJsonList.add(permission);
			}
		}
		return tableJsonList;
	}

	/**
	 * 获取意见列表JSON。
	 * 
	 * @param formRightsList
	 * @param html
	 * @return
	 */
	private List<JSONObject> getOpinionJsonList(List<BpmFormRights> formRightsList, String html) {
		Map<String, BpmFormRights> opinionMaps = convertToMap(formRightsList);
		List<JSONObject> opinionJsonList = new ArrayList<JSONObject>();
		Map<String, String> opinionMap = FormUtil.parseOpinion(html);
		Set<Entry<String, String>> set = opinionMap.entrySet();
		for (Iterator<Entry<String, String>> it = set.iterator(); it.hasNext();) {
			Entry<String, String> tmp = it.next();
			String key = tmp.getKey();
			if (opinionMap.containsKey(key)) {
				BpmFormRights bpmFormRights = opinionMaps.get(key);
				JSONObject opinionPermission = new JSONObject();
				if(BeanUtils.isNotEmpty(bpmFormRights)){
					opinionPermission = JSONObject.fromObject(bpmFormRights.getPermission());
				}
				opinionJsonList.add(opinionPermission);
			} else {
				JSONObject permission = getPermissionJson(tmp.getKey(), tmp.getValue(), 3);
				opinionJsonList.add(permission);
			}
		}
		return opinionJsonList;
	}

	/**
	 * 根据formkey删除表单权限
	 * 
	 * @param cascade
	 *            是同时否删除表单人流程的流程节点表单权限设置
	 * @param formKey
	 */
	public void delByFormKey(String formKey, boolean cascade) {
		bpmFormRightsDao.delByFormKey(formKey, cascade);
	}

	public List<JSONObject> getTeamTableJson(BpmFormDef bpmFormDef){
		List<JSONObject> teamTableJsonList = new ArrayList<JSONObject>();
		BpmFormTable bpmFormTable = bpmFormDef.getBpmFormTable();
		String 	teams = bpmFormTable.getTeam();
		if(StringUtil.isNotEmpty(teams)){
			JSONObject teamJson = JSONObject.fromObject(teams);
			if(BeanUtils.isNotEmpty(teamJson)){
				JSONArray jsonArray = teamJson.getJSONArray("team");
				Iterator<Object> it = jsonArray.iterator();
				while (it.hasNext()) {
					JSONObject team = getTeamTablePermission((JSONObject) it.next());
					teamTableJsonList.add(team);
				}
			}
		}
		return teamTableJsonList;
	}
	
}

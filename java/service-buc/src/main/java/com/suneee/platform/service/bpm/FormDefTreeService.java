package com.suneee.platform.service.bpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.service.BaseService;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.bpm.FormDefTreeDao;
import com.suneee.platform.model.bpm.FormDefTree;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormTable;
import net.sf.json.JSONObject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.service.BaseService;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.bpm.FormDefTreeDao;
import com.suneee.platform.model.bpm.FormDefTree;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormFieldService;
import com.suneee.platform.service.form.BpmFormTableService;

/**
 * <pre>
 * 对象功能:form_def_tree Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-05-12 14:46:20
 * </pre>
 */
@Service
public class FormDefTreeService extends BaseService<FormDefTree> {
	@Resource
	private FormDefTreeDao dao;
	@Resource
	private BpmFormFieldService bpmFormFieldService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmFormDefService bpmFormDefService;

	public FormDefTreeService() {
	}

	@Override
	protected IEntityDao<FormDefTree, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 form_def_tree 信息
	 * 
	 * @param formDefTree
	 */
	public void save(FormDefTree formDefTree) {
		Long id = formDefTree.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			formDefTree.setId(id);
			this.add(formDefTree);
		} else {
			this.update(formDefTree);
		}
	}

	/**
	 * 根据formKey获取树形表单配置。
	 * @param formKey
	 * @return
	 */
	public FormDefTree getByFormKey(String formKey) {
		return  dao.getByFormKey(formKey);
	}

	
	/**
	 * 获取JSON数据。
	 * @param formKey
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<JSONObject> treeListJson(String formKey, Map<String, Object> params) throws Exception {
		FormDefTree formDefTree = getByFormKey(formKey);
		BpmFormDef bpmFormDef = bpmFormDefService.getDefaultPublishedByFormKey(formKey);
		BpmFormTable bpmFormTable = bpmFormTableService.getTableById(bpmFormDef.getTableId());
				
		String sql=getSql(formDefTree, bpmFormTable, params);

		// 获取jdbcTemplate
		JdbcTemplate jdbcTemplate = JdbcTemplateUtil.getNewJdbcTemplate(bpmFormTable.getDsAlias());
		List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
		
		List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
		for (Map<String, Object> map : mapList) {
			JSONObject jo=getJsonByMap(map);
			jsonObjectList.add(jo);
		}
		return jsonObjectList;
	}
	
	private JSONObject getJsonByMap(Map<String, Object> map){
		JSONObject jo = new JSONObject();
		for (String key : map.keySet()) {
			Object val=map.get(key) == null ? "" : map.get(key);
			//下面换大小写是为了处理oracle没大小写之分的状况
			if (key.equals("PARENTID")) {
				jo.put("parentId", val);
				continue;
			}
			if(key.equals("ID")){
				jo.put("id", val);
				continue;
			}
			if(key.equals("NAME")){
				jo.put("name", val);
				continue;
			}
			if(key.equals("ISPARENT")){
				jo.put("isParent", val);
				continue;
			}
			jo.put(key, val);
		}
		return jo;
		
	}
	
	/**
	 * 获取SQL语句。
	 * @param formDefTree
	 * @param bpmFormTable
	 * @param params
	 * @return
	 */
	private String getSql(FormDefTree formDefTree,BpmFormTable bpmFormTable,Map<String, Object> params){
		String treeIdField = getFieldNameByFieldId(formDefTree.getTreeId(), bpmFormTable.isExtTable());
		String parentIdField = getFieldNameByFieldId(formDefTree.getParentId(), bpmFormTable.isExtTable());
		String displayField = getFieldNameByFieldId(formDefTree.getDisplayField(), bpmFormTable.isExtTable());
		StringBuffer sb = new StringBuffer();

		sb.append("select " + treeIdField + " id ");
		sb.append("," + parentIdField + " parentId ");
		sb.append("," + displayField + " name ");

		// 异步加载，需要返回isParent的值
		if (formDefTree.getLoadType().equals(FormDefTree.LOADTYPE_ASYNC)) {
			StringBuffer isParentSql = new StringBuffer();
			isParentSql.append("( case (select count(*)  from " + bpmFormTable.getDbTableName() + " p ");
			isParentSql.append("where p." + parentIdField + " = o." + treeIdField + " and p." + treeIdField + " != p." + parentIdField + ")");
			isParentSql.append("when 0 then 'false' else 'true' end )isParent ");
			sb.append("," + isParentSql);
		}

		sb.append("from " + bpmFormTable.getDbTableName() + " o ");

		// 异步加载，根据id查询
		if (formDefTree.getLoadType().equals(FormDefTree.LOADTYPE_ASYNC)) {
			String id = params.get("id").toString();
			if (StringUtil.isNotEmpty(id)) {
				sb.append("where " + parentIdField + " = " + id);
			} else {
				sb.append("where " + parentIdField + " = " + formDefTree.getRootId());
			}
		}
		return sb.toString();
	}

	/**
	 * 这个是专门为formDefTree服务的 当fid=0的时候，就当选择的是表的主键 因为内部表的fieldList是不包含其主键"ID"的
	 * 
	 * @param fid
	 *            ：字段ID
	 * @param isExtTable
	 *            :是否外部表
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	private String getFieldNameByFieldId(String fid, boolean isExtTable) {
		if (fid.toString().equals(TableModel.PK_COLUMN_NAME)) {
			return TableModel.PK_COLUMN_NAME;
		} else {
			if(isExtTable){
				return fid;
			}
			return TableModel.CUSTOMER_COLUMN_PREFIX + fid;
		}
	}
}

/**
 * 对象功能:自定义表单数据处理
 * 开发公司:广州宏天软件有限公司
 * 开发人员:xwy
 * 创建时间:2011-12-22 11:07:56
 */
package com.suneee.platform.dao.form;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.model.FieldPool;
import com.suneee.core.table.ITableOperator;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.platform.dao.bpm.BpmSubtableRightsDao;
import com.suneee.platform.dao.bpm.ProcessRunDao;
import com.suneee.platform.model.bpm.BpmSubtableRights;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.form.*;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.bpm.BpmBusLinkService;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.form.FormDataUtil;
import com.suneee.platform.service.form.SubTableSortService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

@Repository
public class BpmFormHandlerDao {

	private Log logger = LogFactory.getLog(BpmFormHandlerDao.class);

	@Resource
	private BpmSubtableRightsDao bpmSubtableRightsDao;
	@Resource
	private BpmBusLinkService bpmBusLinkService;
	@Resource
	private ITableOperator tableOperator;
	@Resource
	private ProcessRunDao processRunDao;
	@Resource
	private SubTableSortService subTableSortService;
	@Resource(name="jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	/*参数要填太多。我也很无奈。这样吧就。 */
	// 排除不需要format的字段类型。
	private static ThreadLocal<Set<Class>>  excludeFormatObjects = new ThreadLocal<Set<Class>>();
	public BpmFormData getBussnessDatagetByKey(BpmFormTable mainFormTableDef, String pkValue, String actDefId, String nodeId,boolean isHandleData,Set<Class> excludeFormat) {
		try {
			excludeFormatObjects.set(excludeFormat);
			return getByKey(mainFormTableDef, pkValue, actDefId, nodeId,isHandleData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			excludeFormatObjects.remove();
		}
	}
	
	/**
	 * 处理动态表单数据
	 * 
	 * @param bpmFormData
	 * @throws Exception
	 */
	public void  handFormData(BpmFormData bpmFormData,ProcessRun processRun,String nodeId) throws Exception {
		JdbcTemplate jdbcTemplate =null;
		BpmFormTable bpmFormTable=bpmFormData.getBpmFormTable();
		
		if(bpmFormTable!=null){
			jdbcTemplate= JdbcTemplateUtil.getNewJdbcTemplate(bpmFormTable.getDsAlias());
		}
		String actDefId="";
		if(processRun!=null){
			actDefId=processRun.getActDefId();
		}
		
		List<SqlModel> list = FormDataUtil.parseSql(bpmFormData,actDefId,nodeId);
		for (SqlModel sqlModel : list) {
			String sql = sqlModel.getSql();
			if (StringUtil.isEmpty(sql)) continue;
			Object[] obs = sqlModel.getValues();
			jdbcTemplate.update(sql, obs);
		}
		//处理关联数据。
		handlerBusLinkData(list,processRun);
	}
	
	/**
	 * 处理关联数据。
	 * @param list
	 * @param processRun
	 */
	private void handlerBusLinkData(List<SqlModel> list,ProcessRun processRun){
		for (SqlModel sqlModel : list) {
			String sql = sqlModel.getSql();
			if (StringUtil.isEmpty(sql) ) continue; 
			switch(sqlModel.getSqlType()){
				case SqlModel.SQLTYPE_INSERT:
					bpmBusLinkService.add(sqlModel.getPk(),processRun,sqlModel.getBpmFormTable() );
					break;
				case SqlModel.SQLTYPE_UPDATE:
					bpmBusLinkService.updBusLink(sqlModel.getPk(),processRun, sqlModel.getBpmFormTable());
					break;
				case SqlModel.SQLTYPE_DEL:
					bpmBusLinkService.delBusLink(sqlModel.getPk(), sqlModel.getBpmFormTable());
					break;
			}
		}
	}
	
	/**
	 * 保存表单数据
	 * @param bpmFormData
	 * @throws Exception
	 */
	public void handFormData(BpmFormData bpmFormData, ProcessRun processRun) throws Exception {
		handFormData( bpmFormData,processRun, "");
	}
	
	public void handFormData(BpmFormData bpmFormData) throws Exception {
		handFormData( bpmFormData,null, "");
	}

	/**
	 * 判断指定的表是否有数据。
	 * 
	 * @param tableName
	 *            数据库表名
	 */
	public boolean getHasData(String tableName) {
		JdbcTemplate jdbcTemplate = (JdbcTemplate) AppUtil.getBean("jdbcTemplate");
		int rtn = jdbcTemplate.queryForObject("select count(*) from " + tableName,Integer.class);
		return rtn > 0;
	}
	
	/**
	 * 检查表是否存在，可以是该用户空间下的所有表
	 * @Methodname: tableExists
	 * @param tableName
	 * @return
	 *
	 */
	public boolean tableExists(String tableName){
		return tableOperator.isTableExist(tableName);
	}

	/**
	 * 根据主键查询列表数据。
	 * 
	 * @param tableId
	 * @param pkValue
	 * @return
	 * @throws Exception
	 */
	public BpmFormData getByKey(BpmFormTable formTable, String pkValue,boolean isHandleData) throws Exception {
		return getByKey(formTable, pkValue, null, null,isHandleData);
	}
	
	
	
	/**
	 * 根据流程定义id，节点id和表ID获取子表显示条件的SQL片段。
	 * @param tableId
	 * @param actDefId
	 * @param nodeId
	 * @return
	 */
	private String getLimitSql(Long tableId, String actDefId,String nodeId, String pkValue){
		if(StringUtil.isEmpty(actDefId) || StringUtil.isEmpty(nodeId)) return "";
		String limitSql = "";
		
		ProcessRun processRun = processRunDao.getByBusinessKeyAndActDefId(pkValue, actDefId);
		String parentActDefId = processRunDao.getParentProcessRunActDefId(processRun);
		BpmSubtableRights rights = null;
		if(StringUtil.isNotEmpty(parentActDefId)){
			rights = bpmSubtableRightsDao.getByDefIdAndNodeId(actDefId, nodeId, tableId,parentActDefId);
		}
		if(rights==null){
			rights = bpmSubtableRightsDao.getByDefIdAndNodeId(actDefId, nodeId, tableId,"");
		}
		
		if(rights==null) return "";
		int permissionType=rights.getPermissiontype().intValue();
		// 处理权限,生成sql约束片段.
		switch (permissionType) {
			case 0:// 简单配置:判断用户
				Long userId = ContextUtil.getCurrentUserId();
				limitSql = " and b.BUS_CREATOR_ID   = " + userId;
				break;
			case 1:// 简单配置:判断组织
				SysOrg org = (SysOrg) ContextUtil.getCurrentOrg();
				if (org == null) {
					limitSql = " and 1 = 2";// 强制查询不到
				} else {
					limitSql = " and b.BUS_ORG_ID = " + org.getOrgId();
				}
				break;
			case 2:// 脚本
				GroovyScriptEngine scriptEngine = (GroovyScriptEngine) AppUtil.getBean(GroovyScriptEngine.class);
				limitSql = scriptEngine.executeString(rights.getPermissionseting(), new HashMap<String, Object>());// 不需流程变量入参
				break;
			default:
				break;
		}
		return limitSql;
		
	}

	/**
	 * 根据表ID，当前流程定义ID,流程节点ID查询业务数据。
	 * @param tableId		表ID
	 * @param pkValue		主键
	 * @param actDefId		流程定义ID
	 * @param nodeId		节点ID
	 * @return
	 * @throws Exception
	 */
		// 获取jdbctemplate对象。
	public BpmFormData getByKey(BpmFormTable mainFormTableDef, String pkValue, String actDefId, String nodeId,boolean isHandleData) throws Exception {
		JdbcTemplate jdbcTemplate=JdbcTemplateUtil.getNewJdbcTemplate(mainFormTableDef.getDsAlias());
		
		String tableName = mainFormTableDef.getFactTableName();
		String pkField = mainFormTableDef.getPkField();
	
		PkValue pk = new PkValue(pkField, pkValue);
		// 取得主表的数据
		Map<String, Object> mainData = getByKey(jdbcTemplate,  pk, mainFormTableDef,isHandleData);

		// 取子表的数据
		BpmFormData bpmFormData = new BpmFormData(mainFormTableDef);
		List<BpmFormTable> tableList=mainFormTableDef.getSubTableList();
		for (BpmFormTable table : tableList) {
			SubTable subTable = new SubTable();
			//TableRelation tableRelation= table.getTableRelation();			
			String fk=table.getRelation();
			String subPk =table.getPkField();		
			List<Map<String, Object>> list = getByFk(table, pk.getValue().toString(), actDefId,nodeId,isHandleData);
			subTable.setTableName(table.getTableName());
			subTable.setFkName(fk);
			subTable.setPkName(subPk);
			subTable.setDataList(list);
			
			bpmFormData.addSubTable(subTable);
		}

		bpmFormData.setTableId(mainFormTableDef.getTableId());
		bpmFormData.setTableName(tableName);
		bpmFormData.setPkValue(pk);
		bpmFormData.addMainFields(mainData);

		return bpmFormData;
	}

	/**
	 * 根据主键查询一条数据。
	 * 
	 * @param tableName
	 *            需要查询的表名
	 * @param pk
	 *            主键
	 * @param tableId
	 *            表ID
	 * @return
	 */
	public Map<String, Object> getByKey(JdbcTemplate jdbcTemplate,  PkValue pk, BpmFormTable bpmFormTable ,boolean isHandleData) {
		String sql = "select a.* from " + bpmFormTable.getFactTableName() + " a where " + pk.getName() + "='" +pk.getValue()+"'";
		Map<String, Object> map = null;
		try {
			map = jdbcTemplate.queryForMap(sql);
			if(isHandleData){
				List<BpmFormField> fieldList = bpmFormTable.getFieldList();
				Map<String, BpmFormField> fieldMap = convertToMap(fieldList);
				map = handMap(bpmFormTable, fieldMap, map);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			map = new HashMap<String, Object>();
		}
		return map;
	}

	/**
	 * 根据表名和主键获取一行数据。
	 * @param tableName
	 * @param pk
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> getByKey(String dsName, String tableName, String pkName, String pk) throws Exception {
		JdbcTemplate jdbcTemplate = JdbcTemplateUtil.getNewJdbcTemplate(dsName);
		String sql = "select a.* from " + tableName + " a where "+pkName+"="  + pk;
		Map<String, Object> map = null;
		try {
			map = jdbcTemplate.queryForMap(sql);
		} catch (Exception ex) {
			map = new HashMap<String, Object>();
		}
		return map;
	}
	
	/**
	 * 根据表对象 主键 字段获取数据。
	 * @param bpmFormTable
	 * @param pk
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getByKey(BpmFormTable bpmFormTable, String pk,String... fields) throws Exception {
		String field=" * ";
		if(BeanUtils.isNotEmpty(fields) )
			field=StringUtils.join(fields, ",");
		
		String tableName=bpmFormTable.getFactTableName();
		String pkName= bpmFormTable.getPkField();
		JdbcTemplate jdbcTemplate=JdbcTemplateUtil.getNewJdbcTemplate(bpmFormTable.getDsAlias());
		String sql = "select "+field+" from " + tableName + "  where "+pkName+"="  + pk;
		
		Map<String, Object> map = null;
		try {
			map = jdbcTemplate.queryForMap(sql);
		} catch (Exception ex) {
			map = new HashMap<String, Object>();
		}
		return map;
	}
	
	

	/**
	 * 将list转换为map对象。
	 * 
	 * @param fieldList
	 * @return
	 */
	private Map<String, BpmFormField> convertToMap(List<BpmFormField> fieldList) {
		Map<String, BpmFormField> map = new HashMap<String, BpmFormField>();
		for (BpmFormField field : fieldList) {
			String fieldName = field.getFieldName();
			map.put(fieldName, field);
		}
		return map;
	}

	/**
	 * 根据外键查询列表数据。
	 * 
	 * @param tableName
	 * @param fkValue
	 * @param tableId
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> getByFk(BpmFormTable table, String fkValue,boolean isHandleData) throws Exception {
		return getByFk(table, fkValue, null, null,isHandleData);
	}

	/**
	 * 根据外键查询列表数据。
	 * @param table
	 * @param fkValue
	 * @param actDefId
	 * @param nodeId
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> getByFk(BpmFormTable table, String fkValue, String actDefId,String nodeId,boolean isHandleData) throws Exception {
	
		List<BpmFormField> fieldList=table.getFieldList();
		
		JdbcTemplate jdbcTemplate=JdbcTemplateUtil.getNewJdbcTemplate(table.getDsAlias());
		
		String sql=getSql(table, actDefId, nodeId, fkValue);
		
		Map<String, BpmFormField> fieldMap = convertToMap(fieldList);

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if(!isHandleData)return list;
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> map : list) {
			Map<String, Object> obj = handMap(table,fieldMap, map);
			rtnList.add(obj);
		}
		return rtnList;
	}
	
	/**
	 * 获取子表的SQL语句。
	 * @param table
	 * @param actDefId
	 * @param nodeId
	 * @param fkValue
	 * @return
	 */
	private String getSql(BpmFormTable table,String actDefId,String nodeId,String fkValue){
		String subTableName = table.getFactTableName();
		String fkField=table.getRelation();
		String pkField=table.getPkField();
		short keyDataType= table.getKeyDataType();
		
		
		String sql="";
		
		Long tableId=table.getTableId();
		
		boolean isExternal=table.isExtTable();
		
		if(!isExternal){

			//数字类型
			if(keyDataType==0){
				sql = "select a.* from " + subTableName + " a,bpm_bus_link b where  a."+pkField+"=b.bus_pk and a." + fkField + "=" + fkValue;
			}
			else{
				sql = "select a.* from " + subTableName + " a,bpm_bus_link b where  a."+pkField+"=b.bus_pkstr and  a." + fkField + "='" + fkValue +"'";
			}
			 
			
			String limitSql=getLimitSql(tableId, actDefId, nodeId, fkValue);
			String sortSql =getSortSql(table, actDefId);
			// 处理子表权限.2013-1-17,by wwz.
			if (!StringUtil.isEmpty(limitSql)) {
				sql += " " + limitSql;
			}
			
			if (StringUtil.isNotEmpty(sortSql)) {
				sql +=" order by "+sortSql;
			}else {
				sql+=" order by b.BUS_PK";
			}
		}
		else{
			if(keyDataType==0){
				sql="select * from " + subTableName + " where " + fkField + "=" + fkValue;
			}
			else{
				sql="select * from " + subTableName + " where " + fkField + "='" + fkValue +"'";
			}
			sql+=" order by " + table.getPkField();
		}
		
		
		return sql;
	}

	private String getSortSql(BpmFormTable table, String actDefId) {
		if(StringUtil.isEmpty(actDefId)) return "";
		
		String flowKey= BpmUtil.getDefKeyByActDefId(actDefId);
		SubTableSort subTableSort = subTableSortService.getByAKeyAndTName(flowKey,table.getTableName());
		if (BeanUtils.isEmpty(subTableSort))  return "";
		String sql=this.getSortSQL(subTableSort.getFieldsort(), String.valueOf(table.getIsExternal()));
		return sql;
	}
	
	/**
	 * 获取排序的SQL
	 * 
	 * @param sortField
	 * @param source
	 * @return
	 */
	private String getSortSQL(String sortField, String source) {
		StringBuffer sb = new StringBuffer();
		JSONArray jsonArray = JSONArray.fromObject(sortField);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.get(i);
			String name = (String) jsonObj.get("name");
			String sort = (String) jsonObj.get("sort");
			sb.append(this.fixFieldName(name, source, "")).append(" " + sort)
					.append(",");
		}
		if (sb.length() > 0)
			return sb.substring(0, sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * 修正字段名
	 * 
	 * @param fieldName
	 *            字段名
	 * @param source
	 *            数据来源 1.表示自定义表（需要加F_修正）
	 * @param prefix
	 *            前缀修正
	 * @return
	 */
	private String fixFieldName(String fieldName, String source, String prefix) {
		if (StringUtils.isEmpty(fieldName) || StringUtils.isEmpty(source))
			return fieldName;
		if ("0".equals(source))
			fieldName = TableModel.CUSTOMER_COLUMN_PREFIX + fieldName;
		if (StringUtils.isNotEmpty(prefix))
			fieldName = prefix.toLowerCase() + "." + fieldName;
		return fieldName;
	}

	/**
	 * 处理返回的map数据 将key转换成小写。 如果是本地数据库，则替换字段前缀。 如果数据是日期类型，则转换数据格式。
	 * 
	 * @param isExternal
	 * @param fieldMap
	 * @param map
	 */
	public Map<String, Object> handMap(BpmFormTable table,Map<String, BpmFormField> fieldMap, Map<String, Object> map) {
		
		Set<Class> excludeFormator = excludeFormatObjects.get();
		if(excludeFormator == null) excludeFormator = new java.util.HashSet<Class>();
		
		String pkField=table.getPkField();
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String fieldName = entry.getKey();
			Object obj = entry.getValue();
			String key = fieldName;
			if(!table.isExtTable() && fieldName.indexOf(TableModel.CUSTOMER_COLUMN_PREFIX) == 0){
				key = fieldName.replaceFirst(TableModel.CUSTOMER_COLUMN_PREFIX, "");
			}
			if (obj == null) {
				rtnMap.put(key, "");
				continue;
			}
			if(pkField.equalsIgnoreCase(key)){
				rtnMap.put(key, obj);
				continue;
			}
			BpmFormField bpmFormField = fieldMap.get(key);
			if(bpmFormField==null) continue;
			
			// 对时间字段单独处理。
			if (obj instanceof Date) {
				String format = "yyyy-MM-dd";
				String confFormat = bpmFormField.getPropertyMap().get("format");
				if (StringUtil.isNotEmpty(confFormat)) {
					format=confFormat;
				}
				String str = TimeUtil.getDateTimeString((Date) obj, format);
				rtnMap.put(key, str);
			} 
			else if(obj instanceof Number && !excludeFormator.contains(Number.class)){
				Map<String,String> paraMap=bpmFormField.getPropertyMap();
				Object isShowComdify= paraMap.get("isShowComdify");
				Object decimalValue= paraMap.get("decimalValue");
				Object coinValue= paraMap.get("coinValue");
				
				String str =StringUtil. getNumber(obj,isShowComdify,decimalValue,coinValue);
				rtnMap.put(key, str);
			}
			else {
				
				//附件替换
				if( BeanUtils.isNotEmpty(bpmFormField.getControlType()) && bpmFormField.getControlType().shortValue() == FieldPool.ATTACHEMENT ){
					obj = ((String) obj).replace("\"", "￥@@￥");
				}	
				rtnMap.put(key, obj);
			}
		}
		return rtnMap;
	}
	
	


	/**
	 * 根据主键获取子表的数据。
	 * @param tableName
	 * @param pk
	 * @return
	 */
	public List<Map<String,Object>> getByFk(String tableName,Long fk){
		String sql = "select * from " + tableName + " a where "+TableModel.FK_COLUMN_NAME+"=?"  ;
		List<Map<String,Object>> list= jdbcTemplate.queryForList(sql, fk);
		return list;
	}
	
	/**
	 * 判断是否有数据。
	 * @param tableName
	 * @param pk
	 * @return
	 */
	public boolean isHasDataByPk(String tableName, Long pk ){
		String sql = "select count(1) from " + tableName + " a where id=?"  ;
		int  rtn=jdbcTemplate.queryForObject(sql,Integer.class,pk);
		return rtn>0;
	}

}
package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.*;
import com.suneee.platform.dao.system.SysQuerySqlDefDao;
import com.suneee.platform.model.system.SysQueryFieldSetting;
import com.suneee.platform.model.system.SysQueryMetaField;
import com.suneee.platform.model.system.SysQuerySqlDef;
import com.suneee.platform.model.system.SysQueryView;
import com.suneee.platform.model.util.FieldPool;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.platform.xml.system.SysQuerySqlDefXml;
import com.suneee.platform.xml.system.SysQuerySqlDefXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;


/**
 *<pre>
 * 对象功能:自定义SQL定义 Service类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-08 16:02:04
 *</pre>
 */
@Service
public class SysQuerySqlDefService extends BaseService<SysQuerySqlDef>
{
	@Resource
	private SysQuerySqlDefDao dao;
	@Resource(name="jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	@Resource
	private SysQueryMetaFieldService sysQueryMetaFieldService;
	@Resource
	private SysQueryViewService sysQueryViewService;
	@Resource
	private CurrentUserService  currentUserService;
	@Resource
	private SysQueryFieldSettingService sysQueryFieldSettingService;
	
	
	public SysQuerySqlDefService()
	{
	}
	
	@Override
	protected IEntityDao<SysQuerySqlDef, Long> getEntityDao()
	{
		return dao;
	}
	
	public void save(SysQuerySqlDef querySqlDef){
		if(querySqlDef.getId()==null||querySqlDef.getId()==0){
			long id= UniqueIdUtil.genId();
			List<SysQueryMetaField> fields=querySqlDef.getMetaFields();
			for(SysQueryMetaField field:fields){
				field.setId(UniqueIdUtil.genId());
				field.setSqlId(id);
				sysQueryMetaFieldService.add(field);
			}
			querySqlDef.setId(id);
			
			dao.add(querySqlDef);
		}
		else{
			sysQueryMetaFieldService.removeBySQLId(querySqlDef.getId());
			List<SysQueryMetaField> fields=querySqlDef.getMetaFields();
			for(SysQueryMetaField field:fields){
				field.setId(UniqueIdUtil.genId());
				field.setSqlId(querySqlDef.getId());
				sysQueryMetaFieldService.add(field);
			}
			
			dao.update(querySqlDef);
		}
	}
	
	/**
	 * 判断别名是否存在。
	 * @param alias
	 * @param id
	 * @return
	 */
	public boolean isAliasExists(String alias,Long id){
		return dao.isAliasExists(alias, id);
	}
	
	
	/**
	 * 根据json数组字符串获取SysQueryMetaField对象集，同时同步json内容
	 * 
	 * @param jsonArr
	 * @return List<SysQueryField>
	 */
	public List<SysQueryMetaField> getSysQueryMetaFieldArr(String jsonArr) {
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
		if (StringUtil.isEmpty(jsonArr))
			return null;
		List<SysQueryMetaField> sysQueryFieldList = new ArrayList<SysQueryMetaField>();
		JSONArray jsonArray = JSONArray.fromObject(jsonArr);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject obj = jsonArray.getJSONObject(i);
			String controlContent = obj.containsKey("controlContent")?obj.getString("controlContent"):"";
			String alarmSetting = obj.containsKey("alarmSetting")?obj.getString("alarmSetting"):"";
			String resultFrom = obj.containsKey("resultFrom")?obj.getString("resultFrom"):"";
			obj.remove("controlContent");
			obj.remove("alarmSetting");
			obj.remove("resultFrom");
			SysQueryMetaField sysQueryField = (SysQueryMetaField) JSONObject.toBean(obj, SysQueryMetaField.class);
			sysQueryField.setControlContent(controlContent);
			sysQueryField.setAlarmSetting(alarmSetting);
			sysQueryField.setResultFrom(resultFrom);
			sysQueryField.setId(null);
			sysQueryFieldList.add(sysQueryField);
		}

		return sysQueryFieldList;
	}

	/**
	 * 根据别名获取SQL定义。
	 * @param alias
	 * @return
	 */
	public SysQuerySqlDef getByAlias(String alias){
		return dao.getByAlias(alias);
	}

	
	/**
	 * 根据json字符串获取SysQuerySqlDef对象
	 * @param json
	 * @return
	 */
	public SysQuerySqlDef getSysQuerySqlDef(String json){
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
		if(StringUtil.isEmpty(json))return null;
		JSONObject obj = JSONObject.fromObject(json);
		SysQuerySqlDef sysQuerySqlDef = (SysQuerySqlDef)JSONObject.toBean(obj, SysQuerySqlDef.class);
		return sysQuerySqlDef;
	}
	
	public List<SysQueryMetaField> obtainFieldListBySql(String sql){
		List<SysQueryMetaField> list=new ArrayList<SysQueryMetaField>();
		
		SqlRowSet srs = jdbcTemplate.queryForRowSet(sql);
		SqlRowSetMetaData srsmd = srs.getMetaData();
		// 列从1开始算
		for (int i = 1; i < srsmd.getColumnCount() + 1; i++) {
			String cn = srsmd.getColumnName(i).toUpperCase();
			String ctn = srsmd.getColumnTypeName(i);
			SysQueryMetaField field = getQueryMetaField(cn,ctn);
			field.setSn((short)(i*10));
			list.add(field);
		}
		
		return list;
	}
	
	/**
	 * 构建SysQueryMetaField。
	 * @param fieldName
	 * @param fieldType
	 * @return
	 */
	private SysQueryMetaField getQueryMetaField(String fieldName,String fieldType){
		SysQueryMetaField field = new SysQueryMetaField();
		field.setId(UniqueIdUtil.genId());
		
		field.setName(fieldName);
		field.setFieldName(fieldName);
		field.setFieldDesc(fieldName);
		
		field.setDataType(ServiceUtil. getDataType(fieldType));
		field.setIsShow(SysQueryMetaField.IS_SHOW);
		field.setIsSearch(SysQueryMetaField.IS_NOT_SEARCH);
		field.setControlType(FieldPool.TEXT_INPUT);
		field.setIsVirtual((short)0);
		
		return field;
	}
	
	

	public void delByIds(Long[] lAryId) {
		for(Long id : lAryId){
			SysQuerySqlDef sq = this.getById(id);
			sysQueryMetaFieldService.removeBySQLId(id);
			String alias = sq.getAlias();
			sysQueryViewService.removeBySQLAlias(alias);
			this.delById(id);
		}
	}

	/**
	 * 设置分类
	 * @param categoryId
	 * @param aryTableId
	 */
	public void updCategory(Long categoryId, String[] aryId) {
		dao.updCategory(categoryId, aryId);
	}
	
	/**
	 * 导入自定义Sql查询XML
	 * @param inputStream
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream) throws Exception {
		Document doc = Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		// 验证格式是否正确
		XmlUtil.checkXmlFormat(root, "querySql", "querySqlDefs");

		String xmlStr = root.asXML();
		SysQuerySqlDefXmlList sysQuerySqlDefXmlList = (SysQuerySqlDefXmlList) XmlBeanUtil.unmarshall(xmlStr, SysQuerySqlDefXmlList.class);
		
		List<SysQuerySqlDefXml> list = sysQuerySqlDefXmlList.getSysQuerySqlDefXmlList();
		
		for (SysQuerySqlDefXml sysQuerySqlDefXml : list) {
			// 导入表，并解析相关信息
			this.importSysQuerySqlDefXml(sysQuerySqlDefXml);
			
		}
	}
	
	/**
	 * 导入时生成自定义Sql查询
	 * @param sysQuerySqlDefXml
	 * @return
	 * @throws Exception
	 */
	
	private void importSysQuerySqlDefXml(SysQuerySqlDefXml sysQuerySqlDefXml)throws Exception 
	{
		Long sqlId=UniqueIdUtil.genId();
		SysQuerySqlDef sqlDef=sysQuerySqlDefXml.getSysQuerySqlDef();
		//Long sqlId=sqlDef.getId();
		if(BeanUtils.isEmpty(sqlDef))
		{
			//MsgUtil.addMsg(MsgUtil.WARN, "什么内容也没有，请检查你的Xml文件！");
			throw new Exception();
			//return;
		}
		String alias=sqlDef.getAlias();
		SysQuerySqlDef querySqlDef=dao.getByAlias(alias);
		if(BeanUtils.isNotEmpty(querySqlDef))
		{
			MsgUtil.addMsg(MsgUtil.WARN, "别名为‘" + alias + "’的Sql查询已经存在，请检查你的xml文件！");
			return;
		}
		sqlDef.setId(sqlId);
		
		//字段
		List<SysQueryMetaField> metaFields= sysQuerySqlDefXml.getSysQueryMetaFieldList();
		if(BeanUtils.isNotEmpty(metaFields))
		{
		for(SysQueryMetaField metaField:metaFields)
		{
			Long metaFieldId=UniqueIdUtil.genId();
			metaField.setId(metaFieldId);
			metaField.setSqlId(sqlId);
			sysQueryMetaFieldService.add(metaField);
		}
		sqlDef.setMetaFields(metaFields);
		}
		
		//视图
		List<SysQueryView> queryViews= sysQuerySqlDefXml.getSysQueryViewList();
		//System.out.println(queryViews);
		if (BeanUtils.isNotEmpty(queryViews)) {
			for (SysQueryView view : queryViews) {
				Long viewId = UniqueIdUtil.genId();
				view.setId(viewId);
				view.setSqlAlias(alias);
				view.setAlias(view.getAlias());
				// 字段设置
				List<SysQueryFieldSetting> fieldSettings = view
						.getFieldSettings();
				if (BeanUtils.isNotEmpty(fieldSettings)) {
					for (SysQueryFieldSetting fieldSetting : fieldSettings) {
						Long fieldId = UniqueIdUtil.genId();
						fieldSetting.setId(fieldId);
						fieldSetting.setViewId(viewId);
						sysQueryFieldSettingService.add(fieldSetting);

					}
					view.setFieldSettings(fieldSettings);
					sysQueryViewService.add(view);
				}
			}
			sqlDef.setSysQueryViewList(queryViews);
		}
		
		dao.add(sqlDef);
		MsgUtil.addMsg(MsgUtil.SUCCESS, "别名为"+alias+"的自定义Sql导入成功！");
	}
	
	/**
	 * 
	 * 导出自定义Sql查询XML
	 * @param tableIds
	 * @return
	 * @throws Exception
	 */
	public String exportXml(Long[] tableIds) throws Exception {
		List<SysQuerySqlDef> querySqlDefs=new ArrayList<SysQuerySqlDef>();
		for (int i = 0; i < tableIds.length; i++) {
			SysQuerySqlDef sysQuerySqlDef = dao.getById(tableIds[i]);
			querySqlDefs.add(sysQuerySqlDef);
		}
		return exportXml(querySqlDefs);
	}
	
	
	/**
	 * 导出全部自定义Sql查询
	 * @param querySqlDefs
	 * @return 
	 * @throws Exception
	 */
	public String exportXml(List<SysQuerySqlDef> querySqlDefs)throws Exception{
		SysQuerySqlDefXmlList sysQuerySqlDefXmls = new SysQuerySqlDefXmlList();
		List<SysQuerySqlDefXml> list = new ArrayList<SysQuerySqlDefXml>();
		for(SysQuerySqlDef sysQuerySqlDef:querySqlDefs){
			SysQuerySqlDefXml sysQuerySqlDefXml =this.exportSysQuerySqlDefXml(sysQuerySqlDef);
			list.add(sysQuerySqlDefXml);
		}
		sysQuerySqlDefXmls.setSysQuerySqlDefXmlList(list);
		return XmlBeanUtil.marshall(sysQuerySqlDefXmls, SysQuerySqlDefXmlList.class);
	}

	/**
	 * 导出表的信息
	 * @param sysQuerySqlDef
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	private SysQuerySqlDefXml exportSysQuerySqlDefXml(SysQuerySqlDef sysQuerySqlDef) throws Exception{
		SysQuerySqlDefXml sysQuerySqlDefXml = new SysQuerySqlDefXml();
		
		Long id =sysQuerySqlDef.getId();
		String alias=sysQuerySqlDef.getAlias();
		//字段
		List<SysQueryMetaField>  queryMetaFieldList=sysQueryMetaFieldService.getListBySqlId(id);
		
		//视图列表
		List<SysQueryView>  viewList=sysQueryViewService.getListBySqlAlias(alias);
		this.exportView(viewList);
		sysQuerySqlDefXml.setSysQuerySqlDef(sysQuerySqlDef);
		
		if (BeanUtils.isNotEmpty(queryMetaFieldList)){
			sysQuerySqlDefXml.setSysQueryMetaFieldList(queryMetaFieldList);
		}
		if(BeanUtils.isNotEmpty(viewList)){
			sysQuerySqlDefXml.setSysQueryViewList(viewList);
		}

		return sysQuerySqlDefXml;
	}


	/**
	 * 导出视图
	 * @throws Exception 
	 */
	private void exportView(List<SysQueryView> viewList) throws Exception {
		for(SysQueryView view:viewList){
			Long id=view.getId();
			List<SysQueryFieldSetting> fieldSettingList=sysQueryFieldSettingService.getBySysQueryViewId(id);
			//导出字段列表
			view.setFieldSettings(fieldSettingList);
		}
	}
	
	
	/**
	 * 获取最终要保存的metaFile
	 * 如果，页面上已经存在了，就排除掉，如果没有就添加
	 * @param pageFieldList
	 * @param metaFieldList
	 * @return
	 */
	public List<SysQueryMetaField> getMetaFieldList(List<SysQueryMetaField> pageFieldList,List<SysQueryMetaField> metaFieldList) {
		if(metaFieldList == null || metaFieldList.size() == 0 ) 
			return pageFieldList;
		Map<String,SysQueryMetaField> map =  this.converToMapForMetaField(metaFieldList);
		for(SysQueryMetaField metaField : metaFieldList){	// 以metaField 为基准
			for(SysQueryMetaField pageField : pageFieldList){
				if(metaField.getName().equalsIgnoreCase(pageField.getName())){
					map.remove(pageField.getName());			// 清除页面有的
				}
				map.put(pageField.getName(), pageField);	// 加上页面的
			}
		}
		return this.converToListForMetaField(map);
	}

	private List<SysQueryMetaField> converToListForMetaField(
			Map<String, SysQueryMetaField> map) {
		List<SysQueryMetaField> list = new LinkedList<SysQueryMetaField>();
		for(Map.Entry<String, SysQueryMetaField> entry:map.entrySet()){   
		    list.add(entry.getValue());
		}   
		return list;
	}

	private Map<String, SysQueryMetaField> converToMapForMetaField(
			List<SysQueryMetaField> metaFieldList) {
		Map<String,SysQueryMetaField> metaFieldMap = new LinkedHashMap<String,SysQueryMetaField>();
		for(SysQueryMetaField metaField	: metaFieldList){
			metaFieldMap.put(metaField.getName(), metaField);
		}
		return metaFieldMap;
	}
	
	
	
	
	
	
}

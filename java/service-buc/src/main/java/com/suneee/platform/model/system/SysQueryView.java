package com.suneee.platform.model.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.suneee.core.model.BaseModel;
import com.suneee.platform.xml.constant.XmlConstant;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 对象功能:视图定义 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-06-14 11:13:24
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "sysQueryView")
@XmlAccessorType(XmlAccessType.NONE)
public class SysQueryView extends BaseModel {
	
	public static String FIELD_BEGIN="begin";
	
	public static String FIELD_END="end";
	
	public static String CONDITION_AND="and";
	
	
	
	
	private static final long serialVersionUID = 1L;
	// 主键
	@XmlElement
	protected Long id;
	// sql别名
	@XmlElement
	protected String sqlAlias="";
	// 视图定义名称
	@XmlElement
	protected String name="";
	// 视图别名
	@XmlElement
	protected String alias="";
	// 查询条件
	@XmlElement
	protected String conditions;
	// 过滤器
	@XmlElement
	protected String filter;
	// 过滤器类型(1，字段组合，2，自定义SQL)
	@XmlElement
	protected Short filterType=1;
	// 按钮定义
	@XmlElement
	protected String buttons="";
	// 是否初始化查询
	@XmlElement
	protected Short initQuery=1;
	/**
	 * 模版别名
	 */
	@XmlElement
	protected String templateAlias="";
	// 模版定义
	@XmlElement
	@JSONField(serialize=false)
	protected String template;
	// 是否支持分组(1,支持分组，0,不支持分组)
	@XmlElement
	protected Short supportGroup=0;
	// 分组设定
	@XmlElement
	protected String groupSetting="";
	// 需要分页
	@XmlElement
	protected Short needPage=1;
	// 分页大小
	@XmlElement
	protected Short pageSize=20;
	// 显示行号
	@XmlElement
	protected Short showRowsNum=0;
	// 排序
	@XmlElement
	protected Short sn=0;
	
	/**
	 * 显示字段列表。
	 */
	@XmlElementWrapper(name = XmlConstant.SYS_QUERY_FIELD_SETTING_LIST)
	@XmlElement(name =XmlConstant.SYS_QUERY_FIELD_SETTING, type =SysQueryFieldSetting.class)
	protected List<SysQueryFieldSetting> fieldSettings=new ArrayList<SysQueryFieldSetting>();
	
	protected Map<String,SysQueryMetaField> fieldMap=new HashMap<String, SysQueryMetaField>();
	
	protected Map<String,SysQueryMetaField> factFieldMap=new HashMap<String, SysQueryMetaField>();

	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 主键
	 * @return
	 */
	public Long getId() {
		return this.id;
	}
	public void setSqlAlias(String sqlAlias){
		this.sqlAlias = sqlAlias;
	}
	/**
	 * 返回 sql别名
	 * @return
	 */
	public String getSqlAlias() {
		return this.sqlAlias;
	}
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 返回 视图定义名称
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	public void setAlias(String alias){
		this.alias = alias;
	}
	/**
	 * 返回 视图别名
	 * @return
	 */
	public String getAlias() {
		return this.alias;
	}
	public void setConditions(String conditions){
		this.conditions = conditions;
	}
	/**
	 * 返回 查询条件
	 * @return
	 */
	public String getConditions() {
		return this.conditions;
	}
	public void setFilter(String filter){
		this.filter = filter;
	}
	/**
	 * 返回 过滤器
	 * @return
	 */
	public String getFilter() {
		return this.filter;
	}
	public void setFilterType(Short filterType){
		this.filterType = filterType;
	}
	/**
	 * 返回 过滤器类型
	 * @return
	 */
	public Short getFilterType() {
		return this.filterType;
	}
	public void setButtons(String buttons){
		this.buttons = buttons;
	}
	/**
	 * 返回 按钮定义
	 * @return
	 */
	public String getButtons() {
		return this.buttons;
	}
	public void setInitQuery(Short initQuery){
		this.initQuery = initQuery;
	}
	/**
	 * 返回 是否初始化查询
	 * @return
	 */
	public Short getInitQuery() {
		return this.initQuery;
	}
	public void setTemplate(String template){
		this.template = template;
	}
	/**
	 * 返回 模版定义
	 * @return
	 */
	public String getTemplate() {
		return this.template;
	}
	public void setSupportGroup(Short supportGroup){
		this.supportGroup = supportGroup;
	}
	/**
	 * 返回 是否支持分组
	 * @return
	 */
	public Short getSupportGroup() {
		return this.supportGroup;
	}
	public void setGroupSetting(String groupSetting){
		this.groupSetting = groupSetting;
	}
	/**
	 * 返回 分组设定
	 * @return
	 */
	public String getGroupSetting() {
		return this.groupSetting;
	}
	public void setPageSize(Short pageSize){
		this.pageSize = pageSize;
	}
	/**
	 * 返回 分页大小
	 * @return
	 */
	public Short getPageSize() {
		return this.pageSize;
	}
	public void setShowRowsNum(Short showRowsNum){
		this.showRowsNum = showRowsNum;
	}
	/**
	 * 返回 显示行号
	 * @return
	 */
	public Short getShowRowsNum() {
		return this.showRowsNum;
	}
	public void setSn(Short sn){
		this.sn = sn;
	}
	/**
	 * 返回 排序
	 * @return
	 */
	public Short getSn() {
		return this.sn;
	}
	
	public Short getNeedPage() {
		return needPage;
	}
	public void setNeedPage(Short needPage) {
		this.needPage = needPage;
	}
	public List<SysQueryFieldSetting> getFieldSettings() {
		return fieldSettings;
	}
	
	
	public void setFieldSettings(List<SysQueryFieldSetting> fieldSettings) {
		for(SysQueryFieldSetting setting:fieldSettings){
			SysQueryMetaField metaField=setting.getMetaField();
			this.fieldMap.put(metaField.getName(), metaField);
		}
		this.fieldSettings = fieldSettings;
	}
	
	public void setMetaFieldMap(List<SysQueryMetaField> list){
		for(SysQueryMetaField metaField:list){
			factFieldMap.put(metaField.getFieldName(), metaField);
		}
	}
	
	
	public String getTemplateAlias() {
		return templateAlias;
	}
	public void setTemplateAlias(String templateAlias) {
		this.templateAlias = templateAlias;
	}
	public Map<String, SysQueryMetaField> getFieldMap() {
		return fieldMap;
	}
	public void setFieldMap(Map<String, SysQueryMetaField> fieldMap) {
		this.fieldMap = fieldMap;
	}
	

   	public Map<String, SysQueryMetaField> getFactFieldMap() {
		return factFieldMap;
	}
	public void setFactFieldMap(Map<String, SysQueryMetaField> factFieldMap) {
		this.factFieldMap = factFieldMap;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysQueryView)) 
		{
			return false;
		}
		SysQueryView rhs = (SysQueryView) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.sqlAlias, rhs.sqlAlias)
		.append(this.name, rhs.name)
		.append(this.alias, rhs.alias)
		.append(this.conditions, rhs.conditions)
		.append(this.filter, rhs.filter)
		.append(this.filterType, rhs.filterType)
		.append(this.buttons, rhs.buttons)
		.append(this.initQuery, rhs.initQuery)
		.append(this.template, rhs.template)
		.append(this.supportGroup, rhs.supportGroup)
		.append(this.groupSetting, rhs.groupSetting)
		.append(this.pageSize, rhs.pageSize)
		.append(this.showRowsNum, rhs.showRowsNum)
		.append(this.sn, rhs.sn)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.sqlAlias) 
		.append(this.name) 
		.append(this.alias) 
		.append(this.conditions) 
		.append(this.filter) 
		.append(this.filterType) 
		.append(this.buttons) 
		.append(this.initQuery) 
		.append(this.template) 
		.append(this.supportGroup) 
		.append(this.groupSetting) 
		.append(this.pageSize) 
		.append(this.showRowsNum) 
		.append(this.sn) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("sqlAlias", this.sqlAlias) 
		.append("name", this.name) 
		.append("alias", this.alias) 
		.append("conditions", this.conditions) 
		.append("filter", this.filter) 
		.append("filterType", this.filterType) 
		.append("buttons", this.buttons) 
		.append("initQuery", this.initQuery) 
		.append("template", this.template) 
		.append("supportGroup", this.supportGroup) 
		.append("groupSetting", this.groupSetting) 
		.append("pageSize", this.pageSize) 
		.append("showRowsNum", this.showRowsNum) 
		.append("sn", this.sn) 
		.toString();
	}
	
   
  

}
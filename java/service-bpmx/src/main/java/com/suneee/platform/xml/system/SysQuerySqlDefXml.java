package com.suneee.platform.xml.system;

import com.suneee.platform.model.system.SysQueryMetaField;
import com.suneee.platform.model.system.SysQuerySqlDef;
import com.suneee.platform.model.system.SysQueryView;
import com.suneee.platform.xml.constant.XmlConstant;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "querySqlDefs")
@XmlAccessorType(XmlAccessType.FIELD)
public class SysQuerySqlDefXml 
{
	/**
	 * 自定义Sql查询
	 */
	@XmlElement(name = XmlConstant.SYS_QUERY_SQL_DEF, type = SysQuerySqlDef.class)
	private SysQuerySqlDef sysQuerySqlDef;
	/**
	 * 视图List
	 */
	
	@XmlElementWrapper(name =XmlConstant.SYS_QUERY_VIEW_LIST)
	@XmlElement(name = XmlConstant.SYS_QUERY_VIEW, type =SysQueryView.class)
	private List<SysQueryView> sysQueryViewList;
	
	/**
	 * 字段List
	 */
	@XmlElementWrapper(name =XmlConstant.SYS_QUERY_META_FIELD_LIST)
	@XmlElement(name = XmlConstant.SYS_QUERY_META_FIELD, type =SysQueryMetaField.class)
	private List<SysQueryMetaField> sysQueryMetaFieldList;
	


	
	
	
	public SysQuerySqlDef getSysQuerySqlDef() {
		return sysQuerySqlDef;
	}

	public void setSysQuerySqlDef(SysQuerySqlDef sysQuerySqlDef) {
		this.sysQuerySqlDef = sysQuerySqlDef;
	}

	

	public List<SysQueryMetaField> getSysQueryMetaFieldList() {
		return sysQueryMetaFieldList;
	}

	public void setSysQueryMetaFieldList(List<SysQueryMetaField> sysQueryMetaFieldList) {
		this.sysQueryMetaFieldList = sysQueryMetaFieldList;
	}

	public List<SysQueryView> getSysQueryViewList() {
		return sysQueryViewList;
	}

	public void setSysQueryViewList(List<SysQueryView> sysQueryViewList) {
		this.sysQueryViewList = sysQueryViewList;
	}

	
	
	

	
}

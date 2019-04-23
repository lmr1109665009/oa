package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


/**
 * 对象功能:自定义SQL定义 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-06-14 10:54:16
 */
@XmlRootElement(name = "sysQuerySqlDef")
@XmlAccessorType(XmlAccessType.NONE)
public class SysQuerySqlDef extends BaseModel {
	private static final long serialVersionUID = 1L;
	// 主键
	@XmlElement
	protected Long id;
	// 名称
	@XmlElement
	protected String name;
	// sql语句
//	@JSONField(serialize=false)
	@XmlElement
	protected String sql;
	// 数据源名称
	@XmlElement
	protected String dsname;
	// 按钮定义
	@XmlElement
	protected String buttonDef;
	// 别名
	@XmlElement
	protected String alias;
	// 分类
	@XmlElement
	protected String categoryid;
	// 支持tab
	@XmlElement
	protected Integer supportTab=1;
	// 分类名称。
	@XmlElement
	protected String categoryName="";
	//字段
	//@XmlElementWrapper(name ="metaFieldList")
	//@XmlElement(name = "metaField", type =SysQueryMetaField.class)
	@XmlElement
	protected List<SysQueryMetaField> metaFields=new ArrayList<SysQueryMetaField>();
	//视图
	//@XmlElementWrapper(name ="viewList")
	//@XmlElement(name = "view", type =SysQueryView.class)
	@XmlElement
	protected List<SysQueryView>sysQueryViewList=new ArrayList<SysQueryView>();
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
	
	public List<SysQueryView> getSysQueryViewList() {
		return sysQueryViewList;
	}
	public void setSysQueryViewList(List<SysQueryView> sysQueryViewList) {
		this.sysQueryViewList = sysQueryViewList;
	}
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 返回 名称
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	public void setSql(String sql){
		this.sql = sql;
	}
	/**
	 * 返回 sql语句
	 * @return
	 */
	public String getSql() {
		return this.sql;
	}
	public void setDsname(String dsname){
		this.dsname = dsname;
	}
	/**
	 * 返回 数据源名称
	 * @return
	 */
	public String getDsname() {
		return this.dsname;
	}
	public void setButtonDef(String buttonDef){
		this.buttonDef = buttonDef;
	}
	/**
	 * 返回 按钮定义
	 * @return
	 */
	public String getButtonDef() {
		return this.buttonDef;
	}
	public void setAlias(String alias){
		this.alias = alias;
	}
	/**
	 * 返回 别名
	 * @return
	 */
	public String getAlias() {
		return this.alias;
	}
	public void setCategoryid(String categoryid){
		this.categoryid = categoryid;
	}
	/**
	 * 返回 分类
	 * @return
	 */
	public String getCategoryid() {
		return this.categoryid;
	}
   	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public List<SysQueryMetaField> getMetaFields() {
		return metaFields;
	}
	public void setMetaFields(List<SysQueryMetaField> metaFields) {
		this.metaFields = metaFields;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysQuerySqlDef)) 
		{
			return false;
		}
		SysQuerySqlDef rhs = (SysQuerySqlDef) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.name, rhs.name)
		.append(this.sql, rhs.sql)
		.append(this.dsname, rhs.dsname)
		.append(this.buttonDef, rhs.buttonDef)
		.append(this.alias, rhs.alias)
		.append(this.categoryid, rhs.categoryid)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.name) 
		.append(this.sql) 
		.append(this.dsname) 
		.append(this.buttonDef) 
		.append(this.alias) 
		.append(this.categoryid) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("name", this.name) 
		.append("sql", this.sql) 
		.append("dsname", this.dsname) 
		.append("buttonDef", this.buttonDef) 
		.append("alias", this.alias) 
		.append("categoryid", this.categoryid) 
		.toString();
	}
	public Integer getSupportTab() {
		return supportTab;
	}
	public void setSupportTab(Integer supportTab) {
		this.supportTab = supportTab;
	}
   
  

}
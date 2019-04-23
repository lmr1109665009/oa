package com.suneee.platform.model.form;

import com.suneee.core.model.BaseModel;
import com.suneee.platform.model.bpm.BpmNodeSet;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:手机表单 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miao
 * 创建时间:2015-10-28 10:52:49
 */
public class BpmMobileFormDef extends BaseModel {
	// ID
	protected Long id;
	// 版本
	protected Integer version;
	// 表单KEY
	protected String formKey;
	// 表单HTML
	protected String formHtml;
	// 表ID
	protected Long tableId;
	// 表名
	protected String tableName;
	// 表单主题
	protected String subject;
	// 创建人ID
	protected Long createBy;
	// 创建人
	protected String creator;
	// 创建时间
	protected Date createTime;
	// 是否默认版本
	protected Integer isDefault;
	// 是否发布
	protected Integer isPublished;
	// 发布人
	protected String publisher;
	// 发布人ID
	protected Long publishBy;
	// 发布时间
	protected Date publishTime;
	// 更新人
	protected String updator;
	// 更新人ID
	protected Long updateBy;
	// 最后更新时间
	protected Date updateTime;
	// 分类
	protected Long categoryId;
	//10000005210003,oneColumn;10000005210007,openWindow
	//表ID,模版别名;表ID,模版别名
	protected String templatesId="";
	
	protected Integer versionCount=1;
	
	/**
	 * pc表单key。
	 */
	protected String pcFormKey="";
	
	/**
	 * 节点配置信息。
	 */
	protected BpmNodeSet bpmNodeSet=null;

	/**
	 * 分类名称
	 */
	protected String categoryName;
	
	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 ID
	 * @return
	 */
	public Long getId() {
		return this.id;
	}

	public void setFormKey(String formKey){
		this.formKey = formKey;
	}
	/**
	 * 返回 表单KEY
	 * @return
	 */
	public String getFormKey() {
		return this.formKey;
	}
	public void setFormHtml(String formHtml){
		this.formHtml = formHtml;
	}
	/**
	 * 返回 表单HTML
	 * @return
	 */
	public String getFormHtml() {
		return this.formHtml;
	}
	
	public void setTableId(Long tableId){
		this.tableId = tableId;
	}
	/**
	 * 返回 表ID
	 * @return
	 */
	public Long getTableId() {
		return this.tableId;
	}
	public void setTableName(String tableName){
		this.tableName = tableName;
	}
	/**
	 * 返回 表名
	 * @return
	 */
	public String getTableName() {
		return this.tableName;
	}
	public void setSubject(String subject){
		this.subject = subject;
	}
	/**
	 * 返回 表单主题
	 * @return
	 */
	public String getSubject() {
		return this.subject;
	}
	public void setCreateBy(Long createBy){
		this.createBy = createBy;
	}
	/**
	 * 返回 创建人ID
	 * @return
	 */
	public Long getCreateBy() {
		return this.createBy;
	}
	public void setCreator(String creator){
		this.creator = creator;
	}
	/**
	 * 返回 创建人
	 * @return
	 */
	public String getCreator() {
		return this.creator;
	}
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	/**
	 * 返回 创建时间
	 * @return
	 */
	public Date getCreateTime() {
		return this.createTime;
	}
	
	public void setPublisher(String publisher){
		this.publisher = publisher;
	}
	/**
	 * 返回 发布人
	 * @return
	 */
	public String getPublisher() {
		return this.publisher;
	}
	public void setPublishBy(Long publishBy){
		this.publishBy = publishBy;
	}
	/**
	 * 返回 发布人ID
	 * @return
	 */
	public Long getPublishBy() {
		return this.publishBy;
	}
	public void setPublishTime(Date publishTime){
		this.publishTime = publishTime;
	}
	public Integer getVersionCount() {
		return versionCount;
	}
	public void setVersionCount(Integer versionCount) {
		this.versionCount = versionCount;
	}
	/**
	 * 返回 发布时间
	 * @return
	 */
	public Date getPublishTime() {
		return this.publishTime;
	}
	public void setUpdator(String updator){
		this.updator = updator;
	}
	/**
	 * 返回 更新人
	 * @return
	 */
	public String getUpdator() {
		return this.updator;
	}
	public void setUpdateBy(Long updateBy){
		this.updateBy = updateBy;
	}
	/**
	 * 返回 更新人ID
	 * @return
	 */
	public Long getUpdateBy() {
		return this.updateBy;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}
	/**
	 * 返回 最后更新时间
	 * @return
	 */
	public Date getUpdateTime() {
		return this.updateTime;
	}
	public void setCategoryId(Long categoryId){
		this.categoryId = categoryId;
	}
	/**
	 * 返回 分类
	 * @return
	 */
	public Long getCategoryId() {
		return this.categoryId;
	}
	

   	public Integer getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}
	public Integer getIsPublished() {
		return isPublished;
	}
	public void setIsPublished(Integer isPublished) {
		this.isPublished = isPublished;
	}
	
	public String getPcFormKey() {
		return pcFormKey;
	}
	
	/**
	 * 设置pc表单。
	 * @param pcFormKey
	 */
	public void setPcFormKey(String pcFormKey) {
		this.pcFormKey = pcFormKey;
	}
	
	
	public String getTemplatesId() {
		return templatesId;
	}
	public void setTemplatesId(String templatesId) {
		this.templatesId = templatesId;
	}
	public BpmNodeSet getBpmNodeSet() {
		return bpmNodeSet;
	}
	public void setBpmNodeSet(BpmNodeSet bpmNodeSet) {
		this.bpmNodeSet = bpmNodeSet;
	}
	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}
	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof BpmMobileFormDef)) 
		{
			return false;
		}
		BpmMobileFormDef rhs = (BpmMobileFormDef) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.version, rhs.version)
		.append(this.formKey, rhs.formKey)
		
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.version) 
		.append(this.formKey) 
	
		.append(this.tableId) 
		
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("version", this.version) 
		.append("formKey", this.formKey) 
		.append("formHtml", this.formHtml) 
		.append("tableId", this.tableId) 
		.append("tableName", this.tableName) 
		.append("subject", this.subject) 
		.append("createBy", this.createBy) 
		.append("creator", this.creator) 
		.append("createTime", this.createTime) 
		.append("isDefault", this.isDefault) 
		.append("isPublished", this.isPublished) 
		.append("publisher", this.publisher) 
		.append("publishBy", this.publishBy) 
		.append("publishTime", this.publishTime) 
		.append("updator", this.updator) 
		.append("updateBy", this.updateBy) 
		.append("updateTime", this.updateTime) 
		.append("categoryId", this.categoryId) 
		.toString();
	}
   
  

}
/**
 * 
 */
package com.suneee.ucp.base.model.system;

import java.util.Date;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 企业信息对象
 * @author xiongxianyun
 *
 */
public class Enterpriseinfo extends UcpBaseModel{

	/**
	 *  
	 */ 
	private static final long serialVersionUID = 5586971042514397391L;

	/**
	 * 企业ID
	 */
	private Long id;
	
	/**
	 * 企业名称
	 */
	private String comp_name;
	
	/**
	 * 企业简称
	 */
	private String comp_short_name;
	
	/**
	 * 企业编码
	 */
	private String comp_code;
	
	/**
	 * 是否集团企业：Y=是，N=否
	 */
	private String isGroup;
	
	/**
	 * 父企业ID
	 */
	private Long comp_parent_code;
	
	/**
	 * 集团编码
	 */
	private String groupCode;
	
	/**
	 * 企业地址 
	 */ 
	private String address;
	
	/**
	 * 联系方式
	 */ 
	private String contact;
	
	/**
	 * 联系电话
	 */ 
	private String telephone;
	
	/**
	 * 备注
	 */ 
	private String remarks;
	
	/**
	 * 类型
	 */ 
	private String type;
	
	/**
	 * 
	 */ 
	private String cluster_code;
	
	/**
	 * 
	 */ 
	private String cluster_name;
	
	/**
	 * 
	 */ 
	private String auditFlag;
	
	/**
	 * 删除标识:0=未删除
	 */ 
	private String deleteFlag;
	
	/**
	 * 创建日期
	 */ 
	private Long createDate;
	
	/**
	 * 更新日期
	 */ 
	private Long updateDate;
	
	/**
	 *操作时间
	 */ 
	private Date opTime;

	/**
	 * @return the comp_name
	 */
	public String getComp_name() {
		return comp_name;
	}

	/**
	 * @param comp_name the comp_name to set
	 */
	public void setComp_name(String comp_name) {
		this.comp_name = comp_name;
	}

	/**
	 * @return the comp_short_name
	 */
	public String getComp_short_name() {
		return comp_short_name;
	}

	/**
	 * @param comp_short_name the comp_short_name to set
	 */
	public void setComp_short_name(String comp_short_name) {
		this.comp_short_name = comp_short_name;
	}

	/**
	 * @return the comp_code
	 */
	public String getComp_code() {
		return comp_code;
	}

	/**
	 * @param comp_code the comp_code to set
	 */
	public void setComp_code(String comp_code) {
		this.comp_code = comp_code;
	}

	/**
	 * @return the isGroup
	 */
	public String getIsGroup() {
		return isGroup;
	}

	/**
	 * @param isGroup the isGroup to set
	 */
	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the comp_parent_code
	 */
	public Long getComp_parent_code() {
		return comp_parent_code;
	}

	/**
	 * @param comp_parent_code the comp_parent_code to set
	 */
	public void setComp_parent_code(Long comp_parent_code) {
		this.comp_parent_code = comp_parent_code;
	}

	/**
	 * @return the group_code
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the group_code to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * @param contact the contact to set
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the cluster_code
	 */
	public String getCluster_code() {
		return cluster_code;
	}

	/**
	 * @param cluster_code the cluster_code to set
	 */
	public void setCluster_code(String cluster_code) {
		this.cluster_code = cluster_code;
	}

	/**
	 * @return the cluster_name
	 */
	public String getCluster_name() {
		return cluster_name;
	}

	/**
	 * @param cluster_name the cluster_name to set
	 */
	public void setCluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
	}

	/**
	 * @return the auditFlag
	 */
	public String getAuditFlag() {
		return auditFlag;
	}

	/**
	 * @param auditFlag the auditFlag to set
	 */
	public void setAuditFlag(String auditFlag) {
		this.auditFlag = auditFlag;
	}

	/**
	 * @return the deleteFlag
	 */
	public String getDeleteFlag() {
		return deleteFlag;
	}

	/**
	 * @param deleteFlag the deleteFlag to set
	 */
	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	/**
	 * @return the createDate
	 */
	public Long getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the updateDate
	 */
	public Long getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Long updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return the opTime
	 */
	public Date getOpTime() {
		return opTime;
	}

	/**
	 * @param opTime the opTime to set
	 */
	public void setOpTime(Date opTime) {
		this.opTime = opTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [id=");
		builder.append(id);
		builder.append(", comp_name=");
		builder.append(comp_name);
		builder.append(", comp_short_name=");
		builder.append(comp_short_name);
		builder.append(", comp_code=");
		builder.append(comp_code);
		builder.append(", isGroup=");
		builder.append(isGroup);
		builder.append(", comp_parent_code=");
		builder.append(comp_parent_code);
		builder.append(", groupCode=");
		builder.append(groupCode);
		builder.append(", address=");
		builder.append(address);
		builder.append(", contact=");
		builder.append(contact);
		builder.append(", telephone=");
		builder.append(telephone);
		builder.append(", remarks=");
		builder.append(remarks );
		builder.append(", type=");
		builder.append(type);
		builder.append(", cluster_code=");
		builder.append(cluster_code);
		builder.append(", cluster_name=");
		builder.append(cluster_name);
		builder.append(", auditFlag=");
		builder.append(auditFlag);
		builder.append(", deleteFlag=");
		builder.append(deleteFlag);
		builder.append(", updateDate=");
		builder.append(updateDate);
		builder.append(", createDate=");
		builder.append(createDate);
		builder.append(", opTime=");
		builder.append(opTime);
		builder.append("]");
		return builder.toString();
	}
}

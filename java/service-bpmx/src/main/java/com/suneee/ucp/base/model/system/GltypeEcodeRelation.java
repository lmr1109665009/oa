/**
 * 
 */
package com.suneee.ucp.base.model.system;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author xiongxianyun
 *
 */
public class GltypeEcodeRelation{
	/**
	 * 总分类与企业编码关系ID
	 */
	private Long typeEcodeId;
	
	/**
	 * 总分类ID
	 */
	private Long typeId;
	
	/**
	 * 企业编码
	 */
	private String enterpriseCode;

	/**
	 * 企业名称
	 */
	private String enterpriseName;

	/**
	 * @return the typeEcodeId
	 */
	public Long getTypeEcodeId() {
		return typeEcodeId;
	}

	/**
	 * @param typeEcodeId the typeEcodeId to set
	 */
	public void setTypeEcodeId(Long typeEcodeId) {
		this.typeEcodeId = typeEcodeId;
	}

	/**
	 * @return the typeId
	 */
	public Long getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the enterpriseCode
	 */
	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	/**
	 * @param enterpriseCode the enterpriseCode to set
	 */
	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(-82280557, -700257973)
			.append(this.typeEcodeId)
			.append(this.typeId)
			.append(this.enterpriseCode)
			.append(this.enterpriseName)
			.toHashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof GltypeEcodeRelation)){
			return false;
		}
		GltypeEcodeRelation rhs = (GltypeEcodeRelation)obj;
		return new EqualsBuilder()
			.append(this.typeEcodeId, rhs.typeEcodeId)
			.append(this.typeId, rhs.typeId)
			.append(this.enterpriseCode, rhs.enterpriseCode)
			.append(this.enterpriseName, rhs.enterpriseName)
			.isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [typeEcodeId=");
		builder.append(typeEcodeId);
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append(", enterpriseCode=");
		builder.append(enterpriseCode);
		builder.append(", enterpriseName=");
		builder.append(enterpriseName);
		builder.append("]");
		return builder.toString();
	}
	
	

}

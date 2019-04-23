/**
 * @Title: UserEnterprise.java 
 * @Package com.suneee.oa.model.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */
package com.suneee.ucp.base.model.user;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * @ClassName: UserEnterprise 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-07 14:52:38 
 *
 */
public class UserEnterprise extends UcpBaseModel{
	
	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = -1392614106877125387L;
	
	public static final Short DELETE_YES = 1;
	public static final Short DELETE_NO = 0;

	/**
	 * 用户企业关系ID 
	 */ 
	private Long userEnterpriseId;
	
	/**
	 * 用户ID
	 */ 
	private Long userId;
	
	/**
	 * 企业编码
	 */ 
	private String enterpriseCode;
	
	/**
	 * 是否删除：0=未删除，1=已删除
	 */ 
	private Short isDelete = DELETE_NO;
	
	/**
	 * 集团编码
	 */ 
	private String groupCode;

	/**
	 * @return the userEnterpriseId
	 */
	public Long getUserEnterpriseId() {
		return userEnterpriseId;
	}

	/**
	 * @param userEnterpriseId the userEnterpriseId to set
	 */
	public void setUserEnterpriseId(Long userEnterpriseId) {
		this.userEnterpriseId = userEnterpriseId;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
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

	/**
	 * @return the isDelete
	 */
	public Short getIsDelete() {
		return isDelete;
	}

	/**
	 * @param isDelete the isDelete to set
	 */
	public void setIsDelete(Short isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	/** (non-Javadoc)
	 * @Title: toString 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("userEnterpriseId", this.userEnterpriseId)
			.append("userId", this.userId)
			.append("enterpriseCode", this.enterpriseCode)
			.append("isDelete", this.isDelete)
			.append("createtime", this.getCreatetime())
			.append("createBy", this.getCreateBy())
			.append("updatetime", this.getUpdatetime())
			.append("updateBy", this.getUpdateBy())
			.toString();
	}
}

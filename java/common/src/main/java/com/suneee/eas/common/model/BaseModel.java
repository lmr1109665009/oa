/**
 * @Title: BaseModel.java 
 * @Package com.suneee.eas.common.model 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.common.model;

import java.util.Date;

/**
 * @ClassName: BaseModel 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 11:23:39 
 *
 */
public class BaseModel {
	/**
	 * 创建人
	 */ 
	protected Long createBy;
	
	/**
	 * 创建人姓名
	 */ 
	protected String createByName;
	
	/**
	 * 创建时间
	 */ 
	protected Date createTime;
	
	/**
	 * 修改人
	 */ 
	protected Long updateBy;
	
	/**
	 * 修改人姓名
	 */ 
	protected String updateByName;
	
	/**
	 * 修改时间
	 */ 
	protected Date updateTime;

	/**
	 * @return the createBy
	 */
	public Long getCreateBy() {
		return createBy;
	}

	/**
	 * @param createBy the createBy to set
	 */
	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	/**
	 * @return the createByName
	 */
	public String getCreateByName() {
		return createByName;
	}

	/**
	 * @param createByName the createByName to set
	 */
	public void setCreateByName(String createByName) {
		this.createByName = createByName;
	}

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the updateBy
	 */
	public Long getUpdateBy() {
		return updateBy;
	}

	/**
	 * @param updateBy the updateBy to set
	 */
	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}

	/**
	 * @return the updateByName
	 */
	public String getUpdateByName() {
		return updateByName;
	}

	/**
	 * @param updateByName the updateByName to set
	 */
	public void setUpdateByName(String updateByName) {
		this.updateByName = updateByName;
	}

	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}

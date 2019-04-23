package com.suneee.ucp.mh.model.codeTable;

import com.suneee.ucp.base.model.UcpBaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

/**
 * 对象功能:外部系统调用流程记录表
 * 开发公司:深圳象弈软件有限公司
 * 开发人员:pengfeng
 * 创建时间:2018-06-16
 */

public class ApiTaskHistory extends UcpBaseModel{

	/**
	 *
	 */
	private static final String serialVersionUID = "1";

	/**
	 * 设置记录ID
	 */
	private Long Id;
	/**
	 * 设置类型：
	 * 1=区域HR设置，2=部门HR设置，3=地区行政副总设置,4=多企业地区设置,5=
	 */
	private Long runId;
	/**
	 * 设置项ID
	 */
	private String processName;
	/**
	 *设置项值
	 */
	private String comeFrom;
	/**
	 *描述
	 */
	private Long createBy;
	/**
	 * 企业编码
	 */
	private Date createDate;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getRunId() {
		return runId;
	}

	public void setRunId(Long runId) {
		this.runId = runId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getComeFrom() {
		return comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}

	@Override
	public Long getCreateBy() {
		return createBy;
	}

	@Override
	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("Id", this.Id)
		.append("runId", this.runId)
		.append("processName", this.processName)
		.append("comeFrom", this.comeFrom)
		.append("createBy", this.createBy)
		.append("createDate",createDate)
		.toString();
	}
}

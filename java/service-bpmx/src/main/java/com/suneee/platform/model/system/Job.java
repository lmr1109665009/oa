package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
/**
 * 对象功能:职务表 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-11-28 16:17:48
 */
public class Job extends BaseModel
{
	public static final String NODE_KEY = "zwjb";	// 数据字典职务字段分类
	
	/**
	 * 职务类别对应的数据字典nodekey
	 */ 
	public static final String CATEGORY_NODE_KEY = "zwlb";  
	// 职务ID
	protected Long  jobid;
	// 职务名称
	protected String  jobname;
	// 职务代码
	protected String  jobcode;
	// 职务描述
	protected String  jobdesc;
	// 设置码
	protected Long  setid=0L;
	// 是否删除
	protected Long  isdelete=0L;
	// 等级
	protected Short  grade=0;
	//等级名称
	protected String gradeName;
	//职务类别
	protected Long jobCategory;
	//职务类别名称
	protected String jobCategoryName;

	/**
	 * 企业编码
	 */
	protected String enterpriseCode;

	/**
	 * 企业名称
	 */
	protected String enterpriseName;

	//职务参数列表
	protected List<JobParam> jobParamList=new ArrayList<JobParam>();
	
	public void setJobid(Long jobid) 
	{
		this.jobid = jobid;
	}
	/**
	 * 返回 职务ID
	 * @return
	 */
	public Long getJobid() 
	{
		return this.jobid;
	}
	public void setJobname(String jobname) 
	{
		this.jobname = jobname;
	}
	/**
	 * 返回 职务名称
	 * @return
	 */
	public String getJobname() 
	{
		return this.jobname;
	}
	public void setJobcode(String jobcode) 
	{
		this.jobcode = jobcode;
	}
	/**
	 * 返回 职务代码
	 * @return
	 */
	public String getJobcode() 
	{
		return this.jobcode;
	}
	public void setJobdesc(String jobdesc) 
	{
		this.jobdesc = jobdesc;
	}
	/**
	 * 返回 职务描述
	 * @return
	 */
	public String getJobdesc() 
	{
		return this.jobdesc;
	}
	public void setSetid(Long setid) 
	{
		this.setid = setid;
	}
	/**
	 * 返回 设置码
	 * @return
	 */
	public Long getSetid() 
	{
		return this.setid;
	}
	public void setIsdelete(Long isdelete) 
	{
		this.isdelete = isdelete;
	}
	/**
	 * 返回 是否删除
	 * @return
	 */
	public Long getIsdelete() 
	{
		return this.isdelete;
	}

   	public Short getGrade() {
		return grade;
	}
	public void setGrade(Short grade) {
		this.grade = grade;
	}

	public String getGradeName() { return gradeName; }
	public void setGradeName(String gradeName) { this.gradeName = gradeName; }
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

	public String getEnterpriseName() { return enterpriseName; }
	public void setEnterpriseName(String enterpriseName) { this.enterpriseName = enterpriseName; }

	public Long getJobCategory() {
		return jobCategory;
	}

	public void setJobCategory(Long jobCategory) {
		this.jobCategory = jobCategory;
	}

	public String getJobCategoryName() {
		return jobCategoryName;
	}

	public void setJobCategoryName(String jobCategoryName) {
		this.jobCategoryName = jobCategoryName;
	}

	public void setJobParamList(List<JobParam> jobParamList){
		this.jobParamList = jobParamList;
	}
	/**
	 * 返回 职务参数列表
	 * @return
	 */
	public List<JobParam> getJobParamList(){
		return this.jobParamList;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof Job)) 
		{
			return false;
		}
		Job rhs = (Job) object;
		return new EqualsBuilder()
		.append(this.jobid, rhs.jobid)
		.append(this.jobname, rhs.jobname)
		.append(this.jobcode, rhs.jobcode)
		.append(this.jobdesc, rhs.jobdesc)
		.append(this.setid, rhs.setid)
		.append(this.isdelete, rhs.isdelete)
		.append(this.grade, rhs.grade)
		.append(this.gradeName, rhs.gradeName)
		.append(this.enterpriseCode, rhs.enterpriseCode)
		.append(this.enterpriseName, rhs.enterpriseName)
		.append(this.createtime, rhs.createtime)
		.append(this.createBy, rhs.createBy)
		.append(this.updatetime, rhs.updatetime)
		.append(this.updateBy, rhs.updateBy)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.jobid) 
		.append(this.jobname) 
		.append(this.jobcode) 
		.append(this.jobdesc) 
		.append(this.setid) 
		.append(this.isdelete) 
		.append(this.grade)
		.append(this.gradeName)
		.append(this.enterpriseCode)
		.append(this.enterpriseName)
		.append(this.createtime)
		.append(this.createBy)
		.append(this.updatetime)
		.append(this.updateBy)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("jobid", this.jobid) 
		.append("jobname", this.jobname) 
		.append("jobcode", this.jobcode) 
		.append("jobdesc", this.jobdesc) 
		.append("setid", this.setid) 
		.append("isdelete", this.isdelete) 
		.append("grade", this.grade)
		.append("gradeName", this.gradeName)
		.append("enterpriseCode", this.enterpriseCode)
		.append("enterpriseName", this.enterpriseName)
		.append("createtime", this.createtime)
		.append("createBy", this.createBy)
		.append("updatetime", this.updatetime)
		.append("updateBy", this.updateBy)
		.toString();
	}
}
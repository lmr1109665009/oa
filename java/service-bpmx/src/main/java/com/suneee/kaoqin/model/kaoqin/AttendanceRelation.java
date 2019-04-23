package com.suneee.kaoqin.model.kaoqin;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * 对象功能:考勤关系映射表 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-03 11:57:25
 */
public class AttendanceRelation extends BaseModel {
	// ID
	protected Long id;
	// 用户ID
	protected Long userId;
	// 考勤编号
	protected String badgenumber;
	// 卡号
	protected String cardNo;

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
	public void setUserId(Long userId){
		this.userId = userId;
	}
	/**
	 * 返回 用户ID
	 * @return
	 */
	public Long getUserId() {
		return this.userId;
	}
	public void setBadgenumber(String badgenumber){
		this.badgenumber = badgenumber;
	}
	/**
	 * 返回 考勤编号
	 * @return
	 */
	public String getBadgenumber() {
		return this.badgenumber;
	}
	public void setCardNo(String cardNo){
		this.cardNo = cardNo;
	}
	/**
	 * 返回 卡号
	 * @return
	 */
	public String getCardNo() {
		return this.cardNo;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof AttendanceRelation)) 
		{
			return false;
		}
		AttendanceRelation rhs = (AttendanceRelation) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.userId, rhs.userId)
		.append(this.badgenumber, rhs.badgenumber)
		.append(this.cardNo, rhs.cardNo)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.userId) 
		.append(this.badgenumber) 
		.append(this.cardNo) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("userId", this.userId) 
		.append("badgenumber", this.badgenumber) 
		.append("cardNo", this.cardNo) 
		.toString();
	}
   
  

}
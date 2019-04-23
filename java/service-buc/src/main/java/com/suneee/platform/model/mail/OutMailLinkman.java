package com.suneee.platform.model.mail;

import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
/**
 * 对象功能:最近联系人 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zyp
 * 创建时间:2012-04-13 11:11:56
 */
public class OutMailLinkman extends BaseModel
{
	// linkId
	protected Long linkId;

	//用户ID
	protected Long userId;
	// 发送时间
	protected java.util.Date sendTime;
	// 联系人名称
	protected String linkName;
	// linkAddress
	protected String linkAddress;
	//
	protected Long mailId;
	public Long getMailId()
	{
		return mailId;
	}
	public void setMailId(Long mailid)
	{
		this.mailId = mailId;
	}

	//发送次数
	protected int sendTimes=0;

	public int getSendTimes()
	{
		return sendTimes;
	}
	public void setSendTimes(int sendTimes)
	{
		this.sendTimes = sendTimes;
	}
	public void setLinkId(Long linkId) 
	{
		this.linkId = linkId;
	}
	/**
	 * 返回 linkId
	 * @return
	 */
	public Long getLinkId() 
	{
		return linkId;
	}

	

	/**
	 * 返回用户Id
	 * @return
	 */
	public Long getUserId() 
	{
		return userId;
	}
	
	public void setUserId(Long userId)
	{
		this.userId = userId;
	}
	
	public void setSendTime(java.util.Date sendTime) 
	{
		this.sendTime = sendTime;
	}
	/**
	 * 返回 发送时间
	 * @return
	 */
	public java.util.Date getSendTime() 
	{
		return sendTime;
	}

	public void setLinkName(String linkName) 
	{
		this.linkName = linkName;
	}
	/**
	 * 返回 linkName
	 * @return
	 */
	public String getLinkName() 
	{
		return linkName;
	}

	public void setLinkAddress(String linkAddress) 
	{
		this.linkAddress = linkAddress;
	}
	/**
	 * 返回 linkAddress
	 * @return
	 */
	public String getLinkAddress() 
	{
		return linkAddress;
	}

   
   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof OutMailLinkman)) 
		{
			return false;
		}
		OutMailLinkman rhs = (OutMailLinkman) object;
		return new EqualsBuilder()
		.append(this.linkId, rhs.linkId)
		.append(this.sendTime, rhs.sendTime)
		.append(this.sendTimes, rhs.sendTimes)
		.append(this.linkName, rhs.linkName)
		.append(this.linkAddress, rhs.linkAddress)
		.append(this.mailId, rhs.mailId)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.linkId) 
		.append(this.sendTime)
		.append(this.sendTimes)
		.append(this.linkName) 
		.append(this.linkAddress)
		.append(this.mailId) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("linkId", this.linkId) 
		.append("sendTime", this.sendTime)
		.append("sendTimes", this.sendTimes)
		.append("linkName", this.linkName) 
		.append("linkAddress", this.linkAddress)
		.append("mailId", this.mailId)
		.toString();
	}

}
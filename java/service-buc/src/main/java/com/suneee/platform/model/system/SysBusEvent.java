package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import com.suneee.platform.xml.constant.XmlConstant;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 对象功能:sys_bus_event Model对象
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-05-22 11:14:30
 */
@XmlRootElement(name = XmlConstant.SYS_BUS_EVENT)
@XmlAccessorType(XmlAccessType.NONE)
public class SysBusEvent extends BaseModel {
	// ID
	//@XmlAttribute
	@XmlElement
	protected Long id;
	// FORMKEY
	//@XmlAttribute
	@XmlElement
	protected String formkey;
	// JS_PRE_SCRIPT
	//@XmlAttribute
	
	@XmlElement
	protected String jsPreScript;
	// js后置脚本
	//@XmlAttribute
	
	@XmlElement
	protected String jsAfterScript;
	// PRE_SCRIPT
	//@XmlAttribute
	
	@XmlElement
	protected String preScript;
	// AFTER_SCRIPT
	//@XmlAttribute
	
	@XmlElement
	protected String afterScript;

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
	public void setFormkey(String formkey){
		this.formkey = formkey;
	}
	/**
	 * 返回 FORMKEY
	 * @return
	 */
	public String getFormkey() {
		return this.formkey;
	}
	public void setJsPreScript(String jsPreScript){
		this.jsPreScript = jsPreScript;
	}
	/**
	 * 返回 JS_PRE_SCRIPT
	 * @return
	 */
	public String getJsPreScript() {
		return this.jsPreScript;
	}
	public void setJsAfterScript(String jsAfterScript){
		this.jsAfterScript = jsAfterScript;
	}
	/**
	 * 返回 js后置脚本
	 * @return
	 */
	public String getJsAfterScript() {
		return this.jsAfterScript;
	}
	public void setPreScript(String preScript){
		this.preScript = preScript;
	}
	/**
	 * 返回 PRE_SCRIPT
	 * @return
	 */
	public String getPreScript() {
		return this.preScript;
	}
	public void setAfterScript(String afterScript){
		this.afterScript = afterScript;
	}
	/**
	 * 返回 AFTER_SCRIPT
	 * @return
	 */
	public String getAfterScript() {
		return this.afterScript;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysBusEvent)) 
		{
			return false;
		}
		SysBusEvent rhs = (SysBusEvent) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.formkey, rhs.formkey)
		.append(this.jsPreScript, rhs.jsPreScript)
		.append(this.jsAfterScript, rhs.jsAfterScript)
		.append(this.preScript, rhs.preScript)
		.append(this.afterScript, rhs.afterScript)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.formkey) 
		.append(this.jsPreScript) 
		.append(this.jsAfterScript) 
		.append(this.preScript) 
		.append(this.afterScript) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("formkey", this.formkey) 
		.append("jsPreScript", this.jsPreScript) 
		.append("jsAfterScript", this.jsAfterScript) 
		.append("preScript", this.preScript) 
		.append("afterScript", this.afterScript) 
		.toString();
	}
   
  

}
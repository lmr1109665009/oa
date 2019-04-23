package com.suneee.platform.xml.system;

import com.suneee.platform.model.system.Identity;
import com.suneee.platform.xml.constant.XmlConstant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "identities")
@XmlAccessorType(XmlAccessType.FIELD)
public class IdentityXml 
{
	/**
	 * 流水号
	 */
	@XmlElement(name = XmlConstant.SYS_IDENTITY, type = Identity.class)
	private Identity identity;

	
	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	
	
}

package com.suneee.platform.xml.system;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "system")
@XmlAccessorType(XmlAccessType.FIELD)
public class IdentityXmlList 
{
	/**
	 * 流水号Xml List
	 */
	@XmlElements({ @XmlElement(name = "identities", type = IdentityXml.class) })
	private List<IdentityXml> identityXmlList;

	
	public List<IdentityXml> getIdentityXmlList() {
		return identityXmlList;
	}

	public void setIdentityXmlList(List<IdentityXml> identityXmlList) {
		this.identityXmlList = identityXmlList;
	}
	
	
	
}

package com.suneee.platform.xml.system;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "system")
@XmlAccessorType(XmlAccessType.FIELD)
public class AliasScriptXmlList 
{
	/**
	 * 别名脚本Xml List
	 */
	@XmlElements({ @XmlElement(name = "aliasScripts", type = AliasScriptXml.class)})
	private List<AliasScriptXml> aliasScriptXmlList;

	public List<AliasScriptXml> getAliasScriptXmlList() {
		return aliasScriptXmlList;
	}

	public void setAliasScriptXmlList(List<AliasScriptXml> aliasScriptXmlList) {
		this.aliasScriptXmlList = aliasScriptXmlList;
	}
	
	
}

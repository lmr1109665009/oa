package com.suneee.platform.xml.system;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "system")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonScriptXmlList
{	
	/**
	 * 人员脚本Xml List
	 */
	@XmlElements({ @XmlElement(name = "personScripts", type = PersonScriptXml.class) })
	private List<PersonScriptXml> personScriptXml;

	public List<PersonScriptXml> getPersonScriptXml() {
		return personScriptXml;
	}

	public void setPersonScriptXml(List<PersonScriptXml> personScriptXml) {
		this.personScriptXml = personScriptXml;
	}
	
	
}

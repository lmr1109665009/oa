package com.suneee.platform.xml.system;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.suneee.platform.model.system.PersonScript;
import com.suneee.platform.xml.constant.XmlConstant;
import com.suneee.platform.xml.constant.XmlConstant;

@XmlRootElement(name = "personScripts")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonScriptXml
{
	/**
	 * 人员脚本
	 */
	@XmlElement(name = XmlConstant.SYS_PERSON_SCRIPT, type = PersonScript.class)
	private PersonScript personScript;

	public PersonScript getPersonScript() {
		return personScript;
	}

	public void setPersonScript(PersonScript personScript) {
		this.personScript = personScript;
	}

	
	

}

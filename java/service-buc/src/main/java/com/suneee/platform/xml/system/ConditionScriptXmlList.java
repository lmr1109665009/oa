package com.suneee.platform.xml.system;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.suneee.platform.model.system.ConditionScript;
import com.suneee.platform.xml.constant.XmlConstant;

@XmlRootElement(name = "system")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConditionScriptXmlList
{
	/**
	 * 条件脚本Xml List
	 */
	@XmlElements({ @XmlElement(name = "conditionScripts", type = ConditionScriptXml.class) })
	private List<ConditionScriptXml> conditionScriptXmlList;

	public List<ConditionScriptXml> getConditionScriptXmlList() {
		return conditionScriptXmlList;
	}

	public void setConditionScriptXmlList(
			List<ConditionScriptXml> conditionScriptXmlList) {
		this.conditionScriptXmlList = conditionScriptXmlList;
	}
	
	

}

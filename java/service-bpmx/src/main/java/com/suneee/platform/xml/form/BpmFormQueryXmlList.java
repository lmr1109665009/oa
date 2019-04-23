package com.suneee.platform.xml.form;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bpm")
@XmlAccessorType(XmlAccessType.FIELD)
public class BpmFormQueryXmlList 
{
	/**
	 * 自定义查询Xml List
	 */
	@XmlElements({ @XmlElement(name = "formQuerys", type = BpmFormQueryXml.class) })
	private List<BpmFormQueryXml> bpmFormQueryXmlList;

	public List<BpmFormQueryXml> getBpmFormQueryXmlList() {
		return bpmFormQueryXmlList;
	}

	public void setBpmFormQueryXmlList(List<BpmFormQueryXml> bpmFormQueryXmlList) {
		this.bpmFormQueryXmlList = bpmFormQueryXmlList;
	}
	
	
}

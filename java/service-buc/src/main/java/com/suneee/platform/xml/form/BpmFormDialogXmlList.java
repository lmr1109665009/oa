package com.suneee.platform.xml.form;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.suneee.platform.xml.system.SysQuerySqlDefXml;

@XmlRootElement(name = "bpm")
@XmlAccessorType(XmlAccessType.FIELD)
public class BpmFormDialogXmlList 
{
	/**
	 * 自定义对话框XML List
	 */
	@XmlElements({ @XmlElement(name = "formDialogs", type = BpmFormDialogXml.class) })
	private List<BpmFormDialogXml> bpmFormDialogXmlList;

	
	public List<BpmFormDialogXml> getBpmFormDialogXmlList() {
		return bpmFormDialogXmlList;
	}

	public void setBpmFormDialogXmlList(List<BpmFormDialogXml> bpmFormDialogXmlList) {
		this.bpmFormDialogXmlList = bpmFormDialogXmlList;
	}
	

}

package com.suneee.platform.xml.bpm;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.suneee.platform.xml.system.SysQuerySqlDefXml;

@XmlRootElement(name = "bpm")
@XmlAccessorType(XmlAccessType.FIELD)
public class BpmCommonWsSetXmlList 
{
	/**
	 * Web服务调用配置Xml List
	 */
	
	@XmlElements({ @XmlElement(name = "commonWsSets", type = BpmCommonWsSetXml.class) })
	private List<BpmCommonWsSetXml> bpmCommonWsSetXmlList;

	public List<BpmCommonWsSetXml> getBpmCommonWsSetXmlList() {
		return bpmCommonWsSetXmlList;
	}

	public void setBpmCommonWsSetXmlList(
			List<BpmCommonWsSetXml> bpmCommonWsSetXmlList) {
		this.bpmCommonWsSetXmlList = bpmCommonWsSetXmlList;
	}
		
		

}

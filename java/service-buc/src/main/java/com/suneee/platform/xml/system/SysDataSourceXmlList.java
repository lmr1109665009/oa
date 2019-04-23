package com.suneee.platform.xml.system;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="system")
@XmlAccessorType(XmlAccessType.FIELD)
public class SysDataSourceXmlList 
{
	/**
	 * 系统数据源Xml List
	 */
	@XmlElements({@XmlElement(name="dataSources" ,type=SysDataSourceXml.class)})
	private List<SysDataSourceXml> sysDataSourceXmlList;

	public List<SysDataSourceXml> getSysDataSourceXmlList() {
		return sysDataSourceXmlList;
	}

	public void setSysDataSourceXmlList(List<SysDataSourceXml> sysDataSourceXmlList) {
		this.sysDataSourceXmlList = sysDataSourceXmlList;
	}
	
	
}

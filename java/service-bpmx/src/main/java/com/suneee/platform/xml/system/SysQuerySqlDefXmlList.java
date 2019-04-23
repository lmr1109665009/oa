package com.suneee.platform.xml.system;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.suneee.platform.model.system.SysQuerySqlDef;


@XmlRootElement(name = "querySql")
@XmlAccessorType(XmlAccessType.FIELD)
public class SysQuerySqlDefXmlList 
{
	/**
	 * Sql查询定义Xml  List
	 */
	@XmlElements({ @XmlElement(name = "querySqlDefs", type = SysQuerySqlDefXml.class) })
	private List<SysQuerySqlDefXml> sysQuerySqlDefXmlList;

	
	
	
	public List<SysQuerySqlDefXml> getSysQuerySqlDefXmlList() {
		return sysQuerySqlDefXmlList;
	}

	public void setSysQuerySqlDefXmlList(
			List<SysQuerySqlDefXml> sysQuerySqlDefXmlList) {
		this.sysQuerySqlDefXmlList = sysQuerySqlDefXmlList;
	}
	
}

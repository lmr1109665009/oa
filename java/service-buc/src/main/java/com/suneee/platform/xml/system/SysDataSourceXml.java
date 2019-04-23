package com.suneee.platform.xml.system;

import com.suneee.platform.model.system.SysDataSource;
import com.suneee.platform.xml.constant.XmlConstant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="dataSources")
@XmlAccessorType(XmlAccessType.FIELD)
public class SysDataSourceXml
{
	/**
	 * 系统数据源
	 */
	@XmlElement(name= XmlConstant.SYS_DATA_SOURCE,type=SysDataSource.class)
	private SysDataSource dataSource;

	
	
	public SysDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(SysDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	

}

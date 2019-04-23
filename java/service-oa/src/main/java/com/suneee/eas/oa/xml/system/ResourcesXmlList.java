package com.suneee.eas.oa.xml.system;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "res")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourcesXmlList {
	/**
	 * 流程定义列表
	 */
	@XmlElements({ @XmlElement(name = "resources", type = ResourcesXml.class) })
	private List<ResourcesXml> resourcesXmlList;

	public List<ResourcesXml> getResourcesXmlList() {
		if(resourcesXmlList==null){
			return new ArrayList<ResourcesXml>();
		}
		return resourcesXmlList;
	}

	public void setResourcesXmlList(List<ResourcesXml> resourcesXmlList) {
		this.resourcesXmlList = resourcesXmlList;
	}
	
}

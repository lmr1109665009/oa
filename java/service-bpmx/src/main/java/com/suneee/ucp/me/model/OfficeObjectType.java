/**  
 * @Title: OfficeObjectType.java
 * @Package com.suneee.ucp.me.model
 * @Description: TODO(用一句话描述该文件做什么)
 * @author yiwei
 * @date 2017年4月18日
 * @version V1.0  
 */
package com.suneee.ucp.me.model;

import com.suneee.core.util.StringUtil;
import com.suneee.ucp.base.model.UcpBaseModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: OfficeObjectType
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author yiwei
 * @date 2017年4月18日
 *
 */
public class OfficeObjectType extends UcpBaseModel{

	private Long id;
	private String type;
	private String objectName;
	private String specification;
	private String creator;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public static List<String> getSpecificationList(String specification){
		List<String> result = new ArrayList<String>();
		if(StringUtil.isNotEmpty(specification)){
			String[] s = specification.split("_");
			result = Arrays.asList(s);
		}
		return result;
	}
}

package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 对象功能:数据字典 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ljf
 * 创建时间:2011-11-23 11:07:27
 */
public class Dictionary extends BaseModel {
	
	//脚本类型。
	@Deprecated
	public static final String ScriptType="scriptType";
	
	// dicId
	protected Long dicId=0L;
	// 分类ID
	protected Long typeId;
	// 项Key
	protected String itemKey;
	// 项名
	protected String itemName;
	// 项值
	protected String itemValue;
	// 描述
	protected String descp;
	// 序号
	protected Long sn;
	// nodePath
	protected String nodePath;
	// parentId
	protected Long parentId;
   //companyname
	protected String companyName;
	//公司编码
	protected String eid;
	// 类型(0平铺,1树形)
	protected Integer type=0;
	
	protected String open="true";
	//用于存放字典项的子项
	protected List<Dictionary> dicList = new ArrayList<>();


	// 分类名称
	protected String typeName;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}


	public void setDicId(Long dicId) {
		this.dicId = dicId;
	}
	/**
	 * 返回 dicId
	 * @return
	 */
	public Long getDicId() {
		return dicId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	/**
	 * 返回 分类ID
	 * @return
	 */
	public Long getTypeId() {
		return typeId;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}
	/**
	 * 返回 项Key
	 * @return
	 */
	public String getItemKey() {
		return itemKey;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	/**
	 * 返回 项名
	 * @return
	 */
	public String getItemName() {
		return itemName;
	}

	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}
	/**
	 * 返回 项值
	 * @return
	 */
	public String getItemValue() {
		return itemValue;
	}

	public void setDescp(String descp) {
		this.descp = descp;
	}
	/**
	 * 返回 描述
	 * @return
	 */
	public String getDescp() {
		return descp;
	}

	public void setSn(Long sn) {
		this.sn = sn;
	}
	/**
	 * 返回 序号
	 * @return
	 */
	public Long getSn() {
		return sn;
	}

	public void setNodePath(String nodePath) {
		this.nodePath = nodePath;
	}
	/**
	 * 返回 nodePath
	 * @return
	 */
	public String getNodePath() {
		return nodePath;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	/**
	 * 返回 parentId
	 * @return
	 */
	public Long getParentId() {
		return parentId;
	}
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}

	public List<Dictionary> getDicList() { return dicList; }
	public void setDicList(List<Dictionary> dicList) { this.dicList = dicList; }

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof Dictionary)) {
			return false;
		}
		Dictionary rhs = (Dictionary) object;
		return new EqualsBuilder()
		.append(this.dicId, rhs.dicId)
		.append(this.typeId, rhs.typeId)
		.append(this.itemKey, rhs.itemKey)
		.append(this.itemName, rhs.itemName)
		.append(this.itemValue, rhs.itemValue)
		.append(this.descp, rhs.descp)
		.append(this.sn, rhs.sn)
		.append(this.nodePath, rhs.nodePath)
		.append(this.parentId, rhs.parentId)
		.append(this.eid, rhs.eid)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.dicId) 
		.append(this.typeId) 
		.append(this.itemKey) 
		.append(this.itemName) 
		.append(this.itemValue) 
		.append(this.descp) 
		.append(this.sn) 
		.append(this.nodePath) 
		.append(this.parentId) 
		.append(this.eid)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this)
		.append("dicId", this.dicId) 
		.append("typeId", this.typeId) 
		.append("itemKey", this.itemKey) 
		.append("itemName", this.itemName) 
		.append("itemValue", this.itemValue) 
		.append("descp", this.descp) 
		.append("sn", this.sn) 
		.append("nodePath", this.nodePath) 
		.append("parentId", this.parentId)
		.append("eid",this.eid)
		.toString();
	}
   
  

}
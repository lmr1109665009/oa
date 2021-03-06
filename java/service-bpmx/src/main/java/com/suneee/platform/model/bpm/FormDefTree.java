package com.suneee.platform.model.bpm;

import com.suneee.core.model.BaseModel;
import com.suneee.platform.xml.constant.XmlConstant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 对象功能:表单的树展现配置 开发公司:广州宏天软件有限公司 开发人员:liyj 创建时间:2015-05-12 14:46:20
 */
@XmlRootElement(name = XmlConstant.FORM_DEF_TREE)
@XmlAccessorType(XmlAccessType.NONE)
public class FormDefTree extends BaseModel {
	/**
	 * 同步加载
	 */
	public static Short LOADTYPE_SYNC = 0;
	/**
	 * 异步加载
	 */
	public static Short LOADTYPE_ASYNC = 1;

	// ID
	//@XmlAttribute
	@XmlElement
	protected Long id;
	
	
	// FORM_DEF_ID
	//@XmlAttribute
	@XmlElement
	protected Long formDefId;
	// NAME
	//@XmlAttribute
	@XmlElement
	protected String name;
	// ALIAS
	//@XmlAttribute
	@XmlElement
	protected String alias;
	protected String formKey="";

	// TREE_ID
	//@XmlAttribute
	@XmlElement
	protected String treeId;
	// PARENT_ID
	//@XmlAttribute
	@XmlElement
	protected String parentId;
	// DISPLAY_ID
	//@XmlAttribute
	@XmlElement
	protected String displayField;
	// 加载方式
	//@XmlAttribute
	@XmlElement
	protected Short loadType;
	//异步加载时根节点ID
	//@XmlAttribute
	@XmlElement
	protected String rootId;
	
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 返回 ID
	 * 
	 * @return
	 */
	public Long getId() {
		return this.id;
	}

	public void setFormDefId(Long formDefId) {
		this.formDefId = formDefId;
	}

	/**
	 * 返回 FORM_DEF_ID
	 * 
	 * @return
	 */
	public Long getFormDefId() {
		return this.formDefId;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 返回 NAME
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * 返回 ALIAS
	 * 
	 * @return
	 */
	public String getAlias() {
		return this.alias;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

	/**
	 * 返回 TREE_ID
	 * 
	 * @return
	 */
	public String getTreeId() {
		return this.treeId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * 返回 PARENT_ID
	 * 
	 * @return
	 */
	public String getParentId() {
		return this.parentId;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	/**
	 * 返回 DISPLAY_ID
	 * 
	 * @return
	 */
	public String getDisplayField() {
		return this.displayField;
	}

	/**
	 * loadType
	 * 
	 * @return the loadType
	 * @since 1.0.0
	 */

	public Short getLoadType() {
		return loadType;
	}

	/**
	 * @param loadType
	 *            the loadType to set
	 */
	public void setLoadType(Short loadType) {
		this.loadType = loadType;
	}

	/**
	 * rootId
	 * @return  the rootId
	 * @since   1.0.0
	 */
	
	public String getRootId() {
		return rootId;
	}

	/**
	 * @param rootId the rootId to set
	 */
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

}
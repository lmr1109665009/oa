package com.suneee.platform.model.bpm;

import com.suneee.core.model.BaseModel;

import javax.xml.bind.annotation.*;

/**
 * 对象功能:bpm_nodemsg_template 
 * Model对象 开发公司:广州宏天软件有限公司 
 * 开发人员:liyj 
 * 创建时间:2015-05-27 18:03:14
 */
@XmlRootElement(name = "nodeMsgTemplate")
@XmlAccessorType(XmlAccessType.NONE)
public class NodeMsgTemplate extends BaseModel {
	// ID
	@XmlAttribute
	protected Long id;
	// DEFID
	@XmlAttribute
	protected Long defId;
	// PARENTDEFID
	@XmlAttribute
	protected Long parentDefId;
	// NODEID
	@XmlAttribute
	protected String nodeId;
	// TITLE
	@XmlAttribute
	protected String title;
	// HTML
	@XmlElement
	protected String html;
	// TEXT
	@XmlElement
	protected String text;

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

	/**
	 * defId
	 * 
	 * @return the defId
	 * @since 1.0.0
	 */

	public Long getDefId() {
		return defId;
	}

	/**
	 * @param defId
	 *            the defId to set
	 */
	public void setDefId(Long defId) {
		this.defId = defId;
	}

	/**
	 * parentDefId
	 * 
	 * @return the parentDefId
	 * @since 1.0.0
	 */

	public Long getParentDefId() {
		return parentDefId;
	}

	/**
	 * @param parentDefId
	 *            the parentDefId to set
	 */
	public void setParentDefId(Long parentDefId) {
		this.parentDefId = parentDefId;
	}

	/**
	 * nodeId
	 * 
	 * @return the nodeId
	 * @since 1.0.0
	 */

	public String getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId
	 *            the nodeId to set
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 返回 TITLE
	 * 
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	/**
	 * 返回 HTML
	 * 
	 * @return
	 */
	public String getHtml() {
		return this.html;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 返回 TEXT
	 * 
	 * @return
	 */
	public String getText() {
		return this.text;
	}

}
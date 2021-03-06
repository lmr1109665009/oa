package com.suneee.platform.model.form;

import com.suneee.core.model.BaseModel;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.service.form.BpmFormDialogService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 对象功能:bpm_form_dialog_combinate Model对象 开发公司:广州宏天软件有限公司 开发人员:liyj 创建时间:2015-05-06 11:36:18
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "dialogCombinate")
@XmlAccessorType(XmlAccessType.NONE)
public class BpmFormDialogCombinate extends BaseModel {
	// ID
	@XmlElement
	protected Long id;
	@XmlElement
	protected String name;
	@XmlElement
	protected String alias;
	@XmlElement
	protected int width;
	@XmlElement
	protected int height;
	// 左边树对话框ID
	@XmlElement
	protected Long treeDialogId;
	// TREE_DIALOG_NAME
	@XmlElement
	protected String treeDialogName;
	// 右边列对话框ID
	@XmlElement
	protected Long listDialogId;
	// LIST_DIALOG_NAME
	@XmlElement
	protected String listDialogName;
	// 树数据返回数据对应列表数据的查询条件
	@XmlElement
	protected String field;
	@XmlElement
	protected BpmFormDialog treeDialog;
	@XmlElement
	protected BpmFormDialog listDialog;

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
	 * name
	 * 
	 * @return the name
	 * @since 1.0.0
	 */

	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * alias
	 * 
	 * @return the alias
	 * @since 1.0.0
	 */

	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * width
	 * 
	 * @return the width
	 * @since 1.0.0
	 */

	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * height
	 * 
	 * @return the height
	 * @since 1.0.0
	 */

	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	public void setTreeDialogId(Long treeDialogId) {
		this.treeDialogId = treeDialogId;
	}

	/**
	 * 返回 TREE_DIALOG_ID
	 * 
	 * @return
	 */
	public Long getTreeDialogId() {
		return this.treeDialogId;
	}

	public void setTreeDialogName(String treeDialogName) {
		this.treeDialogName = treeDialogName;
	}

	/**
	 * 返回 TREE_DIALOG_NAME
	 * 
	 * @return
	 */
	public String getTreeDialogName() {
		return this.treeDialogName;
	}

	public void setListDialogId(Long listDialogId) {
		this.listDialogId = listDialogId;
	}

	/**
	 * 返回 LIST_DIALOG_ID
	 * 
	 * @return
	 */
	public Long getListDialogId() {
		return this.listDialogId;
	}

	public void setListDialogName(String listDialogName) {
		this.listDialogName = listDialogName;
	}

	/**
	 * 返回 LIST_DIALOG_NAME
	 * 
	 * @return
	 */
	public String getListDialogName() {
		return this.listDialogName;
	}

	public void setField(String field) {
		this.field = field;
	}

	/**
	 * 返回 树数据返回数据对应列表数据的查询条件
	 * 
	 * @return
	 */
	public String getField() {
		return this.field;
	}

	/**
	 * treeDialog
	 * 
	 * @return the treeDialog
	 * @since 1.0.0
	 */

	public BpmFormDialog getTreeDialog() {
		if(treeDialog==null){
			treeDialog= AppUtil.getBean(BpmFormDialogService.class).getById(treeDialogId);
		}
		return treeDialog;
	}

	/**
	 * listDialog
	 * 
	 * @return the listDialog
	 * @since 1.0.0
	 */

	public BpmFormDialog getListDialog() {
		if(listDialog==null){
			listDialog=AppUtil.getBean(BpmFormDialogService.class).getById(listDialogId);
		}
		return listDialog;
	}


}
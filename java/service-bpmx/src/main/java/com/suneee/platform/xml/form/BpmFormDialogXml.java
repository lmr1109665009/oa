package com.suneee.platform.xml.form;

import com.suneee.platform.model.form.BpmFormDialog;
import com.suneee.platform.model.form.BpmFormDialogCombinate;
import com.suneee.platform.xml.constant.XmlConstant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "formDialogs")
@XmlAccessorType(XmlAccessType.FIELD)
public class BpmFormDialogXml 
{
	/**
	 *自定义对话框
	 */
	@XmlElement(name = XmlConstant.BPM_FORM_DIALOG, type = BpmFormDialog.class)
	private BpmFormDialog bpmFormDialog;
	/**
	 * 组合对话框
	 */
	@XmlElement(name = XmlConstant.BPM_FORM_DIALOG_COMBINATE, type = BpmFormDialogCombinate.class)
	private BpmFormDialogCombinate bpmFormDialogCombinate;
	
	
	
	public BpmFormDialog getBpmFormDialog() {
		return bpmFormDialog;
	}
	
	public void setBpmFormDialog(BpmFormDialog bpmFormDialog) {
		this.bpmFormDialog = bpmFormDialog;
	}
	public BpmFormDialogCombinate getBpmFormDialogCombinate() {
		return bpmFormDialogCombinate;
	}
	public void setBpmFormDialogCombinate(
			BpmFormDialogCombinate bpmFormDialogCombinate) {
		this.bpmFormDialogCombinate = bpmFormDialogCombinate;
	}
	
	
}

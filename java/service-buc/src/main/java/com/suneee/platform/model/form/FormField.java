package com.suneee.platform.model.form;

/**
 * 字段信息
 * @author zxh
 *
 */
public class FormField {
	//字段名
	private String fieldName;
	//字段描述
	private String fieldDesc;
	//是否显示
	private Short isShow =0;
	//排序
	private int sn =1;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldDesc() {
		return fieldDesc;
	}
	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}
	public Short getIsShow() {
		return isShow;
	}
	public void setIsShow(Short isShow) {
		this.isShow = isShow;
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	
}

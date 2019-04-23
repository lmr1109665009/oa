package com.suneee.platform.service.form;

import com.suneee.platform.model.form.BpmFormField;

/**
 * 新增表单数据时计算表单的初始值。
 * @author ray
 *
 */
public interface IFormDataCalculate {
	
	/**
	 * 计算表单初始值。
	 * @param bpmFormField		字段定义
	 * @param bpmFormData		表单数据
	 * @param isPreView			是否预览
	 * @return
	 */
	Object calcShowForm(BpmFormField bpmFormField, boolean isPreView);
}

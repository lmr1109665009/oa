package com.suneee.platform.tag;

import java.util.List;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.bpm.BpmDefVar;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.service.bpm.BpmDefVarService;
import com.suneee.platform.service.form.BpmFormFieldService;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.StringUtil;

/**
 * 流程变量标签。
 * <pre>
 * 功能获取与流程绑定表单的流程变量列表。
 * 权限标签的写法:&lt;f:flowVar defId="${defId}" controlName="控件名称" change="change(this);"/>
 * </pre>
 */
public class FlowVarsTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long defId=0L;
	
	private String controlName="flowVars";
	private String change="";
	private String parentActDefId="";
	
	/**
	 * change事件。
	 * @param change
	 */
	public void setChange(String change) {
		this.change = change;
	}

	/**
	 * 设置流程定义ID
	 * @param defId
	 */
	public void setDefId(Long defId) {
		this.defId = defId;
	}
	
	/**
	 * 下拉框控件的name属性。
	 * @param controlName
	 */
	public void setControlName(String controlName) {
		this.controlName = controlName;
	}
	
	/**
	 * 父流程定义ID
	 * @param parentActDefId
	 */
	public void setParentActDefId(String parentActDefId){
		this.parentActDefId = parentActDefId;
	}

	public int doStartTag() throws JspTagException {
		return EVAL_BODY_BUFFERED;
	}
	
	/**
	 * 根据流程定义ID获取流程变量。
	 * @return
	 */
	private String getFlowVars(){
		if(defId==0){
			return "未定义";
		}
		BpmFormFieldService bpmFormFieldService=(BpmFormFieldService) AppUtil.getBean(BpmFormFieldService.class);
		BpmDefVarService bpmDefVarService=(BpmDefVarService)AppUtil.getBean(BpmDefVarService.class);
		List<BpmFormField> fieldList = null;
		if(StringUtil.isEmpty(parentActDefId)){
			fieldList= bpmFormFieldService.getFlowVarByFlowDefId(defId);
		}else{
			fieldList= bpmFormFieldService.getFlowVarByFlowDefId(defId, parentActDefId);
		}
		List<BpmDefVar> bpmdefVars= bpmDefVarService.getVarsByFlowDefId(defId);
		StringBuffer sb=new StringBuffer();
		sb.append("<select name='"+this.controlName+"'" );
		if(StringUtil.isNotEmpty(change)){
			sb.append(" onchange=\"" + this.change+ "\"");
		}
		sb.append(">");
		sb.append("<option value=''>请选择</option>");
		for(BpmFormField field:fieldList){
				sb.append("<option value='"+field.getFieldName()+"'>"+field.getFieldDesc()+"</option>");
		}
		for (BpmDefVar defVar:bpmdefVars) {
			if (defVar!=null) {
				sb.append("<option value='"+defVar.getVarKey()+"'>"+defVar.getVarName()+"</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}
	
	
	
	public int doEndTag() throws JspTagException {

		try {
			String str = getFlowVars();
			pageContext.getOut().print(str);
		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		return SKIP_BODY;
	}

}

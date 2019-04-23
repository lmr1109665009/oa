package com.suneee.platform.tag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.suneee.platform.model.ats.AtsConstant;
import com.suneee.platform.service.ats.AtsUtils;

public class AtsAttenceTypeTag extends BodyTagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspTagException {
		return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() throws JspTagException {

		try {
			//String str = AtsUtils.getAttenceType(this.val,"、",false);
			pageContext.getOut().print("");
		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		return SKIP_BODY;
	}

	

	

	private String val;

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

}

package com.suneee.platform.web.tag;

import com.suneee.platform.service.system.SubSystemUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 根据权限是否内部内容。<br>
 * <pre>
 * &lt;f:block alias="alias">控制的块。 &lt;/f:block>
 * </pre>
 * @author ray
 *
 */
public class BlockTag extends BodyTagSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Log logger = LogFactory.getLog(AnchorTag.class);
	
	private String alias="";
	
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int doEndTag() throws JspTagException {

		try {
			String systemAlias=SubSystemUtil.getCurrentSystemAlias((HttpServletRequest) pageContext.getRequest());
			boolean canAccess=true;
			if(canAccess){
				String body = this.getBodyContent().getString();
				pageContext.getOut().print(body);
			}
			else{
				logger.debug("没有权限访问:" + alias);
			}
			
			
		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		return SKIP_BODY;
	}

}

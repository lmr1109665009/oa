/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser
 * 文件名：DanhwbkParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-12-11-下午2:49:51
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser.table;

import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <pre> 
 * 描述：附件控件
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class AttachmentTableParser extends TableAbstractParser{
	@Override
	public Object parse(ParserParam param, Element element) {
		Element inputEl=element.after("<input type=\"text\"/>").nextElementSibling();
		Attributes attrs=element.attributes();
		for (Attribute attr:attrs){
			if ("parser".equals(attr.getKey())||"src".equals(attr.getKey().toLowerCase())){
				continue;
			}
			if ("title".equals(attr.getKey().toLowerCase())){
				inputEl.attr("lablename",attr.getValue());
				continue;
			}
			inputEl.attr(attr.getKey(),attr.getValue());
		}
		element.remove();
		element=inputEl;

		BpmFormData data  = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();
		String val = getValue(element, data);
		String right = getRight(element, permission);
		
		element.val(val);
		element.attr("right",right);
		
		if (FieldRight.Y.equals(right)) {//隐藏
			element.remove();
		}
		
		return null;
	}
}

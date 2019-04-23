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

import java.util.Map;

import com.suneee.platform.model.form.BpmFormData;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre> 
 * 描述：多选框
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class TextAreaTableParser extends TableAbstractParser{
	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();
		String val = getValue(element, data);
		String right = getRight(element, permission);

		if (FieldRight.W.equals(right)) {//编辑
			element.val(val);
		} else if (FieldRight.R.equals(right)) {//只读
			val = val.replaceAll("\n", "<br/>");
			element.after(val);
			element.remove();
		} else if (FieldRight.B.equals(right)) {//必填
			element.val(val);
			addRequred(element);
		} else if (FieldRight.RP.equals(right)) {//只读提交
			String content=val.replaceAll("\n", "<br/>");
			element.after(content);
			element.val(val);
			element.attr("class","hidden");
			element.attr("readonly", "readonly");
			element.attr("onclick", "return false;");
		} else {//没有就代表隐藏
			element.remove();
		}
		return null;
	}
}

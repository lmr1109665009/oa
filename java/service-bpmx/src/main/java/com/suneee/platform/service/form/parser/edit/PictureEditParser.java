/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser
 * 文件名：DanhwbkParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-12-11-下午2:49:51
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser.edit;

import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <pre>
 * 描述：单行文本框
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class PictureEditParser extends EditAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();

		String ename = getElementName(element, data);
		String val = getValue(element, data);
		String right = getRight(element, permission);

		if (!FieldRight.Y.getVal().equals(right)) {//非隐藏权限
			Element inputE = element.select("input").get(0);
			inputE.attr("type", "hidden");
			inputE.attr("class", "hidden");
			inputE.attr("name", ename);
			inputE.attr("controltype", "pictureShow");
			inputE.val(val);
			inputE.attr("right", right);
			element.after(inputE.toString());
		}
		element.remove();

		return null;
	}
}

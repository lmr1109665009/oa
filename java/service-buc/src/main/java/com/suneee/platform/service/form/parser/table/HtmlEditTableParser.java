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
import com.suneee.platform.service.form.parser.table.TableAbstractParser;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;
import com.suneee.platform.model.form.BpmFormData;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <pre>
 * 描述：HTML解析本框/多行文本框(HTML)
 * 构建组：转子链
 * 作者：子华
 * 邮箱:zihua@suneee.com
 * 日期:2017-11-22
 * 版权：深圳象羿软件技术公司
 * </pre>
 */
@Service
public class HtmlEditTableParser extends TableAbstractParser {
	//文件访问URL地址
	@Value("#{configProperties['user.webSign.url']}")
	private String staticUrl;

	private String getStaticUrl() {
		if (staticUrl==null){
			return "";
		}
		return staticUrl;
	}

	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();
		String val = getValue(element, data);
		String right = getRight(element, permission);
		boolean isNeedRemove=false;

		if (FieldRight.R.equals(right)||FieldRight.W.equals(right)||FieldRight.B.equals(right)) {//只读/编辑/必填模式

		} else if (FieldRight.RP.equals(right)) {//只读提交

		} else {//没有就代表隐藏
			isNeedRemove=true;
		}
		if (isNeedRemove){
			element.remove();
			return null;
		}
		Element spanContent=element.after("<span></span>").nextElementSibling();
		val=val.replaceAll("\\{\\{staticUrl\\}\\}",getStaticUrl());
		val = val.replaceAll("\n\t", "<br/>");
		val = val.replaceAll("\n", "<br/>");
		//textarea转span标签
		Attributes attrs=element.attributes();
		for (Attribute attr:attrs){
			if ("parser".equals(attr.getKey())){
				continue;
			}
			if ("height".equals(attr.getKey().toLowerCase())){
				spanContent.attr("min-height",attr.getValue());
				continue;
			}
			spanContent.attr(attr.getKey(),attr.getValue());
		}
		spanContent.html(val);
		element.remove();
		return null;
	}
}

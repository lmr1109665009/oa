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

import com.suneee.core.util.StringUtil;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

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
public class TeamTableParser extends TableAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {
		Map<String, Object> permission = param.getPermission();
		String right = getRight(element, permission);
		if(StringUtil.isEmpty(right)){
			element.remove();
		}
		return null;
	}
	
	public String getRight(Element element, Map<String, Object> permission){
		Map<String,String> teamPermission = (Map<String, String>) permission.get("teamPermission");
		String id = element.attr("id");
		String right = teamPermission.get(id);
		return right;
	}
	
}

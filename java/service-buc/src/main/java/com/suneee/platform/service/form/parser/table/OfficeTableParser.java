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

import java.util.List;
import java.util.Map;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：隐藏域
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class OfficeTableParser extends TableAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {
		Element inputEl=element.after("<input type=\"hidden\"/>").nextElementSibling();
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

		Map<String, Object> permission = param.getPermission();
		BpmFormData data =  param.getBpmFormData();
		String val = getValue(element, data);
		String right = getRight(element, permission);
		String filedName= element.attr("name");
		if(filedName.split(":")[0].equals("m")) {
			List <BpmFormField> list=data.getBpmFormTable().getFieldList();
			for (BpmFormField bpmFormField : list) {
				if(bpmFormField.getFieldName().equals(filedName.split(":")[2])) {
					String ctlProperty = bpmFormField.getCtlProperty();
					if(StringUtil.isNotEmpty(ctlProperty)) {
						JSONObject json = JSONObject.parseObject(ctlProperty);
						if(BeanUtils.isNotEmpty(json.get("associated_fields")))
						element.attr("associated_fields" , json.get("associated_fields").toString());
					}
				}
			}
		}
		if (FieldRight.Y.getVal().equals(right)) {//隐藏权限
			element.remove();
		}else{
			element.val(val);
			element.attr("right", right);
		}
		return null;
	}
}

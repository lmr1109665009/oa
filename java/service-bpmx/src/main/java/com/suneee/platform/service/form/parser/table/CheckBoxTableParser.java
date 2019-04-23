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
import com.suneee.platform.model.form.BpmFormData;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：复选框
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class CheckBoxTableParser extends TableAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();
		String val = getValue(element, data);
		String right = getRight(element, permission);
		
		Elements labelEs = element.select("label");
		String text="";//主要用于只读时显示的内容
		for (Element e : labelEs) {
			Elements elementList= e.select("input");
			if (elementList==null||elementList.isEmpty()){
				continue;
			}
			Element checkboxE=elementList.get(0);
			if(StringUtil.contain(val.split(","), checkboxE.val(), false)){
				checkboxE.attr("checked", "checked");
				if(text!="") text+=",";
				text+=e.text();
			}else{
				checkboxE.removeAttr("checked");
			}
			if (FieldRight.W.equals(right) || FieldRight.B.equals(right)) {//编辑或必填时
				element.before(e);
			}
			if (FieldRight.B.equals(right)) {//必填
				addRequred(checkboxE);
			}
			if (FieldRight.R.equals(right)) {//只读
				checkboxE.attr("disabled", "disabled");
				element.before(e);
			}
			if (FieldRight.RP.equals(right)) {//只读提交
				checkboxE.attr("onclick", "return false;");
				element.before(e);
			}
		}
//		if (FieldRight.R.equals(right)) {//只读
//			element.after(text);
//		}
//		if (FieldRight.RP.equals(right)) {//只读提交
//			//提交隐藏字段
//			Element emt = new Element(Tag.valueOf("input"), element.baseUri());
//			emt.attr("type","hidden");
//			emt.val(val);
//			emt.attr("name", element.attr("name"));
//			element.after(emt.toString());
//			
//			element.after(text);
//		}
		
		element.remove();
		return null;
	}
	
	@Override
	public String getValue(Element element, BpmFormData data) {
		return super.getValue(element, data);
	}
}

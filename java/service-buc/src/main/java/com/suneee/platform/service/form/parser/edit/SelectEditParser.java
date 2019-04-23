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

import java.util.Map;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：下拉框
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class SelectEditParser extends EditAbstractParser {
	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();

		String ename = getElementName(element, data);
		String val = getValue(element, data);
		String right = getRight(element, permission);
		
		Element selectE = element.select("select").get(0).attr("name", ename);
		selectE.attr("right",right);
		selectE.attr("val",val);
		Elements opts = element.select("option");
		opts.removeAttr("selected");
		
		BpmFormField bpmFormField = getField(element, data);
		
		Element targetOpt = null;
		for (Element opt : opts) {
			if (valEqual(val, opt.val(), bpmFormField)) {
				targetOpt=opt;
				break;
			}
		}
		
		if (FieldRight.W.equals(right)||FieldRight.B.equals(right)) {//编辑
			if(targetOpt!=null){
				targetOpt.attr("selected", "selected");
			}
			element.after(selectE);
		} else if (FieldRight.R.equals(right)) {//只读
			if(targetOpt!=null){
				element.after(targetOpt.text());
			}else if(selectE.hasAttr("selectquery")&& StringUtil.isNotEmpty(val)){//只读但是有值同时配置了selectquery
				element.after(selectE);
			}
		}else if (FieldRight.RP.equals(right)) {//只读提交
			//提交隐藏字段
			Element emt = new Element(Tag.valueOf("input"), element.baseUri());
			emt.attr("type","hidden");
			emt.val(val);
			emt.attr("name", ename);
			element.after(emt.toString());
			
			if(targetOpt!=null){
				element.after(targetOpt.text());
			}else if(selectE.hasAttr("selectquery")&&StringUtil.isNotEmpty(val)){//只读但是有值同时配置了selectquery
				element.after(selectE);
			}
		}
		
		element.remove();
		return null;
	}
}

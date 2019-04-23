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
import com.suneee.platform.model.form.BpmFormField;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
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
public class SelectTableParser extends TableAbstractParser{
	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();
		
		String val = getValue(element, data);
		String right = getRight(element, permission);
		element.attr("right",right);//for selectquery
		element.attr("val",val);//for selectquery
		BpmFormField bpmFormField = getField(element, data);
		//获取option
		Elements opts = element.select("option");
		Element targetOpt = null;
		for(Element opt : opts){
			if(valEqual(opt.val(), val, bpmFormField)){
				targetOpt = opt;
				break;
			}
		}
		
		if(targetOpt!=null){
			targetOpt.attr("selected", "selected");
		}
		
		if (FieldRight.W.equals(right)) {//编辑
			if(targetOpt!=null){
				targetOpt.attr("selected", "selected");
			}
		} else if (FieldRight.R.equals(right)) {//只读
			if(targetOpt!=null){
				element.after(targetOpt.text());
				element.remove();
			}else if(!element.hasAttr("selectquery")|| StringUtil.isEmpty(val)){//有selectquery配置不删除同时有设值
				element.remove();
			}
		} else if (FieldRight.B.equals(right)) {//必填
			if(targetOpt!=null){
				targetOpt.attr("selected", "selected");
			}
			addRequred(element);
		} else if (FieldRight.RP.equals(right)) {//只读提交
			//提交隐藏字段
			Element emt = new Element(Tag.valueOf("input"), element.baseUri());
			emt.attr("type","hidden");
			emt.val(val);
			emt.attr("name", element.attr("name"));
			element.after(emt.toString());

			if(targetOpt!=null){
				element.after(targetOpt.text());
				element.remove();
			}else if(!element.hasAttr("selectquery")||StringUtil.isEmpty(val)){//有selectquery配置同时有设值才不删除
				element.remove();
			}
		} else{
			Element emt = new Element(Tag.valueOf("input"), element.baseUri());
			emt.attr("type","hidden");
			emt.val(val);
			emt.attr("name", element.attr("name"));
			element.after(emt.toString());
			element.remove();
		}
		
		return null;
	}
}

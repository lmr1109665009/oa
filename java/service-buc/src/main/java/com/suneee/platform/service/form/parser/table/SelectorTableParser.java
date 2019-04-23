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
import org.springframework.stereotype.Service;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：各种选择器，人员，角色，岗位...
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class SelectorTableParser extends TableAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {
		element.removeAttr("showcuruser");
		
		BpmFormData data = param.getBpmFormData();
		String ename = element.attr("name");
		String fieldName = ename.split(":")[2];
		String idFieldName = element.attr("refid");//id字段名称
		if (StringUtil.isEmpty(idFieldName)) {//为空设一个默认
			idFieldName = fieldName + "ID";
		} else {
			idFieldName = idFieldName.split(":")[2];
		}
		
		String idVal = getValue(element, data, idFieldName,false,"");//ID的值
		String val = getValue(element, data);//显示

		Map<String, Object> permission = param.getPermission();
		String right = getRight(element, permission); 

		if (FieldRight.W.equals(right)) {//编辑
			element.val(val);
			element.attr("initvalue", idVal);
//			element.removeAttr("readonly");
		} else if (FieldRight.R.equals(right)) {//只读
			element.after("<div><span name=\""+ename+"\">"+val+"</span></div>");
			element.remove();
		} else if (FieldRight.B.equals(right)) {//必填
			element.val(val);
			element.attr("initvalue", idVal);
//			element.removeAttr("readonly");
			addRequred(element);
		}else if (FieldRight.RP.equals(right)) {//只读提交
			element.after(val);
			element.val(val);
			element.attr("initvalue", idVal);
			element.attr("type","hidden");
		} else {//没有就代表隐藏
			element.remove();
		}

		return null;
	}
}

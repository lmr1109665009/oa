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
public class ProInstTableParser extends TableAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {
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
		String[] valarr = val.split(",");
		String[] idValarr = idVal.split(",");


		Map<String, Object> permission = param.getPermission();
		String right = getRight(element, permission); 

		if (FieldRight.W.equals(right)) {//编辑
			element.val(val);
			element.attr("initvalue", idVal);
			element.before("<input name=\""+ename+"ID\" type=\"hidden\" class=\"hidden\" value=\""+idVal+"\" />");
			element.after("<a href=\"javascript:;\" class=\"link actInsts\" atype=\"select\" name=\""+ename+"\" >选择</a>");
			element.after("<a href=\"javascript:;\" class=\"link reset\" atype=\"select\" name=\""+ename+"\" >重置</a>");
		} else if (FieldRight.R.equals(right)) {//只读
			for (int i=0;i<idValarr.length;i++) {
				element.after("<p><a href=\"javascript:;\" style=\"color:#000;\" runid=\""+idValarr[i]+"\" onclick=\"openProRun(this);\">"+valarr[i]+"</a></p>");
			}

			element.remove();
		} else if (FieldRight.B.equals(right)) {//必填
			element.val(val);
			element.attr("initvalue", idVal);
			addRequred(element);
			element.before("<input name=\""+ename+"ID\" type=\"hidden\" class=\"hidden\" value=\""+idVal+"\" />");
			element.after("<a href=\"javascript:;\" class=\"link actInsts\" atype=\"select\" name=\""+ename+"\" >选择</a>");
			element.after("<a href=\"javascript:;\" class=\"link reset\" atype=\"select\" name=\""+ename+"\" >重置</a>");
		}else if (FieldRight.RP.equals(right)) {//只读提交
			element.before("<input name=\""+ename+"ID\" type=\"hidden\" class=\"hidden\" value=\""+idVal+"\" />");
			for (int i=0;i<idValarr.length;i++) {
				element.after("<p><a href=\"javascript:;\" style=\"color:#000;\" runid=\""+idValarr[i]+"\" onclick=\"openProRun(this);\">"+valarr[i]+"</a></p>");
			}
			element.remove();
		} else {//没有就代表隐藏
			element.remove();
		}

		return null;
	}
}

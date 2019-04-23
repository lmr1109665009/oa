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

import com.suneee.core.util.MapUtil;
import com.suneee.platform.model.form.BpmFormData;
import net.sf.json.JSONObject;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.stereotype.Service;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.MapUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：流程引用控件解析
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class ProInstEditParser extends EditAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {

		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();
		Map<String, Object> external = handleExternal(element);
		String fieldName = MapUtil.getString(external, "name");
		String ename = getElementName(element, data);

		String nameVal = getValue(element, data);

		String right = getRight(element, permission);
		if (FieldRight.W.equals(right) || FieldRight.B.equals(right)) {// 编辑
			String idVal = getValue(element, data, fieldName + "ID", false, "");
			Element inputE = element.select("input").get(0);
			inputE.attr("ctlType", "selector");
			inputE.attr("class", "actInsts");
			inputE.attr("name", ename);
			inputE.attr("lablename", fieldName);
			inputE.attr("readonly", "readonly");
			inputE.val(nameVal);
			inputE.attr("initvalue", idVal);
			inputE.attr("right", right);
			if (FieldRight.B.equals(right)) {
				addRequred(inputE);
			}
			element.after(inputE.toString());
		} else if (FieldRight.R.equals(right)) {// 只读
			element.after(nameVal);
		} else if (FieldRight.RP.equals(right)) {// 只读提交
			element.after(nameVal);
			// 构建两个NAME和ID隐藏字段
			Element idE = new Element(Tag.valueOf("input"), element.baseUri());
			idE.attr("type", "hidden");
			String idVal = getValue(element, data, fieldName + "ID", false, "");
			idE.val(idVal);
			idE.attr("name", ename+ "ID");

			Element nameE = new Element(Tag.valueOf("input"), element.baseUri());
			nameE.attr("type", "hidden");
			nameE.val(nameVal);
			nameE.attr("name", ename);

			element.after(idE.toString());
			element.after(nameE.toString());
		}else {//隐藏
		}
		element.remove();
		return null;
	}
}

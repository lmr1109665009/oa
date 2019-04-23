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

import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.MapUtil;
import com.suneee.platform.model.form.BpmFormData;
import net.sf.json.JSONObject;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.stereotype.Service;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.MapUtil;
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
public class SelectorEditParser extends EditAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();
		Map<String, Object> external = handleExternal(element);
		String fieldName = MapUtil.getString(external, "name");
		String ename = getElementName(element, data);
		
		String idVal = getValue(element, data, fieldName + "ID", false, "");
		String nameVal = getValue(element, data);

		String right = getRight(element, permission);
		if (FieldRight.W.equals(right) || FieldRight.B.equals(right)) {//编辑
			
			Element inputE = element.select("input").get(0);
			inputE.attr("ctlType", "selector");
			String _class = element.select("a").get(0).attr("class").replace("link ", "");
			if (MapUtil.getFloat(external, "singleselect") != 1) {//不是单选就加s样式代表多选
				_class += "s";
			}
			inputE.attr("class", _class);
			inputE.attr("name", ename);
			inputE.attr("lablename", fieldName);
			//inputE.attr("readonly", "readonly");
			
			if(BeanUtils.isNotEmpty(MapUtil.get(external, "scope"))){
				inputE.attr("scope", JSONObject.fromObject(MapUtil.get(external, "scope")).toString());
			}
			
			//按钮名称
			Object buttoncontent = external.get("buttoncontent");
			if(BeanUtils.isNotEmpty(buttoncontent)){
				inputE.attr("buttoncontent",buttoncontent.toString());
			}
			
			inputE.val(nameVal);
			inputE.attr("initvalue", idVal);
			inputE.attr("right", right);
			if(FieldRight.B.equals(right) || (Double.parseDouble(MapUtil.getString(external, "isRequired"))==1.0 && "default".equals(MapUtil.getString(permission, "permissionType"))  )){
				//两种情况下需要添加必填标志，1.节点上的权限本身就是必填权限；2.节点没有没有配置表单时获取默认的权限，此时，若此时表单设计器中字段选择必填，也要天上必填标志
				addRequred(inputE);
			}
			element.after(inputE.toString());
		} else if (FieldRight.R.equals(right)) {//只读
			element.after(nameVal);
		} else if (FieldRight.RP.equals(right)) {//只读提交
			element.after(nameVal);
			//构建两个NAME和ID隐藏字段
			Element idE = new Element(Tag.valueOf("input"), element.baseUri());
			idE.attr("type","hidden");
			idE.val(idVal);
			idE.attr("name",ename+"ID");
			
			Element nameE = new Element(Tag.valueOf("input"), element.baseUri());
			nameE.attr("type","hidden");
			nameE.val(nameVal);
			nameE.attr("name",ename);
			
			element.after(idE.toString());
			element.after(nameE.toString());
		}

		element.remove();
		return null;
	}
}

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
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.stereotype.Service;

import com.suneee.core.util.MapUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：附件控件
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class AttachmentEditParser extends EditAbstractParser {
	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();

		String ename = getElementName(element, data);
		String val = getValue(element, data);
		String right = getRight(element, permission);
		Map<String, Object> external = handleExternal(element);
		String isdirectupload = external.get("directUpLoad")!=null&&external.get("directUpLoad").equals(1)?"1":"0";
		
		Element inputE = new Element(Tag.valueOf("input"), element.baseUri());
		inputE.attr("ctltype", "attachment");
		inputE.attr("name", ename);
		inputE.attr("isdirectupload", isdirectupload);
		inputE.attr("right", right);
		inputE.val(val);
		if (FieldRight.W.equals(right)) {//编辑
			if(Double.parseDouble(MapUtil.getString(external, "isRequired"))==1.0 && "default".equals(MapUtil.getString(permission, "permissionType"))){//必填
				//权限为W时，有可能是获取的默认权限，而不是用户在节点上自己设置的权限,此时若external中isRequired等于1，则添加必填标志
				addRequred(inputE);
				inputE.attr("validatable", "true");
				inputE.attr("right","b");
			}else{
				inputE.attr("validatable", "false");
			}
		}else if(FieldRight.B.equals(right)){//必填
			addRequred(inputE);
			inputE.attr("validatable", "true");
		}
		addLengthValidate(inputE,external);
		if (!FieldRight.Y.equals(right)){//非隐藏才加
			element.after(inputE);
		}
		element.remove();
		return null;
	}
	
}

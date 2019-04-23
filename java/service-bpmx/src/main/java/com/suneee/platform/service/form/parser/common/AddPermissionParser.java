/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser.common
 * 文件名：AddPermissionParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2016-1-21-下午2:35:07
 *  2016广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser.common;

import java.util.Map;

import com.suneee.core.util.BeanUtils;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.service.form.parser.AbstractParser;
import com.suneee.platform.service.form.parser.FieldAbstractParser;
import com.suneee.platform.service.form.parser.MustRunParser;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：在尾部添加了一个permission
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2016-1-21-下午2:35:07
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class AddPermissionParser implements MustRunParser{

	public Object parse(ParserParam param, Document doc) {
		Map<String, Object> permission = param.getPermission();
		String permissionHtml = "{}";
		if (BeanUtils.isNotEmpty(permission)) {
			permissionHtml = JSONObject.fromObject(permission).toString();
		}
		String str = "<script type=\"text/javascript\" > var permission = " + permissionHtml + " </script>";
		doc.select("body").append(str);
		return null;
	}
}

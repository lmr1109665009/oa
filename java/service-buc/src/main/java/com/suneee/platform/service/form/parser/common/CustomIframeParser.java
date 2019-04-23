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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.util.StringUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.AbstractParser;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.stereotype.Service;

import com.suneee.core.util.StringUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.AbstractParser;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：在尾部添加了一个permission
 * 构建组：bpm33
 * 作者：dyg
 * 邮箱:liyj@jee-soft.cn
 * 日期:2016-1-21-下午2:35:07
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class CustomIframeParser extends AbstractParser {

	/**
	 * {name=iframeUrl, options=[{source=fixed, value=1111, key=key1}, {source=field, key=key2, value=field}],
	 * width=200, dbType={type=varchar, length=50}, iframeClass=class1, iframeUrl=www.qq.com, height=200}
	 */
	@Override
	public Object parse(ParserParam param, Element element) {
		Map<String,Object> external =   handleExternal(element);
		BpmFormData bpmFormData = param.getBpmFormData();
		List<Map<String,Object>> options = (List<Map<String,Object>>) external.get("options");
		Element iframeE = new Element(Tag.valueOf("iframe"), element.baseUri());
		iframeE.attr("scrolling","yes");
		String iframeUrl = (String) external.get("iframeUrl");
		if(StringUtil.isNotEmpty(iframeUrl)){
			if(options.size() > 0){
				if(iframeUrl.indexOf("?") == -1){
					iframeUrl += "?";
				}else{
					iframeUrl += "&";
				}
				List<String> params = new ArrayList<String>();
				for(Map<String,Object> r : options){
					String value = null;
					if("fixed".equals(r.get("source"))){
						value =r.get("value").toString();
					}else if("field".equals(r.get("source"))){
						value = getBpmformFieldValue(bpmFormData,r.get("value").toString());
					}
					if(StringUtil.isEmpty(value)){
						continue;
					}
					params.add( r.get("key")+"="+value);
				}
				if(params.isEmpty()){
					element.remove();
					return null;
				}
				iframeUrl += StringUtils.join(params,"&");
			}
			iframeE.attr("src",iframeUrl);
		}
		String iframeClass = (String) external.get("iframeClass");
		if(StringUtil.isNotEmpty(iframeClass)){
			iframeE.attr("class",iframeClass);
		}
		String width = (String) external.get("width");
		if(StringUtil.isNotEmpty(width)){
			iframeE.attr("width",width);
		}
		String height = (String) external.get("height");
		if(StringUtil.isNotEmpty(height)){
			iframeE.attr("height",height);
		}
		element.after(iframeE);
		element.remove();
		return null;
	}
	
	private String getBpmformFieldValue(BpmFormData bpmFormData,String name){
		if(StringUtil.isEmpty(name)){
			return "";
		}
		if("pkField".equals(name)){
			if(bpmFormData.getPkValue() != null){
				return bpmFormData.getPkValue().getValue().toString();
			}
			return "";
		}
		String val = bpmFormData.getMainField(name.toLowerCase()).toString();
		if(StringUtil.isNotEmpty(val)){
			return val;
		}
		return "";
	}
	
	private Map<String, Object> handleExternal(Element element) {
		Map<String, Object> map = new HashMap<String, Object>();
		String external = element.attr("external");
		map = JSONObjectUtil.toBean(external, HashMap.class);
		return map;
	}

}

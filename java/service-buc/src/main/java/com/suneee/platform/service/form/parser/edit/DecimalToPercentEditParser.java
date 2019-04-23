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

import java.text.DecimalFormat;
import java.util.Map;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

import net.sf.json.JSONObject;

/**
 * <pre>
 * 描述：单行文本框
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class DecimalToPercentEditParser extends EditAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();

		Map<String, Object> external = handleExternal(element);
		
		String ename = getElementName(element, data);
		String val = getValue(element, data);
		String right = getRight(element, permission);
		
		Element inputE = element.select("input").get(0);
		inputE.attr("name", ename);
		inputE.val(val);
		//数字类型字段 
		if("number".equals(((Map)external.get("dbType")).get("type").toString())){
			String validate = inputE.attr("validate");
			if (StringUtil.isEmpty(validate)) {
				validate = "{}";
			}
			JSONObject valdJson = JSONObject.fromObject(validate);
			valdJson.put("number", "true");
			valdJson.put("maxIntLen", ((Map)external.get("dbType")).get("intLen").toString());
			valdJson.put("maxDecimalLen", ((Map)external.get("dbType")).get("decimalLen").toString());
			//处理范围限制
			Object minValue = ((Map)external.get("dbType")).get("minValue");
			Object maxValue = ((Map)external.get("dbType")).get("maxValue");
			if(BeanUtils.isNotEmpty(maxValue)&&BeanUtils.isNotEmpty(minValue)){
				valdJson.put("range", "["+minValue+","+maxValue+"]");
			}
			inputE.attr("validate", valdJson.toString());
			
			//数字展示形式
			JSONObject showTypeJson = new JSONObject();
			
			showTypeJson.put("decimalValue",((Map)external.get("dbType")).get("decimalLen").toString());
			
			Object isCoin= ((Map)external.get("dbType")).get("isCoin");
			if(BeanUtils.isNotEmpty(isCoin)&&isCoin.toString().equals("1")){
				showTypeJson.put("coinValue",((Map)external.get("dbType")).get("coinValue").toString());
			}else{
				showTypeJson.put("coinValue","");
			}
			inputE.attr("showType", showTypeJson.toString());
		}
		
		//处理验证规则 valueFrom
		if(StringUtil.isNotEmpty(((Map)external.get("valueFrom")).get("content").toString())){
			String validate = inputE.attr("validate");
			if (StringUtil.isEmpty(validate)) {
				validate = "{}";
			}
			JSONObject json = JSONObject.fromObject(validate);
			json.put(((Map)external.get("valueFrom")).get("content").toString(), "ture");
			inputE.attr("validate", json.toString());
		}
		
		addLengthValidate(inputE,external);
		if(MapUtil.getString(external, "isRequired").equals("1")){//必填
			addRequred(inputE);
		}
		if (FieldRight.W.equals(right)) {//编辑
			element.after(inputE.toString());
		} else if (FieldRight.R.equals(right)) {//只读
			Double  d=Double.parseDouble(val);
			DecimalFormat df = new DecimalFormat("0.#%");
			String r = df.format(d);
			element.after(r);
		} else if (FieldRight.B.equals(right)) {//必填
			addRequred(inputE);
			element.after(inputE.toString());
		} else if (FieldRight.RP.equals(right)) {//只读提交
			inputE.attr("type","hidden");
			Double  d=Double.parseDouble(val);
			DecimalFormat df = new DecimalFormat("0.#%");
			String r = df.format(d);
			element.after(r);
			element.after(inputE.toString());
		}else {//没有就代表隐藏
		}
		element.remove();
		return null;
	}
}

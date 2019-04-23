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
import com.suneee.platform.model.form.BpmFormField;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：复选框
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class CheckBoxEditParser extends EditAbstractParser {
	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();
		Map<String, Object> external = handleExternal(element);
		
		String ename = getElementName(element, data);
		String val = getValue(element, data);
		
		String right = getRight(element, permission);
		
		BpmFormField bpmFormField = getField(element, data);
		
		Elements labelEs = element.select("label");
		String text="";//主要用于只读时显示的内容
		for (Element e : labelEs) {
			Element checkboxE = e.select("input").get(0);
			checkboxE.attr("name", ename);
			if(valEqual(checkboxE.val(),val.split(","),bpmFormField)){
				checkboxE.attr("checked", "checked");
				if(text!="") text+=",";
				text+=e.text();
			}else{
				checkboxE.removeAttr("checked");
			}
			if (FieldRight.W.equals(right) || FieldRight.B.equals(right)) {//编辑或必填时
				element.before(e);
			}
			if(FieldRight.B.equals(right) || (Double.parseDouble(MapUtil.getString(external, "isRequired"))==1.0 && "default".equals(MapUtil.getString(permission, "permissionType")))){
				addRequred(checkboxE);
			}
		}
		if (FieldRight.R.equals(right)) {//只读
			element.after(text);
		}
		if (FieldRight.RP.equals(right)) {//只读提交
			//提交隐藏字段
			Element emt = new Element(Tag.valueOf("input"), element.baseUri());
			emt.attr("type","hidden");
			emt.val(val);
			emt.attr("name", element.attr("name"));
			element.after(emt.toString());
			
			element.after(text);
		}
		
		element.remove();
		return null;
	}
	
	/**
	 * 这里主要是处理初始化值的问题
	 */
	@Override
	public String getValue(Element element, BpmFormData data) {
		Map<String, Object> external = handleExternal(element);
		String fieldName = (String) external.get("name");
		JSONArray ja = JSONArray.fromObject(external.get("options"));
		String initVal = "";
		for(int i=0;i<ja.size();i++){
			JSONObject jo = ja.getJSONObject(i);
			if(jo.get("isDefault")==null||jo.getInt("isDefault")!=1){
				continue;
			}
			if(initVal!=""){
				initVal+=",";
			}
			initVal+=jo.getString("key");
		}
		String val = getValue(element, data, fieldName, true,initVal);
		return val;
	}
	
	private boolean valEqual(String val,String[] strs,BpmFormField bpmFormField){
		for(String str :strs){
			if(super.valEqual(val, str, bpmFormField)){
				return true;
			}
		}
		return false;
	}
}

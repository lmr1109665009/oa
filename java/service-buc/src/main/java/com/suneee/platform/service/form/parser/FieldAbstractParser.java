/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser
 * 文件名：AbstractParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-12-21-下午3:57:56
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import net.sf.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.Map;

/**
 * <pre>
 * 描述：关于字段解释器的抽象对象
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-21-下午3:57:56
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public abstract class FieldAbstractParser extends AbstractParser {

	/**
	 * 为element添加一个必填条件 required:true
	 * 
	 * @param element
	 *            void
	 * @exception
	 * @since 1.0.0
	 */
	public void addRequred(Element element) {
		String validate = element.attr("validate");
		if (StringUtil.isEmpty(validate)) {
			validate = "{}";
		}
		JSONObject json = JSONObject.fromObject(validate);
		json.put("required", "true");
		element.attr("validate", json.toString());
	}

	/**
	 * 添加长度验证
	 * 
	 * @param element
	 * @param external
	 */

	public void addLengthValidate(Element element, Map<String, Object> external) {
		String validate = element.attr("validate");
		if (StringUtil.isEmpty(validate)) {
			validate = "{}";
		}
		JSONObject json = JSONObject.fromObject(validate);
		json.put("maxlength", ((Map) external.get("dbType")).get("length"));
		element.attr("validate", json.toString());
	}
	
	/**
	 * 根据element 获取 BpmFormField 对象，主要是有时候需要知道BpmFormField一些配置数据
	 * @param element
	 * @param BpmFormData
	 * @return 
	 * BpmFormField
	 * @exception 
	 * @since  1.0.0
	 */
	public abstract BpmFormField getField(Element element, BpmFormData data);
	
	/**
	 * 比较v1和v2值是否相等，主要是字段为非字符串时的比较
	 * @param val1
	 * @param val2
	 * @param bpmFormField
	 * @return 
	 * boolean
	 * @exception 
	 * @since  1.0.0
	 */
	public boolean valEqual(String val1,String val2,BpmFormField bpmFormField){
		if(bpmFormField.getFieldType().equals(BpmFormField.DATATYPE_VARCHAR)||bpmFormField.getFieldType().equals(BpmFormField.DATATYPE_CLOB)){
			return val1.equals(val2);
		}
		if(bpmFormField.getFieldType().equals(BpmFormField.DATATYPE_NUMBER)){
			try{
				float f1 = Float.parseFloat(val1.replace(",", ""));
				float f2 = Float.parseFloat(val2.replace(",", ""));
				return f1==f2;
			}catch (Exception e) {
				return false;
			}
		}
		if(bpmFormField.getFieldType().equals(BpmFormField.DATATYPE_DATE)){
			//TODO
		}
		return false;
	}
}

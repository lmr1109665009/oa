/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser
 * 文件名：AbstractParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-12-21-下午3:57:56
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser.edit;

import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.SubTable;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.FieldAbstractParser;
import com.suneee.platform.service.form.parser.util.FieldRight;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 描述：编辑器生成表单的解释器的抽象对象
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-21-下午3:57:56
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public abstract class EditAbstractParser extends FieldAbstractParser {
	
	/**
	 * <pre>
	 * 基类的处理External属性的方法
	 * 如果不能用则需要自行实现
	 * </pre>
	 * 
	 * @param element
	 * @return Map<String,Object>
	 * @exception
	 * @since 1.0.0
	 */
	public Map<String, Object> handleExternal(Element element) {
		Map<String, Object> map = new HashMap<String, Object>();
		String external = element.attr("external");
		map = JSONObjectUtil.toBean(external, HashMap.class);
		return map;
	}

	/**
	 * 获取val值，注意这里是一个基类的常用方法，如果获取不了需要在处理器复写
	 * 注意！！！！这个方法调用后会默认删除subId的
	 * 
	 * @param element
	 * @param data
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public String getValue(Element element, BpmFormData data) {
		Map<String, Object> external = handleExternal(element);
		String fieldName = (String) external.get("name");
		String val = getValue(element, data, fieldName, true, "");
		return val;
	}

	/**
	 * <pre>
	 * 获取val值，注意这里是一个基类的常用方法，如果获取不了需要在处理器复写
	 * 这里的fieldName主要是为了那种一个解释器会对应多个字段的情况
	 * 例如人员，角色选择器会多出一个 ID字段 等等
	 * </pre>
	 * 
	 * @param element
	 * @param data
	 * @param fieldName
	 *            :要获取的字段名
	 * @param isDelSubId
	 *            :如果存在子表数据标记subid，是否删除 :true-删除;false-否
	 * @param initVal
	 *            :初始化值，data.getPkValue() == null和val为StringUtil.isEmpty 时初始化值
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public String getValue(Element element, BpmFormData data, String fieldName, boolean isDelSubId, String initVal) {

		String tableName = getTableName(element, data);

		String val = "";
		if (tableName.equals("undefined")) {//说明是主表
			val = StringUtil.toString(data.getMainField(fieldName));
		} else {
			BpmFormTable mainTable = data.getBpmFormTable();
			if (mainTable != null && tableName.equals(mainTable.getTableName())) {//主表字段
				val = data.getMainField(fieldName).toString();
			} else {
				SubTable subTable = data.getSubTableMap().get(tableName);
				String subid = element.attr(BpmFormDefHtmlParser.SUBID);//获取其是不是子表循环复制出来的数据
				if (StringUtil.isEmpty(subid)) {
					val = StringUtil.toString(subTable.getRow().get(fieldName));
				} else {
					val = MapUtil.getString(subTable.getDataMap().get(subid), fieldName);
					
					if (isDelSubId) {
						element.removeAttr(BpmFormDefHtmlParser.SUBID);//删除标记
					}
				}
			}
		}
		if (data.getPkValue() == null&&StringUtil.isEmpty(val)) {
			val = initVal;
		}
		return val == null ? initVal : val;
	}

	/**
	 * 获取权限值，注意这里是一个基类的常用方法，如果获取不了需要在处理器复写
	 * 
	 * @param element
	 * @param permission
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public String getRight(Element element, Map<String, Object> permission) {
		if (permission == null) {//没权限对象
			return FieldRight.W.getVal();
		}
		if(StringUtil.isNotEmpty(element.attr("right"))){//被强行赋值了权限
			return element.attr("right");
		}
		
		Map<String, Object> external = handleExternal(element);
		String fieldName = (String) external.get("name");

		String right = "";
		String tableName = "";
		Element temp = element;
		while (true) {
			Element parent = temp.parent();
			if (parent == null) {
				break;
			}
			if (parent.attr("type").equals("subtable")) {
				tableName = parent.attr("tablename");
				break;
			}
			temp = parent;
		}
		if (StringUtil.isNotEmpty(tableName)) {//找到就说明是子表
			Map<String, Object> subFieldJson = (Map<String, Object>) permission.get("subFieldJson");
			Map<String, Object> subField = (Map<String, Object>) subFieldJson.get(tableName.toLowerCase());
			right = MapUtil.getString(subField, fieldName);
		} else {
			Map<String, Object> field = (Map<String, Object>) permission.get("field");
			right = MapUtil.getString(field, fieldName);
		}
		return right;
	}

	/**
	 * 基类的获取tableName的方法
	 * 
	 * @param element
	 * @param data
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public String getTableName(Element element, BpmFormData data) {
		//获取表名
		String tableName = "undefined";
		Element temp = element;
		while (true) {
			Element parent = temp.parent();
			if (parent == null) {
				break;
			}
			if (parent.attr("type").equals("subtable")) {
				tableName = parent.attr("tablename");
				break;
			}
			temp = parent;
		}
		if (tableName.equals("undefined")) {
			BpmFormTable table = data.getBpmFormTable();
			if (table != null) {
				tableName = table.getTableName();
			}
		}
		return tableName;
	}

	/**
	 * 获取m:tableName:field|s:tableName:field
	 * 
	 * @param element
	 * @param data
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public String getElementName(Element element, BpmFormData data) {
		String tableName = getTableName(element, data);
		Map<String, Object> external = handleExternal(element);
		String fieldName = (String) external.get("name");
		String elementName = "";
		if (tableName.equals("undefined")) {//说明是主表
			elementName = "m:";
		} else {
			BpmFormTable mainTable = data.getBpmFormTable();
			if (mainTable != null && tableName.equals(mainTable.getTableName())) {//主表字段
				elementName = "m:";
			} else {
				elementName = "s:";
			}
		}
		elementName += tableName + ":" + fieldName;
		return elementName;
	}
	
	public BpmFormField getField(Element element, BpmFormData data){
		String ename = getElementName(element, data);
		String tableName = ename.split(":")[1];
		String fieldName = ename.split(":")[2];
		
		BpmFormTable bpmFormTable = data.getBpmFormTable();
		if (ename.startsWith("m:")) {//主表
			for(BpmFormField field: bpmFormTable.getFieldList()){
				if(field.getFieldName().equals(fieldName)){
					return field;
				}
			}
		}else{//子表
			for(BpmFormTable table : bpmFormTable.getSubTableList()){
				if(!table.getTableName().equalsIgnoreCase(tableName)){
					continue;
				}
				for(BpmFormField field: table.getFieldList()){
					if(field.getFieldName().equals(fieldName)){
						return field;
					}
				}
			}
		}
		
		return null;
	}
}

/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser
 * 文件名：AbstractParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-12-21-下午3:57:56
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser.table;

import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.SubTable;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.FieldAbstractParser;
import com.suneee.platform.service.form.parser.util.FieldRight;
import org.jsoup.nodes.Element;

import java.util.Map;

/**
 * <pre>
 * 描述：表生成模式的解释器的抽象对象
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-21-下午3:57:56
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public abstract class TableAbstractParser extends FieldAbstractParser {
	
	/**
	 * 获取val值，注意这里是一个基类的常用方法，如果获取不了需要在处理器复写
	 * 
	 * @param element
	 * @param data
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public String getValue(Element element, BpmFormData data) {
		String ename = element.attr("name");
		String fieldName = ename.split(":")[2];
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
		String ename = element.attr("name");
		String tableName = ename.split(":")[1];
		String val = "";
		if (ename.startsWith("m:")) {//主表
			val = StringUtil.toString(data.getMainField(fieldName));
		} else {//子表
			SubTable subTable = data.getSubTableMap().get(tableName);
			String subid = element.attr(BpmFormDefHtmlParser.SUBID);//获取其是不是子表循环复制出来的数据
			if (StringUtil.isEmpty(subid)) {
				val = StringUtil.toString(subTable.getRow().get(fieldName));
			} else {
				val = MapUtil.get(subTable.getDataMap().get(subid), fieldName).toString();
				if (isDelSubId) {
					element.removeAttr(BpmFormDefHtmlParser.SUBID);//删除标记
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
	 * @param data
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
		
		String ename = element.attr("name");
		String tableName = ename.split(":")[1];
		String fieldName = ename.split(":")[2];
		String right = "";
		if (ename.startsWith("m:")) {//主表
			Map<String, Object> field = (Map<String, Object>) permission.get("field");
			right = MapUtil.getString(field, fieldName);
		} else {//子表
			Map<String, Object> subFieldJson = (Map<String, Object>) permission.get("subFieldJson");
			Map<String, Object> subField = (Map<String, Object>) subFieldJson.get(tableName.toLowerCase());
			right = MapUtil.getString(subField, fieldName);
		}
		return right;
	}
	
	public BpmFormField getField(Element element, BpmFormData data){
		String ename = element.attr("name");
		String tableName = ename.split(":")[1];
		String fieldName = ename.split(":")[2];
		
		BpmFormTable bpmFormTable =data.getBpmFormTable();
		if (ename.startsWith("m:")) {//主表
			for(BpmFormField field: bpmFormTable.getFieldList()){
				if(field.getFieldName().equals(fieldName)){
					return field;
				}
			}
		}else{//子表
			for(BpmFormTable table : bpmFormTable.getSubTableList()){
				if(!table.getTableName().equals(tableName)){
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

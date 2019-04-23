package com.suneee.platform.service.form.parser.table;

import java.util.Map;

import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：操作按钮
 * 构建组：bpm33
 * 作者：ouxb
 * 日期:2016年3月7日17:55:41
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class CustomBtnTableParser extends TableAbstractParser{

	@Override
	public Object parse(ParserParam param, Element element) {
		Map<String, Object> permission = param.getPermission();
		String right = getRight(element, permission);
		if (FieldRight.R.equals(right) || FieldRight.RP.equals(right)|| FieldRight.Y.equals(right)) {//只读
			element.remove();
		} 
		return null;
	}
	
	/**
	 * 权限只在主表拿
	 * 比较特殊所以重写了
	 */
	public String  getRight(Element element, Map<String, Object> permission){
		String right=FieldRight.W.getVal();
		String fieldName = element.attr("name");
		if(StringUtil.isNotEmpty(fieldName)){//主表
			Map<String, Object> field = (Map<String, Object>) permission.get("field");
			right = MapUtil.getString(field, fieldName);
		}
		fieldName = element.attr("permissionname");
		if(StringUtil.isNotEmpty(fieldName)){//子表
			String tableName = fieldName.split("-")[0];
			fieldName=fieldName.split("-")[1];
			Map<String, Object> subFieldJson = (Map<String, Object>) permission.get("subFieldJson");
			Map<String, Object> subField = (Map<String, Object>) subFieldJson.get(tableName.toLowerCase());
			right = MapUtil.getString(subField, fieldName);
		}
		
		return right;
	}
}

package com.suneee.platform.service.form.parser.edit;

import com.suneee.core.util.MapUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.SubTable;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 
 * <pre>
 * 描述：编辑器子表弹窗模式
 * 构建组：bpmx33
 * 作者：lqf
 * 邮箱:13507732754@189.cn
 * 日期:2016-7-15-下午4:39:56
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class WindowDialogModeEditParser extends EditAbstractParser {
	@Override
	public Object parse(ParserParam param, Element element) {
		element.removeAttr("external");

		BpmFormData data = param.getBpmFormData();
		String subTableName = element.attr("tablename");

		Elements es = element.getElementsByAttribute("fieldname");
		for (Element e : es) {
			String val = "s:" + subTableName + ":" + e.attr("fieldname");
			e.attr("fieldname", val);
		}

		SubTable subTable = data.getSubTableByName(subTableName);

		Element rowE = element.select("[formtype]").get(0);//获取一列
		Element idE = new Element(Tag.valueOf("input"), element.baseUri());//ID隐藏列
		String idStr = "s:" + subTableName.toLowerCase() + ":" + subTable.getPkName().toLowerCase();
		idE.attr("type", "hidden");
		idE.attr("name", idStr);
		idE.val("");
		//随便找一个td放id隐藏域，为什么要放在td里面，因为在在tr.append这样结构出粗导致jsoup解释有问题，会出现两个id
		//不能放在tdNo这个序号列中就行
		rowE.appendChild(idE);

		//循环子表中的数据，显示到页面上
		for (String id : subTable.getDataMap().keySet()) {
			Map<String, Object> map = subTable.getDataMap().get(id);
			Element newRowE = rowE.clone();//克隆
			newRowE.attr("formtype", "newrow");
			Element ide = newRowE.select("[name=" + idStr + "]").get(0);//拿到ID隐藏域
			//拿到dataId，为什么不直接用id？因为在copy情况下dataId可能为空
			String dataId = map.get(subTable.getPkName().toLowerCase()).toString();
			ide.val(dataId);
			
			for(Element e : newRowE.select("[fieldname]")){
				String fn = e.attr("fieldname");
				Elements fnEls = element.select("[formtype=window] [external*="+"'name':'"+fn.split(":")[2]+"'"+"]");
				
				for(Element fnEl:fnEls){
					Element temp = fnEl.clone();
					temp.attr("right", getFRight(fn,param.getPermission()));
					temp.attr(BpmFormDefHtmlParser.SUBID, id);
					e.appendChild(temp);
				}
			}
			
			rowE.after(newRowE);
		}
		return null;

	}
	
	/**
	 * 获取弹出框模式的列表字段的权限
	 * @param fieldName
	 * @param permission
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public static String getFRight(String fieldName,Map<String, Object> permission){
		Map<String, Object> subFieldJson = (Map<String, Object>) permission.get("subFieldJson");
		Map<String, Object> subField = (Map<String, Object>) subFieldJson.get(fieldName.split(":")[1]);
		String right = MapUtil.getString(subField, fieldName.split(":")[2]);
		if(right.equals(FieldRight.W.getVal())||right.equals(FieldRight.B.getVal())){//必填和编辑在列表中也只是只读提交
			return FieldRight.RP.getVal();
		}
		return right;
	}
}

/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser
 * 文件名：DanhwbkParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-12-11-下午2:49:51
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser.table;

import java.util.List;
import java.util.Map;

import com.suneee.core.util.StringUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.system.Dictionary;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.suneee.core.util.AppUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;
import com.suneee.platform.service.system.DictionaryService;

/**
 * <pre>
 * 描述：数据字典
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class DictTableParser extends TableAbstractParser {
	@Override
	public Object parse(ParserParam param, Element element) {
		DictionaryService dictionaryService = AppUtil.getBean(DictionaryService.class);

		BpmFormData data = param.getBpmFormData();
		Map<String, Object> permission = param.getPermission();
		String val = getValue(element, data);
		String right = getRight(element, permission);

		if (FieldRight.W.equals(right)) {//编辑
			element.val(val);
		} else if (FieldRight.B.equals(right)) {//必填
			element.val(val);
			addRequred(element);
		} else {
			String desc = val;
			List<Dictionary> list=dictionaryService.getByNodeKey(element.attr("nodekey"));
			for(Dictionary dict : list){
				if(dict.getItemValue().equals(val)){
					desc = dict.getItemName();
					break;
				}
			}
			if (FieldRight.R.equals(right)) {//只读
				if(StringUtil.isEmpty(val)){
					val="";
				}
				if (StringUtil.isEmpty(desc)){
					desc="";
				}
				element.val(val);
				element.attr("label",desc);
				element.attr("dicReadOnly", "true");
			} else if (FieldRight.RP.equals(right)) {//只读提交
				element.after(desc);
				element.val(val);
				element.removeClass("dicComboTree");//删除要被解释的样式
				element.attr("type", "hidden");
			} else {//没有就代表隐藏
				element.remove();
			}
		}
		
		return null;
	}
}

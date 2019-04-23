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

import java.util.Map;

import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.SubTable;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.stereotype.Service;

import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.SubTable;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.ParserParam;

/**
 * <pre>
 * 描述：子表_表内编辑模式
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class TableInEditModeTableParser extends TableAbstractParser {
	@Override
	public Object parse(ParserParam param, Element element) {
		BpmFormData data = param.getBpmFormData();

		if (data.getPkValue() == null) {//没有主键，说明没有数据需要初始化
			element.removeAttr(BpmFormDefHtmlParser.PARSE_TARGET);
			return null;
		}
		String tableName = element.attr("tablename");
		SubTable subTable = data.getSubTableByName(tableName);

		Element rowE = element.select("[formtype]").get(0);//获取一列
		Element idE = new Element(Tag.valueOf("input"), element.baseUri());//ID隐藏列
		String idStr = "s:" + tableName.toLowerCase() + ":" + subTable.getPkName().toLowerCase();
		idE.attr("type", "hidden");
		idE.attr("name", idStr);
		idE.val("");
		//随便找一个td放id隐藏域，为什么要放在td里面，因为在在tr.append这样结构出粗导致jsoup解释有问题，会出现两个id
		//不能放在tdNo这个序号列中就行
		rowE.select("td").not(".tdNo").get(0).append(idE.toString());
		
		/**
		 * <pre>
		 * 在CustomForm.js会默认跳过第一个对象所以第一个必须是模板
		 * </pre>
		 */
		for(String id:subTable.getDataMap().keySet()){
			Map<String, Object> map = subTable.getDataMap().get(id);
			
			Element newRowE = rowE.clone();//开始循环出子表
			newRowE.attr("formtype", "newrow");
			Element ide = newRowE.select("[name=" + idStr + "]").get(0);//拿到ID隐藏域
			//拿到dataId，为什么不直接用id？因为在copy情况下dataId可能为空
			String dataId = map.get(subTable.getPkName()).toString();
			ide.val(dataId);

			//告诉解释器其自身是复制来子表数据对应的ID
			for (Element e : newRowE.select("[" + BpmFormDefHtmlParser.PARSE_TARGET + "]")) {
				e.attr(BpmFormDefHtmlParser.SUBID, id);
			}
			rowE.after(newRowE);
		}

		return null;
	}
}

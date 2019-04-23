/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser
 * 文件名：AbstractParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2016-9-9-下午2:02:44
 *  2016广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser;

import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Element;

/**
 * <pre> 
 * 描述：最基类的解释器的抽象类
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2016-9-9-下午2:02:44
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public abstract class AbstractParser implements ParserInter{
	@Override
	public String getName() {
		String str = this.getClass().getSimpleName().replace("Parser", "").toLowerCase();
		return str;
	}

	@Override
	public Object doParse(ParserParam param, Element element) {
		Object obj = null;
		try {
			obj = parse(param, element);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}

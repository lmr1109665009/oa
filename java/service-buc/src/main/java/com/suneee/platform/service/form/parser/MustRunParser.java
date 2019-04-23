/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser.common
 * 文件名：MustRunParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2016-9-9-下午1:56:31
 *  2016广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser;

import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Document;


/**
 * <pre> 
 * 描述：必须运行的解释器
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2016-9-9-下午1:56:31
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public interface MustRunParser{
	public Object parse(ParserParam param, Document doc);
}

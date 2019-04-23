/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser
 * 文件名：ParserInter.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-12-11-下午2:51:13
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser;

import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Element;

/**
 * <pre>
 * 描述：解释器的接口
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:51:13
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public interface ParserInter {
	/**
	 * 返回解释器的名字
	 * 
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String getName();

	/**
	 * 开始解释一个element对象
	 * 
	 * @param param
	 * @param element
	 * @return Object ：返回值，目前没用
	 * @exception
	 * @since 1.0.0
	 */
	Object parse(ParserParam param, Element element);
	
	/**
	 * <pre>
	 * 开始解释一个element对象
	 * 里面有一些其他逻辑 例如
	 * 1 try catch
	 * 2 remove的
	 * </pre>
	 * @param param
	 * @param element
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object doParse(ParserParam param, Element element);
}

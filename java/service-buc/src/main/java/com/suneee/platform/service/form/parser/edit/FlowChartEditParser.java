/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser
 * 文件名：DanhwbkParser.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-12-11-下午2:49:51
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser.edit;

import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * 描述：
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-11-下午2:49:51
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Service
public class FlowChartEditParser extends EditAbstractParser {

	@Override
	public Object parse(ParserParam param, Element element) {
		Element emt = new Element(Tag.valueOf("iframe"), element.baseUri());
		String ctx= ContextUtil.getRequest().getContextPath();
		Object instanceId = param.getVar("instanceId");
		Object actDefId=param.getVar("actDefId");
		if(BeanUtils.isNotEmpty(instanceId)){
			emt.attr("src",ctx+"/platform/bpm/processRun/processImage.ht?actInstId="+instanceId);
		}else if(BeanUtils.isNotEmpty(actDefId)){
			emt.attr("src",ctx+"/platform/bpm/bpmDefinition/flowImg.ht?actDefId="+actDefId);
		}else{
			return null;
		}
		emt.attr("name","flowchart");
		emt.attr("id","flowchart");
		emt.attr("marginwidth","0");
		emt.attr("marginheight","0");
		emt.attr("frameborder","0");
		emt.attr("scrolling","no");
		emt.attr("width","100%");
		element.after(emt);
		element.remove();
		return null;
	}
}

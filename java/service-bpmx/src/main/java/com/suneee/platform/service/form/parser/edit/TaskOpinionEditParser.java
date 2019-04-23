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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.service.bpm.TaskApprovalItemsService;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.service.bpm.TaskApprovalItemsService;
import com.suneee.platform.service.form.parser.util.FieldRight;
import com.suneee.platform.service.form.parser.util.ParserParam;

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
public class TaskOpinionEditParser extends EditAbstractParser {

	protected static  Logger logger = LoggerFactory.getLogger(TaskOpinionEditParser.class);
	//文件访问URL地址
	@Value("#{configProperties['user.webSign.url']}")
	private String staticUrl;

	private String getStaticUrl() {
		if (staticUrl==null){
			return "";
		}
		return staticUrl;
	}
		
	@Override
	public Object parse(ParserParam param, Element element) {
		Map<String, Object> permission = param.getPermission();
		Map<String,String> opinionRight = getOpinionRight(element, permission);
		List<TaskOpinion> opinions = param.getOpinionList();
		element.removeAttr("parser");
		String opinionName= element.attr("name");
		String right = opinionRight.get("right");
		String isShowOrHide= opinionRight.get("isShowOrHide");
		FreemarkEngine freemarkEngine= AppUtil.getBean(FreemarkEngine.class);
		Map<String, Object> model = new HashMap<String, Object>();
		String opinionHtml = "";
		if(BeanUtils.isNotEmpty(opinions) && isShowOrHide.equals("show")){
			for (TaskOpinion taskOpinion : opinions) {
				if(StringUtil.isEmpty(taskOpinion.getFieldName())){
					continue;
				}
				if(taskOpinion.getFieldName().equals(opinionName)){
					model.put("opinion", taskOpinion);
					try {
						opinionHtml += freemarkEngine.mergeTemplateIntoString("opinionhtml.ftl", model);
						opinionHtml = opinionHtml.replaceAll("\\{\\{staticUrl\\}\\}",getStaticUrl());
					} catch (Exception e) {
						logger.debug(e.getMessage());
					}
				}
			}
		}
		element.before(opinionHtml);
		if(FieldRight.R.equals(right) || FieldRight.Y.equals(right)){
			element.remove(); //只读
		}else if(FieldRight.B.equals(right)){
			// 添加必填条件
			addRequred(element);
			// 添加常用语
			addApproval(element, param.getBpmDefinition());
		}else if(FieldRight.W.equals(right)){
			// 添加常用语
			addApproval(element, param.getBpmDefinition());
		}
		return null;
	}
	
	private void addApproval(Element element, BpmDefinition bpmDefinition){
		if(bpmDefinition == null){
			return;
		}
		TaskApprovalItemsService taskApprovalItemsService = AppUtil.getBean(TaskApprovalItemsService.class);
		List<String> taskApprovalItems = 
				taskApprovalItemsService.getApprovalByDefKeyAndTypeId(bpmDefinition.getDefKey(), bpmDefinition.getTypeId());
		if(taskApprovalItems == null || taskApprovalItems.size() == 0){
			return;
		}
		Element selectElement = new Element(Tag.valueOf("select"), element.baseUri());
		StringBuffer buffer = new StringBuffer("<option value=\"\">-- 请选择 --</option>");
		for(String approval : taskApprovalItems){
			buffer.append("<option value=\"").append(approval).append("\">").append(approval).append("</option>");
		}
		selectElement.append(buffer.toString());
		selectElement.attr("name", "taskApprovalItems");
		selectElement.attr("class", "approvalitem");
		selectElement.attr("style", "display:block;"); // 设置元素独占一行
		//element.before("<span style=\"color:blue\">常用语选择：</span>" + selectElement.toString());
		element.before("<span class=\"commonSelect\">常用语选择：</span>" + selectElement.toString());
	}
	
	
	/**
	 * 重新计算意见控件的权限
	 */
	public Map<String,String> getOpinionRight(Element element, Map<String, Object> permission) {
		Map<String,String> opinionRight = new HashMap<>();
		if (BeanUtils.isEmpty(permission)) {//没权限对象  默认只读，不显示历史
			opinionRight.put("right", FieldRight.W.getVal());
			opinionRight.put("isShowOrHide","hide");
		}
		Map<String,String> map = (Map<String, String>) permission.get("opinion");
		String right = map.get(element.attr("name"));
		if(StringUtil.isNotEmpty(right)){
			String[] rightArr=right.split("_");
			if (right.length()>1){
				opinionRight.put("right", rightArr[0]);
				opinionRight.put("isShowOrHide",rightArr[1]);
				return opinionRight;
			}
			opinionRight.put("right", rightArr[0]);
			opinionRight.put("isShowOrHide","show");
		}else{
			opinionRight.put("right", FieldRight.W.getVal());
			opinionRight.put("isShowOrHide","hide");
		}
		return opinionRight;
	}
	
}

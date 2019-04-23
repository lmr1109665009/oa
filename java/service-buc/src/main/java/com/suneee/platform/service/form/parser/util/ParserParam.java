/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser.util
 * 文件名：ParserParam.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2016-1-20-下午3:29:36
 *  2016广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser.util;

import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.form.BpmFormData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：解释器所用到的参数对象
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2016-1-20-下午3:29:36
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public class ParserParam {
	private BpmFormData bpmFormData;//表单字段
	private Map<String, Object> permission;//表单权限
	private List<TaskOpinion> opinionList; //流程审批历史
	private BpmDefinition bpmDefinition; // 流程定义
	/**
	 * <pre>
	 * 变量参数，供一些特殊组件使用
	 * 例如流程图 flowchar组件需要actfid流程定义Id
	 * 流程意见，需要instanceId流程实例id等等
	 *</pre>
	 */
	private Map<String, Object> vars = new HashMap<String, Object>();

	/**
	 * 创建一个新的实例 ParserParam.
	 * 
	 * @param bpmFormData 表单数据
	 * @param permission 表单权限
	 * @param opinionList 流程审批历史
	 */
	public ParserParam(BpmFormData bpmFormData, Map<String, Object> permission, List<TaskOpinion> opinionList, BpmDefinition bpmDefinition) {
		super();
		this.bpmFormData = bpmFormData;
		this.permission = permission;
		this.opinionList = opinionList;
		this.bpmDefinition = bpmDefinition;
	}
	
	/**
	 * bpmFormData
	 * 
	 * @return the bpmFormData
	 * @since 1.0.0
	 */

	public BpmFormData getBpmFormData() {
		return bpmFormData;
	}

	/**
	 * permission
	 * 
	 * @return the permission
	 * @since 1.0.0
	 */

	public Map<String, Object> getPermission() {
		return permission;
	}
	
	/**
	 * 获取流程审批记录
	 * @return
	 */
	public List<TaskOpinion> getOpinionList(){
		return opinionList;
	}
	
	/**
	 * 获取流程定义
	 * @return
	 */
	public BpmDefinition getBpmDefinition(){
		return bpmDefinition;
	}
	
	public void putVar(String key,Object obj){
		this.vars.put(key, obj);
	}
	
	public Object getVar(String key){
		return vars.get(key);
	}
	
}
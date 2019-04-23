package com.suneee.platform.service.bpm;

import com.suneee.core.soap.SoapUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.bpm.BpmNodeWebService;
import net.sf.json.JSONArray;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

import java.util.Map;


/**
 * Webservice任务
 * 
 * @author zxh
 * 
 */
public class WebServiceTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ExecutionEntity ent = (ExecutionEntity) execution;
		String nodeId = ent.getActivityId();
		String actDefId = ent.getProcessDefinitionId();
		
		Map<String, Object> map = execution.getVariables();
		BpmNodeWebServiceService bpmNodeWebServiceService = (BpmNodeWebServiceService) AppUtil.getBean("bpmNodeWebServiceService");
		// 获取脚本节点的脚本对象。
		BpmNodeWebService bpmNodeWebService = bpmNodeWebServiceService.getByNodeIdActDefId(nodeId, actDefId);

		if(BeanUtils.isEmpty(bpmNodeWebService))return;
		String document = bpmNodeWebService.getDocument();
		
		JSONArray jArray = (JSONArray)JSONArray.fromObject(document);
		if(jArray.size()==0)return;

		SoapUtil.invoke(map, jArray);
		execution.setVariables(map);

	}

}

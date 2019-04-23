package com.suneee.platform.service.bpm.listener;

import java.util.Map;

import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.bpm.BpmNodeScript;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.bpm.BpmNodeScript;
import com.suneee.platform.service.bpm.BpmNodeScriptService;

/**
 *子流程最后完成。 
 * @author ray
 *
 */
public class SubProcessEndListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		Integer nrOfInstances=(Integer)execution.getVariable("nrOfInstances");
		Integer nrOfCompletedInstances=(Integer)execution.getVariable("nrOfCompletedInstances");
		//子流程第一次执行
		if(nrOfInstances==null || (nrOfCompletedInstances!=null &&  nrOfInstances.equals(nrOfCompletedInstances))){
			String actDefId=execution.getProcessDefinitionId();
			String nodeId=execution.getCurrentActivityId();
			exeEventScript(execution, BpmConst.EndScript,actDefId,nodeId);
		}
		
	}

	private void exeEventScript(DelegateExecution execution,int scriptType,String actDefId,String nodeId ){
		BpmNodeScriptService bpmNodeScriptService=(BpmNodeScriptService) AppUtil.getBean("bpmNodeScriptService");
		BpmNodeScript model=bpmNodeScriptService.getScriptByType(nodeId, actDefId, scriptType);
		if(model==null) return;

		String script=model.getScript();
		if(StringUtil.isEmpty(script)) return;
		
		GroovyScriptEngine scriptEngine=(GroovyScriptEngine)AppUtil.getBean("scriptEngine");
		Map<String, Object> vars=execution.getVariables();
		vars.put("execution", execution);
		scriptEngine.execute(script, vars);
	}

}

package com.suneee.platform.service.bpm.listener;

import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.model.bpm.TaskOpinion;
import org.activiti.engine.delegate.DelegateExecution;

import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.service.bpm.BpmProStatusService;

public class AutoTaskListener extends BaseNodeEventListener {

	@Override
	protected void execute(DelegateExecution execution, String actDefId,
			String nodeId) {
		BpmProStatusService bpmProStatusService=(BpmProStatusService) AppUtil.getBean("bpmProStatusService");
		Long actInstanceId=Long.parseLong(execution.getProcessInstanceId());
		/**
		 * 记录节点执行状态。
		 */
		bpmProStatusService.addOrUpd(actDefId, actInstanceId,nodeId, TaskOpinion.STATUS_EXECUTED);
		
	}

	@Override
	protected Integer getScriptType() {
		
		return BpmConst.StartScript;
	}

}

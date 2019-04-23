package com.suneee.platform.event.listener;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.platform.event.def.CallActivitiBackEvent;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import com.suneee.platform.event.def.CallActivitiBackEvent;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.service.bpm.TaskOpinionService;

@Service
public class CallActivityBackListener implements ApplicationListener<CallActivitiBackEvent>,Ordered {
	
	@Resource
	TaskOpinionService taskOpinionService;

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void onApplicationEvent(CallActivitiBackEvent event) {
		List<TaskEntity> taskList=(List<TaskEntity>) event.getSource();
		String curTaskId=event.getCurTaskId();
		for(TaskEntity task:taskList){
			Short status=curTaskId.equals(task.getId())?TaskOpinion.STATUS_REJECT:TaskOpinion.STATUS_BACK_CANCEL;
			Long taskId=new Long(task.getId());
			TaskOpinion taskOpinion= taskOpinionService.getByTaskId(taskId);
			taskOpinion.setCheckStatus(status);
			taskOpinion.setEndTime(new Date());
			taskOpinionService.update(taskOpinion);
		}
		
	}

}

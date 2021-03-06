package com.suneee.platform.service.bpm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.platform.dao.bpm.BpmProStatusDao;
import com.suneee.platform.model.bpm.BpmProStatus;
import com.suneee.platform.service.bpm.IFlowStatus;
import com.suneee.platform.dao.bpm.BpmProStatusDao;
import com.suneee.platform.model.bpm.BpmProStatus;

public class FlowStatus implements IFlowStatus {
	
	private BpmProStatusDao bpmProStatusDao;
	
	private Map<Short, String> statusColorMap=new HashMap<Short, String>();
	
	public void setBpmProStatus(BpmProStatusDao bpmProStatusDao){
		this.bpmProStatusDao=bpmProStatusDao;
	}
	
	public void setStatusColor(Map<Short, String> tmp){
		this.statusColorMap=tmp;
	}
	
	@Override
	public Map<String,String> getStatusByInstanceId(Long instanceId) {
		Map<String, String> map=new HashMap<String, String>();
		List<BpmProStatus> list= this.bpmProStatusDao.getByActInstanceId(instanceId.toString());
		for(BpmProStatus obj:list){
			String color=statusColorMap.get(obj.getStatus());
			map.put(obj.getNodeid(), color);
			//userTask2,#00ff99.
		}
		return map;
	}
	
}

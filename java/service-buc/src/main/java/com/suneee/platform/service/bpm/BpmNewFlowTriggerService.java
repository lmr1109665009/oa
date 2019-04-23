package com.suneee.platform.service.bpm;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.bpm.BpmNewFlowTriggerDao;
import com.suneee.platform.model.bpm.BpmNewFlowTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *<pre>
 * 对象功能:触发新流程配置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miao
 * 创建时间:2015-05-28 11:20:59
 *</pre>
 */
@Service
public class BpmNewFlowTriggerService extends BaseService<BpmNewFlowTrigger>
{
	@Resource
	private BpmNewFlowTriggerDao dao;
	
	
	
	public BpmNewFlowTriggerService()
	{
	}
	
	@Override
	protected IEntityDao<BpmNewFlowTrigger, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 通过 节点ID 流程key获取触发设置
	 * @param nodeId
	 * @param flowKey
	 * @return
	 */
	public BpmNewFlowTrigger getByFlowkeyNodeId(String nodeId, String flowKey) {
		return dao.getByFlowkeyNodeId(nodeId,flowKey);
	}
	

	
	/**
	 * 获取某动作触发流程数据
	 * @param nodeId
	 * @param flowKey
	 * @param action
	 */
	public BpmNewFlowTrigger getByNodeAction(String nodeId, String flowKey, String action) {
		BpmNewFlowTrigger triggerSetting = dao.getByFlowkeyNodeId(nodeId,flowKey);
		if(triggerSetting != null && action.equals(triggerSetting.getAction()))
			return triggerSetting;
		
		return null;
	}
	
	/**
	 * 根据流程key获取触发定义。
	 * @param flowKey
	 * @return
	 */
	public List< BpmNewFlowTrigger> getByFlowkey( String flowKey) {
		return dao.getByFlowkey(flowKey);
	}
	
	
	
}

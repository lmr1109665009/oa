package com.suneee.platform.service.bpm;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.bpm.BpmProStatusDao;
import com.suneee.platform.model.bpm.BpmProStatus;
import com.suneee.platform.model.bpm.TaskOpinion;
import org.springframework.stereotype.Service;

import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.bpm.BpmProStatus;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.dao.bpm.BpmProStatusDao;

/**
 * 对象功能:流程节点状态 Service类 开发公司:广州宏天软件有限公司 开发人员:ray 创建时间:2012-07-19 11:42:55
 */
@Service
public class BpmProStatusService extends BaseService<BpmProStatus> {
	@Resource
	private BpmProStatusDao dao;

	public BpmProStatusService() {
	}

	@Override
	protected IEntityDao<BpmProStatus, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 添加流程数据状态。
	 * 
	 * @param actDefId
	 *            流程定义ID
	 * @param processInstanceId
	 *            流程实例ID
	 * @param nodeId
	 *            节点ID
	 */
	public void addOrUpd(String actDefId, Long processInstanceId, String nodeId) {
		addOrUpd(actDefId,processInstanceId,nodeId, TaskOpinion.STATUS_CHECKING);
	}
	
	/**
	 * 添加或修改节点的执行状态。
	 * @param actDefId
	 * @param processInstanceId
	 * @param nodeId
	 * @param status
	 */
	public void addOrUpd(String actDefId, Long processInstanceId, String nodeId,Short status) {
		BpmProStatus bpmProStatus = this.dao.getByInstNodeId(processInstanceId, nodeId);
		if (bpmProStatus == null) {
			Map<String, FlowNode> mapNode = NodeCache.getByActDefId(actDefId);
			BpmProStatus tmp = new BpmProStatus();
			tmp.setId(UniqueIdUtil.genId());
			tmp.setActdefid(actDefId);
			tmp.setActinstid(processInstanceId);
			tmp.setLastupdatetime(new Date());
			tmp.setNodeid(nodeId);
			tmp.setStatus(status);
			FlowNode flowNode = mapNode.get(nodeId);
			tmp.setNodename(flowNode.getNodeName());
			dao.add(tmp);
		} else {
			this.dao.updStatus(processInstanceId, nodeId, status);
		}
	}
}

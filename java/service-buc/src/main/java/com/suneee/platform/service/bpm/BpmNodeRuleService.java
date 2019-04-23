package com.suneee.platform.service.bpm;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.bpm.BpmNodeRuleDao;
import com.suneee.platform.model.bpm.BpmNodeRule;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对象功能:流程节点规则 Service类 开发公司:广州宏天软件有限公司 开发人员:ray 创建时间:2011-12-14 15:41:53
 */
@Service
public class BpmNodeRuleService extends BaseService<BpmNodeRule> {
	@Resource
	private BpmNodeRuleDao dao;

	public BpmNodeRuleService() {
	}

	@Override
	protected IEntityDao<BpmNodeRule, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 获取所有的任务定义
	 * 
	 * @param actDefId
	 * @param ruleId
	 * @return
	 */
	public List<BpmNodeRule> getByDefIdNodeId(String actDefId, String nodeId) {
		return dao.getByDefIdNodeId(actDefId, nodeId);
	}

	/**
	 * 规则重新排序
	 * 
	 * @param ruleIds
	 */
	public void reSort(String ruleIds) {
		if (StringUtil.isEmpty(ruleIds))
			return;
		String[] aryRuleIds = ruleIds.split(",");
		for (int i = 0; i < aryRuleIds.length; i++) {
			Long ruleId = Long.parseLong(aryRuleIds[i]);
			dao.reSort(ruleId, (long) i);
		}
	}

	/**
	 * 获取所有的任务定义
	 * 
	 * @param actDefId
	 * @param ruleId
	 * @return
	 */
	public List<BpmNodeRule> getByActDefId(String actDefId) {
		return dao.getByDefIdNodeId(actDefId, null);
	}

}

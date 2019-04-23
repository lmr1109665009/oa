package com.suneee.platform.service.bpm;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.bpm.BpmSubtableRightsDao;
import com.suneee.platform.model.bpm.BpmSubtableRights;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *<pre>
 * 对象功能:子表权限 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:wwz
 * 创建时间:2013-01-16 10:04:39
 *</pre>
 */
@Service
public class BpmSubtableRightsService extends BaseService<BpmSubtableRights>
{
	@Resource
	private BpmSubtableRightsDao dao;
	
	public BpmSubtableRightsService()
	{
	}
	
	@Override
	protected IEntityDao<BpmSubtableRights, Long> getEntityDao()
	{
		return dao;
	}
	

	
	/**
	 * 根据流程ID和节点ID和父流程ID获取子表权限配置
	 * 
	 * @param defId
	 * @param nodeId
	 * @param parentActDefId
	 * @return
	 */
	public BpmSubtableRights getByDefIdAndNodeId(String actDefId, String nodeId, Long tableId, String parentActDefId){
		return dao.getByDefIdAndNodeId(actDefId, nodeId, tableId, parentActDefId);
	}
	
}

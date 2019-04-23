package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.BpmSubtableRights;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 对象功能:子表权限 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:wwz
 * 创建时间:2013-01-16 10:04:39
 * </pre>
 */
@Repository
public class BpmSubtableRightsDao extends BaseDao<BpmSubtableRights> {
	@Override
	public Class<?> getEntityClass() {
		return BpmSubtableRights.class;
	}

	/**
	 * 根据流程ID和节点ID获取子表权限配置
	 * 
	 * @param defId
	 * @param nodeId
	 * @param tableId
	 * @return
	 */
	@SuppressWarnings("all")
	public BpmSubtableRights getByDefIdAndNodeId(String actDefId, String nodeId,Long tableId,String parentActDefId) {
		Map map = new HashMap();
		map.put("actdefid", actDefId);
		map.put("nodeid", nodeId);
		map.put("tableid", tableId);
		map.put("parentActDefId", parentActDefId);
		BpmSubtableRights model = (BpmSubtableRights) this.getUnique("getByDefIdAndNodeId", map);
		return model;
	}
	
	

	public void delByActDefId(String actDefId) {
		this.delBySqlKey("delByActDefId", actDefId);
	}

}
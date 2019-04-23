package com.suneee.platform.dao.bpm;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.BpmBatchApproval;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:流程批量审批定义设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-04-17 15:34:17
 *</pre>
 */
@Repository
public class BpmBatchApprovalDao extends BaseDao<BpmBatchApproval>
{
	@Override
	public Class<?> getEntityClass()
	{
		return BpmBatchApproval.class;
	}

	public List<String> businessKeyList(String defKey, String nodeId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("defKey", defKey);
		params.put("nodeId", nodeId);
		params.put("userId", ContextUtil.getCurrentUserId());
		return getBySqlKeyGenericity("businessKeyList", params);
	}

	public List<BpmBatchApproval> isExists(String defKey, String nodeId, Long id) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("defKey", defKey);
		params.put("nodeId", nodeId);
		params.put("id", id);
		return getBySqlKey("isExists", params);
	}

	public void delByDefKey(String defKey) {
		this.delBySqlKey("delByDefKey", defKey);
	}

	
	
	
	
}
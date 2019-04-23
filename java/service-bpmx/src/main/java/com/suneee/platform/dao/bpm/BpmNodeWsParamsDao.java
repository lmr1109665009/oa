package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.BpmNodeWsParams;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:流程webservice节点参数 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2012-12-21 16:51:20
 * </pre>
 */
@Repository
public class BpmNodeWsParamsDao extends BaseDao<BpmNodeWsParams> {
	@Override
	public Class<?> getEntityClass() {
		return BpmNodeWsParams.class;
	}

	/**
	 * 通过webserviceId
	 * 
	 * @param webserviceId
	 * @return
	 */
	public List<BpmNodeWsParams> getByWebserviceId(Long webserviceId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("webserviceId", webserviceId);
		return getBySqlKey("getByWebserviceId", params);
	}

	public void delByWebserviceId(Long webserviceId) {
		getBySqlKey("delByWebserviceId", webserviceId);
	}

}
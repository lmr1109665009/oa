package com.suneee.oa.dao.user;

import com.suneee.platform.model.system.Position;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 岗位Position扩展DAO类
 * @author xiongxianyun
 *
 */
@Repository
public class PositionExtendDao extends UcpBaseDao<Position>{
	
	@Override
	public Class<Position> getEntityClass() {
		return Position.class;
	}
	
	/**
	 * 根据组织ID和职务ID获取岗位信息
	 * @param orgId
	 * @param jobId
	 * @return
	 */
	public Position getByOrgIdAndJobId(Long orgId, Long jobId) {
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("orgId", orgId);
		params.put("jobId", jobId);
		return getUnique("getByOrgIdAndJobId", params);
	}
}
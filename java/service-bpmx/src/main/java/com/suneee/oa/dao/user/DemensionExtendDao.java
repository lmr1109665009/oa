package com.suneee.oa.dao.user;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.Demension;
import com.suneee.ucp.base.dao.UcpBaseDao;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 维度信息Demension扩展DAO类
 * @author xiongxianyun
 *
 */
@Repository
public class DemensionExtendDao extends UcpBaseDao<Demension> {
	@Override
	public Class<Demension> getEntityClass() {
		return Demension.class;
	}

	/**
	 * 获取系统所有维度信息
	 * @param filter
	 * @return
	 */
	public List<Demension> getAllDemension(QueryFilter filter) {
		return getBySqlKey("getAllDemension", filter);
	}
}
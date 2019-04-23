package com.suneee.oa.dao.user;

import com.suneee.platform.model.system.SysOrgType;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

/**
 * 组织类型SysOrgType扩展DAO类
 * @author xiongxianyun
 *
 */
@Repository
public class SysOrgTypeExtendDao extends UcpBaseDao<SysOrgType> {
	@Override
	public Class<SysOrgType> getEntityClass() {
		return SysOrgType.class;
	}

	/**
	 * 根据维度ID删除组织类型
	 * @param demId
	 * @return
	 */
	public int delByDemId(Long demId) {
		return delBySqlKey("delByDemId", demId);
	}
}
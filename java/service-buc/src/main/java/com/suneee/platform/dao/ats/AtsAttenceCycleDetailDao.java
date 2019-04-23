package com.suneee.platform.dao.ats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsAttenceCycleDetail;

/**
 * <pre>
 * 对象功能:考勤周期明细 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-17 15:47:49
 * </pre>
 */
@Repository
public class AtsAttenceCycleDetailDao extends BaseDao<AtsAttenceCycleDetail> {
	@Override
	public Class<?> getEntityClass() {
		return AtsAttenceCycleDetail.class;
	}

	public void delByCycleId(Long cycleId) {
		this.delBySqlKey("delByCycleId", cycleId);

	}

	public List<AtsAttenceCycleDetail> getByCycleId(Long cycleId) {
		return getBySqlKey("getByCycleId", cycleId);
	}


	public AtsAttenceCycleDetail getByCycleIdName(Long cycleId, String name) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("cycleId", cycleId);
		params.put("name", name);
		return (AtsAttenceCycleDetail) getOne("getByCycleIdName", params);
	}

}
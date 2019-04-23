package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsHolidayPolicyDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:假期制度明细 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-05-28 14:25:03
 *</pre>
 */
@Repository
public class AtsHolidayPolicyDetailDao extends BaseDao<AtsHolidayPolicyDetail>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsHolidayPolicyDetail.class;
	}

	public void delByHolidayId(Long holidayId) {
		this.delBySqlKey("delByHolidayId", holidayId);
	}

	public List<AtsHolidayPolicyDetail> getByHolidayId(Long holidayId) {
		return getBySqlKey("getByHolidayId", holidayId);
	}

	
	
	
}
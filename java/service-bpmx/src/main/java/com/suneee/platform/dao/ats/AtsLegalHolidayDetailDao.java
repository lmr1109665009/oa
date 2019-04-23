package com.suneee.platform.dao.ats;

import java.util.List;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsLegalHolidayDetail;
/**
 *<pre>
 * 对象功能:法定节假日明细 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-17 15:48:34
 *</pre>
 */
@Repository
public class AtsLegalHolidayDetailDao extends BaseDao<AtsLegalHolidayDetail>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsLegalHolidayDetail.class;
	}

	public void delByHolidayId(Long holidayId) {
		this.delBySqlKey("delByHolidayId", holidayId);
	}

	public List<AtsLegalHolidayDetail> getByHolidayId(Long holidayId) {
		return getBySqlKey("getByHolidayId", holidayId);
	}

	public List<AtsLegalHolidayDetail> getHolidayListByAttencePolicy(Long attencePolicy) {
		return getBySqlKey("getHolidayListByAttencePolicy", attencePolicy);
	}
	
	
	
	
}
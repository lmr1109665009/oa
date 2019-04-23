package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import com.suneee.core.util.DateUtil;
import com.suneee.platform.model.ats.AtsCardRecord;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:打卡记录 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-26 11:21:21
 *</pre>
 */
@Repository
public class AtsCardRecordDao extends BaseDao<AtsCardRecord>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsCardRecord.class;
	}
	public List<AtsCardRecord> getByCardNumber(String cardNumber) {
		return this.getBySqlKey("getByCardNumber", cardNumber);
	}
	public List<AtsCardRecord> getByCardNumberCardDate(String cardNumber,
			Date startTime, Date endTime) {
		Map<String,Object> params =  new HashMap<String,Object>();
		params.put("cardNumber", cardNumber);
		params.put("startTime", DateUtil.addDay(startTime, -1));
		params.put("endTime", DateUtil.addDay(endTime, 1));
		return this.getBySqlKey("getByCardNumberCardDate", params);
	}
}
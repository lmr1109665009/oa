package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsCalendarTemplDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * 对象功能:日历模版明细 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-17 15:45:31
 * </pre>
 */
@Repository
public class AtsCalendarTemplDetailDao extends BaseDao<AtsCalendarTemplDetail> {
	@Override
	public Class<?> getEntityClass() {
		return AtsCalendarTemplDetail.class;
	}

	public void delByCalendarId(Long calendarId) {
		this.delBySqlKey("delByCalendarId", calendarId);
	}

	public List<AtsCalendarTemplDetail> getByCalendarId(Long calendarId) {
		return getBySqlKey("getByCalendarId", calendarId);
	}

}
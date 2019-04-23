package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsShiftTime;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * 对象功能:班次时间设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-19 14:05:03
 * </pre>
 */
@Repository
public class AtsShiftTimeDao extends BaseDao<AtsShiftTime> {
	@Override
	public Class<?> getEntityClass() {
		return AtsShiftTime.class;
	}

	public void delByShiftId(Long shiftId) {
		this.delBySqlKey("delByShiftId", shiftId);
	}

	public List<AtsShiftTime> getByShiftId(Long shiftId) {
		return getBySqlKey("getByShiftId", shiftId);
	}

}
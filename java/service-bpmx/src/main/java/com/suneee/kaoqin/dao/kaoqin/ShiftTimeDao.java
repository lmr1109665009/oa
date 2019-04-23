package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.ShiftTime;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:班次时间段 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:09:27
 *</pre>
 */
@Repository
public class ShiftTimeDao extends BaseDao<ShiftTime>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ShiftTime.class;
	}

	/**
	 * 根据班次ID删除关联的班次时间段
	 * @param shiftId
	 */
	public void removeTimeByShiftId(Long shiftId) {
		delBySqlKey("removeTimeByShiftId", shiftId);
	}
	
	/**
	 * 根据班次ID获取班次时间段列表
	 * @param shiftId
	 * @return
	 */
	public List<ShiftTime> getTimesByShiftId(Long shiftId) {
		return getBySqlKey("getTimesByShiftId", shiftId);
	}
	
}
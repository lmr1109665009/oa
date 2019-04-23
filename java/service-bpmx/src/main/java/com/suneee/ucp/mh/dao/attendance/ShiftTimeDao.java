package com.suneee.ucp.mh.dao.attendance;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.attendance.ShiftTime;
/**
 *<pre>
 * 对象功能:班次时间段 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:09:27
 *</pre>
 */
@Repository("ucpShiftTimeDao")
public class ShiftTimeDao extends UcpBaseDao<ShiftTime>
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
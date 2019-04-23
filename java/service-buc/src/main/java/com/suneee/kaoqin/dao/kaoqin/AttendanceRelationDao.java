package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.AttendanceRelation;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:考勤关系映射表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-03 11:57:25
 *</pre>
 */
@Repository
public class AttendanceRelationDao extends BaseDao<AttendanceRelation>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AttendanceRelation.class;
	}

	/**
	 * 根据考勤卡编号获取用户ID
	 * @param badgenumber
	 * @return
	 */
	public AttendanceRelation getByBadgenumber(String badgenumber) {
		return getUnique("getByBadgenumber", badgenumber);
	}
}
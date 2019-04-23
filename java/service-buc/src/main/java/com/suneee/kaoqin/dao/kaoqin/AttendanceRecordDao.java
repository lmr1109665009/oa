package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.AttendanceRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:考勤记录表 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-03 11:58:37
 *</pre>
 */
@Repository
public class AttendanceRecordDao extends BaseDao<AttendanceRecord>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AttendanceRecord.class;
	}

	/**
	 * 批量添加数据
	 * @param list
	 * @return
	 */
	public boolean batchAdd(List<AttendanceRecord> list){
		String statement = getIbatisMapperNamespace() + ".batchAdd";
		int rows = getSqlSessionTemplate().insert(statement, list);
		return rows > 0;
	}
	
}
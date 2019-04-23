package com.suneee.kaoqin.dao.kaoqin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.AttendanceVacation;
/**
 *<pre>
 * 对象功能:假期类型 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 10:43:17
 *</pre>
 */
@Repository
public class AttendanceVacationDao extends BaseDao<AttendanceVacation>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AttendanceVacation.class;
	}

	public List<AttendanceVacation> checkRepeat(Long id, String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (id != null && id != 0) {
			params.put("id", id);
		}
		params.put("name", name);
		return this.getBySqlKey("checkRepeat", params);
	}

	
	
	
	
}
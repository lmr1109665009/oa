/**
 * 对象功能:班次时间 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:cjj
 * 创建时间:2012-02-22 16:58:15
 */
package com.suneee.platform.dao.worktime;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.worktime.WorkTime;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WorkTimeDao extends BaseDao<WorkTime>
{
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass()
	{
		return WorkTime.class;
	}
	
	/**
	 * 根据settingId取worktime
	 * @param settingId
	 * @return
	 */
	public List<WorkTime> getBySettingId(String settingId){
		Map<String, String> p = new HashMap<String, String>();
		p.put("settingId", settingId);
		return this.getSqlSessionTemplate().selectList(
				this.getIbatisMapperNamespace() + ".getBySettingId", p);
	}
}
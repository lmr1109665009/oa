package com.suneee.ucp.mh.dao.schedulecalendar;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.schedulecalendar.ScheduleCalendar;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:日程表 Dao类
 * 开发公司:深圳象翌微链股份有限公司
 * 开发人员:xiongxianyun
 * 创建时间:2017-06-26 13:36:34
 *</pre>
 */
@Repository
public class ScheduleCalendarDao extends UcpBaseDao<ScheduleCalendar>
{
	@Override
	public Class<ScheduleCalendar> getEntityClass()
	{
		return ScheduleCalendar.class;
	}
	
	/**
	 * 根据查询条件获取日程列表
	 * @param params
	 * @return
	 */
	public List<ScheduleCalendar> getListBy(Map<String, Object> params){
		return this.getBySqlKey("getListBy", params);
	}
	
	/**
	 * 根据条件删除日程信息
	 * @param params
	 */
	public void delBy(Map<String, Object> params){
		this.delBySqlKey("delBy", params);
	}
	
	/**
	 * 根据查询条件获取日程详情
	 * @param params
	 * @return
	 */
	public ScheduleCalendar getBy(Map<String, Object> params){
		return this.getUnique("getBy", params);
	}

	/**
	 * 重写父类方法（解决问题：将model中字段createBy数据修改成当前用户ID）
	 * @param scheduleCalendar
	 */
	@Override
	public void add(ScheduleCalendar scheduleCalendar){
		String addStatement=getIbatisMapperNamespace() + ".add";
		getSqlSessionTemplate().insert(addStatement, scheduleCalendar);
	}

	/**
	 * 根据数据来源id删除对应日程数据
	 */
	public void delBySourceId(Long sourceId) {
		this.delBySqlKey("delBySourceId",sourceId);
	}
}
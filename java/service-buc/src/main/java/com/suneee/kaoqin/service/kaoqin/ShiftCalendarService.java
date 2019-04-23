package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.kaoqin.dao.kaoqin.ShiftCalendarDao;
import com.suneee.kaoqin.model.kaoqin.ShiftCalendar;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:班次日历 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-04 16:19:21
 *</pre>
 */
@Service
public class ShiftCalendarService extends BaseService<ShiftCalendar>
{
	@Resource
	private ShiftCalendarDao dao;
	
	
	
	public ShiftCalendarService()
	{
	}
	
	@Override
	protected IEntityDao<ShiftCalendar, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 根据班次ID获取班次休息日设置列表
	 * @param shiftId
	 * @return
	 */
	public List<ShiftCalendar> getByShiftId(Long shiftId) {
		return dao.getBySqlKey("getByShiftId", shiftId);
	}

	/**
	 * 获取周几的中文描述
	 * @param week
	 * @return
	 */
	public String getWeekTitle(short week) {
		switch (week) {
		case Calendar.SUNDAY:
			return "周日";
		case Calendar.MONDAY:
			return "周一";
		case Calendar.TUESDAY:
			return "周二";
		case Calendar.WEDNESDAY:
			return "周三";
		case Calendar.THURSDAY:
			return "周四";
		case Calendar.FRIDAY:
			return "周五";
		case Calendar.SATURDAY:
			return "周六";
		}
		return "周日";
	}
	
	/**
	 * 流程处理器方法 用于处理业务数据
	 * @param cmd
	 * @throws Exception
	 */
	public void processHandler(ProcessCmd cmd)throws Exception{
		Map data=cmd.getFormDataMap();
		if(BeanUtils.isNotEmpty(data)){
			String json=data.get("json").toString();
			ShiftCalendar shiftCalendar=getShiftCalendar(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				shiftCalendar.setId(genId);
				this.add(shiftCalendar);
			}else{
				shiftCalendar.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(shiftCalendar);
			}
			cmd.setBusinessKey(shiftCalendar.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取ShiftCalendar对象
	 * @param json
	 * @return
	 */
	public ShiftCalendar getShiftCalendar(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		ShiftCalendar shiftCalendar = JSONObjectUtil.toBean(json, ShiftCalendar.class);
		return shiftCalendar;
	}
	
	/**
	 * 保存 班次日历 信息
	 * @param shiftCalendar
	 */
	public void save(ShiftCalendar shiftCalendar){
		Long id=shiftCalendar.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			shiftCalendar.setId(id);
			this.add(shiftCalendar);
		}
		else{
			this.update(shiftCalendar);
		}
	}
	
	/**
	 * 根据班次ID和日期查询班次排班日历信息
	 * @param shiftId
	 * @param day
	 * @return
	 */
	public ShiftCalendar getShiftCalendarBy(Long shiftId, Calendar day){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("week", day.get(Calendar.DAY_OF_WEEK));
		params.put("shiftId", shiftId);
		return dao.getUnique("getShiftCalendarBy", params);
	}
	
}

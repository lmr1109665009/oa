package com.suneee.platform.service.calendar.impl;

import com.suneee.platform.calendar.ICalendarDatas;
import com.suneee.platform.calendar.model.CalendarData;
import com.suneee.platform.calendar.util.CalendarUtil;
import com.suneee.platform.dao.calendar.PersonalCalendarDao;
import com.suneee.platform.model.calendar.PersonalCalendar;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 个人日历
 * @author zxh
 *
 */
public class PersonalCalendarData  implements ICalendarDatas {
	@Resource
	private PersonalCalendarDao personalCalendarDao;
	@Override
	public List<CalendarData> getCalendarData(Map<String, Object> map)
			throws Exception {
		List<PersonalCalendar> list =  personalCalendarDao.getPersonalCalendar(map);
		String mode = (String)map.get("mode");
		Date nowDate =  new Date();
		List<CalendarData> calendarDataList = new ArrayList<CalendarData>();
		for (PersonalCalendar personalCalendar : list) {		
			CalendarData calendarData = new CalendarData();
			calendarData.setId(personalCalendar.getId());

			CalendarUtil.setCalendarData(calendarData, mode,nowDate,personalCalendar.getStartTime(),personalCalendar.getEndTime() );
			
			calendarData.setDescription(personalCalendar.getDescription());
			calendarData.setTitle(personalCalendar.getTitle());
			calendarData.setType(CalendarData.TYPE_MY_CALENDARS);
			calendarData.setStatus(CalendarData.COLOR_MY_CALENDARS);		
			calendarDataList.add(calendarData);
		}
		return calendarDataList;
	}

}

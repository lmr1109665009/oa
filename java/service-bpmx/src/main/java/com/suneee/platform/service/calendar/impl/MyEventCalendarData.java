package com.suneee.platform.service.calendar.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.platform.dao.bpm.TaskDao;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.platform.calendar.ICalendarDatas;
import com.suneee.platform.calendar.model.CalendarData;
import com.suneee.platform.calendar.util.CalendarUtil;
import com.suneee.platform.dao.bpm.TaskDao;

/**
 *我的待办
 * @author zxh
 *
 */
public class MyEventCalendarData implements ICalendarDatas {
	@Resource
	private TaskDao taskDao;
	
	
	@Override
	public List<CalendarData> getCalendarData(Map<String,Object> map) throws Exception { 
		String mode = (String)map.get("mode");
		List<TaskEntity> list = taskDao.getMyEvents(map);
		Date nowDate =  new Date();
		List<CalendarData> calendarDataList = new ArrayList<CalendarData>();
		for (Object obj : list) {
			ProcessTask task = (ProcessTask)obj;
			CalendarData calendarData = new CalendarData();
			calendarData.setId(Long.valueOf(task.getId()));
			CalendarUtil.setCalendarData(calendarData, mode,nowDate,task.getCreateTime(),task.getDueDate() );
			calendarData.setDescription(task.getProcessName());
			calendarData.setTitle(task.getSubject());
			calendarData.setType(CalendarData.TYPE_MY_EVENTS);
			calendarData.setStatus(CalendarData.COLOR_MY_EVENTS);		
			calendarDataList.add(calendarData);
		}
		return calendarDataList;
	}
}

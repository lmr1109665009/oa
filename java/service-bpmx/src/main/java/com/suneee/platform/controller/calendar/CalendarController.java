package com.suneee.platform.controller.calendar;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.suneee.platform.annotion.Action;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.calendar.CalendarCollection;

@Controller
@RequestMapping("/platform/calendar/")
public class CalendarController  extends BaseController {
	@Resource
	public CalendarCollection calendarCollection;
	
	@RequestMapping("getData")
	@Action(description = "获取日历的数据")
	public void getData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mode", RequestUtil.getString(request, "mode","month"));
		map.put("startDate", RequestUtil.getString(request, "startDate") );
		map.put("endDate", RequestUtil.getString(request, "endDate"));
		response.getWriter().print(calendarCollection.getCalendarDatasString(map));
	}
}

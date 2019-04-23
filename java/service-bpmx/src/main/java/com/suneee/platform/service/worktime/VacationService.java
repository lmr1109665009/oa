package com.suneee.platform.service.worktime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.platform.dao.worktime.VacationDao;
import com.suneee.platform.model.worktime.Vacation;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.platform.dao.worktime.VacationDao;
import com.suneee.platform.model.worktime.Vacation;

/**
 * 对象功能:法定假期设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-20 09:25:49
 */
@Service
public class VacationService extends BaseService<Vacation>
{
	@Resource
	private VacationDao dao;
	
	public VacationService()
	{
	}
	
	@Override
	protected IEntityDao<Vacation, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 根据年月取法定假日。
	 * map 对象存放天数 {1,"五一"}
	 * @return
	 */
	public Map<Integer,String> getByYearMon(String statTime, String endTime){
		
		Map<Integer,String> map=new HashMap<Integer, String>();
		
		Date startDate= TimeUtil.convertString(statTime, "yyyy-MM-dd");
		Date endDate=TimeUtil.convertString(endTime, "yyyy-MM-dd");
		List<Vacation> valist = dao.getByYearMon(startDate, endDate);
		int curMonth = Integer.parseInt(statTime.split("-")[1]);
		for(Vacation va:valist){
			String[] days = DateUtil.getDaysBetweenDate(va.getStatTime().toString(), va.getEndTime().toString());
			for(String day:days){
				int tmpMonth = Integer.parseInt(day.split("-")[1]);
				if(curMonth==tmpMonth){
					map.put(Integer.parseInt(day.substring(8, 10)),va.getName());
				}
			}
		}
		return map;
	}
}

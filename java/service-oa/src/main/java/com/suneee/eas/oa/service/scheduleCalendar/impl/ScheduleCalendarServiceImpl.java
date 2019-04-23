/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ScheduleCalendarServiceImpl
 * Author:   lmr
 * Date:     2018/8/23 11:15
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.service.scheduleCalendar.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.scheduleCalendar.ScheduleCalendarDao;
import com.suneee.eas.oa.model.schedulecalendar.ScheduleCalendar;
import com.suneee.eas.oa.service.scheduleCalendar.ScheduleCalendarService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/8/23
 * @since 1.0.0
 */
@Service
public class ScheduleCalendarServiceImpl extends BaseServiceImpl<ScheduleCalendar> implements ScheduleCalendarService{
    private static final Logger LOGGER = LogManager.getLogger(ScheduleCalendarServiceImpl.class);
    private ScheduleCalendarDao scheduleCalendarDao;

    @Autowired
    public void setScheduleCalendarDao(ScheduleCalendarDao scheduleCalendarDao) {
        this.scheduleCalendarDao = scheduleCalendarDao;
        setBaseDao(scheduleCalendarDao);
    }
    /**
     * 保存 日程表 信息
     * @param scheduleCalendar
     */
    public int save(ScheduleCalendar scheduleCalendar){
        Long id = scheduleCalendar.getId();
        scheduleCalendar.setUpdateBy(ContextSupportUtil.getCurrentUserId());
        scheduleCalendar.setUpdatetime(new Date());
        if(id == null || id == 0){
            id = IdGeneratorUtil.getNextId();
            scheduleCalendar.setId(id);
            scheduleCalendar.setCreateBy(ContextSupportUtil.getCurrentUserId());
            scheduleCalendar.setCreatetime(new Date());
            return scheduleCalendarDao.save(scheduleCalendar);
        }
        else{
            return scheduleCalendarDao.update(scheduleCalendar);
        }
    }

    /**
     * 获取开始时间和结束时间之间的用户日程信息
     * @param startDate
     * @param endDate
     * @return
     */
    public List<ScheduleCalendar> getListBy(Date startDate, Date endDate){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("beginendTime", startDate);
        params.put("endstartTime", endDate);
        params.put("createBy", ContextSupportUtil.getCurrentUserId());
        return scheduleCalendarDao.getListBy(params);
    }

    /**
     * 根据日程ID和用户ID删除日程信息
     * @param id
     * @param id
     */
    public void delBy(Long id){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("createBy", ContextSupportUtil.getCurrentUserId() );
        scheduleCalendarDao.delBy(params);
    }

    /**
     * 根据日程ID和用户ID获取日程信息
     * @param id
     * @param id
     * @return
     */
    public ScheduleCalendar getBy(Long id){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("createBy", ContextSupportUtil.getCurrentUserId() );
        return scheduleCalendarDao.getBy(params);
    }
}
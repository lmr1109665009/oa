/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ScheduleCalendarService
 * Author:   lmr
 * Date:     2018/8/23 11:12
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.service.scheduleCalendar;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.schedulecalendar.ScheduleCalendar;

import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/8/23
 * @since 1.0.0
 */
public interface ScheduleCalendarService extends BaseService<ScheduleCalendar> {

    List<ScheduleCalendar> getListBy(Date startDate, Date endDate);

    void delBy(Long id);

    ScheduleCalendar getBy(Long id);
}

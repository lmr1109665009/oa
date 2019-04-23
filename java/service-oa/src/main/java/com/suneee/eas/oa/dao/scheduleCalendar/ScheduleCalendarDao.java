/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ScheduleCalendarDao
 * Author:   lmr
 * Date:     2018/8/23 11:21
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.dao.scheduleCalendar;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.schedulecalendar.ScheduleCalendar;
import org.springframework.stereotype.Repository;

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
@Repository
public class ScheduleCalendarDao extends BaseDao<ScheduleCalendar>{


    public void delBy(Map<String, Object> params) {
        getSqlSessionTemplate().delete(getNamespace() + ".delBy",params);
    }

    public ScheduleCalendar getBy(Map<String, Object> params) {
        return getSqlSessionTemplate().selectOne(getNamespace() + ".getBy",params);
    }

    public List<ScheduleCalendar> getListBy(Map<String, Object> params) {
        return getSqlSessionTemplate().selectList(getNamespace() + ".getListBy",params);
    }
}
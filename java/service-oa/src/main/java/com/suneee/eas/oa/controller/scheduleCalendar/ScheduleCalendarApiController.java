/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ScheduleCalendarApiController
 * Author:   lmr
 * Date:     2018/8/23 11:26
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.controller.scheduleCalendar;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.DateUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.schedulecalendar.ScheduleCalendar;
import com.suneee.eas.oa.service.scheduleCalendar.ScheduleCalendarService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
    /**
     *<pre>
     * 对象功能:日程表 控制器类
     * 开发公司:深圳象翌微链股份有限公司
     * 开发人员:xiongxianyun
     * 创建时间:2017-06-26 13:36:34
     *</pre>
     */
    @RestController
    @RequestMapping(ModuleConstant.SCHEDULE_MODULE + FunctionConstant.SCHDULE_CALENDAR)
    public class ScheduleCalendarApiController {
        private static final Logger LOGGER = LogManager.getLogger(ScheduleCalendarApiController.class);
        @Resource
        private ScheduleCalendarService scheduleCalendarService;


        /**
         * 添加或更新日程表。
         * @param request
         * @param response
         * @param scheduleCalendar 添加或更新的实体
         * @return
         * @throws Exception
         */
        @RequestMapping("save")
        public ResponseMessage save(HttpServletRequest request, HttpServletResponse response, ScheduleCalendar scheduleCalendar) throws Exception
        {
            try {
                scheduleCalendarService.save(scheduleCalendar);
                return ResponseMessage.success( "日程信息保存成功！", scheduleCalendar.getId());
            } catch (Exception e) {
                LOGGER.error("保存日程信息失败", e);
                return ResponseMessage.fail( "日程信息保存失败！", scheduleCalendar.getId());
            }
        }


        /**
         * 取得日程表分页列表
         * @param request
         * @param response
         * @return
         * @throws Exception
         */
        @RequestMapping("list")
        public ResponseMessage list(HttpServletRequest request,HttpServletResponse response) throws Exception
        {
            // 请求参数校验
            Date startDate = RequestUtil.getDate(request, "startDate", DateUtil.FORMAT_DATE);
            Date endDate = RequestUtil.getDate(request, "endDate",DateUtil.FORMAT_DATE);
            if(startDate == null || endDate == null){
                return ResponseMessage.fail( "请求参数错误");
            }

            try {
                // 查询日程列表
                List<ScheduleCalendar> list = scheduleCalendarService.getListBy(startDate, endDate);
                return ResponseMessage.success("查询日程列表成功", list);
            } catch (Exception e) {
                LOGGER.error("获取日程列表失败", e);
                return ResponseMessage.fail( "查询日程列表失败");
            }
        }

        /**
         * 删除日程表
         * @param request
         * @param response
         * @throws Exception
         */
        @RequestMapping("del")
        public ResponseMessage del(HttpServletRequest request, HttpServletResponse response) throws Exception
        {
            // 请求参数校验
            Long id = RequestUtil.getLong(request, "scheduleId", 0L);
            if(id == 0){
                return ResponseMessage.fail("请求参数错误");
            }

            try{
                // 根据日程ID删除日程信息
                scheduleCalendarService.delBy(id);
                return ResponseMessage.success("删除日程成功!");
            }catch(Exception ex){
                LOGGER.error("删除日程失败", ex);
                return ResponseMessage.fail("删除日程失败！");
            }
        }

        /**
         * 取得日程表明细
         * @param request
         * @param response
         * @return
         * @throws Exception
         */
        @RequestMapping("get")
        public ResponseMessage get(HttpServletRequest request, HttpServletResponse response) throws Exception
        {
            // 请求参数校验
            Long id = RequestUtil.getLong(request, "id", 0L);
            if(id == 0){
                return ResponseMessage.fail("请求参数错误");
            }
            try {
                // 根据日程ID获取日程详情
                ScheduleCalendar scheduleCalendar = scheduleCalendarService.getBy(id);
                return ResponseMessage.success("获取日程详情成功！", scheduleCalendar);
            } catch (Exception e) {
                LOGGER.error("获取日程详情失败", e);
                return ResponseMessage.fail("获取日程详情失败！");
            }
        }
}
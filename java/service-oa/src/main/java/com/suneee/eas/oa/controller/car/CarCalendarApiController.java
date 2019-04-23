/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarCalendarApiController
 * Author:   lmr
 * Date:     2018/9/5 10:13
 * Description: 车辆日历
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.controller.car;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.car.CarArrange;
import com.suneee.eas.oa.model.car.CarInfo;
import com.suneee.eas.oa.service.car.CarArrangeService;
import com.suneee.eas.oa.service.car.CarInfoService;
import com.suneee.platform.calendar.util.CalendarUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈车辆日历〉
 *
 * @author lmr
 * @create 2018/9/5
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.CAR_MODULE+ FunctionConstant.CAR_CALENDAR)
public class CarCalendarApiController {
    private static final Logger LOGGER = LogManager.getLogger(CarCalendarApiController.class);

    @Autowired
    private CarArrangeService carArrangeService;
    @Autowired
    private CarInfoService carInfoService;
    @RequestMapping("list")
    public ResponseMessage list(HttpServletRequest request, HttpServletResponse response){
        String message = null;
        try {
            Date startDate = RequestUtil.getDate(request, "startDate","yyyy-MM-dd");
            Date endDate = RequestUtil.getDate(request, "endDate","yyyy-MM-dd");
            Date startTime = CalendarUtil.getDateBeginTime(startDate);
            Date endTime = CalendarUtil.getDateEndTime(endDate);
            QueryFilter filter = new QueryFilter("listAll",request);
            filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
            List<CarInfo> carList = carInfoService.getListBySqlKey(filter);
            List<Map<String,Object>> resultList = new ArrayList<>();
            //创建查询对象
            QueryFilter queryFilter = new QueryFilter("listAllCa");
            //封装查询参数
            Map<String,Object> params = new HashMap<>();
            params.put("startTime",startTime);
            params.put("endTime",endTime);
            params.put("enterpriseCode",ContextSupportUtil.getCurrentEnterpriseCode());
            //将车辆信息和派车信息一对一封装map中
            for(CarInfo carInfo:carList){
                params.put("carId",carInfo.getCarId());
                queryFilter.setFilters(params);
                //获取该车的所有派车信息
                List<CarArrange> carArrangeList = carArrangeService.getListBySqlKey(queryFilter);
                Map<String,Object> crMap = new HashMap<>();
                if(BeanUtils.isNotEmpty(carInfo)) {
                    crMap.put("carInfo", carInfo);
                    crMap.put("carArrangeList", carArrangeList);
                    resultList.add(crMap);
                }
            }
            return ResponseMessage.success("获取派车列表成功！",resultList);
        } catch (ParseException e) {
            LOGGER.error("获取派车列表失败：" + e.getMessage(), e);
            message = "请求参数格式错误！";
        } catch (IllegalArgumentException e) {
            LOGGER.error("获取派车列表失败：" + e.getMessage(), e);
            message = "请求参数错误！";
        } catch (Exception e) {
            LOGGER.error("获取派车列表失败：" + e.getMessage(), e);
            message = "系统内部错误！";
        }
        return ResponseMessage.fail("获取车辆申请列表失败：" + message);
    }
}
package com.suneee.eas.oa.controller.car;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.car.*;
import com.suneee.eas.oa.service.car.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @user zousiyu
 * @date 2018/9/3 10:21
 */
@RestController
@RequestMapping(ModuleConstant.CAR_MODULE + FunctionConstant.REMIND_INFO)
public class RemindSettingApiController {
    private static final Logger LOGGER = LogManager.getLogger(RemindSettingApiController.class);
    @Autowired
    private CarDriverService carDriverService;

    @Autowired
    private CarInsuranceService carInsuranceService;

    @Autowired
    private CarInspectionService carInspectionService;

    @Autowired
    private CarMaintenanceService carMaintenanceService;

    @Autowired
    private CarRemindSettingService carRemindSettingService;

    /**
     * 获取维修保养数据
     * @param request
     * @return
     */
    @RequestMapping("carMaintenanceListAll")
    public ResponseMessage carMaintenanceListAll(HttpServletRequest request) {
        String enterpriseCode = RequestUtil.getString(request, "enterpriseCode");
        try {
            List<CarInfo> carInfoList = carRemindSettingService.mainAndInfoList(enterpriseCode);
            return ResponseMessage.success("获取当前车辆公程数，下次保养公里数，下次保养时间数据成功！", carInfoList);
        } catch (Exception e) {
            LOGGER.error("获取当前车辆公程数，下次保养公里数，下次保养时间数据失败:" + e.getMessage());
            return ResponseMessage.fail("获取当前车辆公程数，下次保养公里数，下次保养时间数据失败！");
        }
    }
    
    /**
     * 获取年检数据
     * @param request
     * @return
     */
    @RequestMapping("carInspectionListAll")
    public ResponseMessage carInspectionListAll(HttpServletRequest request) {
        QueryFilter queryFilter = new QueryFilter("listAll", request);
        String enterpriseCode = RequestUtil.getString(request, "enterpriseCode");
        queryFilter.addFilter("enterpriseCode",enterpriseCode);
        try {
            List<CarInspection> carInspectionList = carInspectionService.getListBySqlKey(queryFilter);
            return ResponseMessage.success("获取车辆年检数据成功！", carInspectionList);
        } catch (Exception e) {
            LOGGER.error("获取车辆年检数据失败:" + e.getMessage());
            return ResponseMessage.fail("获取车辆年检数据失败！");
        }
    }

    /**
     * 获取车辆保险数据
     * @param request
     * @return
     */
    @RequestMapping("carInsuranceListAll")
    public ResponseMessage carInsuranceListAll(HttpServletRequest request) {
        QueryFilter queryFilter = new QueryFilter("listAll", request);
        String enterpriseCode = RequestUtil.getString(request, "enterpriseCode");
        queryFilter.addFilter("enterpriseCode",enterpriseCode);
        try {

            List<CarInsurance> carInsuranceList = carInsuranceService.getListBySqlKey(queryFilter);
            return ResponseMessage.success("获取车辆保险数据成功！", carInsuranceList);
        } catch (Exception e) {
            LOGGER.error("获取车辆保险数据失败:" + e.getMessage());
            return ResponseMessage.fail("获取车辆保险数据失败！");
        }
    }
    
    /**
     * 获取驾驶员信息列表
     * @param request
     * @return
     */
    @RequestMapping("carDriverListAll")
    public ResponseMessage carDriverListAll(HttpServletRequest request) {
        QueryFilter queryFilter=new QueryFilter("listAll",request);
        String enterpriseCode = RequestUtil.getString(request, "enterpriseCode");
        queryFilter.addFilter("enterpriseCode",enterpriseCode);
        try {
            List<CarDriver> list = carDriverService.getListBySqlKey(queryFilter);
            return ResponseMessage.success("获取列表成功!", list);
        } catch (Exception e) {
            LOGGER.error("获取列表失败!" + e.getMessage());
            return ResponseMessage.fail("获取列表失败!");
        }
    }

    /**
     * 提醒设置信息列表
     * @param request
     * @return
     */
    @RequestMapping("carRemindSettingListAll")
    public ResponseMessage carRemindSettingListAll(HttpServletRequest request) {
        QueryFilter queryFilter=new QueryFilter("listAll",request);
        String enterpriseCode = RequestUtil.getString(request, "enterpriseCode");
        queryFilter.addFilter("enterpriseCode",enterpriseCode);
        try {
            List<CarRemindSetting> list = carRemindSettingService.getListBySqlKey(queryFilter);
            return ResponseMessage.success("获取列表成功!", list);
        } catch (Exception e) {
            LOGGER.error("获取列表失败!" + e.getMessage());
            return ResponseMessage.fail("获取列表失败!");
        }
    }
    
}

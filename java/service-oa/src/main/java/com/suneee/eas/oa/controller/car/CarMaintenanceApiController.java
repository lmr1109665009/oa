/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarMaintenanceController
 * Author:   lmr
 * Date:     2018/8/20 11:25
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.controller.car;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.car.CarInfo;
import com.suneee.eas.oa.model.car.CarMaintenance;
import com.suneee.eas.oa.service.car.CarInfoService;
import com.suneee.eas.oa.service.car.CarMaintenanceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/8/20
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.CAR_MODULE + FunctionConstant.CAR_MAINTENANCE)
public class CarMaintenanceApiController {
    private static final Logger LOGGER = LogManager.getLogger(CarMaintenanceApiController.class);
    @Autowired
    private CarMaintenanceService carMaintenanceService;
    @Autowired
    private CarInfoService carInfoService;
    /**
     * 获取维修保养分页数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("listPage")
    public ResponseMessage listPage(HttpServletRequest request, HttpServletResponse response) {
        QueryFilter queryFilter = new QueryFilter("listAll", request);
        try {
            queryFilter.getFilters().put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
            Pager<CarMaintenance> carPeccancyList = carMaintenanceService.getPageBySqlKey(queryFilter);
            return ResponseMessage.success("获取车辆维护保养数据成功！", carPeccancyList);
        } catch (Exception e) {
            LOGGER.error("获取车辆维护保养数据失败:" + e.getMessage());
            return ResponseMessage.fail("获取车辆维护保养数据失败！");
        }
    }

    /**
     * 添加/更新维修保养信息
     * @param request
     * @param carMaintenance
     * @return
     */
    @RequestMapping("save")
    public ResponseMessage save(HttpServletRequest request, CarMaintenance carMaintenance) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
        Long maintId = carMaintenance.getMaintId();
        Long carId = carMaintenance.getCarId();
        CarInfo carInfo = carInfoService.findById(carId);
        if(BeanUtils.isEmpty(carInfo)){
            LOGGER.error("选择的车辆不存在，请重新选择！");
            return ResponseMessage.fail("选择的车辆不存在，请重新选择！");
        }
        if(carMaintenance.getMaintDate()==null||carMaintenance.getBackDate()==null){
            return ResponseMessage.fail("送修时间或者取回时间不能为空！");
        }
        if(carMaintenance.getBackDate().before(carMaintenance.getMaintDate())){
            return ResponseMessage.fail("取回时间要在送修时间之后，请重新检查！");
        }
        if(carMaintenance.getNextMaintDate()!=null&&carMaintenance.getNextMaintDate().before(carMaintenance.getBackDate())){
            return ResponseMessage.fail("下次保养时间有误，请重新检查！");
        }
        carMaintenance.setCarName(carInfo.getName());
        carMaintenance.setPlateNum(carInfo.getPlateNum());
        try {
            if (maintId == null||maintId == 0) {
                carMaintenanceService.save(carMaintenance);
                responseMessage.setMessage("维护保养信息添加成功！");
            } else {
                carMaintenanceService.update(carMaintenance);
                responseMessage.setMessage("维护保养信息信息更新成功！");
            }
        } catch (Exception e) {
            responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
            if (maintId == null||maintId == 0) {
                responseMessage.setMessage("维护保养信息添加失败！");
            } else {
                responseMessage.setMessage("维护保养信息更新失败！");
            }
            LOGGER.error(responseMessage.getMessage() + ":" + e.getMessage());
        }
        return responseMessage;
    }

    /**
     * 删除维修保养信息
     * @param request
     * @return
     */
    @RequestMapping("delByIds")
    public ResponseMessage listPage(HttpServletRequest request) {
        Long[] maintIds = RequestUtil.getLongAryByStr(request,"maintId");
        if (BeanUtils.isEmpty(maintIds)) {
            return ResponseMessage.fail("删除失败，参数不能为空!");
        }
        try {
            carMaintenanceService.deleteByIds(maintIds);
            return ResponseMessage.success("维护保养信息删除成功!");
        } catch (Exception ex) {
            LOGGER.error("维护保养信息删除失败:" + ex.getMessage());
            return ResponseMessage.fail("维护保养信息删除失败！");
        }
    }

    /**
     * 获取维修保养详情
     * @param request
     * @return
     */
    @RequestMapping("details")
    public ResponseMessage details(HttpServletRequest request) {
        Long maintId = RequestUtil.getLong(request,"maintId");
        if (BeanUtils.isEmpty(maintId)) {
            return ResponseMessage.fail("获取维修保养详情失败，参数不能为空!");
        }
        try {
            CarMaintenance carMaintenance = carMaintenanceService.findById(maintId);
            return ResponseMessage.success("获取维修保养详情成功!",carMaintenance);
        } catch (Exception ex) {
            LOGGER.error("获取维修保养详情失败:" + ex.getMessage());
            return ResponseMessage.fail("获取维修保养详情失败！");
        }
    }
}
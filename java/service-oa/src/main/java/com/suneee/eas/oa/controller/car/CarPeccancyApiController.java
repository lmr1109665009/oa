/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarPeccancyApiController
 * Author:   lmr
 * Date:     2018/8/17 16:52
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
import com.suneee.eas.oa.model.car.CarDriver;
import com.suneee.eas.oa.model.car.CarInfo;
import com.suneee.eas.oa.model.car.CarPeccancy;
import com.suneee.eas.oa.service.car.CarDriverService;
import com.suneee.eas.oa.service.car.CarInfoService;
import com.suneee.eas.oa.service.car.CarPeccancyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/8/17
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.CAR_MODULE + FunctionConstant.CAR_PECCANCY)
public class CarPeccancyApiController {
    private static final Logger LOGGER = LogManager.getLogger(CarPeccancyApiController.class);
    @Autowired
    private CarPeccancyService carPeccancyService;
    @Autowired
    private CarInfoService carInfoService;
    @Autowired
    private CarDriverService carDriverService;

    /**
     * 获取违章事故分页数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("listPage")
    public ResponseMessage listPage(HttpServletRequest request, HttpServletResponse response) {
        QueryFilter queryFilter = new QueryFilter("listAll", request);
        try {
            queryFilter.getFilters().put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
            Pager<CarPeccancy> carPeccancyList = carPeccancyService.getPageBySqlKey(queryFilter);
            return ResponseMessage.success("获取车辆违章事故数据成功！", carPeccancyList);
        } catch (Exception e) {
            LOGGER.error("获取车辆违章事故数据失败:" + e.getMessage());
            return ResponseMessage.fail("获取车辆违章事故数据失败！");
        }
    }

    /**
     * 添加/更新年检信息
     * @param request
     * @param carPeccancy
     * @return
     */
    @RequestMapping("save")
    public ResponseMessage save(HttpServletRequest request, CarPeccancy carPeccancy) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
        Long peccId = carPeccancy.getPeccId();
        Long carId = carPeccancy.getCarId();
        CarInfo carInfo = carInfoService.findById(carId);
        if(carPeccancy.getPeccTime().getTime() > new Date().getTime()){
            LOGGER.error("违规时间不能大于当前时间，请重新选择！");
            return ResponseMessage.fail("违规时间不能大于当前时间，请重新选择！");
        }
        if(BeanUtils.isEmpty(carInfo)){
            LOGGER.error("选择的车辆不存在，请重新选择！");
            return ResponseMessage.fail("选择的车辆不存在，请重新选择！");
        }
        Long driverId = carPeccancy.getDriverId();
        CarDriver carDriver = carDriverService.findById(driverId);
        if(BeanUtils.isEmpty(carDriver)){
            LOGGER.error("选择的驾驶员不存在，请重新选择！");
            return ResponseMessage.fail("选择的驾驶员不存在，请重新选择！");
        }
        carPeccancy.setCarName(carInfo.getName());
        carPeccancy.setPlateNum(carInfo.getPlateNum());
        try {
            if (peccId == null||peccId == 0) {
                carPeccancyService.save(carPeccancy);
                responseMessage.setMessage("违章事故信息添加成功！");
            } else {
                carPeccancyService.update(carPeccancy);
                responseMessage.setMessage("违章事故信息信息更新成功！");
            }
        } catch (Exception e) {
            responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
            if (peccId == null||peccId == 0) {
                responseMessage.setMessage("违章事故信息添加失败！");
            } else {
                responseMessage.setMessage("违章事故信息更新失败！");
            }
            LOGGER.error(responseMessage.getMessage() + ":" + e.getMessage());
        }
        return responseMessage;
    }

    /**
     * 删除年检信息
     * @param request
     * @return
     */
    @RequestMapping("delByIds")
    public ResponseMessage listPage(HttpServletRequest request) {
        Long[] peccIds = RequestUtil.getLongAryByStr(request,"peccId");
        if (BeanUtils.isEmpty(peccIds)) {
            return ResponseMessage.fail("删除失败，参数不能为空!");
        }
        try {
            carPeccancyService.deleteByIds(peccIds);
            return ResponseMessage.success("违章事故信息删除成功!");
        } catch (Exception ex) {
            LOGGER.error("违章事故信息删除失败:" + ex.getMessage());
            return ResponseMessage.fail("违章事故信息删除失败！");
        }
    }
    /**
     * 获取违章事故详情
     * @param request
     * @return
     */
    @RequestMapping("details")
    public ResponseMessage details(HttpServletRequest request) {
        Long peccId = RequestUtil.getLong(request,"peccId");
        if (BeanUtils.isEmpty(peccId)) {
            return ResponseMessage.fail("获取违章事故详情失败，参数不能为空!");
        }
        try {
            CarPeccancy carPeccancy = carPeccancyService.findById(peccId);
            return ResponseMessage.success("获取违章事故详情成功!",carPeccancy);
        } catch (Exception ex) {
            LOGGER.error("获取违章事故详情失败:" + ex.getMessage());
            return ResponseMessage.fail("获取违章事故详情失败！");
        }
    }
}
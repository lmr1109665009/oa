/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarInspectionApiController
 * Author:   lmr
 * Date:     2018/8/17 14:13
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
import com.suneee.eas.oa.model.car.CarInspection;
import com.suneee.eas.oa.service.car.CarInfoService;
import com.suneee.eas.oa.service.car.CarInspectionService;
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
 * @create 2018/8/17
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.CAR_MODULE + FunctionConstant.CAR_INSPECTION)
public class CarInspectionApiController {
    private static final Logger LOGGER = LogManager.getLogger(CarInspectionApiController.class);
    @Autowired
    private CarInspectionService carInspectionService;
    @Autowired
    private CarInfoService carInfoService;

    /**
     * 获取年检分页数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("listPage")
    public ResponseMessage listPage(HttpServletRequest request, HttpServletResponse response) {
        QueryFilter queryFilter = new QueryFilter("listAll", request);
        try {
            queryFilter.getFilters().put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
            Pager<CarInspection> carInspectionList = carInspectionService.getPageBySqlKey(queryFilter);
            return ResponseMessage.success("获取车辆年检数据成功！", carInspectionList);
        } catch (Exception e) {
            LOGGER.error("获取车辆年检数据失败:" + e.getMessage());
            return ResponseMessage.fail("获取车辆年检数据失败！");
        }
    }

    /**
     * 添加/更新年检信息
     * @param request
     * @param carInspection
     * @return
     */
    @RequestMapping("save")
    public ResponseMessage save(HttpServletRequest request, CarInspection carInspection) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
        Long inspId = carInspection.getInspId();
        Long carId= carInspection.getCarId();
        CarInfo carInfo = carInfoService.findById(carId);
        if(BeanUtils.isEmpty(carInfo)){
            LOGGER.error("选择的车辆不存在，请重新选择！");
            return ResponseMessage.fail("选择的车辆不存在，请重新选择！");
        }
        carInspection.setCarName(carInfo.getName());
        carInspection.setPlateNum(carInfo.getPlateNum());
        try {
            if (inspId == null||inspId == 0) {
                carInspectionService.save(carInspection);
                responseMessage.setMessage("年检信息添加成功！");
            } else {
                carInspectionService.update(carInspection);
                responseMessage.setMessage("年检信息更新成功！");
            }
        } catch (Exception e) {
            responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
            if (inspId == null||inspId == 0) {
                responseMessage.setMessage("年检信息添加失败！");
            } else {
                responseMessage.setMessage("年检信息更新失败！");
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
        Long[] inspIds = RequestUtil.getLongAryByStr(request,"inspId");
        if (BeanUtils.isEmpty(inspIds)) {
            return ResponseMessage.fail("删除失败，参数不能为空!");
        }
        try {
            carInspectionService.deleteByIds(inspIds);
            return ResponseMessage.success("年检信息删除成功!");
        } catch (Exception ex) {
            LOGGER.error("年检信息删除失败:" + ex.getMessage());
            return ResponseMessage.fail("年检信息删除失败！");
        }
    }

    /**
     * 获取年检详情
     * @param request
     * @return
     */
    @RequestMapping("details")
    public ResponseMessage details(HttpServletRequest request) {
        Long inspId = RequestUtil.getLong(request,"inspId");
        if (BeanUtils.isEmpty(inspId)) {
            return ResponseMessage.fail("获取详情失败，参数不能为空!");
        }
        try {
            CarInspection carInspection = carInspectionService.findById(inspId);
            return ResponseMessage.success("获取年检详情成功!",carInspection);
        } catch (Exception ex) {
            LOGGER.error("获取年检详情失败:" + ex.getMessage());
            return ResponseMessage.fail("获取年检详情失败！");
        }
    }
}
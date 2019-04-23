/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarInsuranceApiController
 * Author:   lmr
 * Date:     2018/8/16 13:20
 * Description: 保险
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
import com.suneee.eas.oa.model.car.CarInsurance;
import com.suneee.eas.oa.service.car.CarInfoService;
import com.suneee.eas.oa.service.car.CarInsuranceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 〈一句话功能简述〉<br>
 * 〈保险〉
 *
 * @author lmr
 * @create 2018/8/16
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.CAR_MODULE + FunctionConstant.CAR_INSURANCE)
public class CarInsuranceApiController {
    private static final Logger LOGGER = LogManager.getLogger(CarInsuranceApiController.class);
    @Autowired
    private CarInsuranceService carInsuranceService;
    @Autowired
    private CarInfoService carInfoService;

    /**
     * 获取车辆保险分页数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("listPage")
    public ResponseMessage listPage(HttpServletRequest request, HttpServletResponse response) {
        QueryFilter queryFilter = new QueryFilter("listAll", request);
        try {
            queryFilter.getFilters().put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
            Pager<CarInsurance> carInsuranceList = carInsuranceService.getPageBySqlKey(queryFilter);
            return ResponseMessage.success("获取车辆保险数据成功！", carInsuranceList);
        } catch (Exception e) {
            LOGGER.error("获取车辆保险数据失败:" + e.getMessage());
            return ResponseMessage.fail("获取车辆保险数据失败！");
        }
    }

    /**
     * 添加/更新保险信息
     *
     * @param request
     * @param carInsurance
     * @return
     */
    @RequestMapping("save")
    public ResponseMessage save(HttpServletRequest request, CarInsurance carInsurance) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
        Long insurId = carInsurance.getInsurId();
        String insurNum = carInsurance.getInsurNum();
        Long carId = carInsurance.getCarId();
        CarInfo carInfo = carInfoService.findById(carId);
        if(BeanUtils.isEmpty(carInfo)){
            LOGGER.error("选择的车辆不存在，请重新选择！");
            return ResponseMessage.fail("选择的车辆不存在，请重新选择！");
        }
        carInsurance.setCarName(carInfo.getName());
        carInsurance.setPlateNum(carInfo.getPlateNum());
        if (BeanUtils.isEmpty(insurNum)) {
            return ResponseMessage.fail("保险单号不能为空！");
        }
        try {
            if (insurId == null||insurId == 0) {
                boolean flag = carInsuranceService.isInsurNumRepeatForAdd(insurNum);
                if (flag) {
                    return ResponseMessage.fail("保险单号已存在，请重新输入！");
                }
                carInsuranceService.save(carInsurance);
                responseMessage.setMessage("保险信息添加成功！");
            } else {
                //判断保险单号是否重复
                boolean flag = carInsuranceService.isInsurNumRepeatForUpdate(insurId, insurNum);
                if (flag) {
                    return ResponseMessage.fail("保险单号已存在，请重新输入！");
                }
                carInsuranceService.update(carInsurance);
                responseMessage.setMessage("保险信息更新成功！");
            }
        } catch (Exception e) {
            responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
            if (insurId == null||insurId == 0) {
                responseMessage.setMessage("保险信息添加失败！");
            } else {
                responseMessage.setMessage("保险信息更新失败！");
            }
            LOGGER.error(responseMessage.getMessage() + ":" + e.getMessage());
        }
        return responseMessage;
    }

    /**
     * 删除保险信息
     * @param request
     * @return
     */
    @RequestMapping("delByIds")
    public ResponseMessage listPage(HttpServletRequest request) {
        Long[] insurIds = RequestUtil.getLongAryByStr(request, "insurId");
        if (BeanUtils.isEmpty(insurIds)) {
            return ResponseMessage.fail("删除失败，参数不能为空!");
        }
        try {
            carInsuranceService.deleteByIds(insurIds);
            return ResponseMessage.success("保险信息删除成功!");
        } catch (Exception ex) {
            LOGGER.error("保险信息删除失败:" + ex.getMessage());
            return ResponseMessage.fail("保险信息删除失败！");
        }
    }

    /**
     * 获取保险详情
     * @param request
     * @return
     */
    @RequestMapping("details")
    public ResponseMessage details(HttpServletRequest request) {
        Long insurId = RequestUtil.getLong(request,"insurId");
        if (BeanUtils.isEmpty(insurId)) {
            return ResponseMessage.fail("获取保险详情失败，参数不能为空!");
        }
        try {
            CarInsurance carInsurance = carInsuranceService.findById(insurId);
            return ResponseMessage.success("获取保险详情成功!",carInsurance);
        } catch (Exception ex) {
            LOGGER.error("获取保险详情失败:" + ex.getMessage());
            return ResponseMessage.fail("获取保险详情失败！");
        }
    }
}
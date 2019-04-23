package com.suneee.eas.oa.controller.car;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.component.schedule.ParameterObj;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.car.CarRemindSetting;
import com.suneee.eas.oa.service.car.CarRemindSettingService;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @user zousiyu
 * @date 2018/8/23 15:34
 */
@RestController
@RequestMapping(ModuleConstant.CAR_MODULE+ FunctionConstant.REMIND_INFO)
public class CarRemindSettingApiController {
    private static final Logger LOGGER = LogManager.getLogger(CarRemindSettingApiController.class);
    @Autowired
    private CarRemindSettingService carRemindSettingService;

    /**
     * 获取提醒设置列表
     * @param request
     * @return
     */
    @RequestMapping("listPage")
    public ResponseMessage listPage(HttpServletRequest request){
        QueryFilter queryFilter = new QueryFilter("listAll",request);
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        queryFilter.addFilter("enterpriseCode",enterpriseCode);
        Pager<CarRemindSetting> page = carRemindSettingService.getPageBySqlKey(queryFilter);
        return ResponseMessage.success("获取提醒设置列表成功!",page);
    }

    /**
     * 添加/修改提醒设置
     * @param request
     * @return
     */
    @RequestMapping("save")
    public ResponseMessage save(HttpServletRequest request){
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        String str = RequestUtil.getString(request,"paramJson");
        JSONArray aryJson = JSONArray.fromObject(str);
        CarRemindSetting[] list = (CarRemindSetting[]) aryJson.toArray(aryJson, CarRemindSetting.class);
        ResponseMessage responseMessage = new ResponseMessage();
        int num = 0;
        for(CarRemindSetting crs:list){
            if(!"1".equals(crs.getFlag()) ){
                num++;
            }
        }
        if(num == 0 && list[0].getId() == 0){
            return ResponseMessage.fail("请至少选中一条提醒内容！");
        }
        for (int i = 0; i < list.length; i++) {
            CarRemindSetting carRemindSetting = (CarRemindSetting) list[i];
            Long id = carRemindSetting.getId();
            String name = carRemindSetting.getName();
            //提醒名称不能重复
            boolean count = carRemindSettingService.countByName(name,enterpriseCode);
            if(name.contains("保养"))
                carRemindSetting.setAlias("com.suneee.eas.schedule.job.oa.ComputeCarMaintenanceExpireDateJob");
            if(name.contains("年检"))
                carRemindSetting.setAlias("com.suneee.eas.schedule.job.oa.ComputeCarInspectionExpireDateJob");
            if(name.contains("保险"))
                carRemindSetting.setAlias("com.suneee.eas.schedule.job.oa.ComputeCarInsuranceExpireDateJob");
            if(name.contains("驾驶证"))
                carRemindSetting.setAlias("com.suneee.eas.schedule.job.oa.ComputeDriverLisenceValidDateJob");
            try {
                if(id==0 && !count){
                    //提醒天数不能为负数
                    if(Integer.parseInt(carRemindSetting.getParams())<0){
                        responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
                        responseMessage.setMessage("提醒天数不能为负数!");
                        throw new IllegalArgumentException("提醒天数不能为负数!");
                    }
                    carRemindSettingService.save(carRemindSetting);
                    responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
                    responseMessage.setMessage("添加提醒设置成功!");
                } else
                {
                    //提醒天数不能为负数
                    if("0".equals(carRemindSetting.getFlag())&&Integer.parseInt(carRemindSetting.getParams())<0){
                        responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
                        responseMessage.setMessage("提醒天数不能为负数!");
                        throw new IllegalArgumentException("提醒天数不能为负数!");
                    }
                    carRemindSettingService.update(carRemindSetting);
                    responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
                    responseMessage.setMessage("修改提醒设置成功!");
                }
             }catch (Exception e) {
                e.printStackTrace();
                if (id==0 && !count) {
                    LOGGER.error("添加提醒设置失败!",e.getMessage());
                    responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
                    responseMessage.setMessage("添加提醒设置失败!");
                } else {
                    LOGGER.error("修改提醒设置失败!",e.getMessage());
                    responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
                    responseMessage.setMessage("修改提醒设置失败!");
                }
            }
        }
        return responseMessage;
    }

    /**
     * 提醒设置详情/修改
     * @param request
     * @return
     */
    @RequestMapping("edit")
    public ResponseMessage edit(HttpServletRequest request){
        Long id = RequestUtil.getLong(request,"id");
        if(id==null){
            return ResponseMessage.fail("参数不能为空!");
        }
        CarRemindSetting carRemindSetting = null;
        try {
            carRemindSetting = carRemindSettingService.findById(id);
            return ResponseMessage.success("获取提醒设置详情成功!", carRemindSetting);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取提醒设置失败!",e.getMessage());
            return ResponseMessage.fail("获取提醒设置失败!");
        }
    }

    /**
     * 根据ID删除提醒设置
     * @param request
     * @return
     */
    @RequestMapping("delById")
    public ResponseMessage delById(HttpServletRequest request){
        Long[] ids = RequestUtil.getLongAryByStr(request, "id");
        if(ids==null || ids.length==0){
            return ResponseMessage.fail("参数不能为空!");
        }
        try {
            for (Long id :ids){
                carRemindSettingService.deleteById(id);
            }
            return ResponseMessage.fail("删除提醒设置成功!");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("删除提醒设置失败!",e.getMessage());
            return ResponseMessage.fail("删除提醒设置失败!");
        }

    }

    
}

package com.suneee.eas.oa.controller.car;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.car.CarDriver;
import com.suneee.eas.oa.service.car.CarDriverService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**    
 * @user zousiyu
 * @date 2018/8/16 15:25
 */
@RestController
@RequestMapping(ModuleConstant.CAR_MODULE + FunctionConstant.DRIVER_INFO)
public class CarDriverApiController {
    private static final Logger LOGGER = LogManager.getLogger(CarDriverApiController.class);
    @Autowired
    private CarDriverService carDriverService;

    /**
     * 获取驾驶员信息分页列表
     * @param request
     * @return
     */
    @RequestMapping("listPage")
    public ResponseMessage listPage(HttpServletRequest request) {
        QueryFilter queryFilter = new QueryFilter("listAll", request);
        queryFilter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today1 = sd.format(date);
        Date today = null;
        Long betweenDate = null;

        try {
            today = sd.parse(today1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        try {
            Pager<CarDriver> page = carDriverService.getPageBySqlKey(queryFilter);
            List<CarDriver> list = page.getList();
            for (CarDriver carDriver : list){
                Date licenseDate = carDriver.getLicenseDate();
                betweenDate = (today.getTime() - licenseDate.getTime()) / (1000L * 60L * 60L * 24L * 365L);
                carDriver.setDrivingAge(betweenDate);
            }
            page.setList(list);
            return ResponseMessage.success("获取列表成功!", page);
        } catch (Exception e) {
            LOGGER.error("获取列表失败!" + e.getMessage());
            return ResponseMessage.fail("获取列表失败!");
        }
        
    }


    /**
     * 获取驾驶员信息列表
     * @param request
     * @return
     */
    @RequestMapping("listAll")
    public ResponseMessage listAll(HttpServletRequest request) {
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
     * 新增/修改驾驶员信息
     * @param request
     * @param carDriver
     * @return
     */
    @RequestMapping("save")
    public ResponseMessage save(HttpServletRequest request, CarDriver carDriver) {
        //主键Id
        Long id =carDriver.getId();
        //驾驶证初次领证时间日期
        String licenseDate = RequestUtil.getString(request,"licenseDate");
        //驾驶证证件有效期日期
        String validDate = RequestUtil.getString(request,"validDate");
        //驾驶员id
        Long driverId = carDriver.getDriverId();
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        //驾驶员不能重复
        boolean isEx = carDriverService.countByDriverId(driverId, enterpriseCode);
        
        try {
            if (id==0) {
                if(isEx){
                    return ResponseMessage.fail("驾驶员已经存在!");
                }
                carDriverService.save(carDriver,licenseDate,validDate);
                return ResponseMessage.success("新增驾驶员信息成功!");
            } else {
                carDriverService.update(carDriver,licenseDate,validDate);
                return ResponseMessage.success("修改驾驶员信息成功!");
            }
        }catch (IllegalArgumentException ie){
            return ResponseMessage.fail(ie.getMessage());
        } catch (Exception e) {
            if (id ==0) {
                LOGGER.error("新增驾驶员信息失败!" + e.getMessage());
                return ResponseMessage.fail("新增驾驶员信息失败!");
            } else {
                LOGGER.error("修改驾驶员信息失败!" + e.getMessage());
                return ResponseMessage.fail("修改驾驶员信息失败!");
            }
        }
    }

    /**
     * 根据id 删除驾驶员信息
     * @param request
     * @return
     */
    @RequestMapping("deleteByIds")
    public ResponseMessage deleteByIds(HttpServletRequest request){
        Long[] ids = RequestUtil.getLongAryByStr(request, "id");
        if (ids==null||ids.length==0) {
            return  ResponseMessage.fail("删除失败!参数不能为空!");
        }
        try {
            for (Long id:ids){
                carDriverService.deleteById(id);
            }
            return  ResponseMessage.success("删除驾驶员信息成功!");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("删除驾驶员信息失败!",e.getMessage());
            return ResponseMessage.fail("删除驾驶员信息失败!");
        }

    }

    /**
     * 获取驾驶员详情
     * @param request
     * @return
     */
    @RequestMapping("edit")
    public ResponseMessage edit(HttpServletRequest request){
        Long id = RequestUtil.getLong(request,"id");
        if(id==0){
            return  ResponseMessage.fail("获取失败!参数不能为空!");  
        }
        try {
            CarDriver carDriver = carDriverService.findById(id);
            return ResponseMessage.success("获取驾驶员详情成功!",carDriver);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取驾驶员详情失败!",e.getMessage());
            return ResponseMessage.fail("获取驾驶员详情失败!");
        }
    }
    
}

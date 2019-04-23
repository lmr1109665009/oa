package com.suneee.eas.oa.service.car.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.oa.dao.car.CarDriverDao;
import com.suneee.eas.oa.model.car.CarDriver;
import com.suneee.eas.oa.service.car.CarDriverService;
import com.suneee.eas.oa.service.user.UserService;
import com.suneee.platform.model.system.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @user zousiyu
 * @date 2018/8/16 15:02
 */
@Service
public class CarDriverServiceImpl extends BaseServiceImpl<CarDriver> implements CarDriverService {
    
    private CarDriverDao carDriverDao;

    @Resource
    private UserService userService;

    @Autowired
    public void setCarDriverDao(CarDriverDao carDriverDao) {
        this.carDriverDao = carDriverDao;
        setBaseDao(carDriverDao);
    }
    

    @Override
    public void save(CarDriver carDriver,String licenseDate,String validDate) {
        //获取主键id
        Long id = IdGeneratorUtil.getNextId();
        //获取当前企业编码
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        //获取当前用户id
        Long currrentId = ContextSupportUtil.getCurrentUserId();
        //获取当前时间
        Date date = new Date();
        Date licenseDate1 = null;
        Date validDate1 = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            licenseDate1 = dateFormat.parse(licenseDate);
            validDate1 = dateFormat.parse(validDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(validDate1.before(licenseDate1)){
            throw new IllegalArgumentException("证件有效期不能小于初次领证时间!");
        }
       /* if(licenseDate1.after(date)){
            throw new IllegalArgumentException("初次领证时间不能大于当前时间!");
        }
        if(validDate1.before(date)){
            throw new IllegalArgumentException("证件有效期不能小于当前时间!");
        }*/
        if(StringUtil.isEmpty(carDriver.getMobile())){
            try {
                SysUser user = userService.getUserDetails(carDriver.getDriverId());
                carDriver.setMobile(user.getMobile());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        carDriver.setId(id);
        carDriver.setEnterpriseCode(enterpriseCode);
        carDriver.setCreateBy(currrentId);
        carDriver.setCreateTime(date);
        carDriver.setLicenseDate(licenseDate1);
        carDriver.setValidDate(validDate1);

         carDriverDao.save(carDriver);
    }
   

    @Override
    public void update(CarDriver carDriver,String licenseDate,String validDate) {
        //获取当前企业编码
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        //获取当前用户id
        Long currrentId = ContextSupportUtil.getCurrentUserId();
        //获取当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //将驾驶证初次领证时间日期字符串转化为date类型
        Date licenseDate1 = null;
        Date validDate1 = null;
        try {
            licenseDate1 = dateFormat.parse(licenseDate);
            validDate1 = dateFormat.parse(validDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(validDate1.before(licenseDate1)){
            throw new IllegalArgumentException("证件有效期不能小于初次领证时间!");
        }
        /*if(licenseDate1.after(date)){
            throw new IllegalArgumentException("初次领证时间不能大于当前时间!");
        }
        if(validDate1.before(date)){
            throw new IllegalArgumentException("证件有效期不能小于当前时间!");
        }*/
        carDriver.setEnterpriseCode(enterpriseCode);
        carDriver.setUpdateBy(currrentId);
        carDriver.setUpdateTime(date);
        carDriver.setLicenseDate(licenseDate1);
        carDriver.setValidDate(validDate1);
         carDriverDao.update(carDriver);
    }

    @Override
    public  boolean countByDriverId(Long driverId ,String enterpriseCode){
        Map<String,Object> params = new HashMap<>();
        params.put("driverId", driverId);
        params.put("enterpriseCode", enterpriseCode);
        int count = carDriverDao.countByDriverId(params);
        if (count>0) {
            return true;
        } else {
            return false;
        }
    }
}

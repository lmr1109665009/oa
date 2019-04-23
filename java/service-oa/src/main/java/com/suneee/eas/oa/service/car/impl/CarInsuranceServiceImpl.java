/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarInsuranceServiceImpl
 * Author:   lmr
 * Date:     2018/8/16 13:41
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.service.car.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.car.CarInsuranceDao;
import com.suneee.eas.oa.model.car.CarInsurance;
import com.suneee.eas.oa.service.car.CarInsuranceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author lmr
 * @create 2018/8/16
 * @since 1.0.0
 */
@Service
public class CarInsuranceServiceImpl extends BaseServiceImpl<CarInsurance> implements CarInsuranceService {
    private static final Logger LOGGER = LogManager.getLogger(CarInsuranceServiceImpl.class);
    private CarInsuranceDao carInsuranceDao;

    @Autowired
    public void setCarInsuranceDao(CarInsuranceDao carInsuranceDao) {
        this.carInsuranceDao = carInsuranceDao;
        setBaseDao(carInsuranceDao);
    }

    /**
     * 查看添加时是否有相同的保险单号
     * @param insurNum
     * @return
     */
    @Override
    public boolean isInsurNumRepeatForAdd(String insurNum) {
        if(BeanUtils.isEmpty(insurNum)){
            LOGGER.error("insurNum can not be empty.");
        }
        Map<String,Object> params = new HashMap<>();
        params.put("insurNum",insurNum);
        int count = carInsuranceDao.isInsurNumRepeatForAdd(params);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 查看更新时是否有相同的保险单号
     * @param insurId
     * @param insurNum
     * @return
     */
    @Override
    public boolean isInsurNumRepeatForUpdate(Long insurId, String insurNum) {
        if(BeanUtils.isEmpty(insurNum)||BeanUtils.isEmpty(insurId)){
            LOGGER.error("insurNum and insurId can not be empty.");
        }
        Map<String,Object> params = new HashMap<>();
        params.put("insurId",insurId);
        params.put("insurNum",insurNum);
        int count = carInsuranceDao.isInsurNumRepeatForUpdate(params);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteByIds(Long[] insurIds) {
        if(BeanUtils.isEmpty(insurIds)||insurIds.length==0){
            LOGGER.error("parameter cannot be empty.");
        }
        for (Long insurId: insurIds) {
            deleteById(insurId);
        }
    }
    @Override
    public int deleteById(Long id) {
        Map<String,Object> params = new HashMap<>();
        params.put("updateBy", ContextSupportUtil.getCurrentUserId());
        params.put("updateTime",new Date());
        params.put("insurId",id);
        return carInsuranceDao.deleteById(params);
    }

    @Override
    public int save(CarInsurance carInsurance) {
        //生成唯一主键ID
        Long id = IdGeneratorUtil.getNextId();
        carInsurance.setInsurId(id);
        carInsurance.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
        //设置创建人、更新人
        Long currrentUserId = ContextSupportUtil.getCurrentUserId();
        String aliasName = ContextSupportUtil.getCurrentUser().getAliasName();
        carInsurance.setCreateBy(currrentUserId);
        carInsurance.setUpdateBy(currrentUserId);
        //获取当前时间
        Date date = new Date();
        carInsurance.setCreateTime(date);
        carInsurance.setUpdateTime(date);
        return carInsuranceDao.save(carInsurance);
    }

    @Override
    public int update(CarInsurance carInsurance) {
        //设置更新人更新人
        carInsurance.setUpdateBy(ContextSupportUtil.getCurrentUserId());
        //设置更新时间
        carInsurance.setUpdateTime(new Date());
        return carInsuranceDao.update(carInsurance);
    }
}
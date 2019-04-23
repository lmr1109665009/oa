/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarMaintenanceServiceImpl
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
import com.suneee.eas.oa.dao.car.CarMaintenanceDao;
import com.suneee.eas.oa.model.car.CarMaintenance;
import com.suneee.eas.oa.service.car.CarMaintenanceService;
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
public class CarMaintenanceServiceImpl extends BaseServiceImpl<CarMaintenance> implements CarMaintenanceService {
    private static final Logger LOGGER = LogManager.getLogger(CarMaintenanceServiceImpl.class);
    private CarMaintenanceDao carMaintenanceDao;

    @Autowired
    public void setCarMaintenanceDao(CarMaintenanceDao carMaintenanceDao) {
        this.carMaintenanceDao = carMaintenanceDao;
        setBaseDao(carMaintenanceDao);
    }
    @Override
    public void deleteByIds(Long[] maintIds) {
        if(BeanUtils.isEmpty(maintIds)||maintIds.length==0){
            LOGGER.error("parameter cannot be empty.");
        }
        for (Long maintId: maintIds) {
            deleteById(maintId);
        }
    }
    @Override
    public int deleteById(Long id) {
        Map<String,Object> params = new HashMap<>();
        params.put("updateBy", ContextSupportUtil.getCurrentUserId());
        params.put("updateTime",new Date());
        params.put("maintId",id);
        return carMaintenanceDao.deleteById(params);
    }

    @Override
    public int save(CarMaintenance carMaintenance) {
        //生成唯一主键ID
        Long id = IdGeneratorUtil.getNextId();
        carMaintenance.setMaintId(id);
        carMaintenance.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
        //设置创建人、更新人
        Long currrentUserId = ContextSupportUtil.getCurrentUserId();
        String aliasName = ContextSupportUtil.getCurrentUser().getAliasName();
        carMaintenance.setCreateBy(currrentUserId);
        carMaintenance.setUpdateBy(currrentUserId);
        //获取当前时间
        Date date = new Date();
        carMaintenance.setCreateTime(date);
        carMaintenance.setUpdateTime(date);
        return carMaintenanceDao.save(carMaintenance);
    }

    @Override
    public int update(CarMaintenance carMaintenance) {
        //设置更新人更新人
        carMaintenance.setUpdateBy(ContextSupportUtil.getCurrentUserId());
        //设置更新时间
        carMaintenance.setUpdateTime(new Date());
        return carMaintenanceDao.update(carMaintenance);
    }
}
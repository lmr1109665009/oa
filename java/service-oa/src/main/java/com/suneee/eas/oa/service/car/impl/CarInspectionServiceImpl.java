/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarInspectionServiceImpl
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
import com.suneee.eas.oa.dao.car.CarInspectionDao;
import com.suneee.eas.oa.model.car.CarInspection;
import com.suneee.eas.oa.service.car.CarInspectionService;
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
public class CarInspectionServiceImpl extends BaseServiceImpl<CarInspection> implements CarInspectionService {
    private static final Logger LOGGER = LogManager.getLogger(CarInspectionServiceImpl.class);
    private CarInspectionDao carInspectionDao;

    @Autowired
    public void setCarInspectionDao(CarInspectionDao carInspectionDao) {
        this.carInspectionDao = carInspectionDao;
        setBaseDao(carInspectionDao);
    }
    @Override
    public int save(CarInspection carInspection) {
        //生成唯一主键ID
        Long id = IdGeneratorUtil.getNextId();
        carInspection.setInspId(id);
        carInspection.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
        //设置创建人、更新人
        Long currrentUserId = ContextSupportUtil.getCurrentUserId();
        String aliasName = ContextSupportUtil.getCurrentUser().getAliasName();
        carInspection.setCreateBy(currrentUserId);
        carInspection.setUpdateBy(currrentUserId);
        //获取当前时间
        Date date = new Date();
        carInspection.setCreateTime(date);
        carInspection.setUpdateTime(date);
        return carInspectionDao.save(carInspection);
    }

    @Override
    public int update(CarInspection carInspection) {
        //设置更新人更新人
        carInspection.setUpdateBy(ContextSupportUtil.getCurrentUserId());
        //设置更新时间
        carInspection.setUpdateTime(new Date());
        return carInspectionDao.update(carInspection);
    }
    @Override
    public void deleteByIds(Long[] inspIds) {
        if(BeanUtils.isEmpty(inspIds)||inspIds.length==0){
            LOGGER.error("parameter cannot be empty.");
        }
        for (Long inspId: inspIds) {
            deleteById(inspId);
        }
    }
    @Override
    public int deleteById(Long id) {
        Map<String,Object> params = new HashMap<>();
        params.put("updateBy", ContextSupportUtil.getCurrentUserId());
        params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
        params.put("updateTime",new Date());
        params.put("inspId",id);
        return carInspectionDao.deleteById(params);
    }
}
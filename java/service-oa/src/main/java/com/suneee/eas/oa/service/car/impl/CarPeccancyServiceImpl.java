/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CarPeccancyServiceImpl
 * Author:   lmr
 * Date:     2018/8/16 13:42
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
import com.suneee.eas.oa.dao.car.CarPeccancyDao;
import com.suneee.eas.oa.model.car.CarPeccancy;
import com.suneee.eas.oa.service.car.CarPeccancyService;
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
public class CarPeccancyServiceImpl extends BaseServiceImpl<CarPeccancy> implements CarPeccancyService {
    private static final Logger LOGGER = LogManager.getLogger(CarPeccancyServiceImpl.class);
    private CarPeccancyDao carPeccancyDao;
    @Autowired
    public void setCarPeccancyDao(CarPeccancyDao carPeccancyDao) {
        this.carPeccancyDao = carPeccancyDao;
        setBaseDao(carPeccancyDao);
    }

    @Override
    public void deleteByIds(Long[] peccIds) {
        if(BeanUtils.isEmpty(peccIds)||peccIds.length==0){
            LOGGER.error("parameter cannot be empty.");
        }
        for (Long peccId: peccIds) {
            deleteById(peccId);
        }
    }
    @Override
    public int deleteById(Long id) {
        Map<String,Object> params = new HashMap<>();
        params.put("updateBy", ContextSupportUtil.getCurrentUserId());
        params.put("updateTime",new Date());
        params.put("peccId",id);
        return carPeccancyDao.deleteById(params);
    }

    @Override
    public int save(CarPeccancy carPeccancy) {
        //生成唯一主键ID
        Long id = IdGeneratorUtil.getNextId();
        carPeccancy.setPeccId(id);
        carPeccancy.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
        //设置创建人、更新人
        Long currrentUserId = ContextSupportUtil.getCurrentUserId();
        String aliasName = ContextSupportUtil.getCurrentUser().getAliasName();
        carPeccancy.setCreateBy(currrentUserId);
        carPeccancy.setUpdateBy(currrentUserId);
        //获取当前时间
        Date date = new Date();
        carPeccancy.setCreateTime(date);
        carPeccancy.setUpdateTime(date);
        return carPeccancyDao.save(carPeccancy);
    }

    @Override
    public int update(CarPeccancy carPeccancy) {
        //设置更新人更新人
        carPeccancy.setUpdateBy(ContextSupportUtil.getCurrentUserId());
        //设置更新时间
        carPeccancy.setUpdateTime(new Date());
        return carPeccancyDao.update(carPeccancy);
    }
}
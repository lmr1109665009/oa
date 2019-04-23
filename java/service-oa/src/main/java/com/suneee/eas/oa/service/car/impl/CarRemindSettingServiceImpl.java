package com.suneee.eas.oa.service.car.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.car.CarRemindSettingDao;
import com.suneee.eas.oa.model.car.CarInfo;
import com.suneee.eas.oa.model.car.CarRemindSetting;
import com.suneee.eas.oa.service.car.CarRemindSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @user zousiyu
 * @date 2018/8/23 15:19
 */
@Service
public class CarRemindSettingServiceImpl extends BaseServiceImpl<CarRemindSetting> implements CarRemindSettingService {
    
    private CarRemindSettingDao carRemindSettingDao;
    
@Autowired
    public void setCarRemindSettingDao(CarRemindSettingDao carRemindSettingDao) {
        this.carRemindSettingDao = carRemindSettingDao;
        setBaseDao(carRemindSettingDao);
    }

    @Override
    public int save(CarRemindSetting carRemindSetting) {
        Long id = IdGeneratorUtil.getNextId();
    String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
    carRemindSetting.setEnterpriseCode(enterpriseCode);
        carRemindSetting.setId(id);
    carRemindSettingDao.save(carRemindSetting);
        return 0;
    }

    @Override
    public int update(CarRemindSetting carRemindSetting) {
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        carRemindSetting.setEnterpriseCode(enterpriseCode);
        carRemindSettingDao.update(carRemindSetting);
    return 0;
    }

    @Override
    public  boolean countByName(String name,String enterpriseCode){
        Map<String,Object> params = new HashMap<>();
        params.put("name", name);
        params.put("enterpriseCode", enterpriseCode);
        int count = carRemindSettingDao.countByName(params);
        if (count>0) {
            return true;
        } else {
            return false;
        }
    }

    //根据提醒名称查询
    @Override
    public  List<CarRemindSetting> getByName(String name){
        Map<String,Object> params = new HashMap<>();
        params.put("name", name);
        List<CarRemindSetting> list = carRemindSettingDao.getByName(params);
        return list;
    }
    
    //获取车辆当前公里数，下次保养时间，下次保养公里数
  @Override
    public List<CarInfo> mainAndInfoList(String enterpriseCode){
        Map<String,Object> params = new HashMap<>();
        params.put("enterpriseCode", enterpriseCode);
        List<CarInfo> list = carRemindSettingDao.mainAndInfoList(params);
        return list;
    }
    }


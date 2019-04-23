package com.suneee.eas.oa.service.car;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.car.CarInfo;
import com.suneee.eas.oa.model.car.CarRemindSetting;

import java.util.List;

/**
 * @user zousiyu
 * @date 2018/8/23 15:18
 */
public interface CarRemindSettingService extends BaseService<CarRemindSetting> {
    //是否存在提醒
    boolean countByName(String name,String enterpriseCode);

    //根据提醒名称查询
    List<CarRemindSetting> getByName(String name);
    
    //获取车辆当前公里数，下次保养时间，下次保养公里数
    List<CarInfo> mainAndInfoList(String enterpriseCode);
}

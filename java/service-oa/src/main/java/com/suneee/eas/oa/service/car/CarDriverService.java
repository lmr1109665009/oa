package com.suneee.eas.oa.service.car;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.car.CarDriver;

/**
 * @user zousiyu
 * @date 2018/8/16 14:59
 */
public interface CarDriverService extends BaseService<CarDriver> {

    //新增驾驶员信息
    void save(CarDriver carDriver, String licenseDate, String validDate);

    //新增驾驶员信息
    void update(CarDriver carDriver, String licenseDate, String validDate);

    //驾驶员是否存在
    boolean countByDriverId(Long driverId,String enterpriseCode);
}

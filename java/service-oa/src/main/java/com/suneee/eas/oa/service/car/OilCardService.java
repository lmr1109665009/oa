package com.suneee.eas.oa.service.car;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.car.OilCard;

/**
 * @user zousiyu
 * @date 2018/8/20 11:29
 */
public interface OilCardService extends BaseService<OilCard> {
    

    //新增油卡信息
    void save(OilCard oilCard ,String date);


    //修改油卡员信息
    void update(OilCard oilCard ,String date);
    
    //查询油卡编号是否存在
    boolean checkCardNumIsExist(String cardNum);
    
}

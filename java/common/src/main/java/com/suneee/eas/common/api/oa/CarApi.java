package com.suneee.eas.common.api.oa;

import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.constant.ServiceConstant;

/**
 * @user zousiyu
 * @date 2018/8/31 17:15
 */
public class CarApi {

    /**
     * 获取维修保养数据
     */
    public static final String carMaintenance = ModuleConstant.CAR_MODULE + FunctionConstant.REMIND_INFO + "carMaintenanceListAll";

    /**
     * 获取年检数据
     */
    public static final String carInspection = ModuleConstant.CAR_MODULE + FunctionConstant.REMIND_INFO + "carInspectionListAll";

    /**
     * 获取车辆保险数据
     */
    public static final String carInsurance = ModuleConstant.CAR_MODULE + FunctionConstant.REMIND_INFO + "carInsuranceListAll";
    
    /**
     * 获取驾驶员列表
     */
    public static final String carDriver = ModuleConstant.CAR_MODULE + FunctionConstant.REMIND_INFO + "carDriverListAll";

    /**
     * 提醒设置列表
     */
    public static final String carRemindSetting = ModuleConstant.CAR_MODULE + FunctionConstant.REMIND_INFO + "carRemindSettingListAll";

   
}

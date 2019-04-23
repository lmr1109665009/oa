package com.suneee.eas.oa.service.car;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.car.CarApply;
import org.flowable.task.api.Task;

import java.util.Map;

/**
 * 车辆申请service
 * @user 子华
 * @created 2018/9/4
 */
public interface CarApplyService extends BaseService<CarApply> {

    public int autoSave(CarApply model);

    public void updateStatus(Long applyId, int status);

    public CarApply findByProcInstId(String procInstId);

    Map<String, Object> getDetails(Long applyId);

    void deleteBatch(Long applyId);

    public void recover(String procInstId, int status);

    public void cleanData(Task currentTask, CarApply carApply, int status, String isRecover, String message);
}

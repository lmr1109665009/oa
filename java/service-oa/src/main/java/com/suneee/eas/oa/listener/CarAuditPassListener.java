package com.suneee.eas.oa.listener;

import com.suneee.eas.oa.dao.car.CarApplyDao;
import com.suneee.eas.oa.dao.car.CarArrangeDao;
import com.suneee.eas.oa.model.car.CarApply;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 车辆审批通过监听器
 * @user 子华
 * @created 2018/9/26
 */
@Component
public class CarAuditPassListener implements TaskListener {
    @Autowired
    private CarApplyDao applyDao;
    @Autowired
    private CarArrangeDao arrangeDao;
    @Override
    public void notify(DelegateTask delegateTask) {
        //如果是撤回操作，则不做任何处理
        String isRecover = delegateTask.getVariable("isRecover", String.class);
        if("true".equals(isRecover)){
            return;
        }
        if (TaskListener.EVENTNAME_CREATE.equals(delegateTask.getEventName())){

        }else if (TaskListener.EVENTNAME_COMPLETE.equals(delegateTask.getEventName())){
            //任务被创建时，新增审批记录
            auditPassNotify(delegateTask);
        }
    }

    /**
     * 审批通过通知
     * @param delegateTask
     */
    private void auditPassNotify(DelegateTask delegateTask) {
        CarApply carApply= (CarApply) delegateTask.getVariable("form");
        carApply.setStatus(CarApply.STATUS_CAR_ARRANGE_PENDING);
        delegateTask.setVariable("form",carApply);
        applyDao.updateStatus(carApply.getApplyId(),CarApply.STATUS_CAR_ARRANGE_PENDING);
    }
}

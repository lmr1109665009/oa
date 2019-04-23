package com.suneee.eas.oa.listener;

import com.suneee.eas.oa.dao.car.CarArrangeDao;
import com.suneee.eas.oa.model.car.CarApply;
import com.suneee.eas.oa.model.car.CarArrange;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 驾驶员出车监听器
 * @user 子华
 * @created 2018/9/26
 */
@Component
public class CarOutAuditListener implements TaskListener {
    @Autowired
    private CarArrangeDao arrangeDao;
    @Override
    public void notify(DelegateTask delegateTask) {
        if (TaskListener.EVENTNAME_CREATE.equals(delegateTask.getEventName())){

        }else if (TaskListener.EVENTNAME_ASSIGNMENT.equals(delegateTask.getEventName())){
            //任务被创建时，新增审批记录
            carOutNotify(delegateTask);
        }
    }

    /**
     * 出车通知
     * @param delegateTask
     */
    private void carOutNotify(DelegateTask delegateTask) {
        CarApply carApply= (CarApply) delegateTask.getVariable("form");
        CarArrange carArrange=new CarArrange();
        carArrange.setApplyId(carApply.getApplyId());
        arrangeDao.updateTaskId(carApply.getApplyId(),delegateTask.getId());
    }
}

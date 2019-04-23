package com.suneee.eas.oa.listener;

import com.suneee.eas.oa.dao.car.CarApplyDao;
import com.suneee.eas.oa.model.car.CarApply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.FlowableCancelledEvent;
import org.flowable.engine.delegate.event.FlowableProcessTerminatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 车辆申请流程全局监听器
 * @user 子华
 * @created 2018/9/6
 */
@Component
public class CarApplyProcessListener implements FlowableEventListener {
    private static Logger log= LogManager.getLogger(CarApplyProcessListener.class);
    @Autowired
    private CarApplyDao applyDao;

    /**
     * 更新流程审批状态通知
     * @param procInstId
     */
    private void updateApplyNotify(String procInstId) {
        applyDao.updateStatusByInstId(procInstId,CarApply.STATUS_CANCEL);
    }

    @Override
    public void onEvent(FlowableEvent event) {
        if (event instanceof FlowableCancelledEvent){
            FlowableCancelledEvent cancelledEvent = (FlowableCancelledEvent)event;
            updateApplyNotify(cancelledEvent.getProcessInstanceId());
            log.debug("执行取消流程操作，流程实例ID："+cancelledEvent.getProcessInstanceId()+",流程定义ID："+cancelledEvent.getProcessDefinitionId());
        }else if (event instanceof FlowableProcessTerminatedEvent){
            //流程审批结束事件
            FlowableProcessTerminatedEvent entityEvent= (FlowableProcessTerminatedEvent) event;
        }
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return true;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
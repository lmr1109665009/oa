package com.suneee.eas.flowable.listener;

import com.suneee.eas.flowable.exception.NonAssignUserException;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.common.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * 全局流程引擎监听器
 * @user 子华
 * @created 2018/9/6
 */
@Component
public class MyProcessListener implements FlowableEventListener {
    private static Logger log= LogManager.getLogger(MyProcessListener.class);
    /**
     * 任务创建时动态分配执行人通知
     * @param entityEvent
     */
    private void assignNotify(FlowableEntityEvent entityEvent) {
        DelegateTask delegateTask= (DelegateTask) entityEvent.getEntity();
        String userId=delegateTask.getAssignee();
        if (StringUtil.isNotEmpty(userId)){
            return;
        }
        userId= (String) delegateTask.getVariable("userId");
        if (StringUtil.isNotEmpty(userId)){
            delegateTask.setAssignee(userId);
            delegateTask.removeVariable("userId");
            return;
        }
        userId= RequestUtil.getString(ContextUtil.getRequest(),"userId");
        if (StringUtil.isNotEmpty(userId)){
            delegateTask.setAssignee(userId);
            return;
        }
        throw new NonAssignUserException("执行人不允许为空");
    }

    @Override
    public void onEvent(FlowableEvent event) {
        if(event.getType() == FlowableEngineEventType.TASK_CREATED){
            FlowableEntityEvent entityEvent= (FlowableEntityEvent) event;
            assignNotify(entityEvent);
        }
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
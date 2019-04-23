package com.suneee.eas.flowable.config;

import com.suneee.eas.flowable.component.DbIdGenerator;
import com.suneee.eas.flowable.listener.MyProcessListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MyFlowableConfigurer implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
    @Autowired
    private DbIdGenerator idGenerator;
    @Autowired
    private MyProcessListener myProcessListener;

    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        //设置ProcessEngineConfigurationImpl里的uuidGenerator
        processEngineConfiguration.setIdGenerator(idGenerator);
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        Map<String, List<FlowableEventListener>> typedListeners = new HashMap<>();
        typedListeners.put(FlowableEngineEventType.TASK_CREATED.name(),getTaskListenerList());
        processEngineConfiguration.setTypedEventListeners(typedListeners);
    }

    /**
     * 获取流程任务监听器
     * @return
     */
    private List<FlowableEventListener> getTaskListenerList(){
        List<FlowableEventListener> list=new ArrayList<>();
        list.add(myProcessListener);
        return list;
    }

}
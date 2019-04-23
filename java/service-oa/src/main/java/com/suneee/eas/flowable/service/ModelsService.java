package com.suneee.eas.flowable.service;

import com.suneee.eas.common.service.BaseService;
import org.flowable.ui.modeler.domain.Model;

/**
 * 流程引擎model service
 * @user 子华
 * @created 2018/10/22
 */
public interface ModelsService extends BaseService<Model> {
    /**
     * 发布流程定义
     * @param modelId
     * @return
     */
    public void publish(String modelId);
}

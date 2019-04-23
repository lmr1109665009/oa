package com.suneee.eas.flowable.service.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.flowable.exception.PublishProcessException;
import com.suneee.eas.flowable.service.ModelsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @user 子华
 * @created 2018/10/22
 */
@Service
public class ModelsServiceImpl extends BaseServiceImpl<Model> implements ModelsService {
    private static final Logger log= LogManager.getLogger(ModelsServiceImpl.class);
    @Autowired
    private ModelService modelService;
    @Autowired
    private RepositoryService repositoryService;


    @Override
    public void publish(String modelId) {
        Model model=modelService.getModel(modelId);
        BpmnModel bpmnModel=modelService.getBpmnModel(model);
        //生成BPMN自动布局
        new BpmnAutoLayout(bpmnModel).execute();
        Deployment deploy =repositoryService.createDeployment().addBpmnModel(model.getKey()+".bpmn20.xml",bpmnModel).key(model.getKey()).name(model.getName()).deploy();
        if (deploy==null){
            log.error("流程发布失败：key="+model.getKey()+",name="+model.getName());
            throw new PublishProcessException("流程发布失败");
        }
        log.info("流程发布成功，流程ID："+deploy.getId()+"，流程名称："+deploy.getName()+"，流程Key："+deploy.getKey());
    }
}

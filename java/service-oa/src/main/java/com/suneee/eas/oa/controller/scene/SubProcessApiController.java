package com.suneee.eas.oa.controller.scene;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.scene.MobileScene;
import com.suneee.eas.oa.model.scene.SubProcess;
import com.suneee.eas.oa.service.scene.MobileSceneService;
import com.suneee.eas.oa.service.scene.SubProcessService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 对象功能:手机端子流程 控制类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2018-5-15
 * </pre>
 */
@RequestMapping("/oa/subProcess/")
@RestController
public class SubProcessApiController{
	private static final Logger LOGGER = LogManager.getLogger(SubProcessApiController.class);
    @Resource
    private SubProcessService subProcessService;
    @Resource
    private MobileSceneService mobileSceneService;

    @RequestMapping("getList")
    public ResponseMessage getList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            QueryFilter filter = new QueryFilter("getAll", request);
            Pager<SubProcess> processList = subProcessService.getPageBySqlKey(filter);
            return ResponseMessage.success("获取列表信息成功!",processList);
        }catch (Exception e){
        	LOGGER.error("获取列表信息失败：" + e.getMessage(), e);
            return ResponseMessage.fail("获取列表信息失败：系统内部错误！");
        }
    }

    @RequestMapping("save")
    public ResponseMessage saveSubProcess(HttpServletRequest request, @RequestBody SubProcess subProcess) throws Exception{
        try {
            subProcessService.save(subProcess);
            return ResponseMessage.fail("保存子流程设置成功！");
        }catch (Exception e){
        	LOGGER.error("保存子流程设置失败：" + e.getMessage(), e);
            return ResponseMessage.fail("保存子流程设置失败：系统内部错误！");
        }
    }

    /**
     *根据场景Id获取所有关联子流程
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getListBySceneId")
    @ResponseBody
    public ResponseMessage getListBySceneId(HttpServletRequest request,HttpServletResponse response) throws Exception{
        long sceneId = RequestUtil.getLong(request, "sceneId", 0L);
        try{
            List<SubProcess> subProcess = subProcessService.getBySceneId(sceneId);
            return ResponseMessage.success("获取子流程列表成功！",subProcess);
        }catch (Exception e){
        	LOGGER.error("获取子流程列表失败：" + e.getMessage(), e);
            return ResponseMessage.fail("获取子流程列表失败：系统内部错误！");
        }
    }

    /**
     * 根据场景Id获取所有关联场景（关联子流程中没有绑定场景的子流程去掉）
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getSceListBySceneId")
    @ResponseBody
    public ResponseMessage getSceListBySceneId(HttpServletRequest request,HttpServletResponse response) throws Exception{
        long sceneId = RequestUtil.getLong(request, "sceneId", 0L);
        try{
            List<SubProcess> finalPro = new ArrayList<>();
            List<SubProcess> subProcesses = subProcessService.getBySceneId(sceneId);
            for (SubProcess subProcess:subProcesses){
                //如果关联子流程没有绑定场景，则不返回
                List<MobileScene> subScene = mobileSceneService.getByDefId(subProcess.getSubDefId(), null);
                if(subScene.size()>0){
                    //将场景的图标地址赋值给description字段
                    String ImgPath = subScene.get(0).getImgPath();
                    //
                    subProcess.setSceneName(subScene.get(0).getSceneName());
                    //将子场景Id赋值给sceneId
                    subProcess.setSceneId(subScene.get(0).getId());
                    subProcess.setDescription(ImgPath);
                    finalPro.add(subProcess);
                }
            }
            return ResponseMessage.success("获取关联场景子流程列表成功",finalPro);
        }catch (Exception e){
        	LOGGER.error("获取关联场景子流程列表失败" + e.getMessage(), e);
            return ResponseMessage.fail("获取关联场景子流程列表失败：系统内部错误！");
        }
    }
}

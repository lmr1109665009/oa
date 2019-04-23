package com.suneee.oa.controller.scene;

import com.suneee.core.page.PageList;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.oa.model.scene.MobileScene;
import com.suneee.oa.model.scene.SubProcess;
import com.suneee.oa.service.scene.MobileSceneService;
import com.suneee.oa.service.scene.SubProcessService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
@Controller
public class SubProcessController extends BaseController {

    @Resource
    private SubProcessService subProcessService;
    @Resource
    private MobileSceneService mobileSceneService;

    @RequestMapping("getList")
    @ResponseBody
    private ResultVo getList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            QueryFilter filter = new QueryFilter(request);
            PageList<SubProcess> processList = (PageList<SubProcess>) subProcessService.getAll(filter);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取列表信息成功",processList);
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取，诶表信息失败",e.getMessage());
        }
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultVo saveSubProcess(HttpServletRequest request, @RequestBody SubProcess subProcess) throws Exception{
        try {
            subProcessService.add(subProcess);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"保存子流程设置成功");
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"保存子流程设置失败",e.getMessage());
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
    public ResultVo getListBySceneId(HttpServletRequest request,HttpServletResponse response) throws Exception{
        long sceneId = RequestUtil.getLong(request, "sceneId", 0L);
        try{
            List<SubProcess> subProcess = subProcessService.getBySceneId(sceneId);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取子流程列表成功",subProcess);
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取子流程列表失败",e.getMessage());
        }
    }

    /**
     * 根据场景Id获取所有关联场景
     * （关联子流程中没有绑定场景的子流程去掉）
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getSceListBySceneId")
    @ResponseBody
    public ResultVo getSceListBySceneId(HttpServletRequest request,HttpServletResponse response) throws Exception{
        long sceneId = RequestUtil.getLong(request, "sceneId", 0L);
        try{
            List<SubProcess> finalPro = new ArrayList<>();
            List<SubProcess> subProcesses = subProcessService.getBySceneId(sceneId);
            for (SubProcess subProcess:subProcesses){
                //如果关联子流程没有绑定场景，则不返回
                List<MobileScene> subScene = mobileSceneService.getByDefId(subProcess.getSubDefId());
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
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取子流程列表成功",finalPro);
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取子流程列表失败",e.getMessage());
        }
    }
}

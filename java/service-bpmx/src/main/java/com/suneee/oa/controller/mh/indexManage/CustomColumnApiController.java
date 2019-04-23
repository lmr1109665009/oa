package com.suneee.oa.controller.mh.indexManage;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.util.LongUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.oa.model.docFile.DocFile;
import com.suneee.oa.service.docFile.DocFileService;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.customColumn.*;
import com.suneee.ucp.mh.model.customColumn.CustomLCTabVO;
import com.suneee.ucp.mh.service.customColumn.CustomColumnService;
import com.suneee.ucp.mh.service.customColumn.CustomColumnVOService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/mh/index/customColumnApi")
public class CustomColumnApiController extends UcpBaseController {


    @Autowired
    private CustomColumnService customColumnService;
    @Autowired
    private CustomColumnVOService customColumnVOService;

    /**
     * 根据columnid获取下面的所有的tab
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getCustomTab", method = RequestMethod.GET)
    @ResponseBody
    public ResultVo getCustomTab(HttpServletRequest request, HttpServletResponse response) {
        Long columnId = RequestUtil.getLong(request, "columnId");
        if (columnId == null) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数异常, columnId=null");
        }
        CustomColumn param = new CustomColumn();
        try {
            param.setColumnId(columnId);
            param.setCreateBy(ContextUtil.getCurrentUserId());
            List<CustomColumn> customColumnList = this.customColumnService.getCustomTab(param);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取栏目自定义成功！", customColumnList);
        } catch (Exception e) {
            logger.error("获取栏目的自定义tab失败, pamras={}", param, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取栏目自定义tab失败！");
        }
    }

    /**
     * 保存子定义的tab(旧的）
     * @param customColumn
     * @return
     */
    @RequestMapping(value = "/saveCustomTab", method = RequestMethod.POST)
    @ResponseBody
    public ResultVo saveCustomTab(@RequestBody CustomColumnVO customColumn) {
        try {
            return this.customColumnService.saveCustomTab(customColumn);
        } catch (Exception e) {
            logger.error("saveCustomTab exception", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存自定义栏目失败");
        }
    }


//    /**
//     * 根据栏目id获取栏目下所有自定义tab的数据(首次加载)
//     * @param columnId 栏目id
//     * */
//    @RequestMapping("/getColumnDataByColumnId")
//    @ResponseBody
//    public ResultVo getColumnDataByColumnId(Long columnId){
//        if(columnId == null){
//            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数异常, columnId=null");
//        }
//        CustomColumn param = new CustomColumn();
//        try {
//            param.setColumnId(columnId);
//            param.setUserId(ContextUtil.getCurrentUserId());
//            //已办事宜
//            if (columnId == CustomColumnService.ALREADY) {
//                return this.customColumnService.getAlreadyDataByColumnId(param);
//                //待办事宜
//            } else if (columnId == CustomColumnService.PENDDING) {
//                return this.customColumnService.getPenddingDataByColumnId(param);
//            } else {
//                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数非法");
//            }
//        }catch (Exception e){
//            logger.error("getAlreadyDataByColumnId exception, param={}", param, e);
//            return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "获取数据失败");
//        }
//    }

    /**
     * 根据自定义tab的Id加载数据(用于分页加载)
     *
     * @param tabId 自定义tab的id
     */
    @RequestMapping("/getTabDataByTabId")
    @ResponseBody
    public ResultVo getTabDataByTabId(Long tabId, HttpServletRequest request) {
        try {
            int page = RequestUtil.getInt(request, "page", 1);
            int pageSize = RequestUtil.getInt(request, "pageSize", PageBean.DEFAULT_PAGE_SIZE);
            PageBean pageBean = new PageBean(page, pageSize);
            return this.customColumnService.getTabDataByTabId(tabId, pageBean);
        } catch (Exception e) {
            logger.error("getTabDataByTabId exception,tabId={}", tabId, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取数据失败");
        }
    }

//    /**
//     * 首页栏目编辑模式下，数据预览
//     * @param columnId 栏目id
//     * @param defIds 流程定义id数组
//     * */
//    @RequestMapping("/getPreviewDataByDefIds")
//    @ResponseBody
//    public ResultVo getPreviewDataByDefIds(Long columnId, String defIds, HttpServletRequest request){
//        try {
//            PageBean pageBean = new PageBean(1, PageBean.DEFAULT_PAGE_SIZE);
//            pageBean.setShowTotal(false);
//            if (columnId == null) {
//                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数异常, columId为null");
//            }
//            if (StringUtils.isEmpty(defIds)) {
//                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数异常, defIds为空");
//            }
//            String[] defIdArr = defIds.split(",");
//            return this.customColumnService.getPreviewDataByDefIds(columnId, defIdArr, pageBean);
//        }catch (Exception e){
//            logger.error("getPreviewDataByDefIds exception, columnId={}, defIds={}", columnId, defIds, e);
//            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取预览数据失败");
//        }
//    }

    /**
     * 根据columnId获取所有的tab.
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getTab")
    @ResponseBody
    public ResultVo getTab(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long columnId = RequestUtil.getLong(request, "columnId");
        if (columnId == null) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数异常, columnId=null");
        }
        CustomColumn param = new CustomColumn();
        try {
            param.setColumnId(columnId);
            param.setCreateBy(ContextUtil.getCurrentUserId());
            List<CustomColumn> customColumnList = this.customColumnService.getCustomTab(param);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取tab成功！", customColumnList);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取tab失败！");
        }

    }

    /**
     * 根据columnId获取tab的数据包括tab下面的数据
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getDataByColumnId")
    @ResponseBody
    public ResultVo getDataByColumnId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            CustomColumn param = new CustomColumn();
            Long columnId = RequestUtil.getLong(request, "columnId");
            param.setColumnId(columnId);
            param.setCreateBy(ContextUtil.getCurrentUserId());
            CustomColumnVO customColumnVO = customColumnVOService.getById(columnId);
            String columnType = customColumnVO.getColumnType();
            if (columnType==null||columnType==""||columnId == null) {
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数异常！");
            }
                //流程类的数据
            if(customColumnService.LCGL_TYPE.equals(columnType)){
                List<CustomLCTabVO> lcList = new ArrayList<>();
                lcList = customColumnService.getLCDataByColumnId(param);
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取tab数据成功！", lcList);
                //文件类的数据
            }else if(customColumnService.WJG_TYPE.equals(columnType)||customColumnService.WJGL_TYPE.equals(columnType)){
                    List<CustomWJTabVO> wjList = new ArrayList<>();
                    wjList=customColumnService.getWJDataByColumnId(param,request);
                    return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取tab数据成功！", wjList);
            }else{
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取tab数据失败，没有此类型数据！");
            }
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取tab数据失败！");
        }

    }

}

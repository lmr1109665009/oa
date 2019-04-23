package com.suneee.oa.controller.document;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.model.docFile.DocFile;
import com.suneee.oa.service.docFile.DocFileService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/me/newDocFile/")
public class DocFileListController extends UcpBaseController {

    @Resource
    private DocFileService docFileService;

    @Resource
    private SysOrgService orgService;
    @Resource
    private UserPositionService uerPositionService;
    @Resource
    private Properties configProperties;

    @RequestMapping("list")
    @ResponseBody
    @Action(description = "查看文档管理分页列表", execOrder = ActionExecOrder.AFTER, detail = "查看文档管理分页列表", exectype = "管理日志")
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = new QueryFilter(request);
        String keyWord = RequestUtil.getString(request, "keyword");
        long id = RequestUtil.getLong(request, "id");
        long classify = RequestUtil.getLong(request,"classify");
        Long curUserId = ContextUtil.getCurrentUserId();
        String eid = ContextSupportUtil.getCurrentEnterpriseCode();
        JSONObject json = new JSONObject();
        List<DocFile> list = null;
        try {
            //如果没有传id则将classify作为id获取筛选条件
            if(id!=0){
                queryFilter = docFileService.getFilter(queryFilter,id);
            }else{
                queryFilter = docFileService.getFilter(queryFilter,classify);
            }
            list = docFileService.getAll(queryFilter);
            boolean enh = false;
            if (null == list || list.size() < 1) {
                enh = true;
            }
            ListModel model = new ListModel();
            if (list != null) {
                model = CommonUtil.getListModel((PageList) list);
            }
            json.put("docFileList", model);
            json.put("enh", enh);
            json.put("eid", eid);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取文件列表成功！", json);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取文件列表失败！", e);
        }

    }


    /**
     * 获取文件夹目录。
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getTreeData")
    @ResponseBody
    public ResultVo getTreeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<DocFile> list = new ArrayList<DocFile>();
        Long curUserId = ContextUtil.getCurrentUserId();
        String eid = ContextSupportUtil.getCurrentEnterpriseCode();
        QueryFilter queryFilter = new QueryFilter(request, false);
        queryFilter.addFilter("isDocType", 1);
        list = docFileService.getList(1l, list, 1);
        boolean enh = false;
        if (null == list || list.size() < 1) {
            enh = true;
        }
        JSONObject json = new JSONObject();
        json.put("docFileList", list);
        json.put("enh", enh);
        json.put("eid", eid);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取文件列表成功！", json);

    }

    @RequestMapping("getBread")
    @ResponseBody
    public ResultVo getBreadCrumb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = RequestUtil.getLong(request, "id");
        try {
            List<DocFile> breadCrumbList = new ArrayList<DocFile>();
            breadCrumbList = this.getParentDocFile(id, breadCrumbList);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取面包屑导航数据成功！", breadCrumbList);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取面包屑导航数据失败！", e);
        }
    }

    /**
     * 获取面包屑层级列表
     * @param id
     * @param breadCrumbList
     * @return
     */

    public List<DocFile> getParentDocFile(Long id, List<DocFile> breadCrumbList) {
        DocFile docFile = docFileService.getById(id);
        if (docFile != null) {
            Long parentId = docFile.getParentId();
            breadCrumbList.add(docFile);
            if (parentId != null && parentId != 1) {
                this.getParentDocFile(parentId, breadCrumbList);
            }
        }
        return breadCrumbList;
    }

}

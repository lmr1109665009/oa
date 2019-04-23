package com.suneee.eas.oa.controller.fs;

import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.fs.DocFile;
import com.suneee.eas.oa.service.fs.DocFileService;
import com.suneee.eas.oa.service.user.SysOrgService;
import com.suneee.eas.oa.service.user.UserPositionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/me/newDocFile/")
public class DocFileListController {

    @Resource
    private DocFileService docFileService;

    @Resource
    private SysOrgService orgService;
    @Resource
    private UserPositionService uerPositionService;

    @RequestMapping("list")
    @ResponseBody
    public ResponseMessage list(HttpServletRequest request) {
        QueryFilter queryFilter = new QueryFilter("getAll",request);
        long id = RequestUtil.getLong(request, "id");
        long classify = RequestUtil.getLong(request,"classify");
        try {
            //如果没有传id则将classify作为id获取筛选条件
            if(id!=0){
                queryFilter = docFileService.getFilter(queryFilter,id);
            }else{
                queryFilter = docFileService.getFilter(queryFilter,classify);
            }
            Pager<DocFile> docFilePager = docFileService.getPageBySqlKey(queryFilter);
            return ResponseMessage.success("获取文件列表成功！",docFilePager);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.fail("获取文件列表失败！");
        }

    }


    /**
     * 获取文件夹目录。
     * @return
     * @throws Exception
     */
    @RequestMapping("getTreeData")
    @ResponseBody
    public ResponseMessage getTreeData() {
        List<DocFile> list = new ArrayList<DocFile>();
        list = docFileService.getList(1l, list, 1);
        return ResponseMessage.success("获取文件列表成功！",list);
    }

    @RequestMapping("getBread")
    @ResponseBody
    public ResponseMessage getBreadCrumb(HttpServletRequest request){
        Long id = RequestUtil.getLong(request, "id");
        try {
            List<DocFile> breadCrumbList = new ArrayList<DocFile>();
            breadCrumbList = this.getParentDocFile(id, breadCrumbList);
            return ResponseMessage.success("获取面包屑导航数据成功！", breadCrumbList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.fail("获取面包屑导航数据失败！");
        }
    }

    /**
     * 获取面包屑层级列表
     * @param id
     * @param breadCrumbList
     * @return
     */

    public List<DocFile> getParentDocFile(Long id, List<DocFile> breadCrumbList) {
        DocFile docFile = docFileService.findById(id);
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

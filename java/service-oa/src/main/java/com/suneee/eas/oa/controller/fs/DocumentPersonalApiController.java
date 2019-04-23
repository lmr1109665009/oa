/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DocumentPersonalApiController
 * Author:   lmr
 * Date:     2018/10/25 11:55
 * Description: 个人文档管理
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.controller.fs;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.fs.Document;
import com.suneee.eas.oa.service.fs.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈个人文档管理〉
 *
 * @author lmr
 * @create 2018/10/25
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.FS_MODULE + FunctionConstant.PERSONAL_DOCUMENT)
public class DocumentPersonalApiController {
    private static final Logger LOGGER = LogManager.getLogger(DocumentPersonalApiController.class);
    @Autowired
    private DocumentService documentService;
    @RequestMapping("treeData")
    public ResponseMessage treeData(HttpServletRequest request, HttpServletResponse response){
        try {
            Map<String,List> map = new HashMap<>();
            List<Document> documentList = documentService.getByDirPath(DocumentService.PERSONAL_DOCUMENT_ROOT_ID+".",1,ContextSupportUtil.getCurrentUserId());
         //   List<Document> myShareList = documentService.
                    map.put("personalDoc",documentList);
            return ResponseMessage.success("获取树数据成功！",documentList);
        } catch (Exception e) {
            LOGGER.error("获取树数据据错误！"+e);
            return ResponseMessage.fail("获取树数据失败！");
        }
    }

    @RequestMapping("list")
    public ResponseMessage list(HttpServletRequest request, HttpServletResponse response){
        QueryFilter queryFilter = new QueryFilter("getByParentId",request);
        queryFilter.addFilter("enterpriseCode",ContextSupportUtil.getCurrentEnterpriseCode());
        try {
            Pager<Document> documentPager = documentService.getPageBySqlKey(queryFilter);
            return ResponseMessage.success("获取列表数据成功！",documentPager);
        }catch (Exception e){
            LOGGER.error("获取列表数据错误！"+e);
            return ResponseMessage.fail("获取列表数据失败！");
        }
    }


    /**
     * 文件上传后保存按钮
     *
     * @param request
     * @param documentList
     * @return
     */
    @RequestMapping("save")
    public ResponseMessage save(HttpServletRequest request, @RequestBody List<Document> documentList) {
        if (documentList.size() == 0) {
            return ResponseMessage.fail("您没有上传文件！");
        }
        Document document = documentList.get(0);
        String msg1 = "";
        String msg2 = "";
        String type = "";
        if (document.getId() == null || document.getId() == 0) {
            msg1 = "保存";
        } else {
            msg1 = "更新";
        }
        if (document.getIsDir() == 1) {
            document.setSize("0.00M");
            msg2 = "文件夹";
        } else {
            msg2 = "文件";
        }
        try {
            if (document.getId() == null || document.getId() == 0) {
                documentService.saveList(documentList);
            }else{
                documentService.update(document);
            }
            return ResponseMessage.success(msg1+msg2+"成功！");
        } catch (Exception e) {
            LOGGER.error(msg1+msg2+"失败:"+e);
            return ResponseMessage.fail(msg1+msg2+"失败！");
        }
    }

    /**
     * 删除
     * @param request
     * @return
     */
    @RequestMapping("delete")
    public ResponseMessage delete(HttpServletRequest request){
        Long[] ids = RequestUtil.getLongAryByStr(request,"ids");
        List<Document> documentList = new ArrayList<>();
        try {
            //如果是文件夹，则文件夹下面的所有文件都删除
            for(Document document:documentList){

                if (document.getIsDir() == 1) {
                    documentService.delByPath(document.getDirPath());
                }
                documentService.deleteById(document.getId());
            }
        }catch (Exception e){
            LOGGER.error("删除失败"+e);
            return ResponseMessage.fail("删除失败！");
        }
        return ResponseMessage.success("删除成功！");
    }
    /**
     * 移动和复制
     * @param request
     * @return
     */
    @RequestMapping("moveOrCopy")
    public ResponseMessage moveOrCopy(HttpServletRequest request){
        Long[] ids = RequestUtil.getLongAryByStr(request,"ids");
        int isMove =RequestUtil.getInt(request,"isMove");
        Long id = RequestUtil.getLong(request,"id");
        String msg = "";
        if(isMove==1){
            msg="移动";
        }else {
            msg="复制";
        }
        try {
            Document newDocument = documentService.findById(id);
            for(int i= 0;i<ids.length;i++){
                Document document = documentService.findById(ids[i]);
                documentService.moveOrCopy(newDocument,document,isMove,false);
            }
        }catch (Exception e){
            LOGGER.error(msg+"失败："+e);
            return ResponseMessage.fail(msg+"失败,请重试！");
        }
        return ResponseMessage.success(msg+"成功！");
    }
}
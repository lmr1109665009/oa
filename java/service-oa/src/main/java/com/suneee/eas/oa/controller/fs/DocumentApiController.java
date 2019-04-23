/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DocumentOperateApiController
 * Author:   lmr
 * Date:     2018/10/12 9:49
 * Description: 文件操作
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.controller.fs;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FsConstant;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.fs.Document;
import com.suneee.eas.oa.service.fs.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈文件操作〉
 *
 * @author lmr
 * @create 2018/10/12
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.FS_MODULE + FunctionConstant.PUBLIC_DOCUMENT)
public class DocumentApiController {
    private static final Logger LOGGER = LogManager.getLogger(DocumentApiController.class);
    @Autowired
    private DocumentService documentService;

    @Autowired
    private UploaderHandler uploaderHandler;



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
            type = DocumentService.OWNER_TYPE_CREATE;
            msg1 = "保存";
        } else {
            msg1 = "更新";
            type = DocumentService.OWNER_TYPE_EDIT;
        }
        if (document.getIsDir() == 1) {
            document.setSize("0.00M");
            msg2 = "文件夹";
        } else {
            msg2 = "文件";
        }
        try {
            //查看用户是否拥有新建的权限
            boolean hasAuth = documentService.hasAuth(document, type);
            if (!hasAuth) {
                return ResponseMessage.fail("您没有权限执行此操作！");
            }
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
            //先判断是否权限，才能继续操作
            for(int i =0;i<ids.length;i++){
                Document document = documentService.findById(ids[i]);
                boolean hasAuth = documentService.hasAuth(document, DocumentService.OWNER_TYPE_DEL);
                if (!hasAuth) {
                    return ResponseMessage.fail("您没有权限执行此操作！");
                }
                documentList.add(document);
            }
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
            boolean hasAuth1 = documentService.hasAuth(newDocument, DocumentService.OWNER_TYPE_EDIT);
            if (!hasAuth1) {
                return ResponseMessage.fail("您没有权限对"+newDocument.getName()+"执行此操作！");
            }
            //查看用户是否拥有编辑的权限,如果全部都有权限，才能进行移动或复制的操作
            for(int i= 0;i<ids.length;i++) {
                Document document = documentService.findById(ids[i]);
                boolean hasAuth2 = documentService.hasAuth(document, DocumentService.OWNER_TYPE_EDIT);
                if (!hasAuth2) {
                    return ResponseMessage.fail("您没有权限对"+document.getName()+"执行此操作！");
                }
            }
            for(int i= 0;i<ids.length;i++){
                Document document = documentService.findById(ids[i]);
                documentService.moveOrCopy(newDocument,document,isMove,true);
            }
        }catch (Exception e){
            LOGGER.error(msg+"失败："+e);
            return ResponseMessage.fail(msg+"失败,请重试！");
        }
        return ResponseMessage.success(msg+"成功！");
    }



}
/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DocumentApiController
 * Author:   lmr
 * Date:     2018/9/30 16:18
 * Description: 文件柜
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
import com.suneee.eas.common.utils.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.fs.Document;
import com.suneee.eas.oa.service.fs.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br> 
 * 〈文件列表〉
 *
 * @author lmr
 * @create 2018/9/30
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.FS_MODULE + FunctionConstant.PUBLIC_DOCUMENT)
public class DocumentListApiController {
    private static final Logger LOGGER = LogManager.getLogger(DocumentListApiController.class);
    @Autowired
    private DocumentService documentService;

    /**
     * 获取左侧边栏树结构的数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("treeData")
    public ResponseMessage treeData(HttpServletRequest request, HttpServletResponse response){
        try {
            String type = RequestUtil.getString(request,"type");
            Set<Document> documentSet = documentService.getTreeData(type);
            return ResponseMessage.success("获取树数据成功！",documentSet);
        } catch (Exception e) {
            LOGGER.error("获取树数据错误！"+e);
            return ResponseMessage.fail("获取树数据失败！");
        }
    }

    /**
     * 获取该用户所有能访问权限的文档（不包括文件夹)
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("listByPage")
    public ResponseMessage listByPage(HttpServletRequest request, HttpServletResponse response){
        try {
            QueryFilter queryFilter = new QueryFilter("listAll",request);
            //获取有权限的文件夹
            List<Document> documentList = documentService.getDocumentByAuth(DocumentService.OWNER_TYPE_ACCESS);
            Long[] docIds = new Long[documentList.size()];
            for (int i =0;i<documentList.size();i++) {
                docIds[i]=documentList.get(i).getId();
            }
            queryFilter.addFilter("parentIds",docIds);
            queryFilter.addFilter("isDir",0);
            queryFilter.addFilter("enterpriseCode",ContextSupportUtil.getCurrentEnterpriseCode());
            Pager<Document> documents = documentService.getPageBySqlKey(queryFilter);
            if(BeanUtils.isNotEmpty(documents)&&BeanUtils.isNotEmpty(documents.getList())) {
                for (Document document : documents.getList()) {
                    document.setAuthNum(documentService.getAllAuthNum(document.getParentId()));
                }
            }
            return ResponseMessage.success("获取文档列表成功！",documents);
        } catch (Exception e) {
            LOGGER.error("获取文档列表失败！"+e);
            return ResponseMessage.fail("获取文档列表失败！");
        }
    }

    /**
     * 点击文件夹后的所有文件（包括文件夹）
     * @param request
     * @return
     */
    @RequestMapping("list")
    public ResponseMessage listByParent(HttpServletRequest request) {
        QueryFilter queryFilter = new QueryFilter("list",request);
        Long id = RequestUtil.getLong(request,"parentId");
        try {
            List<Document> documentList = documentService.getDocumentByAuth(DocumentService.OWNER_TYPE_ACCESS);
            List<Long> ids = new ArrayList<>();
            for(Document document:documentList){
                ids.add(document.getId());
            }
            //如果传了parentId 表示点击了文件夹后获取所有的文件夹和文件，需要判断是否有权限
            //如果没有传，表示搜索。不需要判断权限。
            if(id!=null&&id!=0){
                Set<String> allAuthNum = documentService.getAllAuthNum(id);
                if(allAuthNum.size()==0){
                    return ResponseMessage.fail("您没有权限访问！");
                }
            }
            queryFilter.addFilter("ids",ids);
            queryFilter.addFilter("enterpriseCode",ContextSupportUtil.getCurrentEnterpriseCode());
            Pager<Document> documents = documentService.getPageBySqlKey(queryFilter);
            if(BeanUtils.isNotEmpty(documents)&&BeanUtils.isNotEmpty(documents.getList())) {
                for (Document document : documents.getList()) {
                    if (document.getIsDir() == 1) {
                        document.setSize(documentService.getDirSize(document));
                    }
                }
            }
            return ResponseMessage.success("获取列表成功！",documents);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("获取列表失败！"+e);
            return ResponseMessage.fail("获取列表失败！");
        }
    }

}
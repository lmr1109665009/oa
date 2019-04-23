/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: AuthApiController
 * Author:   lmr
 * Date:     2018/10/9 10:31
 * Description: 分类权限
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.controller.fs;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.fs.Authorization;
import com.suneee.eas.oa.model.fs.Document;
import com.suneee.eas.oa.service.fs.AuthService;
import com.suneee.eas.oa.service.fs.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈分类权限〉
 *
 * @author lmr
 * @create 2018/10/9
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.FS_MODULE + FunctionConstant.AUTH)
public class AuthApiController {
    private static final Logger logger = LogManager.getLogger(AuthApiController.class);
    @Autowired
    private AuthService authService;
    @Autowired
    private DocumentService documentService;

    /**
     * 保存权限设置
     * @param request
     * @param response
     * @param auths
     * @return
     */
    @RequestMapping("save_{isExtend}")
    public ResponseMessage save(HttpServletRequest request,@PathVariable boolean isExtend, HttpServletResponse response,@RequestBody List<Authorization> auths){
        try {
            if(BeanUtils.isEmpty(auths)){
                return ResponseMessage.fail("参数错误！，请重试！");
            }
            authService.saveList(auths);
            if(isExtend){
                //获取其子文件夹
                Long docId = auths.get(0).getDocId();
                Document document = documentService.findById(docId);
                List<Document> documentList =documentService.getByDirPath(document.getDirPath(),1, ContextSupportUtil.getCurrentUserId());
                //设置了父文件夹，子文件夹权限和父一样
                for(Document doc:documentList){
                    for(Authorization auth:auths){
                        auth.setDocId(doc.getId());
                    }
                    authService.deleteByDocId(doc.getId());
                    authService.saveList(auths);
                }
            }
            return ResponseMessage.success("保存成功！");
        }catch (Exception ex){
            logger.error("保存失败:"+ex);
            return ResponseMessage.fail("保存失败，请重试！");
        }
    }
    @RequestMapping("getByDocId")
    public ResponseMessage get(HttpServletRequest request, HttpServletResponse response){
        Long docId = RequestUtil.getLong(request,"docId");
        try {
            List<Authorization> auths = authService.getByDocId(docId);
            return ResponseMessage.success("获取权限信息成功！",auths);
        }catch (Exception ex){
            logger.error("获取权限信息失败："+ex);
            return ResponseMessage.fail("获取权限信息失败，请重试！");
        }
    }
}
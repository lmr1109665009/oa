package com.suneee.ucp.me.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.attachment.AttachmentHandler;
import com.suneee.platform.attachment.AttachmentHandlerFactory;
import com.suneee.platform.attachment.impl.FtpAttachmentHandler;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysPropertyConstants;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.DocType;
import com.suneee.ucp.me.model.Document;
import com.suneee.ucp.me.service.DocTypeService;
import com.suneee.ucp.me.service.DocumentService;

/**
 * @author 游刃
 * @ClassName: DocumentController
 * @Description: 文档管理控制器
 * @date 2017年4月12日 下午8:21:52
 */


@Controller
@RequestMapping("/me/document/")
@Action(ownermodel = SysAuditModelType.XINGZHENG_MANAGEMENT)
public class DocumentformController extends UcpBaseController {
    @Resource
    private DocumentService documentService;
    @Resource
    private DocTypeService docTypeService;
    @Resource
    private SysOrgService orgService;

    //当前附件处理器
    private AttachmentHandler currentmentHander;

    //初始化当前处理器
    private void initCurrentmentHander() throws Exception {
        if (BeanUtils.isEmpty(currentmentHander)) {
            AttachmentHandlerFactory attachmentHandlerFactory = AppUtil.getBean(AttachmentHandlerFactory.class);
            currentmentHander = attachmentHandlerFactory.getCurrentHandler("document.saveType");
        }
    }

    @RequestMapping(value = "save")
    @Action(description = "保存或更新文档", detail = "保存或更新文档")
    public void save(HttpServletRequest request, HttpServletResponse response, Document adocument) throws IOException {
        String resultMsg = "";
        boolean isFront = RequestUtil.getBoolean(request, "isFront");
        int isPrivate = RequestUtil.getInt(request, "isPrivate", 0);
        try {
            SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
            SysOrg sysOrg = orgService.getByUserId(user.getUserId());
            String eid = sysOrg.getOrgCode();
            // 找到文档类型和子文档类型串成一个字符串
            //String types = docTypeService.getTypes(adocument.getDocTypeId());
            //adocument.setTypes(types);
            //判断是否有文件名重复，有重复则重命名
            documentService.renameDocument(adocument);

            //个人文件柜更新
            if (adocument.getId() != null && adocument.getId() != 0 && isPrivate == 1) {
                documentService.update(adocument);
                resultMsg = "更新文档成功!";
                if (isFront) {
                    addMessage(ResultMessage.Success, resultMsg, "", response);
                    return;
                }
                writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
            } else if (eid != null) {
                Long docTypeId = adocument.getDocTypeId();
                List<DocType> list = docTypeService.getDocByParentId(docTypeId);
                if (list.size() != 0) {
                    resultMsg = "该目录下还有子目录，不能上传文件!";
                    writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Fail);
                } else {
                    if (null == adocument.getId() || 0 == adocument.getId()) {// 新增
                        initCurrentmentHander();
                        List<String[]> nameAndPaths = uplodeFile(request, currentmentHander, eid);
                        // 设置创建者
                        adocument.setUper(user.getUserId());
                        adocument.setUperName(user.getFullname());
                        //adocument.setName(nameAndPaths.get(0)[0]);
                        adocument.setPath(nameAndPaths.get(0)[1]);
                        adocument.setSize(nameAndPaths.get(0)[2]);
                        adocument.setId(UniqueIdUtil.genId());
                        adocument.setEid(eid);
                        Date date = new Date();
                        Timestamp ts = new Timestamp(date.getTime());
                        adocument.setUpTime(ts);
                        documentService.add(adocument);
                        resultMsg = "添加文档成功!";
                    } else {
                        // 更新
                        documentService.update(adocument);
                        resultMsg = "更新文档成功!";
                    }
                    if (isFront) {
                        addMessage(ResultMessage.Success, resultMsg, "", response);
                        return;
                    }
                    writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
                }
            } else {
                resultMsg = "您还没有设置组织，请设置后再操作!";
                writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Fail);
            }
        } catch (Exception e) {
            String str = MessageUtil.getMessage();
            ResultMessage resultMessage;
            String message;
            if (StringUtil.isNotEmpty(str)) {
                message = "操作文档失败:" + str;
            } else {
                message = ExceptionUtil.getExceptionMessage(e);
            }
            if (isFront) {
                addMessage(ResultMessage.Fail, resultMsg, "", response);
                return;
            }
            resultMessage = new ResultMessage(ResultMessage.Fail, message);
            response.getWriter().print(resultMessage);
        }
    }




    /**
     * 在实体对象进行封装前，从对应源获取原实体
     *
     * @param acceptId
     * @param model
     * @return
     * @throws Exception
     */
    @ModelAttribute
    protected Document getFormObject(@RequestParam(value = "id", required = false) Long id, Model model) throws Exception {
        logger.debug("enter SysBulletin getFormObject here....");
        Document document = null;
        if (id != null) {
            document = documentService.getById(id);
        } else {
            document = new Document();
        }
        return document;
    }

}

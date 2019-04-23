package com.suneee.oa.controller.document;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.model.docFile.DocFile;
import com.suneee.oa.service.docFile.DocFileService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.attachment.AttachmentHandler;
import com.suneee.platform.attachment.AttachmentHandlerFactory;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/me/newDocFile/")
public class DocFileEditController extends UcpBaseController {

    @Resource
    private DocFileService docFileService;

    @Resource
    private SysOrgService orgService;
    @Resource
    private UserPositionService uerPositionService;
    @Resource
    private Properties configProperties;
    private AttachmentHandler currentmentHander;

    // 初始化当前处理器
    private void initCurrentmentHander() throws Exception {
        if (BeanUtils.isEmpty(currentmentHander)) {
            AttachmentHandlerFactory attachmentHandlerFactory = AppUtil.getBean(AttachmentHandlerFactory.class);
            currentmentHander = attachmentHandlerFactory.getCurrentHandler();
        }
    }

    @RequestMapping(value = "save")
    @ResponseBody
    @Action(description = "保存或更新文档", detail = "保存或更新文档")
    public ResultVo save(HttpServletRequest request, HttpServletResponse response, DocFile adocFile) throws IOException {
        String resultMsg = "";
        int isPrivate = RequestUtil.getInt(request, "isPrivate", 0);
        int classify = RequestUtil.getInt(request, "classify");
        Long depmentId = null;
        // 获取当前用户所在的部门id
        Long curUserId = ContextUtil.getCurrentUserId();
        List<UserPosition> userPositionList = uerPositionService.getByUserId(curUserId);
        if (userPositionList != null && userPositionList.size() > 0) {
            depmentId = userPositionList.get(0).getOrgId();
        }
        try {
            SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
            String eid = ContextSupportUtil.getCurrentEnterpriseCode();
            // 找到文档类型和子文档类型串成一个字符串
            // String types = docTypeService.getTypes(adocument.getDocTypeId());
            // adocument.setTypes(types);
            // 判断是否有文件名重复，有重复则重命名
            adocFile.setClassify(classify);
            docFileService.renameDocFile(adocFile);
            adocFile.setIsPrivate(isPrivate);
            // 文件柜更新
            if (eid != null) {
                if (adocFile.getId() != null && adocFile.getId() != 0) {
                    docFileService.update(adocFile);
                    if (adocFile.getIsDocType() != 1) {
                        resultMsg = "更新文档成功!";
                    } else {
                        resultMsg = "更新文件夹成功!";
                    }
                } else {
                    // 新增
                    int isDocType = RequestUtil.getInt(request, "isDocType");
                    if (1 != isDocType) {
                        initCurrentmentHander();
                        List<String[]> nameAndPaths = uplodeFile(request, currentmentHander, eid);
                        adocFile.setPath(nameAndPaths.get(0)[1]);
                        adocFile.setSize(nameAndPaths.get(0)[2]);
                    }
                    // 设置创建者
                    adocFile.setUper(user.getUserId());
                    adocFile.setUperName(user.getFullname());
                    // adocument.setName(nameAndPaths.get(0)[0]);
                    adocFile.setDepartmentId(depmentId);
                    adocFile.setId(UniqueIdUtil.genId());
                    adocFile.setIsDocType(isDocType);
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    adocFile.setUpTime(ts);
                   if ((long) classify == Long.valueOf(configProperties.getProperty("groupId"))) {
                        eid = docFileService.getGroupCode();
                    }
                    adocFile.setEid(eid);
                    docFileService.add(adocFile);
                    if(adocFile.getIsDocType()!=1) {
                        resultMsg = "添加文档成功!";
                    }else{
                        resultMsg= "添加文件夹成功!";
                    }
                    DocFile parentDocFile = docFileService.getById(adocFile.getParentId());
                    String si = adocFile.getSize();
                    Double size = docFileService.getDoubleSize(si);
                    docFileService.setAddParentSize(adocFile.getParentId(), size);
                }
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,resultMsg);
            } else {
                resultMsg = "您还没有设置组织，请设置后再操作!";
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,resultMsg);
            }
        } catch (Exception e) {
            String message = "";
            ResultVo resultMessage= null;
            if(adocFile.getId()==null||adocFile.getId()==0){
                if(adocFile.getIsDocType()!=1) {
                    message = "添加文档失败!";
                }else{
                    message= "添加文件夹失败!";
                }
            }else{
                if(adocFile.getIsDocType()!=1) {
                    message = "编辑文档失败!";
                }else{
                    message= "编辑文件夹失败!";
                }
            }
            resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
            return resultMessage;
        }
    }


    @RequestMapping("details")
    @Action(description = "查看文档明细")
    @ResponseBody
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = RequestUtil.getLong(request, "id");
        JSONObject json = new JSONObject();
        try {
            DocFile docFile = docFileService.getById(id);
            json.put("docFile", docFile);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取文档详情成功", json);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取文档详情失败", e);
        }

    }

    /**
     * 删除公告栏目
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    @Action(description = "删除文档")
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            Long[] ids = RequestUtil.getLongAryByStr(request, "id");
            // 物理删除代码
            // for (Long id : ids) {
            // DocFile file = docFileService.getById(id);
            // String FTPname = file.getPath();
            // initCurrentmentHander();
            // SysFile sysfile = new SysFile();
            // sysfile.setFilePath(FTPname);
            // String type = currentmentHander.getType();
            //
            // List<DocFile> docFileList =
            // docFileService.findSame(file.getPath(),file.getSize());
            // if(docFileList.size()==1){
            //
            // currentmentHander.remove(sysfile);
            // }
            // }
            List<DocFile> docFileList = docFileService.getByIds(ids);
            Double countSize = 0.0;
            for (DocFile docFile : docFileList) {
                this.delete(docFile.getId());
                String si = docFile.getSize();
                Double size = docFileService.getDoubleSize(si);
                countSize += size;
            }
            docFileService.setSubParentSize(docFileList.get(0).getParentId(), countSize);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除成功!");

        } catch (Exception ex) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败!");
        }
    }

    public void delete(Long id) {
        List<DocFile> docFileList = docFileService.getByParentId(id);
        for (DocFile docFile : docFileList) {
            if (1 == docFile.getIsDocType()) {
                this.delete(docFile.getId());
            }
        }
        docFileService.delById(id);
        docFileService.deleteByParentId(id);
    }
}

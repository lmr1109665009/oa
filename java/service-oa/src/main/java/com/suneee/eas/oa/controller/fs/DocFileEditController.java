package com.suneee.eas.oa.controller.fs;

import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FsConstant;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.utils.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.fs.DocFile;
import com.suneee.eas.oa.service.fs.DocFileService;
import com.suneee.eas.oa.service.user.UserPositionService;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserPosition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/me/newDocFile/")
public class DocFileEditController {
    @Value("${fs.docFile.groupId}")
    private Long groupId;

    @Autowired
    private DocFileService docFileService;

    @Autowired
    private UserPositionService uerPositionService;

    @Autowired
    private UploaderHandler uploaderHandler;


    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseMessage save(HttpServletRequest request, HttpServletResponse response, DocFile adocFile) throws IOException {
        String resultMsg = "";
        int isPrivate = RequestUtil.getInt(request, "isPrivate", 0);
        int classify = RequestUtil.getInt(request, "classify");
        Long depmentId = null;
        List<UserPosition> userPositionList = uerPositionService.getByAccount(ContextSupportUtil.getCurrentUser().getAccount());
        if (userPositionList != null && userPositionList.size() > 0) {
            depmentId = userPositionList.get(0).getOrgId();
        }
        try {
            SysUser user = (SysUser) ContextSupportUtil.getCurrentUser();// 获取当前用户
            String eid = ContextSupportUtil.getCurrentEnterpriseCode();
            // 找到文档类型和子文档类型串成一个字符串
            // String types = docTypeService.getTypes(adocument.getDocTypeId());
            // adocument.setTypes(types);
            // 判断是否有文件名重复，有重复则重命名
            adocFile.setClassify(classify);
            adocFile.setIsPrivate(isPrivate);
            // 文件柜更新
            if (eid != null) {
                if (adocFile.getId() != null && adocFile.getId() != 0) {
                    docFileService.renameDocFile(adocFile,false);
                    docFileService.update(adocFile);
                    if (adocFile.getIsDocType() != 1) {
                        resultMsg = "更新文档成功!";
                    } else {
                        resultMsg = "更新文件夹成功!";
                    }
                } else {
                    // 新增
                    docFileService.renameDocFile(adocFile,true);
                    int isDocType = RequestUtil.getInt(request, "isDocType");
                    if (1 != isDocType) {
                        List<String[]> nameAndPaths = uplodeFile(request, uploaderHandler, eid, FsConstant.PATHTYPE_FILING_CABINET);
                        if(BeanUtils.isEmpty(nameAndPaths)){
                            return ResponseMessage.fail("您没有上传文件");
                        }
                        adocFile.setPath(nameAndPaths.get(0)[1]);
                        adocFile.setSize(nameAndPaths.get(0)[2]);
                    }
                    // 设置创建者
                    adocFile.setUper(user.getUserId());
                    adocFile.setUperName(ContextSupportUtil.getCurrentUsername());
                    // adocument.setName(nameAndPaths.get(0)[0]);
                    adocFile.setDepartmentId(depmentId);
                    adocFile.setId(IdGeneratorUtil.getNextId());
                    adocFile.setIsDocType(isDocType);
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    adocFile.setUpTime(ts);
                   if ((long) classify == groupId) {
                        eid = docFileService.getGroupCode();
                    }
                    adocFile.setEid(eid);
                    docFileService.save(adocFile);
                    if(adocFile.getIsDocType()!=1) {
                        resultMsg = "添加文档成功!";
                    }else{
                        resultMsg= "添加文件夹成功!";
                    }
                    DocFile parentDocFile = docFileService.findById(adocFile.getParentId());
                    String si = adocFile.getSize();
                    Double size = docFileService.getDoubleSize(si);
                    docFileService.setAddParentSize(adocFile.getParentId(), size);
                }
                return ResponseMessage.success(resultMsg);
            } else {
                resultMsg = "您还没有设置组织，请设置后再操作!";
                return ResponseMessage.fail(resultMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = "";
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
            return ResponseMessage.fail(message);
        }
    }

    /**
     * minio文件上传 返回一个数组内容的list，数组【0】为文件名称，【1】为文件路径+实际名称
     *
     * @param request
     * @throws IOException
     * @author 游刃
     */
    public List<String[]> uplodeFile(HttpServletRequest request,UploaderHandler currentHander,String eid, String pathType) throws Exception {

        List<String[]> list = new ArrayList<>();
        MultipartHttpServletRequest multipartRequest= (MultipartHttpServletRequest) request;
        // 检查form中是否有enctype="multipart/form-data"
        if (multipartRequest.getFile("files")!=null) {
            // 将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            for (MultipartFile file:multipartRequest.getFiles("files")){
                if (file==null){
                    continue;
                }
                String sqlfileName = file.getOriginalFilename();
                if (StringUtils.isBlank(sqlfileName))
                    return null;
                // imgPath为原文件名
                int idx = sqlfileName.lastIndexOf(".");
                // 文件后缀
                String extention = sqlfileName.substring(idx);
                Date date = new java.util.Date(System.currentTimeMillis());
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String time = fmt.format(date);

                // 新的文件名(日期+后缀)
                sqlfileName = time + extention;
                String path = eid+"/"+pathType+"/"+sqlfileName;
                currentHander.upload(path, file.getInputStream());

                String[] nameAndPath = new String[3];
                double d = Double.valueOf(file.getSize());
                nameAndPath[0] = file.getOriginalFilename();
                nameAndPath[1] = path;
                nameAndPath[2] = getsize(d);
                list.add(nameAndPath);
            }
        }

        return list;
    }

    /**
     * 计算文件大小
     *
     * @param length
     * @return
     * @author 游刃
     */
    public static String getsize(Double length) {
        return length > 1024
                ? ((length /= 1024) > 1024 ? ((length /= 1024) > 1024 ? String.format("%.2f", (length /= 1024)) + "GB"
                : String.format("%.2f", length) + "M") : String.format("%.2f", length) + "KB")
                : String.format("%.2f", length) + "B";
    }


    @RequestMapping("details")
    @ResponseBody
    public ResponseMessage get(HttpServletRequest request){
        Long id = RequestUtil.getLong(request, "id");
        JSONObject json = new JSONObject();
        try {
            DocFile docFile = docFileService.findById(id);
            json.put("docFile", docFile);
            return ResponseMessage.success("获取文档详情成功", json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.fail("获取文档详情失败");
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
    public ResponseMessage del(HttpServletRequest request) throws Exception {
        try {
            Long[] ids = RequestUtil.getLongAryByStr(request, "id");
            List<DocFile> docFileList = docFileService.getByIds(ids);
            Double countSize = 0.0;
            for (DocFile docFile : docFileList) {
                this.delete(docFile.getId());
                String si = docFile.getSize();
                Double size = docFileService.getDoubleSize(si);
                countSize += size;
            }
            docFileService.setSubParentSize(docFileList.get(0).getParentId(), countSize);
            return ResponseMessage.success("删除成功!");

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseMessage.fail("删除失败!");
        }
    }

    public void delete(Long id) {
        List<DocFile> docFileList = docFileService.getByParentId(id);
        for (DocFile docFile : docFileList) {
            if (1 == docFile.getIsDocType()) {
                this.delete(docFile.getId());
            }
        }
        docFileService.deleteById(id);
        docFileService.deleteByParentId(id);
    }
}

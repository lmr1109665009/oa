package com.suneee.oa.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.encrypt.Base64;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.*;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 对象功能:附件 控制器类 开发公司:广州宏天软件有限公司 开发人员:csx 创建时间:2011-11-26 18:19:16
 */
@Controller
@RequestMapping("/platform/system/sysFileApi/")
@Action(ownermodel = SysAuditModelType.SYSTEM_SETTING)
public class SysFileApiController extends BaseController {
    private Log logger = LogFactory.getLog(com.suneee.platform.controller.system.SysFileController.class);
    @Resource
    private SysFileService sysFileService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private GlobalTypeService globalTypeService;
    @Resource
    private SysTypeKeyService sysTypeKeyService;
    //附件保存类型
    private static String saveType;
    private String getSaveType(){
        if (saveType!=null){
            return saveType;
        }
        saveType=ServiceUtil.getSaveType();
        return saveType;
    }
    @Resource
    private SysPropertyService sysPropertyService;
    /**
     * 取得附件分页列表
     *
     * @param request
     * @param response
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @ResponseBody
    @Action(description = "查看附件分页列表", exectype = "管理日志", detail = "查看附件分页列表")
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long typeId = RequestUtil.getLong(request, "typeId");
        QueryFilter filter = new QueryFilter(request, "sysFileItem");
        if (typeId != 0) {
            filter.addFilter("typeId", typeId);

        }

        List<SysFile> list = sysFileService.getAll(filter);
//        return getAutoView().addObject("sysFileList", list);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",list);
    }

    /**
     * 删除附件
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    @Action(description = "删除附件",
            execOrder = ActionExecOrder.BEFORE,
            exectype = "管理日志",
            detail = "删除系统附件" + "<#list StringUtils.split(fileId,\",\") as item>"
                    + "<#assign entity=sysFileService.getById(Long.valueOf(item))/>"
                    + "【${entity.fileName}】" + "</#list>")
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultVo message = null;
        Long[] ids = RequestUtil.getLongAryByStr(request, "fileId");
        try {
            sysFileService.delSysFileByIds(ids);
            message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除附件成功");
        } catch (Exception e) {
            message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除附件失败");
        }
        return message;
    }

    /**
     * 删除附件。
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("delByFileId")
    @Action(description = "删除附件", execOrder = ActionExecOrder.BEFORE, exectype = "管理日志",
            detail = "删除系统附件" + "<#list StringUtils.split(ids,\",\") as item>"
                    + "<#assign entity=sysFileService.getById(Long.valueOf(item))/>" + "【${entity.fileName}】" + "</#list>")
    public void delByFileId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        Long[] ids = RequestUtil.getLongAryByStr(request, "ids");
        try {
            sysFileService.delSysFileByIds(ids);
            out.print("{\"success\":\"true\"}");
        } catch (Exception e) {
            out.print("{\"success\":\"false\"}");
        }

    }

    @RequestMapping("edit")
    @ResponseBody
    @Action(description = "编辑附件", exectype = "管理日志",
            detail = "<#if isAdd>添加附件<#else>编辑附件" +
                    "<#assign entity=sysFileService.getById(Long.valueOf(fileId))/>"+
                    "【${entity.fileName}】</#if>")
    public ResultVo edit(HttpServletRequest request) throws Exception {
        Long fileId = RequestUtil.getLong(request, "fileId");
        String returnUrl = RequestUtil.getPrePage(request);
        SysFile sysFile = null;

        boolean isadd=true;
        if (fileId != 0) {
            sysFile = sysFileService.getById(fileId);
            isadd=false;
        } else {
            sysFile = new SysFile();
        }
        SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
//        return getAutoView().addObject("sysFile", sysFile).addObject("returnUrl", returnUrl);
        Map<String,Object> dataMap=new HashMap<String,Object>();
        dataMap.put("sysFile",sysFile);
        dataMap.put("returnUrl",returnUrl);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取信息成功！",dataMap);
    }

    /**
     * 取得附件明细
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("get")
    @ResponseBody
    @Action(description = "查看附件明细", exectype = "管理日志",
            detail = "查看附件" +
                    "<#assign entity=sysFileService.getById(Long.valueOf(fileId))/>"
                    + "【${entity.fileName}】" + "的明细")
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long id = RequestUtil.getLong(request, "fileId");
        long canReturn=RequestUtil.getLong(request, "canReturn",0);
        SysFile sysFile = sysFileService.getById(id);
        //返回的Url地址
        String returnUrl = RequestUtil.getPrePage(request);
//        return getAutoView()
// .addObject("sysFile", sysFile).addObject("canReturn",canReturn)
// .addObject("returnUrl",returnUrl).addObject("saveType",saveType);
        Map<String,Object> dataMap=new HashMap<String,Object>();
        dataMap.put("sysFile",sysFile);
        dataMap.put("canReturn",canReturn);
        dataMap.put("returnUrl",returnUrl);
        dataMap.put("saveType",getSaveType());
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取信息成功！",dataMap);

    }

    /**
     * 附件上传操作
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("fileUpload")
    @Action(description = "文件上传", exectype = "管理日志", detail = "上传文件" + "<#list sysFiles as item>" + "【${item.fileName}】" + "</#list>")
    public void fileUpload(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter writer = response.getWriter();
        try {
            long typeId = RequestUtil.getLong(request, "typeId");
            String uploadType = RequestUtil.getString(request, "uploadType");             //控件的类型
            String fileFormates = RequestUtil.getString(request, "fileFormates");         //格式要求
            Map<String, MultipartFile> files = request.getFileMap();
            JSONObject resultJson = sysFileService.uploadFile(typeId, null, null, null,uploadType, fileFormates, files);
            writer.println(resultJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
            String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
            response.sendError(500, rootCauseMessage);
        }
    }

    /**
     * 如果文件存在则更新，如果文件不存在则新建文件。
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("saveFile")
    @Action(description = "保存文件",
            execOrder=ActionExecOrder.AFTER,
            detail="<#if isAdd>添加<#else>更新</#if>文件" +
                    "<#list sysFiles as item>" +
                    "【${item.fileName}】" +
                    "</#list>"
    )
    public ResultVo saveFile(MultipartHttpServletRequest request) throws Exception {
        long typeId = RequestUtil.getLong(request,"typeId");
        long fileId=RequestUtil.getLong(request, "fileId");
        ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
        String uploadName = RequestUtil.getString(request, "uploadName", "");
        String ajaxType = RequestUtil.getString(request, "ajaxType", "");
        String inputObjNum = RequestUtil.getString(request, "inputObjNum", "");   //在OFFICE控件对象数组的的序号，用于返回前台时，把相应的值存放在相应的对象里
        String fileType = RequestUtil.getString(request, "fileType", "");	//	扩展wordTemplate 打印时的保存路径
        try{
            Map<String, MultipartFile> files = request.getFileMap();
            JSONObject resultJson = sysFileService.uploadFile(typeId, fileId, fileType, uploadName, null, null, files);
            fileId = resultJson.getLong("fileId");
            String reutrnStr = Long.toString(fileId);
            if(StringUtil.isNotEmpty(inputObjNum)){   //只有火狐和谷歌浏览器才会加入并有这个参数
                reutrnStr = inputObjNum+"##"+reutrnStr;     //返回到前台（用于火狐和谷歌浏览器）时再解析
            }
            if(StringUtil.isNotEmpty(ajaxType) && "obj".equals(ajaxType)&& BeanUtils.isNotIncZeroEmpty(fileId) ){
                result.setMessage(reutrnStr);
                result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
            }else{
                result.setMessage(reutrnStr);
            }
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
            if(StringUtil.isNotEmpty(ajaxType) && "obj".equals(ajaxType)&& BeanUtils.isNotIncZeroEmpty(fileId)  ){
                result.setMessage("-1");
                result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
            }else{
                result.setMessage("-1");
            }
        }
        return result;
    }

    /**
     * 取得附件明细
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getJson")
    @ResponseBody
    @Action(description = "查看附件明细", ownermodel = SysAuditModelType.SYSTEM_SETTING, exectype = "管理日志",detail="查看附件明细")
    public Map<String, Object> getJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        int status = 1;
        try {
            long id = RequestUtil.getLong(request, "fileId");
            SysFile sysFile = sysFileService.getById(id);
            map.put("sysFile", sysFile);
        } catch (Exception e) {
            status = -1;
        }
        map.put("status", status);
        return map;
    }

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("selector")
    @ResponseBody
    @Action(description = "附件查询", exectype = "管理日志", detail = "附件查询")
    public ResultVo selector(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        ModelAndView result = getAutoView();
        Map<String,Object> dataMap=new HashMap<String,Object>();

        Long userId = ContextUtil.getCurrentUserId();
        Long typeId = RequestUtil.getLong(request, "typeId");
        QueryFilter filter = new QueryFilter(request, "sysFileItem");

        filter.addFilter("userId", userId);
        Long cateTypeId = sysTypeKeyService.getByKey(GlobalType.CAT_FILE).getTypeId();
        if (typeId != 0L && !typeId.equals(cateTypeId)) {
            filter.addFilter("typeId", typeId);
        } else {
            filter.addFilter("typeId", null);
        }
        List<SysFile> list = sysFileService.getFileAttch(filter);
//        result.addObject("sysFileList", list);
        dataMap.put("sysFileList",list);
        int isSingle = RequestUtil.getInt(request, "isSingle", 0);
//        result.addObject("isSingle", isSingle);
        dataMap.put("isSingle",isSingle);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",dataMap);
    }

    /**
     * 附件下载
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("download")
    @Action(description = "附件下载", exectype = "管理日志",
            detail ="附件【${SysAuditLinkService.getSysFileLink(Long.valueOf(fileId))}】下载")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 附件保存路径
        long fileId = RequestUtil.getLong(request, "fileId", 0);
        if (fileId == 0)
            return;

        OutputStream outStream = response.getOutputStream();
        try{
            response.setContentType("APPLICATION/OCTET-STREAM");
            SysFile sysFile = sysFileService.getById(fileId);
            long totalBytes = sysFile.getTotalBytes();
            response.setContentLength((int)totalBytes);
            String filedisplay = sysFile.getFileName() + "." + sysFile.getExt();
			/*String agent = (String)request.getHeader("USER-AGENT");
			// FireFox
			if(agent != null && agent.indexOf("MSIE") == -1) {*/
            String agent = request.getHeader("USER-AGENT").toLowerCase();
            // FireFox
            if(agent != null && agent.indexOf("msie") == -1 && !(agent.indexOf("rv") != -1 && agent.indexOf("firefox") == -1)) {
                String enableFileName = "=?UTF-8?B?" + (new String(Base64.getBase64(filedisplay))) + "?=";
                response.setHeader("Content-Disposition", "attachment; filename=" + enableFileName);
            }
            else{
                filedisplay = URLEncoder.encode(filedisplay,"utf-8");
                response.addHeader("Content-Disposition", "attachment;filename=" + filedisplay);
            }
            sysFileService.downloadFile(fileId, outStream);
        }
        catch(Exception e){
            response.setContentType("text/plain");
            response.sendError(404);
        }
        finally{
            if (outStream != null) {
                outStream.close();
                outStream = null;
                response.flushBuffer();
            }
        }
    }

    /**
     * 根据文件id取得附件数据。
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("getFileById")
    @Action(description = "根据文件id取得附件数据", exectype = "管理日志", detail = "根据文件id取得附件数据")
    public void getFileById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long fileId = RequestUtil.getLong(request, "fileId", 0);
        if (fileId == 0)
            return;
        response.setHeader("Content-Type", "image/jpeg");
        OutputStream outStream = response.getOutputStream();
        try{
            SysFile sysFile = sysFileService.downloadFile(fileId, outStream);
            long totalBytes = sysFile.getTotalBytes();
            response.setContentLength((int)totalBytes);
        }
        catch(Exception e){
            outStream.write("获取文件失败".getBytes("utf-8"));
        }
        finally{
            if (outStream != null) {
                outStream.close();
                outStream = null;
            }
            response.flushBuffer();
        }
    }

    /**
     * 取得附件明细
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getFile")
    @Action(description = "查看附件明细", detail = "查看附件明细")
    @ResponseBody
    public SysFile getFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long id = RequestUtil.getLong(request, "fileId");
        SysFile sysFile = sysFileService.getById(id);
        return sysFile;
    }

    /**
     * 根据ID获取附件
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/file_{fileId}")
    public void getById(@PathVariable("fileId") Long fileId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        getFile(request, response, fileId);
    }

    /**
     * 根据ID获取照片(用于用户管理)
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/file_id{fileId}")
    public void getPictureById(@PathVariable("fileId") Long fileId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        getFile(request, response, fileId);
    }

    public void getFile(HttpServletRequest request, HttpServletResponse response, Long fileId) throws IOException {
        response.reset();
        String vers = request.getHeader("USER-AGENT");
        OutputStream outStream = response.getOutputStream();
        String isDownload = request.getParameter("download");
        try{
            SysFile sysFile = sysFileService.getById(fileId);
            String fileName = sysFile.getFileName() + "." + sysFile.getExt();
            String contextType = sysFileService.getContextType(sysFile.getExt(), true);
            response.setContentType(contextType);
            response.setCharacterEncoding("utf-8");
            if (vers.indexOf("Chrome") != -1 && vers.indexOf("Mobile") != -1) {
                fileName = fileName.toString();
            } else {
                fileName = StringUtil.encodingString(fileName, "GB2312", "ISO-8859-1");
            }
            if ("application/octet-stream".equals(contextType) || StringUtils.isNotBlank(isDownload)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            } else {
                response.addHeader("Content-Disposition", "filename=" + fileName);
            }
            response.addHeader("Content-Transfer-Encoding", "binary");
            long totalBytes = sysFile.getTotalBytes();
            response.setContentLength((int)totalBytes);
            sysFileService.downloadFile(fileId, outStream);
        }
        catch(Exception e){
            outStream.write("获取文件失败".getBytes("utf-8"));
        }
        finally{
            if (outStream != null) {
                outStream.close();
                outStream = null;
            }
            response.flushBuffer();
        }
    }


    /**
     * 如果PDF文件存在则更新，如果PDF文件不存在则新建文件。
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("saveFilePdf")
    @Action(description = "保存PDF文件",detail="保存PDF文件")
    public void saveFilePdf(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter writer = response.getWriter();
        long typeId = RequestUtil.getLong(request, "typeId");
        long fileId = RequestUtil.getLong(request, "fileId");
        String uploadName = RequestUtil.getString(request, "uploadName", "");
        try {
            Map<String, MultipartFile> files = request.getFileMap();
            JSONObject resultJson = sysFileService.uploadFile(typeId, fileId, null , uploadName, null, null, files);
            writer.print(resultJson.getString("fileId"));
        } catch (Exception e) {
            logger.warn(e.getMessage());
            writer.print(-1);
        }
    }

    /**
     * 根据文件id取得附件数据。
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("getFileType")
    public void getFileType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        try {
            long fileId = RequestUtil.getLong(request, "fileId", 0);
            SysFile sysFile = null;
            String type = "doc";
            if (fileId > 0) {
                sysFile = sysFileService.getById(fileId);
                type = sysFile.getExt().toLowerCase();
            }
            writer.print(type);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            writer.print("doc");
        } finally {
            writer.close();
        }
    }

    /**
     * 如果Info文件存在则更新，如果Info文件不存在则新建文件。
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("saveFileInfo")
    @Action(description = "保存Info附件（web签章）",detail = "保存Info附件（web签章）")
    public void saveFileInfo(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter writer = response.getWriter();
        long typeId = RequestUtil.getLong(request, "typeId");
        long fileId = RequestUtil.getLong(request, "fileId");
        String uploadName = RequestUtil.getString(request, "uploadName", "");
        try {
            Map<String, MultipartFile> files = request.getFileMap();
            JSONObject resultJson = sysFileService.uploadFile(typeId, fileId, null , uploadName, null, null, files);
            writer.print(resultJson.getString("fileId"));
        } catch (Exception e) {
            logger.warn(e.getMessage());
            writer.print(-1);
        }
    }


    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("pictureShow")
    @ResponseBody
    @Action(description = "图片展示")
    public ResultVo pictureShow(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        ModelAndView result = getAutoView();
        Map<String,Object> dataMap=new HashMap<String,Object>();

        String id = RequestUtil.getString(request, "id");
        String title = RequestUtil.getString(request, "title");
        String type = RequestUtil.getString(request, "type");
        title = URLDecoder.decode(title,"utf-8");

//        result.addObject("id", id);
//        result.addObject("title", title);
//        result.addObject("type", type);
        dataMap.put("id",id);
        dataMap.put("title",title);
        dataMap.put("type",type);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获得数据成功！",dataMap);
    }

    /**
     * 获取用户信息
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("getUserData")
    public void getUserData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        try {
            SysUser user = (SysUser) ContextUtil.getCurrentUser();
            writer.println("{\"success\":\"true\",\"user\":{\"id\":\"" + user.getUserId() + "\",\"name\":\"" + user.getFullname() + "\",\"groupId\":\""+user.getUserOrgId()+"\",\"groupName\":\""+user.getOrgName()+"\" } }");
        } catch (Exception e) {
            logger.warn(e.getMessage());
            writer.println("{\"success\":\"false\",\"user\":\"\"}");
        } finally {
            writer.close();
        }
    }

    /**
     * 更新附件名称。
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("updateFileNameByFileId")
    public void updateFileNameByFileId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        Long fileId = RequestUtil.getLong(request, "fileId");
        String filename = RequestUtil.getString(request, "filename");
        try {
            SysFile file = sysFileService.getById(fileId);
            file.setFileName(filename);
            sysFileService.update(file);
            out.print("{\"success\":\"true\"}");
        } catch (Exception e) {
            out.print("{\"success\":\"false\"}");
        }

    }
}
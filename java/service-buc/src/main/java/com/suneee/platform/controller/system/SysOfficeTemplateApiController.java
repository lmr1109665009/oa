package com.suneee.platform.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.SealRightService;
import com.suneee.platform.service.system.SysFileService;
import com.suneee.platform.service.system.SysOfficeTemplateService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static com.suneee.core.web.util.RequestUtil.*;

/**
 * 对象功能:office模版 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zousiyu
 * 创建时间:2018-01-19
 */
@Controller
@RequestMapping("/api/system/sysOfficeTemplate/")
@Action(ownermodel= SysAuditModelType.SYSTEM_SETTING)
public class SysOfficeTemplateApiController extends BaseController {

    @Resource
    private SysOfficeTemplateService sysOfficeTemplateService;
    @Resource
    protected Properties configproperties;
    @Resource
    private SealRightService sealRightService;
    @Resource
    private SysFileService sysFileService;

    @RequestMapping("listJson")
	@Action(description="查看系统模版分页列表",detail="查看系统模版分页列表")
	@ResponseBody
	public ResultVo listJson(HttpServletRequest request, HttpServletResponse response) throws Exception
		{
			Long id= RequestUtil.getLong(request,"id");
		    QueryFilter queryFilter = new QueryFilter(request,true);
		    Integer templatetype= RequestUtil.getInt(request, "templatetype",1);
			Map<String, Object> filter = queryFilter.getFilters();
			PageList<SysOfficeTemplate> list=(PageList<SysOfficeTemplate>)sysOfficeTemplateService.getAll(queryFilter);
			return new	ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "成功", PageUtil.getPageVo(list));
	}

    /**
     * 删除office模版
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @Action(description="删除系统模版",execOrder= ActionExecOrder.BEFORE,
            detail="删除系统模版:" +
                    "<#list StringUtils.split(id,\",\") as item>" +
                    "<#assign entity=sysOfficeTemplateService.getById(Long.valueOf(item))/>" +
                    "【${entity.subject}】"+
                    "</#list>")
    @ResponseBody
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        ResultMessage message=null;
        try{
            Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
            //删除印章时 一并删除 印章权限
            for (Long id : lAryId)
            {
                sealRightService.delBySealId(id, SealRight.CONTROL_TYPE_OFFICE);
            }
            sysOfficeTemplateService.delByIds(lAryId);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除成功!");
        }
        catch(Exception ex){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"删除失败:" + ex.getMessage());
        }
    }


    @RequestMapping("edit")
    @Action(description="添加或编辑系统模版",
            detail="<#if isAdd>添加系统模版<#else>" +
                    "编辑系统模版" +
                    "<#assign entity=sysOfficeTemplateService.getById(Long.valueOf(id))/>" +
                    "【${entity.subject}】</#if>")
    @ResponseBody
    public ResultVo edit(HttpServletRequest request) throws Exception
    {
        Long id=RequestUtil.getLong(request,"id");

        SysOfficeTemplate sysOfficeTemplate=null;
        boolean isadd=true;
        if(id!=0){
            sysOfficeTemplate= sysOfficeTemplateService.getById(id);
            isadd=false;
        }else{
            sysOfficeTemplate=new SysOfficeTemplate();
        }
        SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"编辑成功",sysOfficeTemplate);
    }


    /**
     * 取得office模版明细
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("get")
    @Action(description="查看系统模版明细",detail="查看系统模版明细")
    @ResponseBody
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        long id=RequestUtil.getLong(request,"id");
        SysOfficeTemplate sysOfficeTemplate = sysOfficeTemplateService.getById(id);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取系统模版明细成功",sysOfficeTemplate);
    }

    @RequestMapping("saveTemplate")
    @Action(description="保存更新系统模板",detail="添加或更新系统模板【${SysAuditLinkService.getsysOfficeTemplateLink(Long.valueOf(id))}】")
    @ResponseBody
    public void saveTemplate(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String resultMsg = "";
        try {
            SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
            String memo = RequestUtil.getString(request, "memo");
            String subject = RequestUtil.getString(request, "subject");
            int templatetype = RequestUtil.getInt(request, "templatetype");
            long id = RequestUtil.getLong(request, "id", 0);
            long fileId = RequestUtil.getLong(request, "fileId", 0);

            SysOfficeTemplate sysOfficeTemplate = null;
            if (id == 0) {//新增
                sysOfficeTemplate = new SysOfficeTemplate();
                sysOfficeTemplate.setId(UniqueIdUtil.genId());
                sysOfficeTemplate.setCreatetime(new Date());
                sysOfficeTemplate.setCreator(user.getFullname());
                sysOfficeTemplate.setCreatorid(user.getUserId());
                sysOfficeTemplate.setMemo(memo);
                sysOfficeTemplate.setSubject(subject);
                sysOfficeTemplate.setTemplatetype(templatetype);

                //根据附件Id获取到附件的保存路径和对应的fileblob
                if (fileId != 0) {
                    SysFile sysFile = sysFileService.getById(fileId);
                    sysOfficeTemplate.setFileid(fileId);
                    sysOfficeTemplate.setTemplateBlob(sysFile.getFileBlob());
                } else {
                    resultMsg = "请选择office模板";
                }
                sysOfficeTemplateService.add(sysOfficeTemplate);
                resultMsg = "添加office模板成功!";

            } else {//更新
                sysOfficeTemplate = sysOfficeTemplateService.getById(id);
                sysOfficeTemplate.setMemo(memo);
                sysOfficeTemplate.setSubject(subject);
                sysOfficeTemplate.setTemplatetype(templatetype);

                if (fileId != 0) {
                    SysFile sysFile = sysFileService.getById(fileId);
                    sysOfficeTemplate.setFileid(fileId);
                    sysOfficeTemplate.setTemplateBlob(sysFile.getFileBlob());
                }
                sysOfficeTemplateService.update(sysOfficeTemplate);
                resultMsg = "更新office模板成功!";
            }


            SysAuditThreadLocalHolder.putParamerter("id", sysOfficeTemplate.getId().toString());
            writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Success, resultMsg));
        } catch (Exception e) {
            String str = MessageUtil.getMessage();
            if (StringUtil.isNotEmpty(str)) {
                ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "操作系统模板失败:" + str);
                response.getWriter().print(resultMessage);
            } else {
                String message = ExceptionUtil.getExceptionMessage(e);
                ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
                response.getWriter().print(resultMessage);
            }
        }
    }


    /**
     * 创建文件目录
     * @param tempPath
     * @param fileName 文件名称
     * @return 文件的完整目录
     */
    private String createFilePath(String tempPath, String fileName){
        File one = new File(tempPath);
        Calendar cal = Calendar.getInstance();
        Integer year = cal.get(Calendar.YEAR); // 当前年份
        Integer month = cal.get(Calendar.MONTH) + 1; // 当前月份
        one = new File(tempPath + File.separator + year + File.separator + month);
        if(!one.exists()){
            one.mkdirs();
        }
        return one.getPath() + File.separator+ fileName;
    }

    /**
     * 判断上传的模板文件是否为DOC,DOCX,XLS格式
     * @param extName
     * @return
     */
    private boolean isOfficeFile(String extName){
        String doc= PropertyUtil.getByAlias("officedoc");
        String [] fileExts=doc.split(",");
        boolean isOfficeFile=true;
        for(String ext:fileExts){
            if(extName.equals(ext)){
                return true;
            }else{
                isOfficeFile=false;
            }
        }
        return isOfficeFile;
    }

    @RequestMapping("dialog")
    @Action(description="跳转到dialog",detail="跳转到dialog")
    @ResponseBody
    public ResultVo dialog(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
        String type = RequestUtil.getString(request, "type");
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"跳转成功",type);
    }


    /**
     * 根据文件id取得模板数据。
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("getTemplateById")
    @Action(description = "根据文件id取得模板数据", exectype = "管理日志", detail = "根据文件id取得模板数据")
    @ResponseBody
    public void getTemplateById(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long templateId = RequestUtil.getLong(request, "templateId", 0);
        response.reset();
        String vers = request.getHeader("USER-AGENT");
        OutputStream outStream = response.getOutputStream();
        String isDownload = request.getParameter("download");
        try{
            SysOfficeTemplate sysOfficeTemplate = sysOfficeTemplateService.getById(templateId);
            SysFile sysFile = sysFileService.getById(sysOfficeTemplate.getFileid());
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
            sysFile = sysFileService.downloadFile(sysFile.getFileId(), outStream);
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

    @RequestMapping("editRight")
    @Action(description="添加或编辑系统模版权限",
            detail="")
    @ResponseBody
    public ResultVo editRight(HttpServletRequest request) throws Exception
    {
        Long templateId=RequestUtil.getLong(request,"id");
        String templateSubject=RequestUtil.getString(request, "name");
        //权限类型列表
        List<Map> typeList = sealRightService.getRightType();
        //印章权限
        Map sealRightMap = sealRightService.getSealRight(templateId,SealRight.CONTROL_TYPE_OFFICE);

        Map map = new HashMap();
        map.put("templateSubject",templateSubject);
        map.put("typeList",typeList);
        map.put("sealRightMap",sealRightMap);
        map.put("templateId",templateId);
        return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"添加或编辑系统模版权限成功",map);

    }

    @SuppressWarnings("unused")
    @RequestMapping("saveRight")
    @Action(description="保存系统模版权限",
            detail="")
    @ResponseBody
    public ResultVo saveRight(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Long templateId=RequestUtil.getLong(request, "templateId");
        String rightType=RequestUtil.getString(request,"rightType");
        String rightIds=RequestUtil.getString(request,"rightIds");
        String rightNames=RequestUtil.getString(request,"rightNames");
        Long userId=ContextUtil.getCurrentUserId();
        String resultMsg=null;
        //保存印章权限
        try {
            sealRightService.saveSealRight(templateId,rightType,rightIds,rightNames,userId,SealRight.CONTROL_TYPE_OFFICE);
            resultMsg="添加office模板权限成功";
            return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,resultMsg);
        } catch (Exception e) {
            String message = ExceptionUtil.getExceptionMessage(e);
            resultMsg="添加office模板权限失败: "+message;
            return  new ResultVo(ResultVo.COMMON_STATUS_FAILED,resultMsg);
        }

    }





}

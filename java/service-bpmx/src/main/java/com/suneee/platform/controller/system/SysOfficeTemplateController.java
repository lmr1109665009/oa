package com.suneee.platform.controller.system;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SealRight;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.model.system.SysOfficeTemplate;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.SealRightService;
import com.suneee.platform.service.system.SysFileService;
import com.suneee.platform.service.system.SysOfficeTemplateService;
import com.suneee.platform.service.util.ServiceUtil;

/**
 * 对象功能:office模版 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zyp
 * 创建时间:2012-05-25 10:16:16
 */
@Controller
@RequestMapping("/platform/system/sysOfficeTemplate/")
@Action(ownermodel=SysAuditModelType.SYSTEM_SETTING)
public class SysOfficeTemplateController extends BaseController
{
	@Resource
	private SysOfficeTemplateService sysOfficeTemplateService;
	@Resource
	protected	Properties configproperties; 
	@Resource
	private SealRightService sealRightService;
	@Resource
	private SysFileService sysFileService;
	
	@RequestMapping("selector")
	@Action(description="查看系统模版分页列表",detail="查看系统模版分页列表")
	public ModelAndView selector(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(request,"sysOfficeTemplateItem");
		
		Long userId = ContextUtil.getCurrentUserId();
		Integer templatetype= RequestUtil.getInt(request, "templatetype",1);
		filter.addFilter("templatetype", templatetype);
		List<SysOfficeTemplate> list=sysOfficeTemplateService.getOfficeTemplateByUserId(userId,filter);
		//List<SysOfficeTemplate> list=sysOfficeTemplateService.getAll(filter);
		ModelAndView mv=this.getAutoView().addObject("sysOfficeTemplateList",list)
				.addObject("templatetype",RequestUtil.getString(request, "templatetype"));
		return mv;
	}
	
	/**
	 * 取得office模版分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看系统模版分页列表",detail="查看系统模版分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<SysOfficeTemplate> list=sysOfficeTemplateService.getAll(new QueryFilter(request,"sysOfficeTemplateItem"));
		ModelAndView mv=this.getAutoView().addObject("sysOfficeTemplateList",list);
		
		return mv;
	}
	
	/**
	 * 删除office模版
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除系统模版",execOrder=ActionExecOrder.BEFORE,
			detail="删除系统模版:" +
					"<#list StringUtils.split(id,\",\") as item>" +
					"<#assign entity=sysOfficeTemplateService.getById(Long.valueOf(item))/>" +
					"【${entity.subject}】"+
					"</#list>")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			//删除印章时 一并删除 印章权限
			for (Long id : lAryId)
			{
				sealRightService.delBySealId(id, SealRight.CONTROL_TYPE_OFFICE);
			}
			sysOfficeTemplateService.delByIds(lAryId);
			
			message=new ResultMessage(ResultMessage.Success, "删除成功!");
		}
		catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail,"删除失败:" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("edit")
	@Action(description="添加或编辑系统模版",
			detail="<#if isAdd>添加系统模版<#else>" +
					"编辑系统模版" +
					"<#assign entity=sysOfficeTemplateService.getById(Long.valueOf(id))/>" +
					"【${entity.subject}】</#if>")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		String returnUrl=RequestUtil.getPrePage(request);
		SysOfficeTemplate sysOfficeTemplate=null;
		boolean isadd=true;
		if(id!=0){
			 sysOfficeTemplate= sysOfficeTemplateService.getById(id);
			 isadd=false;
		}else{
			sysOfficeTemplate=new SysOfficeTemplate();
		}
		SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
		return getAutoView().addObject("sysOfficeTemplate",sysOfficeTemplate).addObject("returnUrl", returnUrl);
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
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		SysOfficeTemplate sysOfficeTemplate = sysOfficeTemplateService.getById(id);		
		return getAutoView().addObject("sysOfficeTemplate", sysOfficeTemplate);
	}
	
	@RequestMapping("saveTemplate")
	@Action(description="保存更新系统模板",detail="添加或更新系统模板【${SysAuditLinkService.getsysOfficeTemplateLink(Long.valueOf(id))}】")
	public void saveTemplate(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String resultMsg="";
		try{
			SysUser user =  (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
			String memo=RequestUtil.getString(request, "memo");
			String subject=RequestUtil.getString(request, "subject");
			int templatetype=RequestUtil.getInt(request, "templatetype");
			long id=RequestUtil.getLong(request, "id",0);
			long fileId = RequestUtil.getLong(request, "fileId",0);
			
			SysOfficeTemplate sysOfficeTemplate=null;
			if (id==0) {//新增
				sysOfficeTemplate=new SysOfficeTemplate();
				sysOfficeTemplate.setId(UniqueIdUtil.genId());
				sysOfficeTemplate.setCreatetime(new Date());
				sysOfficeTemplate.setCreator(user.getFullname());
				sysOfficeTemplate.setCreatorid(user.getUserId());
				sysOfficeTemplate.setMemo(memo);
				sysOfficeTemplate.setSubject(subject);
				sysOfficeTemplate.setTemplatetype(templatetype);
				
				//根据附件Id获取到附件的保存路径和对应的fileblob
				if (fileId!=0) {
					SysFile sysFile=sysFileService.getById(fileId);
					sysOfficeTemplate.setFileid(fileId);
					sysOfficeTemplate.setTemplateBlob(sysFile.getFileBlob());
				}else{
					resultMsg="请选择office模板";
				}
				sysOfficeTemplateService.add(sysOfficeTemplate);
		    	resultMsg="添加office模板成功!";
				
			}else{//更新
				sysOfficeTemplate=sysOfficeTemplateService.getById(id);
				sysOfficeTemplate.setMemo(memo);
				sysOfficeTemplate.setSubject(subject);
				sysOfficeTemplate.setTemplatetype(templatetype);
				
				if (fileId!=0) {
					SysFile sysFile=sysFileService.getById(fileId);
					sysOfficeTemplate.setFileid(fileId);
					sysOfficeTemplate.setTemplateBlob(sysFile.getFileBlob());
				}
				sysOfficeTemplateService.update(sysOfficeTemplate);
		    	resultMsg="更新office模板成功!";
			}
			
			
			SysAuditThreadLocalHolder.putParamerter("id", sysOfficeTemplate.getId().toString());
			writeResultMessage(response.getWriter(),new ResultMessage(ResultMessage.Success,resultMsg));
		}catch (Exception e) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail,"操作系统模板失败:" + str);
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
	public ModelAndView dialog(HttpServletRequest request,HttpServletResponse response) throws Exception
	{
		return this.getAutoView().addObject("type", RequestUtil.getString(request, "type"));
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
	public ResponseMessage getTemplateById(HttpServletRequest request, HttpServletResponse response){
		long templateId = RequestUtil.getLong(request, "templateId", 0);
		try{
			SysOfficeTemplate sysOfficeTemplate = sysOfficeTemplateService.getById(templateId);
			SysFile sysFile = sysFileService.getById(sysOfficeTemplate.getFileid());
			String fileName = sysFile.getFileName() + "." + sysFile.getExt();
			String url = sysFileService.downloadFile(sysFile.getFilePath());
			long totalBytes = sysFile.getTotalBytes();
			response.setContentLength((int)totalBytes);
			Map<String, Object> result = new HashMap<>();
			result.put("url", url);
			result.put("fileName", fileName);
			return ResponseMessage.success("获取模板数据成功。", result);
		}catch(Exception e){
			logger.error("获取模板数据失败："+e.getMessage(), e);
			return ResponseMessage.fail("获取模板数据失败："+e.getMessage(), e);
		}
	}
	
	@RequestMapping("editRight")
	@Action(description="添加或编辑系统模版权限",
			detail="")
	public ModelAndView editRight(HttpServletRequest request) throws Exception
	{
		Long templateId=RequestUtil.getLong(request,"id");
		String templateSubject=RequestUtil.getString(request, "name");
		//templateSubject = new String(templateSubject.getBytes("ISO-8859-1"),"UTF-8");
		String returnUrl=RequestUtil.getPrePage(request);
		//权限类型列表
		List<Map> typeList = sealRightService.getRightType();
		//印章权限
		Map sealRightMap = sealRightService.getSealRight(templateId,SealRight.CONTROL_TYPE_OFFICE);
		return getAutoView().addObject("returnUrl", returnUrl)
							.addObject("templateSubject",templateSubject)
							.addObject("typeList",typeList)
							.addObject("sealRightMap", sealRightMap)
							.addObject("templateId",templateId);
	}
	
	@SuppressWarnings("unused")
	@RequestMapping("saveRight")
	@Action(description="保存系统模版权限",
			detail="")
	public void saveRight(HttpServletRequest request, HttpServletResponse response) throws Exception
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
		} catch (Exception e) {
			String message = ExceptionUtil.getExceptionMessage(e);
			resultMsg="添加office模板权限失败: "+message;
		}
		
		writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
	}

}

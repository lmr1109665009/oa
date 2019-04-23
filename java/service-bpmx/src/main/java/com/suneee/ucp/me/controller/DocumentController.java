package com.suneee.ucp.me.controller;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.attachment.AttachmentHandler;
import com.suneee.platform.attachment.AttachmentHandlerFactory;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.DocType;
import com.suneee.ucp.me.model.Document;
import com.suneee.ucp.me.service.DocTypeService;
import com.suneee.ucp.me.service.DocumentService;
import org.apache.commons.lang3.StringUtils;
import org.displaytag.util.ParamEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 
 * @ClassName: DocumentController
 * @Description: 文档管理控制器
 * @author 游刃
 * @date 2017年4月12日 下午8:21:52
 *
 */

@Controller
@RequestMapping("/me/document/")
@Action(ownermodel = SysAuditModelType.XINGZHENG_MANAGEMENT)
public class DocumentController extends UcpBaseController {
	@Resource
	private DocumentService documentService;
	@Resource
	private DocTypeService docTypeService;
	@Resource
	private SysOrgService orgService;
	@Resource
	private UserPositionService uerPositionService;
   //当前附件处理器
	private AttachmentHandler currentmentHander;
	
	//初始化当前处理器
	private void initCurrentmentHander() throws Exception{
		if(BeanUtils.isEmpty(currentmentHander)){
			AttachmentHandlerFactory attachmentHandlerFactory = AppUtil.getBean(AttachmentHandlerFactory.class);
			currentmentHander=attachmentHandlerFactory.getCurrentHandler();
		}
	}
	@RequestMapping("list")
	@Action(description = "查看文档管理分页列表", execOrder = ActionExecOrder.AFTER, detail = "查看文档管理分页列表", exectype = "管理日志")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
		QueryFilter queryFilter = new QueryFilter(request, "documentItem");
		long typeId = RequestUtil.getLong(request, "docTypeId");
		Long curUserId = ContextUtil.getCurrentUserId();
		SysOrg sysOrg = orgService.getByUserId(curUserId);
	    String eid = sysOrg.getOrgCode();
	    List<Document> list = null;
	    if(typeId<4){
	    	queryFilter.addFilter("eid", eid);
	        list = documentService.getAll(queryFilter);
	    }else{
	    	list = documentService.getAll(queryFilter);
	    }		
		boolean enh = false;
		if (null == list || list.size() < 1) {
			enh = true;
		}
		ModelAndView mv = this.getAutoView().addObject("documentList", list)
				.addObject("isSupportWeixin", isSupportWeixin).addObject("enh", enh);
		// 如果docTypeId不为空，则返回到页面
		String docTypeId = RequestUtil.getString(request, "docTypeId");
		if (StringUtils.isNotBlank(docTypeId)) {
			mv.addObject("docTypeId", docTypeId);			
		}
		mv.addObject("eid", eid);
		return mv;
	}

	@RequestMapping("frontList")
	@Action(description = "查看文档分页列表", execOrder = ActionExecOrder.AFTER, detail = "查看文档分页列表", exectype = "前端日志")
	public void frontList(HttpServletRequest request, HttpServletResponse response) {
		JSONObject data = new JSONObject();
		String message;
		try {
			QueryFilter queryFilter = new QueryFilter(request, "documentItem");
			List<Document> list = documentService.getAll(queryFilter);
			ParamEncoder paramEncoder = new ParamEncoder("documentItem");
			String tableIdCode = paramEncoder.encodeParameterName("");
			data.put("varList", list);
			data.put("tableIdCode", tableIdCode);
			message = "查询成功";
			addMessage(ResultMessage.Success, message, "", data, response);
		} catch (Exception e) {
			message = "查询出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}
	}

	@RequestMapping("frontGetById")
	@Action(description = "查看文档详情", execOrder = ActionExecOrder.AFTER, detail = "查看文档详情", exectype = "前端日志")
	public void frontGetById(HttpServletRequest request, HttpServletResponse response) {
		JSONObject data = new JSONObject();
		String message;
		Long id = RequestUtil.getLong(request, "id");
		try {
			Document document = documentService.getById(id);
			data.put("document", document);
			message = "查询成功";
			addMessage(ResultMessage.Success, message, "", data, response);
		} catch (Exception e) {
			message = "查询出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}

	}

	@RequestMapping("get")
	@Action(description = "查看文档明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		Document document = documentService.getById(id);
		return getAutoView().addObject("adocument", document);
	}

	@RequestMapping("edit")
	@Action(description = "编辑文档栏目")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		String returnUrl = RequestUtil.getPrePage(request);
		ModelAndView mv = getAutoView().addObject("returnUrl", returnUrl);
		if (0 != id) {
			// 编辑，获取原信息
			Document aDocument = documentService.getById(id);
			mv.addObject("adocument", aDocument);
		}
		// 如果docTypeId不为空，则返回到页面
		String docTypeId = RequestUtil.getString(request, "docTypeId");
		mv.addObject("docTypeId", docTypeId);

		// 所有目录
		List<DocType> docTypes = docTypeService.getAllName(null);
		mv.addObject("docTypes", docTypes);
		return mv;
	}

	/**
	 * 删除公告栏目
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除文档")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] ids = RequestUtil.getLongAryByStr(request, "id");
			Long curUserId = ContextUtil.getCurrentUserId();
			SysOrg sysOrg = orgService.getByUserId(curUserId);
		    String eid = sysOrg.getOrgCode();
			for (Long id : ids) {
				Document file = documentService.getById(id);
				String FTPname = file.getPath();			
				initCurrentmentHander();
				SysFile sysfile = new SysFile();
				sysfile.setFilePath(FTPname);				
		        String type = currentmentHander.getType();
				currentmentHander.remove(sysfile);
			}
			if (0 < documentService.deleteAll(ids)) {
				message = new ResultMessage(ResultMessage.Success, "删除文档成功!");
			}
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}   
	/**
	 * 
	 * 文件下载
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "download")
	public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {

		OutputStream outStream = response.getOutputStream();
		try {
			String filePath = RequestUtil.getString(request, "documentIPath");
			response.reset();
			String vers = request.getHeader("USER-AGENT");
			String openIt = request.getParameter("openIt");
			Long id = RequestUtil.getLong(request, "id");
			String fileName = documentService.getById(id).getName();
			filePath = URLDecoder.decode(filePath, "UTF-8");
			// 如果是Linux,所有反斜杠转成斜杠
			filePath = filePath.replace("\\", System.getProperty("file.separator"));
			
			if (StringUtils.isBlank(fileName)) {
				fileName = filePath.substring(filePath.lastIndexOf(System.getProperty("file.separator")) + 1);
			} else {
				// 判断名称是否有文件类型，没有就加上
				if (fileName.split("\\.").length < 2) {
					String[] ds = filePath.split("\\.");
					fileName = fileName + "." + ds[ds.length - 1];
				}
			}
			
			String contextType = fileName.substring(fileName.lastIndexOf(".") + 1);
			response.setContentType(contextType);
			response.setCharacterEncoding("utf-8");
/*			if (vers.indexOf("Chrome") != -1 && vers.indexOf("Mobile") != -1) {
				fileName = fileName.toString();
			} else {*/
				fileName = StringUtil.encodingString(fileName, "GB2312", "ISO-8859-1");
			//}
			if (!"application/octet-stream".equals(contextType) && StringUtils.isNotBlank(openIt)) {
				fileName = URLEncoder.encode(fileName,"utf-8");
				response.addHeader("Content-Disposition", "filename=" + fileName);
			} else {
				response.setContentType("application/force-download");// 设置强制下载不打开
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			}
			response.addHeader("Content-Transfer-Encoding", "binary");
			initCurrentmentHander();
			SysFile sysFile = new SysFile();
			sysFile.setFilePath(filePath);
			currentmentHander.download(sysFile, outStream);
			//response.setContentLength(download(filePath, outStream));
			documentService.updateDownNumber(id);
		} catch (Exception e) {
			outStream.write("获取文件失败".getBytes("utf-8"));
		} finally {
			if (outStream != null) {
				outStream.close();
				outStream = null;
			}
			response.flushBuffer();
		}

	}

	public int download(String filePath, OutputStream outStream) throws Exception {
		String fullPath = filePath;
		File file = new File(fullPath);
		if (file.exists()) {
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(fullPath);
				byte[] b = new byte[1024];
				int i = 0;
				while ((i = inputStream.read(b)) > 0) {
					outStream.write(b, 0, i);
				}
				outStream.flush();
			} catch (Exception e) {
				throw e;
			} finally {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (outStream != null) {
					outStream.close();
					outStream = null;
				}
			}
			return (int) file.length();
		} else {
			throw new RuntimeException("该附件不存在");
		}
	}

	
	
	@RequestMapping(value = "frontSave")
	@Action(description = "保存或更新文档", detail = "保存或更新文档")
	public void frontSave(HttpServletRequest request, HttpServletResponse response,Document adocument,@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		JSONObject data = new JSONObject();
		String message;
		try {
			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
			SysOrg sysOrg = orgService.getByUserId(user.getUserId());
		    String eid = sysOrg.getOrgCode();
			initCurrentmentHander();
			List<String[]> nameAndPaths = uplodeFile(request,currentmentHander,eid);
			// 设置创建者
			adocument.setUper(user.getUserId());
			adocument.setUperName(user.getFullname());
			adocument.setPath(nameAndPaths.get(0)[1]);
			adocument.setSize(nameAndPaths.get(0)[2]);
			adocument.setId(UniqueIdUtil.genId());
			adocument.setEid(eid);
			Date date = new Date();  
		    Timestamp ts = new Timestamp(date.getTime());
		    adocument.setUpTime(ts);
			if(StringUtil.isNotEmpty(adocument.getFileSaveId())){
				Long docTypeId = null;
				DocType docTypeParam = new DocType();
				docTypeParam.setOwner(user.getUserId());
				docTypeParam.setTypeName("归档文件");
				List<DocType> docTypeList =  docTypeService.getDocByNameAndOwerId(docTypeParam);
				if(docTypeList!=null && docTypeList.size()>0){
					docTypeId = docTypeList.get(0).getId();
				}
				
				if(docTypeId == null){
					// 设置创建者
					DocType docType = new DocType();
					docType.setPromulgator(user.getUserId());
					docType.setPromulgatorName(user.getFullname());
					docType.setId(UniqueIdUtil.genId());
					docType.setEid(eid);
					docType.setTypeName("归档文件");
					docType.setParentId(Long.valueOf(4));
					docType.setOwner(user.getUserId());
					docTypeService.add(docType);
					docTypeId = docType.getId();
				}
				adocument.setDocTypeId(docTypeId);
			}

			//判断是否有文件名重复，有重复则重命名
			documentService.renameDocument(adocument);

			documentService.add(adocument);
			message = "添加文档成功!";
			addMessage(ResultMessage.Success, message, "", data, response);
		} catch (Exception e) {
			message = "添加文档出错";
			addMessage(ResultMessage.Fail, message, "", response);
			
		}
	}
	
	
	@RequestMapping(value = "frontSaveFile")
	@Action(description = "保存或更新文档", detail = "保存或更新文档")
	public void frontSaveFile(HttpServletRequest request, HttpServletResponse response,Document adocument,@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		JSONObject data = new JSONObject();
		response.setCharacterEncoding("UTF-8");
		String message;
		try {
			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
			String attachPath = ServiceUtil.getBasePath().replace("/", File.separator);
			String enterpriseCode = adocument.getEid();
			String downloadUrl = adocument.getDownloadUrl();
			//String fileName = new String(adocument.getName().getBytes("iso8859-1"),"utf-8");
			String fileName = adocument.getName();
			List<String[]> nameAndPaths = downLoadFromUrlToFTP(downloadUrl, fileName,attachPath,enterpriseCode);
			// 设置创建者
			adocument.setUper(user.getUserId());
			adocument.setUperName(user.getFullname());
			adocument.setPath(nameAndPaths.get(0)[1]);
			adocument.setSize(nameAndPaths.get(0)[2]);
			adocument.setId(UniqueIdUtil.genId());
			Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
			if(StringUtil.isNotEmpty(adocument.getFileSaveId())){
				Long docTypeId = null;
				
				//查询是否存在当前用户的归档文件
				DocType docType = new DocType();
				docType.setOwner(user.getUserId());
				docType.setTypeName("归档文件");
				List<DocType> docTypeList = docTypeService.getDocByNameAndOwerId(docType);
				if(docTypeList!=null && docTypeList.size()>0){
					docTypeId = docTypeList.get(0).getId();
				}
				
				if(docTypeId==null){
					// 设置创建者
					DocType newDocType = new DocType();
					newDocType.setPromulgator(user.getUserId());
					newDocType.setPromulgatorName(user.getFullname());
					newDocType.setId(UniqueIdUtil.genId());
					newDocType.setIsPrivate(1);
					newDocType.setEid(enterpriseCode);
					newDocType.setOwner(user.getUserId());
					newDocType.setTypeName("归档文件");
					newDocType.setParentId(Long.valueOf(4));
					docTypeService.add(newDocType);
					docTypeId = newDocType.getId();
				}
				adocument.setDocTypeId(docTypeId);
				
				//先查找看是否已经存在
				List<Document> historyDocument =  documentService.queryFileById(adocument);
				if(historyDocument!=null && historyDocument.size()>0){
					message = "不要重复上传";
					addMessage(ResultMessage.Fail, message, "1", response);
					return;
				}
				adocument.setName(fileName);
				documentService.add(adocument);
				message = "添加文档成功!";
				
				String url = "/bpmx/weixin/filingCabinetP/personnelFiling.html?id="+adocument.getDocTypeId();
				String flag = request.getParameter("flag");
				if("0".equals(flag)){
					url = "/bpmx/weixin/filingCabinetM/personnelFiling.html?id="+adocument.getDocTypeId();
				}
				
				data.put("url", url);
				addMessage(ResultMessage.Success, message, "", data, response);
			}else{
				message = "fileSaveId不能为空";
				addMessage(ResultMessage.Fail, message, "", response);
			}
			
		} catch (Exception e) {
			message = "添加文档出错";
			addMessage(ResultMessage.Fail, message, "", response);
			
		}
	}
	
	@RequestMapping(value = "frontSaveFileList")
	@Action(description = "保存或更新文档", detail = "保存或更新文档")
	public void frontSaveFileList(HttpServletRequest request, HttpServletResponse response){
		JSONObject data = new JSONObject();
		String message;
		try {
			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
			String enterpriseCode = RequestUtil.getString(request, "eid");
			QueryFilter queryFilter = new QueryFilter(request);
			queryFilter.addFilter("uper", user.getUserId());
			queryFilter.addFilter("eid", enterpriseCode);
			String name = RequestUtil.getString(request, "name");
			if(StringUtil.isNotEmpty(name)){
				queryFilter.addFilter("name", name);
			}
			List<Document> fileList = documentService.frontFileList(queryFilter);
			data.put("varList", fileList);
			data.put("totalSize", queryFilter.getPageBean().getTotalCount());
			message = "获取文件成功!";
			addMessage(ResultMessage.Success, message, "", data, response);
		} catch (Exception e) {
			message = "获取文件出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}
		
	}

	/**
	 * 文件柜文件搜索接口
	 * @param request
	 * @return
	 */
	@RequestMapping("searchFile")
	@ResponseBody
	public Object searchFile(HttpServletRequest request) {
		QueryFilter queryFilter = new QueryFilter(request,true);
		queryFilter.addFilter("privateFlag",0);
		Long curUserId = ContextUtil.getCurrentUserId();
		List<Document> list = null;
		int flag=RequestUtil.getInt(request,"flag",0);

		switch (flag){
			case 1:
				//公共文件柜
				SysOrg sysOrg = orgService.getByUserId(curUserId);
				String eid = sysOrg.getOrgCode();
				if (eid==null){
					eid="";
				}
				queryFilter.addFilter("eid",eid);
				queryFilter.addFilter("allDoctypeIds",docTypeService.queyDoctypeChildIds(3L));
				break;
			case 2:
				//部门文件柜
				Long depmentId = null;
				//获取当前用户所在的部门id
				List<SysOrg> orgList =  orgService.getOrgsByUserId(curUserId);
				if(orgList!=null && orgList.size()>0){
					//获取当前用户所在的部门id
					List<UserPosition> userPositionList =  uerPositionService.getByUserId(curUserId);
					if(userPositionList!=null && userPositionList.size()>0){
						depmentId = userPositionList.get(0).getOrgId();
					}
				}
				//depmentId=null为超级管理员，可查看所有部门文件柜
				if(depmentId!=null){
					QueryFilter docFilter=new QueryFilter(request,false);
					docFilter.addFilter("departmentId",depmentId);
					String allDoctypeIds="";
					List<DocType> docTypeList=docTypeService.getAll(docFilter);
					for (DocType docType:docTypeList){
						allDoctypeIds+=docTypeService.queyDoctypeChildIds(docType.getId())+",";
					}
					if (StringUtil.isNotEmpty(allDoctypeIds)){
						allDoctypeIds=allDoctypeIds.substring(0,allDoctypeIds.lastIndexOf(","));
					}
					if (StringUtil.isEmpty(allDoctypeIds)){
						allDoctypeIds="-1";
					}
					queryFilter.addFilter("allDoctypeIds",allDoctypeIds);
				}else {
					queryFilter.addFilter("allDoctypeIds",docTypeService.queyDoctypeChildIds(2L));
				}
				break;
			default:
				//个人文件柜
				//获取所有个人文件夹
				QueryFilter dirFilter=new QueryFilter(request,false);
				dirFilter.addFilter("promulgator",curUserId);
				dirFilter.addFilter("privateFlag",1);
				List<DocType> dirList=docTypeService.getAll(dirFilter);
				StringBuilder allDoctypeIds=new StringBuilder("4");
				for (DocType dir:dirList){
					allDoctypeIds.append(","+dir.getId());
				}
				queryFilter.addFilter("privateFlag",1);
				queryFilter.addFilter("uper",curUserId);
				queryFilter.addFilter("allDoctypeIds",allDoctypeIds.toString());
				break;
		}
		queryFilter.addFilter("orderField","upTime");
		queryFilter.addFilter("orderSeq","DESC");
		list = documentService.getAll(queryFilter);
		return getPageList(list,queryFilter);

	}

}

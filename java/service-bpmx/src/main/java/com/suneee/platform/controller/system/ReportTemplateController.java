package com.suneee.platform.controller.system;

import java.io.File;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.ReportTemplate;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.ReportTemplateService;

/**
 * 对象功能:报表模板 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:cjj
 * 创建时间:2012-04-12 09:59:47
 */
@Controller
@RequestMapping("/platform/system/reportTemplate/")
@Action(ownermodel = SysAuditModelType.FORM_MANAGEMENT)
public class ReportTemplateController extends BaseController
{
	@Resource
	private ReportTemplateService reportTemplateService;
	
	@Resource
	private GlobalTypeService globalTypeService;
	
	/**
	 * 取得报表模板分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看报表模板分页列表",detail="查看报表模板分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<ReportTemplate> list=reportTemplateService.getAll(new QueryFilter(request,"reportTemplateItem"));
		ModelAndView mv=this.getAutoView().addObject("reportTemplateList",list);
		
		return mv;
	}
	
	/**
	 * 删除报表模板

	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除报表模板",
			execOrder=ActionExecOrder.BEFORE,
			detail="删除报表模板：" +
					"<#list StringUtils.split(reportId,\",\") as item>" +
					"<#assign entity=reportTemplateService.getById(Long.valueOf(item))/>" +
					"【${entity.title}】" +
					"</#list>")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "reportId");
			reportTemplateService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除报表模板成功!");
		}
		catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败:" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("edit")
	@Action(description="添加或编辑班次",
			detail="<#if isAdd>添加班次:<#else>"+
					"编辑班次:" +
					"<#assign entity=reportTemplateService.getById(Long.valueOf(reportId))/>" +
					"【entity.title】</#if>")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long reportId=RequestUtil.getLong(request,"reportId");
		String returnUrl=RequestUtil.getPrePage(request);
		ReportTemplate reportTemplate=null;
		boolean isadd=true;
		if(reportId!=0){
			reportTemplate= reportTemplateService.getById(reportId);
			isadd=false;
		}else{
			reportTemplate=new ReportTemplate();
		}
		//添加系统日志信息 -B
		SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
		
		List<GlobalType> list=globalTypeService.getByCatKey(GlobalType.CAT_REPORT, true);
		return getAutoView().addObject("reportTemplate",reportTemplate).addObject("returnUrl", returnUrl)
				.addObject("typelist", list);
	}

	/**
	 * 取得报表模板明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看报表模板明细",detail="查看报表模板明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"reportId");
		long canReturn=RequestUtil.getLong(request, "canReturn",0);
		ReportTemplate reportTemplate = reportTemplateService.getById(id);
		return getAutoView().addObject("reportTemplate", reportTemplate).addObject("canReturn",canReturn);
	}
	
	/**
	 * 添加或更新报表模板
	 * @param request
	 * @param response
	 * @param reportTemplate 添加或更新的实体
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新流程任务评论",
			execOrder=ActionExecOrder.AFTER,
			detail="<#if isAdd>添加<#else>更新</#if>流程任务评论：" +
					"【${SysAuditLinkService.getReportTemplateLink(Long.valueOf(reportId))}】")
	public void save(MultipartHttpServletRequest request, HttpServletResponse response, ReportTemplate reportTemplate) throws Exception
	{
		String createTime = RequestUtil.getString(request, "tmpCreateTime");
		String st = "";
		if(StringUtil.isNotEmpty(createTime)){
			st = DateUtil.timeStrToDateStr(createTime);
		}
		boolean isadd=reportTemplate.getReportId()==null;
		
		Map<String, MultipartFile> files = request.getFileMap();
		Iterator<MultipartFile> it = files.values().iterator();
		
		if(it.hasNext()) {
			MultipartFile f = it.next();
			String oriFileName = f.getOriginalFilename();
		    
			//开始写入物理文件
			String filePath = FileUtil.getRootPath()+"WEB-INF"+File.separator+ReportTemplate.targetPath+File.separator+oriFileName.replace("\\", File.separator);
			
			FileUtil.writeByte(filePath, f.getBytes()) ;
			
		    // 向数据库中添加数据 
			reportTemplateService.saveReportTemplate(reportTemplate,
					File.separator+"WEB-INF"+File.separator+ReportTemplate.targetPath+File.separator+oriFileName.replace("\\", File.separator),
					st.length()>0?DateUtil.parseDate(st):new Date());
		}
		
		PrintWriter writer =  response.getWriter();
		String result = "{\"result\":"+ResultMessage.Success+",\"message\":\"上传报表模板成功\""+"}";
		writer.print(result);
		//添加系统日志信息 -B
				try {
					SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
					SysAuditThreadLocalHolder.putParamerter("reportId", reportTemplate.getReportId().toString());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
	}

}

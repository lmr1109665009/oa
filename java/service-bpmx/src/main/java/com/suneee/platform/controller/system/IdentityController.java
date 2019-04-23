package com.suneee.platform.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.core.log.SysAuditThreadLocalHolder;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.bpm.BpmFormQuery;
import com.suneee.platform.model.system.Identity;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.platform.xml.util.MsgUtil;

/**
 * 对象功能:流水号生成 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-03 14:40:59
 */
@Controller
@RequestMapping("/platform/system/identity/")
@Action(ownermodel=SysAuditModelType.PROCESS_AUXILIARY)
public class IdentityController extends BaseController
{
	@Resource
	private IdentityService identityService;
	
	/**
	 * 取得流水号生成分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看流水号生成分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<Identity> list=identityService.getAll(new QueryFilter(request,"identityItem"));
		ModelAndView mv=this.getAutoView().addObject("identityList",list);
		return mv;
	}
	
	/**
	 * 取得流水号生成分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("showlist")
	@Action(description="查看流水号生成分页列表",detail="查看流水号生成分页列表")
	public ModelAndView showlist(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<Identity> list=identityService.getAll(new QueryFilter(request,"identityItem"));
		ModelAndView mv=this.getAutoView().addObject("identityList",list);
		return mv;
	}
	
	/**
	 * 删除流水号生成
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除流水号",
			execOrder=ActionExecOrder.AFTER,
			detail="删除流水号:" +
					"<#list StringUtils.split(id,\",\") as item>" +
					"<#assign entity=identityService.getById(Long.valueOf(item))/>" +
					"【${entity.name}】" +
				"</#list>")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			identityService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除流水号生成成功!");
		}
		catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败:" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("edit")
	@Action(description="添加或编辑流水号生成",
			detail="<#if isAdd>添加新的流水号生成<#else>" +
					"编辑流水号:" +
					"【${identity.name}】</#if>"
					)
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		String returnUrl=RequestUtil.getPrePage(request);
		String  islist=RequestUtil.getString(request, "islist");
		Identity identity=null;
		boolean isadd=true;
		if(id!=0){
			 identity= identityService.getById(id);
			 isadd=false;
		}else{
			identity=new Identity();
		}
		SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
		SysAuditThreadLocalHolder.putParamerter("identity", identity);
		return getAutoView().addObject("identity",identity).addObject("returnUrl", returnUrl).addObject("islist",islist);
	}
	/**
	 * 获取所有的流水号
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getAllIdentity")
	@ResponseBody
	public List<Identity> getAllIdentity(HttpServletRequest request) throws Exception
	{
		return identityService.getAll();
	}

	/**
	 * 取得流水号生成明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看流水号生成明细",detail="查看流水号生成明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		long canReturn=RequestUtil.getLong(request, "canReturn",0);
		Identity identity = identityService.getById(id);		
		return getAutoView().addObject("identity", identity).addObject("canReturn",canReturn);
	}
	
	/**
	 * 根据alias取得流水号生成明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getById")
	public void getById(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String alias=RequestUtil.getString(request,"alias");
		Identity identity = identityService.getByAlias(alias);	
		writeResultMessage(response.getWriter(), identity.getName(), ResultMessage.Success);
	}
	/**
	 * 导出选择导出xml
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("export")
	@Action(description = "导出选择导出xml")
	public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String tableIds = RequestUtil.getString(request, "tableIds");
		ModelAndView mv = this.getAutoView();
		mv.addObject("tableIds", tableIds);
		return mv;
	}
	/**
	 * 导出自定义查询xml。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportXml")
	@Action(description = "导出流水号", detail = "导出流水号:" + "<#list StringUtils.split(tableIds,\",\") as item>" + "<#assign entity=bpmFormTableService.getById(Long.valueOf(item))/>" + "【${entity.tableDesc}(${entity.tableName})】" + "</#list>")
	public void exportXml(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String strXml = null;
		String fileName = null;
		Long[] tableIds = RequestUtil.getLongAryByStr(request, "tableIds");
		List<Identity> list = identityService.getAll();
		try {
			if (BeanUtils.isEmpty(tableIds)) {
				if (BeanUtils.isNotEmpty(list)) {
					strXml = identityService.exportXml(list);
					fileName = "全部流水号记录_"
							+ DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
							+ ".xml";
				}
			} else {
				strXml = identityService.exportXml(tableIds);
				fileName = DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
						+ ".xml";
				if (tableIds.length == 1) {
					Identity identity = identityService.getById(tableIds[0]);
					fileName = identity.getName() + "_" + fileName;
				} else if (tableIds.length == list.size()) {
					fileName = "全部流水号记录_" + fileName;
				} else {
					fileName = "多条流水号记录_" + fileName;
				}
			}
			FileUtil.downLoad(request, response, strXml, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 导入流水号的XML
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importXml")
	@Action(description = "导入流水号")
	public void importXml(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartFile fileLoad = request.getFile("xmlFile");
		ResultMessage message = null;
		try {
			identityService.importXml(fileLoad.getInputStream());
			message = new ResultMessage(ResultMessage.Success,
					MsgUtil.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, "导入文件异常，请检查文件格式！");
		}
		writeResultMessage(response.getWriter(), message);
	}
	

}

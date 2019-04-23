/**
 * 
 */
package com.suneee.ucp.base.controller.system;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.AppConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.excel.util.ExcelUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.weixin.util.CommonUtil;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.system.SysOrgExtService;
import com.suneee.ucp.base.vo.ResultVo;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/platform/system/sysOrg/")
public class SysOrgExtController extends UcpBaseController{
	@Resource 
	private SysOrgExtService sysOrgExtService;
	
	/**
	 * 打开组织架构导入对话框
	 * @param request
	 * @param resonse
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("importDialog")
	@Action(description = "组织架构导入对话框")
	public ModelAndView importDialog(HttpServletRequest request, HttpServletResponse resonse) throws Exception{
		return getAutoView();
	}
	
	@RequestMapping("import")
	@Action(description = "组织架构导入")
	@ResponseBody
	public ResultVo importOrg(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			// 获取导入文件
			MultipartFile file = request.getFile("orgFile");
			
			// 文件名
			String fileName = file.getOriginalFilename();
			// 文件扩展名
			String extName = fileName.substring(fileName.lastIndexOf("."));
			if(!".xls".equals(extName)){
				logger.error("批量导入组织架构失败：导入文件仅支持.xls格式！");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "批量导入组织架构失败：导入文件仅支持.xls格式！");
			}
			String failedOrg = sysOrgExtService.importOrg(file);
			if(StringUtils.isBlank(failedOrg)){
				return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "批量导入组织架构成功！");
			}else{
				logger.error("以下组织导入失败：\n组织名称路径\t失败原因\n" + Constants.SEPARATOR_LINE_BREAK + failedOrg);
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "以下组织导入失败：\n组织名称路径\t失败原因\n" + Constants.SEPARATOR_LINE_BREAK + failedOrg);
			}
		} catch (Exception e) {
			this.writeResultMessage(response.getWriter(), "批量导入组织架构失败", ResultMessage.Fail);
			logger.error("批量导入组织架构失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "批量导入组织架构失败：系统内部错误！");
		}
	}
	
	/**
	 * 组织架构导入模板下载
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("download")
	@Action(description = "组织架构导入模板下载")
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取模板文件路径
		//String filePath = request.getSession().getServletContext().getRealPath(AppConfigUtil.get(Constants.FILE_UPLOAD)+"org-template.xls");
		String filePath = AppConfigUtil.get(Constants.FILE_UPLOAD)+"org-template.xls";
		logger.debug(AppConfigUtil.get(Constants.FILE_UPLOAD));
		logger.debug(filePath+"-----------------------------------------------------------------------------------------------------------------文件路径");
		downloadFile(filePath, "组织导入模板.xls", request, response);
	}
	
	/**
	 * 获取企业信息列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getEnterpriseinfoList")
	@Action(description="获取企业信息列表", detail = "从权限中心获取企业信息列表")
	public ModelAndView getEnterpirseInfoList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Enterpriseinfo> enterpriseinfoList = sysOrgExtService.getEnterpirseinfoList(new QueryFilter(request, "enterpriseinfoItem"));
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/base/system/enterpriseinfoList");
		mv.addObject("enterpriseinfoList", enterpriseinfoList);
		return mv;
	}
	
	/**
	 * 获取组织信息列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getOrgList")
	@ResponseBody
	@Action(description="获取组织信息列表", detail="根据企业编码获取组织信息列表")
	public ResultVo getOrgList(HttpServletRequest request, HttpServletResponse response){
		String enterpriseCodes = RequestUtil.getString(request, "enterpriseCodes");
		if(StringUtils.isBlank(enterpriseCodes)){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数不能为空");
		}
		try {
			List<SysOrg> sysOrgList = sysOrgExtService.getByOrgCodes(enterpriseCodes);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "查询组织信息列表成功", sysOrgList);
		} catch (Exception e) {
			logger.error("获取组织信息列表失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息列表失败：" + e.getMessage());
		}
	}
	
	/**
	 * 导出系统组织信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportOrg")
	@Action(description="导出组织信息", detail="导出系统所有组织信息", execOrder=ActionExecOrder.AFTER, exectype="", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	public void exportOrg(HttpServletRequest request, HttpServletResponse response) throws Exception{
		// 导出用户当前的所属企业组织
		QueryFilter queryFilter = new QueryFilter();
		queryFilter.addFilter("orgCode", CookieUitl.getCurrentEnterpriseCode());
		List<Map<String, String>> orgList = sysOrgExtService.getOrgDetailsList(queryFilter);
		Map<String, String> fieldMap = new LinkedHashMap<String, String>();
		fieldMap.put("orgName", "组织名称");
		fieldMap.put("orgCode", "组织代码");
		fieldMap.put("orgSupName", "上级组织名称");
		fieldMap.put("orgTypeName", "组织类型");
		fieldMap.put("orgPathName", "组织名称路径");
		fieldMap.put("orgDesc", "组织描述");
		
		HSSFWorkbook workBook = ExcelUtil.exportExcel("组织架构", 20, fieldMap, orgList);
		HSSFSheet sheet = workBook.getSheetAt(0);
		for(int i = 0; i < fieldMap.size(); i++){
			sheet.autoSizeColumn(i);
		}
		ExcelUtil.downloadExcel(workBook, "组织架构", response);
	}
	
	/**
	 * 获取企业信息列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("enterpriseinfoPageList")
	@Action(description="获取企业信息列表", detail="根据集团编码从权限中心获取企业信息列表", execOrder=ActionExecOrder.AFTER, exectype="管理日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo enterpriseinfoPageList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			PageList<Enterpriseinfo> enterpriseinfoList = 
					(PageList<Enterpriseinfo>)sysOrgExtService.getEnterpirseinfoList(new QueryFilter(request));
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取企业信息列表成功！", CommonUtil.getListModel(enterpriseinfoList));
		} catch (Exception e) {
			logger.error("获取企业信息列表：失败" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取企业信息列表失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("details")
	@Action(description="获取组织信息详情", detail="获取组织信息详情", execOrder=ActionExecOrder.AFTER, exectype="管理日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo details(HttpServletRequest request, HttpServletResponse response){
		Long orgId = RequestUtil.getLong(request, "orgId");
		if(orgId == 0){
			logger.error("获取组织信息详情失败：请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息详情失败：请求参数错误");
		}
		try {
			SysOrg sysOrg = sysOrgExtService.getById(orgId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取组织信息详情成功！", sysOrg);
		} catch (Exception e) {
			logger.error("获取组织信息详情失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息详情失败：" + e.getMessage());
		}
	}
	
	@RequestMapping("getSubOrg")
	@Action(description="获取组织的子组织信息", detail="获取组织的子组织信息", execOrder=ActionExecOrder.AFTER, exectype="管理日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo getSubOrg(HttpServletRequest request, HttpServletResponse response){
		Long orgId = RequestUtil.getLong(request, "orgId");
		if(orgId == 0){
			logger.error("获取组织的子组织信息失败：请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织的子组织信息失败：请求参数错误");
		}
		try {
			SysOrg sysOrg = sysOrgExtService.getById(orgId);
			if(sysOrg == null){
				logger.error("获取组织的子组织信息失败：组织ID为【" + orgId + "】的组织不存在！");
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织的子组织信息失败：该组织不存在", sysOrg);
			}
			
			List<SysOrg> orgList = sysOrgExtService.getByOrgPath(sysOrg.getPath());
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取组织的子组织信息成功！", orgList);
		} catch (Exception e) {
			logger.error("获取组织的子组织信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织的子组织信息失败：" + e.getMessage());
		}
	}
}

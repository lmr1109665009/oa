package com.suneee.ucp.me.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.SysPropertyConstants;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.OfficeObject;
import com.suneee.ucp.me.model.OfficeObjectApply;
import com.suneee.ucp.me.model.OfficeObjectType;
import com.suneee.ucp.me.service.OfficeObjectApplyService;
import com.suneee.ucp.me.service.OfficeObjectTypeService;

@Controller
@RequestMapping("/me/officeObjectApply/")
public class OfficeObjectApplyController extends UcpBaseController{
	@Resource
	private OfficeObjectApplyService service;
	
	@RequestMapping("list")
	public ModelAndView list(HttpServletRequest request) throws Exception{
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
		
		QueryFilter queryFilter = new QueryFilter(request, "officeObjectApplyItem");
		List<OfficeObjectApply> list = service.getAll(queryFilter);

		ModelAndView mv = this.getAutoView().addObject("officeObjectApplyList", list).addObject("isSupportWeixin",
				isSupportWeixin);
		
		List<OfficeObjectApply> typeList = service.getTypeList();
		List<OfficeObjectApply> creatorList = service.getCreatorList();
		
		mv.addObject("typeList", typeList);
		mv.addObject("creatorList", creatorList);
		
		return mv;
	}
	
	/**
	 * 导出领用记录
	 * @throws IOException 
	 */
	@RequestMapping("exportExcel")
	public void exportExcel(HttpServletRequest request,HttpServletResponse response) throws IOException{
		QueryFilter queryFilter = new QueryFilter(request, false);
		List<OfficeObjectApply> list = service.getAll(queryFilter);
		//创建HSSFWorkbook对象(excel的文档对象)  
	      HSSFWorkbook wb = new HSSFWorkbook();  
	//建立新的sheet对象（excel的表单）  
	HSSFSheet sheet=wb.createSheet("领用记录");
	sheet.setColumnWidth(0, 20*256);
	sheet.setColumnWidth(5, 10*256);
	//在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个  
	HSSFRow row1=sheet.createRow(0);  
	row1.createCell(0).setCellValue("流水号");
	row1.createCell(1).setCellValue("名称");
	row1.createCell(2).setCellValue("类目");
	row1.createCell(3).setCellValue("领用人");
	row1.createCell(4).setCellValue("领用数量");
	row1.createCell(5).setCellValue("领用时间");
	row1.createCell(6).setCellValue("审批人");
	row1.createCell(7).setCellValue("地域");
	int i = 1;
	for (OfficeObjectApply OfficeObject : list) {
		HSSFRow row=sheet.createRow(i);  
		row.createCell(0).setCellValue(String.valueOf(OfficeObject.getProcessId()));
		row.createCell(1).setCellValue(OfficeObject.getObjectName());
		row.createCell(2).setCellValue(OfficeObject.getType());
		row.createCell(3).setCellValue(OfficeObject.getCreator());
		row.createCell(4).setCellValue(OfficeObject.getApplyNumber());
		row.createCell(5).setCellValue(OfficeObject.getCreatetime());
        //set date format
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFDataFormat format= wb.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("yyyy-MM-dd"));
        row.getCell(5).setCellStyle(cellStyle);
		row.createCell(6).setCellValue(OfficeObject.getApprover());
		row.createCell(7).setCellValue(OfficeObject.getArea());
		i++;
	}
	//输出Excel文件  
    OutputStream output=response.getOutputStream();  
    response.reset();  
    response.setHeader("Content-disposition", "attachment; filename=officeObjectApply.xls");  
    response.setContentType("application/msexcel");          
    wb.write(output);  
    output.close();  
	}
	
	/**
	 * 增加领用申请
	 * @Title: apply
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @param officeObject
	 * @param @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
//	@RequestMapping("save")
//	public void save(HttpServletRequest request, HttpServletResponse response, OfficeObjectApply officeObjectApply) throws Exception{
//		String resultMsg = "";
//		
//		try {
//			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
//			officeObjectApply.setId(UniqueIdUtil.genId());
//			officeObjectApply.setCreateBy(user.getUcUserid());
//			officeObjectApply.setCreator(user.getUsername());
//			
//			service.add(officeObjectApply);
//			resultMsg = "添加成功!";
//			
//			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
//		} catch (Exception e) {
//			
//			String str = MessageUtil.getMessage();
//			if (StringUtil.isNotEmpty(str)) {
//				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "操作失败:" + str);
//				response.getWriter().print(resultMessage);
//			} else {
//				String message = ExceptionUtil.getExceptionMessage(e);
//				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
//				response.getWriter().print(resultMessage);
//			}
//		}
//	}
	
	
	
	/**
	 * 领用列表 和 审核列表
	 * @Title: applyList
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
//	@RequestMapping("applyList")
//	public ModelAndView applyList(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		
//		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
//		String applyType = request.getParameter("applyType");
//		
//		QueryFilter queryFilter = new QueryFilter(request, "officeObjectApplyItem");
//		queryFilter.addFilter("state", applyType);
//		List<OfficeObjectApply> list = new ArrayList<OfficeObjectApply>();
//		if("1".equals(applyType)){
//			list = service.getApplyList(queryFilter);
//		}else if("2".equals(applyType)){
//			list = service.getApprovalList(queryFilter);
//		}
//
//		ModelAndView mv = this.getAutoView().addObject("officeObjectApplyList", list).addObject("isSupportWeixin",
//				isSupportWeixin);
//		
//		List<OfficeObjectType> typeList = typeService.getAll();
//		mv.addObject("typeList", typeList);
//		if("1".equals(applyType)){
//			mv.setViewName("me/officeObjectApplyList");
//		}else if("2".equals(applyType)){
//			mv.setViewName("me/officeObjectApprovalList");
//		}
//		return mv;
//	}
	
	/**
	 * 前端 领用申请
	 * @Title: fsave
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param officeObjectApply
	 * @param @return    参数
	 * @return ResultVo    返回类型
	 * @throws
	 */
//	@RequestMapping("fsave")
//	@ResponseBody
//	public ResultVo fsave(OfficeObjectApply officeObjectApply){
//		ResultVo result = new ResultVo();
//		try {
//			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
//			officeObjectApply.setId(UniqueIdUtil.genId());
//			officeObjectApply.setCreateBy(user.getUcUserid());
//			officeObjectApply.setApplicant(user.getUsername());
//			
//			service.add(officeObjectApply);
//			
//			result.setSuccess();
//			result.setMessage("领用申请成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setFaile();
//			result.setMessage("领用申请失败");
//		}
//		return result;
//		
//	}
	
	
	
	/**
	 * 查询所有的领用记录
	 * @param request
	 * @return
	 */
//	@RequestMapping("fallList")
//	@ResponseBody
//	public ResultVo allList(HttpServletRequest request){
//		ResultVo result = new ResultVo();
//		try {
//			List<OfficeObjectApply> dataList;
//			dataList = service.getAll();
//			
//			result.setData(dataList);
//			result.setSuccess();
//			result.setMessage("获取申请列表成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setFaile();
//			result.setMessage("获取申请列表失败");
//		}
//		return result;
//	}
	
//	@RequestMapping("flist")
//	public void flist(HttpServletRequest request, HttpServletResponse response){
//		
//		String message;
//		try {
//			QueryFilter queryFilter = new QueryFilter(request, "officeObjectApplyItem");
//			List<OfficeObjectApply> list = service.getAll(queryFilter);
//			message = "查询成功";
//			addMessage(ResultMessage.Success, message, "", list, response);
//		} catch (Exception e) {
//			message = "查询出错";
//			addMessage(ResultMessage.Fail, message, "", response);
//		}
//	}
	
	
	/**
	 * 查询所有的领用记录
	 * @param request
	 * @return
	 */
//	@RequestMapping("allList")
//	public ModelAndView allList2Jsp(HttpServletRequest request) throws Exception{
//		
//		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
//		QueryFilter queryFilter = new QueryFilter(request, "officeObjectApplyItem");
//		List<OfficeObjectApply> list = service.getAll(queryFilter);
//
//		ModelAndView mv = this.getAutoView().addObject("officeObjectApplyList", list).addObject("isSupportWeixin",
//				isSupportWeixin);
//		
//		mv.setViewName("officeObjectList");
//		return mv;
//	}
	
	/**
	 * 根据条件进行获取数据
	 * @param request
	 * @return
	 */
//	@RequestMapping("list")
//	@ResponseBody
//	public ResultVo list(HttpServletRequest request){
//		ResultVo result = new ResultVo();
//		try {
//			List<OfficeObjectApply> dataList;
//			QueryFilter queryFilter = new QueryFilter(request);
//			dataList = service.getAll(queryFilter);
//			
//			result.setData(dataList);
//			result.setSuccess();
//			result.setMessage("获取申请列表成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setFaile();
//			result.setMessage("获取申请列表失败");
//		}
//		return result;
//	}
	
	/**
	 * 更新状态
	 * @param request
	 * @return
	 */
//	@RequestMapping("update")
//	public void update(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		
//		String resultMsg = "";
//		
//		String preUrl = RequestUtil.getPrePage(request);
//		ResultMessage message = null;
//		try {
//			
//			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
//			
//			Map params = RequestUtil.getQueryMap(request);
//			params.put("approver", user.getUsername());
//			int num = service.updateState(params);
//			if(num>0){
//				resultMsg = "操作成功";
//			}else{
//				resultMsg = "操作失败";
//			}
//			message = new ResultMessage(ResultMessage.Success, resultMsg);
//		} catch (Exception e) {
//			message = new ResultMessage(ResultMessage.Fail, "删除失败"
//					+ e.getMessage());
//		}
//		addMessage(message, request);
//		response.sendRedirect(preUrl);
//	}
	
}

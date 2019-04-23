/**
 * 
 */
package com.suneee.ucp.base.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.oa.controller.mh.codeTable.AboutDepartApiController;
import com.suneee.oa.model.user.UserSynclog;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.oa.service.user.UserSynclogService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.*;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.service.system.*;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.service.system.SysOrgExtService;
import com.suneee.ucp.base.service.system.SysUserExtService;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对象功能:用户表 控制器类 扩展
 *
 * @author Administrator
 */
@Controller
@RequestMapping("/platform/system/sysUser/")
public class SysUserExtController extends UcpBaseController{
	private Logger logger = LoggerFactory.getLogger(SysUserExtController.class);
	@Resource(name="sysUserExtService")
	private SysUserExtService sysUserExtService;
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private PositionService positionService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private UserSynclogService userSynclogService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	@Resource
	private SysOrgExtService sysOrgExtService;
	@Resource
	private SysUserOrgService sysUserOrgService;

	
	/**
	 * 批量导入用户
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("import")
	@ResponseBody
	public ResultVo importUser(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			// 获取导入文件
			MultipartFile userFile = request.getFile("userFile");
			// 文件名
			String fileName = userFile.getOriginalFilename();
			// 文件扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf("."));
			if(!".xls".equals(fileExt)){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "批量导入用户信息失败：导入文件仅支持.xls格式！");
			}
			String failedUser = sysUserExtService.importUserOrg(userFile);
			if(StringUtils.isBlank(failedUser)){
				return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "批量导入用户信息成功");
			}else{
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "以下用户导入失败：\n姓名\t账号\t失败原因\n" + failedUser);
			}
		} catch (Exception e) {
			logger.error("批量导入用户信息失败：系统内部错误", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "批量导入用户信息失败：系统内部错误！");
		}
	}
	
	/**
	 * 跳转到批量导入页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("importDialog")
	public ModelAndView importDialog(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/system/sysUserImportDialog");
		return mv;
	}
	
	/**
	 * 下载人事档案模板
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("download")
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取模板文件路径

		//String filePath = request.getSession().getServletContext().getRealPath(AppConfigUtil.get(Constants.FILE_UPLOAD)+"user-template.xls");
		String filePath = AppConfigUtil.get(Constants.FILE_UPLOAD)+"user-template.xls";
		logger.debug(AppConfigUtil.get(Constants.FILE_UPLOAD));
		logger.debug(filePath+"-----------------------------------------------------------------------------------------------------------------文件路径");
		downloadFile(filePath, "用户导入模板.xls", request, response);
	}
	/**
	 * 导出用户信息到Excel
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportUser")
	public void exportUser(HttpServletRequest request,HttpServletResponse response) throws Exception{		
			 HSSFWorkbook wb = new HSSFWorkbook();
	         HSSFSheet sheet = wb.createSheet("sheet1");
	         //设置表头样式   
	         HSSFCellStyle style = wb.createCellStyle();
	         HSSFFont font = wb.createFont();
	         font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	         style.setFillForegroundColor(HSSFColor.RED.index);
	         style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	         style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	         style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	         style.setBorderRight(HSSFCellStyle.BORDER_THIN);
	         style.setBorderTop(HSSFCellStyle.BORDER_THIN);
	         style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	         style.setFont(font);
	         //添加第一行表头
	         HSSFRow r0 = sheet.createRow(0);
	         HSSFCell r0c0 = r0.createCell(0);
	         r0c0.setCellStyle(style);
	         r0c0.setCellValue("工号");
	         HSSFCell r0c1 = r0.createCell(1);
	         r0c1.setCellValue("姓名");
	         r0c1.setCellStyle(style);
	         HSSFCell r0c2 = r0.createCell(2);
	         r0c2.setCellValue("字号");
	         r0c2.setCellStyle(style);
	         HSSFCell r0c3 = r0.createCell(3);
	         r0c3.setCellValue("用户名");
	         r0c3.setCellStyle(style);
	         HSSFCell r0c4 = r0.createCell(4);
	         r0c4.setCellValue("部门名称");
	         r0c4.setCellStyle(style);
	         HSSFCell r0c5 = r0.createCell(5);
	         r0c5.setCellValue("部门名称路径");
	         r0c5.setCellStyle(style);
	         HSSFCell r0c6 = r0.createCell(6);
	         r0c6.setCellValue("职位名称");
	         r0c6.setCellStyle(style);
	         HSSFCell r0c7 = r0.createCell(7);
	         r0c7.setCellValue("是否负责人");
	         r0c7.setCellStyle(style);
	         HSSFCell r0c8 = r0.createCell(8);
	         r0c8.setCellValue("手机");
	         r0c8.setCellStyle(style);
	         HSSFCell r0c9 = r0.createCell(9);
	         r0c9.setCellValue("邮箱地址");
	         r0c9.setCellStyle(style);
	         HSSFCell r0c10 = r0.createCell(10);
	         r0c10.setCellValue("性别");
	         r0c10.setCellStyle(style);
	         HSSFCell r0c11 = r0.createCell(11);
	         r0c11.setCellValue("身份证号");
	         r0c11.setCellStyle(style);
	         HSSFCell r0c12 = r0.createCell(12);
	         r0c12.setCellValue("考勤编号");
	         r0c12.setCellStyle(style);
	         HSSFCell r0c13 = r0.createCell(13);
	         r0c13.setCellValue("入职日期");
	         r0c13.setCellStyle(style);
	         HSSFCell r0c14 = r0.createCell(14);
	         r0c14.setCellValue("工作日期");
	         r0c14.setCellStyle(style);
	         HSSFCell r0c15 = r0.createCell(15);
	         r0c15.setCellValue("上级主管");
	         r0c15.setCellStyle(style);
	         //循环将每个用户信息添加到Excel表
	        // List<SysUser> list =this.getUserList();
	        List<SysUser> userList=sysUserExtService.getAll();
	        for(int i=0;i<userList.size();i++){
	        	SysUser user = userList.get(i); 
	        	 //获取组织信息
	        	 SysOrg org = sysOrgService.getByUserId(user.getUserId());
	        	 UserPosition position = userPositionService.getChargeByUserId(user.getUserId());
	        	 Position pos = positionService.getPosNameByUserId(user.getUserId());
	        	
	        	 HSSFRow row = sheet.createRow(i+1);       	 
	        	 row.createCell(0).setCellValue(user.getStaffNo());  
	        	 row.createCell(1).setCellValue(user.getFullname());
	        	 row.createCell(2).setCellValue(user.getAliasName());
	        	 row.createCell(3).setCellValue(user.getAccount());      	
	        	 row.createCell(4).setCellValue(org.getOrgName());
	        	 row.createCell(5).setCellValue(org.getOrgPathname()==null?"":org.getOrgPathname());
	        	 row.createCell(6).setCellValue(pos.getPosName());
	        	 Short ischarge = position.getIsCharge();
	        	 if(ischarge==null){
	        		 row.createCell(7).setCellValue("");
	        	 }else{
	        		 String str = ischarge.toString();
	        		 row.createCell(7).setCellValue(str.equals("1")?"是":"否");
	        	 }
	        	
	        	 row.createCell(8).setCellValue(user.getPhone());   	
	        	 row.createCell(9).setCellValue(user.getEmail());
	        	 if(StringUtils.isEmpty(user.getSex())){
					 row.createCell(10).setCellValue("");
				 }else {
					 row.createCell(10).setCellValue(user.getSex().equals("1") ? "男" : "女");
				 }
	        	 row.createCell(11).setCellValue(user.getIdentification());
	        	 row.createCell(12).setCellValue(user.getAttendNo()==null?"":user.getAttendNo().toString());
	        	 SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd ");
	        	 Date entry = user.getEntryDate();
	        	 Date work = user.getWorkDate();
	        	 if(entry==null){
	        		 row.createCell(13).setCellValue("");
	        	 }else{
	        		 String entrystr = format.format(entry);
	        		 row.createCell(13).setCellValue(entrystr);
	        	 }       	
	        	 if(work==null){
	        		 row.createCell(14).setCellValue("");
	        	 }else{
	        		 String workstr =format.format(work);
	        		 row.createCell(14).setCellValue(workstr);
	        	 }     
	        	Long userId = user.getUserId();       	
	        	 if(userId==null){
	        		 row.createCell(15).setCellValue("");
	        	 }else{
	        		SysUser u = sysUserService.getSupUserByUserId(userId);
	        		 row.createCell(15).setCellValue(u.getStaffNo());
	        	 }
	        }
	        //设置自动列宽
	        sheet.autoSizeColumn((short)4);
	        sheet.autoSizeColumn((short)5);
	        sheet.autoSizeColumn((short)6);
	        sheet.autoSizeColumn((short)8);
	        sheet.autoSizeColumn((short)9);
	        sheet.autoSizeColumn((short)11);
	      //输出Excel文件  
	        OutputStream output=response.getOutputStream();  
	        response.reset();  
	        response.setHeader("Content-disposition", "attachment; filename=userInfo.xls");  
	        response.setContentType("application/msexcel");          
	        wb.write(output);  
	        output.close();  					
	}
	/**
	 * 手动同步用户信息到用户中心
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("syncUserToUc")
	public ResultVo syncUserToUc(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			String result = sysUserExtService.syncUserToUserCenter();
			if(Constants.UC_API_SUCCESS.equals(result)){
				return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "同步用户信息成功！");
			}else{
				logger.error("同步用户信息失败：" + result);
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "同步用户信息失败" + result);
			}
		} catch (Exception e) {
			logger.error("同步用户信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "同步用户信息失败：系统内部错误！");
		}
	}
	
	/**
	 * 获取用户信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("userList")
	@ResponseBody
	public ResultVo getUserList(HttpServletRequest request, HttpServletResponse response)throws Exception{
		try {
			List<SysUser> userList = sysUserExtService.getUserList();
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户信息成功！", userList);
		} catch (Exception e) {
			logger.error("获取用户信息失败！", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户信息失败！");
		}
	}
	/**
	 * 从权限中心获取设置权限页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getFromAuthorityCenter")
	@ResponseBody
	public ResultVo getFromAuthorityCenter(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		try {
		SysUser sysUser = sysUserService.getById(ContextUtil.getCurrentUserId());
		map.put("url", AppConfigUtil.get(Constants.AUTHORITY_CENTER_URL));
		map.put("preUrl",RequestUtil.getPrePage(request));
		map.put("name",sysUser.getAccount());
		map.put("password",sysUser.getPassword());

			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取成功",map);
		}catch (Exception e){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取失败",e.getMessage());
		}
	}
	/**
	 * 从用户中心获取用户信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getFromUserCenter")
	@Action(description="从用户中心获取用户信息", detail="根据手机号、邮箱、账号或者字号从用户中心查询用户信息", execOrder= ActionExecOrder.AFTER,
		exectype="查询日志", ownermodel= SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo getUserFromUserCenter(HttpServletRequest request, HttpServletResponse response)throws Exception{
		// 获取邮箱和手机号
		String mobile = RequestUtil.getString(request, "mobile");
		String email = RequestUtil.getString(request, "email");
		String account = RequestUtil.getString(request, "account");
		String aliasName = RequestUtil.getString(request, "aliasName");
		if(StringUtils.isBlank(mobile) && StringUtils.isBlank(email) && StringUtils.isBlank(account) 
				&& StringUtils.isBlank(aliasName)){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请输入查询条件！");
		}
		
		try {
			// 查询用户信息
			SysUser sysUser = sysUserExtService.getUserFromUserCenter(mobile, email, account, aliasName);
			if(sysUser == null){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "未查询到用户信息！");
			}
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户信息成功！", sysUser);
		} catch (Exception e) {
			logger.error("获取用户信息失败！", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户信息失败！");
		}
	}
	
	/**
	 * 更新用户类型
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("syncToUserCenter")
	@ResponseBody
	public ResultVo syncToUserCenter(HttpServletRequest request, HttpServletResponse response) throws Exception{
		// 查询需要同步的用户信息
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("syncToUc", SysUser.SYNC_UC_NO);
		List<SysUser> sysUserList = sysUserExtService.getAll(new QueryFilter(jsonObj));
		
		// 同步失败用户信息
		StringBuilder failedMessage = new StringBuilder();
		for(SysUser user : sysUserList){
			if(user.getUcUserid() == null){
				try {
					// 添加企业B用户
					sysUserExtService.addToUserCenter(user);
				} catch (Exception e) {
					// 记录同步失败日志
					userSynclogService.save(user.getUserId(), UserSynclog.OPTYPE_ADD_USER, e.getMessage(), user.toString());
					logger.error(e.getMessage(), e);
					failedMessage.append(user.getFullname()).append("\t").append(user.getAccount()).append("\n");
				}
			}else{
				try {
					// 更新用户在用户中心的类型（C用户更新为B用户）
					sysUserExtService.updateToUserCenter(user, enterpriseinfoService.getCodeMapByUserId(user.getUserId()));
				} catch (Exception e) {
					// 记录同步失败日志
					userSynclogService.save(user.getUserId(), UserSynclog.OPTYPE_UPD_USER_ORG, e.getMessage(), user.toString());
					logger.error(e.getMessage(), e);
					failedMessage.append(user.getFullname()).append("\t").append(user.getAccount()).append("\n");
				}
				try {
					// 更新用户信息
					sysUserExtService.updateUserToUserCenter(user, false);
				} catch (Exception e) {
					// 记录同步失败日志
					userSynclogService.save(user.getUserId(), UserSynclog.OPTYPE_UPD_USER, e.getMessage(), user.toString());
					logger.error(e.getMessage(), e);
					failedMessage.append(user.getFullname()).append("\t").append(user.getAccount()).append("\n");
				}
			}
		}
		if(failedMessage.length() == 0){
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "同步用户信息到用户中心成功！");
		} else {
			failedMessage.insert(0, "如下用户同步失败：\n姓名\t账号\n");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, failedMessage.toString());
		}
	}
	
	/**
	 * 获取地区列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getRegionList")
	@Action(description="获取地区列表")
	public ModelAndView getRegionList(HttpServletRequest request, HttpServletResponse response){
		List<Dictionary> dicList = dictionaryService.getByNodeKey(Constants.DIC_NODEKEY_DQ);
		ModelAndView view = new ModelAndView();
		view.setViewName("/base/system/setRegion");
		view.addObject("dicList", dicList);
		return view;
	}
	
	/**
	 * 为用户设置地区
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("setRegion")
	@Action(description="为用户设置地区",detail="为用户<#if userIds??><#list StringUtils.split(userIds,\",\") as item>"
			+ "【${SysAuditLinkService.getSysUserLink(Long.valueOf(item))}】</#list></#if>设置地区<#if region??>【${region}】</#if>")
	@ResponseBody
	public ResultVo setRegion(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			String userIds = RequestUtil.getString(request, "userIds");
			String region = RequestUtil.getString(request, "region");
			sysUserExtService.setRegion(userIds, region);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "设置地区成功！");
		} catch (Exception e) {
			logger.error("设置地区失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "设置地区失败：系统内部错误！");
		}
	}

	/**
	 * 修改用户状态为删除（用户数据做逻辑删除）
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("delOrRevert")
	@Action(description="删除/恢复用户",execOrder=ActionExecOrder.BEFORE,detail="<#if status == 1>恢复<#else>删除</#if>用户表<#if userId??>"
			+ "<#list StringUtils.split(userId,\",\") as item><#assign entity=sysUserService.getById(Long.valueOf(item))/>"
			+ "<#if entity!=''>【${entity.fullname}】</#if></#list></#if>")
	@ResponseBody
	public ResultVo delOrRevert(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Long[] userIds = RequestUtil.getLongAryByStr(request, "userId");
		Short status = RequestUtil.getShort(request, "status", (short)-1);
		String message = "删除";
		if(SysUser.STATUS_OK.equals(status)){
			message = "恢复";
		}
		if(userIds == null){
			logger.error(message + "用户失败:请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "用户失败：请求参数错误");
		}
		try {
			String tempMessage = "";
			StringBuffer failedMessage = new StringBuffer(); 
			if(SysUser.STATUS_OK.equals(status)){
				for(Long userId : userIds){
					tempMessage = sysUserExtService.revertUser(userId);
					if(StringUtils.isNotBlank(tempMessage)){
						failedMessage.append(tempMessage).append("\n");
					}
				}
			} else {
				for(Long userId : userIds){
					tempMessage = sysUserExtService.deleteUser(userId);
					if(StringUtils.isNotBlank(tempMessage)){
						failedMessage.append(tempMessage).append("\n");
					}
				}
			}
			if(StringUtils.isBlank(failedMessage.toString())){
				return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, message + "用户成功！");
			} else {
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "以下用户" + message + "失败：\n姓名\t失败原因\n" + failedMessage.toString());
			}
		} catch (Exception e) {
			logger.error(message + "用户失败:" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message + "用户失败：系统内部错误！");
		} 
	}
	
	/**
	 * 获取删除用户列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("delList")
	@Action(description="获取删除用户列表")
	public ModelAndView getDelList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		QueryFilter queryFilter = new QueryFilter(request, "sysUserItem");
		queryFilter.addFilter("status", SysUser.STATUS_Del);
		
		List<SysUser> sysUserList = sysUserService.getAll(queryFilter);
		
		ModelAndView view = new ModelAndView();
		view.setViewName("/base/system/sysUserDelList");
		view.addObject("sysUserList", sysUserList);
		return view;
	}
	
	/**
	 * 根据组织ID获取用户信息列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getUserByOrgId")
	@ResponseBody
	@Action(description="获取用户信息列表接口", detail="根据组织ID获取用户信息列表")
	public ResultVo getUserByOrgId(HttpServletRequest request, HttpServletResponse response)throws Exception{
		Long orgId = RequestUtil.getLong(request, "orgId");
		String enterpriseCodes = RequestUtil.getString(request, "enterpriseCodes");
		if(orgId == 0 || StringUtils.isBlank(enterpriseCodes)){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数不能为空");
		}
		try {
			List<SysUser> userList = sysUserExtService.getByOrgId(orgId, enterpriseCodes);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户信息列表成功",userList);
		} catch (Exception e) {
			logger.error("获取用户信息列表失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户信息列表失败：" + e.getMessage());
		}
	}

	/**
	 * 根据用户ID获取用户信息列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getToUIdByOrg")
	@ResponseBody
	@Action(description="获取用户信息列表接口", detail="根据组织ID获取用户信息列表")
	public ResultVo getToUIdByOrg(HttpServletRequest request, HttpServletResponse response)throws Exception{
		Long userId = RequestUtil.getLong(request, "userId");
		//String enterpriseCodes = RequestUtil.getString(request, "enterpriseCodes");
		if(userId == 0 ){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请求参数不能为空");
		}

		try {
			Map map =new HashMap();
			List<Object> list = new ArrayList<>();
			SysUser byUserId = sysUserService.getByUserId(userId);
			String headImgPath = FileUtil.getDownloadUrl(byUserId.getHeadImgPath(), false);
			list.add(headImgPath);
			List toUIdByOrg = sysUserOrgService.getPrimaryOrgByUserId(userId);
			map.put("headImgPath",list);
			map.put("toUIdByOrg",toUIdByOrg);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取组织信息列表成功",map);
		} catch (Exception e) {
			logger.error("获取用户信息列表失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息列表失败：" + e.getMessage());
		}
	}
}

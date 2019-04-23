package com.suneee.platform.controller.system;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:用户web签章 控制器类
 * 开发公司:深圳象羿软件技术
 * 开发人员:子华
 * 创建时间:2017-11-23
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/webSign/")
@Action(ownermodel=SysAuditModelType.PROCESS_AUXILIARY)
public class UserWebSignController extends BaseController
{
	@Resource
	private SysUserService userService;
	@Resource
	private SysOrgService orgService;
	//文件存储基本路径
	@Value("#{configProperties['common.file.basePath']}")
	private String basePath;
	//签章路径，绝对路径=basePath+contextPath
	@Value("#{configProperties['user.webSign.context']}")
	private String contextPath;
	//文件访问URL地址
	@Value("#{configProperties['user.webSign.url']}")
	private String staticUrl;

	private String getStaticUrl() {
		if (staticUrl==null){
			return "";
		}
		return staticUrl;
	}

	/**
	 * 获取拥有web签名用户列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看用户签章图片分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{
		QueryFilter queryFilter = new QueryFilter(request, "userItem");
		queryFilter.addFilter("hasWebSign",true);
		List<SysUser> list=userService.getUsersByQuery(queryFilter);
		ModelAndView mv=this.getAutoView().addObject("userList",list).addObject("staticUrl",getStaticUrl());
		return mv;
	}

	/**
	 * 保存签章图片
	 * @param request
	 * @param userId
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="保存用户签章图片")
	public ModelAndView save(HttpServletRequest request, @RequestParam Long userId, @RequestParam MultipartFile file) throws Exception
	{
		ModelAndView mv=this.getAutoView();
		String originName=file.getOriginalFilename();
		originName= StringUtils.specialCharFilter(originName);
		String suffix=originName.substring(originName.lastIndexOf("."));
		if (suffix!=null){
			suffix=suffix.toLowerCase();
		}
		if (!(".png".equals(suffix)||".jpg".equals(suffix)||".bpm".equals(suffix)||".jpeg".equals(suffix)||".gif".equals(suffix))){
			mv.addObject("hasError",true);
			mv.addObject("msg","请上传有效的图片格式文件！");
			return mv;
		}
		String dir=new SimpleDateFormat("yyyyMMdd").format(new Date());
		String destPath=contextPath+"/"+dir+"/"+System.currentTimeMillis()+suffix;
		File destFile=new File(basePath+destPath);
		if (!destFile.getParentFile().exists()){
			destFile.getParentFile().mkdirs();
		}
		file.transferTo(destFile);
		SysUser sysUser=userService.getByUserId(userId);
		if (sysUser.getWebSignUrl()==null){
			mv.addObject("msg","签章添加成功!");
		}else {
			mv.addObject("msg","签章更新成功!");
		}
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("userIds",new Long[]{userId});
		params.put("webSignUrl",destPath);
		userService.updateWebSign(params);
		mv.addObject("ctx",request.getServletContext().getContextPath());
		return mv;
	}

	/**
	 * 配置用户签章
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="配置用户签章")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long userId= RequestUtil.getLong(request,"id");
		ModelAndView mv =getAutoView();
		mv.addObject("user",null);
		if (userId!=null&&userId>0){
			SysUser user=userService.getByUserId(userId);
			SysOrg sysOrg=orgService.getByUserId(userId);
			user.setOrgName(sysOrg.getOrgName());
			return mv.addObject("user",user).addObject("staticUrl",getStaticUrl());
		}
		return mv;
	}

	/**
	 * 删除签章图片
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除签章图片")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
		if (lAryId==null||lAryId.length==0){
			response.sendRedirect(preUrl);
		}
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("userIds",lAryId);
		params.put("webSignUrl",null);
		userService.updateWebSign(params);
		response.sendRedirect(preUrl);
	}
}

package com.suneee.kaoqin.controller.kaoqin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.dao.system.SysUserDao;

import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.suneee.platform.model.system.Demension;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.DemensionService;
import com.suneee.platform.service.system.SysRoleService;
import com.suneee.platform.service.system.SysUserService;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import com.suneee.core.util.StringUtil;

import com.suneee.kaoqin.model.kaoqin.ExemmptSetting;
import com.suneee.kaoqin.service.kaoqin.ExemmptSettingService;
import com.suneee.core.web.ResultMessage;
/**
 *<pre>
 * 对象功能:免签设置 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:06:05
 *</pre>
 */
@Controller
@RequestMapping("/hotent/kaoqin/exemmptSetting/")
public class ExemmptSettingController extends BaseController
{
	@Resource
	private ExemmptSettingService exemmptSettingService;
	@Resource
	private DemensionService demensionService;
	@Resource
	private SysUserService userService;
	@Resource
	private SysUserDao userDao;
	@Resource
	private SysRoleService roleService;
	
	/**
	 * 添加或更新免签设置。
	 * @param request
	 * @param response
	 * @param exemmptSetting 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新免签设置")
	public void save(HttpServletRequest request, HttpServletResponse response,ExemmptSetting exemmptSetting) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			exemmptSettingService.save(exemmptSetting);
			if(id==0){
				resultMsg=getText("添加成功","免签设置");
			}else{
				resultMsg=getText("更新成功","免签设置");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得免签设置分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看免签设置分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<ExemmptSetting> list=exemmptSettingService.getAll(new QueryFilter(request,"exemmptSettingItem"));
		ModelAndView mv=this.getAutoView().addObject("exemmptSettingList",list);
		return mv;
	}
	
	/**
	 * 删除免签设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除免签设置")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			exemmptSettingService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除免签设置成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑免签设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑免签设置")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		ModelAndView mv = this.getAutoView();
		Long id=RequestUtil.getLong(request,"id",0L);
		mv.addObject("action", "global");
		List<Demension> demensionList = demensionService.getAll();
		String returnUrl=RequestUtil.getPrePage(request);
		ExemmptSetting exemmptSetting=exemmptSettingService.getById(id);
		
		return mv.addObject("exemmptSetting",exemmptSetting)
							.addObject("returnUrl",returnUrl).addObject("demensionList", demensionList);
	}

	/**
	 * 取得免签设置明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看免签设置明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		ExemmptSetting exemmptSetting = exemmptSettingService.getById(id);	
		return getAutoView().addObject("exemmptSetting", exemmptSetting);
	}
	
	@RequestMapping("getByOrgIds")
	@ResponseBody
	@Action(description = "根据组织选择人员")
	public List<SysUser> getByOrgIds(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String orgIdStr = request.getParameter( "orgId");
		String userIdIdStr = request.getParameter( "userId");
		String[] ids = orgIdStr.split(",");
		List<Long> idList = new ArrayList<Long>();
		for(String id :ids){
			if(BeanUtils.isNotEmpty(id)){
				idList.add(Long.parseLong(id));
			}
		}
		
		String[] userIds =  userIdIdStr.split(",");
		List<Long> userIdList = new ArrayList<Long>();
		for(String id :userIds){
			if(BeanUtils.isNotEmpty(id)){
				userIdList.add(Long.parseLong(id));
			}
		}
		List<SysUser> userList = new ArrayList<SysUser>();
		if(BeanUtils.isNotEmpty(idList)){
			userList = userDao.getByOrgIds(idList);
		}
		List<SysUser> rtnList = new ArrayList<SysUser>();
		//用户
		for(Long id :userIdList){
			SysUser user = userDao.getById(id);
			userList.add(user);
		}
		for(SysUser u : userList){
			SysUser s = setUserInfo(u);
			rtnList.add(s);
		}
		return rtnList;
	}
	
	private SysUser setUserInfo(SysUser u){
		JdbcTemplate jdbcTemplate=(JdbcTemplate ) AppUtil.getBean("jdbcTemplate");
		String sql1 = "select o.orgname from sys_org o INNER JOIN sys_user_pos p ON o.ORGID = p.orgid INNER JOIN sys_user u ON u.userid = p.userid";
		sql1 += " where u.userid =" +u.getUserId() + " and p.isprimary=1";
		List<Map<String,Object>> rtnMap1 = jdbcTemplate.queryForList(sql1);
		String orgName = "";
		for(Map p :rtnMap1){
			orgName = p.get("orgname").toString();//部门名称
		}
		u.setOrgName(orgName);
		String sql2 = "select r.ROLENAME from sys_role r INNER JOIN sys_user_role s ON r.ROLEID = s.ROLEID where s.USERID = "+u.getUserId();
		List<Map<String,Object>> rtnMap2 = jdbcTemplate.queryForList(sql2);
		String roleName = "";
		for(int i=0;i<rtnMap2.size();i++){
			Map<String,Object> map = rtnMap2.get(i);
			if(i<rtnMap2.size()-1){
				roleName += map.get("ROLENAME").toString()+",";//角色名称
			}else{
				roleName += map.get("ROLENAME").toString();
			}
		}
		u.setRoleNames(roleName);
		return u;
	}
	
	/**
	 * 添加免签设置。
	 * @param request
	 * @param response
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("saveSetting")
	@Action(description="添加或更新免签设置")
	public void saveSetting(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String resultMsg=null;
		Long[] ids = RequestUtil.getLongAry(request, "userId");		
		try{
			for(Long id : ids){
				ExemmptSetting e = exemmptSettingService.getByTargetId(id);
				if(BeanUtils.isEmpty(e)){
					e = new ExemmptSetting();
					e.setTargetId(id);
					e.setStatus((short)0);
					e.setTargetType((short)0);
					e.setCreateBy(ContextUtil.getCurrentUserId());
					exemmptSettingService.save(e);
				}else{
					exemmptSettingService.save(e);
				}
			}
			resultMsg=getText("操作成功","免签设置");
			writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
}


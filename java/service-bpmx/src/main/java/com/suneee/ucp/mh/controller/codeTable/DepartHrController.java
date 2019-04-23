package com.suneee.ucp.mh.controller.codeTable;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.mh.model.codeTable.CodeTable;
import com.suneee.ucp.mh.service.codeTable.CodeTableService;

/**
 *<pre>
 * 对象功能:部门HR设置 控制器类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2017-08-30 
 *</pre>
 */

@Controller
@RequestMapping("/mh/codeTable/departHr/")
public class DepartHrController extends UcpBaseController { 

	@Resource
	private CodeTableService codeTableService;
	@Resource
	private SysUserService userService;
	@Resource
	private GlobalTypeService globalService;
	
	/**
	 * 查看部门HR列表信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看部门HR设置列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		QueryFilter filter = new QueryFilter(request,"codeTableItem",true);
		filter.addFilter("settingType", 2);
		List<CodeTable> lists = codeTableService.getAll(filter);
		for (CodeTable list : lists) {
			Long userId =Long.parseLong(list.getItemId());
			//获取部门人员ids字符串
			String value = list.getItemValue();			
			SysUser user = userService.getByUserId(Long.parseLong(value));					
			list.setItemValue(user.getFullname());
			list.setUserAccount(user.getAliasName());
			SysUser userValue = userService.getByUserId(userId);			
			list.setUserName(userValue.getFullname());		
		}
		ModelAndView mv = this.getAutoView()
				.addObject("codeTableList",lists);
		return mv;
	}
	/**
	 * 编辑部门HR设置信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑部门HR设置")
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String returnUrl = RequestUtil.getPrePage(request);
		Long settingId= RequestUtil.getLong(request, "settingId", 0L);
		CodeTable codeTable = codeTableService.getById(settingId);
		//根据itemId获取用户姓名
		if(codeTable!=null){
			SysUser user = userService.getByUserId(Long.parseLong(codeTable.getItemId()));
			codeTable.setUserName(user.getFullname());
			SysUser user2 = userService.getByUserId(Long.parseLong(codeTable.getItemValue()));
			codeTable.setUserAccount(user2.getFullname());
		}
		return getAutoView().addObject("codeTable", codeTable)				
				.addObject("returnUrl", returnUrl);
	}
	/**
	 * 删除部门HR信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除部门HR")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try{
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "settingId");
			codeTableService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除部门HR设置成功!");
		}catch(Exception ex){
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	/**
	 * 添加或更新部门HR设置信息
	 * @param request
	 * @param response
	 * @param codeTable
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="保存部门HR设置")
	public void save(HttpServletRequest request,HttpServletResponse response,CodeTable codeTable) throws Exception{
		String resultMsg = null;
		Long settingId = RequestUtil.getLong(request, "settingId");	
		try {
			String Values = codeTable.getItemValue();
			//如果是新增多个部门人员
			if(Values.contains(",")){
				String itemValues[] = Values.split(",");
				for (String itemValue : itemValues) {
					//判断数据是否存在
					boolean rtn =codeTableService.isExist(codeTable.getItemId(),itemValue);
					if(rtn){
					    String userName = userService.getByUserId(Long.parseLong(itemValue)).getFullname();
						resultMsg=userName+"已经设置在该部门中了，请不要重复设置";
						writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Fail);
						return;
					}
					codeTable.setSettingId(null);
					codeTable.setItemValue(itemValue);
					codeTableService.save(codeTable);
				}
			}else{
				if(codeTable.getSettingId()==null){
					boolean rtn =codeTableService.isExist(codeTable.getItemId(),codeTable.getItemValue());
					if(rtn){
					    String userName = userService.getByUserId(Long.parseLong(codeTable.getItemValue())).getFullname();
						resultMsg=userName+"已经设置在该部门中了，请不要重复设置";
						writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Fail);
						return;
					      }
			        }
			codeTableService.save(codeTable);
			}
				if(0==settingId){
					resultMsg="添加部门HR成功";
				}else{
					resultMsg="更新部门人员成功";
				}
		    writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}
	@RequestMapping("getEdit")
	public ModelAndView getEdit(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String returnUrl = RequestUtil.getPrePage(request);
		Long settingId= RequestUtil.getLong(request, "settingId", 0L);
		CodeTable codeTable = codeTableService.getById(settingId);
		String itemId = codeTable.getItemId();
		codeTable.setItemValue(itemId);
		SysUser user = userService.getByUserId(Long.parseLong(codeTable.getItemId()));
		codeTable.setUserName(user.getFullname());
		String userAcount ="";
		List<CodeTable> lists = codeTableService.getByItemId(itemId,codeTable.getSettingType());
		for (CodeTable list : lists) {			
			SysUser userValue = userService.getByUserId(Long.parseLong(list.getItemValue()));			
			userAcount+=userValue.getFullname()+",";
		}
		codeTable.setUserAccount(userAcount);
		ModelAndView mv = new ModelAndView("/mh/codeTable/departHrEdit2.jsp");
		mv.addObject("codeTable", codeTable)				
		.addObject("returnUrl", returnUrl);
		return mv;
	}
	@RequestMapping("saveHr")
	public void saveHr(HttpServletRequest request,HttpServletResponse response,CodeTable codeTable) throws Exception{
		String resultMsg = null;
		List<CodeTable> lists = codeTableService.getByItemId(codeTable.getItemValue(),codeTable.getSettingType());
		try {
			for (CodeTable list : lists) {
				list.setItemId(codeTable.getItemId());
				list.setDescription(codeTable.getDescription());
				codeTableService.save(list);
			}
			resultMsg="更新部门HR成功";
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			// TODO: handle exception
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}	
	}
}

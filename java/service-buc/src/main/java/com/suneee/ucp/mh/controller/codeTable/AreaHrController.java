package com.suneee.ucp.mh.controller.codeTable;

import java.io.IOException;
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
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.conference.ConferenceRoom;
import com.suneee.ucp.mh.model.codeTable.CodeTable;
import com.suneee.ucp.mh.service.codeTable.CodeTableService;
/**
 *<pre>
 * 对象功能:区域HR设置 控制器类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2017-08-30 
 *</pre>
 */
@Controller
@RequestMapping("/mh/codeTable/areaHr/")
public class AreaHrController extends UcpBaseController{

	@Resource
	private CodeTableService codeTableService;
	@Resource
	private SysUserService userService;
	@Resource
	private GlobalTypeService globalService;
	@Resource
	private DictionaryService dictionaryService;
	
	/**
	 * 查看区域HR设置列表信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看区域HR设置列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		QueryFilter filter = new QueryFilter(request,"codeTableItem",true);
		filter.addFilter("settingType", 1);
		List<CodeTable> lists = codeTableService.getAll(filter);
		for (CodeTable list : lists) {
			Long userId =Long.parseLong(list.getItemId());
			String nodekey = list.getItemValue();
			String typename = dictionaryService.getByItemValue(nodekey).get(0).getItemName();
			SysUser user = userService.getByUserId(userId);
			list.setUserAccount(user.getAliasName());
			list.setUserName(user.getFullname());
			list.setItemValue(typename);
		}
		ModelAndView mv = this.getAutoView()
				.addObject("codeTableList",lists);
		return mv;
	}
	/**
	 * 编辑区域HR信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑区域HR")
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String returnUrl = RequestUtil.getPrePage(request);
		Long settingId= RequestUtil.getLong(request, "settingId", 0L);
		CodeTable codeTable = codeTableService.getById(settingId);
		//根据itemId获取用户姓名
		if(codeTable!=null){
			SysUser user = userService.getByUserId(Long.parseLong(codeTable.getItemId()));
			codeTable.setUserName(user.getFullname());
		}
		// 从数据字典中获取地区列表
		List<Dictionary> regionList = dictionaryService.getByNodeKey(ConferenceRoom.REGION_NODE_KEY);
		return getAutoView().addObject("codeTable", codeTable)
				.addObject("regionList", regionList)
				.addObject("returnUrl", returnUrl);
	}
	/**
	 * 删除区域HR信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除区域HR")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try{
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "settingId");
			codeTableService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除区域HR成功!");
		}catch(Exception ex){
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	/**
	 * 添加或更新区域HR信息
	 * @param request
	 * @param response
	 * @param codeTable
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="保存区域HR")
	public void save(HttpServletRequest request,HttpServletResponse response,CodeTable codeTable) throws Exception{
		String resultMsg = null;
		Long settingId = RequestUtil.getLong(request, "settingId");			
		try {
			codeTableService.save(codeTable);
				if(0==settingId){
					resultMsg="添加区域HR成功";
				}else{
					resultMsg="更新区域HR成功";
				}
		    writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}
	
	
}

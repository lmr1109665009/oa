package com.suneee.ucp.mh.controller.codeTable;

import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.conference.ConferenceRoom;
import com.suneee.ucp.mh.model.codeTable.CodeTable;
import com.suneee.ucp.mh.service.codeTable.CodeTableService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *<pre>
 * 对象功能:区域HR设置 控制器类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2017-08-30 
 *</pre>
 */
@Controller
@RequestMapping("/mh/codeTable/typeManege/")
public class TypeManegeController extends UcpBaseController{

	@Resource
	private CodeTableService codeTableService;
	@Resource
	private SysUserService userService;
	@Resource
	private GlobalTypeService globalService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private SysUserService sysUserService;
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

		if(null!=filter.getFilters().get("fullname")||null!=filter.getFilters().get("aliasname")){
			if(sysUserService.getUsersByQuery(filter).size()>0){
			String uId = sysUserService.getUsersByQuery(filter).get(0).getUserId().toString();
			filter.addFilter("itemId", uId);
		}else{
			filter.addFilter("itemId", -1);
		}
		}
		filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		List<CodeTable> lists = codeTableService.getTypeList(filter);
		for (CodeTable list : lists) {
			Long userId =Long.parseLong(list.getItemId());
			List<Dictionary> types = dictionaryService.getByItemValue(list.getSettingType());
			String nodekey = list.getItemValue();
			String typename = dictionaryService.getByItemValue(nodekey).get(0).getItemName();
			SysUser user = userService.getByUserId(userId);
			list.setUserAccount(user.getAliasName());
			list.setUserName(user.getFullname());
			list.setItemValue(typename);
			list.setSettingType(types.get(0).getItemName());
		}
		// 从数据字典中获取地区列表
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceRoom.REGION_NODE_KEY,ContextSupportUtil.getCurrentEnterpriseCode());
		// 从数据字典中获取流程审批类型
		List<Dictionary> typeList = dictionaryService.getByNodeKeyAndEid("qyzwxg",ContextSupportUtil.getCurrentEnterpriseCode());
		ModelAndView mv = this.getAutoView()
				.addObject("regionList", regionList)
				.addObject("typeList", typeList)
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
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		Long settingId= RequestUtil.getLong(request, "settingId", 0L);
		CodeTable codeTable = codeTableService.getById(settingId);
		//根据itemId获取用户姓名
		if(codeTable!=null){
			SysUser user = userService.getByUserId(Long.parseLong(codeTable.getItemId()));
			codeTable.setUserName(user.getFullname());
		}
		// 从数据字典中获取地区列表
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceRoom.REGION_NODE_KEY,enterpriseCode);
		// 从数据字典中获取流程审批类型
		List<Dictionary> typeList = dictionaryService.getByNodeKeyAndEid("qyzwxg",enterpriseCode);
		
		return getAutoView().addObject("codeTable", codeTable)
				.addObject("regionList", regionList)
				.addObject("typeList", typeList)
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
			logger.error("删除失败", ex);
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
			String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
			if(StringUtil.isEmpty(enterpriseCode)){
				writeResultMessage(response.getWriter(), "您没设组织，你能执行此操作", ResultMessage.Fail);
			}
			codeTable.setEnterpriseCode(enterpriseCode);
			codeTableService.save(codeTable);
				if(0==settingId){
					resultMsg="添加类型人员成功";
				}else{
					resultMsg="更新类型人员成功";
				}
		    writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			logger.error("save操作失败", e);
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}
	
	
}

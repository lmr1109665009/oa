package com.suneee.kaoqin.controller.kaoqin;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.kaoqin.model.kaoqin.ShiftUserSetting;
import com.suneee.kaoqin.service.kaoqin.ShiftUserSettingService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Demension;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.DemensionService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.mh.model.attendance.AttendanceShift;
import com.suneee.ucp.mh.service.attendance.AttendanceShiftService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
/**
 *<pre>
 * 对象功能:排班人员设置 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:10:16
 *</pre>
 */
@Controller
@RequestMapping("/hotent/kaoqin/shiftUserSetting/")
public class ShiftUserSettingController extends BaseController
{
	@Resource
	private ShiftUserSettingService shiftUserSettingService;
	@Resource
	private DemensionService demensionService;
	@Resource
	private SysUserService userService;
	@Resource(name="ucpAttendanceShiftService")
	private AttendanceShiftService shiftService;
	
	/**
	 * 添加或更新排班人员设置。
	 * @param request
	 * @param response
	 * @param shiftUserSetting 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新排班人员设置")
	public void save(HttpServletRequest request, HttpServletResponse response,ShiftUserSetting shiftUserSetting) throws Exception
	{
		String resultMsg=null;
		Long id = RequestUtil.getLong(request, "id");
		try{
			shiftUserSettingService.save(shiftUserSetting);
			if(id==0){
				resultMsg=getText("添加成功","排班人员设置");
			}else{
				resultMsg=getText("更新成功","排班人员设置");
			}
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			e.printStackTrace();
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得排班人员设置分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看排班人员设置分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(request,"attendanceItem");
		List<AttendanceShift> list=shiftService.getShiftUserList(filter);
		ModelAndView mv=this.getAutoView().addObject("attendanceShiftList",list);
		return mv;
	}
	
	/**
	 * 删除排班人员设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除排班人员设置")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			shiftUserSettingService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除排班人员设置成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑排班人员设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑排班人员设置")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		ModelAndView mv = this.getAutoView();
		Long id=RequestUtil.getLong(request,"shiftId",0L);
		mv.addObject("action", "global");
		List<Demension> demensionList = demensionService.getAll();
		String returnUrl=RequestUtil.getPrePage(request);
//		ShiftUserSetting shiftUserSetting=shiftUserSettingService.getById(id);
		AttendanceShift shift = shiftService.getById(id);

//		return mv.addObject("shiftUserSetting",shiftUserSetting)
		return mv.addObject("shift",shift)
							.addObject("returnUrl",returnUrl).addObject("demensionList", demensionList);
	}

	/**
	 * 取得排班人员设置明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看排班人员设置明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		ShiftUserSetting shiftUserSetting = shiftUserSettingService.getById(id);	
		return getAutoView().addObject("shiftUserSetting", shiftUserSetting);
	}
	
	@RequestMapping("getByOrgIds")
	@ResponseBody
	@Action(description = "根据组织选择人员")
	public List<SysUser> getByOrgIds(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String orgIdStr = request.getParameter( "orgId");
		String[] ids = orgIdStr.split(",");
		List<Long> idList = new ArrayList<Long>();
		for(String id :ids){
			if(BeanUtils.isNotEmpty(id)){
				idList.add(Long.parseLong(id));
			}
		}
		String userIdIdStr = request.getParameter( "userId");
		String[] userIds =  userIdIdStr.split(",");
		List<Long> userIdList = new ArrayList<Long>();
		for(String id :userIds){
			if(BeanUtils.isNotEmpty(id)){
				userIdList.add(Long.parseLong(id));
			}
		}
		if (idList.size() > 0) {
			List<SysUser> orgUserList = userService.getByOrgIds(idList);
			for(SysUser u : orgUserList){
				userIdList.add(u.getUserId());
			}
		}
		return userService.getByUserIdsWithOrgName(userIdList);
	}
	
	/**
	 * 添加或更新排班人员设置。
	 * @param request
	 * @param response
	 * @param shiftUserSetting 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("saveShift")
	@Action(description="添加或更新排班人员设置")
	public void saveShift(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String resultMsg=null;
		Long[] userIds = RequestUtil.getLongAry(request, "userId");
		Long settingId = RequestUtil.getLong(request, "settingId");//班次id
		try{
			if (userIds != null) {
				for(Long id :userIds){
					List<ShiftUserSetting> settings = shiftUserSettingService.getByTargetId(id);
					ShiftUserSetting shift = null;
					if (settings.size() > 0) {
						shift = settings.get(0);
					}
					if(BeanUtils.isEmpty(shift)){
						shift = new ShiftUserSetting();
						shift.setCreateby(ContextUtil.getCurrentUserId());
						shift.setCreatetime(new Date());
						shift.setStatus((short)0);
						shift.setTargetId(id);
						// 目前暂只添加用户类型
						shift.setTargetType(ShiftUserSetting.TYPE_USER);
					}
					shift.setSettingId(settingId);
					shiftUserSettingService.save(shift);
				}
			}
			resultMsg=getText("操作成功","排班人员设置");
			writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	/**
	 * 取得排班人员设置分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getListBySettingId")
	@Action(description="查看相应班次的排班人员设置分页列表")
	public ModelAndView getListBySettingId(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		ModelAndView mv = new ModelAndView("/hotent/kaoqin/shiftUserSettingBySetId.jsp");
		Long shiftId = RequestUtil.getLong(request, "shiftId");
		QueryFilter filter = new QueryFilter(request,"shiftUserSettingItem");
		if (shiftId != 0) {
			filter.addFilter("settingId", shiftId);
		}
		List<ShiftUserSetting> list=shiftUserSettingService.getListBySettingId(filter);
		AttendanceShift shift = shiftService.getById(shiftId);
		mv.addObject("shiftUserSettingList",list)
		.addObject("shift", shift);
		addQueryFieldToView(mv, request);
		return mv;
	}
	
	protected void addQueryFieldToView(ModelAndView view, HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements()) {
			String name = names.nextElement();
			view.addObject(name, request.getParameter(name));
		}
	}
}


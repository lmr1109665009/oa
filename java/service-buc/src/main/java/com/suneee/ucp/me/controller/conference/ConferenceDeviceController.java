package com.suneee.ucp.me.controller.conference;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.conference.ConferenceDevice;
import com.suneee.ucp.me.model.conference.ConferenceRoom;
import com.suneee.ucp.me.service.conference.ConferenceDeviceService;
import com.suneee.ucp.me.service.conference.ConferenceRoomService;

/**
 *<pre>
 * 对象功能:设备信息 控制器类
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-04-27 17:45:03
 *</pre>
 */
@Controller
@RequestMapping("/me/conference/conferenceDevice/")
public class ConferenceDeviceController extends UcpBaseController
{
	@Resource
	private ConferenceDeviceService conferenceDeviceService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private ConferenceRoomService conferenceRoomService;
	@Resource
	private SysOrgService orgService;
	/**
	 * 添加或更新设备信息。
	 * @param request
	 * @param response
	 * @param conferenceDevice 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新设备信息")
	public void save(HttpServletRequest request, HttpServletResponse response, ConferenceDevice conferenceDevice) throws Exception
	{
		String resultMsg = null;
		Long deviceId = RequestUtil.getLong(request, "deviceId");		
		try{
			conferenceDeviceService.save(conferenceDevice);
			if(0 == deviceId){
				resultMsg=getText("添加会议设备信息成功","设备信息");
			}else{
				resultMsg=getText("更新会议设备信息成功","设备信息");
			}
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}
	
	/**
	 * 取得设备信息分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看设备信息分页列")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception
	{	
		Long userId = ContextUtil.getCurrentUserId();
		SysOrg sysOrg = orgService.getByUserId(userId);
	    String eid = sysOrg.getOrgCode();	  
	    QueryFilter filter =new QueryFilter(request,"conferenceRoomItem");
	    filter.addFilter("eid", eid);
		// 查询设备列表
		List<ConferenceDevice> list = conferenceDeviceService.getAll(filter);
		// 查询地区集合
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceDevice.REGION_NODE_KEY,eid);
		// 查询设备类型集合
		List<Dictionary> deviceTypeList = dictionaryService.getByNodeKeyAndEid(ConferenceDevice.DEVICE_TYPE_NODE_KEY,eid);
		return this.getAutoView().addObject("conferenceDeviceList", list)
				.addObject("regionList", regionList)
				.addObject("deviceTypeList", deviceTypeList);
	    
	    }
	
	/**
	 * 删除设备信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除设备信息")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try{
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "deviceId");
			conferenceDeviceService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除设备信息表成功!");
		}catch(Exception ex){
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑设备信息表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑设备信息表")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long userId = ContextUtil.getCurrentUserId();
		SysOrg sysOrg = orgService.getByUserId(userId);
	    String eid = sysOrg.getOrgCode();
		Long deviceId = RequestUtil.getLong(request, "deviceId", 0L);
		String returnUrl = RequestUtil.getPrePage(request);
		// 查询设备信息
		ConferenceDevice conferenceDevice = conferenceDeviceService.getById(deviceId);
		// 查询地区集合
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceDevice.REGION_NODE_KEY,eid);
		// 查询设备类型集合
		List<Dictionary> deviceTypeList = dictionaryService.getByNodeKeyAndEid(ConferenceDevice.DEVICE_TYPE_NODE_KEY,eid);
		// 根据设备所在地区查找会议室
		Long region = null;
		if(null != conferenceDevice){
			region = conferenceDevice.getRegion();
		}
		List<ConferenceRoom> roomList = conferenceRoomService.getByRegion(region);
		return getAutoView().addObject("conferenceDevice", conferenceDevice)
							.addObject("returnUrl", returnUrl)
							.addObject("regionList", regionList)
							.addObject("deviceTypeList", deviceTypeList)
							.addObject("roomList", roomList);
	}

	/**
	 * 取得设备信息表明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看设备信息表明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long deviceId = RequestUtil.getLong(request,"deviceId");
		ConferenceDevice conferenceDevice = conferenceDeviceService.getById(deviceId);	
		return getAutoView().addObject("conferenceDevice", conferenceDevice);
	}
}


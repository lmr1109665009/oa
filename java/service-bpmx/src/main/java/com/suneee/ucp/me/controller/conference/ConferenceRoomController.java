package com.suneee.ucp.me.controller.conference;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.me.model.conference.ConferenceRoom;
import com.suneee.ucp.me.service.conference.ConferenceRoomService;

/**
 *<pre>
 * 对象功能:会议室信息 控制器类
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-04-27 15:24:26
 *</pre>
 */
@Controller
@RequestMapping("/me/conference/conferenceRoom/")
public class ConferenceRoomController extends UcpBaseController
{
	@Resource
	private ConferenceRoomService conferenceRoomService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private SysOrgService orgService;
	/**
	 * 添加或更新会议室信息。
	 * @param request
	 * @param response
	 * @param conferenceRoom 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新会议室信息")
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, ConferenceRoom conferenceRoom) throws Exception
	{
		String resultMsg = null;
		Long roomId = RequestUtil.getLong(request, "roomId");		
		try{
			conferenceRoomService.save(conferenceRoom);
			if(0 == roomId){
				resultMsg = getText("添加会议室信息成功");
			}else{
				resultMsg = getText("更新会议室信息成功", "会议室信息");
			}
			return  new ResultVo(ResultConst.COMMON_STATUS_SUCCESS,resultMsg);
		}catch(Exception e){
			return  new ResultVo(ResultConst.COMMON_STATUS_FAILED,resultMsg+ "," + e.getMessage());
		}
	}
	
	/**
	 * 取得会议室信息分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看会议室信息分页列")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception
	{	
		Long userId = ContextUtil.getCurrentUserId();
		SysOrg sysOrg = orgService.getByUserId(userId);
	    String eid = sysOrg.getOrgCode();
	    /*if(eid==null){
	    	return this.getAutoView();
	    }*/
	    QueryFilter filter =new QueryFilter(request,"conferenceRoomItem");
	    filter.addFilter("eid", eid);  
		// 获取会议室信息列表
		List<ConferenceRoom> list = conferenceRoomService.getAll(filter);
		// 获取地区列表
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceRoom.REGION_NODE_KEY,eid);
		// 获取地区内的会议室信息
		Long region = RequestUtil.getLong(request, "Q_region_L");
		List<ConferenceRoom> regionRoomList = conferenceRoomService.getByRegion(region);
		
		ModelAndView mv = this.getAutoView()
				.addObject("conferenceRoomList",list)
				.addObject("regionList", regionList)
				.addObject("regionRoomList", regionRoomList);
		return mv;
	}
	
	/**
	 * 删除会议室信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除会议室信息")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try{
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "roomId");
			conferenceRoomService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除会议室信息成功!");
		}catch(Exception ex){
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑会议室信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑会议室信息")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		
		Long userId = ContextUtil.getCurrentUserId();
		SysOrg sysOrg = orgService.getByUserId(userId);
	    String eid = sysOrg.getOrgCode();
		Long roomId = RequestUtil.getLong(request, "roomId", 0L);
		String returnUrl = RequestUtil.getPrePage(request);
		ConferenceRoom conferenceRoom = conferenceRoomService.getById(roomId);
		// 获取地区列表
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceRoom.REGION_NODE_KEY,eid);
		return getAutoView().addObject("conferenceRoom", conferenceRoom)
							.addObject("returnUrl", returnUrl)
							.addObject("regionList", regionList);
	}

	/**
	 * 取得会议室信息明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看会议室信息明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long roomId = RequestUtil.getLong(request,"roomId");
		ConferenceRoom conferenceRoom = conferenceRoomService.getById(roomId);	
		return getAutoView().addObject("conferenceRoom", conferenceRoom);
	}
	
	/**
	 * 根据地区获取会议室列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getRegionRoom")
	@Action(description = "根据地区获取会议室列表")
	@ResponseBody
	public ResultVo getRegionRoom(HttpServletRequest request, HttpServletResponse response){
		try {
			Long region = RequestUtil.getLong(request, "region", 0);
			List<ConferenceRoom> regionRoomList = conferenceRoomService.getByRegion(region);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取会议室名称列表成功", regionRoomList);
		} catch (Exception e) {
			logger.error("获取会议室名称列表失败", e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取会议室名称列表失败");
		}
	}
}
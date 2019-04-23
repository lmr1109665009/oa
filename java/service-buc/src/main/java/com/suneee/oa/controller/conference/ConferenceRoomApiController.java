package com.suneee.oa.controller.conference;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.me.model.conference.ConferenceDevice;
import com.suneee.ucp.me.model.conference.ConferenceRoom;
import com.suneee.ucp.me.service.conference.ConferenceDeviceService;
import com.suneee.ucp.me.service.conference.ConferenceRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:会议室信息 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-01-22 13:45:00
 *</pre>
 */
@Controller
@RequestMapping("/api/conference/conferenceRoom/")
public class ConferenceRoomApiController extends UcpBaseController
{
	@Resource
	private ConferenceRoomService conferenceRoomService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private SysOrgService orgService;
	@Resource
	private ConferenceDeviceService conferenceDeviceService;

	/**
	 * 取得会议室信息分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description = "查看会议室信息分页列")
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception
	{	
		//获取用户当前企业编码
		String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
		QueryFilter filter =new QueryFilter(request,true);
	    filter.addFilter("enterpriseCode", enterpriseCode);
		// 获取会议室信息列表
		PageList<ConferenceRoom> list = (PageList<ConferenceRoom>) conferenceRoomService.getAll(filter);
		// 获取地区列表
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceRoom.REGION_NODE_KEY,enterpriseCode);
		// 获取地区内的会议室信息
		Long region = RequestUtil.getLong(request, "Q_region_L");
		List<ConferenceRoom> regionRoomList = conferenceRoomService.getByRegion(region);

		Map<String,Object> map = new HashMap<>();
		map.put("conferenceRoomList", PageUtil.getPageVo(list));
		map.put("regionList",regionList);
		map.put("regionRoomList",regionRoomList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取会议室信息分页列表数据成功！",map);
	}
	
	/**
	 * 删除会议室信息
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description="删除会议室信息")
	public ResultVo del(HttpServletRequest request) throws Exception
	{
		ResultVo message = null;
		try{
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "roomId");
			for(Long roomId:lAryId){
				List<ConferenceDevice> deviceList = conferenceDeviceService.getConferenceDevicesByRoomId(roomId);
				if(deviceList.size() > 0){
					message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请先删除该会议室关联设备！");
					return message;
				}
			}
			conferenceRoomService.delByIds(lAryId);
			message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除会议室信息成功!");
		}catch(Exception ex){
			logger.error(ex.getMessage(),ex);
			message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败" + ex.getMessage());
		}
		return message;
	}
	
	/**
	 * 	编辑会议室信息
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description = "编辑会议室信息")
	public ResultVo edit(HttpServletRequest request) throws Exception
	{
		
		Long userId = ContextUtil.getCurrentUserId();
		SysOrg sysOrg = orgService.getByUserId(userId);
	    String eid = sysOrg.getOrgCode();
		Long roomId = RequestUtil.getLong(request, "roomId", 0L);
		ConferenceRoom conferenceRoom = conferenceRoomService.getById(roomId);
		// 获取地区列表
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceRoom.REGION_NODE_KEY,eid);
		Map<String,Object> map = new HashMap<>();
		map.put("conferenceRoom",conferenceRoom);
		map.put("regionList",regionList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取会议室信息成功！",map);
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
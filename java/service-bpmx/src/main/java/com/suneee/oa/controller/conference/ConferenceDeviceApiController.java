package com.suneee.oa.controller.conference;

import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Dictionary;
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
 * 对象功能:设备信息 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-01-222 16:18:00
 *</pre>
 */
@Controller
@RequestMapping("/api/conference/conferenceDevice/")
public class ConferenceDeviceApiController extends UcpBaseController
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
	 * 取得设备信息分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description = "查看设备信息分页列")
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//获取用户当前企业编码
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
	    QueryFilter filter =new QueryFilter(request,true);
	    filter.addFilter("enterpriseCode", enterpriseCode);
		// 查询设备列表
		PageList<ConferenceDevice> list = (PageList<ConferenceDevice>) conferenceDeviceService.getAll(filter);
		// 查询地区集合
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceDevice.REGION_NODE_KEY,enterpriseCode);
		// 查询设备类型集合
		List<Dictionary> deviceTypeList = dictionaryService.getByNodeKeyAndEid(ConferenceDevice.DEVICE_TYPE_NODE_KEY,enterpriseCode);

		Map<String,Object> map = new HashMap<>();
		map.put("conferenceDeviceList", PageUtil.getPageVo(list));
		map.put("regionList",regionList);
		map.put("deviceTypeList",deviceTypeList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取会议设备分页列表数据成功！",map);
	    
	    }
	
	/**
	 * 删除设备信息
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description = "删除设备信息")
	public ResultVo del(HttpServletRequest request) throws Exception
	{
		ResultVo message = null;
		try{
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "deviceId");
			conferenceDeviceService.delByIds(lAryId);
			message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除设备信息表成功!");
		}catch(Exception ex){
			message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败" + ex.getMessage());
		}
		return message;
	}
	
	/**
	 * 	编辑设备信息表
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description = "编辑设备信息表")
	public ResultVo edit(HttpServletRequest request) throws Exception
	{
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		Long deviceId = RequestUtil.getLong(request, "deviceId", 0L);
		// 查询设备信息
		ConferenceDevice conferenceDevice = conferenceDeviceService.getById(deviceId);
		// 查询地区集合
		List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceDevice.REGION_NODE_KEY,enterpriseCode);
		// 查询设备类型集合
		List<Dictionary> deviceTypeList = dictionaryService.getByNodeKeyAndEid(ConferenceDevice.DEVICE_TYPE_NODE_KEY,enterpriseCode);
		// 根据设备所在地区查找会议室
		Long region = null;
		if(null != conferenceDevice){
			region = conferenceDevice.getRegion();
		}
		List<ConferenceRoom> roomList = conferenceRoomService.getByRegion(region);

		Map<String,Object> map = new HashMap<>();
		map.put("conferenceDevice", conferenceDevice);
		map.put("regionList",regionList);
		map.put("deviceTypeList",deviceTypeList);
		map.put("roomList",roomList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取会议设备数据成功！",map);
	}

	/**
	 * 取得设备信息表明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@ResponseBody
	@Action(description = "查看设备信息表明细")
	public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long deviceId = RequestUtil.getLong(request,"deviceId");
		ConferenceDevice conferenceDevice = conferenceDeviceService.getById(deviceId);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取设备信息成功！",conferenceDevice);
	}
}


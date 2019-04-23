package com.suneee.eas.oa.controller.conference;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.conference.ConferenceRoom;
import com.suneee.eas.oa.service.conference.ConferenceRoomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 会议室控制器类
 * @Author: kaize
 * @Date: 2018/7/31 17:54
 */
@RestController
@RequestMapping(ModuleConstant.CONFERENCE_MODULE+FunctionConstant.CONFERENCE_ROOM)
public class ConferenceRoomApiController {
	private static final Logger LOGGER =  LogManager.getLogger(ConferenceRoomApiController.class);

	@Autowired
	private ConferenceRoomService conferenceRoomService;


	/**
	 * 新增/更新会议室
	 * @param request
	 * @param conferenceRoom
	 * @return
	 */
	@RequestMapping("save")
	public ResponseMessage save(HttpServletRequest request, ConferenceRoom conferenceRoom){
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
		Long roomId = conferenceRoom.getRoomId();
		try {
			if (roomId == 0) {
				//判断会议室名称是否重复
				boolean repeat = conferenceRoomService.isRoomNameRepeatForAdd(conferenceRoom);
				if(repeat){
					return ResponseMessage.fail("会议室名称重复，请重新输入!");
				}
				//新增会议室
				conferenceRoomService.saveRoomAndAuthority(request,conferenceRoom);
				responseMessage.setMessage("会议室新增成功！");
			} else {
				//判断会议室名称是否重复
				boolean repeat = conferenceRoomService.isRoomNameRepeatForUpdate(conferenceRoom);
				if(repeat){
					return ResponseMessage.fail("会议室名称重复，请重新输入!");
				}
				//更新会议室
				conferenceRoomService.updateRoomAndAuthority(request,conferenceRoom);
				responseMessage.setMessage("会议室更新成功！");
			}
		}catch (Exception ex){
			responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
			if (roomId == 0) {
				responseMessage.setMessage("会议室新增失败！");
			} else {
				responseMessage.setMessage("会议室更新失败！");
			}
			LOGGER.error(responseMessage.getMessage()+":"+ex.getMessage());
		}
		return responseMessage;
	}

	/**
	 * 获取会议室列表数据
	 * @return
	 */
	@RequestMapping("listPage")
	public ResponseMessage listPage(HttpServletRequest request){
		QueryFilter queryFilter = new QueryFilter("listAll",request);
		try {
			queryFilter.getFilters().put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			Pager<ConferenceRoom> page = conferenceRoomService.getPageBySqlKey(queryFilter);
			return ResponseMessage.success("获取会议室列表数据成功！",page);
		}catch (Exception ex){
			LOGGER.error("获取会议室列表数据失败:"+ex.getMessage());
			return ResponseMessage.fail("获取会议室列表数据失败！");
		}
	}

	/**
	 * 批量删除会议室
	 * @return
	 */
	@RequestMapping("delByIds")
	public ResponseMessage delByIds(HttpServletRequest request){
		String roomIds = RequestUtil.getString(request,"roomIds");
		if(BeanUtils.isEmpty(roomIds)){
			return ResponseMessage.fail("删除失败，参数不能为空！");
		}
		try {
			conferenceRoomService.deleteByIds(roomIds);
			return ResponseMessage.success("删除会议室成功!");
		}catch (Exception ex){
			ex.printStackTrace();
			LOGGER.error("删除会议室失败:"+ex.getMessage());
			return ResponseMessage.fail("删除会议室失败！");
		}
	}

	/**
	 * 获取会议室详情
	 * @return
	 */
	@RequestMapping("findById")
	public ResponseMessage findById(HttpServletRequest request){
		Long roomId = RequestUtil.getLong(request,"roomId");
		if(roomId == 0){
			return ResponseMessage.fail("获取失败，参数不能为空！");
		}
		try {
			ConferenceRoom conferenceRoom = conferenceRoomService.findById(roomId);
			return ResponseMessage.success("获取会议室详情成功!",conferenceRoom);
		}catch (Exception ex){
			LOGGER.error("获取会议室详情失败:"+ex.getMessage());
			return ResponseMessage.fail("获取会议室详情失败！");
		}
	}

	/**
	 * 根据地区获取会议室
	 * @param request
	 * @return
	 */
	@RequestMapping("getRoomByRegion")
	public ResponseMessage getRoomByRegion(HttpServletRequest request){
		Long region = RequestUtil.getLong(request, "region");
		if(BeanUtils.isEmpty(region)){
			return ResponseMessage.fail("获取失败，参数不能为空！");
		}
		try {
			List<ConferenceRoom> roomList = conferenceRoomService.getRoomByRegion(region);
			return ResponseMessage.success("获取会议室数据成功!",roomList);
		}catch (Exception ex){
			LOGGER.error("获取会议室数据失败:"+ex.getMessage());
			return ResponseMessage.fail("获取会议室数据失败!");
		}
	}
}

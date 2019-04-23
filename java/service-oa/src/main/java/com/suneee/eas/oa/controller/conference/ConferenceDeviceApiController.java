package com.suneee.eas.oa.controller.conference;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.conference.ConferenceDevice;
import com.suneee.eas.oa.service.conference.ConferenceDeviceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 会议设备相关控制器类
 * @Author: kaize
 * @Date: 2018/7/31 14:54
 */
@RestController
@RequestMapping(ModuleConstant.CONFERENCE_MODULE+FunctionConstant.CONFERENCE_DEVICE)
public class ConferenceDeviceApiController {
	private static final Logger LOGGER = LogManager.getLogger(ConferenceDeviceApiController.class);

	@Autowired
	private ConferenceDeviceService conferenceDeviceService;

	/**
	 * 新增/更新会议设备
	 * @param request
	 * @param conferenceDevice
	 * @return
	 */
	@RequestMapping("save")
	public ResponseMessage save(HttpServletRequest request, ConferenceDevice conferenceDevice){
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
		Long deviceId = conferenceDevice.getDeviceId();
		try {
			String deviceCode = conferenceDevice.getDeviceCode();
			if(BeanUtils.isEmpty(deviceCode)){
				return ResponseMessage.fail("设备编号不能为空！");
			}
			if (deviceId == 0) {
				//判断设备编号是否重复
				boolean flag = conferenceDeviceService.isDeviceCodeRepeatForAdd(deviceCode);
				if(flag){
					return ResponseMessage.fail("设备编号已存在，请重新输入！");
				}
				conferenceDeviceService.save(conferenceDevice);
				responseMessage.setMessage("会议设备新增成功！");
			} else {
				//判断设备编号是否重复
				boolean flag = conferenceDeviceService.isDeviceCodeRepeatForUpdate(deviceId,deviceCode);
				if(flag){
					return ResponseMessage.fail("设备编号已存在，请重新输入！");
				}
				conferenceDeviceService.update(conferenceDevice);
				responseMessage.setMessage("会议设备更新成功！");
			}
		}catch (Exception ex){
			responseMessage.setStatus(ResponseMessage.STATUS_FAIL);
			if (deviceId == 0) {
				responseMessage.setMessage("会议设备新增失败！");
			} else {
				responseMessage.setMessage("会议设备更新失败！");
			}
			LOGGER.error(responseMessage.getMessage()+":"+ex.getMessage());
		}
		return responseMessage;
	}

	/**
	 * 根据ID(批量)删除会议设备
	 * @param request
	 * @return
	 */
	@RequestMapping("delByIds")
	public ResponseMessage delByIds(HttpServletRequest request){
		String deviceIds = RequestUtil.getString(request, "deviceIds");
		if(BeanUtils.isEmpty(deviceIds)){
			return ResponseMessage.fail("删除失败，参数不能为空!");
		}
		try {
			conferenceDeviceService.deleteByIds(deviceIds);
			return ResponseMessage.success("会议设备删除成功!");
		}catch (Exception ex){
			LOGGER.error("会议设备删除失败:"+ex.getMessage());
			return ResponseMessage.fail("会议设备删除失败！");
		}
	}

	/**
	 * 获取所有会议设备
	 * @return
	 */
	@RequestMapping("listPage")
	public ResponseMessage listPage(HttpServletRequest request){
		QueryFilter queryFilter = new QueryFilter("listAll",request);
		try {
			queryFilter.getFilters().put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			Pager<ConferenceDevice> conferenceDeviceList = conferenceDeviceService.getPageBySqlKey(queryFilter);
			return ResponseMessage.success("获取会议设备列表数据成功！",conferenceDeviceList);
		}catch (Exception ex){
			LOGGER.error("获取会议设备列表数据失败:"+ex.getMessage());
			return ResponseMessage.fail("获取会议设备列表数据失败！");
		}
	}

	/**
	 * 获取会议设备详情
	 * @param request
	 * @return
	 */
	@RequestMapping("findById")
	public ResponseMessage findById(HttpServletRequest request){
		long deviceId = RequestUtil.getLong(request, "deviceId");
		if(deviceId == 0){
			return ResponseMessage.fail("获取失败，参数不能为空！");
		}
		try {
			ConferenceDevice conferenceDevice = conferenceDeviceService.findById(deviceId);
			return ResponseMessage.success("获取会议设备详情成功！",conferenceDevice);
		}catch (Exception ex){
			LOGGER.error("获取会议设备详情失败:"+ex.getMessage());
			return ResponseMessage.fail("获取会议设备详情失败!");
		}
	}
}

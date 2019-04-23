package com.suneee.eas.oa.service.conference.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.conference.ConferenceDeviceDao;
import com.suneee.eas.oa.model.conference.ConferenceDevice;
import com.suneee.eas.oa.service.conference.ConferenceDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: ConferenceDeviceServiceImpl
 * @Author: kaize
 * @Date: 2018/7/31 15:08
 */
@Service
public class ConferenceDeviceServiceImpl extends BaseServiceImpl<ConferenceDevice> implements ConferenceDeviceService {

	private ConferenceDeviceDao conferenceDeviceDao;

	@Autowired
	public void setConferenceDeviceDao(ConferenceDeviceDao conferenceDeviceDao) {
		this.conferenceDeviceDao = conferenceDeviceDao;
		setBaseDao(conferenceDeviceDao);
	}

	@Override
	public int save(ConferenceDevice conferenceDevice) {
		//生成唯一主键ID
		Long id = IdGeneratorUtil.getNextId();
		conferenceDevice.setDeviceId(id);
		conferenceDevice.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
		//设置创建人、更新人
		Long currrentUserId = ContextSupportUtil.getCurrentUserId();
		String fullName = ContextSupportUtil.getCurrentUsername();
		conferenceDevice.setCreateBy(currrentUserId);
		conferenceDevice.setUpdateBy(currrentUserId);
		conferenceDevice.setCreateByName(fullName);
		conferenceDevice.setUpdateByName(fullName);
		//获取当前时间
		Date date = new Date();
		conferenceDevice.setCreateTime(date);
		conferenceDevice.setUpdateTime(date);
		return conferenceDeviceDao.save(conferenceDevice);
	}

	@Override
	public int update(ConferenceDevice conferenceDevice) {
		//设置更新人更新人
		conferenceDevice.setUpdateBy(ContextSupportUtil.getCurrentUserId());
		conferenceDevice.setUpdateByName(ContextSupportUtil.getCurrentUsername());
		//设置更新时间
		conferenceDevice.setUpdateTime(new Date());
		return conferenceDeviceDao.update(conferenceDevice);
	}

	@Override
	public int deleteById(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("updateBy", ContextSupportUtil.getCurrentUserId());
		params.put("updateByName", ContextSupportUtil.getCurrentUsername());
		params.put("updateTime",new Date());
		params.put("deviceId",id);
		return conferenceDeviceDao.deleteById(params);
	}

	/**
	 * 判断设备编号是否重复(新增时)
	 *
	 * @param deviceCode
	 * @return
	 */
	public boolean isDeviceCodeRepeatForAdd(String deviceCode) {
		Map<String,Object> params = new HashMap<>();
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		params.put("deviceCode",deviceCode);
		int count = conferenceDeviceDao.isDeviceCodeRepeatForAdd(params);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断设备编号是否重复(编辑时)
	 *
	 * @param deviceCode
	 * @return
	 */
	@Override
	public boolean isDeviceCodeRepeatForUpdate(Long deviceId,String deviceCode) {
		Map<String,Object> params = new HashMap<>();
		params.put("deviceId",deviceId);
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		params.put("deviceCode",deviceCode);
		int count = conferenceDeviceDao.isDeviceCodeRepeatForUpdate(params);
		if (count > 0) {
			return true;
		}
		return false;
	}
	
	/** 
	 * @Title: countAvailableDeviceNumber 
	 * @Description: 获取当前企业下指定区域下的设备数量
	 * @param region
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceDeviceService#countAvailableDeviceNumber(java.lang.Long)
	 */
	public List<Map<String, Object>> countAvailableDeviceNumber(Long region){
		List<Map<String, Object>> deviceTypeList = this.countAvailableDeviceNumber(ContextSupportUtil.getCurrentEnterpriseCode(), region);
		for(Map<String, Object> deviceType : deviceTypeList){
			deviceType.put("deviceType", deviceType.get("deviceType").toString());
		}
		return deviceTypeList;
	}
	
	
	/**  
	 * @Title: countAvailableDeviceNumber 
	 * @Description: 统计指定企业指定区域内的各设备类型总数
	 * @param enterpriseCode
	 * @param region
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceDeviceService#countAvailableDeviceNumber(java.lang.String, java.lang.Long)
	 */
	public List<Map<String, Object>> countAvailableDeviceNumber(String enterpriseCode, Long region){
		Map<String,Object> params = new HashMap<>();
		params.put("enterpriseCode",enterpriseCode);
		params.put("region",region);
		return conferenceDeviceDao.countAvailableDeviceNumber(params);
	}

	/**
	 * 批量删除会议设备
	 * @param deviceIds
	 */
	@Override
	public void deleteByIds(String deviceIds) {
		String[] deviceIdList = deviceIds.split(",");
		for (String deviceId: deviceIdList) {
			deleteById(Long.valueOf(deviceId));
		}
	}
}

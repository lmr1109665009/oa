/**
 * @Title: ConferenceReserveDeviceServiceImpl.java 
 * @Package com.suneee.eas.oa.service.conference.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.oa.dao.conference.ConferenceReserveDeviceDao;
import com.suneee.eas.oa.exception.conference.ConferenceDeviceNotAvailableException;
import com.suneee.eas.oa.model.conference.ConferenceReserveDevice;
import com.suneee.eas.oa.service.conference.ConferenceDeviceService;
import com.suneee.eas.oa.service.conference.ConferenceReserveDeviceService;

/**
 * @ClassName: ConferenceReserveDeviceServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 14:47:43 
 *
 */
@Service
public class ConferenceReserveDeviceServiceImpl extends BaseServiceImpl<ConferenceReserveDevice> implements ConferenceReserveDeviceService{
	private static final Logger LOGGER = LogManager.getLogger(ConferenceReserveDeviceServiceImpl.class);
	private ConferenceReserveDeviceDao conferenceReserveDeviceDao;
	@Autowired
	private ConferenceDeviceService conferenceDeviceService;
//	private ThreadLocal<Long> deviceTypeLocal = new ThreadLocal<Long>();
	@Autowired
	public void setConferenceReserveDeviceDao(ConferenceReserveDeviceDao conferenceReserveDeviceDao){
		this.conferenceReserveDeviceDao = conferenceReserveDeviceDao;
		setBaseDao(conferenceReserveDeviceDao);
	}
	/** 
	 * @Title: save 
	 * @Description: 保存预定会议设备 
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @param deviceTypes 
	 * @param deviceTypeNames
	 * @throws ConferenceDeviceNotAvailableException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveDeviceService#batchSave(java.lang.Long, java.lang.Long, java.util.Date, java.util.Date, java.lang.String)
	 */
	@Override
	public void save(Long conferenceId, Long region, Date startTime, Date endTime, String deviceTypes, 
			String deviceTypeNames) throws ConferenceDeviceNotAvailableException {
		if(StringUtils.isBlank(deviceTypes)||  StringUtils.isBlank(deviceTypeNames)){
			LOGGER.info("deviceTypes or deviceTypeNames is blank, do not perform a save operation.");
			return;
		}
		
		// 将会议设备ID字符串转换为ID集合
		String[] deviceTypeArr = deviceTypes.split(",");
		List<Long> deviceTypeList = new ArrayList<Long>();
		for(String deviceId : deviceTypeArr){
			deviceTypeList.add(Long.parseLong(deviceId));
		}
		
		String[] deviceTypeNameArr = deviceTypeNames.split(",");
		List<String> deviceTypeNameList = Arrays.asList(deviceTypeNameArr);
		this.batchSave(conferenceId, region, startTime, endTime, deviceTypeList, deviceTypeNameList);
	}
	
	/** 
	 * @Title: update 
	 * @Description: 更新会议设备
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @param deviceTypes
	 * @param deviceTypeNames
	 * @throws ConferenceDeviceNotAvailableException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveDeviceService#update(java.lang.Long, java.lang.Long, java.util.Date, java.util.Date, java.lang.String, java.lang.String)
	 */
	public void update(Long conferenceId, Long region, Date startTime, Date endTime, String deviceTypes, 
			String deviceTypeNames) throws ConferenceDeviceNotAvailableException {
		// 删除已经存在的预定会议设备
		this.deleteByConferenceId(conferenceId);
		// 保存新的预定会议设备
		this.save(conferenceId, region, startTime, endTime, deviceTypes, deviceTypeNames);
	}
	
	/** 
	 * @Title: batchSave 
	 * @Description: 批量保存预定会议设备
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @param deviceTypes
	 * @param deviceTypeNames
	 * @throws ConferenceDeviceNotAvailableException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveDeviceService#batchSave(java.lang.Long, java.lang.Long, java.util.Date, java.util.Date, java.util.List, java.util.List)
	 */
	@Override
	public void batchSave(Long conferenceId, Long region, Date startTime, Date endTime, List<Long> deviceTypes, 
			List<String> deviceTypeNames) throws ConferenceDeviceNotAvailableException {
		if(conferenceId == null || deviceTypes == null || deviceTypeNames == null){
			throw new IllegalArgumentException("conferenceId, deviceTypes and deviceTypeNames must not be null.");
		}
		if(deviceTypes.size() != deviceTypeNames.size()){
			throw new IllegalArgumentException("the length of 'deviceTypes' is not accordance with 'deviceTypeNames'.");
		}
		// 判断选择的会议设备是否可用
//		if(!this.isDeviceAvailable(conferenceId, region, startTime, endTime, deviceTypes)){
//			throw new ConferenceDeviceNotAvailableException(deviceTypeLocal.get() + " is not available.");
//		}
		List<ConferenceReserveDevice> list = new ArrayList<ConferenceReserveDevice>();
		ConferenceReserveDevice conferenceReserveDevice = null;
		// 构建预定会议设备信息对象
		for(int i = 0; i < deviceTypes.size(); i++){
			conferenceReserveDevice = new ConferenceReserveDevice();
			conferenceReserveDevice.setConferenceId(conferenceId);
			conferenceReserveDevice.setDeviceType(deviceTypes.get(i));
			conferenceReserveDevice.setDeviceTypeName(deviceTypeNames.get(i));
			list.add(conferenceReserveDevice);
		}
		// 批量添加
		conferenceReserveDeviceDao.batchSave(list);
	}

	/** 
	 * 删除预定会议设备
	 * @param conferenceId
	 */
	@Override
	public void deleteByConferenceId(Long conferenceId) {
		// TODO Auto-generated method stub
		conferenceReserveDeviceDao.deleteByConferenceId(conferenceId);
	}

	/** 
	 * 查询预定会议设备
	 * @param conferenceId
	 * @return
	 */
	@Override
	public List<ConferenceReserveDevice> getByConferenceId(Long conferenceId) {
		// TODO Auto-generated method stub
		return conferenceReserveDeviceDao.getByConferenceId(conferenceId);
	}
	
	/** 
	 * @Title: isDeviceAvailable 
	 * @Description: 判断区域内指定时间段内选择的会议设备是否可用
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @param deviceTypes
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveDeviceService#isDeviceAvailable(java.lang.Long, java.util.Date, java.util.Date, java.util.List)
	 */
	@Override
	public boolean isDeviceAvailable(Long conferenceId, Long region, Date startTime, Date endTime, List<Long> deviceTypes){
		if(deviceTypes == null){
			throw new IllegalArgumentException("availableDevices and deviceTypes must not be null.");
		}
		// 获取可用会议设备
		List<Map<String, Object>> availableDevices = this.getAvailableDeviceNumber(conferenceId, region, startTime, endTime);
				
		// 将可用设备及其数量数据转换成key=设备类型，value=数量的map结构
		Map<Long, Long> deviceNumbers = new HashMap<Long, Long>();
		for(Map<String, Object> total : availableDevices){
			deviceNumbers.put((Long)total.get("deviceType"), (Long)total.get("number"));
		}
		Long number = null;
		for(Long deviceType : deviceTypes){
			number = deviceNumbers.get(deviceType);
			if(number == null || number <= 0){
//				deviceTypeLocal.set(deviceType);
				return false;
			}
		}
		return true;
	}
	
	/** 
	 * @Title: getAvailableDeviceNumber 
	 * @Description: 获取区域内指定时间段的可用设备及其数量 
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveDeviceService#getAvailableDeviceNumber(java.lang.Long, java.lang.Long, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Map<String, Object>> getAvailableDeviceNumber(Long conferenceId, Long region, Date startTime, Date endTime){
		if(region == null || startTime == null || endTime == null){
			throw new IllegalArgumentException("region, startTime and endTime must not be null.");
		}
		// 查询系统的设备类型总数
		List<Map<String, Object>> totalList = conferenceDeviceService.countAvailableDeviceNumber(region);
		
		// 获取区域内指定时间段内已使用的设备数量
		List<Map<String, Object>> usedList = this.getUsedDeviceNumber(conferenceId, region, startTime, endTime);
		
		// 计算区域内指定时间段的可用设备及其数量（总数-已使用数）
		for(Map<String, Object> used : usedList){
			String usedDeviceType = used.get("deviceType").toString();
			for(Map<String, Object> total : totalList){
				String totalDeviceType = total.get("deviceType").toString();
				total.put("deviceType", totalDeviceType);
				if(usedDeviceType.equals(totalDeviceType)){
					long surplus = (Long)total.get("number") - (Long)used.get("number");
					total.put("number", surplus < 0 ? 0 : surplus);
				}
			}
		}
		return totalList;
	}
	
	/** 
	 * @Title: getUsedDeviceNumber 
	 * @Description: 获取当前企业下指定区域指定时间段内已使用的设备数量
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReserveDeviceService#getUsedDeviceNumber(java.lang.Long, java.lang.Long, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Map<String, Object>> getUsedDeviceNumber(Long conferenceId, Long region, Date startTime, Date endTime){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		params.put("region", region);
		params.put("beginTime", startTime);
		params.put("endTime", endTime);
		params.put("conferenceId", conferenceId);
		return conferenceReserveDeviceDao.getUsedDeviceNumber(params);
	}
}

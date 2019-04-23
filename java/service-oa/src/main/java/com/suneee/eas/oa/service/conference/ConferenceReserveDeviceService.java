/**
 * @Title: ConferenceReserveDeviceService.java 
 * @Package com.suneee.eas.oa.service.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.suneee.eas.oa.exception.conference.ConferenceDeviceNotAvailableException;
import com.suneee.eas.oa.model.conference.ConferenceReserveDevice;

/**
 * @ClassName: ConferenceReserveDeviceService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 14:45:19 
 *
 */
public interface ConferenceReserveDeviceService{
	/** 
	 * 保存预定会议设备
	 * @param conferenceId
	 * @param region
	 * @param startDate
	 * @param endDate
	 * @param deviceTypes
	 * @param deviceTypeNames
	 * @throws ConferenceDeviceNotAvailableException
	 */
	public void save(Long conferenceId, Long region, Date startDate, Date endDate, String deviceTypes, 
			String deviceTypeNames) throws ConferenceDeviceNotAvailableException;
	/** 
	 * 批量保存预定会议设备
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @param deviceTypes
	 * @param deviceTypeNames
	 * @throws ConferenceDeviceNotAvailableException
	 */
	public void batchSave(Long conferenceId, Long region, Date startTime, Date endTime, List<Long> deviceTypes, 
			List<String> deviceTypeNames) throws ConferenceDeviceNotAvailableException;
	/** 
	 * 更新会议设备
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @param deviceTypes
	 * @param deviceTypeNames
	 * @throws ConferenceDeviceNotAvailableException
	 */
	public void update(Long conferenceId, Long region, Date startTime, Date endTime, String deviceTypes, 
			String deviceTypeNames) throws ConferenceDeviceNotAvailableException;
	/** 
	 * 删除预定会议设备
	 * @param conferenceId
	 */
	public void deleteByConferenceId(Long conferenceId);
	/** 
	 * 查询预定会议设备
	 * @param conferenceId
	 * @return
	 */
	public List<ConferenceReserveDevice> getByConferenceId(Long conferenceId);
	/** 判断区域内指定时间段内选择的会议设备是否可用
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @param deviceTypes
	 * @return
	 */
	public boolean isDeviceAvailable(Long conferenceId, Long region, Date startTime, Date endTime, List<Long> deviceTypes);
	/** 获取区域内指定时间段的可用设备情况
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Map<String, Object>> getAvailableDeviceNumber(Long conferenceId, Long region, Date startTime, Date endTime);
	/** 
	 * 获取当前企业下指定区域指定时间段内已使用的设备情况
	 * @param conferenceId
	 * @param region
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Map<String, Object>> getUsedDeviceNumber(Long conferenceId, Long region, Date startTime, Date endTime);
}

package com.suneee.eas.oa.service.conference;

import java.util.List;
import java.util.Map;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.conference.ConferenceDevice;

/**
 * @Description: ConferenceDeviceService
 * @Author: kaize
 * @Date: 2018/7/31 15:07
 */
public interface ConferenceDeviceService extends BaseService<ConferenceDevice> {

	boolean isDeviceCodeRepeatForAdd(String deviceCode);

	boolean isDeviceCodeRepeatForUpdate(Long deviceId,String deviceCode);
	/** 获取当前企业下指定区域下的设备数量
	 * @param region
	 * @return
	 */
	public List<Map<String, Object>> countAvailableDeviceNumber(Long region);
	/** 统计指定企业指定区域内的各设备类型总数
	 * @param enterpriseCode
	 * @param region
	 * @return
	 */
	public List<Map<String, Object>> countAvailableDeviceNumber(String enterpriseCode, Long region);

	/**
	 * 批量删除会议设备
	 * @param deviceIds
	 */
	void deleteByIds(String deviceIds);
}

package com.suneee.ucp.me.dao.conference;

import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.conference.ConferenceDevice;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:设备信息表 Dao类
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-04-27 17:45:03
 *</pre>
 */
@Repository
public class ConferenceDeviceDao extends UcpBaseDao<ConferenceDevice>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ConferenceDevice.class;
	}
	
	/**
	 * 根据设备ID获取设备信息
	 * @param conferenceDeviceIds
	 * @return
	 */
	public List<ConferenceDevice> getConferenceDevicesByIds(List<Long> deviceIds){
		if(null == deviceIds || 0 == deviceIds.size()){
			return null;
		}
		return getBySqlKey("getConferenceDevicesByIds", deviceIds);
	}
	
	/**
	 * 获取可用设备
	 * @param conferenceDeviceIds
	 * @return
	 */
	public List<ConferenceDevice> getConferenceDevicesNotIn(List<Long> deviceIds, Long region){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("deviceIds", new HashSet<Long>(deviceIds));
		params.put("region", region);
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		return getBySqlKey("getConferenceDevicesNotIn", params);
	}

	/**
	 * 根据会议室id获取会议设备
	 * @param roomId
	 * @return
	 */
	public List<ConferenceDevice> getConferenceDevicesByRoomId(Long roomId){
		return this.getBySqlKey("getConferenceDevicesByRoomId",roomId);
	}
}
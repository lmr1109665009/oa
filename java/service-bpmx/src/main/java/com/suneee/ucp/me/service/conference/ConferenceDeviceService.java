package com.suneee.ucp.me.service.conference;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.conference.ConferenceDeviceDao;
import com.suneee.ucp.me.model.conference.ConferenceDevice;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *<pre>
 * 对象功能:设备信息表 Service类
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-04-27 17:45:03
 *</pre>
 */
@Service
public class ConferenceDeviceService extends UcpBaseService<ConferenceDevice>
{
	@Resource
	private ConferenceDeviceDao dao;

	@Override
	protected IEntityDao<ConferenceDevice, Long> getEntityDao() 
	{
		return dao;
	}
	
	/**
	 * 保存 设备信息表 信息
	 * @param conferenceDevice
	 */
	public void save(ConferenceDevice conferenceDevice){
		Long id = conferenceDevice.getDeviceId();
		Date nowDate = new Date();
		Long userId = ContextUtil.getCurrentUserId();
		conferenceDevice.setUpdateBy(userId);
		conferenceDevice.setUpdatetime(nowDate);
		conferenceDevice.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
		if(null == id || 0 == id){
			id=UniqueIdUtil.genId();
			conferenceDevice.setDeviceId(id);
			conferenceDevice.setCreateBy(userId);
			conferenceDevice.setCreatetime(nowDate);
			this.add(conferenceDevice);
		}
		else{
			this.update(conferenceDevice);
		}
	}
	
	/**
	 * 根据会议设备ID获取设备信息
	 * @param conferenceDeviceIds 多个设备ID之间使用逗号分隔
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConferenceDevice> getConferenceDevicesByIds(String conferenceDeviceIds){
		String[] conferenceDeviceIdArr = conferenceDeviceIds.split(Constants.SEPARATOR_COMMA);
		List<Long> deviceIdList=new ArrayList<Long>();
		for (String deviceItem:conferenceDeviceIdArr) {
			deviceIdList.add(Long.parseLong(deviceItem));
		}
		return this.getConferenceDevicesByIds(deviceIdList);
	}
	
	/**
	 * 根据会议设备ID获取设备信息
	 * @param conferenceDeviceIds
	 * @return
	 */
	public List<ConferenceDevice> getConferenceDevicesByIds(List<Long> deviceIds){
		return dao.getConferenceDevicesByIds(deviceIds);
	}
	
	/**
	 * 获取指定地区可用设备
	 * @param devicesIds
	 * @param region
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConferenceDevice> getConferenceDevicesNotIn(String devicesIds, Long region){
		String[] deviceIdArr = devicesIds.split(Constants.SEPARATOR_COMMA);
		List<Long> deviceIdList=new ArrayList<Long>();
		for(String deviceIdItem:deviceIdArr){
			deviceIdList.add(Long.parseLong(deviceIdItem));
		}
		return getConferenceDevicesNotIn(deviceIdList, region);
	}
	
	/**
	 * 获取指定地区的可用设备
	 * @param deviceIds
	 * @param region
	 * @return
	 */
	public List<ConferenceDevice> getConferenceDevicesNotIn(List<Long> deviceIds, Long region){
		return dao.getConferenceDevicesNotIn(deviceIds, region);
	}

	/**
	 * 根据会议室id获取会议设备
	 * @param roomId
	 * @return
	 */
	public List<ConferenceDevice> getConferenceDevicesByRoomId(Long roomId){
		return dao.getConferenceDevicesByRoomId(roomId);
	}
}

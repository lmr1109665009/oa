package com.suneee.ucp.me.service.conference;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.web.util.CookieUitl;
import org.springframework.stereotype.Service;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.conference.ConferenceRoomDao;
import com.suneee.ucp.me.model.conference.ConferenceRoom;

/**
 *<pre>
 * 对象功能:会议室信息 Service类
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-04-27 15:24:26
 *</pre>
 */
@Service
public class ConferenceRoomService extends UcpBaseService<ConferenceRoom>
{
	@Resource
	private ConferenceRoomDao dao;
	
	@Override
	protected IEntityDao<ConferenceRoom, Long> getEntityDao() 
	{
		return dao;
	}

	/**
	 * 保存 会议室信息 信息
	 * @param conferenceRoom
	 */
	public void save(ConferenceRoom conferenceRoom){
		Long id = conferenceRoom.getRoomId();
		Date nowDate = new Date();
		Long userId = ContextUtil.getCurrentUserId();
		conferenceRoom.setUpdateBy(userId);
		conferenceRoom.setUpdatetime(nowDate);
		conferenceRoom.setEnterpriseCode(CookieUitl.getCurrentEnterpriseCode());
		if(null == id || 0 == id){
			id = UniqueIdUtil.genId();
			conferenceRoom.setRoomId(id);
			conferenceRoom.setCreateBy(userId);
			conferenceRoom.setCreatetime(nowDate);
			this.add(conferenceRoom);
		}
		else{
			this.update(conferenceRoom);
		}
	}
	
	/**
	 * 根据地区获取会议室列表
	 * @param region
	 * @return
	 */
	public List<ConferenceRoom> getByRegion(Long region){
		return dao.getByRegion(region);
	}
}

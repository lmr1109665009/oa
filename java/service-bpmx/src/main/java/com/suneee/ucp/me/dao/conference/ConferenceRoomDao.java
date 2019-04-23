package com.suneee.ucp.me.dao.conference;

import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.conference.ConferenceRoom;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:会议室信息 Dao类
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-04-27 15:24:26
 *</pre>
 */
@Repository
public class ConferenceRoomDao extends UcpBaseDao<ConferenceRoom>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ConferenceRoom.class;
	}
	
	/**
	 * 根据地区获取会议室列表
	 * @param region
	 * @return
	 */
	public List<ConferenceRoom> getByRegion(Long region){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("region", region);
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		return getBySqlKey("getByRegion", params);
	}
}
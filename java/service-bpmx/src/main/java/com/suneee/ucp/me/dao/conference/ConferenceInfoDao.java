package com.suneee.ucp.me.dao.conference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.web.query.QueryFilter;
import org.springframework.stereotype.Repository;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.conference.ConferenceInfo;

/**
 *<pre>
 * 对象功能:会议信息 Dao类
 * 开发公司:深圳象翌
 * 开发人员:xiongxianyun
 * 创建时间:2017-05-05 13:21:21
 *</pre>
 */
@Repository
public class ConferenceInfoDao extends UcpBaseDao<ConferenceInfo>
{
	@Override
	public Class<ConferenceInfo> getEntityClass()
	{
		return ConferenceInfo.class;
	}
	
	/**
	 * 根据指定条件查询会议室预订记录
	 * @param params
	 * @return
	 */
	public List<ConferenceInfo> getByCondition(Map<String, Object> params){
		return getBySqlKey("getAll", params);
	}
	
	
	/**
	 * 判断会议室在指定的时间段内是否被预订
	 * @param conferenceInfo
	 * @return
	 */
	public List<ConferenceInfo> isReverse(ConferenceInfo conferenceInfo){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roomId", conferenceInfo.getRoomId());
		params.put("convokeDate", conferenceInfo.getConvokeDate());
		params.put("beginTime", conferenceInfo.getBeginTime());
		params.put("endTime", conferenceInfo.getEndTime());
		return getBySqlKey("isReverse", params);
	}

	/**
	 * 获取当前用户所需参加的会议信息列表
	 * @param queryFilter
	 * @return
	 */
	public List<ConferenceInfo> getAllAboutCurUser(QueryFilter queryFilter) {
		String statementName = this.getIbatisMapperNamespace() + ".getAllAboutCurUser";
		List<ConferenceInfo> list = this.getList(statementName, queryFilter);
		queryFilter.setForWeb();
		return list;
	}
}
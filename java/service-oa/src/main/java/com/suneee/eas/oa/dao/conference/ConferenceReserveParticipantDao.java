/**
 * @Title: ConferenceReserveParticipantDao.java 
 * @Package com.suneee.eas.oa.dao.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.dao.conference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.conference.ConferenceReserveParticipant;

/**
 * @ClassName: ConferenceReserveParticipantDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 13:16:18 
 *
 */
@Repository
public class ConferenceReserveParticipantDao extends BaseDao<ConferenceReserveParticipant>{
	
	/** 
	 * 批量添加与会人员
	 * @param list
	 * @return
	 */
	public int batchSave(List<ConferenceReserveParticipant> list){
		return this.getSqlSessionTemplate().insert(getNamespace() + ".batchSave", list);
	}
	
	/** 删除预定会议的与会人员
	 * @param conferenceId
	 * @param type
	 * @return
	 */
	public int deleteByConferenceIdAndType(Long conferenceId, Byte type){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conferenceId", conferenceId);
		params.put("type", type);
		return this.getSqlSessionTemplate().delete(getNamespace() + ".deleteByConferenceIdAndType", params);
	}
	
	/** 获取预定会议的与会人员
	 * @param conferenceId
	 * @param type
	 * @return
	 */
	public List<ConferenceReserveParticipant> getByConferenceIdAndType(Long conferenceId, Byte type){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conferenceId", conferenceId);
		params.put("type", type);
		return this.getSqlSessionTemplate().selectList(getNamespace() + ".getByConferenceId", params);
	}
	
	/** 获取参会人员
	 * @param conferenceId
	 * @param userId
	 * @param type
	 * @return
	 */
	public ConferenceReserveParticipant getConferenceReserveParticipant(Long conferenceId, Long userId, Byte type){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conferenceId", conferenceId);
		params.put("userId", userId);
		params.put("type", type);
		return this.getSqlSessionTemplate().selectOne(getNamespace() + ".getConferenceReserveParticipant", params);
	}
}

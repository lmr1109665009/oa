/**
 * @Title: ConferenceReserveDao.java 
 * @Package com.suneee.eas.oa.dao.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.dao.conference;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.conference.ConferenceReserve;

/**
 * @ClassName: ConferenceReserveService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 11:20:04 
 *
 */
@Repository
public class ConferenceReserveDao extends BaseDao<ConferenceReserve>{
	/** 条件查询会议记录
	 * @param params
	 * @return
	 */
	public List<ConferenceReserve> listAll(Map<String, Object> params){
		return this.getSqlSessionTemplate().selectList(getNamespace() + ".listAll", params);
	}
	
	/** 更新会议纪要状态
	 * @param conferenceId
	 * @param status
	 * @return
	 */
	public int updateNoteStatus(Long conferenceId, Byte status){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conferenceId", conferenceId);
		params.put("noteStatus", status);
		return this.getSqlSessionTemplate().update(getNamespace() + ".updateNoteStatus", params);
	}
	/** 更新会议状态
	 * @param conferenceId
	 * @param status
	 * @return
	 */
	public int updateAuditStatus(Long conferenceId, Byte status){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conferenceId", conferenceId);
		params.put("auditStatus", status);
		return this.getSqlSessionTemplate().update(getNamespace() + ".updateAuditStatus", params);
	}
	
	public int updateEndTime(Long conferenceId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conferenceId", conferenceId);
		params.put("endTime", new Date());
		return this.getSqlSessionTemplate().update(getNamespace() + ".updateEndTime", params);
	}
}

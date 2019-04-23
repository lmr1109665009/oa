/**
 * @Title: ConferenceNoteDao.java 
 * @Package com.suneee.eas.oa.dao.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.dao.conference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.conference.ConferenceNote;
import com.suneee.eas.oa.model.conference.ConferenceReserve;

/**
 * @ClassName: ConferenceNoteDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 09:20:21 
 *
 */
@Repository
public class ConferenceNoteDao extends BaseDao<ConferenceNote>{
	/** 更新会议纪要状态
	 * @param noteId
	 * @param status
	 * @return
	 */
	public int updateStatus(Long noteId, Byte status){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("noteId", noteId);
		params.put("status", status);
		return this.getSqlSessionTemplate().update(getNamespace() + ".updateStatus", params);
	}
	
	/** 获取指定会议的会议纪要
	 * @param conferenceId
	 * @return
	 */
	public ConferenceNote getByConferenceId(Long conferenceId){
		return this.getSqlSessionTemplate().selectOne(getNamespace() + ".getByConferenceId", conferenceId);
	}
	
	/** 获取会议纪要列表
	 * @param filter
	 * @return
	 */
	public List<ConferenceReserve> getAll(QueryFilter filter){
		return this.getSqlSessionTemplate().selectList(getNamespace() + "." + filter.getSqlKey(), filter.getFilters());
	}
}

/**
 * @Title: ConferenceReceiptDao.java 
 * @Package com.suneee.eas.oa.dao.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.dao.conference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.conference.ConferenceReceipt;

/**
 * @ClassName: ConferenceReceiptDao 
 * @Description: 会议回执Dao层实现类
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-09 11:14:41 
 *
 */
@Repository
public class ConferenceReceiptDao extends BaseDao<ConferenceReceipt>{
	/** 获取会议的所有回执信息
	 * @param conferenceId
	 * @return
	 */
	public List<ConferenceReceipt> getByConferenceId(Long conferenceId){
		return this.getSqlSessionTemplate().selectList(getNamespace() + ".getByConferenceId", conferenceId);
	}
	
	/** 获取
	 * @param conferenceId
	 * @param userId
	 * @return
	 */
	public ConferenceReceipt getConferenceReceipt(Long conferenceId, Long userId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conferenceId", conferenceId);
		params.put("userId", userId);
		return this.getSqlSessionTemplate().selectOne(getNamespace() + ".getConferenceReceipt", params);
	}
	
	/** 删除会议回执信息
	 * @param conferenceId
	 * @return
	 */
	public int deleteByConferenceId(Long conferenceId){
		return this.getSqlSessionTemplate().delete(getNamespace() + ".deleteByConferenceId", conferenceId);
	}
}

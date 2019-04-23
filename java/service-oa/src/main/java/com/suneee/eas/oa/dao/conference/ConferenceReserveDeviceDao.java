/**
 * @Title: ConferenceReserveDeviceDao.java 
 * @Package com.suneee.eas.oa.dao.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.dao.conference;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.conference.ConferenceReserveDevice;

/**
 * @ClassName: ConferenceReserveDeviceDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 13:15:12 
 *
 */
@Repository
public class ConferenceReserveDeviceDao extends BaseDao<ConferenceReserveDevice>{
	
	/** 
	 * 批量保存
	 * @param list
	 * @return
	 */
	public int batchSave(List<ConferenceReserveDevice> list){
		return this.getSqlSessionTemplate().insert(getNamespace() + ".batchSave", list);
	}
	
	/** 
	 * 删除预定会议设备
	 * @param conferenceId
	 * @return
	 */
	public int deleteByConferenceId(Long conferenceId){
		return this.getSqlSessionTemplate().delete(getNamespace() + ".deleteByConferenceId", conferenceId);
	}
	
	/** 
	 * 获取预定会议设备
	 * @param conferenceId
	 * @return
	 */
	public List<ConferenceReserveDevice> getByConferenceId(Long conferenceId){
		return this.getSqlSessionTemplate().selectList(getNamespace() + ".getByConferenceId", conferenceId);
	}
	
	/** 
	 * 获取已使用的设备情况
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getUsedDeviceNumber(Map<String, Object> params){
		return this.getSqlSessionTemplate().selectList(getNamespace() + ".getUsedDeviceNumber", params);
	}
}

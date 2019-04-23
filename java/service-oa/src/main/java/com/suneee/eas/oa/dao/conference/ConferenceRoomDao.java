package com.suneee.eas.oa.dao.conference;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.conference.ConferenceRoom;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: kaize
 * @Date: 2018/7/31 17:50
 */
@Repository
public class ConferenceRoomDao extends BaseDao<ConferenceRoom> {

	/**
	 * 根据ID删除
	 * @param params
	 * @return
	 */
	public int deleteById(Map<String, Object> params){
		return getSqlSessionTemplate().update(getNamespace()+".deleteById",params);
	}

	/**
	 * 根据地区获取会议室
	 * @param params
	 * @return
	 */
	public List<ConferenceRoom> getRoomByRegion(Map<String, Object> params){
		return getSqlSessionTemplate().selectList(getNamespace()+".getRoomByRegion",params);
	}

	/**
	 * 获取可用会议室
	 * @param params
	 * @return
	 */
	public List<ConferenceRoom> getAvailableRoom(Map<String, Object> params){
		return getSqlSessionTemplate().selectList(getNamespace()+".getAvailableRoom",params);
	}

	/**
	 * 判断同一区域会议室名称是否重复（新增）
	 * @param params
	 * @return
	 */
	public int isRoomNameRepeatForAdd(Map<String, Object> params){
		return this.getSqlSessionTemplate().selectOne("isRoomNameRepeatForAdd",params);
	}

	/**
	 * 判断同一区域会议室名称是否重复（更新）
	 * @param params
	 * @return
	 */
	public int isRoomNameRepeatForUpdate(Map<String, Object> params) {
		return this.getSqlSessionTemplate().selectOne("isRoomNameRepeatForUpdate",params);
	}
}

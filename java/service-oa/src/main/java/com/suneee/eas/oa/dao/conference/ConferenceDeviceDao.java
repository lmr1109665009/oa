package com.suneee.eas.oa.dao.conference;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.conference.ConferenceDevice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description: ConferenceDeviceDao
 * @Author: kaize
 * @Date: 2018/7/31 15:11
 */
@Repository
public class ConferenceDeviceDao extends BaseDao<ConferenceDevice> {

	/**
	 * 判断设备编号是否重复（新增时）
	 * @param params
	 * @return
	 */
	public int isDeviceCodeRepeatForAdd(Map<String,Object> params){
		return getSqlSessionTemplate().selectOne(getNamespace() + ".isDeviceCodeRepeatForAdd",params);
	}

	/**
	 * 判断设备编号是否重复（更新时）
	 * @param params
	 * @return
	 */
	public int isDeviceCodeRepeatForUpdate(Map<String,Object> params){
		return getSqlSessionTemplate().selectOne(getNamespace() + ".isDeviceCodeRepeatForUpdate",params);
	}

	/**
	 * 根据ID删除
	 * @param params
	 * @return
	 */
	public int deleteById(Map<String, Object> params){
		return getSqlSessionTemplate().update(getNamespace() + ".deleteById",params);
	}

	/** 
	 * 统计指定企业指定区域内的各可用设备类型总数
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> countAvailableDeviceNumber(Map<String, Object> params){
		return getSqlSessionTemplate().selectList(getNamespace() + ".countAvailableDeviceNumber",params);
	}
}

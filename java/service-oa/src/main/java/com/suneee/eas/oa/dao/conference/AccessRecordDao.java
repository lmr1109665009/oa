/**
 * @Title: AccessRecordDao.java 
 * @Package com.suneee.eas.oa.dao.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.dao.conference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.conference.AccessRecord;

/**
 * @ClassName: AccessRecordDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 12:00:20 
 *
 */
@Repository
public class AccessRecordDao extends BaseDao<AccessRecord>{
	/** 获取访问记录
	 * @param targetId
	 * @param targetType
	 * @return
	 */
	public List<AccessRecord> getByTargetIdAndType(Long targetId, String targetType){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("targetId", targetId);
		params.put("targetType", targetType);
		return this.getSqlSessionTemplate().selectList(getNamespace() + ".getByTargetIdAndType", params);
	}
}

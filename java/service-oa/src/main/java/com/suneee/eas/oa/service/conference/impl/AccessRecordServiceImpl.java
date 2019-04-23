/**
 * @Title: AccessRecordServiceImpl.java 
 * @Package com.suneee.eas.oa.service.conference.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.conference.AccessRecordDao;
import com.suneee.eas.oa.model.conference.AccessRecord;
import com.suneee.eas.oa.service.conference.AccessRecordService;

/**
 * @ClassName: AccessRecordServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 15:08:23 
 *
 */
@Service
public class AccessRecordServiceImpl extends BaseServiceImpl<AccessRecord> implements AccessRecordService{
	private AccessRecordDao accessRecordDao;
	@Autowired
	public void setAccessRecordDao(AccessRecordDao accessRecordDao){
		this.accessRecordDao = accessRecordDao;
		setBaseDao(accessRecordDao);
	}
	
	/** 
	 * @Title: save 
	 * @Description: 保存访问记录
	 * @param userId
	 * @param userName
	 * @param targetId
	 * @param targetType 
	 * @see com.suneee.eas.oa.service.conference.AccessRecordService#save(java.lang.Long, java.lang.String, java.lang.Long, java.lang.String)
	 */
	public void save(Long userId, String userName, Long targetId, String targetType){
		if(userId == null || userName == null || targetId == null ||targetType == null){
			throw new IllegalArgumentException("userId, userName, targetId and targetType must not be null.");
		}
		AccessRecord accessRecord = new AccessRecord();
		accessRecord.setAccessId(IdGeneratorUtil.getNextId());
		accessRecord.setAccessor(userId);
		accessRecord.setAccessorName(userName);
		accessRecord.setTargetId(targetId);
		accessRecord.setTargetType(targetType);
		accessRecord.setAccessTime(new Date());
		this.save(accessRecord);
	}
	
	/** 
	 * @Title: getByTargetIdAndType 
	 * @Description: 获取访问记录
	 * @param targetId
	 * @param targetType
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.AccessRecordService#getByTargetIdAndType(java.lang.Long, java.lang.String)
	 */
	public List<AccessRecord> getByTargetIdAndType(Long targetId, String targetType){
		return this.accessRecordDao.getByTargetIdAndType(targetId, targetType);
	}
}

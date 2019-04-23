/**
 * @Title: AccessRecordService.java 
 * @Package com.suneee.eas.oa.service.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference;

import java.util.List;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.conference.AccessRecord;

/**
 * @ClassName: AccessRecordService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 15:07:52 
 *
 */
public interface AccessRecordService extends BaseService<AccessRecord>{
	/** 保存访问记录
	 * @param userId
	 * @param userName
	 * @param targetId
	 * @param targetType
	 */
	public void save(Long userId, String userName, Long targetId, String targetType);
	
	/** 获取访问记录
	 * @param targetId
	 * @param targetType
	 * @return
	 */
	public List<AccessRecord> getByTargetIdAndType(Long targetId, String targetType);
}

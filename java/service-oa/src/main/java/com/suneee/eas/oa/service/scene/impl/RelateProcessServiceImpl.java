/**
 * @Title: RelateProcessServiceImpl.java 
 * @Package com.suneee.eas.oa.service.scene.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.scene.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.scene.RelateProcessDao;
import com.suneee.eas.oa.model.scene.RelateProcess;
import com.suneee.eas.oa.service.scene.RelateProcessService;

/**
 * @ClassName: RelateProcessServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-09-04 14:27:23 
 *
 */
@Service
public class RelateProcessServiceImpl extends BaseServiceImpl<RelateProcess> implements RelateProcessService{
	private RelateProcessDao relateProcessDao;
	@Autowired
	public void setRelateProcessDao(RelateProcessDao relateProcessDao){
		this.relateProcessDao = relateProcessDao;
		setBaseDao(relateProcessDao);
	}
	
	/** 
	 * @Title: batchSave 
	 * @Description: 批量保存相关流程信息
	 * @param relProcessList
	 * @param sceneId 
	 * @see com.suneee.eas.oa.service.scene.RelateProcessService#batchSave(java.util.List, java.lang.Long)
	 */
	@Override
	public void batchSave(List<RelateProcess> relProcessList, Long sceneId) {
		if(relProcessList == null || relProcessList.size() <= 0 || sceneId == null){
			return;
		}
		for(RelateProcess relProcess : relProcessList){
			relProcess.setRelId(IdGeneratorUtil.getNextId());
			relProcess.setSceneId(sceneId);
		}
		
		relateProcessDao.batchSave(relProcessList);
	}

	/** (non-Javadoc)
	 * @Title: batchUpdate 
	 * @Description: 批量更新相关流程信息
	 * @param relProcessList
	 * @param sceneId 
	 * @see com.suneee.eas.oa.service.scene.RelateProcessService#batchUpdate(java.util.List, java.lang.Long)
	 */
	@Override
	public void batchUpdate(List<RelateProcess> relProcessList, Long sceneId) {
		// 删除场景相关的流程信息
		this.delBySceneId(sceneId);
		// 批量新增相关的流程信息
		this.batchSave(relProcessList, sceneId);
	}
	
	/** 
	 * @Title: delBySceneId 
	 * @Description: 删除场景下的相关流程 
	 * @param sceneId 
	 * @see com.suneee.eas.oa.service.scene.RelateProcessService#delBySceneId(java.lang.Long)
	 */
	@Override
	public void delBySceneId(Long sceneId) {
		relateProcessDao.delBySceneId(sceneId);
	}
	
	/** (non-Javadoc)
	 * @Title: getBySceneId 
	 * @Description: 获取场景下的相关流程
	 * @param sceneId
	 * @return 
	 * @see com.suneee.eas.oa.service.scene.RelateProcessService#getBySceneId(java.lang.Long)
	 */
	@Override
	public List<RelateProcess> getBySceneId(Long sceneId) {
		return relateProcessDao.getBySceneId(sceneId);
	}
}

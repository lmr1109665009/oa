/**
 * @Title: RelateProcessService.java 
 * @Package com.suneee.eas.oa.service.scene 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.scene;

import java.util.List;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.scene.RelateProcess;

/**
 * @ClassName: RelateProcessService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-09-04 14:26:36 
 *
 */
public interface RelateProcessService extends BaseService<RelateProcess>{
	/** 批量保存相关流程信息
	 * @param relProcessList
	 * @param sceneId
	 */
	public void batchSave(List<RelateProcess> relProcessList, Long sceneId);
	
	/** 批量更新场景相关流程信息
	 * @param relProcessList
	 * @param sceneId
	 */
	public void batchUpdate(List<RelateProcess> relProcessList, Long sceneId);
	
	/** 删除场景下的相关流程
	 * @param sceneId
	 */
	public void delBySceneId(Long sceneId);
	
	/** 获取场景下的相关流程
	 * @param sceneId
	 * @return
	 */
	public List<RelateProcess> getBySceneId(Long sceneId);
}

/**
 * @Title: SubProcessService.java 
 * @Package com.suneee.eas.oa.service.scene 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.scene;

import java.util.List;
import java.util.Map;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.scene.MobileScene;
import com.suneee.eas.oa.model.scene.SubProcess;

/**
 * @ClassName: SubProcessService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-20 16:38:27 
 *
 */
public interface SubProcessService extends BaseService<SubProcess>{
	/** 批量增加场景关联的子流程
	 * @param mobileScene
	 * @param subProcessList
	 * @return
	 */
	public Map<String, String> batchSave(MobileScene mobileScene, List<SubProcess> subProcessList);
	
	/** 批量更新场景关联的子流程
	 * @param mobileScene
	 * @param subProcessList
	 * @return
	 */
	public Map<String, String> batchUpdate(MobileScene mobileScene, List<SubProcess> subProcessList);
	
	/** 获取场景关联的子流程列表
	 * @param sceneId
	 * @return
	 */
	public List<SubProcess> getBySceneId(Long sceneId);
	
	/** 删除场景关联的子流程
	 * @param sceneId
	 */
	public void delBySceneId(Long sceneId);
	
	/** 通过场景ID和流程定义ID获取关联子流程
	 * @param sceneId
	 * @param defId
	 * @return
	 */
	public SubProcess getBySceneIdAndDefId(Long sceneId,Long defId);
	
}

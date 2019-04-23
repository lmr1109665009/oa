package com.suneee.eas.oa.service.scene.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.scene.SubProcessDao;
import com.suneee.eas.oa.model.scene.MobileScene;
import com.suneee.eas.oa.model.scene.SubProcess;
import com.suneee.eas.oa.service.scene.SubProcessService;

@Service
public class SubProcessServiceImpl extends BaseServiceImpl<SubProcess> implements SubProcessService{
    private SubProcessDao subProcessDao;
    @Autowired
    public void setSubProcessDao(SubProcessDao subProcessDao){
    	this.subProcessDao = subProcessDao;
    	setBaseDao(subProcessDao);
    }
    
    /** (non-Javadoc)
     * @Title: batchSave 
     * @Description: 批量增加场景关联的子流程 
     * @param mobileScene
     * @param subProcessList
     * @return 
     * @see com.suneee.eas.oa.service.scene.SubProcessService#batchSave(com.suneee.eas.oa.model.scene.MobileScene, java.util.List)
     */
	public Map<String, String> batchSave(MobileScene mobileScene, List<SubProcess> subProcessList){
		Map<String,String> map = new HashMap<>();
    	if(mobileScene == null || subProcessList == null || subProcessList.size() <= 0){
    		return map;
    	}
        String[] relDefIds = new String[subProcessList.size()];
		String[] relDefNames = new String[subProcessList.size()];
		int count = 0;
		for (SubProcess sub : subProcessList){
			// 处理子流程对象信息
			sub.setId(IdGeneratorUtil.getNextId());
			sub.setSceneId(mobileScene.getId());
			sub.setSceneName(mobileScene.getSceneName());
			sub.setDefId(mobileScene.getDefId());
	        sub.setDefName(mobileScene.getDefName());
	        sub.setDefKey(mobileScene.getDefKey());
	        
	        relDefIds[count] = sub.getSubDefId().toString();
	        relDefNames[count] = sub.getSubDefName();
	        count++;
	    }
		
		// 批量保存子流程信息
		subProcessDao.batchSave(subProcessList);
		
	    map.put("relDefIds", StringUtils.join(relDefIds, ","));
	    map.put("relDefNames",StringUtils.join(relDefNames, ","));
	    return map;
    }
    
    /** (non-Javadoc)
     * @Title: batchUpdate 
     * @Description: 批量更新场景关联的子流程 
     * @param mobileScene
     * @param subProcessList
     * @return 
     * @see com.suneee.eas.oa.service.scene.SubProcessService#batchUpdate(com.suneee.eas.oa.model.scene.MobileScene, java.util.List)
     */
    public Map<String, String> batchUpdate(MobileScene mobileScene, List<SubProcess> subProcessList){
    	if(mobileScene == null){
    		return new HashMap<String, String>();
    	}
    	
    	// 删除场景已关联的子流程
    	this.delBySceneId(mobileScene.getId());
    	
    	// 批量添加场景关联的子流程
    	return this.batchSave(mobileScene, subProcessList);
    }
    
    /** (non-Javadoc)
     * @Title: getBySceneId 
     * @Description: 获取场景关联的子流程列表 
     * @param sceneId
     * @return 
     * @see com.suneee.eas.oa.service.scene.SubProcessService#getBySceneId(java.lang.Long)
     */
    public List<SubProcess> getBySceneId(Long sceneId){
        return subProcessDao.getBySceneId(sceneId);
    }

    /** (non-Javadoc)
     * @Title: delBySceneId 
     * @Description: 删除场景关联的子流程
     * @param sceneId 
     * @see com.suneee.eas.oa.service.scene.SubProcessService#delBySceneId(java.lang.Long)
     */
    public void delBySceneId(Long sceneId){
    	subProcessDao.delBySceneId(sceneId);
    }

    /** 通过场景ID和流程定义ID获取关联子流程
     * @Title: getBySceneIdAndDefId 
     * @Description: TODO(这里用一句话描述这个方法的作用) 
     * @param sceneId
     * @param defId
     * @return 
     * @see com.suneee.eas.oa.service.scene.SubProcessService#getBySceneIdAndDefId(java.lang.Long, java.lang.Long)
     */
    public SubProcess getBySceneIdAndDefId(Long sceneId, Long defId){
        return subProcessDao.getBySceneIdAndDefId(sceneId,defId);
    }
}

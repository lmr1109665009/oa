package com.suneee.eas.oa.dao.scene;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.scene.SubProcess;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SubProcessDao extends BaseDao<SubProcess> {
	
	public int batchSave(List<SubProcess> subProcessList){
		return this.getSqlSessionTemplate().insert(getNamespace() + ".batchSave", subProcessList);
	}
	
    /** 获取场景关联的子流程列表 
     * @param sceneId
     * @return
     */
    public List<SubProcess> getBySceneId(Long sceneId) {
       return this.getSqlSessionTemplate().selectList(getNamespace() + ".getBySceneId", sceneId);
    }

    /** 通过场景ID和流程定义ID获取关联子流程
     * @param sceneId
     * @param defId
     * @return
     */
    public SubProcess getBySceneIdAndDefId(Long sceneId, Long defId) {
        Map<String,Long> params = new HashMap<>();
        params.put("sceneId",sceneId);
        params.put("subDefId",defId);
        List<SubProcess> list = this.getSqlSessionTemplate().selectList(getNamespace() + ".getBySceneIdAndDefId", params);
        if(list.size()==0) {
            return null;
        }else{
            return list.get(0);
        }
    }
    
    /** 删除场景关联的子流程
     * @param sceneId
     * @return
     */
    public int delBySceneId(Long sceneId){
    	return this.getSqlSessionTemplate().delete(getNamespace() + ".delBySceneId", sceneId);
    }
}

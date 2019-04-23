package com.suneee.eas.oa.dao.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.scene.MobileScene;

@Repository
public class MobileSceneDao extends BaseDao<MobileScene> {

    public List<MobileScene> getByTypeId(Long typeId) {
        return this.getSqlSessionTemplate().selectList(getNamespace() + ".getByTypeId", typeId);
    }

    /** 根据流程ID查询场景，当场景ID不为空时，查询结果去除该ID对应的场景
     * @param defId
     * @param sceneId
     * @return
     */
    public List<MobileScene> getByDefId(Long defId, Long sceneId) { 
    	Map<String, Long> params = new HashMap<>();
    	params.put("defId", defId);
    	params.put("sceneId", sceneId);
    	return this.getSqlSessionTemplate().selectList(getNamespace() + ".getByDefId", params);
    }
}

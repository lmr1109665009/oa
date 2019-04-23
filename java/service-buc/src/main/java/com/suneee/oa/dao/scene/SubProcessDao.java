package com.suneee.oa.dao.scene;

import com.suneee.oa.model.scene.SubProcess;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SubProcessDao extends UcpBaseDao<SubProcess> {
    @Override
    public Class<SubProcess> getEntityClass() {
        return SubProcess.class;
    }

    public List<SubProcess> getBySceneId(Long sceneId) {
       return this.getBySqlKey("getBySceneId", sceneId);
    }

    public SubProcess getBySceneIdAndDefId(Long sceneId, Long defId) {
        Map<String,Long> params = new HashMap<>();
        params.put("sceneId",sceneId);
        params.put("subDefId",defId);
        List<SubProcess> list = this.getBySqlKey("getBySceneIdAndDefId", params);
        if(list.size()==0) {
            return null;
        }else{
            return list.get(0);
        }
    }
}

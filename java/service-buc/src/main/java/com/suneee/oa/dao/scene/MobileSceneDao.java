package com.suneee.oa.dao.scene;

import com.suneee.oa.model.scene.MobileScene;
import com.suneee.ucp.base.dao.UcpBaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MobileSceneDao extends UcpBaseDao<MobileScene> {
    @Override
    public Class<MobileScene> getEntityClass() {
        return MobileScene.class;
    }

    public List<MobileScene> getByTypeId(Long typeId) {
        return this.getBySqlKey("getByTypeId", typeId);
    }

    public List<MobileScene> getByDefId(Long defId) { return this.getBySqlKey("getByDefId",defId);
    }
}

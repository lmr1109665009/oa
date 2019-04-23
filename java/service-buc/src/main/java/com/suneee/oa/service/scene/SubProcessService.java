package com.suneee.oa.service.scene;

import com.suneee.core.db.IEntityDao;
import com.suneee.oa.dao.scene.SubProcessDao;
import com.suneee.oa.model.scene.SubProcess;
import com.suneee.ucp.base.service.UcpBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SubProcessService extends UcpBaseService<SubProcess> {
    @Resource
    private SubProcessDao subProcessDao;
    @Override
    protected IEntityDao<SubProcess, Long> getEntityDao() {
        return this.subProcessDao;
    }

    public List<SubProcess> getBySceneId(Long sceneId){
        return subProcessDao.getBySceneId(sceneId);
    }

    public void delBySceneId(Long sceneId){
        List<SubProcess> subProcessList = this.getBySceneId(sceneId);
        if(subProcessList.size()>0){
            for (SubProcess subProcess:subProcessList){
                subProcessDao.delById(subProcess.getId());
            }
        }
    }

    public SubProcess getBySceneIdAndDefId(Long sceneId,Long defId){
        return subProcessDao.getBySceneIdAndDefId(sceneId,defId);
    }
}

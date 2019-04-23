/**
 * @Title: RelateProcessDao.java 
 * @Package com.suneee.eas.oa.dao.scene 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.dao.scene;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.scene.RelateProcess;

/**
 * @ClassName: RelateProcessDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-09-04 14:25:48 
 *
 */
@Repository
public class RelateProcessDao extends BaseDao<RelateProcess>{
	/** 批量新增场景相关流程
	 * @param relProcessList
	 * @return
	 */
	public int batchSave(List<RelateProcess> relProcessList){
		return this.getSqlSessionTemplate().insert(getNamespace() + ".batchSave", relProcessList);
	}
	/** 删除场景相关的流程
	 * @param sceneId
	 * @return
	 */
	public int delBySceneId(Long sceneId){
		return this.getSqlSessionTemplate().delete(getNamespace() + ".delBySceneId", sceneId);
	}
	
	/** 获取场景相关的流程信息
	 * @param sceneId
	 * @return
	 */
	public List<RelateProcess> getBySceneId(Long sceneId){
		return this.getSqlSessionTemplate().selectList(getNamespace() + ".getBySceneId", sceneId);
	}
}

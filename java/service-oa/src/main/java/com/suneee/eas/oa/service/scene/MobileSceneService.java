/**
 * @Title: MobileSceneService.java 
 * @Package com.suneee.eas.oa.service.scene 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.scene;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.scene.MobileScene;
import com.suneee.eas.oa.model.scene.SubProcess;

/**
 * @ClassName: MobileSceneService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-20 16:35:38 
 *
 */
public interface MobileSceneService extends BaseService<MobileScene>{
	public void delBySceneIds(Long[] idAry);

	public List<MobileScene> getByTypeId(Long typeId);
	
	public Long startSubProcess(SubProcess subProcess,Long runId) throws UnsupportedEncodingException;
	
	public String getNewFlowFormData(String formDataStr, JSONArray jsonMapping);
	
	public List<MobileScene> getByDefId(Long defId, Long sceneId);
}

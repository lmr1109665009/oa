/**
 * @Title: RelateProcessApiController.java 
 * @Package com.suneee.eas.oa.controller.scene 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.controller.scene;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.scene.MobileScene;
import com.suneee.eas.oa.model.scene.RelateProcess;
import com.suneee.eas.oa.service.scene.MobileSceneService;
import com.suneee.eas.oa.service.scene.RelateProcessService;

/**
 * @ClassName: RelateProcessApiController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-09-04 14:47:19 
 *
 */
@RestController
@RequestMapping("/api/mobilescene/relateProcess/")
public class RelateProcessApiController {
	private static final Logger LOGGER = LogManager.getLogger(RelateProcessApiController.class);
	@Autowired
	private RelateProcessService relateProcessService;
	@Autowired
	private MobileSceneService mobileSceneService;
	
	/** 获取场景相关流程
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getBySceneId")
	public ResponseMessage getBySceneId(HttpServletRequest request, HttpServletResponse response){
		Long sceneId = RequestUtil.getLong(request, "sceneId", null);
		if(sceneId == null){
			LOGGER.error("获取相关流程失败：场景ID为空！");
			return ResponseMessage.fail("获取相关流程失败：请求参数错误！");
		}
		try {
			// 查询场景信息
			MobileScene mobileScene = mobileSceneService.findById(sceneId);
			if(mobileScene == null){
				LOGGER.error("获取相关流程失败：场景ID为【" + sceneId + "】的场景记录不存在！");
				return ResponseMessage.fail("获取相关流程失败：场景不存在！");
			}
			
			// 查询场景的相关流程
			List<RelateProcess> list = relateProcessService.getBySceneId(sceneId);
			
			// 将场景中的流程加入相关流程列表
			RelateProcess relateProcess = new RelateProcess();
			relateProcess.setDefId(mobileScene.getDefId());
			relateProcess.setDefName(mobileScene.getDefName());
			relateProcess.setDefKey(mobileScene.getDefKey());
			relateProcess.setActDefId(mobileScene.getActDefId());
			relateProcess.setSceneId(mobileScene.getId());
			relateProcess.setRelImgPath(mobileScene.getImgPath());
			relateProcess.setSn(0);
			list.add(0, relateProcess);
			return ResponseMessage.success("获取相关流程列表成功！", list);
		} catch (Exception e) {
			LOGGER.error("获取相关流程失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取相关流程失败：系统内部错误！");
		}
	}
}

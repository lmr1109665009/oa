/**
 * @Title: BpmReferDefinitionApiController.java 
 * @Package com.suneee.common.controller 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.bpmx.controller.bpm;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.bpm.BpmReferDefinition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.service.bpm.BpmReferDefinitionService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.weixin.util.CommonUtil;

/**
 * @ClassName: BpmReferDefinitionApiController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-09-21 15:52:35 
 *
 */
@RestController
@RequestMapping("/api/bpm/bpmReferDefinition/")
public class BpmReferDefinitionApiController {
	private static final Logger LOGGER = LogManager.getLogger(BpmReferDefinitionApiController.class);
	@Autowired
	private BpmReferDefinitionService bpmReferDefinitionService;
	@Autowired
	private ProcessRunService processRunService;
	
	/** 获取引用流程列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getBpmDefinition")
	public ResponseMessage getBpmDefinition(HttpServletRequest request, HttpServletResponse response){
		Long defId = RequestUtil.getLong(request, "defId", null);
		boolean needPage = RequestUtil.getBoolean(request, "needPage", false);
		if(defId == null){
			LOGGER.error("获取引用流程列表失败：defId为空");
			return ResponseMessage.fail("获取引用流程列表失败：请求参数错误！");
		}
		try {
			// 查询流程下的引用流程
			List<BpmReferDefinition> list = bpmReferDefinitionService.getByDefId(defId);
			
			// 如果有引用流程，则查询设置的引用流程
			boolean hasRefer = (list.size() > 0);
			if(hasRefer){
				list = bpmReferDefinitionService.getAll(new QueryFilter(request, needPage));
			} 
			// 流程下没有引用流程，则查询系统所有的流程作为引用流程
			else {
				list = bpmReferDefinitionService.getAllDefinition(new QueryFilter(request, needPage));
			}
			JSONObject json = new JSONObject();
			json.put("hasRefer", hasRefer);
			if(needPage){
				json.put("referList", CommonUtil.getListModel((PageList<BpmReferDefinition>)list));
			} else {
				json.put("referList", list);
			}
			return ResponseMessage.success("获取引用流程列表成功！", json);
		} catch (Exception e) {
			LOGGER.error("获取引用流程列表失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取引用流程列表失败：系统内部错误！");
		}
	}
	
	/** 获取流程实例列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getActInstance")
	public ResponseMessage getActInstance(HttpServletRequest request, HttpServletResponse response){
		try {
			boolean hasRefer = RequestUtil.getBoolean(request, "hasRefer", false);
			boolean needPage = RequestUtil.getBoolean(request, "needPage", false);
			QueryFilter filter = new QueryFilter(request, needPage);
			filter.addFilter("creatorId", ContextSupportUtil.getCurrentUserId());
			List<ProcessRun> runList = null;
			// 如果设置了引用流程，则查询我申请的流程和抄送转发给我的办结流程
			if(hasRefer){
				runList = processRunService.getMyFlowsAndCptoList(filter);
			} 
			// 如果未设置引用流程，则查询我申请的办结流程和抄送转发给我的办结流程
			else {
				runList = processRunService.getMyCompletedAndCptoList(filter);
			}
			if(needPage){
				return ResponseMessage.success("获取引用流程实例列表成功！", CommonUtil.getListModel((PageList<ProcessRun>)runList));
			} else {
				return ResponseMessage.success("获取引用流程实例列表成功！", runList);
			}
		} catch (Exception e) {
			LOGGER.error("获取引用流程实例列表失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取引用流程实例列表失败：系统内部错误！");
		}
	}
}

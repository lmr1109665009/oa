/**
 * @Title: BpmMobileFormDefApiController.java 
 * @Package com.suneee.bpmx.controller.form 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.bpmx.controller.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.platform.model.form.BpmMobileFormDef;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmMobileFormDefService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.weixin.util.CommonUtil;

/**
 * @ClassName: BpmMobileFormDefApiController 
 * @Description: 手机表单控制器类
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-10-11 11:10:17 
 *
 */
@RestController
@RequestMapping("/api/form/bpmMobileFormDef/")
public class BpmMobileFormDefApiController {
	private static final Logger LOGGER = LogManager.getLogger(BpmMobileFormDefApiController.class);
	@Autowired
	private BpmMobileFormDefService bpmMobileFormDefService;
	@Autowired
	private GlobalTypeService globalTypeService;
	
	/** 获取手机表单列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("list")
	public ResponseMessage list(HttpServletRequest request, HttpServletResponse response){
		Long categoryId  = RequestUtil.getLong(request, "categoryId");
		QueryFilter filter = new QueryFilter(request);
		
		try {
			// 查询条件中不存在分类ID时，查询当前企业下的所有表单分类ID
			if(categoryId == 0){
				List<Long> typeIdList = globalTypeService.getTypeIdsByEcode(ContextSupportUtil.getCurrentEnterpriseCode());
				BpmUtil.typeIdFilter(typeIdList);
				filter.addFilter("typeIdList", typeIdList);
			} 
			// 查询条件中存在分类ID时，查询该分类及其子分类ID
			else {
				GlobalType globalType = globalTypeService.getById(categoryId);
				List<GlobalType> globalTypeList = globalTypeService.getByNodePath(globalType.getNodePath());
				List<Long> list = new ArrayList<>();
				for(GlobalType gt:globalTypeList){
					if(gt.getTypeId() != null){
						list.add(gt.getTypeId());
					}
				}
				filter.addFilter("typeIdList",list);
				filter.getFilters().remove("categoryId");
			}
			
			List<BpmMobileFormDef> list = bpmMobileFormDefService.getAll(filter);
			return ResponseMessage.success("获取手机表单列表成功！", CommonUtil.getListModel((PageList<BpmMobileFormDef>)list));
		} catch (Exception e) {
			LOGGER.error("获取手机表单列表失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取手机表单列表失败：系统内部错误！");
		}
	}
	
	/** 发布手机表单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("publish")
	public ResponseMessage publish(HttpServletRequest request, HttpServletResponse response){
		Long id = RequestUtil.getLong(request, "id", null);
		if(id == null){
			LOGGER.error("手机表单发布失败：手机表单ID为空！");
			return ResponseMessage.fail("手机表单发布失败：请求参数错误！");
		}
		try {
			BpmMobileFormDef def = bpmMobileFormDefService.getById(id);
			def.setIsPublished(1);
			def.setPublishBy(ContextSupportUtil.getCurrentUserId());
			def.setPublisher(ContextSupportUtil.getCurrentUsername());
			def.setPublishTime(new Date());
			bpmMobileFormDefService.update(def);
			return ResponseMessage.success("手机表单发布成功！");
		} catch (Exception e) {
			LOGGER.error("手机表单发布失败：" + e.getMessage(), e);
			return ResponseMessage.fail("手机表单发布失败：系统内部错误！");
		} 
	}
	
	/** 删除手机表单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("delByFormKey")
	public ResponseMessage delByFormKey(HttpServletRequest request, HttpServletResponse response){
		String[] formKeys= RequestUtil.getStringAryByStr(request, "formKey");
		if(formKeys == null){
			LOGGER.error("手机表单删除失败：手机表单Key为空！");
			return ResponseMessage.fail("手机表单删除失败：请求参数错误！");
		}
		
		try {
			List<String> formNames = bpmMobileFormDefService.delByFormKeys(formKeys);
			// 所有表单都没有被删除，则返回删除失败失败
			if(formNames.size() == formKeys.length){
				return ResponseMessage.fail("所选表单已关联流程，请先取消对应关联！");
			} 
			// 所有表单都成功被删除，则返回删除成功
			else if (formNames.size() == 0){
				return ResponseMessage.success("手机表单删除成功！");
			} 
			// 部分表单被删除，则返回删除成功，并指出未删除的表单名称
			else {
				return ResponseMessage.success(StringUtils.join(formNames.iterator(), ",") + "表单已关联流程，请先取消对应关联!");
			}
		} catch (Exception e) {
			LOGGER.error("手机表单删除失败：" + e.getMessage(), e);
			return ResponseMessage.fail("手机表单删除失败：系统内部错误！");
		}
	}
	
	/** 获取手机表单详情
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("details")
	public ResponseMessage details(HttpServletRequest request, HttpServletResponse response){
		Long id = RequestUtil.getLong(request, "id", null);
		if(id == null){
			LOGGER.error("获取手机表单详情失败：手机表单ID为空！");
			return ResponseMessage.fail("获取手机表单详情失败：请求参数错误！");
		}
		
		try {
			BpmMobileFormDef bpmMobileFormDef = bpmMobileFormDefService.getById(id);
			return ResponseMessage.success("获取手机表单详情成功！", bpmMobileFormDef);
		} catch (Exception e) {
			LOGGER.error("获取手机表单详情失败：" + e.getMessage(), e);

			return ResponseMessage.fail("获取手机表单详情失败：系统内部错误！");
		}
	}
}

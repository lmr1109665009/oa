package com.suneee.platform.controller.form;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.FileUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.util.FileUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.FormDefCombinate;
import com.suneee.platform.service.form.BpmDataTemplateService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.form.FormDefCombinateService;

/**
 * <pre>
 * 对象功能:form_def_combinate 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-05-20 09:55:36
 * </pre>
 */
@Controller
@RequestMapping("/platform/form/formDefCombinate/")
public class FormDefCombinateController extends BaseController {
	@Resource
	private FormDefCombinateService formDefCombinateService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	BpmDataTemplateService bpmDataTemplateService;
	/**
	 * 添加或更新form_def_combinate。
	 * 
	 * @param request
	 * @param response
	 * @param formDefCombinate
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新form_def_combinate")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resultMsg = null;
		String json = FileUtil.inputStream2String(request.getInputStream());
		FormDefCombinate formDefCombinate = JSONObjectUtil.toBean(json, FormDefCombinate.class);
		try {
			if (formDefCombinate.getId() == null || formDefCombinate.getId() == 0) {
				resultMsg = getText("添加", "form_def_combinate");
			} else {
				resultMsg = getText("更新", "form_def_combinate");
			}
			formDefCombinateService.save(formDefCombinate);
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			if (e instanceof DuplicateKeyException) {
				resultMsg = "别名已被使用：" + formDefCombinate.getAlias();
			} else {
				resultMsg += "," + e.getMessage();
			}
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 取得form_def_combinate分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看form_def_combinate分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<FormDefCombinate> list = formDefCombinateService.getAll(new QueryFilter(request, "formDefCombinateItem"));
		ModelAndView mv = this.getAutoView().addObject("formDefCombinateList", list);
		return mv;
	}

	/**
	 * 删除form_def_combinate
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除form_def_combinate")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			formDefCombinateService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除form_def_combinate成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	@RequestMapping("show_{alias}")
	@Action(description = "删除form_def_tree")
	public ModelAndView show(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "alias") String alias) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/platform/form/formDefCombinateShow");
		return mv.addObject("alias", alias);

	}
	
	@RequestMapping("ngjs")
	@Action(description = "ngjs的请求，分为参数和action，action是说明这次请求的目的")
	@ResponseBody
	public Object ngjs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String action = RequestUtil.getString(request, "action");
		if (action.equals("getTableById")) {
			Long tableId = RequestUtil.getLong(request, "tableId");
			return bpmFormTableService.getByTableId(tableId, 0);
		}
		if (action.equals("getById")) {
			Long id = RequestUtil.getLong(request, "id");
			return formDefCombinateService.getById(id);
		}
		if (action.equals("getTableByFormDefId")) {
			Long formDefId = RequestUtil.getLong(request, "formDefId");
			BpmFormDef bpmFormDef = bpmFormDefService.getById(formDefId); 
			return bpmFormTableService.getByTableId(bpmFormDef.getTableId(), 0);
		}
		if (action.equals("getTemplateByFormDefId")) {
			Long formDefId = RequestUtil.getLong(request, "formDefId");
			BpmFormDef bpmFormDef = bpmFormDefService.getById(formDefId); 
			return bpmDataTemplateService.getByFormKey(bpmFormDef.getFormKey());
		}
		if (action.equals("getByAlias")) {
			String alias=RequestUtil.getString(request, "alias");
			return formDefCombinateService.getByAlias(alias);
		}
		return null;
	}
}

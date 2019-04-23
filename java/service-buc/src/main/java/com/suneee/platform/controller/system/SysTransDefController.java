package com.suneee.platform.controller.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.FileUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
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
import com.suneee.platform.model.system.SysTransDef;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysTransDefService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.util.SysObjLogUtil;

/**
 * <pre>
 * 对象功能:sys_trans_def 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-04-16 11:15:55
 * </pre>
 */
@Controller
@RequestMapping("/platform/system/sysTransDef/")
public class SysTransDefController extends BaseController {
	@Resource
	private SysTransDefService sysTransDefService;
	@Resource
	private SysUserService sysUserService;

	/**
	 * 添加或更新sys_trans_def。
	 * 
	 * @param request
	 * @param response
	 * @param sysTransDef
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新sys_trans_def")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String json = FileUtil.inputStream2String(request.getInputStream());
		SysTransDef sysTransDef = JSONObjectUtil.toBean(json, SysTransDef.class);
		String resultMsg = null;
		try {
			if (sysTransDef.getId() == null || sysTransDef.getId() == 0) {
				resultMsg = getText("添加", "sys_trans_def");
			} else {
				resultMsg = getText("更新", "sys_trans_def");
			}
			sysTransDefService.save(sysTransDef);
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 取得sys_trans_def分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看sys_trans_def分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<SysTransDef> list = sysTransDefService.getAll(new QueryFilter(request, "sysTransDefItem"));
		ModelAndView mv = this.getAutoView().addObject("sysTransDefList", list);
		return mv;
	}

	/**
	 * 删除sys_trans_def
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除sys_trans_def")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			sysTransDefService.delByIds(lAryId);
			message = new ResultMessage(ResultMessage.Success, "删除sys_trans_def成功!");
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("getObject")
	@Action(description = "按各种参数查询sysTransDef")
	@ResponseBody
	public SysTransDef getObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id", null);
		if (id != null) {
			return sysTransDefService.getById(id);
		}
		return null;
	}

	@RequestMapping("treeListJson")
	@Action(description = "返回树格式，其实也是平铺")
	@ResponseBody
	public List<JSONObject> treeListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter qf = new QueryFilter(request, "sysTransDefItem");
		qf.addFilter("state", "1");
		List<SysTransDef> list = sysTransDefService.getAll(qf);
		String authorId = RequestUtil.getString(request, "authorId", "");
		return sysTransDefService.treeListJson(list, authorId);
	}

	@RequestMapping("excuteSelectSql")
	@Action(description = "执行select语句")
	@ResponseBody
	public List<Map<String, Object>> excuteSelectSql(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id", null);
		if (id == null) {
			return null;
		}
		String authorId = RequestUtil.getString(request, "authorId", "");
		SysTransDef sysTransDef = sysTransDefService.getById(id);
		
		List<Map<String, Object>> mapList = sysTransDefService.excuteSelectSql(sysTransDef, authorId);
		
		/**oracle 字段改成小写 */
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> dataMap : mapList) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			for (String key : dataMap.keySet()) {
				resultMap.put(key.toLowerCase(),dataMap.get(key));
			}
			resultList.add(resultMap);
		}
		
		return resultList;
	}

	@RequestMapping("excuteUpdateSql")
	@Action(description = "按各种参数查询sysTransDef")
	@ResponseBody
	public void excuteUpdateSql(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id", null);
		String authorId = RequestUtil.getString(request, "authorId", "");
		String targetPersonId = RequestUtil.getString(request, "targetPersonId", "");
		String selectedItem = RequestUtil.getString(request, "selectedItem", "");

		SysUser author = sysUserService.getById(Long.parseLong(authorId));
		SysUser targetPerson = sysUserService.getById(Long.parseLong(targetPersonId));
		
		SysTransDef sysTransDef = sysTransDefService.getById(id);
		try {
			sysTransDefService.excuteUpdateSql(sysTransDef, author, targetPerson, selectedItem);
			writeResultMessage(response.getWriter(), this.getText("操作成功"), ResultMessage.Success);
			String content = sysTransDefService.getLogContent(sysTransDef, authorId, targetPersonId, selectedItem);
			SysObjLogUtil.add("权限转移", content, SysObjLogController.OBJ_TYPE_SYSTRANSDEF);
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), "操作失败:"+e.getMessage(), ResultMessage.Fail);
		}
	}

}

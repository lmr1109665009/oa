package com.suneee.platform.controller.system;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.oa.service.user.SysOrgExtendService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysOrgService;

/**
 * 对象功能:数据字典 控制器类 开发公司:广州宏天软件有限公司 开发人员:ray 创建时间:2011-11-23 14:53:34
 */
@Controller
@RequestMapping("/platform/system/dictionary/")
@Action(ownermodel = SysAuditModelType.SYSTEM_SETTING)
public class DictionaryController extends BaseController {
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private SysOrgService orgService;
	@Resource
	private  SysOrgExtendService  sysOrgExtendService;

	
	@RequestMapping("edit")
	@Action(description = "添加或编辑数据字典", execOrder = ActionExecOrder.AFTER, detail = "<#if isAdd==1>添加数据字典<#else>编辑数据字典" + "<#assign entity=dictionaryService.getById(Long.valueOf(dicId))/>" + "【${entity.itemName}】</#if>")
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int isAdd = RequestUtil.getInt(request, "isAdd", 0);
		int isRoot = RequestUtil.getInt(request, "isRoot", 0);
		Long dicId = RequestUtil.getLong(request, "dicId", 0);//分类节点
		long canReturn = RequestUtil.getLong(request, "canReturn", 0);
		ModelAndView mv = getAutoView();
		Dictionary dictionary = null;
		if (isAdd == 1) {
			dictionary = new Dictionary();
			Long userId = ContextUtil.getCurrentUserId();
			SysOrg sysOrg = orgService.getByUserId(userId);
		    String eid = sysOrg.getOrgCode();
		    dictionary.setEid(eid);
			if (isRoot == 1) {
				GlobalType globalType = globalTypeService.getById(dicId);
				dictionary.setTypeId(dicId);
				dictionary.setParentId(dicId);
				dictionary.setType(globalType.getType());
			} else {
				Dictionary parentDic = dictionaryService.getById(dicId);
				dictionary.setParentId(dicId);
				dictionary.setTypeId(parentDic.getTypeId());
				dictionary.setType(parentDic.getType());
			}
		} else {
			dictionary = dictionaryService.getById(dicId);
		}
		mv.addObject("dictionary", dictionary).addObject("isAdd", isAdd).addObject("canReturn", canReturn);

		return mv;
	}

	/**
	 * 删除数据字典
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除数据字典", execOrder = ActionExecOrder.BEFORE, detail = "删除数据字典" + "<#list StringUtils.split(dicId,\",\") as item>" + "<#assign entity=dictionaryService.getById(Long.valueOf(item))/>" + "【${entity.itemName}】" + "</#list>")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		ResultMessage message = null;
		try {
			Long dicId = RequestUtil.getLong(request, "dicId");
			dictionaryService.delByDicId(dicId);
			message = new ResultMessage(ResultMessage.Success, "删除数据字典成功");
		} catch (Exception e) {
			message = new ResultMessage(ResultMessage.Fail, "删除数据字典失败");
		}
		writeResultMessage(out, message);
	}

	/**
	 * 排序数据字典列表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("sortList")
	@Action(description = "排序数据字典列表", detail = "排序数据字典列表")
	public ModelAndView sortList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long parentId = RequestUtil.getLong(request, "parentId", -1);
		List<Dictionary> list = dictionaryService.getByParentId(parentId);
		return getAutoView().addObject("dictionaryList", list);
	}

	/**
	 * 排序数据字典
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("sort")
	@Action(description = "数据字典排序", detail = "数据字典排序")
	public void sort(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage resultMessage = null;
		Long[] lAryId = RequestUtil.getLongAryByStr(request, "dicIds");
		try {
			dictionaryService.updSn(lAryId);
			resultMessage = new ResultMessage(ResultMessage.Success, "字典排序成功");
			writeResultMessage(response.getWriter(), resultMessage);
		} catch (Exception e) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = new ResultMessage(ResultMessage.Fail, "字典排序失败:" + str);
				response.getWriter().print(resultMessage);
			} else {
				String message = ExceptionUtil.getExceptionMessage(e);
				resultMessage = new ResultMessage(ResultMessage.Fail, message);
				response.getWriter().print(resultMessage);
			}
		}

	}

	/**
	 * 根据类型获取字典。
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("getByTypeId")
	@ResponseBody
	public List<Dictionary> getByTypeId(HttpServletRequest request) {
		Long typeId = RequestUtil.getLong(request, "typeId");
		Long userId = ContextUtil.getCurrentUserId();
		SysOrg sysOrg = orgService.getByUserId(userId);
	    String eid = sysOrg.getOrgCode();
	    List<Dictionary> list= dictionaryService.getBytypeIdAndEid(typeId,eid,true);		
		//List<Dictionary> list= dictionaryService.getByTypeId(typeId, true);
		return list;
	}

	/**
	 * 根据分类表中的nodekey获取数据字典数据。
	 * 
	 * @param request
	 */
	@RequestMapping("getByNodeKey")
	@ResponseBody
	public List<Dictionary> getByNodeKey(HttpServletRequest request) {
		String nodeKey = RequestUtil.getString(request, "nodeKey");
		List<Dictionary> list = dictionaryService.getByNodeKey(nodeKey);
		return list;
	}

	/**
	 * 根据nodekey获取分类对象和字典列表对象。
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("getMapByNodeKey")
	@ResponseBody
	public Map<String, Object> getMapByNodeKey(HttpServletRequest request) {
		String nodeKey = RequestUtil.getString(request, "nodeKey");
		Map<String, Object> map = new HashMap<String, Object>();
		GlobalType globalType = globalTypeService.getByDictNodeKey(nodeKey);
		if (globalType != null) {
			List<Dictionary> list = dictionaryService.getByTypeId(globalType.getTypeId(), false);
			map.put("globalType", globalType);
			map.put("dicList", list);
		}
		return map;
	}

	/**
	 * 移动字典。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("move")
	@Action(description = "移动字典", detail = "<#assign dragEntity=dictionaryService.getById(Long.valueOf(dragId))/>" + "<#assign targetEntity=dictionaryService.getById(Long.valueOf(targetId))/>" + "字典【${dragEntity.itemName}】转移到" + "字典【${targetEntity.itemName}】<#if moveType=='prev'>之前<#elseif moveType=='next'>之后<#else>之下</#if>")
	public void move(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultMessage resultMessage = null;
		PrintWriter out = response.getWriter();
		long targetId = RequestUtil.getLong(request, "targetId", 0);
		long dragId = RequestUtil.getLong(request, "dragId", 0);
		String moveType = RequestUtil.getString(request, "moveType");
		try {
			dictionaryService.move(targetId, dragId, moveType);
			resultMessage = new ResultMessage(ResultMessage.Success, "移动字典成功");
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage = new ResultMessage(ResultMessage.Fail, "移动字典失败:" + str);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = new ResultMessage(ResultMessage.Fail, message);
			}
		}
		out.print(resultMessage);
	}

	@RequestMapping("getDictionaryKey")
	public void getDictionaryKey(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dicName = RequestUtil.getString(request, "subject");
		String pingyin = PinyinUtil.getPinYinHeadCharFilter(dicName);
		writeResultMessage(response.getWriter(), pingyin, ResultMessage.Success);
	}


	@RequestMapping("listJson")
	@ResponseBody

	public ResultVo listJson(HttpServletRequest request, HttpServletResponse response) throws Exception{

		String  typeName= RequestUtil.getString(request,"typeName");
		Long typeId =  RequestUtil.getLong(request,"typeId");

		QueryFilter queryFilter = new QueryFilter(request, true);
		Map<String, Object> filter = queryFilter.getFilters();

		PageList<Dictionary> list= (PageList<Dictionary>)dictionaryService.getDictionarysByQueryFilter(queryFilter);

		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "成功",PageUtil.getPageVo(list));

	}

}

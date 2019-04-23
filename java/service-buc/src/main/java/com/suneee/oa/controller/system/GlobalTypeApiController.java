package com.suneee.oa.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.controller.mobile.MobileBaseController;
import com.suneee.platform.model.bpm.AuthorizeRight;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysTypeKey;
import com.suneee.platform.service.bpm.BpmDefAuthorizeService;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysTypeKeyService;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 对象功能:系统分类接口
 * 开发公司:深圳象羿
 * 开发人员:子华
 * 创建时间:2018-01-09 09:28:34
 */
@Controller
@RequestMapping("/api/system/globalType/")
@Action(ownermodel = SysAuditModelType.SYSTEM_SETTING)
public class GlobalTypeApiController extends MobileBaseController {
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private SysTypeKeyService sysTypeKeyService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService;

	/**
	 * 编辑分类
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description = "编辑系统分类", execOrder = ActionExecOrder.AFTER, detail = "编辑系统分类" + "<#assign entity=globalTypeService.getById(Long.valueOf(typeId))/>" + "【${entity.typeName}】")
	public ResultVo edit(HttpServletRequest request) throws Exception {
		Long typeId = RequestUtil.getLong(request, "typeId");
		GlobalType globalType = globalTypeService.getById(typeId);
		Map<String,Object> data=new HashMap<String, Object>();
		GlobalType parent=getParent(globalType);
		data.put("typeId",globalType.getTypeId());
		data.put("typeName",globalType.getTypeName());
		data.put("parentId",globalType.getParentId());
		data.put("parentName",parent.getTypeName());
		data.put("nodePath",globalType.getNodePath());
		data.put("sn",globalType.getSn());
		data.put("catKey",globalType.getCatKey());
		data.put("ecodes",globalType.getEnterpriseCode());
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取编辑系统分类成功",data);
	}

	/**
	 * 获取父级分类
	 * @param type
	 * @return
	 */
	private GlobalType getParent(GlobalType type){
		if (type.getDepth()>1){
			return globalTypeService.getById(type.getParentId());
		}
		SysTypeKey sysTypeKey=sysTypeKeyService.getByKey(type.getCatKey());
		GlobalType globalType=new GlobalType();
		globalType.setTypeName(sysTypeKey.getTypeName());
		globalType.setCatKey(sysTypeKey.getTypeKey());
		globalType.setParentId(0L);
		globalType.setIsParent("true");
		globalType.setTypeId(sysTypeKey.getTypeId());
		globalType.setType(sysTypeKey.getType());
		globalType.setNodePath(sysTypeKey.getTypeId() +".");
		return globalType;
	}

	/**
	 * 分类排序数据列表
	 * @param request
	 * @return
	 */
	@RequestMapping("sortList")
	@ResponseBody
	@Action(description = "编辑分类排序", execOrder = ActionExecOrder.AFTER, detail = "编辑分类排序,typeId=${typeId}")
	public ResultVo sortList(HttpServletRequest request){
		Long parentId = RequestUtil.getLong(request, "parentId", -1);
		QueryFilter filter=new QueryFilter(request,false);
		filter.addFilter("parentId",parentId);
		//获取当前用户企业编码
		String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
		filter.getFilters().put("enterpriseCode",enterpriseCode);
		filter.addFilter("orderField","sn");
		filter.addFilter("orderSeq","asc");
		List<GlobalType> list = globalTypeService.getByQueryfilter(filter);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取分类排序成功",list);
	}

	/**
	 * 分类排序数据列表
	 * @param request
	 * @return
	 */
	@RequestMapping("sortListWithRight")
	@ResponseBody
	@Action(description = "编辑分类排序", execOrder = ActionExecOrder.AFTER, detail = "编辑分类排序,typeId=${typeId}")
	public ResultVo sortListWithRight(HttpServletRequest request){
		Long parentId = RequestUtil.getLong(request, "parentId", -1);
		QueryFilter filter = new QueryFilter(request,false);
		filter.addFilter("parentId",parentId);
		filter.addFilter("enterpriseCode",CookieUitl.getCurrentEnterpriseCode());
		List<GlobalType> list = globalTypeService.getByQueryfilter(filter);
		//增加流程分管授权的启动权限分配查询判断
		Long userId = ContextUtil.getCurrentUserId();
		String isNeedRight = "";
		Map<String, AuthorizeRight> authorizeRightMap = null;
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			//获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId, BPMDEFAUTHORIZE_RIGHT_TYPE.START, false, false);
			//获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
		}
		filter.addFilter("isNeedRight", isNeedRight);
		List<GlobalType> resultList = new CopyOnWriteArrayList<>(list);
		for(GlobalType gt:resultList){
			//根据权限获取当前用户所能启动的流程
			List<BpmDefinition> bpmList = bpmDefinitionService.getMyDefList(filter, gt.getTypeId());
			if(bpmList.size() == 0){
				resultList.remove(gt);
			}
		}
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取分类排序成功",resultList);
	}

	/**
	 * 分类排序数据列表
	 * @param request
	 * @return
	 */
	@RequestMapping("sortListForGltype")
	@ResponseBody
	@Action(description = "编辑分类排序", execOrder = ActionExecOrder.AFTER, detail = "编辑分类排序,typeId=${typeId}")
	public ResultVo sortListForGltype(HttpServletRequest request){
		QueryFilter queryFilter = new QueryFilter(request,false);
		List<GlobalType> list = globalTypeService.getByQueryfilter(queryFilter);
		List<GlobalType> resultList = new ArrayList<>();
		for(GlobalType gt:list){
			//找出为字典分类的字典类型
			if(gt.getIsType() == 1){
				resultList.add(gt);
			}
		}
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取分类排序成功",resultList);
	}

	/**
	 * 根据catKey获取分类 。 catKey：分类key hasRoot：是否有根节点。1，有根节点，0，无根节点。
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("getByCatKey")
	@ResponseBody
	public ResultVo getByCatKey(HttpServletRequest request) {
		String catKey = RequestUtil.getString(request, "catKey");
		String nodePath=RequestUtil.getString(request,"nodePath");
		boolean hasRoot = RequestUtil.getInt(request, "hasRoot", 1) == 1;
		List<GlobalType> list = globalTypeService.getByCatKey(catKey, hasRoot,nodePath);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"",list);
	}

	/**
	 * 根据catKey获取分类 。 catKey：分类key hasRoot：是否有根节点。1，有根节点，0，无根节点。
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("getByCatKeyWithoutLastLeaf")
	@ResponseBody
	public ResultVo getByCatKeyWithoutLastLeaf(HttpServletRequest request) {
		String catKey = RequestUtil.getString(request, "catkey");
		String nodePath=RequestUtil.getString(request,"nodePath");
		boolean hasRoot = RequestUtil.getInt(request, "hasRoot", 1) == 1;
		List<GlobalType> list = globalTypeService.getByCatKeyWithoutLastLeaf(catKey, hasRoot,nodePath);
		List<GlobalType> resultList = new ArrayList<>();
		for(GlobalType gt:list){
			if(gt.getIsType() == 1){
				resultList.add(gt);
			}
		}
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"",resultList);
	}

	/**
	 * 删除系统分类。
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description = "删除系统分类", execOrder = ActionExecOrder.BEFORE, detail = "删除系统分类" + "<#assign entity=globalTypeService.getById(Long.valueOf(typeId))/>" + "【${entity.typeName}】")
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ResultVo resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除成功!");

		try {
			Long[] typeIds = RequestUtil.getLongAryByStr(request, "typeIds");
			for(Long typeId:typeIds){
				//判断该分类下是否有数据字典（针对数据字典分类DIC）
				GlobalType globalType = globalTypeService.getById(typeId);
				List<GlobalType> globalTypeList = globalTypeService.getByNodePath(globalType.getNodePath());
				int number = 0;
				for(GlobalType gt:globalTypeList){
					if(gt.getIsType() == 0 && gt.getTypeId().longValue()!=typeId.longValue()){
						number++;
					}
				}
				if(number > 0){
					resultMessage.setStatus(ResultVo.COMMON_STATUS_FAILED);
					resultMessage.setMessage("无法删除，请先删除该字典类下所有数据字典！");
					return resultMessage;
				}
				//判断分类下是否有关联数据
				QueryFilter filter=new QueryFilter(request,false);
				filter.addFilter("typeIdList",new Long[]{typeId});
				List<BpmDefinition> bpmDefinitionList=bpmDefinitionService.getAll(filter);
				if (bpmDefinitionList.size()>0){
					resultMessage.setStatus(ResultVo.COMMON_STATUS_FAILED);
					resultMessage.setMessage("无法删除，请先删除分类所关联的数据！");
					return resultMessage;
				}
			}
			globalTypeService.delByTypeIds(typeIds);
			return resultMessage;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				resultMessage.setStatus(ResultVo.COMMON_STATUS_FAILED);
				resultMessage.setMessage("删除分类失败:" + str);
				return resultMessage;
			} else {
				String message = ExceptionUtil.getExceptionMessage(e);
				resultMessage.setStatus(ResultVo.COMMON_STATUS_FAILED);
				resultMessage.setMessage(message);
				return resultMessage;
			}
		}
	}
}

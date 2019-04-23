package com.suneee.oa.controller.bpm;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.controller.mobile.MobileBaseController;
import com.suneee.platform.model.bpm.AuthorizeRight;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.service.bpm.BpmDefAuthorizeService;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <pre>
 * 对象功能:流程实例扩展控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2011-12-12 10:56:13
 * </pre>
 */
@Controller
@RequestMapping("/api/bpm/processRun/")
@Action(ownermodel = SysAuditModelType.PROCESS_MANAGEMENT)
public class ProcessRunApiController extends MobileBaseController{

    @Resource
    private ProcessRunService processRunService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private BpmService bpmService;
	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService;
	@Resource
	private GlobalTypeService globalTypeService;

    /**
     * 查看我发起的流程列表（外部调用）
     * @param map
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("myStart")
    @ResponseBody
    @Action(description = "查看我发起的流程列表")
    public Object myStart(@RequestBody Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter filter = new QueryFilter(request,true);
        filter.addFilter("creatorId", Long.valueOf(map.get("curUserId").toString()));
        //查询流程实例列表，过滤掉草稿状态的流程
        //filter.addFilter("isForm", ProcessRun.STATUS_FORM);
        List<ProcessRun> list = processRunService.getAll(filter);

        if (BeanUtils.isNotEmpty(list)) {
            for (ProcessRun processRun : list) {
                BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(processRun.getActDefId());
                if (bpmDefinition.getIsPrintForm() == 1) {
                    processRun.setIsPrintForm(bpmDefinition.getIsPrintForm());
                }
            }
        }
        return getPageList(list,filter);
    }

    /**
     * 取得流程实例扩展明细
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("get")
    @ResponseBody
    @Action(description = "查看流程实例扩展明细")
    public ProcessRun get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long runId = RequestUtil.getLong(request, "runId", 0L);
        String taskId = RequestUtil.getString(request, "taskId");
        ProcessRun processRun = null;
        // String actInstId="";
        if (runId != 0L) {
            processRun = processRunService.getById(runId);
        } else {
            TaskEntity task = bpmService.getTask(taskId);
            processRun = processRunService.getByActInstanceId(new Long(task.getProcessInstanceId()));
        }

        if (processRun == null)
            throw new Exception("实例不存在");
        return processRun;
    }

    /**
     * 任务追回（外部调用）
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("recover")
    @ResponseBody
    public ResultVo recover(@RequestBody Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long runId = Long.valueOf(map.get("runId").toString());
        String informType = map.get("informType").toString();
        String memo = map.get("opinion").toString();
        int backToStart = Integer.parseInt(map.get("backToStart").toString());
        ResultMessage resultMessage = null;
        ResultVo result = null;
        try {
            // 追回到发起人
            if(backToStart == 1){
                resultMessage = processRunService.checkRecoverByStart(runId);
            }else{
                // 追回
                resultMessage = processRunService.checkRecover(runId);
            }
            //可以撤销
            if(resultMessage.getResult() == 1){
                // 追回到发起人
                if (backToStart == 1) {
                    resultMessage = processRunService.redo(runId, informType, memo);
                } else {
                    // 追回
                    resultMessage = processRunService.recover(runId, informType, memo);
                }
            }
            int status = resultMessage.getResult() == ResultMessage.Success ? ResultVo.COMMON_STATUS_SUCCESS : ResultVo.COMMON_STATUS_FAILED;
            result = new ResultVo(status, resultMessage.getMessage());
        } catch (Exception ex) {
            String str = MessageUtil.getMessage();
            if (StringUtil.isNotEmpty(str)) {
                result = new ResultVo(ResultVo.COMMON_STATUS_FAILED, str);
            } else {
                String message = ExceptionUtil.getExceptionMessage(ex);
                result = new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
            }
        }
        return result;
    }

	/**
	 * 取得流程实例扩展分页列表
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("list")
	@ResponseBody
	@Action(description = "查看流程实例扩展分页列表")
	public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, true);
		// 过滤掉草稿实例
		filter.addFilter("exceptStatus", 4);
		// 过滤流程定义状态为"禁用实例" 的流程实例
		filter.addFilter("exceptDefStatus", 3);

		// 增加按新的流程分管授权中任务类型的权限获取流程的任务
		Long userId = ContextUtil.getCurrentUserId();
		String isNeedRight = "";
		Map<String, AuthorizeRight> authorizeRightMap = null;
		String actRights = "";
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId, BPMDEFAUTHORIZE_RIGHT_TYPE.INSTANCE, true, false);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
			// 获得流程分管授权与用户相关的信息集合的流程权限内容
			authorizeRightMap = (Map<String, AuthorizeRight>) actRightMap.get("authorizeRightMap");
		}
		filter.addFilter("isNeedRight", isNeedRight);
		if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
			String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
			filter.addFilter("subject",subject);
		}
		//通过typeId获取该分类下所有子类数据（流程定义），存放于list
		Long typeId = (Long) filter.getFilters().get("typeId");
		if(typeId != null ){
			List<BpmDefinition> defList = bpmDefinitionService.getMyDefList(filter, typeId);
			List<Long> list = new ArrayList<>();
			for(BpmDefinition bd:defList){
				if(bd.getTypeId() != null){
					list.add(bd.getTypeId());
				}
			}
			//list去重
			List<Long> typeIds = new ArrayList(new TreeSet(list));
			filter.addFilter("typeIds",typeIds);
		}else {
			List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, false);
			Set<Long> typeIdList= BpmUtil.getTypeIdList(globalTypeList);
			filter.addFilter("typeIds",typeIdList);
		}
		//按照查询条件查询数据
		List<ProcessRun> list = processRunService.getAll(filter);

		// 把前面获得的流程分管授权的权限内容设置到流程管理列表
		if (authorizeRightMap != null) {
			for (ProcessRun processRun : list) {
				processRun.setAuthorizeRight(authorizeRightMap.get(processRun.getFlowKey()));
			}
		} else {
			// 如果需要所有权限的就直接虚拟一个有处理权限的对象
			AuthorizeRight authorizeRight = new AuthorizeRight();
			authorizeRight.setRightByAuthorizeType("Y", BPMDEFAUTHORIZE_RIGHT_TYPE.INSTANCE);
			for (ProcessRun processRun : list) {
				processRun.setAuthorizeRight(authorizeRight);
			}
		}

		// 是否有全局流水号
		boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
		//处理流程实例持续时间
		for(ProcessRun pr:list){
			if(pr.getStatus() != 2 && pr.getStatus() != 3 && pr.getStatus() != 10){
				Long durationTime = TimeUtil.getTime(pr.getCreatetime(),new Date());
				pr.setDuration(durationTime);
			}
		}
		PageList<ProcessRun> resultList = (PageList<ProcessRun>) list;
		Map<String,Object> map = new HashMap<>();
		map.put("processRunList", PageUtil.getPageVo(resultList));
		map.put("hasGlobalFlowNo",hasGlobalFlowNo);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程实例列表数据成功！",map);
	}

	/**
	 * 删除流程实例扩展
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description = "删除流程实例扩展", execOrder = ActionExecOrder.BEFORE, detail = "删除流程实例" + "<#list StringUtils.split(runId,\",\") as item>" + "<#assign entity=processRunService.getById(Long.valueOf(item))/>" + "【${entity.subject}】" + "</#list>")
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultVo message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "runId");
			processRunService.delByIds(lAryId);
			message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除流程实例成功");
		} catch (Exception e) {
			message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除流程实例失败");
		}
		return message;
	}
}

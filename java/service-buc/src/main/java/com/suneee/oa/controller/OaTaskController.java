package com.suneee.oa.controller;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.bpm.model.ProcessTaskHistory;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.model.ForkTaskReject;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.page.PageList;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.controller.mobile.MobileBaseController;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.util.WarningSetting;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.BpmNodeSignService.BpmNodePrivilegeType;
import com.suneee.platform.service.bpm.impl.BpmActService;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.*;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysFileService;
import com.suneee.platform.service.system.SysPropertyService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import com.suneee.oa.Exception.TipInfoException;
import com.suneee.oa.dto.TaskDto;
import com.suneee.ucp.base.util.JsonUtils;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONArray;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 * 对象功能:流程模块接口
 * 开发公司:深圳象弈
 * 开发人员:pengfeng
 * 创建时间:2011-12-11 10:12:13
 * </pre>
 */
@Controller
@RequestMapping("/oa/oaTask/")
public class OaTaskController extends MobileBaseController {

    @Resource
    private BpmService bpmService;
	@Resource
	private SysUserService userService;
    @Resource
    private TaskReminderService reminderService;
    @Resource
    private ProcessRunService processRunService;
    @Resource
    private TaskOpinionService taskOpinionService;
    @Resource
    private BpmTaskExeService bpmTaskExeService;
    @Resource
    private BpmProTransToService bpmProTransToService;
    @Resource
    private BpmProCopytoService bpmProCopytoService;
    @Resource
    private BpmRunLogService bpmRunLogService;
    @Resource
    private AgentSettingService agentSettingService;
    @Resource
    private TaskHistoryService taskHistoryService;
    @Resource
    private TaskReadService taskReadService;
    @Resource
    private CommuReceiverService commuReceiverService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private BpmFormDefService bpmFormDefService;
    @Resource
    private BpmGangedSetService bpmGangedSetService;
    @Resource
    private TaskService taskService;
    @Resource
    private BpmNodeSetService bpmNodeSetService;
    @Resource
    private BpmFormHandlerService bpmFormHandlerService;
    @Resource
    private BpmActService bpmActService;
    @Resource
    private BpmNodeButtonService bpmNodeButtonService;
    @Resource
    private TaskApprovalItemsService taskAppItemService;
    @Resource
    private SysFileService sysFileService;
    @Resource
    private TaskSignDataService taskSignDataService;
    @Resource
    private BpmNodeSignService bpmNodeSignService;
    @Resource
    private SysPropertyService sysPropertyService;
    @Resource
    private ExecutionStackService executionStackService;
    @Resource
    private BpmBatchApprovalService bpmBatchApprovalService;
    @Resource
    private BpmDefAuthorizeService bpmDefAuthorizeService;
	@Resource
	private TaskUserService taskUserService;
	@Autowired
	private BpmFormTableService formTableService;
    @Autowired
    private BpmFormFieldService formFieldService;
    @Autowired
    private GlobalTypeService globalTypeService;

    /**
     * 待办事宜-接口数据
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("pendingMattersListJson")
    @ResponseBody
    public ResultVo MyToDo(HttpServletRequest request, HttpServletResponse response) throws Exception{
        JSONObject json = new JSONObject();
        QueryFilter filter = new QueryFilter(request, true);
        String nodePath = RequestUtil.getString(request, "nodePath");
        try {
            if (StringUtils.isNotEmpty(nodePath))
                filter.getFilters().put("nodePath", nodePath + "%");
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }

            flowCateFilter(filter);

			//处理表单自定义字段查询
			processFormQuery(request,filter);
			//按照查询条件查询数据
            List<TaskEntity> list = bpmService.getMyTasks(filter);
            ListModel model = CommonUtil.getListModel((PageList)list);
            Map<Integer, WarningSetting> warningSetMap = reminderService.getWaringSetMap();
            // 是否有全局流水号
            Map<String, WarningSetting> warningStr =new HashMap<>();
            for (Integer task : warningSetMap.keySet()) {
                warningStr.put(task.toString(),warningSetMap.get(task));
            }
            boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
            json.put("hasGlobalFlowNo", hasGlobalFlowNo);
            json.put("warningStr", warningStr);
            json.put("list", model);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"我的待办查询成功",json);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "我的待办查询失败", e);
        }
    }

    /**
     * 流程分类条件过滤
     * @param filter
     */
    private void flowCateFilter(QueryFilter filter) {
        Long typeId = (Long) filter.getFilters().get("typeId");
        if (typeId != null) {
            //通过typeId获取该分类下所有子类数据（流程定义），存放于list
            List<Long> typeIds = globalTypeService.subTypeIdByParent(typeId);
            if (typeIds.size() == 0) {
                typeIds.add(0L);
            }
            filter.addFilter("typeIds", typeIds);
        } else {
            List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, false);
            Set<Long> typeIdList = BpmUtil.getTypeIdList(globalTypeList);
            filter.addFilter("typeIds", typeIdList);
        }
    }

    /**
     * 处理表单自定义查询字段
     * @param request
     * @param filter
     */
    private void processFormQuery(HttpServletRequest request,QueryFilter filter){
        int isExt=RequestUtil.getInt(request,"isExt",0);
        if (isExt==0){
            return;
        }
        Long defId=RequestUtil.getLong(request,"defId");
        if (defId==null||defId==0){
            return;
        }
        List<BpmFormField> fieldList=formFieldService.getQueryVarByFlowDefId(defId);
       if (fieldList.size()==0){
           return;
       }
        BpmFormTable formTable=formTableService.getTableById(fieldList.get(0).getTableId());
        if (formTable==null){
            return;
        }
        Map<String,Object> params=new HashMap<String, Object>();
        for (BpmFormField formField:fieldList){
            String val=RequestUtil.getString(request,formField.getFieldName());
            if (StringUtil.isNotEmpty(val)){
                params.put(formField.getDbFieldName(),val);
            }
        }
        if (params.size()>0){
            filter.addFilter("formTable",formTable.getFactTableName());
            filter.addFilter("formParams",params);
        }
    }

    /**
     * 查看已办事宜流程列表--接口
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("alreadyMattersListJson")
    @ResponseBody
    public ResultVo alreadyMattersListJson(HttpServletRequest request,HttpServletResponse response) throws Exception{
        try {
            JSONObject json = new JSONObject();
            QueryFilter filter = new QueryFilter(request, true);
            String nodePath = RequestUtil.getString(request, "nodePath");// 右侧流程分类
            if (StringUtils.isNotEmpty(nodePath))
                filter.getFilters().put("nodePath", nodePath + "%");
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }

            filter.addFilter("assignee", ContextUtil.getCurrentUserId().toString());// 用户id

			flowCateFilter(filter);

            //处理表单自定义字段查询
            processFormQuery(request,filter);
			//按照查询条件查询数据
            List<ProcessRun> list = processRunService.getAlreadyMattersList(filter);
			//处理已办事宜持续时间
			for(ProcessRun pr:list){
				if(pr.getStatus() != 2 && pr.getStatus() != 3 && pr.getStatus() != 10){
					Long durationTime = TimeUtil.getTime(pr.getCreatetime(),new Date());
					pr.setDuration(durationTime);
				}
			}
            for (ProcessRun processRun : list) {
                if (processRun.getStatus().shortValue() != ProcessRun.STATUS_FINISH.shortValue()) {
                    // 1.查找当前用户是否有最新审批的任务
                    TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(processRun.getActInstId(), ContextUtil.getCurrentUserId());
                    if (BeanUtils.isNotEmpty(taskOpinion))
                        processRun.setRecover(ProcessRun.STATUS_RECOVER);
                }
            }
            ListModel model = CommonUtil.getListModel((PageList) list);
            // 是否有全局流水号
            boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
            json.put("hasGlobalFlowNo", hasGlobalFlowNo);
            json.put("alreadyMattersList", model);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "我的已办查询成功", json);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "我的已办查询失败", e);
        }
    }

    /**
     * 我的请求列表接口
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("myRequestListJson")
    @ResponseBody
    public ResultVo myRequestListJson(HttpServletRequest request,HttpServletResponse response) throws Exception{
        try {
            JSONObject json = new JSONObject();
            QueryFilter filter = new QueryFilter(request, true);
            filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
            String nodePath = RequestUtil.getString(request, "nodePath");
            if (StringUtils.isNotEmpty(nodePath))
                filter.getFilters().put("nodePath", nodePath + "%");
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }

            flowCateFilter(filter);

            //处理表单自定义字段查询
            processFormQuery(request,filter);
			//按照查询条件查询数据
            List<ProcessRun> list = processRunService.getMyRequestList(filter);
            ListModel model =CommonUtil.getListModel((PageList) list);

            // 是否有全局流水号
            boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
            json.put("hasGlobalFlowNo", hasGlobalFlowNo);
            json.put("myRequestList", model);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "我的未办结请求查询成功", json);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "我的未办结请求查询失败", e);
        }
    }

    /**
     * 我的办结--接口
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("myCompletedListJson")
    @ResponseBody
    public ResultVo myCompletedListJson(HttpServletRequest request,HttpServletResponse response) throws Exception{
        try {
            JSONObject json = new JSONObject();
            QueryFilter filter = new QueryFilter(request, true);
            filter.addFilter("creatorId", ContextUtil.getCurrentUserId());
            String nodePath = RequestUtil.getString(request, "nodePath");
            if (StringUtils.isNotEmpty(nodePath))
                filter.getFilters().put("nodePath", nodePath + "%");
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }

			flowCateFilter(filter);

            //处理表单自定义字段查询
            processFormQuery(request,filter);
			//按照查询条件查询数据
            List<ProcessRun> list = processRunService.getMyCompletedList(filter);
            ListModel model = CommonUtil.getListModel((PageList) list);
            // 是否有全局流水号
            boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
            json.put("hasGlobalFlowNo", hasGlobalFlowNo);
            json.put("myCompletedList", model);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "我的办结查询成功", json);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "我的办结查询失败", e);
        }
    }

    /**
     * 我的交办接口数据
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("accordingMattersListJson")
    @ResponseBody
    public ResultVo MyGiveToOther(HttpServletRequest request,HttpServletResponse response) throws Exception{
        QueryFilter filter = new QueryFilter(request, true);
        Long userId = ContextUtil.getCurrentUserId();
        filter.addFilter("ownerId", userId);
        try {
            JSONObject json = new JSONObject();
            String nodePath = RequestUtil.getString(request, "nodePath");
            if(StringUtils.isNotEmpty(nodePath))
                filter.getFilters().put("nodePath",nodePath + "%");
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }
            boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
            flowCateFilter(filter);
            //处理表单自定义字段查询
            processFormQuery(request,filter);
            List<BpmTaskExe> list = bpmTaskExeService.accordingMattersList(filter);
            ListModel model = CommonUtil.getListModel((PageList) list);
            json.put("hasGlobalFlowNo", hasGlobalFlowNo);
            json.put("taskList", model);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "我转发给别人的查询成功", json);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "我转发给别人的查询失败", e);
        }
    }

    /**
     * 流转事宜列表json
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("mattersListJson")
    @ResponseBody
    @Action(description = "流转事宜列表")
    public ResultVo mattersListJson(HttpServletRequest request,HttpServletResponse response) throws Exception{
        try {
            JSONObject json = new JSONObject();
            QueryFilter filter = new QueryFilter(request, true);
            String nodePath = RequestUtil.getString(request, "nodePath");
            if(StringUtils.isNotEmpty(nodePath))
                filter.getFilters().put("nodePath",nodePath + "%");
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }
            filter.addFilter("exceptDefStatus", 3);
            Long userId = ContextUtil.getCurrentUserId();
            filter.addFilter("createUserId", userId);
            flowCateFilter(filter);
            //处理表单自定义字段查询
            processFormQuery(request,filter);
            List<BpmProTransTo> list = bpmProTransToService.mattersList(filter);
            ListModel model = CommonUtil.getListModel((PageList) list);
            // 是否有全局流水号
            boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
            json.put("TransToList", model);
            json.put("hasGlobalFlowNo", hasGlobalFlowNo);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "流转给我查询成功", json);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "流转给我查询失败", e);
        }
    }

    /**
     * 查看我的流程抄送转发列表接口
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("copyListJson")
    @ResponseBody
    public ResultVo getMylist(HttpServletRequest request,HttpServletResponse response) throws Exception{
        try {
            JSONObject json = new JSONObject();
            QueryFilter filter = new QueryFilter(request,true);
            String nodePath = RequestUtil.getString(request, "nodePath");
            if(StringUtils.isNotEmpty(nodePath))
                filter.getFilters().put("nodePath",nodePath + "%");
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }
            filter.getFilters().put("ccUid", ContextUtil.getCurrentUserId());
            flowCateFilter(filter);
            //处理表单自定义字段查询
            processFormQuery(request,filter);
            List<BpmProCopyto> list=bpmProCopytoService.getMyList(filter);
            ListModel model = CommonUtil.getListModel((PageList) list);
            Long porIndex = RequestUtil.getLong(request,"porIndex");
            Long tabIndex = RequestUtil.getLong(request,"tabIndex");

            // 是否有全局流水号
            boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);
            json.put("tabIndex", tabIndex);
            json.put("porIndex", porIndex);
            json.put("hasGlobalFlowNo", hasGlobalFlowNo);
            json.put("copyList", model);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "转发抄送给我查询成功", json);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "转发抄送给我查询失败", e);
        }
    }

    /**
     * 删除流程抄送转发
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    @Action(description="删除流程抄送转发",
            execOrder= ActionExecOrder.BEFORE,
            detail="删除流程抄送转发:" +
                    "<#list RequestUtil.getLongAryByStr(request, \"copyId\") as item>" +
                    "<#assign entity=bpmProCopytoService.getById(item)>"+
                    "删除 【${SysAuditLinkService.getSysUserLink(entity.ccUid,entity.ccUname)}】对流程【${SysAuditLinkService.getProcessRunLink(entity.runId,entity.subject)}】任务抄送消息;" +
                    "</#list>"
    )
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        ResultVo message=null;
        try{
            Long[] lAryId =RequestUtil.getLongAryByStr(request, "copyId");
            bpmProCopytoService.delByIds(lAryId);
            message=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除成功!");
        }catch(Exception ex){
            message=new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败" + ex.getMessage());
        }
        return message;
    }

    /**
     * 取得当前用户的流程运行日志分页列表-接口
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("mylistJson")
    @ResponseBody
    public ResultVo mylistJson(HttpServletRequest request,HttpServletResponse response) throws Exception{
        try {
            JSONObject json = new JSONObject();
            Long userId=ContextUtil.getCurrentUserId();
            QueryFilter filter=new QueryFilter(request,true);
            filter.addFilter("userid", userId);
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("processSubject"))){
                String processSubject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("processSubject"));
                filter.addFilter("processSubject",processSubject);
            }
            //多企业条件
           /* List<Long> typeIdList=globalTypeService.getTypeIdsByEcode(CookieUitl.getCurrentEnterpriseCode());
            BpmUtil.typeIdFilter(typeIdList);*/
           
           String orgCode = CookieUitl.getCurrentEnterpriseCode();
           
            filter.addFilter("orgCode", orgCode);

            List<BpmRunLog> list=bpmRunLogService.getAll(filter);
           /* for (BpmRunLog runLog:list){
                ProcessRun processRun=processRunService.getById(runLog.getRunid());
                if(processRun != null){
					runLog.setGlobalFlowNo(processRun.getGlobalFlowNo());
				}
            }*/
            ListModel model = CommonUtil.getListModel((PageList) list);
            json.put("mylist", model);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取我的流程日志成功", json);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取我的流程日志失败", e);
        }
    }

    /**
     * 取得代理设定分页-接口数据
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("listJson")
    @ResponseBody
    public ResultVo listJson(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            JSONObject json = new JSONObject();
            QueryFilter filter = new QueryFilter(request,true);
            Long curUserId = ContextUtil.getCurrentUserId();
            filter.addFilter("authid", curUserId);
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }
            List<AgentSetting> list=agentSettingService.getAll(filter);
            ListModel model  = CommonUtil.getListModel((PageList) list);
            json.put("list", model);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取我的流程代理成功", json);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取我的流程日志失败", e);
        }
    }
    @RequestMapping("getMyCount")
    @Action(description = "我的各个流程状态统计")
    @ResponseBody
    public ResultVo getMyCount(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            Map<String,Object> dataMap=new HashMap<String, Object>();
            Long userId = ContextUtil.getCurrentUserId();
            List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, false);
            Set<Long> typeIdList = BpmUtil.getTypeIdList(globalTypeList);

            QueryFilter pendingFilter = new QueryFilter(request, false);
            pendingFilter.addFilter("typeIds", typeIdList);
            //待办数量
            List<TaskEntity> pendingCountList = bpmService.getMyTasks(pendingFilter);

            //我的已办
            QueryFilter myCompleteFilter = new QueryFilter(request, false);
            myCompleteFilter.addFilter("typeIds", typeIdList);
            myCompleteFilter.addFilter("assignee",userId);
            List<ProcessRun> matterList = processRunService.getAlreadyMattersList(myCompleteFilter);
            //我的申请
            QueryFilter myRequestFilter = new QueryFilter(request, false);
            myRequestFilter.addFilter("typeIds", typeIdList);
            myRequestFilter.addFilter("creatorId", userId);
            List<ProcessRun> myRequestList = processRunService.getMyRequestList(myRequestFilter);
            //我的办结
            List<ProcessRun> completedList = processRunService.getMyCompletedList(myRequestFilter);
            //我的草稿
            QueryFilter myDraftFilter = new QueryFilter(request, false);
            myDraftFilter.addFilter("typeIds", typeIdList);
            myDraftFilter.addFilter("userId",userId);
            List<ProcessRun> MyDraftList = processRunService.getMyDraft(myDraftFilter);
            //我的交办
            QueryFilter accordingFilter = new QueryFilter(request, false);
            accordingFilter.addFilter("typeIds", typeIdList);
            accordingFilter.addFilter("ownerId", userId);
            List<BpmTaskExe> accordingList = bpmTaskExeService.accordingMattersList(accordingFilter);
            //我的加签
            QueryFilter myGaveToFilter = new QueryFilter(request, false);
            myGaveToFilter.addFilter("typeIds", typeIdList);
            myGaveToFilter.addFilter("exceptDefStatus", 3);
            myGaveToFilter.addFilter("createUserId", userId);
            List<BpmProTransTo> myGaveTolist = bpmProTransToService.mattersList(myGaveToFilter);
            //他人转发
            QueryFilter copyListFilter = new QueryFilter(request, false);
            copyListFilter.addFilter("typeIds", typeIdList);
            copyListFilter.getFilters().put("ccUid", userId);
            List<BpmProCopyto> myCopyList=bpmProCopytoService.getMyList(copyListFilter);
            dataMap.put("pendingCount",pendingCountList.size());
            dataMap.put("completedCount",completedList.size());
            dataMap.put("matterCount",matterList.size());
            dataMap.put("myRequestCount",myRequestList.size());
            dataMap.put("MyDraftCount",MyDraftList.size());
            dataMap.put("accordingCount",accordingList.size());
            dataMap.put("myGaveToCount",myGaveTolist.size());
            dataMap.put("myCopyCount",myCopyList.size());
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取请求成功",dataMap);
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取请求失败",e);
        }
    }


	/**
	 * 启动任务界面。 根据任务ID获取流程实例，根据流程实例获取表单数据。
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("toStart")
	@ResponseBody
	public ResultVo toStart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			Map<String,Object> map = new HashMap<>();
			map = getToStartView(request, response, map, 0);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"启动任务界面数据请求成功！",map);
		}catch (TipInfoException tie){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,tie.getMessage());
		}
	}



    /**
     * 管理员使用的页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("doNext")
    @ResponseBody
    public ResultVo doNext(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try{
            Map<String,Object> map = new HashMap<>();
            map = getToStartView(request, response, map, 1);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"数据请求成功！",map);
        }catch (TipInfoException tie){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,tie.getMessage());
        }
    }


    /**
     * 启动任务界面（前台）
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("toStartForFront")
    public ModelAndView toStartForFront(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = this.getAutoView();
        ModelAndView modelAndView = getToStartViewForFront(request, response, mv, 0);
        modelAndView.setViewName("/oa/taskToStartForFront");
        return modelAndView;
    }

    /**
     * 流程启动页面（修改这个方法请修改手机的页面MobileTaskController.getMyTaskForm()）
     *
     * @param request
     * @param response
     * @param map
     * @param isManage
     * @return
     * @throws Exception
     */
    private Map<String,Object> getToStartView(HttpServletRequest request, HttpServletResponse response, Map<String,Object> map, int isManage) throws Exception {
        String ctxPath = request.getContextPath();
        SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
        String taskId = RequestUtil.getString(request, "taskId");
        String instanceId = RequestUtil.getString(request, "instanceId");

        if (StringUtil.isEmpty(taskId) && StringUtil.isEmpty(instanceId)) {
            throw new TipInfoException("没有输入任务或实例ID!");
        }

        // 根据流程实例获取流程任务。
        if (StringUtil.isNotEmpty(instanceId)) {
            List<ProcessTask> list = bpmService.getTasks(instanceId);
            if (BeanUtils.isNotEmpty(list)) {
                taskId = list.get(0).getId();
            }
        }
        // 查找任务节点
        TaskEntity taskEntity = bpmService.getTask(taskId);


		if (taskEntity == null) {
            ProcessTaskHistory taskHistory = taskHistoryService.getById(Long.valueOf(taskId));
            if (taskHistory == null) {
                if (StringUtil.isEmpty(taskId) && StringUtil.isEmpty(instanceId)) {
					throw new TipInfoException("任务ID错误!");
                }
            }
            String actInstId = taskHistory.getProcessInstanceId();
            if (StringUtils.isEmpty(actInstId) && taskHistory.getDescription().equals(TaskOpinion.STATUS_COMMUNICATION.toString())) {
				throw new TipInfoException("此任务为沟通任务,并且此任务已经处理!");
            }
            ProcessRun processRun = processRunService.getByActInstanceId(new Long(actInstId));
            if (processRun == null) {// 实例数据已被删除
				throw new TipInfoException("任务不存在!");
            }
            if(processRun.getStatus()==ProcessRun.STATUS_RECOVER){//流程状态为已撤回，抛出流程已撤回
                throw new TipInfoException("该流程已撤回，请刷新待办列表!");
            }
            String url = request.getContextPath() + "/platform/bpm/processRun/info.ht?link=1&runId=" + processRun.getRunId();
            //response.sendRedirect(url);
            throw new TipInfoException("此任务已经处理!");
        }

        if (TaskOpinion.STATUS_TRANSTO_ING.toString().equals(taskEntity.getDescription()) && taskEntity.getAssignee().equals(sysUser.getUserId().toString())) {
			throw new TipInfoException("对不起,这个任务正在流转中,不能处理此任务!");
        }

        instanceId = taskEntity.getProcessInstanceId();

        if (isManage == 0) {
            boolean hasRights = processRunService.getHasRightsByTask(new Long(taskEntity.getId()), sysUser.getUserId());
            if (!hasRights) {
				throw new TipInfoException("对不起,你不是这个任务的执行人,不能处理此任务!");
            }
        }
        // 更新任务为已读。
        taskReadService.saveReadRecord(Long.parseLong(instanceId), Long.parseLong(taskId));
        // 设置沟通人员或流转人员查看状态。
        commuReceiverService.setCommuReceiverStatus(taskEntity, sysUser);

        String nodeId = taskEntity.getTaskDefinitionKey();
        String actDefId = taskEntity.getProcessDefinitionId();
        Long userId = ContextUtil.getCurrentUserId();

        BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
        ProcessRun processRun = processRunService.getByActInstanceId(new Long(instanceId));

        Long defId = bpmDefinition.getDefId();

        /**
         * 使用API调用的时候获取表单的url进行跳转。
         */
        if (bpmDefinition.getIsUseOutForm() == 1) {
            String formUrl = bpmFormDefService.getFormUrl(taskId, actDefId, nodeId, processRun.getBusinessKey(), ctxPath);
            if (StringUtils.isEmpty(formUrl)) {
				throw new TipInfoException("请设置API调用时表单的url!");
            }
            //response.sendRedirect(formUrl);
            map.put("formUrl",formUrl);
            return map;
        }

        // 通过defid和nodeId获取联动设置
        List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefIdAndNodeId(defId, nodeId);
        JSONArray gangedSetJarray =  JSONArray.fromObject(JsonUtils.toJson(bpmGangedSets));

        Map<String, Object> variables = taskService.getVariables(taskId);

        String parentActDefId = "";
        if (variables.containsKey(BpmConst.FLOW_PARENT_ACTDEFID)) {// 判断当前是否属于子流程任务
            parentActDefId = variables.get(BpmConst.FLOW_PARENT_ACTDEFID).toString();
        }
        BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);

        String toBackNodeId = "";
        if (StringUtil.isNotEmpty(processRun.getStartNode())) {
            toBackNodeId = processRun.getStartNode();
        } else {
            toBackNodeId = NodeCache.getFirstNodeId(actDefId).getNodeId();
        }
        String form = "";

        Long tempLaunchId = userId;
        // 在沟通和加签的时候 把当前用户对于当前表单的权限设置为传递者的权限。
        if (StringUtils.isEmpty(taskEntity.getExecutionId())) {
            if (taskEntity.getDescription().equals(TaskOpinion.STATUS_TRANSTO.toString())) {
                List<TaskOpinion> taskOpinionList = taskOpinionService.getByActInstId(instanceId);
                if (BeanUtils.isNotEmpty(taskOpinionList)) {
                    TaskOpinion taskOpinion = taskOpinionList.get(taskOpinionList.size() - 1);
                    List<CommuReceiver> commuReceiverList = commuReceiverService.getByOpinionId(taskOpinion.getOpinionId());
                    if (BeanUtils.isNotEmpty(commuReceiverList)) {
                        tempLaunchId = taskOpinion.getExeUserId();
                    }
                }
            }
        }

        FormModel formModel = bpmFormDefService.getNodeForm(processRun, nodeId, tempLaunchId, ctxPath, variables, false);
        // 如果是沟通任务 那么不允许沟通者有编辑表单的权限
        if (taskEntity.getDescription().equals(TaskOpinion.STATUS_COMMUNICATION.toString())) {
            BpmFormDef bpmFormDef = null;
            if(StringUtil.isNotEmpty(bpmNodeSet.getFormKey())){
                bpmFormDef=bpmFormDefService.getDefaultPublishedByFormKey(bpmNodeSet.getFormKey());
            }
            form = bpmFormHandlerService.obtainHtml(bpmFormDef,  processRun.getBusinessKey(),
                    instanceId, actDefId, nodeId , ctxPath, parentActDefId, false,true,true,(short)0) ;
            formModel.setFormHtml(form);
        }
        if (!formModel.isValid()) {
			throw new TipInfoException("流程定义的流程表单发生了更改,数据无法显示!");
        }

        String detailUrl = formModel.getDetailUrl();

        Boolean isExtForm = (Boolean) (formModel.getFormType() > 0);

        if (formModel.getFormType() == 0)
            form = formModel.getFormHtml();
        else
            form = formModel.getFormUrl();

        Boolean isEmptyForm = formModel.isFormEmpty();

        // 是否会签任务
        boolean isSignTask = bpmService.isSignTask(taskEntity);
        if (isSignTask) {
            map = handleSignTask(map, instanceId, nodeId, actDefId, userId);
        }

        // 是否支持回退
        boolean isCanBack = bpmActService.isTaskAllowBack(taskId);
        //不支持回退时判断同步条件中是否可以自由回退
        boolean isCanFreeback=false;
        if (isCanBack){
            Map<String,FlowNode> preCacheMap=new HashMap<String,FlowNode>();
            Map<String,FlowNode> nextCacheMap=new HashMap<String,FlowNode>();
            FlowNode node=NodeCache.getFlowNode(taskEntity.getProcessDefinitionId(),taskEntity.getTaskDefinitionKey());
            boolean isSyncNode=NodeCache.isPreParallelGateway(preCacheMap,node)&&NodeCache.isNextParallelGateway(nextCacheMap,node);
            if (isSyncNode){
                isCanBack=false;
            }
            if (isSyncNode&&preCacheMap.size()>1){
                isCanFreeback=true;
            }
        }
        // 是否汇总节点
        boolean gatherNode = bpmActService.isGatherNode(taskId);
        // 是否转办
        boolean isCanAssignee = bpmTaskExeService.isAssigneeTask(taskEntity, bpmDefinition);

        // 是否执行隐藏路径
        boolean isHidePath = getIsHidePath(bpmNodeSet.getIsHidePath());

        // 是否是执行选择路径跳转
        boolean isHandChoolse = false;
        if (!isHidePath) {
            boolean canChoicePath = bpmService.getCanChoicePath(actDefId, taskId);
            Long startUserId = ContextUtil.getCurrentUserId();
            List<NodeTranUser> nodeTranUserList = bpmService.getNodeTaskUserMap(taskId, startUserId, canChoicePath);
            if (nodeTranUserList.size() > 1) {
                isHandChoolse = true;
            }
        }

        // 获取页面显示的按钮
        Map<String, List<BpmNodeButton>> mapButton = bpmNodeButtonService.getMapByDefNodeId(defId, nodeId);

        // 取常用语
        List<String> taskAppItems = taskAppItemService.getApprovalByDefKeyAndTypeId(bpmDefinition.getDefKey(), bpmDefinition.getTypeId());
        // 获取保存的意见
        TaskOpinion taskOpinion = taskOpinionService.getOpinionByTaskId(Long.parseLong(taskId), userId);

        // 帮助文档
        SysFile sysFile = null;
        if (BeanUtils.isNotEmpty(bpmDefinition.getAttachment())) {
            sysFile = sysFileService.getById(bpmDefinition.getAttachment());
        }

        // 是否有全局流水号
        boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);

		//创建一个Dto对象用于接收TaskEntity参数
		TaskDto taskDto = new TaskDto();
		TaskDto taskDtoResult = copyData(taskDto,taskEntity);

		// 消息类型
		Map<String, IMessageHandler> handlersMap = this.getMessageType(bpmDefinition);
		List<String> handlersList = new ArrayList<>();
		for(String s:handlersMap.keySet()){
			handlersList.add(s);
		}

		//截取出表单权限
        String var = StringUtils.substringBetween(form, "permission", "</script>");
        String pression = var.split("=")[1];
        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(pression);

        map.put("task",taskDtoResult);
        map.put("bpmNodeSet",bpmNodeSet);
        map.put("processRun",processRun);
        map.put("bpmDefinition",bpmDefinition);
        map.put("isSignTask",isSignTask);
        map.put("isCanBack",isCanBack);
        map.put("isCanFreeback",isCanFreeback);
        map.put("gatherNode",gatherNode);
        map.put("isCanAssignee",isCanAssignee);
        map.put("isHidePath",isHidePath);
        map.put("toBackNodeId",toBackNodeId);
        map.put("form",form);
        map.put("pression",jsonObject);
        map.put("isExtForm",isExtForm);
        map.put("isEmptyForm",isEmptyForm);
        map.put("taskAppItems",taskAppItems);
        map.put("mapButton",mapButton);
        map.put("detailUrl",detailUrl);
        map.put("isManage",isManage);
        map.put("bpmGangedSets",gangedSetJarray);
        map.put("sysFile",sysFile);
        map.put("taskOpinion",taskOpinion);
        map.put("isHandChoolse",isHandChoolse);
        map.put("curUserId",sysUser.getUserId().toString());
        map.put("curUserName",sysUser.getFullname());
        map.put("hasGlobalFlowNo",hasGlobalFlowNo);
        map.put("formKey",bpmNodeSet.getFormKey());
        map.put("handlersMap",handlersList);

        //正常跳转
        boolean isNormalPath=false;
        //选择路径跳转
        boolean isSelectPath=false;
        //自由跳转
        boolean isFreePath=false;
        if (StringUtil.isNotEmpty(bpmNodeSet.getJumpType())){
            if (bpmNodeSet.getJumpType().indexOf("1")>-1){
                isNormalPath=true;
            }
            if (bpmNodeSet.getJumpType().indexOf("2")>-1){
                isSelectPath=true;
            }
            if (bpmNodeSet.getJumpType().indexOf("3")>-1){
                isFreePath=true;
            }
        }
        map.put("isNormalPath",isNormalPath);
        map.put("isSelectPath",isSelectPath);
        map.put("isFreePath",isFreePath);
        return map;
    }


    /**
     * 流程启动页面（前台）
     * @param request
     * @param response
     * @param mv
     * @param isManage
     * @return
     * @throws Exception
     */
    private ModelAndView getToStartViewForFront(HttpServletRequest request, HttpServletResponse response, ModelAndView mv, int isManage) throws Exception {
        String ctxPath = request.getContextPath();
        SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
        String taskId = RequestUtil.getString(request, "taskId");
        String instanceId = RequestUtil.getString(request, "instanceId");

        if (StringUtil.isEmpty(taskId) && StringUtil.isEmpty(instanceId)) {
            return ServiceUtil.getTipInfo("没有输入任务或实例ID!");
        }

        // 根据流程实例获取流程任务。
        if (StringUtil.isNotEmpty(instanceId)) {
            List<ProcessTask> list = bpmService.getTasks(instanceId);
            if (BeanUtils.isNotEmpty(list)) {
                taskId = list.get(0).getId();
            }
        }
        // 查找任务节点
        TaskEntity taskEntity = bpmService.getTask(taskId);

        if (taskEntity == null) {
            ProcessTaskHistory taskHistory = taskHistoryService.getById(Long.valueOf(taskId));
            if (taskHistory == null) {
                if (StringUtil.isEmpty(taskId) && StringUtil.isEmpty(instanceId)) {
                    return ServiceUtil.getTipInfo("任务ID错误!");
                }
            }
            String actInstId = taskHistory.getProcessInstanceId();
            if (StringUtils.isEmpty(actInstId) && taskHistory.getDescription().equals(TaskOpinion.STATUS_COMMUNICATION.toString())) {
                return ServiceUtil.getTipInfo("此任务为沟通任务,并且此任务已经处理!");
            }
            ProcessRun processRun = processRunService.getByActInstanceId(new Long(actInstId));
            if (processRun == null) {// 实例数据已被删除
                return ServiceUtil.getTipInfo("任务不存在!");
            }
            String url = request.getContextPath() + "/platform/bpm/processRun/info.ht?link=1&runId=" + processRun.getRunId();
            response.sendRedirect(url);
            return null;
        }

        if (TaskOpinion.STATUS_TRANSTO_ING.toString().equals(taskEntity.getDescription()) && taskEntity.getAssignee().equals(sysUser.getUserId().toString())) {
            return ServiceUtil.getTipInfo("对不起,这个任务正在流转中,不能处理此任务!");
        }

        instanceId = taskEntity.getProcessInstanceId();

        if (isManage == 0) {
            boolean hasRights = processRunService.getHasRightsByTask(new Long(taskEntity.getId()), sysUser.getUserId());
            if (!hasRights) {
                return ServiceUtil.getTipInfo("对不起,你不是这个任务的执行人,不能处理此任务!");
            }
        }
        // 更新任务为已读。
        taskReadService.saveReadRecord(Long.parseLong(instanceId), Long.parseLong(taskId));
        // 设置沟通人员或流转人员查看状态。
        commuReceiverService.setCommuReceiverStatus(taskEntity, sysUser);

        String nodeId = taskEntity.getTaskDefinitionKey();
        String actDefId = taskEntity.getProcessDefinitionId();
        Long userId = ContextUtil.getCurrentUserId();

        BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
        ProcessRun processRun = processRunService.getByActInstanceId(new Long(instanceId));

        Long defId = bpmDefinition.getDefId();

        /**
         * 使用API调用的时候获取表单的url进行跳转。
         */
        if (bpmDefinition.getIsUseOutForm() == 1) {
            String formUrl = bpmFormDefService.getFormUrl(taskId, actDefId, nodeId, processRun.getBusinessKey(), ctxPath);
            if (StringUtils.isEmpty(formUrl)) {
                ModelAndView rtnModel = ServiceUtil.getTipInfo("请设置API调用时表单的url!");
                return rtnModel;
            }
            response.sendRedirect(formUrl);
        }

        // 通过defid和nodeId获取联动设置
        List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefIdAndNodeId(defId, nodeId);
        JSONArray gangedSetJarray = (JSONArray) JSONArray.fromObject(bpmGangedSets);

        Map<String, Object> variables = taskService.getVariables(taskId);

        String parentActDefId = "";
        if (variables.containsKey(BpmConst.FLOW_PARENT_ACTDEFID)) {// 判断当前是否属于子流程任务
            parentActDefId = variables.get(BpmConst.FLOW_PARENT_ACTDEFID).toString();
        }
        BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);

        String toBackNodeId = "";
        if (StringUtil.isNotEmpty(processRun.getStartNode())) {
            toBackNodeId = processRun.getStartNode();
        } else {
            toBackNodeId = NodeCache.getFirstNodeId(actDefId).getNodeId();
        }
        String form = "";

        Long tempLaunchId = userId;
        // 在沟通和加签的时候 把当前用户对于当前表单的权限设置为传递者的权限。
        if (StringUtils.isEmpty(taskEntity.getExecutionId())) {
            if (taskEntity.getDescription().equals(TaskOpinion.STATUS_TRANSTO.toString())) {
                List<TaskOpinion> taskOpinionList = taskOpinionService.getByActInstId(instanceId);
                if (BeanUtils.isNotEmpty(taskOpinionList)) {
                    TaskOpinion taskOpinion = taskOpinionList.get(taskOpinionList.size() - 1);
                    List<CommuReceiver> commuReceiverList = commuReceiverService.getByOpinionId(taskOpinion.getOpinionId());
                    if (BeanUtils.isNotEmpty(commuReceiverList)) {
                        tempLaunchId = taskOpinion.getExeUserId();
                    }
                }
            }
        }

        FormModel formModel = bpmFormDefService.getNodeForm(processRun, nodeId, tempLaunchId, ctxPath, variables, false);
        // 如果是沟通任务 那么不允许沟通者有编辑表单的权限
        if (taskEntity.getDescription().equals(TaskOpinion.STATUS_COMMUNICATION.toString())) {
            BpmFormDef bpmFormDef = null;
            if(StringUtil.isNotEmpty(bpmNodeSet.getFormKey())){
                bpmFormDef=bpmFormDefService.getDefaultPublishedByFormKey(bpmNodeSet.getFormKey());
            }
            form = bpmFormHandlerService.obtainHtml(bpmFormDef,  processRun.getBusinessKey(),
                    instanceId, actDefId, nodeId , ctxPath, parentActDefId, false,true,true,(short)0) ;
            formModel.setFormHtml(form);
        }
        if (!formModel.isValid()) {
            ModelAndView rtnModel = ServiceUtil.getTipInfo("流程定义的流程表单发生了更改,数据无法显示!");
            return rtnModel;
        }

        String detailUrl = formModel.getDetailUrl();

        Boolean isExtForm = (Boolean) (formModel.getFormType() > 0);

        if (formModel.getFormType() == 0)
            form = formModel.getFormHtml();
        else
            form = formModel.getFormUrl();

        Boolean isEmptyForm = formModel.isFormEmpty();

        // 是否会签任务
        boolean isSignTask = bpmService.isSignTask(taskEntity);
        if (isSignTask) {
            handleSignTask(mv, instanceId, nodeId, actDefId, userId);
        }

        // 是否支持回退
        boolean isCanBack = bpmActService.isTaskAllowBack(taskId);
        //不支持回退时判断同步条件中是否可以自由回退
        boolean isCanFreeback=false;
        if (isCanBack){
            Map<String,FlowNode> preCacheMap=new HashMap<String,FlowNode>();
            Map<String,FlowNode> nextCacheMap=new HashMap<String,FlowNode>();
            FlowNode node=NodeCache.getFlowNode(taskEntity.getProcessDefinitionId(),taskEntity.getTaskDefinitionKey());
            boolean isSyncNode=NodeCache.isPreParallelGateway(preCacheMap,node)&&NodeCache.isNextParallelGateway(nextCacheMap,node);
            if (isSyncNode){
                isCanBack=false;
            }
            if (isSyncNode&&preCacheMap.size()>1){
                isCanFreeback=true;
            }
        }
        // 是否汇总节点
        boolean gatherNode = bpmActService.isGatherNode(taskId);
        // 是否转办
        boolean isCanAssignee = bpmTaskExeService.isAssigneeTask(taskEntity, bpmDefinition);

        // 是否执行隐藏路径
        boolean isHidePath = getIsHidePath(bpmNodeSet.getIsHidePath());

        // 是否是执行选择路径跳转
        boolean isHandChoolse = false;
        if (!isHidePath) {
            boolean canChoicePath = bpmService.getCanChoicePath(actDefId, taskId);
            Long startUserId = ContextUtil.getCurrentUserId();
            List<NodeTranUser> nodeTranUserList = bpmService.getNodeTaskUserMap(taskId, startUserId, canChoicePath);
            if (nodeTranUserList.size() > 1) {
                isHandChoolse = true;
            }
        }

        // 获取页面显示的按钮
        Map<String, List<BpmNodeButton>> mapButton = bpmNodeButtonService.getMapByDefNodeId(defId, nodeId);

        // 取常用语
        List<String> taskAppItems = taskAppItemService.getApprovalByDefKeyAndTypeId(bpmDefinition.getDefKey(), bpmDefinition.getTypeId());
        // 获取保存的意见
        TaskOpinion taskOpinion = taskOpinionService.getOpinionByTaskId(Long.parseLong(taskId), userId);

        // 帮助文档
        SysFile sysFile = null;
        if (BeanUtils.isNotEmpty(bpmDefinition.getAttachment())) {
            sysFile = sysFileService.getById(bpmDefinition.getAttachment());
        }

        // 是否有全局流水号
        boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);

        return mv.addObject("task", taskEntity)
                .addObject("bpmNodeSet", bpmNodeSet)
                .addObject("processRun", processRun)
                .addObject("bpmDefinition", bpmDefinition)
                .addObject("isSignTask", isSignTask)
                .addObject("isCanBack", isCanBack)
                .addObject("isCanFreeback","isCanFreeback")
                .addObject("gatherNode",gatherNode)
                .addObject("isCanAssignee", isCanAssignee)
                .addObject("isHidePath", isHidePath)
                .addObject("toBackNodeId", toBackNodeId)
                .addObject("form", form)
                .addObject("isExtForm", isExtForm)
                .addObject("isEmptyForm", isEmptyForm)
                .addObject("taskAppItems", taskAppItems)
                .addObject("mapButton", mapButton)
                .addObject("detailUrl", detailUrl)
                .addObject("isManage", isManage)
                .addObject("bpmGangedSets", gangedSetJarray)
                .addObject("sysFile", sysFile)
                .addObject("taskOpinion", taskOpinion)
                .addObject("isHandChoolse", isHandChoolse)
                .addObject("curUserId", sysUser.getUserId().toString())
                .addObject("curUserName", sysUser.getFullname())
                .addObject("hasGlobalFlowNo", hasGlobalFlowNo)
                .addObject("formKey", bpmNodeSet.getFormKey());
    }


    /**
     * 处理会签
     *
     * @param map
     * @param instanceId
     * @param nodeId
     * @param actDefId
     * @param userId
     *            当前用户
     */
    private Map<String,Object> handleSignTask(Map<String,Object> map, String instanceId, String nodeId, String actDefId, Long userId) {

        List<TaskSignData> signDataList = taskSignDataService.getByNodeAndInstanceId(instanceId, nodeId);
        // 获取会签规则
        BpmNodeSign bpmNodeSign = bpmNodeSignService.getByDefIdAndNodeId(actDefId, nodeId);
		//用于存放会签任务结果
        Map<String,Object> handleSignMap = new HashMap<>();
		handleSignMap.put("signDataList",signDataList);
		handleSignMap.put("bpmNodeSign",bpmNodeSign);
		handleSignMap.put("curUser",ContextUtil.getCurrentUser());

        // 获取当前组织
        Long orgId = ContextUtil.getCurrentOrgId();

        // "允许直接处理"特权
        boolean isAllowDirectExecute = bpmNodeSignService.checkNodeSignPrivilege(actDefId, nodeId, BpmNodePrivilegeType.ALLOW_DIRECT, userId, orgId);
        // "允许补签"特权
        boolean isAllowRetoactive = bpmNodeSignService.checkNodeSignPrivilege(actDefId, nodeId, BpmNodePrivilegeType.ALLOW_RETROACTIVE, userId, orgId);
        // "一票决断"特权
        boolean isAllowOneVote = bpmNodeSignService.checkNodeSignPrivilege(actDefId, nodeId, BpmNodePrivilegeType.ALLOW_ONE_VOTE, userId, orgId);
		handleSignMap.put("isAllowDirectExecute",isAllowDirectExecute);
		handleSignMap.put("isAllowRetoactive",isAllowRetoactive);
		handleSignMap.put("isAllowOneVote",isAllowOneVote);

		map.put("handleSignTaskResult",handleSignMap);
		return map;
    }

    /**
     * 处理会签(前台)
     *
     * @param mv
     * @param instanceId
     * @param nodeId
     * @param actDefId
     * @param userId
     *            当前用户
     */
    private void handleSignTask(ModelAndView mv, String instanceId, String nodeId, String actDefId, Long userId) {

        List<TaskSignData> signDataList = taskSignDataService.getByNodeAndInstanceId(instanceId, nodeId);
        // 获取会签规则
        BpmNodeSign bpmNodeSign = bpmNodeSignService.getByDefIdAndNodeId(actDefId, nodeId);

        mv.addObject("signDataList", signDataList);
        mv.addObject("bpmNodeSign", bpmNodeSign);
        mv.addObject("curUser", ContextUtil.getCurrentUser());
        // 获取当前组织
        Long orgId = ContextUtil.getCurrentOrgId();

        // "允许直接处理"特权
        boolean isAllowDirectExecute = bpmNodeSignService.checkNodeSignPrivilege(actDefId, nodeId, BpmNodePrivilegeType.ALLOW_DIRECT, userId, orgId);
        // "允许补签"特权
        boolean isAllowRetoactive = bpmNodeSignService.checkNodeSignPrivilege(actDefId, nodeId, BpmNodePrivilegeType.ALLOW_RETROACTIVE, userId, orgId);
        // "一票决断"特权
        boolean isAllowOneVote = bpmNodeSignService.checkNodeSignPrivilege(actDefId, nodeId, BpmNodePrivilegeType.ALLOW_ONE_VOTE, userId, orgId);
        mv.addObject("isAllowDirectExecute", isAllowDirectExecute).addObject("isAllowRetoactive", isAllowRetoactive).addObject("isAllowOneVote", isAllowOneVote);

    }


    /**
     * 是否执行隐藏路径
     *
     * @param isHidePath
     * @return
     */
    private boolean getIsHidePath(Short isHidePath) {
        if (BeanUtils.isEmpty(isHidePath))
            return false;
        if (BpmNodeSet.HIDE_PATH.shortValue() == isHidePath.shortValue())
            return true;
        return false;
    }

	/**
	 * 用于复制数据（将TaskEntity数据复制到TaskDto）
	 * @param taskDto
	 * @param taskEntity
	 * @return
	 */
	private TaskDto copyData(TaskDto taskDto,TaskEntity taskEntity){
    	taskDto.setId(taskEntity.getId());
    	taskDto.setDescription(taskEntity.getDescription());
    	taskDto.setExecutionId(taskEntity.getExecutionId());
    	taskDto.setName(taskEntity.getName());
    	taskDto.setTaskDefinitionKey(taskEntity.getTaskDefinitionKey());
    	return taskDto;
	}

    /**
     * 跳转到启动流程页面（前端）。<br/>
     *
     * <pre>
     * 传入参数流程定义id：defId。
     * 实现方法：
     * 1.根据流程对应ID查询流程定义。
     * 2.获取流程定义的XML。
     * 3.获取流程定义的第一个任务节点。
     * 4.获取任务节点的流程表单定义。
     * 5.显示启动流程表单页面。
     * </pre>
     *
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping("startFlowFormForFront")
    @Action(description = "跳至启动流程页面")
    public ModelAndView startFlowFormForFront(HttpServletRequest request, HttpServletResponse response, Long defId) throws Exception {
        String businessKey = RequestUtil.getString(request, "businessKey");
        // 复制表单 启动流程
        String copyKey = RequestUtil.getString(request, "copyKey", "");
        ISysUser sysUser = ContextUtil.getCurrentUser();
        String ctxPath = request.getContextPath();
        // 获取流程类型
        String flowType = RequestUtil.getString(request, "flowType");
        ModelAndView mv = new ModelAndView("/oa/taskStartFlowFormForFront.jsp");

        // 流程草稿传入
        Long runId = RequestUtil.getLong(request, "runId", 0L);
        // 从已经完成的流程实例启动流程。
        Long relRunId = RequestUtil.getLong(request, "relRunId", 0L);

        // 构建参数到到JSP页面。
        Map paraMap = getPageParam(request);

        ProcessRun processRun = null;
        BpmDefinition bpmDefinition = null;
        if (StringUtils.isNotEmpty(businessKey) && runId == 0) {
            processRun = processRunService.getByBusinessKey(businessKey);
            if (BeanUtils.isEmpty(processRun)) {//业务数据模板新增表单后，在列表启动流程，没有流程实例
                defId = RequestUtil.getLong(request, "defId");
            } else {
                defId = processRun.getDefId();
                runId = processRun.getRunId();
            }
        }

        if (runId != 0) {
            processRun = processRunService.getById(runId);
            defId = processRun.getDefId();
        }

        if (StringUtils.isNotBlank(flowType) && (defId == null || defId == 0L)) {
            // 根据流程类型获取流程实例ID
            defId = sysPropertyService.getLongByAlias(flowType);
        }

        if (defId != null && defId != 0L)
            bpmDefinition = bpmDefinitionService.getById(defId);

        ModelAndView tmpView = getNotValidView(bpmDefinition, businessKey);
        if (tmpView != null)
            return tmpView;

        // 根据已经完成的流程实例取得业务主键。
        String pk = processRunService.getBusinessKeyByRelRunId(relRunId);
        if (StringUtil.isNotEmpty(pk)) {
            businessKey = pk;
        }
        Boolean isFormEmpty = false;
        Boolean isExtForm = false;
        String form = "";
        String actDefId = "";
        // 通过草稿启动流程
        if (BeanUtils.isNotEmpty(processRun) && processRun.getStatus().equals(ProcessRun.STATUS_FORM)) {
            mv.addObject("isDraft", false);
            businessKey = processRun.getBusinessKey();
            Long formDefId = processRun.getFormDefId();
            actDefId = processRun.getActDefId();
            //是否使用新版本，草稿启动后会记录表单ID,如果
            int isNewVersion = RequestUtil.getInt(request, "isNewVersion", 0);
            if (formDefId != 0L) {
                String tableName = processRun.getTableName();
                if (!tableName.startsWith(TableModel.CUSTOMER_TABLE_PREFIX)) {
                    tableName = TableModel.CUSTOMER_TABLE_PREFIX + tableName;
                }
                boolean isExistsData = bpmFormHandlerService.isExistsData(processRun.getDsAlias(), tableName, processRun.getPkName(), processRun.getBusinessKey());
                if (!isExistsData)
                    return new ModelAndView("redirect:noData.ht");
            }

            if (StringUtil.isNotEmpty(processRun.getBusinessUrl())) {
                isExtForm = true;
                form = processRun.getBusinessUrl();
                // 替换主键。
                form = processRun.getBusinessUrl().replaceFirst(BpmConst.FORM_PK_REGEX, businessKey);
                if (!form.startsWith("http")) {
                    form = ctxPath + form;
                }
            } else {
                if (isNewVersion == 1) {
                    BpmFormDef defaultFormDef = bpmFormDefService.getById(formDefId);
                    formDefId = bpmFormDefService.getDefaultPublishedByFormKey(defaultFormDef.getFormKey()).getFormDefId();
                }
                String nodeId = "";// 流程第一个节点
                FlowNode flowNode = NodeCache.getFirstNodeId(actDefId);
                if (flowNode != null) {
                    nodeId = flowNode.getNodeId();
                }
                BpmFormDef bpmFormDef = bpmFormDefService.getById(formDefId);
                form = bpmFormHandlerService.obtainHtml(bpmFormDef, businessKey, "", actDefId, nodeId, ctxPath, "", true, true, false,processRun.getStatus());
                //下面调用新的解释器
                form = bpmFormDefService.parseHtml(bpmFormDef, businessKey, "", actDefId, nodeId, "", true, true, false,isExtForm, processRun.getStatus());
            }
            // 流程定义里面的启动
        } else {
            boolean isReCalcuate = false;
            if (StringUtil.isNotEmpty(copyKey)) {
                businessKey = copyKey;
                isReCalcuate = true;
            }
            mv.addObject("isDraft", true);
            actDefId = bpmDefinition.getActDefId();

            // 获取表单节点
            BpmNodeSet bpmNodeSet = bpmNodeSetService.getStartBpmNodeSet(actDefId, false);

            FormModel formModel = getStartForm(bpmNodeSet, businessKey, actDefId, ctxPath, isReCalcuate,StringUtil.isNotEmpty(copyKey));
            // 是外部表单
            isFormEmpty = formModel.isFormEmpty();
            isExtForm = formModel.getFormType() > 0;

            if (isExtForm) {
                form = formModel.getFormUrl();
            } else if (formModel.getFormType() == 0) {
                form = formModel.getFormHtml();
            }
            if (BeanUtils.isNotEmpty(bpmNodeSet)) {
                mv.addObject("formKey", bpmNodeSet.getFormKey());
            }
        }
        // 获取按钮
        Map<String, List<BpmNodeButton>> mapButton = bpmNodeButtonService.getMapByStartForm(defId);
        // 帮助文档
        SysFile sysFile = null;
        if (BeanUtils.isNotEmpty(bpmDefinition.getAttachment()))
            sysFile = sysFileService.getById(bpmDefinition.getAttachment());

        // 通过defid和nodeId获取联动设置
        List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefIdAndNodeId(defId, BpmGangedSet.START_NODEID);
        JSONArray gangedSetJarray = (JSONArray) JSONArray.fromObject(bpmGangedSets);

        if (NodeCache.isMultipleFirstNode(actDefId)) {
            mv.addObject("flowNodeList", NodeCache.getFirstNode(actDefId)).addObject("isMultipleFirstNode", true);
        }

        mv.addObject("bpmDefinition", bpmDefinition).addObject("isExtForm", isExtForm).addObject("isFormEmpty", isFormEmpty).addObject("mapButton", mapButton).addObject("defId", defId).addObject("paraMap", paraMap).addObject("form", form).addObject("runId", runId).addObject("businessKey", StringUtil.isEmpty(copyKey) ? businessKey : "").addObject("sysFile", sysFile).addObject("bpmGangedSets", gangedSetJarray).addObject("curUserId", sysUser.getUserId().toString()).addObject("curUserName", sysUser.getFullname());
        return mv;
    }


	/**
	 * 获取流程启动界面json格式数据
	 * @param request
	 * @param response
	 * @param defId
	 * @return
	 */
	@RequestMapping("getStartFlowFormJsonResult")
	@ResponseBody
	public ResultVo getStartFlowFormJsonResult(HttpServletRequest request, HttpServletResponse response, Long defId) throws Exception {
		try{
			Map<String, Object> map = startFlowForm(request, response, defId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"流程启动界面数据请求成功！",map);
		}catch (TipInfoException tie){
			logger.error(tie.getMessage(),tie);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,tie.getMessage());
		}
	}


    /**
     * 跳转到启动流程页面。<br/>
     *
     * <pre>
     * 传入参数流程定义id：defId。
     * 实现方法：
     * 1.根据流程对应ID查询流程定义。
     * 2.获取流程定义的XML。
     * 3.获取流程定义的第一个任务节点。
     * 4.获取任务节点的流程表单定义。
     * 5.显示启动流程表单页面。
     * </pre>
     *
     * @param request
     * @param response
     * @return
     */
    public Map<String,Object> startFlowForm(HttpServletRequest request, HttpServletResponse response, Long defId) throws Exception {
        String businessKey = RequestUtil.getString(request, "businessKey");
        // 复制表单 启动流程
        String copyKey = RequestUtil.getString(request, "copyKey", "");
        ISysUser sysUser = ContextUtil.getCurrentUser();
        String ctxPath = request.getContextPath();
        // 获取流程类型
        String flowType = RequestUtil.getString(request, "flowType");
        Map<String,Object> map = new HashMap<>();

        // 流程草稿传入
        Long runId = RequestUtil.getLong(request, "runId", 0L);
        // 从已经完成的流程实例启动流程。
        Long relRunId = RequestUtil.getLong(request, "relRunId", 0L);

        // 构建参数到到JSP页面。
        Map paraMap = getPageParam(request);

        ProcessRun processRun = null;
        BpmDefinition bpmDefinition = null;
        if (StringUtils.isNotEmpty(businessKey) && runId == 0) {
            processRun = processRunService.getByBusinessKey(businessKey);
            if (BeanUtils.isEmpty(processRun)) {//业务数据模板新增表单后，在列表启动流程，没有流程实例
                defId = RequestUtil.getLong(request, "defId");
            } else {
                defId = processRun.getDefId();
                runId = processRun.getRunId();
            }
        }

        if (runId != 0) {
            processRun = processRunService.getById(runId);
            defId = processRun.getDefId();
        }

        if (StringUtils.isNotBlank(flowType) && (defId == null || defId == 0L)) {
            // 根据流程类型获取流程实例ID
            defId = sysPropertyService.getLongByAlias(flowType);
        }

        if (defId != null && defId != 0L)
            bpmDefinition = bpmDefinitionService.getById(defId);

        getNotValidResult(bpmDefinition, businessKey);

        // 根据已经完成的流程实例取得业务主键。
        String pk = processRunService.getBusinessKeyByRelRunId(relRunId);
        if (StringUtil.isNotEmpty(pk)) {
            businessKey = pk;
        }
        Boolean isFormEmpty = false;
        Boolean isExtForm = false;
        String form = "";
        String actDefId = "";
        // 通过草稿启动流程
        if (BeanUtils.isNotEmpty(processRun) && processRun.getStatus().equals(ProcessRun.STATUS_FORM)) {
            map.put("isDraft",false);
            businessKey = processRun.getBusinessKey();
            Long formDefId = processRun.getFormDefId();
            actDefId = processRun.getActDefId();
            //是否使用新版本，草稿启动后会记录表单ID,如果
            int isNewVersion = RequestUtil.getInt(request, "isNewVersion", 0);
            if (formDefId != 0L) {
                String tableName = processRun.getTableName();
                if (!tableName.startsWith(TableModel.CUSTOMER_TABLE_PREFIX)) {
                    tableName = TableModel.CUSTOMER_TABLE_PREFIX + tableName;
                }
                boolean isExistsData = bpmFormHandlerService.isExistsData(processRun.getDsAlias(), tableName, processRun.getPkName(), processRun.getBusinessKey());
                if (!isExistsData)
                	throw new TipInfoException("指定的表数据不存在！");
            }

            if (StringUtil.isNotEmpty(processRun.getBusinessUrl())) {
                isExtForm = true;
                form = processRun.getBusinessUrl();
                // 替换主键。
                form = processRun.getBusinessUrl().replaceFirst(BpmConst.FORM_PK_REGEX, businessKey);
                if (!form.startsWith("http")) {
                    form = ctxPath + form;
                }
            } else {
                if (isNewVersion == 1) {
                    BpmFormDef defaultFormDef = bpmFormDefService.getById(formDefId);
                    formDefId = bpmFormDefService.getDefaultPublishedByFormKey(defaultFormDef.getFormKey()).getFormDefId();
                }
                String nodeId = "";// 流程第一个节点
                FlowNode flowNode = NodeCache.getFirstNodeId(actDefId);
                if (flowNode != null) {
                    nodeId = flowNode.getNodeId();
                }
                BpmFormDef bpmFormDef = bpmFormDefService.getById(formDefId);
                form = bpmFormHandlerService.obtainHtml(bpmFormDef, businessKey, "", actDefId, nodeId, ctxPath, "", true, true, false,processRun.getStatus());
                //下面调用新的解释器
                form = bpmFormDefService.parseHtml(bpmFormDef, businessKey, "", actDefId, nodeId, "", true, true, false,isExtForm, processRun.getStatus());
            }
            // 流程定义里面的启动
        } else {
            boolean isReCalcuate = false;
            if (StringUtil.isNotEmpty(copyKey)) {
                businessKey = copyKey;
                isReCalcuate = true;
            }
            map.put("isDraft",true);
            actDefId = bpmDefinition.getActDefId();

            // 获取表单节点
            BpmNodeSet bpmNodeSet = bpmNodeSetService.getStartBpmNodeSet(actDefId, false);

            FormModel formModel = getStartForm(bpmNodeSet, businessKey, actDefId, ctxPath, isReCalcuate,StringUtil.isNotEmpty(copyKey));
            // 是外部表单
            isFormEmpty = formModel.isFormEmpty();
            isExtForm = formModel.getFormType() > 0;

            if (isExtForm) {
                form = formModel.getFormUrl();
            } else if (formModel.getFormType() == 0) {
                form = formModel.getFormHtml();
            }
            if (BeanUtils.isNotEmpty(bpmNodeSet)) {
                map.put("formKey",bpmNodeSet.getFormKey());
            }
        }
        // 获取按钮
        Map<String, List<BpmNodeButton>> mapButton = bpmNodeButtonService.getMapByStartForm(defId);
        // 帮助文档
        SysFile sysFile = null;
        if (BeanUtils.isNotEmpty(bpmDefinition.getAttachment()))
            sysFile = sysFileService.getById(bpmDefinition.getAttachment());

        // 通过defid和nodeId获取联动设置
        List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefIdAndNodeId(defId, BpmGangedSet.START_NODEID);

        if (NodeCache.isMultipleFirstNode(actDefId)) {
        	map.put("flowNodeList",NodeCache.getFirstNode(actDefId));
        	map.put("isMultipleFirstNode",true);
        }

        // 消息类型
        Map<String, IMessageHandler> handlersMap = this.getMessageType(bpmDefinition);
		List<String> handlersList = new ArrayList<>();
		for(String s:handlersMap.keySet()){
			handlersList.add(s);
		}

		//截取出表单权限
		String var = StringUtils.substringBetween(form, "var", "</script>");
		String permission = var.split("=")[1];
		net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(permission);

        map.put("bpmDefinition",bpmDefinition);
        map.put("isExtForm",isExtForm);
        map.put("isFormEmpty",isFormEmpty);
        map.put("mapButton",mapButton);
        map.put("defId",defId);
        map.put("paraMap",paraMap);
        map.put("form",form);
        map.put("runId",runId);
        map.put("businessKey",StringUtil.isEmpty(copyKey) ? businessKey : "");
        map.put("sysFile",sysFile);
        map.put("bpmGangedSets",bpmGangedSets);
        map.put("curUserId",sysUser.getUserId().toString());
        map.put("curUserName",sysUser.getFullname());
        map.put("handlersMap",handlersList);
        map.put("permission",jsonObject);

        return map;
    }

    /**
     * 取得起始表单。
     *
     * @param bpmNodeSet
     * @return
     * @throws Exception
     */
    private FormModel getStartForm(BpmNodeSet bpmNodeSet, String businessKey, String actDefId, String ctxPath, boolean isReCalc,boolean isCopy) throws Exception {
        FormModel formModel = new FormModel();
        if (bpmNodeSet == null || bpmNodeSet.getFormType() == -1)
            return formModel;
        if (bpmNodeSet.getFormType() == BpmConst.OnLineForm) {
            String formKey = bpmNodeSet.getFormKey();
            if (StringUtil.isNotEmpty(formKey)) {
                BpmFormDef bpmFormDef = bpmFormDefService.getDefaultPublishedByFormKey(formKey);
                if (bpmFormDef != null) {
                    //String formHtml = bpmFormHandlerService.obtainHtml(bpmFormDef, businessKey, "", actDefId, bpmNodeSet.getNodeId(), ctxPath, "", isReCalc, false, false);

                    //--->测试新解释器
                    String formHtml = bpmFormDefService.parseHtml(bpmFormDef, businessKey, "", actDefId, bpmNodeSet.getNodeId(), "", isReCalc, true, false,isCopy,(short) 0);
                    //<---测试新解释器

                    formModel.setFormHtml(formHtml);
                }
            }
        } else {
            String formUrl = bpmNodeSet.getFormUrl();
            // 替换主键。
            formUrl = formUrl.replaceFirst(BpmConst.FORM_PK_REGEX, businessKey);
            if (!formUrl.startsWith("http")) {
                formUrl = ctxPath + formUrl;
            }
            formModel.setFormType(BpmConst.UrlForm);
            formModel.setFormUrl(formUrl);
        }
        return formModel;
    }

    private Map getPageParam(HttpServletRequest request) {
        Map paraMap = RequestUtil.getParameterValueMap(request, false, false);
        paraMap.remove("businessKey");
        paraMap.remove("defId");
        return paraMap;
    }

	private ModelAndView getNotValidView(BpmDefinition bpmDefinition, String businessKey) {
		if (BeanUtils.isEmpty(bpmDefinition))
			return ServiceUtil.getTipInfo("该流程定义已经被删除!");
		if (bpmDefinition.getStatus().equals(BpmDefinition.STATUS_DISABLED) || bpmDefinition.getStatus().equals(BpmDefinition.STATUS_INST_DISABLED))
			return ServiceUtil.getTipInfo("该流程定义已经被禁用!");
		// 判断该业务主键是否已绑定流程实例
		boolean isProcessInstanceExisted = processRunService.isProcessInstanceExisted(businessKey);
		if (isProcessInstanceExisted) {
			return ServiceUtil.getTipInfo("对不起，该流程实例已存在，不需要再次启动!");
		}
		return null;
	}

    private void getNotValidResult(BpmDefinition bpmDefinition, String businessKey) {
        if (BeanUtils.isEmpty(bpmDefinition)){
			throw new TipInfoException("该流程定义已经被删除!");
		}
        if (bpmDefinition.getStatus().equals(BpmDefinition.STATUS_DISABLED) || bpmDefinition.getStatus().equals(BpmDefinition.STATUS_INST_DISABLED)){
			throw new TipInfoException("该流程定义已经被禁用!");
		}
        // 判断该业务主键是否已绑定流程实例
        boolean isProcessInstanceExisted = processRunService.isProcessInstanceExisted(businessKey);
        if (isProcessInstanceExisted) {
			throw new TipInfoException("对不起，该流程实例已存在，不需要再次启动!");
        }
    }

    /**
     * 获取自由退回，退回节点数据列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getFreeBackNodeData")
    @ResponseBody
    public ResultVo getFreeBackNodeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String isRequired = RequestUtil.getString(request, "isRequired");
        String actDefId = RequestUtil.getString(request, "actDefId");
        String taskId = RequestUtil.getString(request, "taskId");
        String reject = RequestUtil.getString(request, "reject", "0");
        BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
        List<ForkTaskReject> forkTaskExecutor = bpmActService.forkTaskExecutor(taskId);
        // 获取常用语
        List<String> taskAppItems = taskAppItemService.getApprovalByDefKeyAndTypeId(bpmDefinition.getDefKey(), bpmDefinition.getTypeId());
        Map<String,Object> map = new HashMap<>();

		map.put("isRequired",isRequired);
		map.put("reject",reject);
		map.put("taskAppItems",taskAppItems);
		map.put("forkTaskExecutor",forkTaskExecutor);

        //获取可以自由退回节点列表
        TaskEntity task = bpmService.getTask(taskId);
        List<ExecutionStack> stackList=executionStackService.getByActInstId(Long.valueOf(task.getProcessInstanceId()));
        List<ExecutionStack> stackListData=new CopyOnWriteArrayList<ExecutionStack>();
        List<String> containTaskKey=new ArrayList<String>();
        for (ExecutionStack stackItem:stackList){
            //如果任务栈与当前任务相同，则不加入自由退回列表中
            if (task.getTaskDefinitionKey().equals(stackItem.getNodeId())){
                continue;
            }
            if (!containTaskKey.contains(stackItem.getNodeId())){
                containTaskKey.add(stackItem.getNodeId());
                stackListData.add(stackItem);
            }
        }
        //获取审批历史,存在审批历史中则可以退回
        List<TaskOpinion> opinionList = taskOpinionService.getByActInstId(task.getProcessInstanceId());

        FlowNode node=NodeCache.getFlowNode(task.getProcessDefinitionId(),task.getTaskDefinitionKey());
        Map<String,FlowNode> preCacheMap=new HashMap<String, FlowNode>();
        Map<String,FlowNode> nextCacheMap=new HashMap<String, FlowNode>();
        boolean isSyncNode=NodeCache.isPreParallelGateway(preCacheMap,node)&&NodeCache.isNextParallelGateway(nextCacheMap,node);

        if (isSyncNode){
            //同步节点时，获取同步条件中可以驳回的流程
            for (ExecutionStack stackTemp:stackListData){
                boolean isFind=false;
                if (preCacheMap.get(stackTemp.getNodeId())!=null){
                    isFind=true;
                }
                if (!isFind){
                    stackListData.remove(stackTemp);
                }
            }
        }else {
            for (ExecutionStack stackTemp:stackListData){
                boolean isFind=false;
                for (TaskOpinion opinion:opinionList){
                    if (stackTemp.getNodeId().equals(opinion.getTaskKey())&&!NodeCache.isSyncNode(opinion.getActDefId(),opinion.getTaskKey())){
                        isFind=true;
                        break;
                    }
                }
                if (!isFind){
                    stackListData.remove(stackTemp);
                }
            }
        }

        if (stackListData.size()>1){
            Collections.sort(stackListData, new Comparator<ExecutionStack>() {
                @Override
                public int compare(ExecutionStack stack1, ExecutionStack stack2) {
                    if (stack1.getDepth()<stack2.getDepth()){
                        return 1;
                    }else if (stack1.getDepth()>stack2.getDepth()){
                        return -1;
                    }else {
                        return 0;
                    }
                }
            });
        }
        map.put("stackList",stackListData);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取退回节点数据成功！",map);
    }

    /**
     * 验证是否为会签节点
     * @param stack
     * @return
     */
    private boolean isSignNode(ExecutionStack stack){
        return bpmService.isSignTask(stack.getActDefId(),stack.getNodeId());
    }

    /**
     * 取得流程批量审批定义设置列表Json
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("bacthList")
    @ResponseBody
    public ResultVo bacthList(HttpServletRequest request,HttpServletResponse response) throws  Exception{
        Long batchId = RequestUtil.getLong(request, "batchId");
        BpmBatchApproval bpmBatchApproval = bpmBatchApprovalService
                .getById(batchId); // 获取到需要批量审批配置信息
        JSONObject json = new JSONObject();
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        List<String> fieldList = new ArrayList<String>();
        List<String> fieldNameList = new ArrayList<String>();
        boolean isDataError =  false;
        try {
            if(BeanUtils.isNotEmpty(bpmBatchApproval)){
                // 列表显示字段
                fieldList = bpmBatchApprovalService.getFieldList(bpmBatchApproval);
                // 列表字段
                fieldNameList = bpmBatchApprovalService
                        .getFieldNameList(bpmBatchApproval);
                // 列表数据
                dataList = bpmBatchApprovalService.getDataList(bpmBatchApproval);
            }
            json.put("dataList",dataList);
            json.put("fieldNameList",fieldNameList);
            json.put("fieldList",fieldList);
            json.put("isDataError",isDataError);
            return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取批量审批流程列表成功",json);
        } catch (Exception e) {
            e.printStackTrace();
            isDataError = true;
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取批量审批流程列表失败",e);
        }
    }

	/**
	 * 获取流程任务列表数据
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    @SuppressWarnings("unused")
    @RequestMapping("list")
	@ResponseBody
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter filter = new QueryFilter(request, true);

        // 增加按新的流程分管授权中任务类型的权限获取流程的任务
        Long userId = ContextUtil.getCurrentUserId();
        String isNeedRight = "";

        Map<String, AuthorizeRight> authorizeRightMap = null;
        String actRights = "";
        if (!ContextUtil.isSuperAdmin()) {
            isNeedRight = "yes";
            // 获得流程分管授权与用户相关的信息
            Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId, BPMDEFAUTHORIZE_RIGHT_TYPE.TASK, false, false);
            // 获得流程分管授权与用户相关的信息集合的流程KEY
            actRights = (String) actRightMap.get("authorizeIds");
            filter.addFilter("actRights", actRights);
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
			Set<Long> typeIdList=BpmUtil.getTypeIdList(globalTypeList);
			filter.addFilter("typeIds",typeIdList);
        }
        //按照查询条件查询数据
        List<Map<String, Object>> list = bpmService.getAllTasks(filter);
		Map<String, Object> pageList = getPageList(list, filter);
		//PageList<Map<String, Object>> resultList = (PageList<Map<String, Object>>) list;
        request.getSession().setAttribute("isAdmin", true);

        // 是否有全局流水号
        boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);

        Map<String,Object> map = new HashMap<>();
        map.put("taskList", pageList);
        map.put("hasGlobalFlowNo",hasGlobalFlowNo);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程任务列表数据成功！",map);
    }

	/**
	 * 结合前台任务管理列表，点击某行任务时，显示的任务简单明细
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("miniDetail")
	@ResponseBody
	public ResultVo miniDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");

		TaskEntity taskEntity = bpmService.getTask(taskId);

		if (taskEntity == null) {
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"任务不存在！");
		}

		// 取到任务的侯选人员
		Set<TaskExecutor> candidateUsers = taskUserService.getCandidateExecutors(taskId);

		ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));

		BpmDefinition definition = bpmDefinitionService.getByActDefId(taskEntity.getProcessDefinitionId());

		List<ProcessTask> curTaskList = bpmService.getTasks(taskEntity.getProcessInstanceId());
		//用TaskDto封装taskEntity中所需参数
		TaskDto taskDto = new TaskDto();
		taskDto.setId(taskEntity.getId());
		taskDto.setName(taskEntity.getName());
		taskDto.setDescription(taskEntity.getDescription());
		taskDto.setAssigneeName(userService.getByUserId(Long.valueOf(taskEntity.getAssignee())).getFullname());
		taskDto.setAssignee(taskEntity.getAssignee());
		taskDto.setCreateTime(taskEntity.getCreateTime());
		taskDto.setDueDate(taskEntity.getDueDate());

		Map<String,Object> map = new HashMap<>();
		map.put("taskEntity",taskDto);
		map.put("processRun",processRun);
		map.put("candidateUsers",candidateUsers);
		map.put("processDefinition",definition);
		map.put("curTaskList",curTaskList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取任务明细成功！",map);
	}

    /**
     * 返回目标节点及其节点的处理人员映射列表。
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("tranTaskUserMap")
	@ResponseBody
    public ResultVo tranTaskUserMap(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int isStart = RequestUtil.getInt(request, "isStart", 0);
        String taskId = request.getParameter("taskId");
        String actDefId = request.getParameter("actDefId");
        Long defId=null;
        BpmDefinition bpmDefinition=null;

        String scope = "";
        if (StringUtil.isEmpty(taskId)) {
            List<FlowNode> firstNode = NodeCache.getFirstNode(actDefId);
            bpmDefinition=bpmDefinitionService.getByActDefId(actDefId);
            if (BeanUtils.isNotEmpty(firstNode)) {
                FlowNode flowNode = firstNode.get(0);
                String nodeId = flowNode.getNodeId();
                BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, "");
                defId=bpmNodeSet.getDefId();
                if (BeanUtils.isNotEmpty(bpmNodeSet))
                    scope = bpmNodeSet.getScope();
            }
        } else {
            // 获取当前节点的选择器限定配置
            ExecutionEntity execution = null;
            TaskEntity taskEntity = bpmService.getTask(taskId);
            if (taskEntity.getDescription().equals(TaskOpinion.STATUS_TRANSTO.toString())) {// 加签任务
                execution = bpmService.getExecutionByTaskId(taskEntity.getParentTaskId());// 获取它的parentTaskId，这里是放着的加签任务生成的源任务
            } else {
                // 获取当前节点的选择器限定配置
                execution = bpmService.getExecutionByTaskId(taskId);
            }
            if(BeanUtils.isNotEmpty(execution)){
                bpmDefinition=bpmDefinitionService.getByActDefId(execution.getProcessDefinitionId());
                String superExecutionId = execution.getSuperExecutionId();
                String parentActDefId = "";
                if (StringUtil.isNotEmpty(superExecutionId)) {
                    ExecutionEntity supExecution = bpmService.getExecution(superExecutionId);
                    parentActDefId = supExecution.getProcessDefinitionId();
                }
                String nodeId = execution.getActivityId();
                String processDefinitionId = execution.getProcessDefinitionId();
                BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(processDefinitionId, nodeId, parentActDefId);
                if (BeanUtils.isNotEmpty(bpmNodeSet))
                    scope = bpmNodeSet.getScope();
            }
        }
        if (bpmDefinition!=null){
            defId=bpmDefinition.getDefId();
        }
        int selectPath = RequestUtil.getInt(request, "selectPath", 1);

        boolean canChoicePath = bpmService.getCanChoicePath(actDefId, taskId);

        Long startUserId = ContextUtil.getCurrentUserId();
        List<NodeTranUser> nodeTranUserList = null;
        if (isStart == 1) {
            Map<String, Object> vars = new HashMap<String, Object>();
            nodeTranUserList = bpmService.getStartNodeUserMap(actDefId, startUserId, vars);
        } else {
            nodeTranUserList = bpmService.getNodeTaskUserMap(taskId, startUserId, canChoicePath);
        }

        //节点排序
        Map<String,FlowNode> nodeMap=NodeCache.getByActDefId(actDefId);
        for (NodeTranUser nodeItem:nodeTranUserList){
            for (Map.Entry<String, FlowNode> mapItem:nodeMap.entrySet()){
                if (nodeItem.getNodeId().equals(mapItem.getKey())){
                    nodeItem.setSort(mapItem.getValue().getOrder());
                    break;
                }
            }
        }
        nodeSetSort(defId,nodeTranUserList);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("nodeTranUserList",nodeTranUserList);
		resultMap.put("selectPath",selectPath);
		resultMap.put("scope",scope);
		resultMap.put("canChoicePath",canChoicePath);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",resultMap);
    }

	/**
	 * 执行路径节点排序
	 * @param defId
	 * @param nodeTranUserList
	 */
	private void nodeSetSort(Long defId,List<NodeTranUser> nodeTranUserList){
		if (defId==null){
			return;
		}
		List<BpmNodeSet> nodeSetList = bpmNodeSetService.getByDefId(defId);
		for (NodeTranUser nodeItem:nodeTranUserList){
			for (BpmNodeSet nodeSet:nodeSetList){
				if (nodeSet.getNodeId().equals(nodeItem.getNodeId())){
					nodeItem.setSort(nodeSet.getNodeOrder());
					if(nodeItem.getNodeName().contains("）")) {
						int index =nodeItem.getNodeName().indexOf("）");
						String nodeName = nodeItem.getNodeName().substring(index+1);
						nodeItem.setNodeName(nodeName);
					}else if(nodeItem.getNodeName().contains(")")) {
						int index = nodeItem.getNodeName().indexOf(")");
						String nodeName = nodeItem.getNodeName().substring(index + 1);
						nodeItem.setNodeName(nodeName);
					}
					break;
				}
			}
		}
		Collections.sort(nodeTranUserList, new Comparator<NodeTranUser>() {
			@Override
			public int compare(NodeTranUser o1, NodeTranUser o2) {
				return o1.getSort()-o2.getSort();
			}
		});
	}

    /**
     * 获取前端可显示的消息类型
     * @param bpmDefinition
     * @return
     */
    private Map<String, IMessageHandler> getMessageType(BpmDefinition bpmDefinition){
        // 消息类型
        Map<String, IMessageHandler> sysHandlersMap=ServiceUtil.getHandlerMap();
        Map<String, IMessageHandler> handlersMap = new TreeMap<String, IMessageHandler>();
        // 流程定义未设置在审批节点显示的消息类型，则清空消息类型
        if(!StringUtils.isBlank(bpmDefinition.getInformShowInFront())){
            // 流程定义设置了在审批节点显示的消息类型，则只保留设置的消息类型
            Iterator<String> keyIterator = sysHandlersMap.keySet().iterator();
            String key = null;
            while(keyIterator.hasNext()){
                key = keyIterator.next();
                if(bpmDefinition.getInformShowInFront().contains(key)){
                    handlersMap.put(key, sysHandlersMap.get(key));
                }
            }
        }
        return handlersMap;
    }
}

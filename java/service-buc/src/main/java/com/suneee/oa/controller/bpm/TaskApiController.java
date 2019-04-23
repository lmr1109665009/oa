package com.suneee.oa.controller.bpm;


import com.alibaba.fastjson.JSONArray;
import com.suneee.core.api.bpm.model.IProcessRun;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.model.ForkTaskReject;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.impl.BpmActService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.dao.codeTable.ApiTaskHistoryDao;
import com.suneee.ucp.mh.model.codeTable.ApiTaskHistory;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 * 对象功能:后台任务管理控制类（供外部调用）
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2011-12-12 10:56:13
 * </pre>
 */
@Controller
@RequestMapping("/api/bpm/task/")
@Action(ownermodel = SysAuditModelType.PROCESS_MANAGEMENT)
public class TaskApiController {
    protected Logger logger = LoggerFactory.getLogger(TaskApiController.class);

    @Resource
    private ProcessRunService processRunService;
    @Resource
    private SysUserService sysUserService;
	@Resource
	private BpmService bpmService;
	@Resource
	private BpmRunLogService bpmRunLogService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
    private TaskApprovalItemsService taskAppItemService;
	@Resource
    private ExecutionStackService executionStackService;
	@Resource
    private TaskOpinionService taskOpinionService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
    private BpmActService bpmActService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private BpmNodeUserService bpmNodeUserService;
    @Autowired
    private ApiTaskHistoryDao apiTaskHistoryDao;

    /**
     * 外部根据流程actdeId或defId获取流程节点信息
     */
    @RequestMapping(value = "getInfo")
    @ResponseBody
    @Action(description = "外部调用该接口获取某一流程信息")
    public ResultVo getProcessInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
        Long defId =0L;
        String actDefId ="";
        defId = RequestUtil.getLong(request,"defId");
        actDefId = RequestUtil.getString(request,"actDefId");
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        BpmDefinition bpmDefinition=null;
        try {
            if(StringUtil.isNotEmpty(actDefId)){
                bpmDefinition=bpmDefinitionService.getByActDefId(actDefId);
            }
            if(bpmDefinition==null){
                bpmDefinition = bpmDefinitionService.getByDefId(defId.toString());
            }
            List<NodeTranUser> nodeTranUserList = null;
            Map<String, Object> vars = new HashMap<String, Object>();
            if(StringUtil.isNotEmpty(bpmDefinition.getActDefId())){
                if(bpmDefinition.getShowFirstAssignee()==1) {
                    nodeTranUserList = bpmService.getStartNodeUserMap(bpmDefinition.getActDefId(), ContextUtil.getCurrentUserId(), vars);
                    json.put("bpmDefinition", bpmDefinition.getShowFirstAssignee());
                    json.put("nodeTranUserList", nodeTranUserList);
                }else{
                    json.put("bpmDefinition", bpmDefinition.getShowFirstAssignee());
                }
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程信息成功",json);
            }else{
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"没有获取到该参数的流程信息");
            }
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取信息失败",e.getMessage());
        }
    }


    /**
     * 外部调用该接口来新建流程
     */
    @RequestMapping(value = "startFlow")
    @ResponseBody
    @Action(description = "外部调用该接口启动流程")
    public Object startFlow(@RequestBody Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> resultMap = new HashMap<>();
        Long runId = RequestUtil.getLong(request, "runId");
        Object isMobile = map.get("isMobile");
        ISysUser currentUser = ContextUtil.getCurrentUser();
        if(isMobile==null) {
            Object account = map.get("account");
            if (account == null) {
                return new ResultMessage(ResultMessage.Fail, "用户账号不能为空！");
            }
            SysUser newUser = processRunService.getUseridByAccount(account.toString());
            //根据userId对用户进行判断
            if (newUser == null) {
                return new ResultMessage(ResultMessage.Fail, "用户不存在！");
            }
            ContextUtil.setCurrentUser(newUser);
        }
        try {
            ProcessCmd processCmd = BpmUtil.getProcessCmd(request);
            Object lastDestTaskIds = map.get("lastDestTaskId");
            if(lastDestTaskIds!=null){
                String[] destTaskIds = lastDestTaskIds.toString().split(",");
                if (destTaskIds != null) {
                    processCmd.setLastDestTaskIds(destTaskIds);
                    String[] destTaskUserIds = new String[destTaskIds.length];
                    for (int i = 0; i < destTaskIds.length; i++) {

                        Object userIds = map.get(destTaskIds[i]+"_userId");
                        if(userIds!=null){
                            SysUser nextUser = processRunService.getUseridByAccount(userIds.toString());
                            //人事系统这个userIds传的是用户账号
                            if(null!=nextUser){
                                userIds= "user^"+nextUser.getUserId()+"^"+nextUser.getFullname();
                            }
                            userIds=userIds.toString().replace(",","#");
                        }
                        else{
                            userIds="";
                        }
                        destTaskUserIds[i]=userIds.toString();
                    }
                    processCmd.setLastDestTaskUids(destTaskUserIds);
                }
            }
            if(map.get("destTask")!=null){
                processCmd.setDestTask(map.get("destTask").toString());
            }

            processCmd.setCurrentUserId(ContextUtil.getCurrentUserId().toString());
            processCmd.setActDefId( map.get("actDefId").toString());
            processCmd.setFormData(JSONArray.toJSONString(map.get("formData")));
            if (runId != 0L) {
                ProcessRun processRun = processRunService.getById(runId);
                if (BeanUtils.isEmpty(processRun)) {
                    return new ResultMessage(ResultMessage.Fail, "流程草稿不存在或已被清除");
                }
                processCmd.setProcessRun((IProcessRun) processRun);
            }
            ProcessRun processRun = processRunService.startProcess(processCmd);
            Object comeFrom = map.get("comeFrom");
            if(null!=comeFrom){
                ApiTaskHistory apiTaskHistory = new ApiTaskHistory();
                apiTaskHistory.setId(UniqueIdUtil.genId());
                apiTaskHistory.setRunId(processRun.getRunId());
                apiTaskHistory.setProcessName(processRun.getSubject());
                apiTaskHistory.setComeFrom(comeFrom.toString());
                apiTaskHistory.setCreateBy(ContextUtil.getCurrentUserId());
                apiTaskHistory.setCreateDate(new Date());
                apiTaskHistoryDao.add(apiTaskHistory);
            }
            ResultMessage resultMessage = new ResultMessage(ResultMessage.Success, "启动流程成功!");
            ContextUtil.setCurrentUser(currentUser);
            resultMap.put("resultMessage",resultMessage);
            resultMap.put("processRun",processRun);
            return resultMap;
        } catch (Exception ex) {
            logger.debug("startFlow:" + ex.getMessage());
            ex.printStackTrace();
            String str = MessageUtil.getMessage();
            if (StringUtil.isNotEmpty(str)) {
                ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "启动流程失败:\r\n" + str);
                return resultMessage;
            } else {
                String message = ex.getMessage();
                if (StringUtil.isEmpty(message)) {
                    Throwable tt = ex.getCause();
                    message = tt.getMessage();
                }
                ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
                return resultMessage;
            }
        }
    }


    /**
     * 根据任务结束流程实例(外部调用接口)
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("endProcess")
	@ResponseBody
    @Action(description = "根据任务结束流程实例")
    public ResultVo endProcess(@RequestBody Map<String,Object> map,HttpServletRequest request, HttpServletResponse response) throws Exception {
    	//根据全局流水号查询任务taskId（id）
		String globalFlowNo = map.get("globalFlowNo").toString();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("globalFlowNo",globalFlowNo);
		QueryFilter filter = new QueryFilter(jsonObject);
		List<?> tasks = bpmService.getTasks(filter);

		String id = ((ProcessTask)tasks.get(0)).getId();
		Long taskId = Long.valueOf(id);
        String memo = map.get("memo").toString();
        TaskEntity taskEnt = bpmService.getTask(taskId.toString());
        if (taskEnt == null) {
            return new ResultVo(ResultMessage.Fail,"此任务已经完成!");
        }

        String instanceId = taskEnt.getProcessInstanceId();

		String nodeId = taskEnt.getTaskDefinitionKey();
		ProcessRun processRun = bpmService.endProcessByInstanceId(new Long(instanceId), nodeId, memo);
		memo = "结束流程:" + processRun.getSubject() + ",结束原因:" + memo;
		bpmRunLogService.addRunLog(processRun.getRunId(), BpmRunLog.OPERATOR_TYPE_ENDTASK, memo);
		return new ResultVo(ResultMessage.Success,"结束流程实例成功!");
    }

    /**
     * 自由退回节点接口
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("freeBack")
    public ResultVo freeBack(HttpServletRequest request) throws Exception {
        String isRequired = RequestUtil.getString(request, "isRequired");
        String actDefId = RequestUtil.getString(request, "actDefId");
        String taskId = RequestUtil.getString(request, "taskId");
        String reject = RequestUtil.getString(request, "reject", "0");
        BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
        List<ForkTaskReject> forkTaskExecutor = bpmActService.forkTaskExecutor(taskId);
        // 获取常用语
        List<String> taskAppItems = taskAppItemService.getApprovalByDefKeyAndTypeId(bpmDefinition.getDefKey(), bpmDefinition.getTypeId());
        List<ExecutionStack> stackListData = getFreebackStacks(taskId);
        Map<String,Object> data=new HashMap<String,Object>();
        data.put("isRequired",isRequired);
        data.put("reject",reject);
        data.put("taskAppItems",taskAppItems);
        data.put("forkTaskExecutor",forkTaskExecutor);
        data.put("stackList",stackListData);
        ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取自由退回节点数据成功");
        resultVo.setData(data);
        return resultVo;
    }

    /**
     * 获取可以驳回节点
     * @param taskId
     * @return
     */
    private List<ExecutionStack> getFreebackStacks(String taskId) {
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
            if (stackItem.getIsMultiTask()==ExecutionStack.COMMON_TASK&&!isSignNode(stackItem)){
                if (!containTaskKey.contains(stackItem.getNodeId())){
                    containTaskKey.add(stackItem.getNodeId());
                    stackListData.add(stackItem);
                }
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
        return stackListData;
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
     * 返回目标节点及其节点的处理人员映射列表。
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("tranTaskUserMap")
    @ResponseBody
    public ResultVo tranTaskUserMap(HttpServletRequest request) throws Exception {
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
            TaskEntity taskEntity = bpmService.getTask(taskId);
            //获取所有已审批节点信息
            List<ExecutionStack> stackList=executionStackService.getByActInstId(Long.valueOf(taskEntity.getProcessInstanceId()));
            Long runId =  Long.parseLong(request.getParameter("runId"));
            List<TaskOpinion> list = taskOpinionService.getByRunId(runId);
            list=bpmService.setNewOpinionList(list);
            //把已审批节点的人员信息放入要选择的节点中
            nodeTranUserList=bpmService.getTaskerByStack(nodeTranUserList,list);
        }
        nodeSetSort(defId,nodeTranUserList);
        ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取路径相关数据成功");
        Map<String,Object> data=new HashMap<String, Object>();
        data.put("nodeTranUserList",nodeTranUserList);
        data.put("selectPath",selectPath);
        data.put("scope",scope);
        data.put("canChoicePath",canChoicePath);
        resultVo.setData(data);
        return resultVo;
    }

    /**
     * 任务跳转窗口显示
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("freeJump")
    @ResponseBody
    public ResultVo freeJump(HttpServletRequest request) throws Exception {
        Long runId = RequestUtil.getLong(request,"runId");
        String taskId = RequestUtil.getString(request, "taskId");
        // 获取当前节点的选择器限定配置
        ExecutionEntity execution = bpmService.getExecutionByTaskId(taskId);
        String superExecutionId = execution.getSuperExecutionId();
        String parentActDefId = "";
        if (StringUtil.isNotEmpty(superExecutionId)) {
            ExecutionEntity supExecution = bpmService.getExecution(superExecutionId);
            parentActDefId = supExecution.getProcessDefinitionId();
        }
        String nodeId = execution.getActivityId();
        String processDefinitionId = execution.getProcessDefinitionId();
        BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(processDefinitionId, nodeId, parentActDefId);
        String scope = bpmNodeSet.getScope();

        Map<String, Map<String, String>> jumpNodesMap = bpmService.getJumpNodes(taskId);
        Map<String, String> nodesMap = jumpNodesMap.get("任务节点");
        List<NodeTranUser> nodeTranUserList = new ArrayList<NodeTranUser>();

        for (Map.Entry<String, String> map:nodesMap.entrySet()){

            Set<NodeUserMap> nodeUserMap=new LinkedHashSet<NodeUserMap>();
            NodeTranUser nodeTranUser = new NodeTranUser(map.getKey(),map.getValue(),nodeUserMap);
            Set<TaskExecutor> taskExecutors=new HashSet<TaskExecutor>();
            NodeUserMap nodeUser = new NodeUserMap();
            List<TaskExecutor> taskUsers = getTaskUsers(taskId, map.getKey());
            if(taskUsers.size()>0){
                for (TaskExecutor taskExecutor:taskUsers){
                    taskExecutors.add(taskExecutor);
                    nodeUser.setNodeId(map.getKey());
                    nodeUser.setNodeName(map.getValue());
                    nodeUser.setTaskExecutors(taskExecutors);
                    nodeUserMap.add(nodeUser);
                    nodeTranUser.setNodeUserMapSet(nodeUserMap);
                }
                nodeTranUserList.add(nodeTranUser);
            }else{
                nodeUser.setNodeId(map.getKey());
                nodeUser.setNodeName(map.getValue());
                nodeUser.setTaskExecutors(taskExecutors);
                nodeUserMap.add(nodeUser);
                nodeTranUser.setNodeUserMapSet(nodeUserMap);
                nodeTranUserList.add(nodeTranUser);
            }
        }
        //获取审批历史中的数据
        List<TaskOpinion> list = taskOpinionService.getByRunId(runId);
        //处理审批历史中相同的审批数据
        list = bpmService.setNewOpinionList(list);
        //把已审批节点的人员信息放入要选择的节点中
        nodeTranUserList=bpmService.getTaskerByStack(nodeTranUserList,list);
        ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取自由跳转数据成功");
        Map<String,Object> data=new HashMap<String, Object>();
        data.put("jumpNodeMap",jumpNodesMap);
        data.put("nodeTranUserList",nodeTranUserList);
        data.put("scope",scope);
        resultVo.setData(data);
        return resultVo;
    }


    /**
     * 执行路径节点排序
     * @param defId
     * @param nodeTranUserList
     */
    private void nodeSetSort(Long defId,List<NodeTranUser> nodeTranUserList){
        if (defId==null||nodeTranUserList==null||nodeTranUserList.size()<2){
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

    @RequestMapping("getTaskUsers")
    @ResponseBody
    public List<TaskExecutor> getTaskUsers(String  taskId, String nodeId) throws Exception {

        // 任务Id

        TaskEntity taskEntity = bpmService.getTask(taskId);

        //String nodeId = RequestUtil.getString(request, "nodeId");  所选择的节点Id

        if (StringUtil.isEmpty(nodeId)) {
            nodeId = taskEntity.getTaskDefinitionKey(); // 目标节点Id
        }

        String actDefId = taskEntity.getProcessDefinitionId();

        String actInstId = taskEntity.getProcessInstanceId();

        Map<String, Object> vars = runtimeService.getVariables(taskEntity.getExecutionId());

        Object startUserId = vars.get(BpmConst.StartUser);
        if (startUserId==null){
            return new ArrayList<TaskExecutor>();
        }

        @SuppressWarnings("unchecked")
        List<TaskExecutor> taskExecutorList = bpmNodeUserService.getExeUserIds(actDefId, actInstId, nodeId, startUserId.toString(), "", vars);
       return taskExecutorList;
    }

}

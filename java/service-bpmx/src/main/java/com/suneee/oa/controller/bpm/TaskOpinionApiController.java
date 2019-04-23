package com.suneee.oa.controller.bpm;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.impl.BpmActService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormHandlerService;
import com.suneee.platform.service.form.TaskReadService;
import com.suneee.platform.service.system.SysFileService;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 对象功能:流程任务审批意见控制器类
 * 开发公司:深圳象翌 
 * 开发人员:zousiyu 
 * 创建时间:2018-05-22 
 */
@Controller
@RequestMapping("/api/bpm/taskOpinion/")
public class TaskOpinionApiController extends BaseController {

   
    @Resource
    private BpmService bpmService;
    @Resource
    private TaskService taskService;
    @Resource
    private ProcessRunService processRunService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private BpmNodeSetService bpmNodeSetService;
    
    @Resource
    private BpmFormHandlerService bpmFormHandlerService;
    @Resource
    private BpmFormDefService bpmFormDefService;
    
    @Resource
    private TaskApprovalItemsService taskAppItemService;
   
    @Resource
    private BpmNodeButtonService bpmNodeButtonService;
   
    @Resource
    private TaskReadService taskReadService;
    @Resource
    private CommuReceiverService commuReceiverService;
    @Resource
    private BpmGangedSetService bpmGangedSetService;
    @Resource
    private BpmTaskExeService bpmTaskExeService;
    @Resource
    private TaskOpinionService taskOpinionService;
    @Resource
    private SysFileService sysFileService;
   
    @Resource
    private TaskHistoryService taskHistoryService;
   
    @Resource
    private BpmActService bpmActService;
    @Resource
    private BpmProCopytoService bpmProCopytoService;

    @Resource
    private TaskSignDataService taskSignDataService;

    @Resource
    private BpmNodeSignService bpmNodeSignService;
    /**
     * 返回流程的详细信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("info")
    public ResultVo info(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long curUserId = ContextUtil.getCurrentUserId();
        Long runId = RequestUtil.getLong(request,"runId");
        Long copyId1 = RequestUtil.getLong(request,"copyId");
        String copyId = String.valueOf(copyId1);
        String prePage = RequestUtil.getString(request, "prePage");
        String ctxPath = request.getContextPath();
        ProcessRun processRun = processRunService.getById(runId);
        String form = processRunService.getFormDetailByRunId(runId, ctxPath);

        
        
        //截取出表单权限
        String var = StringUtils.substringBetween(form, "var", "</script>");
        String pression = var.split("=")[1];
        JSONObject jsonObject = JSONObject.fromObject(pression);
        
        
        boolean isExtForm = false;
        if (processRun.getFormDefId() == 0L && StringUtil.isNotEmpty(processRun.getBusinessUrl())) {
            isExtForm = true;
        }

        BpmDefinition bpmDefinition = bpmDefinitionService.getById(processRun.getDefId());
        // 是否是第一个节点
        boolean isFirst = this.isFirst(processRun);
        // 是否允许办结转发
        boolean isFinishedDiver = this.isFinishedDiver(bpmDefinition, processRun);
        // 是否允许追回
        boolean isCanRedo = this.isCanRedo(processRun, isFirst, curUserId);

        boolean isCopy = this.isCopy(bpmDefinition);

        if (null == prePage || "".equals(prePage)) {// 抄送事宜修改状态
            try {
                // 适用于冒泡不能传copyId
                if (null == copyId || "".equals(copyId)) {
                    ProcessRun run = processRunService.getCopyIdByRunid(runId);
                    
                    copyId = run.getCopyId().toString();
                }
                bpmProCopytoService.markCopyStatus(copyId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("update copy matter state failed!");
            }
        }

        // 是否允许打印
        boolean isPrintForm = this.isPrintForm(processRun, bpmDefinition, prePage, copyId);

        // 是否有全局流水号
        boolean hasGlobalFlowNo = PropertyUtil.getBooleanByAlias(SysProperty.GlobalFlowNo);

        // 通过defid和nodeId获取联动设置
        List<BpmGangedSet> bpmGangedSets = bpmGangedSetService.getByDefId(processRun.getDefId());
        JSONArray gangedSetJarray = (JSONArray) JSONArray.fromObject(bpmGangedSets);
        
        
        Map<String,Object> map = new HashMap<String, Object>();
        
        map.put("processRun", processRun);
        map.put("form", form);
        map.put("pression",jsonObject);
        map.put("isPrintForm", isPrintForm);
        map.put("isExtForm", isExtForm);
        map.put("isFirst", isFirst);
        map.put("isCanRedo", isCanRedo);
        map.put("isFinishedDiver", isFinishedDiver);
        map.put("isCopy", isCopy);
        map.put("hasGlobalFlowNo", hasGlobalFlowNo);
        
        
        
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程的详细信息成功",map);

    }
    private boolean isCopy(BpmDefinition bpmDefinition) {
        Short status = bpmDefinition.getStatus();
        if (BpmDefinition.STATUS_DISABLED.equals(status))
            return false;
        if (BpmDefinition.STATUS_INST_DISABLED.equals(status))
            return false;
        return true;
    }

    /**
     * 是否允许办结转办
     *
     * @param bpmDefinition
     * @param processRun
     * @return
     */
    private boolean isFinishedDiver(BpmDefinition bpmDefinition, ProcessRun processRun) {
        if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus()))
            return false;
        if (BeanUtils.isNotEmpty(bpmDefinition.getAllowFinishedDivert()))
            return bpmDefinition.getAllowFinishedDivert().shortValue() == BpmDefinition.ALLOW.shortValue() && processRun.getStatus().shortValue() == ProcessRun.STATUS_FINISH.shortValue();

        return false;
    }

    /**
     * 是否可以追回
     *
     * <pre>
     * 是否可以追回，有以下几个判定条件：
     *  1、流程正在运行；
     *  2、流程非第一个节点；
     *  3、上一步执行人是当前用户；
     *  4、上一步操作是同意；
     *  5、目前该实例只有一个任务。
     *  6、 对应流程定义不能为禁用流程实例状态
     *
     *  1.根据流程获取当前的流程任务列表。
     *  2.根据任务列表去堆栈查找执行人，如果堆栈中有当前人，才可以撤销。
     *
     * </pre>
     *
     * @param processRun
     * @param isFirst
     * @param curUserId
     * @return
     * @throws Exception
     */
    private boolean isCanRedo(ProcessRun processRun, boolean isFirst, Long curUserId) throws Exception {
        if (!processRun.getStatus().equals(ProcessRun.STATUS_RUNNING) || isFirst) return false;
        String actDefId = processRun.getActDefId();
        BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
        if (BpmDefinition.STATUS_INST_DISABLED.equals(bpmDefinition.getStatus())) return false;

        String instanceId = processRun.getActInstId();
        TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(instanceId, curUserId);
        if(taskOpinion == null) return false;

        Short checkStatus = taskOpinion.getCheckStatus();

        if (!TaskOpinion.STATUS_AGREE.equals(checkStatus) && !TaskOpinion.STATUS_REFUSE.equals(checkStatus)) return false;


        ResultMessage resultMsg =processRunService.checkRecover(processRun.getRunId());

        return ResultMessage.Success== resultMsg.getResult();

    }

    /**
     * 是否是第一个节点
     *
     * @param processRun
     * @return
     * @throws Exception
     */
    private boolean isFirst(ProcessRun processRun) throws Exception {
        boolean isFirst = false;
        if (BeanUtils.isEmpty(processRun))
            return isFirst;
        Long instId = Long.parseLong(processRun.getActInstId());
        String actDefId = processRun.getActDefId();
        List<TaskOpinion> taskOpinionList = taskOpinionService.getCheckOpinionByInstId(instId);
        String nodeId = "";
        // 判断起始节点后是否有多个节点
        if (NodeCache.isMultipleFirstNode(actDefId)) {
            nodeId = processRun.getStartNode();
        } else {
            FlowNode flowNode = NodeCache.getFirstNodeId(actDefId);
            if (flowNode == null)
                return isFirst;
            nodeId = flowNode.getNodeId();
        }
        for (TaskOpinion taskOpinion : taskOpinionList) {
            isFirst = nodeId.equals(taskOpinion.getTaskKey());
            if (isFirst)
                break;
        }
        return isFirst;
    }

    /**
     * 是否允许打印表单
     *
     * <pre>
     * 	1.允许打印表单
     * 		是我的办结
     * 	2.允许
     * 		办结打印
     * 	3 允许
     * 		抄送打印
     *
     * </pre>
     *
     * @param processRun
     * @param bpmDefinition
     * @param prePage
     * @param copyId
     * @return
     */
    private boolean isPrintForm(ProcessRun processRun, BpmDefinition bpmDefinition, String prePage, String copyId) {
        if (bpmDefinition.getIsPrintForm() == null || bpmDefinition.getIsPrintForm().intValue() != 1 || processRun.getStatus().shortValue() != ProcessRun.STATUS_FINISH.shortValue()) {
            return false;
        } else {
            return true;
        }
    }



    
    /**
     * 查看抄送人流程列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("getCopyUserByInstId")
    @Action(description = "查看抄送人流程列表")
    public ResultVo getCopyUserByInstId(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Long runId = RequestUtil.getLong(request, "runId");
        String orgCode= ContextSupportUtil.getCurrentEnterpriseCode();
        List<BpmProCopyto> list=bpmProCopytoService.getUserInfoByRunIdAndOrgCode(runId,orgCode);
        
        
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取抄送人流程列表成功",list);
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
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long runId = RequestUtil.getLong(request, "runId", 0L);
        String taskId = RequestUtil.getString(request, "taskId");
        ProcessRun processRun = null;
        if (runId != 0L) {
            processRun = processRunService.getById(runId);
        } else {
            TaskEntity task = bpmService.getTask(taskId);
            processRun = processRunService.getByActInstanceId(new Long(task.getProcessInstanceId()));
        }
        
        if (processRun == null){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"实例不存在");
        }
           
        List<HistoricTaskInstance> hisTasks = bpmService.getHistoryTasks(processRun.getActInstId());
        
        Map<String,Object> map = new HashMap();
        map.put("processRun", processRun);
        map.put("isReturn", request.getParameter("isReturn"));
        map.put("hisTasks", hisTasks);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取实例成功",map);
        
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
            /*TaskEntity taskEntity = bpmService.getTask(taskId);
            //获取所有已审批节点信息
            List<ExecutionStack> stackList=executionStackService.getByActInstId(Long.valueOf(taskEntity.getProcessInstanceId()));*/
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


}


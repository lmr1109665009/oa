package com.suneee.rs.controller;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.constant.UserConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.dao.bpm.ProcessRunDao;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import net.sf.json.JSONArray;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.util.WarningSetting;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmFormFieldService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.suneee.ucp.base.vo.ResultVo;
@Controller
@RequestMapping("/platform/bpm/todo/")
public class TaskToDoController {
    @Resource
    ProcessRunDao processRunDao;
    @Resource
    private BpmService bpmService;
    @Resource
    private TaskReminderService reminderService;
    @Autowired
    private BpmFormFieldService formFieldService;
    @Autowired
    private BpmFormTableService formTableService;
    @Autowired
    private GlobalTypeService globalTypeService;
    @Resource
    private TaskOpinionService taskOpinionService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProcessRunService processRunService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private BpmNodeSetService bpmNodeSetService;
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
        String account = RequestUtil.getString(request, "account");
        if (account == null) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户账号不能为空！");
        }
        SysUser newUser = processRunService.getUseridByAccount(account);
        //根据userId对用户进行判断
        if (newUser == null) {
            newUser = processRunService.getUseridByAliasname(account);
            if(newUser==null){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户账号不存在");
            }
        }
        com.suneee.eas.common.utils.ContextUtil.getSession().setAttribute(UserConstant.SESSION_USER_KEY,newUser);
        try {
            if (StringUtils.isNotEmpty(nodePath))
                filter.getFilters().put("nodePath", nodePath + "%");
            if(filter.getFilters().size()>0&& StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }
            flowCateFilter(filter);
            //处理表单自定义字段查询
            processFormQuery(request,filter);
            //按照查询条件查询数据
            List<TaskEntity> list = bpmService.getMyTaskss(filter);
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
            String account = RequestUtil.getString(request, "account");
            if (account == null) {
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户账号不能为空！");
            }
            //SysUser newUser = sysUserService.getByAccount(account);
            SysUser newUser = processRunService.getUseridByAccount(account);
            //根据userId对用户进行判断
            if (newUser == null) {
                newUser = processRunService.getUseridByAliasname(account);
                if(newUser==null){
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户账号不存在");
                }
            }
            com.suneee.eas.common.utils.ContextUtil.getSession().setAttribute(UserConstant.SESSION_USER_KEY,newUser);
            JSONObject json = new JSONObject();
            QueryFilter filter = new QueryFilter(request, true);
            String nodePath = RequestUtil.getString(request, "nodePath");// 右侧流程分类
            if (StringUtils.isNotEmpty(nodePath))
                filter.getFilters().put("nodePath", nodePath + "%");
            if(filter.getFilters().size()>0&&StringUtil.isNotEmpty((String) filter.getFilters().get("subject"))){
                String subject = com.suneee.ucp.base.util.StringUtils.changceFilter((String)filter.getFilters().get("subject"));
                filter.addFilter("subject",subject);
            }
            filter.addFilter("assignee", ContextSupportUtil.getCurrentUserId().toString());// 用户id
            flowCateFilter(filter);
            //处理表单自定义字段查询
            processFormQuery(request,filter);
            //按照查询条件查询数据
            //List<ProcessRun> list = processRunService.getAlreadyMattersList(filter);
            List<ProcessRun> list = processRunService.getAlreadyMattersLists(filter);
            //处理已办事宜持续时间
            for(ProcessRun pr:list){
                if(pr.getStatus() != 2 && pr.getStatus() != 3 && pr.getStatus() != 10){
                    Long durationTime = TimeUtil.getTime(pr.getCreatetime(),new java.util.Date());
                    pr.setDuration(durationTime);
                }
            }
            for (ProcessRun processRun : list) {
                if (processRun.getStatus().shortValue() != ProcessRun.STATUS_FINISH.shortValue()) {
                    // 1.查找当前用户是否有最新审批的任务
                    TaskOpinion taskOpinion = taskOpinionService.getLatestUserOpinion(processRun.getActInstId(), ContextSupportUtil.getCurrentUserId());
                    if (BeanUtils.isNotEmpty(taskOpinion))
                        processRun.setRecover(ProcessRun.STATUS_RECOVER);
                }
            }
            ListModel model = CommonUtil.getListModel((PageList) list);
            List<ProcessRun> list1 = model.getRowList();
            Iterator<ProcessRun> iterator = list1.iterator();
            while(iterator.hasNext()){
                if(iterator.next().getCreatorId()==newUser.getUserId()){
                    iterator.remove();
                }
            }
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
     * 显示意见列表。
     * @param runId
     * @return
     */
    @RequestMapping("listJson")
    @ResponseBody
    public Map<String,Object> getOptionByRunId(@RequestParam(value="runId") Long runId){
        Map<String,Object> rtnMap=new HashMap<String,Object>();
        ProcessRun processRun = processRunService.getById(runId);
        //取得关联的流程实例ID
        List<TaskOpinion> list = taskOpinionService.getByRunId(runId);
        //设置代码执行人
        list = taskOpinionService.setTaskOpinionExecutor(list);
        //list.get(0).setExeUserId(ContextSupportUtil.getCurrrentUserId());
        JSONArray jsonArray=formatTaskOpinionList(list);
        rtnMap.put("processRun", processRun);
        rtnMap.put("taskOpinionList", jsonArray);
        return rtnMap;
    }
    /**
     * <pre>
     * 把taskOpinion封装成jsonArray对象，组成结构:
     * 思路:
     * 按照时间排序，如果i上的taskName跟i+1上的一致就放在同一个json上
     * eg:
     * json:{taskName:name,list:[taskopinion1,taskopinion2,...]}
     * </pre>
     * @param list
     * @return JSONArray
     * @exception
     * @since 1.0.0
     */
    private JSONArray formatTaskOpinionList(List<TaskOpinion> list) {
        JSONArray jsonArray = new JSONArray();
        for (TaskOpinion to : list) {
            String tn = "";
            List<net.sf.json.JSONObject> tos = null;
            net.sf.json.JSONObject jsonObject = null;
            String taskName = to.getTaskName();
            if(StringUtil.isNotEmpty(to.getSuperExecution())){
                taskName=taskName+"【子流程】";
            }
            if (!jsonArray.isEmpty()) {
                jsonObject = jsonArray.getJSONObject(jsonArray.size() - 1);
                tn = jsonObject.getString("taskName");
                tos = (List<net.sf.json.JSONObject>) jsonObject.get("list");
            }
            if (tn.equals(taskName)) {
                net.sf.json.JSONObject jsonObj=convertOpinion(to);
                tos.add(jsonObj);
            } else {
                jsonObject = new net.sf.json.JSONObject();
                tos = new ArrayList<net.sf.json.JSONObject>();
                net.sf.json.JSONObject jsonObj=convertOpinion(to);
                tos.add(jsonObj);
                jsonObject.put("taskName", taskName);
                jsonObject.put("list", tos);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }
    private net.sf.json.JSONObject convertOpinion(TaskOpinion opinion){
        net.sf.json.JSONObject jsonObj=new net.sf.json.JSONObject();
        jsonObj.accumulate("opinionId", opinion.getOpinionId()) ;
        String taskName = opinion.getTaskName();
        if(StringUtil.isNotEmpty(opinion.getSuperExecution())){
            taskName=taskName+"【子流程】";
        }
        jsonObj.accumulate("taskName",taskName ) ;
        jsonObj.accumulate("exeFullname", opinion.getExeFullname()) ;
        jsonObj.accumulate("exeUserId", opinion.getExeUserId()) ;
        jsonObj.accumulate("status", opinion.getStatus()) ;
        jsonObj.accumulate("checkStatus", opinion.getCheckStatus()) ;
        jsonObj.accumulate("startTimeStr", opinion.getStartTimeStr()) ;
        jsonObj.accumulate("durTimeStr", opinion.getDurTimeStr()) ;
        jsonObj.accumulate("endTimeStr", opinion.getEndTimeStr()) ;
        jsonObj.accumulate("opinion", opinion.getOpinion()==null?"":opinion.getOpinion()) ;
        List<SysUser> users= opinion.getCandidateUsers();
        if(BeanUtils.isNotEmpty(users)){
            JSONArray ary=new JSONArray();
            for(SysUser user:users){
                net.sf.json.JSONObject userJson=new net.sf.json.JSONObject();
                userJson.accumulate("userId",user.getUserId());
                userJson.accumulate("fullname",user.getFullname());
                ary.add(userJson);
            }
            jsonObj.accumulate("candidateUsers", ary) ;
        }
        return jsonObj;
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
        String account = request.getParameter("account");
        if (account == null) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户账号不能为空！");
        }
        SysUser newUser = processRunService.getUseridByAccount(account);
        //根据userId对用户进行判断
        if (newUser == null) {
            newUser = processRunService.getUseridByAliasname(account);
            if(newUser==null){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户账号不存在");
            }
        }
        com.suneee.eas.common.utils.ContextUtil.getSession().setAttribute(UserConstant.SESSION_USER_KEY,newUser);
        String runId = request.getParameter("runId");
        ProcessRun processRun= processRunDao.getByRunId(runId);
        TaskOpinion taskOpinion = taskOpinionService.getByActInstIds(processRun.getActInstId());
        String taskId = taskOpinion.getTaskId().toString();
        int isStart = RequestUtil.getInt(request, "isStart", 0);
        //String taskId = request.getParameter("taskId");
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
        Long startUserId = ContextSupportUtil.getCurrentUserId();
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


        List<NodeTranUser> nodeTranUserLists = new ArrayList<NodeTranUser>();
        if(nodeTranUserList.size()>0){
            for (int i = 0; i < nodeTranUserList.size(); i++) {
                NodeTranUser ntu = new NodeTranUser();
                ntu.setNodeName(nodeTranUserList.get(i).getNodeName());
                ntu.setNodeId(nodeTranUserList.get(i).getNodeId());
                StringBuffer nodeUserName = new StringBuffer();
                if(nodeTranUserList.get(i).getNodeUserMapSet().size()>0){
                    Iterator<NodeUserMap> iterator = nodeTranUserList.get(i).getNodeUserMapSet().iterator();
                    while (iterator.hasNext()){
                        Set<TaskExecutor> taskExecutors = iterator.next().getTaskExecutors();
                        if(taskExecutors.size()>0){
                            Iterator<TaskExecutor> iterator2 = taskExecutors.iterator();
                            while(iterator2.hasNext()){
                                Set<ISysUser> sysUser = iterator2.next().getSysUser();
                                if(sysUser.size()>0){
                                    Iterator<ISysUser> iterator3 = sysUser.iterator();
                                    while(iterator3.hasNext()){
                                        nodeUserName.append(iterator3.next().getFullname()).append(",");
                                    }
                                }
                            }

                        }
                    }
                }
                if(!(nodeUserName.toString().equals(""))){
                    String nodeUserNames = nodeUserName.substring(0, nodeUserName.length()-1).toString();
                    ntu.setNodeUserName(nodeUserNames);
                }
                nodeTranUserLists.add(ntu);
            }
        }
        if(nodeTranUserLists.size()>0){
            for (NodeTranUser node:nodeTranUserLists){
                if(node.getNodeId().contains("EndEvent")){
                    //nodeTranUserLists.clear();
                    break;
                }
            }
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("nodeTranUserList",nodeTranUserLists);
        boolean flag = false;
        if(nodeTranUserLists.size()>0){
            flag = true;
        }
        resultMap.put("selectPath",selectPath);
        resultMap.put("scope",scope);
        resultMap.put("canChoicePath",flag);
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
}

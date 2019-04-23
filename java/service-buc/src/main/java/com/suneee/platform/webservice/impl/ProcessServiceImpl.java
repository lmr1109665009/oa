package com.suneee.platform.webservice.impl;

import com.suneee.core.api.bpm.model.IProcessRun;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.page.PageBean;
import com.suneee.core.util.*;
import com.suneee.platform.dao.bpm.BpmProStatusDao;
import com.suneee.platform.dao.bpm.TaskDao;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.impl.BpmActService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.webservice.api.ProcessService;
import com.suneee.platform.webservice.model.BpmFinishTask;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 流程对外服务接口实现类
 * 
 * @author csx
 * 
 */
public class ProcessServiceImpl implements ProcessService {
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private BpmService bpmService;
	@Resource
	private TaskService taskService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
    BpmActService bpmActService;
	@Resource
	private TaskSignDataService taskSignDataService;
	@Resource
	private TaskApprovalItemsService taskApprovalItemsService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private ExecutionStackService executionStackService;
	@Resource
	private BpmProStatusDao bpmProStatusDao;
	@Resource
	private TaskDao taskDao;
	@Resource
	private BpmNodeSignService bpmNodeSignService;
	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService;

	public String addSignUsers(String signUserIds, String taskId) {
		if (StringUtils.isNotEmpty(taskId) && StringUtils.isNotEmpty(signUserIds)) {
			try {
				taskSignDataService.addSign(signUserIds, taskId, null, null);
				return genMessage(true, "成功增加会签人员");
			} catch (Exception e) {
				return genMessage(false, e.getMessage());
			}
		} else {
			return genMessage(false, "signUserIds或taskId不能为空");
		}
	}

	public String canSelectedUser(String taskId) {
		boolean isHidePath = false;
		TaskEntity taskEntity = bpmService.getTask(taskId);
		String nodeId = taskEntity.getTaskDefinitionKey();
		String actDefId = taskEntity.getProcessDefinitionId();
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId,"");
		if (BpmNodeSet.HIDE_PATH.equals(bpmNodeSet.getIsHidePath())) {
			isHidePath = true;
		}
		return genMessage(isHidePath, "");
	}

	public String doNext(String xml) {
		ContextUtil.clearAll();

		Document doc = Dom4jUtil.loadXml(xml);
		Element root = doc.getRootElement();
		String taskId = root.attributeValue("taskId");
		String userAccount = root.attributeValue("account");
		String voteAgree = root.attributeValue("voteAgree");
		String voteContent = root.attributeValue("voteContent");
		String nextNodeId = root.attributeValue("nextNodeId");
		String nextUser = root.attributeValue("nextUser");
		String businessKey = root.attributeValue("businessKey");
		String isBack = root.attributeValue("isBack");
		String nextUserID = root.attributeValue("nextUserID");

		if (StringUtil.isEmpty(taskId)) {
			String errorMesage = genMessage(false, "流程任务ID必须填写!");
			String rtnXml = genMessage(false, errorMesage);
			return rtnXml;
		}

		ProcessCmd processCmd = new ProcessCmd();

		if (StringUtil.isNotEmpty(nextNodeId)) {
			processCmd.setLastDestTaskIds(new String[] { nextNodeId });
			processCmd.setDestTask(nextNodeId);
		}
		
		if(StringUtil.isNotEmpty(businessKey)){
			processCmd.setBusinessKey(businessKey);
		}

		if (StringUtil.isNotEmpty(nextUser)) {
			StringBuffer sb = new StringBuffer();
			for (String uac : nextUser.split(",")) {
				SysUser sysUser = sysUserService.getByAccount(uac);
				String str = "user" + "^" + sysUser.getUserId() + "^" + sysUser.getFullname();
				if (sb.length() > 0) {
					sb.append("#");
				}
				sb.append(str);
			}
			processCmd.setLastDestTaskUids(new String[] { sb.toString() });
		}
		
		if(StringUtil.isNotEmpty(nextUserID)){
			StringBuffer sb = new StringBuffer();
			for(String uId : nextUserID.split(",")){
				SysUser user = sysUserService.getById(new Long(uId));
				String str = "user" + "^" + user.getUserId() + "^" + user.getFullname();
				if (sb.length() > 0) {
					sb.append("#");
				}
				sb.append(str);
			}
			processCmd.setLastDestTaskUids(new String[] { sb.toString() });
		}

		if (StringUtil.isNotEmpty(taskId)) {
			processCmd.setTaskId(taskId);
		}
		// 处理用户账号必填。
		if (StringUtil.isEmpty(userAccount)) {
			String errorMesage = genMessage(false, "处理用户账号必填!");
			String rtnXml = genMessage(false, errorMesage);
			return rtnXml;
		}

		processCmd.setUserAccount(userAccount);

		if (StringUtil.isNotEmpty(voteAgree)) {
			processCmd.setVoteAgree(Short.parseShort(voteAgree));
		}

		if (StringUtil.isNotEmpty(voteContent)) {
			processCmd.setVoteContent(voteContent);
		}

		if (StringUtil.isNotEmpty(isBack) && !"null".equals(isBack)) {
			processCmd.setBack(Integer.parseInt(isBack));
		}

		try {
			// 计算流程变量。
			List<Element> vars = root.elements("var");
			if (BeanUtils.isNotEmpty(vars)) {
				Map<String, Object> variables = addVars(vars);
				processCmd.setVariables(variables);
			}
			SysUser user = (SysUser) ContextUtil.getCurrentUser();
			if (user == null) {
				ContextUtil.setCurrentUserAccount(userAccount);
			}
			Element formDataEl= root.element("formData");
			if(formDataEl!=null){
				String formData=formDataEl.getStringValue();
				processCmd.setFormData(formData);
			}
			
			processRunService.nextProcess(processCmd);
			return genMessage(true, "任务处理成功");
		} catch (Exception ex) {
			return genMessage(false, ex.getMessage());
		}
	}

	public String endProcessByTaskId(String taskId) {
		try {
			bpmActService.endProcessByTaskId(taskId);
			return genMessage(true, "成功结束流程实例");
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getApprovalItems(String taskId) {
		TaskEntity taskEntity = bpmService.getTask(taskId);
		if (taskEntity == null)
			return null;
		ProcessRun processRun = processRunService.getByActInstanceId(Long.parseLong(taskEntity.getProcessInstanceId()));
		if (processRun == null)
			return null;
		String actDefId = processRun.getActDefId();

		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
		List<String> list = taskApprovalItemsService.getApprovalByDefKeyAndTypeId(bpmDefinition.getDefKey(), bpmDefinition.getTypeId());
		if (list == null)
			return null;
		Document createDoc = DocumentHelper.createDocument();
		Element docRoot = createDoc.addElement("list");
		for (String appItem : list) {
			Element elements = docRoot.addElement("item");
			elements.addAttribute("word", appItem.toString());
		}
		return Dom4jUtil.docToPrettyString(createDoc);
	}

	public String getBpmDefinition(String xml) {
		Document doc = Dom4jUtil.loadXml(xml);
		Element root = doc.getRootElement();
		String account = root.attributeValue("account"); // 用户账号
		String pageSize = root.attributeValue("pageSize");// 页面记录数大小
		String currentPage = root.attributeValue("currentPage");// 当前页
		SysUser sysUser = sysUserService.getByAccount(account);
		if (sysUser == null) {
			return genMessage(false, "不存在该帐号的用户");
		}
		String userId = sysUser.getUserId().toString();
		PageBean pb = new PageBean();
		if (StringUtil.isNotEmpty(pageSize)) {
			pb.setPagesize(new Integer(pageSize));
		}
		if (StringUtils.isNotEmpty(currentPage)) {
			pb.setCurrentPage(new Integer(currentPage));
		}

		if (StringUtils.isEmpty(userId))
			return null;
		String isNeedRight = "yes";
		String actRights = " ";
		if (ContextUtil.isSuperAdmin((ISysUser) sysUser)) {
			isNeedRight = "";
		} else {
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(new Long(userId), BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.MANAGEMENT, false, false);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			actRights = (String) actRightMap.get("authorizeIds");
		}

		try {
			List<BpmDefinition> bpmDefinitionList = bpmDefinitionService.getUserBpmList(actRights, isNeedRight);
			return BpmDefinition2Xml(bpmDefinitionList);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getByBusinessKey(String businessKey) {
		try {
			ProcessRun processRun = processRunService.getByBusinessKey(businessKey);
			Element element = ProcessRun2Xml(null, processRun);
			return Dom4jUtil.docToPrettyString(element.getDocument());
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}
	
	@Override
	public String getDestNodeHandleUsers(String taskId, String destNodeId) {
		try {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			String instanceId = taskEntity.getProcessInstanceId();
			Map<String, Object> vars = new HashMap<String, Object>();
			vars.put("executionId", taskEntity.getExecutionId());
			Set<TaskExecutor> userSet = bpmService.getNodeHandlerUsers(instanceId, destNodeId, vars);
			return TaskExecutorSet2Xml(userSet);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getFinishTask(String xml) {
		Document doc = Dom4jUtil.loadXml(xml);
		Element root = doc.getRootElement();
		String account = root.attributeValue("account"); // 用户账号
		String taskName = root.attributeValue("taskName"); // 任务名称
		String subject = root.attributeValue("subject");// 任务标题
		String pageSize = root.attributeValue("pageSize");// 页面记录数大小
		String currentPage = root.attributeValue("currentPage");// 当前页
		SysUser sysUser = sysUserService.getByAccount(account);
		if (sysUser == null) {
			return genMessage(false, "不存在该帐号的用户");
		}
		String userId = sysUser.getUserId().toString();
		PageBean pb = new PageBean();
		if (StringUtil.isNotEmpty(pageSize)) {
			pb.setPagesize(new Integer(pageSize));
		}
		if (StringUtils.isNotEmpty(currentPage)) {
			pb.setCurrentPage(new Integer(currentPage));
		}
		try {
			List<BpmFinishTask> bpmFinishTaskList = taskOpinionService.getByFinishTask(Long.parseLong(userId), subject, taskName, pb);
			return BpmFinishTask2Xml(bpmFinishTaskList,pb.getTotalCount()+"");
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getFinishTaskDetailUrl(String actInstId, String nodeKey) {
		ProcessRun processRun = processRunService.getByActInstanceId(new Long(actInstId));
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(processRun.getActDefId(), nodeKey, "");

		String detailUrl = bpmNodeSet.getDetailUrl();
		if (StringUtils.isNotEmpty(detailUrl)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pk", processRun.getBusinessKey());
			detailUrl = StringUtil.formatParamMsg(detailUrl, map);
			return genMessage(true, detailUrl);
		} else {
			return genMessage(false, "未获取到明细地址");
		}
	}

	public String getMyProcessRunByXml(String xml) {
		Document doc = Dom4jUtil.loadXml(xml);
		Element root = doc.getRootElement();
		String account = root.attributeValue("account"); // 用户账号
		String subject = root.attributeValue("subject");// 任务标题
		String status = root.attributeValue("status");// 流程实例状态
		String pageSize = root.attributeValue("pageSize");// 页数
		String currentPage = root.attributeValue("currentPage");// 当前页
		SysUser sysUser = sysUserService.getByAccount(account);
		if (sysUser == null) {
			return genMessage(false, "不存在该帐号的用户");
		}
		String userId = sysUser.getUserId().toString();
		Short sts = 1;
		if (StringUtils.isNotEmpty(status)) {
			sts = Short.parseShort(status);
		}
		PageBean pb = new PageBean();
		if (StringUtil.isNotEmpty(pageSize)) {
			pb.setPagesize(new Integer(pageSize));
		}
		if (StringUtils.isNotEmpty(currentPage)) {
			pb.setCurrentPage(new Integer(currentPage));
		}
		try {
			List<ProcessRun> processList = processRunService.getMyProcessRun(Long.parseLong(userId), subject, sts, pb);
			return ProcessRunList2Xml(processList);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getNextFlowNodes(String taskId, String account) {
		SysUser sysUser = sysUserService.getByAccount(account);
		if (sysUser == null) {
			return genMessage(false, "不存在该帐号的用户");
		}
		Long userId = sysUser.getUserId();
		List<NodeTranUser> list = bpmService.getNodeTaskUserMap(taskId, userId, true);

		Document document = DocumentHelper.createDocument();
		Element nodesElement = document.addElement("list");
		for (NodeTranUser node : list) {
			Element nodeElement = nodesElement.addElement("node");
			Dom4jUtil.addAttribute(nodeElement, "nodeId", node.getNodeId());
			Dom4jUtil.addAttribute(nodeElement, "nodeName", node.getNodeName());
		}
		return Dom4jUtil.docToPrettyString(document);
	}

	public String getProcessOpinionByActInstId(String actInstId) {
		try {
			List<TaskOpinion> taskOpinionList = taskOpinionService.getByActInstId(actInstId);
			return TaskOpinionList2Xml(taskOpinionList);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getProcessOpinionByRunId(String runId) {
		if (StringUtil.isEmpty(runId)) {
			return genMessage(false, "runId不能为空");
		}
		try {
			ProcessRun processRun = processRunService.getById(Long.parseLong(runId));
			List<TaskOpinion> taskOpinionList = taskOpinionService.getByActInstId(processRun.getActInstId());
			return TaskOpinionList2Xml(taskOpinionList);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getProcessRun(String xml) {
		Document doc = Dom4jUtil.loadXml(xml);
		Element root = doc.getRootElement();
		String defId = root.attributeValue("defId"); // 流程定义ID
		String actDefId = root.attributeValue("actDefId"); // act流程定义ID
		String defKey = root.attributeValue("defKey"); // 流程定义Key
		String pageSize = root.attributeValue("pageSize");// 页面记录数大小
		String currentPage = root.attributeValue("currentPage");// 当前页数

		// 流程定义ID、act流程定义ID、流程定义Key三选一。
		if (StringUtil.isEmpty(defId) && StringUtil.isEmpty(actDefId) && StringUtil.isEmpty(defKey)) {
			return genMessage(false, "流程定义ID、act流程定义ID、流程定义Key必须填写一个!");
		}

		if (StringUtils.isNotEmpty(defKey)) {
			BpmDefinition bpmDefinition = bpmDefinitionService.getMainByDefKey(defKey);
			if (bpmDefinition != null) {
				actDefId = bpmDefinition.getActDefId();
			}
		}

		if (StringUtils.isNotEmpty(defId)) {
			BpmDefinition bpmDefinition = bpmDefinitionService.getById(Long.parseLong(defId));
			if (bpmDefinition != null) {
				actDefId = bpmDefinition.getActDefId();
			}
		}

		PageBean pb = new PageBean();
		if (StringUtil.isNotEmpty(pageSize)) {
			pb.setPagesize(new Integer(pageSize));
		} else {
			pb.setPagesize(20);
		}
		if (StringUtils.isNotEmpty(currentPage)) {
			pb.setCurrentPage(new Integer(currentPage));
		} else {
			pb.setCurrentPage(1);
		}
		try {
			List<ProcessRun> processList = processRunService.getByActDefId(actDefId, pb);
			return ProcessRunList2Xml(processList);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getProcessRunByRunId(String runId) {
		if (StringUtil.isEmpty(runId)) {
			return genMessage(false, "runId不能为空");
		}
		try {
			ProcessRun processRun = processRunService.getById(Long.parseLong(runId));
			Element element = ProcessRun2Xml(null, processRun);
			return Dom4jUtil.docToPrettyString(element.getDocument());
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getProcessRunByTaskId(String taskId) {
		try {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));
			Element element = ProcessRun2Xml(null, processRun);
			return Dom4jUtil.docToPrettyString(element.getDocument());
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getStatusByRunidNodeId(String runId, String nodeId) {
		try {
			Integer status = bpmProStatusDao.getStatusByRunidNodeid(runId, nodeId);
			if (status == null) {
				return genMessage(false, "未获取到该实例该节点的状态");
			} else {
				return genMessage(true, status.toString());
			}
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getTaskByTaskId(String taskId) {
		try {
			ProcessTask processTask = taskDao.getByTaskId(taskId);
			constructTaskUrl(processTask);
			Element element = ProcessTask2Xml(null, processTask);
			return Dom4jUtil.docToPrettyString(element.getDocument());
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getTaskFormUrl(String taskId) {
		try {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			if (taskEntity == null)
				return null;
			Map<String, Object> variables = taskService.getVariables(taskId);
			String bussinessKey = variables.get("businessKey").toString();
			if (StringUtil.isEmpty(bussinessKey)) {
				bussinessKey = (String) taskService.getVariable(taskId, "businessKey");
			}
			String form = "";
			BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(taskEntity.getProcessDefinitionId(), taskEntity.getTaskDefinitionKey(), "");
			if (bpmNodeSet == null)
				return null;
			String formUrl = bpmNodeSet.getFormUrl();
			// 表单的URL和表单key不为空。
			if (StringUtil.isNotEmpty(formUrl) && StringUtil.isNotEmpty(bussinessKey)) {
				// 替换表单的主键。
				// 例如：get.ht?id={pk}&flowRunId={flowRunId}
				form = formUrl.replaceFirst(BpmConst.FORM_PK_REGEX, bussinessKey);
				if (variables != null) {
					Pattern regex = Pattern.compile("\\{(.*?)\\}");
					Matcher regexMatcher = regex.matcher(form);
					while (regexMatcher.find()) {
						String toreplace = regexMatcher.group(0);
						String varName = regexMatcher.group(1);
						if (!variables.containsKey(varName))
							continue;
						form = form.replace(toreplace, variables.get(varName).toString());
					}
				}
			}
			if (StringUtil.isEmpty(form)) {
				return genMessage(false, "未获取到任务对应的url地址");
			} else {
				return genMessage(true, form);
			}
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getTaskNameByTaskId(String taskId) {
		ProcessTask processTask = taskDao.getByTaskId(taskId);
		if (processTask != null) {
			return genMessage(true, processTask.getName());
		} else {
			return genMessage(false, "未获取到该taskId的任务");
		}
	}

	public String getTaskNode(String taskId) {
		try {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			if (taskEntity == null)
				return genMessage(false, "未获取到该taskId的任务");
			ProcessDefinitionEntity pde = bpmService.getProcessDefinitionEntity(taskEntity.getProcessDefinitionId());
			if (pde == null)
				return genMessage(false, "未获取流程定义");
			ActivityImpl curAct = pde.findActivity(taskEntity.getTaskDefinitionKey());
			if (curAct == null)
				return genMessage(false, "未获取节点定义");
			String nodeName = (String) curAct.getProperty("name");
			String nodeType = (String) curAct.getProperty("type");
			String nodeId = curAct.getId();

			Document createDoc = DocumentHelper.createDocument();
			Element docRoot = createDoc.addElement("node");
			docRoot.addAttribute("nodeId", nodeId);
			docRoot.addAttribute("nodeName", nodeName);
			docRoot.addAttribute("nodeType", nodeType);
			return Dom4jUtil.docToPrettyString(createDoc);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getTaskOutNodes(String taskId) {
		TaskEntity taskEntity = bpmService.getTask(taskId);
		if (taskEntity == null)
			return null;
		FlowNode flowNode = NodeCache.getByActDefId(taskEntity.getProcessDefinitionId()).get(taskEntity.getTaskDefinitionKey());
		if (flowNode == null)
			return null;
		Document createDoc = DocumentHelper.createDocument();
		Element docRoot = createDoc.addElement("list");
		for (FlowNode fnode : flowNode.getNextFlowNodes()) {
			Element elements = docRoot.addElement("node");
			elements.addAttribute("nodeId", fnode.getNodeId());
			elements.addAttribute("nodeName", fnode.getNodeName());
			elements.addAttribute("nodeType", fnode.getNodeType());
		}
		return Dom4jUtil.docToPrettyString(createDoc);
	}

	public String getTasksByAccount(String xml) {
		try {
			Document doc = Dom4jUtil.loadXml(xml);
			Element root = doc.getRootElement();
			String account = root.attributeValue("account");

			if (StringUtil.isEmpty(account)) {
				return genMessage(false, "必须传入用户账号");
			}
			SysUser user = sysUserService.getByAccount(account);
			if (user == null) {
				return genMessage(false, "不存在该账号的用户");
			}

			String taskName = Dom4jUtil.getString(root, "taskNodeName", true);
			String subject = Dom4jUtil.getString(root, "subject", true);
			String processName = Dom4jUtil.getString(root, "processName", true);
			String orderField = Dom4jUtil.getString(root, "orderField");
			String orderSeq = Dom4jUtil.getString(root, "orderSeq");
			String pageSize = Dom4jUtil.getString(root, "pageSize");
			String currentPage = Dom4jUtil.getString(root, "currentPage");

			PageBean pb = new PageBean();

			if (StringUtil.isNotEmpty(pageSize)) {
				pb.setPagesize(Integer.parseInt(pageSize));
			}
			if (StringUtil.isNotEmpty(currentPage)) {
				pb.setCurrentPage(Integer.parseInt(currentPage));
			}

			List<ProcessTask> processTasks = taskDao.getTasks(user.getUserId(), taskName, subject, processName, orderField, orderSeq, pb);
			constructTaskUrls(processTasks);
			return ProcessTaskList2Xml(processTasks);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getTasksByRunId(String runId) {
		String str = "";
		try {
			if (StringUtil.isEmpty(runId)) {
				return genMessage(false, "runId不能为空");
			}
			List<ProcessTask> list = taskDao.getTasksByRunId(Long.parseLong(runId));
			str = ProcessTaskList2Xml(list);
			return str;
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getVariablesByRunId(String runId) {
		if (StringUtil.isEmpty(runId)) {
			return genMessage(false, "runId不能为空");
		}
		try {
			ProcessRun processRun = processRunService.getById(Long.parseLong(runId));
			Map<String, Object> varMap = runtimeService.getVariables(processRun.getActInstId().toString());
			Iterator<Map.Entry<String, Object>> it = varMap.entrySet().iterator();
			Document createDoc = DocumentHelper.createDocument();
			Element docRoot = createDoc.addElement("list");
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				Element elements = docRoot.addElement("var");
				elements.addAttribute("varName", entry.getKey());
				if (entry.getValue() != null) {
					elements.addAttribute("varVal", entry.getValue().toString());
				}
			}
			return Dom4jUtil.docToPrettyString(createDoc);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String getVariablesByTaskId(String taskId) {
		try {
			Task task = bpmService.getTask(taskId);
			Map<String, Object> varMap = runtimeService.getVariables(task.getExecutionId());
			return VarMap2Xml(varMap);
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String isAllowAddSign(String account, String taskId) {
		try {
			SysUser sysUser = sysUserService.getByAccount(account);
			if (sysUser == null) {
				return genMessage(false, "不存在该帐号的用户");
			}
			Long userId = sysUser.getUserId();
			TaskEntity taskEntity = bpmService.getTask(taskId);
			boolean isAllowAddSign = false;
			ProcessRun processRun = processRunService.getByActInstanceId(new Long(taskEntity.getProcessInstanceId()));
			boolean isSignTask = bpmService.isSignTask(taskEntity);
			if (isSignTask) {
				SysOrg sysOrg = sysOrgService.getDefaultOrgByUserId(userId);
				if (sysOrg != null) {
					Long orgId = sysOrg.getOrgId();
					isAllowAddSign = bpmNodeSignService.checkNodeSignPrivilege(processRun.getActDefId(), taskEntity.getTaskDefinitionKey(), BpmNodeSignService.BpmNodePrivilegeType.ALLOW_RETROACTIVE, userId, orgId);
				}
			}
			return genMessage(isAllowAddSign, "");
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String isAllowBack(String taskId) {
		try {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			boolean isAllowBack = false;
			String taskToken = (String) taskService.getVariableLocal(taskEntity.getId(), TaskFork.TAKEN_VAR_NAME);
			// 设置了允许回退处理
			ExecutionStack executionStack = executionStackService.getLastestStack(taskEntity.getProcessInstanceId(), taskEntity.getTaskDefinitionKey(), taskToken);
			if (executionStack == null)
				return genMessage(false, "");

			if (executionStack.getParentId() != null && executionStack.getParentId() != 0) {
				ExecutionStack parentStack = executionStackService.getById(executionStack.getParentId());
				if (parentStack != null) {
					isAllowBack = true;
				}
			}
			return genMessage(isAllowBack, "");
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String isSelectPath(String taskId) {
		try {
			TaskEntity taskEntity = bpmService.getTask(taskId);
			String actDefId = taskEntity.getProcessDefinitionId();
			String nodeId = taskEntity.getTaskDefinitionKey();
			BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, "");
			String jumpType = bpmNodeSet.getJumpType();
			if (StringUtil.isNotEmpty(bpmNodeSet.getJumpType())) {
				if (jumpType.indexOf("2") != -1) {
					return genMessage(true, "");
				}
			}
			return genMessage(false, "");
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}
	
	@Override
	public String setVariablesByRunId(String xml) {
		try {
			Document doc = Dom4jUtil.loadXml(xml);
			Element root = doc.getRootElement();
			String runId = root.attributeValue("runId");
			if (StringUtil.isEmpty(runId)) {
				return genMessage(false, "runId不能为空");
			}
			ProcessRun processRun = processRunService.getById(Long.parseLong(runId));
			List<Element> vars = root.elements("var");
			if (BeanUtils.isNotEmpty(vars)) {
				Map<String, Object> variables = addVars(vars);
				runtimeService.setVariables(processRun.getActInstId(), variables);
			}
			return genMessage(true, "");
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String setTaskVars(String xml) {
		try {
			Document doc = Dom4jUtil.loadXml(xml);
			Element root = doc.getRootElement();
			String taskId = root.attributeValue("taskId");
			List<Element> vars = root.elements("var");
			if (BeanUtils.isNotEmpty(vars)) {
				Map<String, Object> variables = addVars(vars);

				for (Iterator<String> it = variables.keySet().iterator(); it.hasNext();) {
					String varName = it.next();
					taskService.setVariable(taskId, varName, variables.get(varName));
				}
			}
			return genMessage(true, "");
		} catch (Exception e) {
			return genMessage(false, e.getMessage());
		}
	}

	public String start(String xml) {

		ContextUtil.clearAll();

		Document doc = Dom4jUtil.loadXml(xml);
		Element root = doc.getRootElement();
		String actDefId = root.attributeValue("actDefId");
		String flowKey = root.attributeValue("flowKey");
		String subject = root.attributeValue("subject");
		String startUser = root.attributeValue("account");
		String businessKey = root.attributeValue("businessKey");
		// 流程key和定义id二选一。
		if (StringUtil.isEmpty(actDefId) && StringUtil.isEmpty(flowKey)) {
			String errorMesage = genMessage(false, "流程定义ID和流程key必须填写一个!");
			return errorMesage;
		}
		// 启动账户必填。
		if (StringUtil.isEmpty(startUser)) {
			String errorMesage = genMessage(false, "启动用户账号必填!");
			return errorMesage;
		}
		ProcessCmd processCmd = new ProcessCmd();

		if (StringUtil.isNotEmpty(actDefId)) {
			processCmd.setActDefId(actDefId);
		}
		if (StringUtil.isNotEmpty(businessKey)) {
			processCmd.setBusinessKey(businessKey);
		}
		if(StringUtil.isEmpty(businessKey)){
			Long buskey= UniqueIdUtil.genId();
			processCmd.setBusinessKey(buskey.toString());
		}
		if (StringUtil.isNotEmpty(flowKey)) {
			processCmd.setFlowKey(flowKey);
		}
		if (StringUtil.isNotEmpty(subject)) {
			processCmd.setSubject(subject);
		}
		processCmd.setUserAccount(startUser);
		// 表单数据
		List<Element> datas = root.elements("data");

		if (BeanUtils.isNotEmpty(datas)) {
			Element data = datas.get(0);
			String formData = data.getText();
			if (StringUtil.isNotEmpty(formData)) {
				processCmd.setFormData(formData);
			}
		}

		try {
			// 流程变量
			List<Element> vars = root.elements("var");
			if (BeanUtils.isNotEmpty(vars)) {
				Map<String, Object> variables = addVars(vars);
				processCmd.setVariables(variables);
			}
			SysUser user = (SysUser) ContextUtil.getCurrentUser();
			if (user == null) {
				ContextUtil.setCurrentUserAccount(startUser);
			}
			
			String runIdStr= root.attributeValue("runId");
			if(StringUtil.isNotEmpty(runIdStr)){
				Long runId = Long.parseLong(runIdStr);
				ProcessRun processRun=processRunService.getById(runId);
				if(BeanUtils.isEmpty(processRun)){
					throw new Exception("草稿已被删除");
				}
				processCmd.setProcessRun((IProcessRun) processRun);
			}
			
			ProcessRun processRun = processRunService.startProcess(processCmd);
			Element element = ProcessRun2Xml(null, processRun);
			return Dom4jUtil.docToPrettyString(element.getDocument());
		} catch (Exception e) {
			e.printStackTrace();
			// 返回错误消息。
			return genMessage(false, e.getMessage());
		}
	}

	private String ProcessRunList2Xml(List<ProcessRun> processList) throws Exception {
		if (processList == null)
			return null;
		Document createDoc = DocumentHelper.createDocument();
		Element docRoot = createDoc.addElement("list");
		for (ProcessRun processRun : processList) {
			ProcessRun2Xml(docRoot, processRun);
		}
		return Dom4jUtil.docToPrettyString(createDoc);
	}

	private void constructTaskUrl(ProcessTask processTask) {
		if (processTask == null)
			return;
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(processTask.getProcessDefinitionId(), processTask.getTaskDefinitionKey(), "");
		
		Map<String, Object> varsMap = taskService.getVariables(processTask.getId());

		if (bpmNodeSet == null)
			return;

		String formUrl = bpmNodeSet.getFormUrl();
		if (StringUtil.isNotEmpty(formUrl)) {
			formUrl = StringUtil.formatParamMsg(formUrl, varsMap);
			processTask.setTaskUrl(formUrl);
		}
	}

	/**
	 * 构建表单的执行URL。
	 * 
	 * @param processTask
	 */
	private void constructTaskUrls(List<ProcessTask> processTaskList) {
		for (ProcessTask processTask : processTaskList) {
			constructTaskUrl(processTask);
		}
	}

	/**
	 * 加入流程变量。
	 * 
	 * @param vars
	 * @return
	 */
	private Map<String, Object> addVars(List<Element> vars) throws Exception {
		Map<String, Object> varMap = new HashMap<String, Object>();
		for (Element var : vars) {
			String name = var.attributeValue("varName");
			String value = var.attributeValue("varVal");
			if (StringUtil.isEmpty(name)) {
				throw new Exception("流程变量的变量名不能为空");
			}
			if (StringUtil.isEmpty(value)) {
				throw new Exception("流程变量的变量值不能为空");
			}
			String dataType = var.attributeValue("varType");
			String dateFormat = var.attributeValue("dateFormat");
			Object obj = convertType(value, dataType, dateFormat);
			varMap.put(name, obj);
		}
		return varMap;
	}

	/**
	 * 将数据进行转型
	 * 
	 * @param value
	 *            值
	 * @param dataType
	 *            数据类型
	 * @param dateFormat
	 *            日期格式
	 * @return
	 */
	private Object convertType(String value, String dataType, String dateFormat) {
		if ("int".equals(dataType)) {
			return new Integer(value);
		} else if ("long".equals(dataType)) {
			return new Long(value);
		} else if ("double".equals(dataType)) {
			return new Double(value);
		} else if ("date".equals(dataType)) {
			if (StringUtil.isEmpty(dateFormat)) {
				dateFormat = "yyyy-MM-dd HH:mm:ss";
			}
			return TimeUtil.convertString(value, dateFormat);
		}
		return value;
	}

	/**
	 * 构建返回的xml消息
	 * 
	 * @param result
	 *            true:表示成功 false:表示失败
	 * @param message
	 *            消息内容
	 * @return
	 */
	private String genMessage(Boolean result, String message) {
		return "<result result=\"" + result.toString() + "\" message=\"" + message + "\"/>";
	}

	private String BpmFinishTask2Xml(List<BpmFinishTask> bpmFinishTaskList,String total) throws Exception {
		Document createDoc = DocumentHelper.createDocument();
		Element docRoot = createDoc.addElement("list");
		for (BpmFinishTask finishTask : bpmFinishTaskList) {
			Element elements = docRoot.addElement("task");
			Dom4jUtil.addAttribute(elements, "actInstId", finishTask.getActInstId());
			Dom4jUtil.addAttribute(elements, "businessKey", finishTask.getBusinessKey());
			Dom4jUtil.addAttribute(elements, "exeFullname", finishTask.getExeFullname());
			Dom4jUtil.addAttribute(elements, "flowName", finishTask.getFlowName());
			Dom4jUtil.addAttribute(elements, "formUrl", finishTask.getFormUrl());
			Dom4jUtil.addAttribute(elements, "opinion", finishTask.getOpinion());
			Dom4jUtil.addAttribute(elements, "subject", finishTask.getSubject());
			Dom4jUtil.addAttribute(elements, "taskKey", finishTask.getTaskKey());
			Dom4jUtil.addAttribute(elements, "taskName", finishTask.getTaskName());
			Dom4jUtil.addAttribute(elements, "checkStatus", finishTask.getCheckStatus());
			Dom4jUtil.addAttribute(elements, "durTime", finishTask.getDurTime());
			Dom4jUtil.addAttribute(elements, "endTime", finishTask.getEndTime());
			Dom4jUtil.addAttribute(elements, "exeUserId", finishTask.getExeUserId());
			Dom4jUtil.addAttribute(elements, "opinionId", finishTask.getOpinionId());
			Dom4jUtil.addAttribute(elements, "startTime", finishTask.getStartTime());
			Dom4jUtil.addAttribute(elements, "taskId", finishTask.getTaskId());
		}
		docRoot.addAttribute("totalCount", total);
		return Dom4jUtil.docToPrettyString(createDoc);
	}

	private String BpmDefinition2Xml(List<BpmDefinition> list) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element nodesElement = document.addElement("list");
		for (BpmDefinition bp : list) {
			Element nodeElement = nodesElement.addElement("definition");
			Dom4jUtil.addAttribute(nodeElement, "actDefId", bp.getActDefId());
			Dom4jUtil.addAttribute(nodeElement, "actDefKey", bp.getActDefKey());
			Dom4jUtil.addAttribute(nodeElement, "canChoicePath", bp.getCanChoicePath());
			Dom4jUtil.addAttribute(nodeElement, "defKey", bp.getDefKey());
			Dom4jUtil.addAttribute(nodeElement, "defXml", bp.getDefXml());
			Dom4jUtil.addAttribute(nodeElement, "descp", bp.getDescp());
			Dom4jUtil.addAttribute(nodeElement, "formDetailUrl", bp.getFormDetailUrl());
			Dom4jUtil.addAttribute(nodeElement, "reason", bp.getReason());
			Dom4jUtil.addAttribute(nodeElement, "subject", bp.getSubject());
			Dom4jUtil.addAttribute(nodeElement, "taskNameRule", bp.getTaskNameRule());
			Dom4jUtil.addAttribute(nodeElement, "typeName", bp.getTypeName());
			Dom4jUtil.addAttribute(nodeElement, "actDeployId", bp.getActDeployId());
			Dom4jUtil.addAttribute(nodeElement, "createBy", bp.getCreateBy());
			Dom4jUtil.addAttribute(nodeElement, "createtime", bp.getCreatetime());
			Dom4jUtil.addAttribute(nodeElement, "defId", bp.getDefId());
			Dom4jUtil.addAttribute(nodeElement, "isMain", bp.getIsMain());
			Dom4jUtil.addAttribute(nodeElement, "isUseOutForm", bp.getIsUseOutForm());
			Dom4jUtil.addAttribute(nodeElement, "parentDefId", bp.getParentDefId());
			Dom4jUtil.addAttribute(nodeElement, "showFirstAssignee", bp.getShowFirstAssignee());
			Dom4jUtil.addAttribute(nodeElement, "status", bp.getStatus());
			Dom4jUtil.addAttribute(nodeElement, "toFirstNode", bp.getToFirstNode());
			Dom4jUtil.addAttribute(nodeElement, "typeId", bp.getTypeId());
			Dom4jUtil.addAttribute(nodeElement, "updateBy", bp.getUpdateBy());
			Dom4jUtil.addAttribute(nodeElement, "updatetime", bp.getUpdatetime());
			Dom4jUtil.addAttribute(nodeElement, "versionNo", bp.getVersionNo());
		}
		return Dom4jUtil.docToPrettyString(document);
	}

	private String TaskOpinionList2Xml(List<TaskOpinion> list) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element nodesElement = document.addElement("list");
		for (TaskOpinion pt : list) {
			Element nodeElement = nodesElement.addElement("task");
			Dom4jUtil.addAttribute(nodeElement, "opinionId", pt.getOpinionId());
			Dom4jUtil.addAttribute(nodeElement, "actInstId", pt.getActInstId());
			Dom4jUtil.addAttribute(nodeElement, "taskName", pt.getTaskName());
			Dom4jUtil.addAttribute(nodeElement, "taskKey", pt.getTaskKey());
			Dom4jUtil.addAttribute(nodeElement, "taskId", pt.getTaskId());
			Dom4jUtil.addAttribute(nodeElement, "startTime", pt.getStartTime());
			Dom4jUtil.addAttribute(nodeElement, "endTime", pt.getEndTime());
			Dom4jUtil.addAttribute(nodeElement, "durTime", pt.getDurTime());
			Dom4jUtil.addAttribute(nodeElement, "exeUserId", pt.getExeUserId());
			Dom4jUtil.addAttribute(nodeElement, "exeFullname", pt.getExeFullname());
			Dom4jUtil.addAttribute(nodeElement, "opinion", pt.getOpinion());
		}
		return Dom4jUtil.docToPrettyString(document);
	}

	/**
	 * ProcessTaskList转 xml
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private String ProcessTaskList2Xml(List<ProcessTask> list) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element nodesElement = document.addElement("list");
		for (ProcessTask pt : list) {
			ProcessTask2Xml(nodesElement, pt);
		}
		return Dom4jUtil.docToPrettyString(document);
	}

	private Element ProcessTask2Xml(Element parentElement, ProcessTask pt) {
		Element nodeElement;
		if (BeanUtils.isEmpty(parentElement)) {
			Document document = DocumentHelper.createDocument();
			nodeElement = document.addElement("task");
		} else {
			nodeElement = parentElement.addElement("task");
		}
		Dom4jUtil.addAttribute(nodeElement, "id", pt.getId());
		Dom4jUtil.addAttribute(nodeElement, "name", pt.getName());
		Dom4jUtil.addAttribute(nodeElement, "subject", pt.getSubject());
		Dom4jUtil.addAttribute(nodeElement, "assignee", pt.getAssignee());
		Dom4jUtil.addAttribute(nodeElement, "executionId", pt.getExecutionId());
		Dom4jUtil.addAttribute(nodeElement, "owner", pt.getOwner());
		Dom4jUtil.addAttribute(nodeElement, "priority", pt.getPriority());
		Dom4jUtil.addAttribute(nodeElement, "processDefinitionId", pt.getProcessDefinitionId());
		Dom4jUtil.addAttribute(nodeElement, "processInstanceId", pt.getProcessInstanceId());
		Dom4jUtil.addAttribute(nodeElement, "processName", pt.getProcessName());
		Dom4jUtil.addAttribute(nodeElement, "taskDefinitionKey", pt.getTaskDefinitionKey());
		Dom4jUtil.addAttribute(nodeElement, "createTime", pt.getCreateTime());
		Dom4jUtil.addAttribute(nodeElement, "typeId", pt.getTypeId());
		Dom4jUtil.addAttribute(nodeElement, "typeName", pt.getTypeName());
		Dom4jUtil.addAttribute(nodeElement, "type", pt.getDescription());
		Dom4jUtil.addAttribute(nodeElement, "status", pt.getTaskStatus());

		return nodeElement;
	}

	private Element ProcessRun2Xml(Element parentElement, ProcessRun processRun) throws Exception {
		Element docRoot;
		if (BeanUtils.isEmpty(parentElement)) {
			Document document = DocumentHelper.createDocument();
			docRoot = document.addElement("run");
		} else {
			docRoot = parentElement.addElement("run");
		}
		Dom4jUtil.addAttribute(docRoot, "actDefId", processRun.getActDefId());
		Dom4jUtil.addAttribute(docRoot, "actInstId", processRun.getActInstId());
		Dom4jUtil.addAttribute(docRoot, "busDescp", processRun.getBusDescp());
		Dom4jUtil.addAttribute(docRoot, "businessKey", processRun.getBusinessKey());
		Dom4jUtil.addAttribute(docRoot, "businessUrl", processRun.getBusinessUrl());
		Dom4jUtil.addAttribute(docRoot, "creator", processRun.getCreator());
		Dom4jUtil.addAttribute(docRoot, "processName", processRun.getProcessName());
		Dom4jUtil.addAttribute(docRoot, "startOrgName", processRun.getStartOrgName());
		Dom4jUtil.addAttribute(docRoot, "subject", processRun.getSubject());
		Dom4jUtil.addAttribute(docRoot, "createtime", processRun.getCreatetime());
		Dom4jUtil.addAttribute(docRoot, "creatorId", processRun.getCreatorId());
		Dom4jUtil.addAttribute(docRoot, "defId", processRun.getDefId());
		Dom4jUtil.addAttribute(docRoot, "duration", processRun.getDuration());
		Dom4jUtil.addAttribute(docRoot, "endTime", processRun.getEndTime());
		Dom4jUtil.addAttribute(docRoot, "formDefId", processRun.getFormDefId());
		Dom4jUtil.addAttribute(docRoot, "parentId", processRun.getParentId());
		Dom4jUtil.addAttribute(docRoot, "recover", processRun.getRecover());
		Dom4jUtil.addAttribute(docRoot, "runId", processRun.getRunId());
		Dom4jUtil.addAttribute(docRoot, "startOrgId", processRun.getStartOrgId());
		Dom4jUtil.addAttribute(docRoot, "status", processRun.getStatus());
		Dom4jUtil.addAttribute(docRoot, "updateBy", processRun.getUpdateBy());
		Dom4jUtil.addAttribute(docRoot, "updatetime", processRun.getUpdatetime());
		return docRoot;
	}

	private String VarMap2Xml(Map<String, Object> map) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element nodesElement = document.addElement("list");
		Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			Element nodeElement = nodesElement.addElement("var");
			Dom4jUtil.addAttribute(nodeElement, "varName", entry.getKey());
			Dom4jUtil.addAttribute(nodeElement, "varVal", entry.getValue());
		}
		return Dom4jUtil.docToPrettyString(document);
	}

	private String TaskExecutorSet2Xml(Set<TaskExecutor> taskExecurot) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element nodesElement = document.addElement("list");
		for (TaskExecutor executor : taskExecurot) {
			Element nodeElement = nodesElement.addElement("executor");
			Dom4jUtil.addAttribute(nodeElement, "type", executor.getType());
			Dom4jUtil.addAttribute(nodeElement, "executeId", executor.getExecuteId());
			Dom4jUtil.addAttribute(nodeElement, "executor", executor.getExecutor());
			Dom4jUtil.addAttribute(nodeElement, "exactType", executor.getExactType());
		}
		return Dom4jUtil.docToPrettyString(document);
	}

}

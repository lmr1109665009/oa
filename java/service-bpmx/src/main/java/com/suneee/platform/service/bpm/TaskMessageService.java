package com.suneee.platform.service.bpm;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.jms.MessageProducer;
import com.suneee.core.model.MessageModel;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.bpm.BpmDefinitionDao;
import com.suneee.platform.dao.bpm.ProcessRunDao;
import com.suneee.platform.dao.form.BpmFormHandlerDao;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.SysTemplate;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.thread.TaskThreadService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.system.SysTemplateService;
import com.suneee.platform.service.util.ServiceUtil;
import net.sf.json.JSONObject;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class TaskMessageService {
	@Resource
	private MessageProducer messageProducer;
	@Resource
	private SysUserDao sysUserDao;
	@Resource
	private TaskUserService taskUserService;
	@Resource
	private SysTemplateService sysTemplateService;
	@Resource
	private BpmDefinitionDao bpmDefinitionDao;
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private NodeMsgTemplateService nodeMsgTemplateService;
	@Resource
	private BpmFormHandlerDao bpmFormHandlerDao;
	@Resource
	private ProcessRunDao dao;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private ProcessRunDao processRunDao;

	public void setMessageProducer(MessageProducer producer) {
		this.messageProducer = producer;
	}

	private void pushUser(Map<SysUser, List<Task>> users, SysUser user, Task task) {
		if (users.containsKey(user)) {
			users.get(user).add(task);
		} else {
			List<Task> list = new ArrayList<Task>();
			list.add(task);
			users.put(user, list);
		}
	}

	/**
	 * 发送 站内消息 短信 邮件 通知
	 * 
	 * @param taskList
	 *            任务列表
	 * @param informTypes
	 *            通知方式
	 * @param subject
	 *            标题
	 * @param map
	 *            消息模版
	 * @param opinion
	 *            意见
	 * @throws Exception
	 */
	public void notify(List<Task> taskList, String informTypes, String subject, Map<String, String> map, String opinion, String parentActDefId) throws Exception {
		// 通知类型为空。
		if (taskList == null)
			return;
		//遍历任务将用户和任务对应上。
		for (Task task : taskList) {
			String actDefId = task.getProcessDefinitionId();
			String nodeId = task.getTaskDefinitionKey();
			BpmDefinition bpmDefinition = bpmDefinitionDao.getByActDefId(actDefId);
			BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);
			
			if (StringUtil.isEmpty(informTypes)) {
				if (StringUtil.isNotEmpty(bpmNodeSet.getInformType())) {
					informTypes = bpmNodeSet.getInformType();
				} else {
					informTypes = bpmDefinition.getInformType();
				}
			}
			if (StringUtil.isEmpty(informTypes)) 	continue;
			String assignee = task.getAssignee();// 执行人
			// 存在指定的用户
			if (ServiceUtil.isAssigneeNotEmpty(assignee)) {
				SysUser user = sysUserDao.getById(Long.parseLong(assignee));
				sendMsg(task, map, user, informTypes, parentActDefId, bpmNodeSet, subject, opinion);
			}
			// 获取该任务的候选用户列表
			else {
				Set<SysUser> cUIds = taskUserService.getCandidateUsers(task.getId());
				for (SysUser user : cUIds) {
					sendMsg(task, map, user, informTypes, parentActDefId, bpmNodeSet, subject, opinion);
				}
			}
		}

	}

	private void sendMsg(Task task, Map<String, String> map, SysUser user, String informTypes, String parentActDefId, BpmNodeSet bpmNodeSet, String subject, String opinion) throws Exception {
		ISysUser curUser =  ContextUtil.getCurrentUser();
		// 是否为代理任务(代理任务发送消息给任务所属人)
		boolean isAgentTask = TaskOpinion.STATUS_AGENT.toString().equals(task.getDescription());
		if (map == null) {
			if (isAgentTask) {
				map = sysTemplateService.getTempByFun(SysTemplate.USE_TYPE_NOTIFYOWNER_AGENT);
			} else {
				map = sysTemplateService.getDefaultTemp();
			}
		}
		if (isAgentTask) {
			user = sysUserDao.getById(Long.parseLong(task.getOwner()));
		}
		// informTypes形如逗号分割字符串：1,2,3
		String infoTypeArray[] = informTypes.split(",");

		//nodeMsgTemplate处理
		ProcessCmd processCmd = TaskThreadService.getProcessCmd();
		BpmFormData bpmFormData = null;
		ProcessRun processRun = null;
		if (processCmd != null) {
			processRun = (ProcessRun) processCmd.getProcessRun();
			bpmFormData = (BpmFormData) processCmd.getTransientVar("bpmFormData");
		} else {
			//没有就自己拼装一个，在追回的时候是没有processCmd对象的，只在启动和next有
			String instanceId = task.getProcessInstanceId();
			processRun = dao.getByActInstanceId(new Long(instanceId));
			BpmFormTable bpmFormTable = bpmFormTableService.getByDefId(processRun.getDefId(), parentActDefId);
			BpmFormTable formTable=bpmFormTableService.getByTableId(bpmFormTable.getTableId(), 1);
			bpmFormData = bpmFormHandlerDao.getByKey(formTable, processRun.getBusinessKey(), false);
		}
		BpmDefinition parentBpmDefinition = bpmDefinitionDao.getByActDefId(parentActDefId);
		Long parentDef = parentBpmDefinition == null ? null : parentBpmDefinition.getDefId();
		JSONObject jsonObject = nodeMsgTemplateService.parse(bpmNodeSet.getNodeId(), bpmNodeSet.getDefId(), parentDef, bpmFormData);
		if (jsonObject != null) {
			map.put("htmlDefForm", jsonObject.getString("html"));
			map.put("textDefForm", jsonObject.getString("text"));
		}

		Map<String, Object> vars = null;
		if(processRun != null){
			vars = new HashMap<String, Object>();
			vars.put("processName", processRun.getProcessName());
			vars.put("creator", processRun.getCreator());
			vars.put("status", processRun.getStatus());
			vars.put("typeId", processRun.getTypeId());
		}
		String runtimeEnv= com.suneee.eas.common.utils.ContextUtil.getRuntimeEnv();
		vars.put("runtimeEnv",runtimeEnv);
		vars.put("system",processCmd.getSystem());
		for (String infoType : infoTypeArray) {
			// 循环,找到具体的实现类
			MessageModel messageModel = new MessageModel(infoType);
			messageModel.setReceiveUser((ISysUser) user);
			messageModel.setSendUser( curUser);
			messageModel.setSubject(subject);
			messageModel.setTemplateMap(map);
			messageModel.setExtId(Long.parseLong(task.getId()));
			messageModel.setIsTask(true);
			messageModel.setOpinion(opinion);
			messageModel.setVars(vars);
			messageProducer.send(messageModel);
		}

		//开始处理是否发信息给启动人
		if (bpmNodeSet.getSendMsgToCreator() == 0) {
			return;
		}
		if (processCmd.getProcessRun() == null)
			return;//在启动流程时可能获取不了这个，逻辑上也不需要发送
		SysUser creator = sysUserDao.getById(processRun.getCreatorId());
		map = sysTemplateService.getTempByFun(SysTemplate.USE_TYPE_SENDTOCREATOR);//获取模板
		map.put("htmlDefForm", jsonObject.getString("html"));//nodeMsgTemplate处理
		map.put("textDefForm", jsonObject.getString("text"));
		for (String infoType : infoTypeArray) {
			// 循环,找到具体的实现类
			MessageModel messageModel = new MessageModel(infoType);
			messageModel.setReceiveUser((ISysUser) creator);
			messageModel.setSendUser( curUser);
			messageModel.setSubject(subject);
			messageModel.setTemplateMap(map);
			messageModel.setExtId(Long.parseLong(task.getId()));
			messageModel.setIsTask(true);
			messageModel.setOpinion(opinion);
			messageModel.setVars(vars);
			messageProducer.send(messageModel);
		}
	}

	/**
	 * 发送消息接口
	 * 
	 * @param sendUser
	 *            发送者名称
	 * @param receiverUserList
	 *            接收者用户列表
	 * @param informTypes
	 *            发送消息类型
	 * @param msgTempMap
	 *            消息模板
	 * @param subject
	 *            事项名称
	 * @param opinion
	 *            意见或原因
	 * @param taskId
	 *            任务Id
	 * @param runId
	 *            流程运行ID
	 * @param userCopyIdMap
	 *            流程转发抄送时对应的用户抄送Id Map
	 * @throws Exception
	 */
	public void sendMessage(SysUser sendUser, List<SysUser> receiverUserList, String informTypes, Map<String, String> msgTempMap, String subject, String opinion, Long taskId, Long runId, Map<Long, Long> userCopyIdMap) throws Exception {
		if (StringUtil.isEmpty(informTypes))
			return;
		if (BeanUtils.isEmpty(sendUser)) {
			sendUser = new SysUser();
			sendUser.setUserId(0L);
			sendUser.setFullname("系统消息");
		}
		if (BeanUtils.isEmpty(receiverUserList))
			return;
		if (BeanUtils.isEmpty(msgTempMap))
			return;

		boolean isTask = true;
		Long extId = 0L;
		if (BeanUtils.isNotIncZeroEmpty(taskId)) {
			isTask = true;
			extId = taskId;
		} else {
			isTask = false;
			extId = runId;
		}
		int userSize = receiverUserList.size();

		for (int i = 0; i < userSize; i++) {
			SysUser receiverUser = receiverUserList.get(i);
			// informTypes形如逗号分割字符串：1,2,3
			String infoTypeArray[] = informTypes.split(",");
			for (String infoType : infoTypeArray) {
				// infoType一定要指定，其值与handlersMap 中的key对应：1邮件，2短信，3内部消息
				MessageModel messageModel = new MessageModel(infoType);
				messageModel.setReceiveUser((ISysUser) receiverUser);
				messageModel.setSendUser((ISysUser) sendUser);
				messageModel.setSubject(subject);
				messageModel.setTemplateMap(msgTempMap);
				messageModel.setExtId(extId);
				messageModel.setIsTask(isTask);
				messageModel.setOpinion(opinion);
				messageModel.setSendDate(new Date());
				// 转发抄送以内部消息发送时，把抄送转发的ID传过去，作为生成超链接时的URL
				Map<String, Object> varMap = new HashMap<String, Object>();
				if (BeanUtils.isNotEmpty(userCopyIdMap)) {
					varMap.put("copyId", userCopyIdMap.get(receiverUser.getUserId()));
				}
				ProcessRun processRun = processRunDao.getById(runId);
				varMap.put("processName", processRun.getProcessName());
				varMap.put("creator", processRun.getCreator());
				varMap.put("status", processRun.getStatus());
				varMap.put("typeId", processRun.getTypeId());
				messageModel.setVars(varMap);

				messageProducer.send(messageModel);
			}
		}
	}

	/**
	 * 直接发送消息
	 * 
	 * @param sendUser
	 *            发送者
	 * @param receiverUserList
	 *            接受者
	 * @param informTypes
	 *            消息方式
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 */
	public void sendMessage(SysUser sendUser, List<SysUser> receiverUserList, String informTypes, String title, String content) {
		int userSize = receiverUserList.size();
		for (int i = 0; i < userSize; i++) {
			SysUser receiverUser = receiverUserList.get(i);
			// informTypes形如逗号分割字符串：1,2,3
			String infoTypeArray[] = informTypes.split(",");
			for (String infoType : infoTypeArray) {
				// 循环,找到具体的实现类
				MessageModel messageModel = new MessageModel(infoType);
				messageModel.setReceiveUser((ISysUser) receiverUser);
				messageModel.setSendUser((ISysUser) sendUser);
				messageModel.setSubject(title);
				messageModel.setSendDate(new Date());
				messageModel.setContent(content);
				messageProducer.send(messageModel);
			}

		}
	}
}

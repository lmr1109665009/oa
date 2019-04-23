package com.suneee.platform.model.bpm;

import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.model.BaseModel;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.tag.TaskStatusTag;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 对象功能:流程任务审批意见Model对象 开发公司:广州宏天软件有限公司 开发人员:csx 创建时间:2012-01-11 16:06:10
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class TaskOpinion extends BaseModel {
	/**
	 * 初始化\尚未审批=-2
	 */
	public final static Short STATUS_INIT = (short) -2;
	/**
	 * 正在审批
	 */
	public final static Short STATUS_CHECKING = (short) -1;
	/**
	 * 弃权=0
	 */
	public final static Short STATUS_ABANDON = (short) 0;
	/**
	 * 同意=1
	 */
	public final static Short STATUS_AGREE = (short) 1;
	/**
	 * 反对=2
	 */
	public final static Short STATUS_REFUSE = (short) 2;
	/**
	 * 退回=3
	 */
	public final static Short STATUS_REJECT = (short) 3;
	/**
	 * 撤销=4
	 */
	public final static Short STATUS_RECOVER = (short) 4;

	/**
	 * 会签通过
	 */
	public final static Short STATUS_PASSED = (short) 5;

	/**
	 * 会签不通过。
	 */
	public final static Short STATUS_NOT_PASSED = (short) 6;

	/**
	 * 知会意见。
	 */
	public final static Short STATUS_NOTIFY = (short) 7;

	/**
	 * 更改执行路径
	 */
	public final static Short STATUS_CHANGEPATH = (short) 8;
	
	/**
	 * 终止
	 */
	public final static Short STATUS_ENDPROCESS=(short)14;
	
	/**
	 * 沟通意见。
	 */
	public final static Short STATUS_COMMUNICATION=(short)15;
	
	/**
	 * 沟通反馈。
	 */
	public final static Short STATUS_COMMUN_FEEDBACK=(short)20;
	
	/**
	 * 办结转发
	 */
	public final static Short STATUS_FINISHDIVERT=(short)16;
	
	/**
	 * 转办。
	 */
	public final   static Short STATUS_DELEGATE=(short)21;
	
	/**
	 * 转办取消。
	 */
	public final static Short STATUS_DELEGATE_CANCEL=(short)22;
	
	/**
	 * 更改执行人。
	 */
	public final static Short STATUS_CHANGE_ASIGNEE = (short) 23;
	/**
	 * 退回到发起人
	 */
	public final static Short STATUS_REJECT_TOSTART = (short) 24;
	/**
	 * 撤销到发起人
	 */
	public final static Short STATUS_RECOVER_TOSTART = (short) 25;
	
	
	
	/**
	 * 撤销
	 */
	public final static Short STATUS_REVOKED=(short)17;
	
	/**
	 * 逻辑删除
	 */
	public final static Short STATUS_DELETE=(short)18;
	
	/**
	 * 抄送
	 */
	public final static Short STATUS_NOTIFY_COPY=(short)19;
	
	/**
	 * 代理。
	 */
	public final   static Short STATUS_AGENT=(short)26;
	/**
	 * 代理撤消。
	 */
	public final   static Short STATUS_AGENT_CANCEL=(short)27;
	
	/**
	 * 表单意见。
	 */
	public final   static Short STATUS_OPINION=(short)28;
	/**
	 * 退回取消
	 */
	public final   static Short STATUS_BACK_CANCEL=(short)29;
	
	/**
	 * 撤销取消
	 */
	public final   static Short STATUS_REVOKED_CANCEL=(short)30;
	/**
	 * 通过取消
	 */
	public final   static Short STATUS_PASS_CANCEL=(short)31;
	/**
	 * 拒绝取消
	 */
	public final   static Short STATUS_REFUSE_CANCEL=(short)32;
	/**
	 * 提交
	 */
	public final   static Short STATUS_SUBMIT=(short)33;
	/**
	 * 重新提交
	 */
	public final   static Short STATUS_RESUBMIT=(short)34;
	
	/**
	 * 干预
	 */
	public final   static Short STATUS_INTERVENE=(short)35;
	
	/**
	 * 重启任务
	 */
	public final   static Short STATUS_RESTART_TASK=(short)36;
	
	/**
	 * 执行过，用于自动节点记录状态。
	 */
	public final static Short STATUS_EXECUTED=(short)37;
	
	/**
	 * 抄送通知
	 */
	public final static String TASKKEY_NOTIFY="NotifyTask";
	/**
	 * 办结转发
	 */
	public final static String TASKKEY_DIVERT="DivertTask";
	/**表示已读 =1*/
	public final static Short READ = 1;
	/**表示未读=0*/
	public final static Short NOT_READ = 0;
	/**
	 * 流转
	 */
	public final static Short STATUS_TRANSTO=(short)38;
	/**
	 * 流转中
	 */
	public final static Short STATUS_TRANSTO_ING=(short)39;
	/**
	 * 代提交
	 */
	public final static Short STATUS_REPLACE_SUBMIT=(short)40;
	/**
	 * 正常流转，即无别人干预
	 */
	public final static Short STATUS_COMMON_TRANSTO=(short)41;
	/**
	 * 流转取消
	 */
	public final static Short STATUS_CANCLE_TRANSTO=(short)42;
	/**
	 * 添加流转人
	 */
	public final static Short STATUS_ADD_TRANSTO=(short)43;
	/**
	 * 补签
	 */
	public final static Short STATUS_RETROACTIVE=(short)44;
	/**
	 * 追回
	 */
	public final static Short STATUS_RETRIEVE=(short)45;
	/**
	 * 自由退回
	 */
	public final static Short STATUS_REJECT_TOANYNODE=(short)46;

	// opinionId
	protected Long opinionId;
	// actInstId
	protected String actInstId;
	// 任务名称
	protected String taskName;
	// 任务Key
	protected String taskKey;
	// 任务令牌
	protected String taskToken;
	// taskId
	protected Long taskId;
	// 执行开始时间
	protected Date startTime;
	// 结束时间
	protected Date endTime;
	// 持续时间
	protected Long durTime;
	// 执行人ID
	protected Long exeUserId;
	// 执行人名
	protected String exeFullname;
	//执行人组织信息
	protected SysOrg sysOrg;
	//签章图片地址
	protected String webSignUrl;
	// 审批意见
	protected String opinion;
	// 审批状态
	protected Short checkStatus = STATUS_CHECKING;
	// 表单定义Id
	protected Long formDefId = 0L;
	// 意见表单名称
	protected String fieldName;
	// 流程定义ID
	protected String actDefId;
	// 父流程实例ID
	protected String superExecution;
 
	
	protected String candidateUser="";
	
	private List<SysUser> candidateUsers=new ArrayList<SysUser>();
	
	public List<TaskExeStatus> candidateUserStatusList=new ArrayList<TaskExeStatus>();
	
	public TaskExeStatus taskExeStatus;
	

	/**
	 * 抄送表的中状态，0为未读，1为已读

	protected Short status=0;
		 */
	protected String status;
	
	protected String startTimeStr;
	protected String endTimeStr;
	protected String durTimeStr;
	
	public TaskOpinion() {

	}

	public TaskOpinion(ProcessTask task) {
		this.actDefId = task.getProcessDefinitionId();
		this.actInstId = task.getProcessInstanceId();
		this.taskId = new Long(task.getId());
		this.taskName = task.getName();
		this.taskKey = task.getTaskDefinitionKey();
		this.startTime = new Date();
	}

	public TaskOpinion(DelegateTask task) {
		this.actDefId = task.getProcessDefinitionId();
		this.actInstId = task.getProcessInstanceId();
		this.taskId = new Long(task.getId());
		this.taskKey = task.getTaskDefinitionKey();
		this.taskName = task.getName();
		this.checkStatus=STATUS_CHECKING;
		this.startTime = new Date();
		ExecutionEntity superExecution = ((ExecutionEntity) task.getExecution()).getProcessInstance()
				.getSuperExecution();
		if (superExecution != null)
			this.superExecution = superExecution.getProcessInstanceId();
	}

	public void setOpinionId(Long opinionId) {
		this.opinionId = opinionId;
	}

	/**
	 * 返回 opinionId
	 * 
	 * @return
	 */
	public Long getOpinionId() {
		return opinionId;
	}

	public void setActInstId(String actInstId) {
		this.actInstId = actInstId;
	}

	/**
	 * 返回 actInstId
	 * 
	 * @return
	 */
	public String getActInstId() {
		return actInstId;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * 返回 任务名称
	 * 
	 * @return
	 */
	public String getTaskName() {
		return taskName;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	/**
	 * 返回 taskId
	 * 
	 * @return
	 */
	public Long getTaskId() {
		return taskId;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * 返回 执行开始时间
	 * 
	 * @return
	 */
	public Date getStartTime() {
		return startTime;
	}

	public String getStartTimeStr() {
		if (startTime == null)
			return "";
		return TimeUtil.getDateTimeString(this.startTime);
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * 返回 结束时间
	 * 
	 * @return
	 */
	public Date getEndTime() {

		return endTime;
	}

	public String getEndTimeStr() {

		if (endTime == null)
			return "";
		return TimeUtil.getDateTimeString(this.endTime);
	}

	public void setDurTime(Long durTime) {
		this.durTime = durTime;
	}

	public String getDurTimeStr() {
		if (this.durTime == null)
			return "";
		return TimeUtil.getTime(this.durTime);
	}

	/**
	 * 返回 持续时间
	 * 
	 * @return
	 */
	public Long getDurTime() {
		return durTime;
	}

	public void setExeUserId(Long exeUserId) {
		this.exeUserId = exeUserId;
	}

	/**
	 * 返回 执行人ID
	 * 
	 * @return
	 */
	public Long getExeUserId() {
		return exeUserId;
	}

	public void setExeFullname(String exeFullname) {
		this.exeFullname = exeFullname;
	}

	/**
	 * 返回 执行人名
	 * 
	 * @return
	 */
	public String getExeFullname() {
		if (StringUtil.isEmpty(exeFullname))
			return "";
		return exeFullname;
	}

	public SysOrg getSysOrg() {
		return sysOrg;
	}

	public void setSysOrg(SysOrg sysOrg) {
		this.sysOrg = sysOrg;
	}

	public String getWebSignUrl() {
		return webSignUrl;
	}

	public void setWebSignUrl(String webSignUrl) {
		this.webSignUrl = webSignUrl;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	/**
	 * 返回 审批意见
	 * 
	 * @return
	 */
	public String getOpinion() {
		return opinion;
	}

	public void setCheckStatus(Short checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getStatus() {
		//获取流程状态
		return TaskStatusTag.getTaskStatus(checkStatus, 1);
	}

	
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 返回 审批状态(来自数据字典)
	 * 
	 * @return
	 */
	public Short getCheckStatus() {
		return checkStatus;
	}
	
	

	/**
	 * 获取表单定义ID
	 * 
	 * @return
	 */
	public Long getFormDefId() {
		return formDefId;
	}

	public void setFormDefId(Long formDefId) {
		this.formDefId = formDefId;
	}

	/**
	 * 获取表单定义名称。
	 * 
	 * @return
	 */
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * 获取流程定义ID。
	 * 
	 * @return
	 */
	public String getActDefId() {
		return actDefId;
	}

	public void setActDefId(String actDefId) {
		this.actDefId = actDefId;
	}

	public String getTaskKey() {
		return taskKey;
	}

	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}

	public String getTaskToken() {
		return taskToken;
	}

	public void setTaskToken(String taskToken) {
		this.taskToken = taskToken;
	}

	public String getSuperExecution() {
		return superExecution;
	}

	public void setSuperExecution(String superExecution) {
		this.superExecution = superExecution;
	}

	public String getCandidateUser() {
		return candidateUser;
	}

	public void setCandidateUser(String candidateUser) {
		this.candidateUser = candidateUser;
	}

	public void setCandidateUsers(List<SysUser> candidateUsers) {
		this.candidateUsers = candidateUsers;
	}

	public List<SysUser> getCandidateUsers() {
		return candidateUsers;
	}
	
	public List<TaskExeStatus> getCandidateUserStatusList() {
		return candidateUserStatusList;
	}

	public void setCandidateUserStatusList(List<TaskExeStatus> candidateUserStatusList) {
		this.candidateUserStatusList = candidateUserStatusList;
	}

	public TaskExeStatus getTaskExeStatus() {
		return taskExeStatus;
	}

	public void setTaskExeStatus(TaskExeStatus taskExeStatus) {
		this.taskExeStatus = taskExeStatus;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof TaskOpinion)) {
			return false;
		}
		TaskOpinion rhs = (TaskOpinion) object;
		return new EqualsBuilder().append(this.opinionId, rhs.opinionId).isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(-82280557, -700257973).append(this.opinionId).toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this).append("opinionId", this.opinionId).append("actInstId", this.actInstId)
				.append("taskName", this.taskName).append("taskId", this.taskId).append("startTime", this.startTime)
				.append("endTime", this.endTime).append("durTime", this.durTime).append("exeUserId", this.exeUserId)
				.append("exeFullname", this.exeFullname).append("opinion", this.opinion)
				.append("checkStatus", this.checkStatus).append("actDefId", this.actDefId)
				.append("superExecution", this.superExecution).toString();
	}
	
	

}
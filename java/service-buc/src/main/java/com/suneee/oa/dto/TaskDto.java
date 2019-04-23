package com.suneee.oa.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 对象功能:任务实体类（用于处理TaskEntity不能转换成Json格式输出问题）
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2017-12-19 11:33:00
 * </pre>
 */
public class TaskDto implements Serializable{

	private String id;
	private String name;
	private String taskDefinitionKey;
	private String description;
	private String executionId;
	//创建时间
	private Date createTime;
	//到期时间
	private Date dueDate;
	//执行人id
	private String assignee;
	//执行人名
	private String assigneeName;

	public String getAssigneeName() {
		return assigneeName;
	}

	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
}

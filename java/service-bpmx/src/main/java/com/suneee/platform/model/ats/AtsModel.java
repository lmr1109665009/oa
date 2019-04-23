package com.suneee.platform.model.ats;

import java.util.Date;

/**
 * 
 * 考勤model
 * 
 * @author hugh zhuang
 * 
 */
public class AtsModel {

	// 以bean为主
	protected String type;
	// 用户ID
	protected Long userId;
	// 档案ID
	protected Long fileId;
	// 开始时间
	protected Date startTime;
	// 结束时间
	protected Date endTime;
	// 使用类型
	protected String useType;
	// 时间长度 (全天、上午、下午、晚上)
	protected Short timeDuration;
	// 使用时间
	protected Double useTime;
	// 补偿方式(加班专用)
	protected String compens;
	// 流程运行ID，可以通过这个查看流程
	protected Long runId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public double getUseTime() {
		return useTime;
	}

	public void setUseTime(double useTime) {
		this.useTime = useTime;
	}

	public String getCompens() {
		return compens;
	}

	public void setCompens(String compens) {
		this.compens = compens;
	}

	public String getUseType() {
		return useType;
	}

	public void setUseType(String useType) {
		this.useType = useType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getRunId() {
		return runId;
	}

	public void setRunId(Long runId) {
		this.runId = runId;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Short getTimeDuration() {
		return timeDuration;
	}

	public void setTimeDuration(Short timeDuration) {
		this.timeDuration = timeDuration;
	}

	public void setUseTime(Double useTime) {
		this.useTime = useTime;
	}

}

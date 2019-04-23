package com.suneee.core.model;

import com.suneee.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 分发任务环节退回对象
 * @author heyifan
 *
 */
public class ForkTaskReject {
	private Long userId;		/*要退回的分发用户ID*/
	private String fullname;	/*要退回的分发用户名称*/
	private String token;		/*要退回的分发任务当时产生的token*/
	private String nodeId;		/*要退回的分发节点ID*/
	private String nodeName;	/*要退回的分发节点名称*/
	
	public ForkTaskReject(){}
	
	public ForkTaskReject(Long userId, String fullname, String token, String nodeId, String nodeName){
		this.userId = userId;
		this.fullname = fullname;
		this.token = token;
		this.nodeId = nodeId;
		this.nodeName = nodeName;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * 将字符串类型序列化为列表
	 * <pre>
	 * 字符串格式示例：1^超级管理员^T_1#1000003823^张飞^T_2
	 * </pre>
	 * @param rejects 字符串类型数据
	 * @return
	 */
	public static List<ForkTaskReject> parser2List(String rejects){
		if(StringUtil.isEmpty(rejects)) return null;
		String[] ary = rejects.split("#");
		List<ForkTaskReject> list = new ArrayList<ForkTaskReject>();
		for (String item : ary) {
			String[] split = item.split("\\^");
			if(split.length!=5) continue;
			String userIdStr = split[0];
			String fullname = split[1];
			String token = split[2];
			String nodeId = split[3];
			String nodeName = split[4];
			list.add(new ForkTaskReject(Long.parseLong(userIdStr), fullname, token, nodeId, nodeName));
		}
		return list;
	}
}

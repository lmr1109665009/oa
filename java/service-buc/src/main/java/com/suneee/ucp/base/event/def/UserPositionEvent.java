/**
 * 
 */
package com.suneee.ucp.base.event.def;

import com.suneee.platform.model.system.UserPosition;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 用户岗位（组织）关系事件
 * 
 * @author xiongxianyun
 *
 */
public class UserPositionEvent extends ApplicationEvent{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4696035599891612693L;
	
	/**
	 * 更新用户岗位（组织）关系
	 */
	public static int ACTION_UPD=1;

	/**
	 * 增加用户岗位（组织）关系
	 */
	public static int ACTION_ADD=0;
	
	/**
	 * 删除用户岗位（组织）关系
	 */
	public static int ACTION_DEL=2;
	
	/**
	 * 删除用户岗位（组织）关系，不需同步到用户中心
	 */
	public static int ACTION_DEL_NC=3;
	
	/**
	 * 用户岗位（组织）关系信息对象
	 */
	private List<UserPosition> userPositionList;
	
	/**
	 * 操作
	 */
	private int action = ACTION_UPD;

	/**
	 * @param source
	 */
	public UserPositionEvent(List<UserPosition> source) {
		super(source);
		this.userPositionList = source;
	}

	/**
	 * @return the userPositionList
	 */
	public List<UserPosition> getUserPositionList() {
		return userPositionList;
	}

	/**
	 * @param userPositionList the userPositionList to set
	 */
	public void setUserPositionList(List<UserPosition> userPositionList) {
		this.userPositionList = userPositionList;
	}

	/**
	 * @return the action
	 */
	public int getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}
}

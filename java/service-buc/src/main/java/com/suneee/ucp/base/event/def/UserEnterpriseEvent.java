/**
 * @Title: UserEnterpriseEvent.java 
 * @Package com.suneee.ucp.base.event.def 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.ucp.base.event.def;

import com.suneee.oa.model.user.UserEnterprise;
import com.suneee.platform.model.system.SysUser;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @ClassName: UserEnterpriseEvent 
 * @Description: 用户企业关系事件 (一次只能发布一个用户的用户企业关系事件)
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-07 18:12:32 
 *
 */
public class UserEnterpriseEvent extends ApplicationEvent{
	private static final long serialVersionUID = -4374876057627660341L;
	public static final int ACTION_ADD = 0;
	public static final int ACTION_UPD = 1;
	public static final int ACTION_DEL = 2;
	
	private int action = ACTION_ADD;
	private SysUser sysUser;
	private List<UserEnterprise> userEnterpriseList;

	/**
	 * @param source
	 */
	public UserEnterpriseEvent(List<UserEnterprise> source) {
		super(source);
		userEnterpriseList = source;
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

	/**
	 * @return the userEnterpriseList
	 */
	public List<UserEnterprise> getUserEnterpriseList() {
		return userEnterpriseList;
	}

	/**
	 * @param userEnterpriseList the userEnterpriseList to set
	 */
	public void setUserEnterpriseList(List<UserEnterprise> userEnterpriseList) {
		this.userEnterpriseList = userEnterpriseList;
	}

	/**
	 * @return the sysUser
	 */
	public SysUser getSysUser() {
		return sysUser;
	}

	/**
	 * @param sysUser the sysUser to set
	 */
	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}
}

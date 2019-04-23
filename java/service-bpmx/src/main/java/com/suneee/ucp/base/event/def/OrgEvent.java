/**
 * 
 */
package com.suneee.ucp.base.event.def;

import com.suneee.platform.model.system.SysOrg;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 组织事件
 * @author xiongxianyun
 *
 */
public class OrgEvent extends ApplicationEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5387040103159941011L;
	
	/**
	 * 更新组织
	 */
	public static int ACTION_UPD=1;

	/**
	 * 增加组织
	 */
	public static int ACTION_ADD=0;
	
	/**
	 * 组织信息对象
	 */
	private List<SysOrg> sysOrgLsit;
	
	/**
	 * 操作
	 */
	private int action = ACTION_UPD;
	
	/**
	 * @param source
	 */
	public OrgEvent(List<SysOrg> source) {
		super(source);
		this.sysOrgLsit = source;
	}
	
	/**
	 * @return the sysOrgLsit
	 */
	public List<SysOrg> getSysOrgList() {
		return sysOrgLsit;
	}
	
	/**
	 * @param sysOrgLsit the sysOrgLsit to set
	 */
	public void setSysOrgList(List<SysOrg> sysOrgLsit) {
		this.sysOrgLsit = sysOrgLsit;
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

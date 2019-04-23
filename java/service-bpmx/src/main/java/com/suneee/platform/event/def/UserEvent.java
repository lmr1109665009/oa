package com.suneee.platform.event.def;

import com.suneee.platform.model.system.SysUser;
import org.springframework.context.ApplicationEvent;

/**
 * 用户事件。
 * @author ray
 *
 */
public class UserEvent extends ApplicationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2595904294669110938L;

	/**
	 * 更新用户。
	 */
	public static int ACTION_UPD = 1;
	
	/**
	 * 删除用户。
	 */
	public static int ACTION_DEL = 2;
	/**
	 * 新增用户
	 */
	public static int ACTION_ADD = 0;
	
	/**
	 * 恢复用户
	 */ 
	public static int ACTION_REVERT = 3;
	
	private Long userId = 0L;
	
	private SysUser user;
	
	/**
	 * 动作。
	 */
	private int action=ACTION_UPD;
	
	/**
	 * 是否需要同步到用户中心
	 */ 
	private boolean needSyncToUserCenter;
	
	public UserEvent(Long source) {
		super(source);
		this.userId=source;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public Long getUserId() {
		return userId;
	}

	public SysUser getUser() {
		return user;
	}

	public void setUser(SysUser user) {
		this.user = user;
	}

	/**
	 * @return the needSyncToUserCenter
	 */
	public boolean isNeedSyncToUserCenter() {
		return needSyncToUserCenter;
	}

	/**
	 * @param needSyncToUserCenter the needSyncToUserCenter to set
	 */
	public void setNeedSyncToUserCenter(boolean needSyncToUserCenter) {
		this.needSyncToUserCenter = needSyncToUserCenter;
	}
}

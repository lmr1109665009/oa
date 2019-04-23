package com.suneee.platform.model.system;

import java.util.Date;

import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;

/**
 * 对象功能:sys_login_log Model对象 开发公司:广州宏天软件有限公司 开发人员:liyj 创建时间:2015-07-01 14:10:56
 */
public class LoginLog extends BaseModel {
	// ID
	protected Long id;
	// ACCOUNT
	protected String account;
	// LOGINTIME
	protected Date loginTime=new Date();
	// IP
	protected String ip;
	// STATUS
	protected Short status;
	// DESC_
	protected String desc;

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 返回 ID
	 * 
	 * @return
	 */
	public Long getId() {
		return this.id;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * 返回 ACCOUNT
	 * 
	 * @return
	 */
	public String getAccount() {
		return this.account;
	}

	/**
	 * loginTime
	 * 
	 * @return the loginTime
	 * @since 1.0.0
	 */

	public Date getLoginTime() {
		return loginTime;
	}

	/**
	 * @param loginTime
	 *            the loginTime to set
	 */
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 返回 IP
	 * 
	 * @return
	 */
	public String getIp() {
		return this.ip;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	/**
	 * 返回 STATUS
	 * 
	 * @return
	 */
	public Short getStatus() {
		return this.status;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * 返回 DESC_
	 * 
	 * @return
	 */
	public String getDesc() {
		return this.desc;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof LoginLog)) {
			return false;
		}
		LoginLog rhs = (LoginLog) object;
		return this.id.equals(rhs.getId());
	}
	/**
	 * 
	 * <pre> 
	 * 描述：登录日志的状态
	 * 注意 status=0 成功
	 * status<0 失败，会累计上失败次数
	 * status>0 失败，但不会累计上失败次数
	 * 构建组：bpm33
	 * 作者：aschs
	 * 邮箱:liyj@jee-soft.cn
	 * 日期:2015-7-14-下午6:04:52
	 * 版权：广州宏天软件有限公司版权所有
	 * </pre>
	 */
	public static class Status{
		/**
		 * 登录成功
		 */
		public static final Short SUCCESS=0;
		/**
		 * 验证码错误
		 */
		public static final Short VCODE_ERR=-1;
		/**
		 * 账号密码为空
		 */
		public static final Short ACCOUNT_PWD_EMPTY=-2;
		/**
		 * 用户名密码输入错误
		 */
		public static final Short ACCOUNT_PWD_ERR=-3;
		/**
		 * 用户被锁定
		 */
		public static final Short ACCOUNT_LOCKED=1;
		/**
		 * 用户被禁用
		 */
		public static final Short ACCOUNT_DISABLED=2;
		/**
		 * 用户已过期
		 */
		public static final Short ACCOUNT_OVERDUE=3;
		/**
		 * 密码策略没通过
		 */
		public static final Short PWDSTRATEGY_UNPASS=4;
	}
}
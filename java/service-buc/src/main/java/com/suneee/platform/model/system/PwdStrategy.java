package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;

/**
 * 对象功能:sys_pwd_strategy Model对象 开发公司:广州宏天软件有限公司 开发人员:liyj 创建时间:2015-06-25 14:30:17
 */
public class PwdStrategy extends BaseModel {
	// ID
	protected Long id;
	// 初始化密码
	protected String initPwd;
	// 强制修改初始化密码	0：否；1：是
	protected Short forceChangeInitPwd;
	// 密码策略	0：无限制；1：数字加字母；2：数字加字母加特殊字符
	protected Short pwdRule;
	// 密码长度	
	protected Short pwdLength;
	// 密码有效期
	protected Short validity;
	//处理过期密码	0：不处理；1：锁定账号
	protected Short handleOverdue;
	// 过期提醒
	protected Short overdueRemind;
	// 错误输入多少次验证码出现
	protected Short verifyCodeAppear;
	// 错误输入多少次锁定账号
	protected Short errLockAccount;
	// 启用策略	0:关闭，1:启动
	protected Short enable;
	// 描述
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

	public void setInitPwd(String initPwd) {
		this.initPwd = initPwd;
	}

	/**
	 * 返回 INIT_PWD
	 * 
	 * @return
	 */
	public String getInitPwd() {
		return this.initPwd;
	}

	public void setForceChangeInitPwd(Short forceChangeInitPwd) {
		this.forceChangeInitPwd = forceChangeInitPwd;
	}

	/**
	 * 返回 FORCE_CHANGE_INIT_PWD
	 * 
	 * @return
	 */
	public Short getForceChangeInitPwd() {
		return this.forceChangeInitPwd;
	}

	public void setPwdRule(Short pwdRule) {
		this.pwdRule = pwdRule;
	}

	/**
	 * 返回 PWD_RULE
	 * 
	 * @return
	 */
	public Short getPwdRule() {
		return this.pwdRule;
	}

	public void setPwdLength(Short pwdLength) {
		this.pwdLength = pwdLength;
	}

	/**
	 * 返回 PWD_LENGTH
	 * 
	 * @return
	 */
	public Short getPwdLength() {
		return this.pwdLength;
	}

	public void setValidity(Short validity) {
		this.validity = validity;
	}

	/**
	 * 返回 VALIDITY
	 * 
	 * @return
	 */
	public Short getValidity() {
		return this.validity;
	}

	public void setHandleOverdue(Short handleOverdue) {
		this.handleOverdue = handleOverdue;
	}

	/**
	 * 返回 HANDLE_OVERDUE
	 * 
	 * @return
	 */
	public Short getHandleOverdue() {
		return this.handleOverdue;
	}

	public void setOverdueRemind(Short overdueRemind) {
		this.overdueRemind = overdueRemind;
	}

	/**
	 * 返回 OVERDUE_REMIND
	 * 
	 * @return
	 */
	public Short getOverdueRemind() {
		return this.overdueRemind;
	}

	public void setVerifyCodeAppear(Short verifyCodeAppear) {
		this.verifyCodeAppear = verifyCodeAppear;
	}

	/**
	 * 返回 VERIFY_CODE_APPEAR
	 * 
	 * @return
	 */
	public Short getVerifyCodeAppear() {
		return this.verifyCodeAppear;
	}

	public void setErrLockAccount(Short errLockAccount) {
		this.errLockAccount = errLockAccount;
	}

	/**
	 * 返回 ERR_LOCK_ACCOUNT
	 * 
	 * @return
	 */
	public Short getErrLockAccount() {
		return this.errLockAccount;
	}

	public void setEnable(Short enable) {
		this.enable = enable;
	}

	/**
	 * 返回 ENABLE
	 * 
	 * @return
	 */
	public Short getEnable() {
		return this.enable;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * 返回 DESC
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
		if (!(object instanceof PwdStrategy)) {
			return false;
		}
		PwdStrategy rhs = (PwdStrategy) object;
		return rhs.getId() == this.getId();
	}
	
	/**
	 * 
	 * <pre> 
	 * 描述：验证策略的状态返回值
	 * 构建组：bpm33
	 * 作者：aschs
	 * 邮箱:liyj@jee-soft.cn
	 * 日期:2015-6-29-下午3:34:03
	 * 版权：广州宏天软件有限公司版权所有
	 * </pre>
	 */
	public static class Status{
		/**
		 * 通过验证
		 */
		public static final int SUCCESS=0;
		/**
		 * 强制修改密码
		 */
		public static final int NEED_TO_CHANGE_PWD=1;
		/**
		 * 密码太短
		 */
		public static final int LENGTH_TOO_SHORT=2;
		/**
		 * 密码不符合 字母跟数字 的规则
		 */
		public static final int NO_MATCH_NUMANDWORD=3;
		/**
		 * 密码不符合 字母跟数字跟特殊字符 的规则
		 */
		public static final int NO_MATCH_NUMANDWORDANDSPECIAL=4;
		/**
		 * 密码过期
		 */
		public static final int PWD_OVERDUE=5;
		/**
		 * 密码因输入错误次数过多而锁住
		 */
		public static final int ERR_TOO_MUCH_LOCKED=6;
		/**
		 * 初始化密码
		 */
		public static final int PWD_INIT=7;
	}
}
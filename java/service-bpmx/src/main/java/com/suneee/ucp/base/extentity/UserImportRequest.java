/**
 * 
 */
package com.suneee.ucp.base.extentity;

import java.util.List;

/**
 * @author Administrator
 *
 */
public class UserImportRequest{
	/**
	 * 账户
	 */
	private String account;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 客户端IP
	 */
	private String clientIp;
	
	/**
	 * 应用系统编码
	 */
	private String appCode;
	
	/**
	 * 用户数组
	 */
	private List<UcUser> employees;

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * @param clientIp the clientIp to set
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	/**
	 * @return the appCode
	 */
	public String getAppCode() {
		return appCode;
	}

	/**
	 * @param appCode the appCode to set
	 */
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	/**
	 * @return the employees
	 */
	public List<UcUser> getEmployees() {
		return employees;
	}

	/**
	 * @param employees the employees to set
	 */
	public void setEmployees(List<UcUser> employees) {
		this.employees = employees;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserImportRequest [account=");
		builder.append(account);
		builder.append(", password=");
		builder.append(password);
		builder.append(", clientIp=");
		builder.append(clientIp);
		builder.append(", appCode=");
		builder.append(appCode);
		builder.append(", employees=");
		builder.append(employees);
		builder.append("]");
		return builder.toString();
	}
}

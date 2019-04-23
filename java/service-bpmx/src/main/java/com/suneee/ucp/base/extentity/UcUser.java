/**
 * 
 */
package com.suneee.ucp.base.extentity;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class UcUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4820688865072040254L;

	/**
	 * 会话ID
	 */
	private String sessionId;
	
	/**
	 * 用户ID
	 */
	private Long userId;
	
	/**
	 * 微链号
	 */
	private String useName;
	
	/**
	 * 用户姓名
	 */
	private String name;
	
	/**
	 * 昵称
	 */
	private String nick;
	
	/**
	 * 性别
	 */
	private boolean sex;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 手机
	 */
	private String mobile;
	
	/**
	 * 个人头像
	 */
	private String photo;
	
	/**
	 * 账号
	 */
	private String account;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 注册时间
	 */
	private String registerTime;
	
	/**
	 * 最后修改时间
	 */
	private String lastUpdateTime;
	
	/**
	 * 地址
	 */
	private String address;
	
	/**
	 * 个性签名
	 */
	private String signature;
	
	/**
	 * 背景图
	 */
	private String backgroundImg;

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the useName
	 */
	public String getUseName() {
		return useName;
	}

	/**
	 * @param useName the useName to set
	 */
	public void setUseName(String useName) {
		this.useName = useName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * @param nick the nick to set
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * @return the sex
	 */
	public boolean isSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(boolean sex) {
		this.sex = sex;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
	}

	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}

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
	 * @return the registerTime
	 */
	public String getRegisterTime() {
		return registerTime;
	}

	/**
	 * @param registerTime the registerTime to set
	 */
	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	/**
	 * @return the lastUpdateTime
	 */
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * @param lastUpdateTime the lastUpdateTime to set
	 */
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * @return the backgroundImg
	 */
	public String getBackgroundImg() {
		return backgroundImg;
	}

	/**
	 * @param backgroundImg the backgroundImg to set
	 */
	public void setBackgroundImg(String backgroundImg) {
		this.backgroundImg = backgroundImg;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [sessionId=");
		builder.append(sessionId);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", useName=");
		builder.append(useName);
		builder.append(", name=");
		builder.append(name);
		builder.append(", nick=");
		builder.append(nick);
		builder.append(", sex=");
		builder.append(sex);
		builder.append(", email=");
		builder.append(email);
		builder.append(", mobile=");
		builder.append(mobile);
		builder.append(", photo=");
		builder.append(photo);
		builder.append(", account=");
		builder.append(account);
		builder.append(", password=");
		builder.append(password);
		builder.append(", registerTime=");
		builder.append(registerTime);
		builder.append(", lastUpdateTime=");
		builder.append(lastUpdateTime);
		builder.append(", address=");
		builder.append(address);
		builder.append(", signature=");
		builder.append(signature);
		builder.append(", backgroundImg=");
		builder.append(backgroundImg);
		builder.append("]");
		return builder.toString();
	}
}

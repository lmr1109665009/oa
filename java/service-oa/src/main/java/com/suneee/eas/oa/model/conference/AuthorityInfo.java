package com.suneee.eas.oa.model.conference;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @Description: 授权信息对象
 * @Author: kaize
 * @Date: 2018/8/2 15:38
 */
public class AuthorityInfo {

	//权限对象类型：会议
	public static final String AUTHTYPE_CONFERENCE = "conference";
	//权限对象类型：会议附件
	public static final String AUTHTYPE_CONFERENCE_ATTACH = "conference-attach";
	//权限对象类型：会议纪要
	public static final String AUTHTYPE_CONFERENCE_NOTE = "conference-note";
	//权限对象类型：会议室
	public static final String AUTHTYPE_CONFERENCE_ROOM = "conference-room";

	//授权对象类型：用户
	public static final String OWNERTYPE_USER = "user";
	//授权对象类型：组织
	public static final String OWNERTYPE_ORG = "org";
	//授权对象类型：角色
	public static final String OWNERTYPE_ROLE = "role";

	/**
	 * 权限对象ID
	 */
	private Long authId;
	/**
	 *权限对象类型：conference,conference-attach,conference-note,conference-room
	 */
	private String authType;
	/**
	 *授权对象ID
	 */
	private Long ownerId;
	/**
	 *授权对象类型：user,org,role
	 */
	private String ownerType;
	/**
	 *授权对象名称
	 */
	private String ownerName;

	public Long getAuthId() {
		return authId;
	}

	public void setAuthId(Long authId) {
		this.authId = authId;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("authId",authId)
				.append("authType",authType)
				.append("ownerId",ownerId)
				.append("ownerType",ownerType)
				.append("ownerName",ownerName)
				.toString();
	}
}

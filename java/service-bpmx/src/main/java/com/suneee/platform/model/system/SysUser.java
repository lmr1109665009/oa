package com.suneee.platform.model.system;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.model.BaseModel;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.service.system.SysRoleService;
import com.suneee.platform.webservice.api.util.adapter.GrantedAuthorityAdapter;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.suneee.platform.model.system.UserPosition.DELETE_NO;

/**
 * 对象功能:用户表 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:hotent
 * 创建时间:2011-11-03 16:02:45
 */
public class SysUser extends BaseModel implements UserDetails ,ISysUser {
	private static final long serialVersionUID = 5998122441782019082L;
	public final static String SEARCH_BY_ROL = "1";// 从角色
	public final static String SEARCH_BY_ORG = "2";// 从组织
	public final static String SEARCH_BY_POS = "3";// 从岗位
	public final static String SEARCH_BY_ONL = "4";// 从在线

	/**
	 * 是否主要岗位(0否,1是)
	 */
	public final static short PRIMARY_YES=1;
	public final static short PRIMARY_NO=0;

	/**
	 * 是否负责人(0否,1是)
	 */
	public final static Short CHARRGE_YES=1;
	public final static Short CHARRGE_NO=0;


	private static ThreadLocal<Collection<GrantedAuthority>> roleList=new ThreadLocal<Collection<GrantedAuthority>>();

	public static void removeRoleList(){
		roleList.remove();
	}

	/**
	 * 账号未锁定
	 */
	public final static Short UN_LOCKED = 0;
	/**
	 * 账号被锁定
	 */
	public final static Short LOCKED = 1;

	/**
	 * 账号未期
	 */
	public final static Short UN_EXPIRED = 0;

	/**
	 * 账号过期
	 */
	public final static Short EXPIRED = 1;

	/**
	 * 账号激活
	 */
	public final static Short STATUS_OK = 1;

	/**
	 * 账号禁用
	 */
	public final static Short STATUS_NO = 0;
	/**
	 * 账号删除
	 */
	public final static Short STATUS_Del = -1;

	/**
	 * 数据来自系统添加
	 */
	public final static Short FROMTYPE_DB=0;
	/**
	 * 数据来自AD同步,并在系统中未被设置
	 */
	public final static Short FROMTYPE_AD=1;
	/**
	 * 数据来自AD同步,并在系统中被设置过
	 */
	public final static Short FROMTYPE_AD_SET=2;

	/**
	 * 同步到用户中心标识：未同步
	 */
	public final static Short SYNC_UC_NO = 0;
	/**
	 * 同步到用户中心标识：已同步
	 */
	public final static Short SYNC_UC_OK = 1;

	// userOrgId
	protected Long userOrgId;
	// userId
	protected Long userId;
	// 姓名
	protected String fullname;
	/**
	 * 字号
	 */
	protected String aliasName;
	// 帐号
	protected String account;
	// 密码
	protected String password;
	// 是否过期
	protected Short isExpired;
	// 是否锁定
	protected Short isLock;
	// 状态
	protected Short status;
	// 邮箱
	protected String email;
	// 手机
	protected String mobile;
	// 电话
	protected String phone;

	// 性别
	protected String sex;
	// 照片
	protected String picture;
	protected String weixinid;


	// 组织名称
	protected String orgName;
	// 岗位名称
	protected String posName;

	// 入职日期
	protected Date entryDate;

	//上级
	private Long[] superiorIds;

	//员工状态
	private String userStatus;

	private boolean isAdd;

	/**
	 * 同步到用户中心标识：0-未同步，1-已同步
	 */
	private Short syncToUc=0;

	/**
	 * 用户中心用户ID
	 */
	private Long ucUserid;

	/**
	 * 工号
	 */
	private String staffNo;

	/**
	 * 身份证号
	 */
	private String identification;

	/**
	 * 考勤编号
	 */
	private Long attendNo;

	/**
	 * 工作日期
	 */
	private Date workDate;

	/**
	 * 地区
	 */
	private String region;

	/**
	 * 地区名称
	 */
	private String regionName;
	//密码更新时间
	protected Date pwdUpdTime;
	//是否同步至微信(0 未同步，1，已同步，2.登陆过)
	protected int hasSyncToWx;

	/**
	 * 数据来源
	 */
	protected short fromType;

	//角色名称
	protected String roleNames;

	// 序号
	private Long sn=0L;

	//用户签章地址
	private String webSignUrl;

	// 集团编码_account
	private String loginAccount;

	//职务名称
	protected String jobName;

	//公司名称
	protected String company;

	// ISPRIMARY
	protected Short  isPrimary;

	// ISCHARGE
	protected Short  isCharge;

	protected Long  userPosId;

	protected Long  posId;

	protected String userName;

	// 路径
	private String path;

	private String[] userEnterprises;
	// 用户企业关系同步时使用
	private String userEnterpriseinfoJson;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	protected Short  isDelete=DELETE_NO;


	public void setFromType(short fromType) {
		this.fromType = fromType;
	}

	public Short getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Short isPrimary) {
		this.isPrimary = isPrimary;
	}

	public Long getUserPosId() {
		return userPosId;
	}

	public void setUserPosId(Long userPosId) {
		this.userPosId = userPosId;
	}

	public Long getPosId() {
		return posId;
	}

	public void setPosId(Long posId) {
		this.posId = posId;
	}

	public Short getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Short isDelete) {
		this.isDelete = isDelete;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	protected Long  orgId;


	public Short getIsCharge() {
		return isCharge;
	}

	public void setIsCharge(Short isCharge) {
		this.isCharge = isCharge;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * 用户岗位关系
	 */
	private List<UserPosition>  userPositions;

	public String getWebSignUrl() {
		return webSignUrl;
	}

	public void setWebSignUrl(String webSignUrl) {
		this.webSignUrl = webSignUrl;
	}

	public Long getSn() {
		return sn;
	}

	public void setSn(Long sn) {
		this.sn = sn;
	}

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public Short getFromType() {
		return fromType;
	}

	public void setFromType(Short fromType) {
		this.fromType = fromType;
	}



	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPosName() {
		return posName;
	}

	public void setPosName(String posName) {
		this.posName = posName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}



	public Long getUserOrgId() {
		return userOrgId;
	}

	public void setUserOrgId(Long userOrgId) {
		this.userOrgId = userOrgId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * 返回 userId
	 *
	 * @return
	 */
	public Long getUserId() {
		return userId;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	/**
	 * 返回 姓名
	 *
	 * @return
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * @return the aliasName
	 */
	public String getAliasName() {
		return aliasName;
	}

	/**
	 * @param aliasName the aliasName to set
	 */
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * 返回 帐号
	 *
	 * @return
	 */
	public String getAccount() {
		return account;
	}

	public void setPassword(String password) {
		this.setPwdUpdTime(new Date());//更新密码时间
		this.password = password;
	}

	/**
	 * 返回 密码
	 *
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	public void setIsExpired(Short isExpired) {
		this.isExpired = isExpired;
	}

	/**
	 * 返回 是否过期
	 *
	 * @return
	 */
	public Short getIsExpired() {
		return isExpired;
	}

	public void setIsLock(Short isLock) {
		this.isLock = isLock;
	}

	/**
	 * 返回 是否锁定
	 *
	 * @return
	 */
	public Short getIsLock() {
		return isLock;
	}

	public String getWeixinid() {
		return weixinid;
	}

	public void setWeixinid(String weixinid) {
		this.weixinid = weixinid;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	/**
	 * 返回 创建时间
	 *
	 * @return
	 */
	public Date getCreatetime() {
		return createtime;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public int getHasSyncToWx() {
		return hasSyncToWx;
	}

	public void setHasSyncToWx(int hasSyncToWx) {
		this.hasSyncToWx = hasSyncToWx;
	}

	/**
	 * 返回 状态
	 *
	 * @return
	 */
	public Short getStatus() {
		return status;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 返回 邮箱
	 *
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * 返回 手机
	 *
	 * @return
	 */
	public String getMobile() {
		return mobile;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 返回 电话
	 *
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	/**
	 * @return the isAdd
	 */
	public boolean isAdd() {
		return isAdd;
	}

	/**
	 * @param isAdd the isAdd to set
	 */
	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

	/**
	 * @see Object#equals(Object)
	 */

	@Override
	public boolean equals(Object rhs) {

		if (rhs instanceof SysUser) {
			return this.account.equals(((SysUser) rhs).account);
		}
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.account.hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this).append("userId", this.userId).append("fullname", this.fullname).append("account", this.account)
				.append("aliasName", this.aliasName).append("staffNo", this.staffNo).append("attendNo", this.attendNo)
				.append("password", this.password).append("isExpired", this.isExpired).append("isLock", this.isLock).append("createtime", this.createtime)
				.append("status", this.status).append("email", this.email).append("mobile", this.mobile).append("phone", this.phone)
				.append("orgName", this.orgName).append("sex", this.sex).append("picture", this.picture).append("retype")
				.append("loginAccount", this.loginAccount).toString();
	}

	/**
	 * 返回角色。
	 * @return
	 */
	public String getRoles() {
		String str="";
		Collection<GrantedAuthority> roles= getAuthorities();
		for(GrantedAuthority role:roles){
			if("".equals(str)){
				str=role.getAuthority();
			}
			else{
				str+="," + role.getAuthority();
			}
		}
		return str;
	}


	/**
	 * 重写UserDetails 的getAuthorities方法。
	 *
	 * <pre>
	 * 1.首先从缓存中读取。
	 * 2.判断帐号在缓存中是否存在帐号，若存在直接重缓存中读取。
	 * 3.如果不存在则从数据库中读取并加入缓存。
	 *
	 * 目前角色支持两种方式。
	 * 1.用户和角色的映射。
	 * 2.部门和角色的映射。
	 *
	 * 两种角色进行合并构成当前用户的角色。
	 * </pre>
	 */
	@Override
	public @XmlJavaTypeAdapter(GrantedAuthorityAdapter.class) Collection<GrantedAuthority> getAuthorities() {
		if(roleList.get()!=null) return roleList.get();
		Collection<GrantedAuthority> rtnList= new ArrayList<GrantedAuthority>();
		SysRoleService sysRoleService = (SysRoleService) AppUtil.getBean(SysRoleService.class);
		SysOrg curOrg=(SysOrg) ContextUtil.getCurrentOrg();
		Long orgId=curOrg==null?0:curOrg.getOrgId();
		//Long orgId=(long) 0;
		Collection<String> totalRoleCol=  sysRoleService.getRolesByUserIdAndOrgId(userId, orgId);
		if(BeanUtils.isNotEmpty(totalRoleCol)){
			for(String role:totalRoleCol){
				rtnList.add(new SimpleGrantedAuthority(role));
			}
		}
		String admin=SysUser.getAdminAccount();
		// 添加超级管理员角色。
		if (admin.equals(this.account)) {
			rtnList.add(SystemConst.ROLE_GRANT_SUPER);
		}
		roleList.set(rtnList);
		return rtnList;
	}

	/**
	 * 取得管理员帐号。
	 * @return
	 */
	public static String getAdminAccount(){
		String admin= PropertyUtil.getByAlias("account");
		if(StringUtil.isEmpty(admin)){
			admin="admin";
		}
		return admin;
	}



	@Override
	public String getUsername() {
		return account;
	}

	@Override
	public boolean isAccountNonExpired() {
		if(isExpired==null) return true;
		if (isExpired.shortValue() == UN_EXPIRED.shortValue()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isAccountNonLocked() {
		if(isLock==null) return true;
		if (isLock.shortValue() == UN_LOCKED.shortValue()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {

		return status == STATUS_OK ? true : false;

	}

	public Long[] getSuperiorIds() {
		return superiorIds;
	}

	public void setSuperiorIds(Long[] superiorIds) {
		this.superiorIds = superiorIds;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	/**
	 * @return the syncToUc
	 */
	public Short getSyncToUc() {
		return syncToUc;
	}

	/**
	 * @param syncToUc the syncToUc to set
	 */
	public void setSyncToUc(Short syncToUc) {
		this.syncToUc = syncToUc;
	}

	/**
	 * @return the ucUserid
	 */
	public Long getUcUserid() {
		return ucUserid;
	}

	/**
	 * @param ucUserid the ucUserid to set
	 */
	public void setUcUserid(Long ucUserid) {
		this.ucUserid = ucUserid;
	}

	/**
	 * @return the staffNo
	 */
	public String getStaffNo() {
		return staffNo;
	}

	/**
	 * @param staffNo the staffNo to set
	 */
	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}

	/**
	 * @return the identification
	 */
	public String getIdentification() {
		return identification;
	}

	/**
	 * @param identification the identification to set
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}

	/**
	 * @return the attendNo
	 */
	public Long getAttendNo() {
		return attendNo;
	}

	/**
	 * @param attendNo the attendNo to set
	 */
	public void setAttendNo(Long attendNo) {
		this.attendNo = attendNo;
	}

	/**
	 * @return the workDate
	 */
	public Date getWorkDate() {
		return workDate;
	}

	/**
	 * @param workDate the workDate to set
	 */
	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return the regionName
	 */
	public String getRegionName() {
		return regionName;
	}

	/**
	 * @param regionName the regionName to set
	 */
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	/**
	 * pwdUpdTime
	 * @return  the pwdUpdTime
	 * @since   1.0.0
	 */

	public Date getPwdUpdTime() {
		return pwdUpdTime;
	}

	/**
	 * @param pwdUpdTime the pwdUpdTime to set
	 */
	public void setPwdUpdTime(Date pwdUpdTime) {
		this.pwdUpdTime = pwdUpdTime;
	}

	/**
	 * @return the userPositions
	 */
	public List<UserPosition> getUserPositions() {
		return userPositions;
	}

	/**
	 * @param userPositions the userPositions to set
	 */
	public void setUserPositions(List<UserPosition> userPositions) {
		this.userPositions = userPositions;
	}

	/**
	 * @return the userEnterprises
	 */
	public String[] getUserEnterprises() {
		return userEnterprises;
	}

	/**
	 * @param userEnterprises the userEnterprises to set
	 */
	public void setUserEnterprises(String[] userEnterprises) {
		this.userEnterprises = userEnterprises;
	}

	/**
	 * @return the userEnterpriseinfoJson
	 */
	public String getUserEnterpriseinfoJson() {
		return userEnterpriseinfoJson;
	}

	/**
	 * @param userEnterpriseinfoJson the userEnterpriseinfoJson to set
	 */
	public void setUserEnterpriseinfoJson(String userEnterpriseinfoJson) {
		this.userEnterpriseinfoJson = userEnterpriseinfoJson;
	}

	public static JSONObject toJsonObject(SysUser sysUser){
		if(sysUser == null){
			return null;
		}
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("userId", sysUser.getUserId());
		jsonObj.put("fullname", sysUser.getFullname());
		jsonObj.put("aliasName", sysUser.getAliasName());
		jsonObj.put("account", sysUser.getAccount());
		jsonObj.put("password", sysUser.getPassword());
		jsonObj.put("isExpired", sysUser.getIsExpired());
		jsonObj.put("isLock", sysUser.getIsLock());
		jsonObj.put("createtime", sysUser.getCreatetime() == null ? null : DateFormatUtil.formaDatetTime(sysUser.getCreatetime()));
		jsonObj.put("status", sysUser.getStatus());
		jsonObj.put("email", sysUser.getEmail());
		jsonObj.put("mobile", sysUser.getMobile());
		jsonObj.put("phone", sysUser.getPhone());
		jsonObj.put("sex", sysUser.getSex());
		jsonObj.put("picture", sysUser.getPicture());
		jsonObj.put("entrydate", sysUser.getEntryDate() == null ? null : DateFormatUtil.formatDate(sysUser.getEntryDate()));
		jsonObj.put("userstatus", sysUser.getUserStatus());
		jsonObj.put("fromType", sysUser.getFromType());
		jsonObj.put("pwdUpdTime", sysUser.getPwdUpdTime() == null ? null : DateFormatUtil.formaDatetTime(sysUser.getPwdUpdTime()));
		jsonObj.put("weixinid", sysUser.getWeixinid());
		jsonObj.put("hasSyncToWx", sysUser.getHasSyncToWx());
		jsonObj.put("syncToUc", sysUser.getSyncToUc());
		jsonObj.put("ucUserid", sysUser.getUcUserid());
		jsonObj.put("staffNo", sysUser.getStaffNo());
		jsonObj.put("identification", sysUser.getIdentification());
		jsonObj.put("attendNo", sysUser.getAttendNo());
		jsonObj.put("workDate", sysUser.getWorkDate() == null ? null : DateFormatUtil.formatDate(sysUser.getWorkDate()));
		jsonObj.put("region", sysUser.getRegion());
		jsonObj.put("sn", sysUser.getSn());
		jsonObj.put("webSignUrl", sysUser.getWebSignUrl());
		jsonObj.put("loginAccount", sysUser.getLoginAccount());
		jsonObj.put("updatetime", sysUser.getUpdatetime() == null ? null : DateFormatUtil.formaDatetTime(sysUser.getUpdatetime()));
		jsonObj.put("createBy", sysUser.getCreateBy());
		jsonObj.put("updateBy", sysUser.getUpdateBy());
		return jsonObj;
	}
}
package com.suneee.oa.service.user;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.encrypt.EncryptUtil;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.dao.user.SysUserExtendDao;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.event.def.UserEvent;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.platform.service.system.UserUnderService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.service.UcpBaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
 
 /**
  * 用户信息SysUser扩展Service类
 * @author xiongxianyun
 *
 */
@Service
 public class SysUserExtendService extends UcpBaseService<SysUser> {
 
	@Resource
	private SysUserExtendDao sysUserExtendDao;
 
	@Resource
	private UserPositionExtendService userPositionExtendService;
 
	@Resource
	private UserRoleExtendService userRoleExtendService;
 
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private UserUnderService userUnderService;
	@Resource
	private UserEnterpriseService userEnterpriseService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
 
	@Override
	protected IEntityDao<SysUser, Long> getEntityDao() {
		return this.sysUserExtendDao;
	}
 
	/**
	 * 获取用户信息列表
	 * @param filter
	 * @return
	 */
	public List<SysUser> getAllUser(QueryFilter filter) {
		return this.sysUserExtendDao.getAllUser(filter);
   }
	
	public List<SysUser> getAllDelUser(QueryFilter filter) {
		return this.sysUserExtendDao.getAllDelUser(filter);
   }
	
 
	/**
	 * 判断账号是否已经注册
	 * @param account 账号
	 * @param userId 用户ID
	 * @return
	 */
	public boolean isAccountExist(String account, Long userId)  {
		int count = this.sysUserExtendDao.getAccountCount(account, userId).intValue();
		return (count > 0);
	}
 
	/**
	 * 判断邮箱是否已经注册
	 * @param email 邮箱
	 * @param userId 用户ID
	 * @return
	 */
	public boolean isEmailExist(String email, Long userId) {
		int count = this.sysUserExtendDao.getEmailCount(email, userId).intValue();
		return (count > 0);
	}
 
	/**
	 * 判断手机号是否已经注册
	 * @param mobile 手机号
	 * @param userId 用户ID
	 * @return
	 */
	public boolean isMobileExist(String mobile, Long userId) {
		int count = this.sysUserExtendDao.getMobileCount(mobile, userId).intValue();
		return (count > 0);
	}
 
	/**
	 * 保存用户信息
	 * @param sysUser 用户基本信息
	 * @param roleIds 角色ID数组
	 * @param positions 岗位信息json字符串
	 * @throws Exception
	 */
	public void save(SysUser sysUser, Long[] roleIds, String positions) throws Exception {
		int event = UserEvent.ACTION_ADD;
		// 用户企业关系是否需要同步到用户中心
		// 新增用户时调用的用户中心注册接口会包括用户企业关系，所以用户企业关系不用再单独同步一次
		boolean needSync = false;
		// 是否同步到用户中心：0-未同步
		sysUser.setSyncToUc(SysUser.SYNC_UC_NO);
		
		// 用户密码
	    String password = sysUser.getPassword();
	    if(StringUtils.isBlank(password)){
	    	// 新增时，若密码为空，则取默认密码
	    	if(sysUser.getUserId() == null){
	    		password = EncryptUtil.encrypt32MD5(AppConfigUtil.get(Constants.USER_DEFAULT_PASSWORD));
	    	}
	    } else {
	    	// 长度不是32位的密码需要加密
	       if(password.length() != 32){
	    	   password = EncryptUtil.encrypt32MD5(password);
	       }
	    }
	    sysUser.setPassword(password);
	    
		if (sysUser.getUserId() == null) {
	       sysUser.setUserId(Long.valueOf(UniqueIdUtil.genId()));
	       // 是否过期
	       String isExpired = AppConfigUtil.get(Constants.USER_DEFAULT_ISEXPIRED, SysUser.UN_EXPIRED.toString());
	       sysUser.setIsExpired(Short.valueOf(isExpired));
	       // 是否锁定
	       String isLock = AppConfigUtil.get(Constants.USER_DEFAULT_ISLOCK, SysUser.UN_LOCKED.toString());
	       sysUser.setIsLock(Short.valueOf(isLock));
	       // 员工状态
	       String userStatus = AppConfigUtil.get(Constants.USER_DEFAULT_USERSTATUS);
	       sysUser.setUserStatus(userStatus);
		} else {
			// 更新时间
			sysUser.setUpdatetime(new Date());
			event = UserEvent.ACTION_UPD;
			if(sysUser.getUcUserid() != null){
				needSync = true;
			}
		}
 
		Long userId = sysUser.getUserId();
		// 保存用户岗位关系
		this.userPositionExtendService.saveUserPos(userId, positions);
		// 保存用户角色关系
		this.userRoleExtendService.saveUserRole(userId, roleIds);
		// 保存用户关联的企业信息
		this.userEnterpriseService.saveUserEnterprise(sysUser, needSync);
		// 保存用户上级设置
		this.userUnderService.saveSuperior(userId, sysUser.getFullname(), sysUser.getSuperiorIds());
		
		// 获取当前登录用户的企业编码生成多系统唯一账号，解决定子链所企业账号重复问题，账号格式为：集团编码_account值
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		String loginAccount = sysUser.getAccount();
		if (StringUtils.isNotBlank(enterpriseCode)) {
			loginAccount = enterpriseCode + Constants.SEPARATOR_UNDERLINE + sysUser.getAccount();
		}
		sysUser.setLoginAccount(loginAccount);
 
		if(sysUser.getSn() == null){
			sysUser.setSn(sysUser.getUserId());
		}
		// 保存用户信息
		if (event == UserEvent.ACTION_ADD)
			this.sysUserExtendDao.add(sysUser);
		else {
			this.sysUserExtendDao.updateUser(sysUser);
		}
 
		// 发布用户信息新增/变更事件
		EventUtil.publishUserEvent(sysUser, event, true);
	}
}

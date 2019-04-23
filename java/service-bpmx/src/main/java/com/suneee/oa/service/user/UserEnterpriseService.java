/**
 * @Title: UserEnterpriseService.java 
 * @Package com.suneee.oa.service.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.service.user;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.oa.dao.user.UserEnterpriseDao;
import com.suneee.oa.model.user.UserEnterprise;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.system.UserRoleService;
import com.suneee.ucp.base.event.def.UserEnterpriseEvent;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.UcpBaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UserEnterpriseService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-07 17:06:34 
 *
 */
@Service
public class UserEnterpriseService extends UcpBaseService<UserEnterprise>{
	@Resource
	private UserEnterpriseDao userEnterpriseDao;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private UserPositionExtendService userPositionExtendService;
	
	/** (non-Javadoc)
	 * @Title: getEntityDao 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see com.suneee.core.service.GenericService#getEntityDao()
	 */
	@Override
	protected IEntityDao<UserEnterprise, Long> getEntityDao() {
		// TODO Auto-generated method stub
		return userEnterpriseDao;
	}
	
	/** 
	 * 根据用户ID获取用户企业关系
	 * @param userId 用户ID
	 * @return
	 */
	public List<UserEnterprise> getByUserId(Long userId){
		return userEnterpriseDao.getByUserId(userId);
	}
	
	/** 
	 * 根据用户ID获取已删除用户企业关系
	 * @param userId 用户ID
	 * @return
	 */
	public List<UserEnterprise> getDelByUserId(Long userId){
		return userEnterpriseDao.getDelByUserId(userId);
	}
	
	/** 
	 * 根据用户ID删除用户企业关系
	 * @param userId 用户ID
	 * @return
	 */
	public void delByUserId(Long userId, boolean needSync){
		List<UserEnterprise> userEnterpriseList = this.getByUserId(userId);
		userEnterpriseDao.delByUserId(userId);

		// 用户企业关系需要同步时，将用户企业关系同步到用户中心（在新增用户和删除用户时，不需要单独同步用户企业关系）
		if(needSync){
			SysUser sysUser = sysUserService.getById(userId);
			EventUtil.publishUserEnterpriseEvent(UserEnterpriseEvent.ACTION_DEL, sysUser, userEnterpriseList);
		}
	}
	
	/** 
	 * 根据用户ID和企业编码删除用户企业关系
	 * @param userId
	 * @param enterpriseCode
	 * @param needSync
	 */
	public void delByUserIdAndEnterpriseCode(Long userId, String enterpriseCode, boolean needSync){
		// 根据用户ID和企业编码查询用户企业关系信息，如果不存在该关系则直接返回
		UserEnterprise userEnterprise = this.getByUserIdAndCode(userId, enterpriseCode);
		if(userEnterprise == null){
			return;
		}
		this.delById(userEnterprise.getUserEnterpriseId(), needSync);
	}
	
	/** 
	 * @param userEnterpriseId
	 * @param needSync
	 */
	public void delById(Long userEnterpriseId, boolean needSync){
		this.delById(userEnterpriseId);
		// 用户企业关系需要同步时，将用户企业关系同步到用户中心（在新增用户和删除用户时，不需要单独同步用户企业关系）
		if(needSync){
			UserEnterprise userEnterprise = this.getById(userEnterpriseId);
			SysUser sysUser = sysUserService.getById(userEnterprise.getUserId());
			List<UserEnterprise> userEnterpriseList = new ArrayList<UserEnterprise>();
			userEnterpriseList.add(userEnterprise);
			EventUtil.publishUserEnterpriseEvent(UserEnterpriseEvent.ACTION_DEL, sysUser, userEnterpriseList);
		}
	}
	
	/** 
	 * 更新用户信息
	 * @param userEnterprise
	 * @param needSync
	 */
	public void update(UserEnterprise userEnterprise, boolean needSync){
		userEnterpriseDao.update(userEnterprise);
		if(needSync){
			// 确定操作类型
			int action = UserEnterpriseEvent.ACTION_ADD;
			if(UserEnterprise.DELETE_NO.equals(userEnterprise.getIsDelete())){
				action =  UserEnterpriseEvent.ACTION_DEL;
			}
			
			// 查询用户信息
			SysUser sysUser = sysUserService.getById(userEnterprise.getUserId());
			
			// 用户企业关系 
			List<UserEnterprise> userEnterpriseList = new ArrayList<UserEnterprise>();
			userEnterpriseList.add(userEnterprise);
			EventUtil.publishUserEnterpriseEvent(action, sysUser, userEnterpriseList);
		}
	}
	
	/** 
	 * 保存用户企业关系
	 * @param sysUser 用户信息
	 * @param needSync 是否需要同步到用户中心
	 */
	public void saveUserEnterprise(SysUser sysUser, boolean needSync){
		if(sysUser == null || sysUser.getUserEnterprises() == null || sysUser.getUserEnterprises().length == 0){
			return;
		}
		// 根据用户ID查询用户已经存在的企业关系
		Long userId = sysUser.getUserId();
		List<UserEnterprise> userEnterpriseList = this.getByUserId(userId);
		
		// 已经存在的用户企业关系ID,用来判断哪些已经存在的企业关系需要删除
		List<Long> userEnterpriseIds = new ArrayList<Long>();
		List<UserEnterprise> userEnterprisesAdd = new ArrayList<UserEnterprise>();
		List<UserEnterprise> userEnterprisesDel = new ArrayList<UserEnterprise>();
		UserEnterprise userEnterpriseDb = null;
		Enterpriseinfo enterpriseinfo = null;
		// 遍历保存的企业编码
		String[] enterpriseCodes = sysUser.getUserEnterprises();
		for(String enterpriseCode : enterpriseCodes){
			userEnterpriseDb = this.getByUserIdAndCode(userId, enterpriseCode);
			// 不存在的用户企业关系则新增
			if(userEnterpriseDb == null){
				userEnterpriseDb = this.add(userId, enterpriseCode);
				enterpriseinfo = enterpriseinfoService.getByCompCode(enterpriseCode);
				userEnterpriseDb.setGroupCode(enterpriseinfo.getGroupCode());
			} 
			// 已经存在的用户企业关系，将关系ID加入已存在的用户企业关系ID集合
			else {
				// 如果用户企业关系存在但已被删除，则修改为未删除状态
				if(UserEnterprise.DELETE_YES.equals(userEnterpriseDb.getIsDelete())){
					userEnterpriseDb.setIsDelete(UserEnterprise.DELETE_NO);
					this.update(userEnterpriseDb);
				}
				userEnterpriseIds.add(userEnterpriseDb.getUserEnterpriseId());
			}
			userEnterprisesAdd.add(userEnterpriseDb);
		}
		
		// 删除在已存在的用户企业关系ID集合中不存在的用户企业关系
		for(UserEnterprise userEnterprise : userEnterpriseList){
			if(!userEnterpriseIds.contains(userEnterprise.getUserEnterpriseId())){
				// 删除用户企业关系（逻辑删除）
				this.delById(userEnterprise.getUserEnterpriseId());
				// 删除指定企业下的用户角色关系（物理删除）
				userRoleService.delByUserIdAndEnterpriseCode(userId, userEnterprise.getEnterpriseCode());
				// 删除指定企业下的用户岗位关系（逻辑删除）
				userPositionExtendService.delByUserIdAndEnterpriseCode(userId, 
						userEnterprise.getEnterpriseCode(), UserPosition.DELFROM_USER_EDIT);
				userEnterprisesDel.add(userEnterprise);
			}
		}
		
		// 发布用户企业关系变更事件
		if(needSync){
			EventUtil.publishUserEnterpriseEvent(UserEnterpriseEvent.ACTION_ADD, sysUser, userEnterprisesAdd);
			EventUtil.publishUserEnterpriseEvent(UserEnterpriseEvent.ACTION_DEL, sysUser, userEnterprisesDel);
		}
	}
	
	/** 
	 * 保存用户企业关系
	 * @param sysUser
	 * @param enterpriseCode
	 * @param needSync  是否需要同步到用户中心
	 */
	public void saveUserEnterprise(SysUser sysUser, String enterpriseCode, boolean needSync){
		if(sysUser == null){
			return;
		}
		// 判断用户企业关系是否已经存在
		UserEnterprise userEnterprise = this.getByUserIdAndCode(sysUser.getUserId(), enterpriseCode);
		if(userEnterprise == null){
			userEnterprise = this.add(sysUser.getUserId(), enterpriseCode);
		} else {
			// 如果用户企业关系已经存在，但是已被删除，则更新为未删除状态
			if(UserEnterprise.DELETE_YES.equals(userEnterprise.getIsDelete())){
				userEnterprise.setIsDelete(UserEnterprise.DELETE_NO);
				this.update(userEnterprise);
			}
		}
		// 新增用户时用户企业关系是与用户信息一起同步到用户中心，不用单独做同步；更新用户时的用户企业关系需要单独同步
		if(needSync){
			Enterpriseinfo enterpriseinfo = enterpriseinfoService.getByCompCode(userEnterprise.getEnterpriseCode());
			List<UserEnterprise> userEnterprises = new ArrayList<UserEnterprise>();
			userEnterprise.setGroupCode(enterpriseinfo.getGroupCode());
			userEnterprises.add(userEnterprise);
			EventUtil.publishUserEnterpriseEvent(UserEnterpriseEvent.ACTION_ADD, sysUser, userEnterprises);
		}
	}
	
	/** 
	 * 添加用户企业关系
	 * @param userId 用户ID
	 * @param enterpriseCode 企业编码
	 * @return
	 */
	private UserEnterprise add(Long userId, String enterpriseCode){
		if(userId == null || StringUtils.isBlank(enterpriseCode)){
			return null;
		}
		UserEnterprise userEnterprise = new UserEnterprise();
		userEnterprise.setUserEnterpriseId(UniqueIdUtil.genId());
		userEnterprise.setUserId(userId);
		userEnterprise.setEnterpriseCode(enterpriseCode);
		userEnterprise.setIsDelete(UserEnterprise.DELETE_NO);
		userEnterpriseDao.add(userEnterprise);
		return userEnterprise;
	}

	/** 
	 * 根据用户ID和企业编码获取用户企业关系
	 * @param userId 用户ID
	 * @param enterpriseCode 企业编码
	 * @return
	 */
	public UserEnterprise getByUserIdAndCode(Long userId, String enterpriseCode){
		return userEnterpriseDao.getByUserIdAndCode(userId, enterpriseCode);
	}
}

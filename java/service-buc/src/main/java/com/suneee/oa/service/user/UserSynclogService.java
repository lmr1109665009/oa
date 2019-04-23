/**
 * 
 */
package com.suneee.oa.service.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.oa.dao.user.UserSynclogDao;
import com.suneee.oa.model.user.UserSynclog;
import com.suneee.ucp.base.service.UcpBaseService;

/**
 * @ClassName: UserSynclogService 
 * @Description: 用户同步日志UserSynclog的Service类
 * @Copyright: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-04 10:57:32 
 *
 */
@Service
public class UserSynclogService extends UcpBaseService<UserSynclog>{
	@Resource
	private UserSynclogDao userSynclogDao;
	
	@Override
	protected IEntityDao<UserSynclog, Long> getEntityDao() {
		// TODO Auto-generated method stub
		return userSynclogDao;
	}
	
	/**
	 * 根据用户ID删除用户同步日志
	 * 
	 * @param userId 用户ID
	 */
	public void deleteByUserId(Long userId){
		userSynclogDao.deleteByUserId(userId);
	}
	
	/**
	 * 根据用户ID查询用户同步日志
	 * @param userId
	 * @return
	 */
	public UserSynclog getByUserId(Long userId){
		return userSynclogDao.getByUserId(userId);
	}
	
	/**
	 * 保存用户同步日志
	 * @param userId
	 * @param opType
	 * @param errorInfo
	 * @param note
	 */
	public void save(Long userId, Short opType, String errorInfo, String note){
		if(userId == null){
			this.add(userId, opType, errorInfo, note);
		} else {
			// 查询用户同步日志
			UserSynclog userSynclog = this.getByUserId(userId);
			// 不存在则新增同步日志
			if(userSynclog == null){
				this.add(userId, opType, errorInfo, note);
			} 
			// 存在则更新同步日志
			else {
				userSynclog.setOpType(opType);
				userSynclog.setErrorInfo(errorInfo);
				userSynclog.setNote(note);
				this.update(userSynclog);
			}
		}
	}
	
	/**
	 * 增加用户同步日志
	 * @param userId 用户ID
	 * @param opType 操作类型
	 * @param errorInfo 出错信息
	 * @param note 备注
	 */
	public void add(Long userId, Short opType, String errorInfo, String note){
		UserSynclog userSynclog = new UserSynclog();
		userSynclog.setLogId(UniqueIdUtil.genId());
		userSynclog.setUserId(userId);
		userSynclog.setOpType(opType);
		userSynclog.setErrorInfo(errorInfo);
		userSynclog.setNote(note);
		userSynclog.setUpdatetime(new Date());
		userSynclog.setUpdateBy(ContextUtil.getCurrentUserId());
		this.add(userSynclog);
	}
	
	/** 
	 * 获取信息列表
	 * @param filter
	 * @return
	 */
	public List<Map<String, Object>> getList(QueryFilter filter){
		return userSynclogDao.getList(filter);
	}
	
	/** 
	 * 根据ID获取同步日志信息
	 * @param logId
	 * @return
	 */
	public Map<String, Object> getByLogId(Long logId){
		return userSynclogDao.getByLogId(logId);
	}
}

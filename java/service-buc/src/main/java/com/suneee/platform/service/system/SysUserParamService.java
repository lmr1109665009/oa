package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.system.SysUserParamDao;
import com.suneee.platform.model.system.SysUserParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对象功能:人员参数属性 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-02-24 10:04:50
 */
@Service
public class SysUserParamService extends BaseService<SysUserParam>
{
	@Resource
	private SysUserParamDao sysUserParamDao;
	
	public SysUserParamService()
	{
	}
	
	@Override
	protected IEntityDao<SysUserParam, Long> getEntityDao()
	{
		return sysUserParamDao;
	}
	
	/**
	 * 添加用户参赛数。
	 * @param userId		用户id
	 * @param valueList		用户参数列表。
	 */
	public void add(long userId,List<SysUserParam> valueList){
		sysUserParamDao.delByUserId(userId);
		if(valueList==null||valueList.size()==0)return;
		for(SysUserParam p:valueList){
			sysUserParamDao.add(p);
		}
	}
	
	/**
	 * 根据用户id获取用户参数。
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysUserParam> getByUserId(long userId){
		return sysUserParamDao.getBySqlKey("getByUserId", userId);
	}
	
	/**
	 * 根据用户和参数key获取用户数据。
	 * @param paramKey
	 * @param userId
	 * @return
	 */
	public SysUserParam getByParamKeyAndUserId(String paramKey,Long userId){
		return sysUserParamDao.getByParaUserId(userId,paramKey);
	}
	
	/**
	 * 根据参数Key和参数Value获取用户集合
	 * @param paramKey
	 * @param paramValue
	 * @return
	 */
	public List<SysUserParam> getByParamKeyValue(String paramKey,
			Object paramValue) {
		if(StringUtil.isEmpty(paramKey) ||
				paramValue == null){
			return null;
		}
		return sysUserParamDao.getByParamKeyValue(paramKey,paramValue);
	}
}

package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.SysTypeKeyDao;
import com.suneee.platform.model.system.SysTypeKey;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 对象功能:系统分类键定义 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-03-10 10:18:36
 */
@Service
public class SysTypeKeyService extends BaseService<SysTypeKey>
{
	@Resource
	private SysTypeKeyDao dao;
	
	public SysTypeKeyService()
	{
	}
	
	@Override
	protected IEntityDao<SysTypeKey, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 根据key取得TypeKey对象。
	 * @param key
	 * @return
	 */
	public SysTypeKey getByKey(String key){
		
		return dao.getByKey(key);
	}
	
	/**
	 * 判断键值是否存在。
	 * @param typeKey
	 * @return
	 */
	public boolean isExistKey(String typeKey){
		typeKey=typeKey.toLowerCase();
		return dao.isExistKey(typeKey);
	}
	
	/**
	 * 检查某个Key是否存在
	 * @param typeKey
	 * @param typeId 除本Key外
	 * @return
	 */
	public boolean isKeyExistForUpdate(String typeKey,Long typeId){
		typeKey=typeKey.toLowerCase();
		return dao.isKeyExistForUpdate(typeKey, typeId);
	}
	
	/**
	 * 排序操作。
	 * @param aryTypeId
	 */
	public void saveSequence(Long[] aryTypeId){
		for(int i=0;i<aryTypeId.length;i++){
			dao.updateSequence(aryTypeId[i],i);
		}
	}
	

}

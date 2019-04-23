package com.suneee.platform.service.system;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.system.ResourcesUrlDao;
import com.suneee.platform.model.system.ResourcesUrl;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.model.system.ResourcesUrl;
import com.suneee.platform.dao.system.ResourcesUrlDao;

/**
 * 对象功能:资源URL Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-05 17:00:58
 */
@Service
public class ResourcesUrlService extends BaseService<ResourcesUrl>
{
	@Resource
	private ResourcesUrlDao resourcesUrlDao;
	
	public ResourcesUrlService()
	{
	}
	
	@Override
	protected IEntityDao<ResourcesUrl, Long> getEntityDao()
	{
		return resourcesUrlDao;
	}
	
	public List<ResourcesUrl> getByResId(long resId){
		
		return resourcesUrlDao.getByResId(resId);
	}
	
	public void update(long resId,List<ResourcesUrl> resourcesUrlList){
		resourcesUrlDao.delByResId(resId);
		if(resourcesUrlList!=null&&resourcesUrlList.size()>0){
			for(ResourcesUrl url:resourcesUrlList){
				add(url);
			}
		}
	}
	
	/** 
	 * 通过资源ID删除资源关联的URL
	 * @param resId
	 */
	public void delByResId(Long resId){
		resourcesUrlDao.delByResId(resId);
	}
	
	
}

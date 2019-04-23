package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysHistoryDataDao;
import com.suneee.platform.model.system.SysHistoryData;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *<pre>
 * 对象功能:历史数据 Service类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-26 22:47:29
 *</pre>
 */
@Service
public class SysHistoryDataService extends BaseService<SysHistoryData>
{
	@Resource
	private SysHistoryDataDao dao;
	
	
	
	public SysHistoryDataService()
	{
	}
	
	@Override
	protected IEntityDao<SysHistoryData, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 添加历史数据。
	 * @param dataType
	 * @param content
	 * @param relateId
	 */
	public void add(String dataType,String subject, String content,Long relateId){
		SysUser sysUser=(SysUser) ContextUtil.getCurrentUser();
		
		SysHistoryData data=new SysHistoryData();
		data.setId(UniqueIdUtil.genId());
		data.setContent(content);
		data.setSubject(subject);
		data.setType(dataType);
		data.setObjId(relateId);
		data.setCreatetime(new Date());
		data.setCreator(sysUser.getFullname());
		
		dao.add(data);
		
	}
	
	public List<SysHistoryData> getByObjId(Long relateId){
		return dao.getByObjId(relateId);
	}
	
	
	
	
}

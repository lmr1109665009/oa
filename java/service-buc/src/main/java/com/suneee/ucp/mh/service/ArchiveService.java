package com.suneee.ucp.mh.service;
import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.Job;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.mh.dao.ArchiveDao;
import com.suneee.ucp.mh.model.Archive;

@Service
public class ArchiveService extends UcpBaseService<Archive>{

	@Resource
	private ArchiveDao archiveDao;
	@Override
	protected IEntityDao<Archive, Long> getEntityDao() {
		return archiveDao;
	}

	/**
	 * 获取档案信息列表
	 * @param queryFilter
	 * @return
	 */
	public List<Archive> getArchiveList(QueryFilter queryFilter){
		return archiveDao.getArchiveList(queryFilter);
	}
	
	/**
	 * 保存 档案 信息
	 * @param job
	 */
	public void save(Archive archive){
		String id=archive.getArchiveId();
		if(id==null || id==""){
			this.add(archive);
		}
		else{
			this.update(archive);
		}
	}
}

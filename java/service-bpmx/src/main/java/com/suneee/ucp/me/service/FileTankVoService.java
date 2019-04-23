package com.suneee.ucp.me.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.FileTankVoDao;
import com.suneee.ucp.me.model.FileTankVo;

/**
 * 
 * @ClassName: DocTypeService
 * @Description: 文件类型sercvice
 * @author 游刃
 * @date 2017年4月12日 下午8:21:06
 *
 */
@Service
public class FileTankVoService extends UcpBaseService<FileTankVo> {

	@Resource
	private FileTankVoDao dao;

	@Override
	protected IEntityDao<FileTankVo, Long> getEntityDao() {
		return dao;
	}


/*	public List<FileTankVo> getVoByParentId(Long demId) {
		return dao.getVoByParentId(demId);
	}*/
	
	public List<FileTankVo> getVoByParentIdAndEid(Long demId,String eid) {
		Map<String,Object> pramenters =new HashMap<String,Object>();
		pramenters.put("parentId", demId);
		pramenters.put("eid", eid);
		pramenters.put("orderField","RELEASE_TIME");
		pramenters.put("orderSeq","DESC");
		return dao.getVoByParentIdAndEid(pramenters);
	}
	
	public List<FileTankVo> getVoByParentIdAndDepartmentIdAndEid(Long parentId,Long userId, Long orgId,String eid) {
		Map<String,Object> pramenters =new HashMap<String,Object>();
		pramenters.put("parentId", parentId);
		pramenters.put("userId", userId);
		pramenters.put("departmentId", orgId);
		pramenters.put("eid", eid);
		pramenters.put("orderField","RELEASE_TIME");
		pramenters.put("orderSeq","DESC");
		return dao.getVoByParentIdAndDepartmentIdAndEid(pramenters);
	}
	
	public List<FileTankVo> getVoByParentId(Long parentId,Long userId, Long orgId,String eid) {
		Map<String,Object> pramenters =new HashMap<String,Object>();
		pramenters.put("parentId", parentId);
		pramenters.put("userId", userId);	
		pramenters.put("eid", eid);
		pramenters.put("orderField","RELEASE_TIME");
		pramenters.put("orderSeq","DESC");
		return dao.getVoByParentIdAndEid(pramenters);
	}
	
	public List<FileTankVo> getDocument(Long demId,Long userId,String eid) {
		Map<String,Object> pramenters = new HashMap<String,Object>();
		pramenters.put("parentId", demId);
		pramenters.put("userId", userId);
		pramenters.put("eid", eid);
		pramenters.put("orderField","UPTIME");
		pramenters.put("orderSeq","DESC");
		return  dao.getDocument(pramenters);
	}
}

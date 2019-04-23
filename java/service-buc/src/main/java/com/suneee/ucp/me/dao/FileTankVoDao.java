package com.suneee.ucp.me.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.FileTankVo;

/**
 * 
 * @ClassName: DocTypeDao
 * @Description: 文件类型dao
 * @author 游刃
 * @date 2017年4月12日 下午4:27:39
 *
 */
@Repository
public class FileTankVoDao extends UcpBaseDao<FileTankVo> {

	@Override
	public Class getEntityClass() {
		return FileTankVo.class;
	}
	
	public List<FileTankVo> getVoByParentIdAndDepartmentIdAndEid(Map<String,Object> pramenters) {
		return  this.getBySqlKey("getVoByParentIdAndDepartmentIdAndEid", pramenters);
	}
	
	public List<FileTankVo> getVoByParentIdAndEid(Map<String,Object> pramenters) {
		return  this.getBySqlKey("getVoByParentId", pramenters);
	}

	public List<FileTankVo> getDocument(Map<String,Object> pramenters) {
		return  this.getBySqlKey("getDocument", pramenters);
	}
}

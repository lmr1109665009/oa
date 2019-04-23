package com.suneee.eas.oa.dao.fs;


import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.fs.DocFile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: DocFileDao
 * @Description: 文档管理dao
 * @author 游刃
 * @date 2017年4月12日 下午4:27:50
 *
 */
@Repository
public class DocFileDao extends BaseDao<DocFile> {

	public int deleteAll(Long[] ids) {
		QueryFilter filter=new QueryFilter("deleteAll");
		filter.addFilter("array",ids);
		return this.deleteBySqlKey(filter);
	}

	public int updateDownNumber(Long id) {
		QueryFilter filter=new QueryFilter("updateDownNumber");
		filter.addFilter("id",id);
		return this.updateBySqlKey(filter);
	}

	public int countNameRepetition(Map<String,Object> params){
		return (int) this.getSqlSessionTemplate().selectOne(getNamespace()+".countNameRepetition",params);
	}


	public List<DocFile> findSame(Map<String, String> params) {
		return this.getSqlSessionTemplate().selectList(getNamespace()+".findSame",params);
	}

	public List<DocFile> searchDocFile(Map<String, String> params) {
		return this.getSqlSessionTemplate().selectList(getNamespace()+".searchDocFile",params);
	}

	public int updateParentId(Map<String, Object> params) {
		return this.getSqlSessionTemplate().update(getNamespace()+".updateParentId", params);
	}
	public List<DocFile> getByIds(Long[] docFileIds) {
		return this.getSqlSessionTemplate().selectList(getNamespace()+".getByIds",docFileIds);
	}

	public List<DocFile> getByParentId(Long parentId) {
		
		return this.getSqlSessionTemplate().selectList(getNamespace()+".getByParentId", parentId);
	}
	public List<DocFile> getDocByParentIdAndDepartmentIdAndEid(DocFile doc) {
		return this.getSqlSessionTemplate().selectList(getNamespace()+".getDocByParentIdAndDepartmentIdAndEid", doc);
	}
	
	
	public List<DocFile> getDocByParentIdAndEid(DocFile doc) {
		
		return this.getSqlSessionTemplate().selectList(getNamespace()+".getDocByParentIdAndEid", doc);
	}

	public int deleteByParentId(Long id) {
		return this.getSqlSessionTemplate().delete(getNamespace()+".deleteByParentId", id);
	}

	public List<DocFile> getRoot(long root) {
		
		return this.getSqlSessionTemplate().selectList(getNamespace()+".getRoot",root);
	}

	
}

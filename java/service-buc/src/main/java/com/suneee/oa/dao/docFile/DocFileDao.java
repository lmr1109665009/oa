package com.suneee.oa.dao.docFile;


import com.suneee.oa.model.docFile.DocFile;
import com.suneee.ucp.base.dao.UcpBaseDao;
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
public class DocFileDao extends UcpBaseDao<DocFile> {

	@Override
	public Class getEntityClass() {
		return DocFile.class;
	}

	public int deleteAll(Long[] ids) {
		return this.delBySqlKey("deleteAll", ids);
	}

	public int updateDownNumber(Long id) {
		return this.update("updateDownNumber", id);
	}
	
//	public List<DocFile> getParentId(Long id){
//		return this.getBySqlKey("getParentId", id);
//	}
//	
//	public List<DocFile> frontFileList(QueryFilter queryFilter){
//		return this.getBySqlKey("getFrontSaveFileList", queryFilter);
//	}
//	
//	public List<DocFile> queryFileById(DocFile DocFile){
//		return this.getBySqlKey("queryFileById", DocFile);
//	}
//
	public int countNameRepetition(Map<String,Object> params){
		return (int) this.getOne("countNameRepetition",params);
	}

//	public int updateDocTyeId(Map<String,Long> params) {
//		
//		return this.update("updateDocTyeId",params);
//	}

	public List<DocFile> findSame(Map<String, String> params) {
		return this.getBySqlKey("findSame",params);
	}

	public List<DocFile> searchDocFile(Map<String, String> params) {
		return this.getBySqlKey("searchDocFile",params);
	}

	public int updateParentId(Map<String, Object> params) {
		return this.update("updateParentId", params);
	}
	public List<DocFile> getByIds(Long[] docFileIds) {
		return this.getBySqlKey("getByIds",docFileIds);
	}

	public List<DocFile> getByParentId(Long parentId) {
		
		return this.getBySqlKey("getByParentId", parentId);
	}
	public List<DocFile> getDocByParentIdAndDepartmentIdAndEid(DocFile doc) {
		return this.getBySqlKey("getDocByParentIdAndDepartmentIdAndEid", doc);
	}
	
	
	public List<DocFile> getDocByParentIdAndEid(DocFile doc) {
		
		return this.getBySqlKey("getDocByParentIdAndEid", doc);
	}

	public int deleteByParentId(Long id) {
		return this.delBySqlKey("deleteByParentId", id);
	}

	public List<DocFile> getRoot(long root) {
		
		return this.getBySqlKey("getRoot",root);
	}

	
}

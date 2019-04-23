package com.suneee.ucp.me.dao;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.Document;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: DocumentDao
 * @Description: 文档管理dao
 * @author 游刃
 * @date 2017年4月12日 下午4:27:50
 *
 */
@Repository
public class DocumentDao extends UcpBaseDao<Document> {

	@Override
	public Class getEntityClass() {
		return Document.class;
	}

	public int deleteAll(Long[] ids) {
		return this.delBySqlKey("deleteAll", ids);
	}

	public int updateDownNumber(Long id) {
		return this.update("updateDownNumber", id);
	}
	
	public List<Document> getByDocType(Long id){
		return this.getBySqlKey("getByDocType", id);
	}
	
	public List<Document> frontFileList(QueryFilter queryFilter){
		return this.getBySqlKey("getFrontSaveFileList", queryFilter);
	}
	
	public List<Document> queryFileById(Document document){
		return this.getBySqlKey("queryFileById", document);
	}

	public int countNameRepetition(Map<String,Object> params){
		return (int) this.getOne("countNameRepetition",params);
	}
}

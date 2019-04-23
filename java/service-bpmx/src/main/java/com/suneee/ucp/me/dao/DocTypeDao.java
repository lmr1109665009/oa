package com.suneee.ucp.me.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.me.model.DocType;

/**
 * 
 * @ClassName: DocTypeDao
 * @Description: 文件类型dao
 * @author 游刃
 * @date 2017年4月12日 下午4:27:39
 *
 */
@Repository
public class DocTypeDao extends UcpBaseDao<DocType> {

	@Override
	public Class getEntityClass() {
		return DocType.class;
	}
	
	public List<DocType> getAllName(DocType docType){
		return this.getBySqlKey("getAllName", docType);
	}
	
	public int getChildNumber(Long id){
		 String delStatement = getIbatisMapperNamespace() + ".getChildNumber";
		 return   getSqlSessionTemplate().selectOne(delStatement, id);
	}

	public List<DocType> getSuperS() {
		return this.getBySqlKey("getSuperS");
	}

	public List<DocType> getDocByParentIdAndDepartmentIdAndEid(DocType doc) {
		return this.getBySqlKey("getDocByParentIdAndDepartmentIdAndEid", doc);
	}
	
	public List<DocType> getDocByParentId(Long parentId) {
		return this.getBySqlKey("getDocByParentId", parentId);
	}
	
	public List<DocType> getDocByParentIdAndEid(DocType doc) {
		
		return this.getBySqlKey("getDocByParentIdAndEid", doc);
	}
	public List<DocType> getDocByNameAndOwerId(DocType docType){
		return this.getBySqlKey("getDocByNameAndOwerId", docType);
	}

	public String queyDoctypeChildIds(Long pid) {
		return (String) this.getOne("queyDoctypeChildIds", pid);
	}

	public int countNameRepetition(Map<String,Object> params){
		return (int) this.getOne("countNameRepetition",params);
	}
}

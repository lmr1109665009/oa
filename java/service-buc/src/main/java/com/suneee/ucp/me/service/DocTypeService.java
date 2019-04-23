package com.suneee.ucp.me.service;

import com.suneee.core.db.IEntityDao;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.DocTypeDao;
import com.suneee.ucp.me.dao.DocumentDao;
import com.suneee.ucp.me.model.DocType;
import com.suneee.ucp.me.model.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: DocTypeService
 * @Description: 文件类型sercvice
 * @author 游刃
 * @date 2017年4月12日 下午8:21:06
 *
 */
@Service
public class DocTypeService extends UcpBaseService<DocType> {

	@Resource
	private DocTypeDao dao;
	
	@Resource
	private DocumentDao docuemntDao;

	@Override
	protected IEntityDao<DocType, Long> getEntityDao() {
		return dao;
	}

	// 找到文档类型和子文档类型串成一个字符串
	public String getTypes(Long id) {
		StringBuffer types = new StringBuffer();
		try {
			DocType docType = null;
			do {
				docType = dao.getById(id);
				types.append(docType.getTypeName());
			} while (0 != docType.getParentId());
		} catch (Exception e) {
		}
		return types.toString();
	}

	public int getChildNumber(Long id) {
		return dao.getChildNumber(id);
	}

	public List<DocType> getAllName(DocType docType) {
		return dao.getAllName(docType);
	}

	public List<DocType> getSuperS() {

		return dao.getSuperS();
	}

	public List<DocType> getDocByParentIdAndDepartmentIdAndEid(Long demId, Long departmentId,String eid){
		DocType doc = new DocType();
		doc.setParentId(demId);
		doc.setDepartmentId(departmentId);
		doc.setEid(eid);
		return dao.getDocByParentIdAndDepartmentIdAndEid(doc);
	}
	
	public List<DocType> getDocByParentIdAndEid(Long demId,String eid) {
		DocType doc = new DocType();
		doc.setParentId(demId);		
		doc.setEid(eid);
		return dao.getDocByParentIdAndEid(doc);
	}
	public List<DocType> getDocByParentId(Long demId) {
		return dao.getDocByParentId(demId);
	}
	public List<DocType> getDocByNameAndOwerId(DocType docType){
		return dao.getDocByNameAndOwerId(docType);
	}
	
	public boolean delAllchilds(Long fid) {
		try {

			List<DocType> childs = this.getDocByParentId(fid);
			if (null != childs && childs.size() > 0) {
				for (DocType docType : childs) {
					delAllchilds(docType.getId());
				}
			}
			//检查是否有文件
			List<Document> documents= docuemntDao.getByDocType(fid);
			if(null!=documents&&documents.size()>0){
				for (Document document : documents) {
					docuemntDao.delById(document.getId());
				}
			}
			
			delById(fid);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 获取所有子分类ID
	 * @param pid
	 * @return
	 */
	public String queyDoctypeChildIds(Long pid) {
		return dao.queyDoctypeChildIds(pid);
	}

	/**
	 * 防重复文件重名
	 * @param docType
	 */
	public void renameDirtory(DocType docType,Map<String,Object> params) {
		int count=dao.countNameRepetition(params);
		if (count==0){
			return;
		}
		int pos=1;
		String newName=docType.getTypeName()+"("+pos+")";
		params.put("name",newName);
		String docTypeName=getNoRepeatName(docType,pos,params);
		docType.setTypeName(docTypeName);
	}

	/**
	 * 获取不重复新名称
	 * @param docType
	 * @param pos
	 * @return
	 */
	private String getNoRepeatName(DocType docType,int pos,Map<String,Object> params){
		int count=dao.countNameRepetition(params);
		if (count==0){
			return (String) params.get("name");
		}
		pos++;
		String newName=docType.getTypeName()+"("+pos+")";
		params.put("name",newName);
		return getNoRepeatName(docType,pos,params);
	}
}

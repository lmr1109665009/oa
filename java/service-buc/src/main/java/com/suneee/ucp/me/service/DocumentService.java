package com.suneee.ucp.me.service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.me.dao.DocumentDao;
import com.suneee.ucp.me.model.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
* @ClassName: DocumentService 
* @Description: 文档管理service 
* @author 游刃 
* @date 2017年4月12日 下午8:21:09 
*
 */
@Service
public class DocumentService extends UcpBaseService<Document>{

	@Resource
	private DocumentDao dao;
	
	@Override
	protected IEntityDao<Document, Long> getEntityDao() {
		return dao;
	}

	public int deleteAll(Long[] ids){
		return dao.deleteAll(ids);
	}
	
	public int updateDownNumber(Long id) {
		return dao.updateDownNumber(id);
	}
	
	public List<Document> frontFileList(QueryFilter queryFilter){
		return dao.frontFileList(queryFilter);
	}
	
	public List<Document> queryFileById(Document document){
		return dao.queryFileById(document);
	}

	/**
	 * 计算文件名称重复数
	 * @param name
	 * @param docTypeId
	 * @return
	 */
	private int countNameRepetition(Long id,String name,Long docTypeId) {
		Map<String,Object> params=new HashMap<String, Object>();
		if (id!=null&&id>0){
			params.put("id",id);
		}
		params.put("name",name);
		params.put("docTypeId",docTypeId);
		return dao.countNameRepetition(params);
	}

	/**
	 * 防重复文件重名
	 * @param adocument
	 */
	public void renameDocument(Document adocument) {
		int count=this.countNameRepetition(adocument.getId(),adocument.getName(),adocument.getDocTypeId());
		if (count==0){
			return;
		}
		int pos=1;
		int extPos=adocument.getName().lastIndexOf(".");
		String newName=adocument.getName().substring(0,extPos)+"("+pos+")"+adocument.getName().substring(extPos);
		String documentName=getNoRepeatName(newName,adocument,pos);
		adocument.setName(documentName);
	}

	/**
	 * 获取不重复新名称
	 * @param name
	 * @param adocument
	 * @param pos
	 * @return
	 */
	private String getNoRepeatName(String name,Document adocument,int pos){
		int count=this.countNameRepetition(adocument.getId(),name,adocument.getDocTypeId());
		if (count==0){
			return name;
		}
		int extPos=adocument.getName().lastIndexOf(".");
		pos++;
		String newName=adocument.getName().substring(0,extPos)+"("+pos+")"+adocument.getName().substring(extPos);
		return getNoRepeatName(newName,adocument,pos);
	}
}

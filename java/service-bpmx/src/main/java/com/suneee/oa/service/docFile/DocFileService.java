package com.suneee.oa.service.docFile;


import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.dao.docFile.DocFileDao;
import com.suneee.oa.model.docFile.DocFile;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.ucp.base.service.UcpBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;


/**
 * 
* @ClassName: DocFileService 
* @Description: 文档管理service 
* @author 游刃 
* @date 2017年4月12日 下午8:21:09 
*
 */
@Service
public class DocFileService extends UcpBaseService<DocFile>{

	@Resource
	private DocFileDao dao;
	@Resource
	private SysOrgService orgService;
	@Resource
	private UserPositionService uerPositionService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;

	@Resource
	private Properties configProperties;
	@Override
	protected IEntityDao<DocFile, Long> getEntityDao() {
		return dao;
	}

	public int deleteAll(Long[] ids){
		return dao.deleteAll(ids);
	}
	
	public int updateDownNumber(Long id) {
		return dao.updateDownNumber(id);
	}
	
	public int move(Long[] docFileIds, Long id,int classify,String eid,Long userId,Long depmentId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		params.put("docFileIds", docFileIds);
		params.put("classify", classify);
		params.put("eid",eid);
		params.put("uper",userId);
		params.put("depmentId",depmentId);
		return dao.updateParentId(params);
	}
	public List<DocFile> getByIds(Long[] docFileIds){
		return dao.getByIds(docFileIds);
	}

	/**
	 * 计算文件名称重复数
	 * @param name
	 * @param parentId
	 * @return
	 */
	private int countNameRepetition(Integer isDocType,String name,Long parentId,int classify) {
		Map<String,Object> params=new HashMap<String, Object>();
		Long cl = (long)classify;
		QueryFilter queryFilter = this.getFilter(new QueryFilter(),cl);
		params.putAll(queryFilter.getFilters());
		params.put("isDocType",isDocType);
		params.put("name",name);
		params.put("parentId",parentId);
		return dao.countNameRepetition(params);
	}

	/**
	 * 防重复文件重名
	 * @param adocFile
	 */
	public void renameDocFile(DocFile adocFile) {
		int classify = this.getClassify(adocFile.getParentId(),(long)adocFile.getClassify());
		int count=this.countNameRepetition(adocFile.getIsDocType(),adocFile.getName(),adocFile.getParentId(),classify);
		if (count==0){
			return;
		}
		int pos=1;
		int extPos=adocFile.getName().lastIndexOf(".");
		String newName="新文件";
		if(extPos==-1){
			newName=adocFile.getName()+"("+pos+")";
		}else{
			newName=adocFile.getName().substring(0,extPos)+"("+pos+")"+adocFile.getName().substring(extPos);
		}
		String docFileName=getNoRepeatName(newName,adocFile,pos);
		adocFile.setName(docFileName);
	}

	/**
	 * 获取不重复新名称
	 * @param name
	 * @param docFile
	 * @param pos
	 * @return
	 */
	private String getNoRepeatName(String name,DocFile docFile,int pos){
		int count=this.countNameRepetition(docFile.getIsDocType(),name,docFile.getParentId(),docFile.getClassify());
		if (count==0){
			return name;
		}
		int extPos=docFile.getName().lastIndexOf(".");
		pos++;
		String newName="";
		if(extPos==-1){
			newName=docFile.getName()+"("+pos+")";
		}else{
			newName=docFile.getName().substring(0,extPos)+"("+pos+")"+docFile.getName().substring(extPos);
		}
		return getNoRepeatName(newName,docFile,pos);
	}
/**
 * 查看是否是复制过的文件
 * @param path
 * @param size
 * @return
 */
	public List<DocFile> findSame(String path, String size) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("path", path);
		params.put("size", size);
		return dao.findSame(params);
	}

	/**
	 * 高级搜索
	 * @param keyWord
	 * @param id
	 * @return
	 */
	public List<DocFile> Search(String keyWord, Long id) {
	Map<String,String> params = new HashMap<String,String>();
	params.put("keyWord", keyWord);
	String sid = id.toString();
	params.put("id", sid);
	return dao.searchDocFile(params);
}

public List<DocFile> getByParentId(Long parentId) {
	return dao.getByParentId(parentId);
}
//
//
public List<DocFile> getDocByParentIdAndDepartmentIdAndEid(Long demId, Long departmentId,String eid){
	DocFile doc = new DocFile();
	doc.setParentId(demId);
	doc.setDepartmentId(departmentId);
	doc.setEid(eid);
	return dao.getDocByParentIdAndDepartmentIdAndEid(doc);
}

public List<DocFile> getDocByParentIdAndEid(Long demId,String eid) {
	DocFile doc = new DocFile();
	doc.setParentId(demId);		
	doc.setEid(eid);
	return dao.getDocByParentIdAndEid(doc);
}

public int deleteByParentId(Long id) {
	return dao.deleteByParentId(id);
}

public List<DocFile> getRoot(long root) {
	return dao.getRoot(root);
}

	/**
	 * 包含该文件的上级目录递归更新size（增加）
	 * @param parentId
	 * @param countSize
	 */
	public void setAddParentSize(Long parentId, Double countSize) {
	DocFile docFile = this.getById(parentId);
		String si = docFile.getSize();
		Double size=this.getDoubleSize(si);
		Double newSize = size+countSize;
		String num="";
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数
		if(newSize>1024){
			 num = df.format(newSize/1024)+"M";
		}else if(newSize<1){
			num = df.format(newSize*1024)+"B";
		}else {
			num = df.format(newSize) + "KB";
		}
		docFile.setSize(num);
		this.update(docFile);
		if(docFile.getParentId()!=1l&&docFile.getParentId()!=null){
			this.setAddParentSize(docFile.getParentId(),countSize);
		}
}

	/**
	 * 包含该文件的上级目录递归更新size（减去）
	 * @param parentId
	 * @param countSize
	 */
	public void setSubParentSize(Long parentId, Double countSize) {
		DocFile docFile = this.getById(parentId);
		String si = docFile.getSize();
		Double size=this.getDoubleSize(si);
		Double newSize=0.0;
		if(size>countSize){
			newSize = size-countSize;
		}
		String num="";
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数
		if(newSize>1024){
			num = df.format(newSize/1024)+"M";
		}else if(newSize<1){
			num = df.format(newSize*1024)+"B";
		}else {
			num = df.format(newSize) + "KB";
		}
		docFile.setSize(num);
		this.update(docFile);
		if(docFile.getParentId()!=1l&&docFile.getParentId()!=null){
			this.setSubParentSize(docFile.getParentId(),countSize);
		}
	}
		public Double getDoubleSize(String si){
			Double size  =0.0;
			if(si!=null&&si.length()!=0){

			//判断size的单位是M,KB还是B
			boolean containM = si.contains("M");
			boolean containK = si.contains("K");
			if(containM){
				size = Double.valueOf(si.substring(0,si.indexOf('M')))*1024;
			}else if(containK){
				size = Double.valueOf(si.substring(0, si.indexOf('K')));
			}else{
				//将B转为KB
				Double sizetemp=0.0;
				if(si.contains("B")){
					 sizetemp = Double.valueOf(si.substring(0, si.indexOf('B')));

				}else{
					sizetemp = Double.valueOf(si);
				}
				DecimalFormat df = new DecimalFormat("0.00");//格式化小数
				size = Double.valueOf(df.format(sizetemp/1024));
			}
		}
			return size;
		}

	/**
	 * 通过文件柜的类型增加筛选条件
	 * ()
	 * @param queryFilter
	 * @param id
	 * @return
	 */
		public QueryFilter getFilter(QueryFilter queryFilter, Long id){

			Long curUserId = ContextUtil.getCurrentUserId();
			//int classify = this.getById(id).getClassify();
			Long depmentId = null;
			String companyCode = ContextSupportUtil.getCurrentEnterpriseCode();
			// 获取当前用户所在的部门id
			List<UserPosition> userPositionList = uerPositionService.getByUserId(curUserId);
			if (userPositionList != null && userPositionList.size() > 0) {
				depmentId = userPositionList.get(0).getOrgId();
			}
			if (id == Long.valueOf(configProperties.getProperty("departmentId"))) {// 如果是部门目录
				// depmentId=null为超级管理员，可查看所有部门文件柜
				if (depmentId != null) {
					// 获取文件目录，过滤没有权限的目录
					queryFilter.addFilter("eid", companyCode);
					queryFilter.addFilter("departmentId", depmentId);
				} else if (companyCode == null) {
					return queryFilter;
				} else {
					queryFilter.addFilter("eid", companyCode);
				}
				// 公共文件柜
			} else if (id == Long.valueOf(configProperties.getProperty("publicId"))) {

				queryFilter.addFilter("eid", companyCode);
				// 个人文件柜
			} else if (id == Long.valueOf(configProperties.getProperty("personalId"))) {
				queryFilter.addFilter("uper", curUserId);
				queryFilter.addFilter("eid",companyCode);
				// 集团文件柜
			} else if (id == Long.valueOf(configProperties.getProperty("groupId"))) {
				String groupCode = this.getGroupCode();
				queryFilter.addFilter("eid", groupCode);
			}
			return queryFilter;
		}

	/**
	 * 获取用户的集团编码
	 * @return
	 */
	public String getGroupCode(){
		Long curUserId = ContextUtil.getCurrentUserId();
		String compCode=ContextSupportUtil.getCurrentEnterpriseCode();
		Map map = enterpriseinfoService.getCodeMapByUserId(curUserId);
		if(map==null){
			return "";
		}
		return enterpriseinfoService.getCodeMapByUserId(curUserId).get(compCode);
	}
	/**
	 * 递归获取目录下的所有文件或者文件夹。
	 * @param id
	 * @param list
	 * @param isDocType
	 * @return
	 */
	public List<DocFile> getList(Long id, List<DocFile> list,int isDocType) {
		QueryFilter queryFilter = new QueryFilter();
		queryFilter.addFilter("id", id);
		queryFilter.addFilter("isDocType", isDocType);
		List<DocFile> temp = new ArrayList<>();
		queryFilter = this.getFilter(queryFilter ,id);
		//将个人文件柜，部门，公共，公司按照页面中的顺序排列。
		if(id==1){
				List<DocFile> temp1 = null;
				List<DocFile> temp2 = new ArrayList<>();
				temp1 = this.getAll(queryFilter);
				temp2.addAll(temp1);

				for (DocFile docFile:temp1) {
					if(docFile.getId()==Long.valueOf(configProperties.getProperty("personalId"))){
						temp2.set(0,docFile);
					}else if(docFile.getId()==Long.valueOf(configProperties.getProperty("publicId"))){
						temp2.set(1,docFile);
					}else if(docFile.getId()==Long.valueOf(configProperties.getProperty("groupId"))){
						temp2.set(2,docFile);
					}else {
						temp2.remove(docFile);
					}
				}
				temp.addAll(temp2);
			}else {
				temp = this.getAll(queryFilter);
			}
		if (temp != null) {
			temp.removeAll(Collections.singleton(null));
			list.addAll(temp);
			for (DocFile docFile : temp) {
				if (docFile != null) {
					//递归获取所有的文件。
					this.getList(docFile.getId(), list,isDocType );
				}
			}
		}
		return list;
	}

	/**
	 * 通过多个id获取文件的名字
	 * @param ids
	 * @return
	 */
	public List getNamesByIds(Long[] ids) {
		return dao.getBySqlKey("getNamesByIds",ids);
	}

	/**
	 * 获取该文件的分类
	 * @param parentId
	 * @param classify
	 * @return
	 */
	public int getClassify(Long parentId,Long classify) {
		DocFile docFile = this.getById(parentId);
		if(docFile!=null&&docFile.getClassify()!=null){
			int  docFileClassify = docFile.getClassify();
			if(docFileClassify!=0){
				return docFileClassify;
			}
		}
		//classify如果为0，则递归获取最顶层的id作为calssify
		String cla="0";
		if (docFile != null) {
			classify = parentId;
			cla = classify.toString();
			parentId = docFile.getParentId();
			if(parentId!=1l){
				return this.getClassify(parentId,classify);
			}
		}
		return Integer.parseInt(cla);
	}

}





package com.suneee.eas.oa.service.fs;


import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.fs.DocFile;

import java.util.List;


/**
 * 
* @ClassName: DocFileService 
* @Description: 文档管理service 
* @author 游刃 
* @date 2017年4月12日 下午8:21:09 
*
 */
public interface DocFileService extends BaseService<DocFile> {

	public int deleteAll(Long[] ids);
	
	public int updateDownNumber(Long id);
	
	public int move(Long[] docFileIds, Long id,int classify,String eid,Long userId,Long depmentId);
	public List<DocFile> getByIds(Long[] docFileIds);


	/**
	 * 防重复文件重名
	 * @param adocFile
	 */
	public void renameDocFile(DocFile adocFile,boolean isAdd);


	public void renameArchiveFile(DocFile docFile);


/**
 * 查看是否是复制过的文件
 * @param path
 * @param size
 * @return
 */
	public List<DocFile> findSame(String path, String size);

	/**
	 * 高级搜索
	 * @param keyWord
	 * @param id
	 * @return
	 */
	public List<DocFile> Search(String keyWord, Long id);

	public List<DocFile> getByParentId(Long parentId);

public List<DocFile> getDocByParentIdAndDepartmentIdAndEid(Long demId, Long departmentId,String eid);

public List<DocFile> getDocByParentIdAndEid(Long demId,String eid);

public int deleteByParentId(Long id);

public List<DocFile> getRoot(long root);

	/**
	 * 包含该文件的上级目录递归更新size（增加）
	 * @param parentId
	 * @param countSize
	 */
	public void setAddParentSize(Long parentId, Double countSize);

	/**
	 * 包含该文件的上级目录递归更新size（减去）
	 * @param parentId
	 * @param countSize
	 */
	public void setSubParentSize(Long parentId, Double countSize);
		public Double getDoubleSize(String si);

	/**
	 * 通过文件柜的类型增加筛选条件
	 * ()
	 * @param queryFilter
	 * @param id
	 * @return
	 */
		public QueryFilter getFilter(QueryFilter queryFilter, Long id);

	/**
	 * 获取用户的集团编码
	 * @return
	 */
	public String getGroupCode();
	/**
	 * 递归获取目录下的所有文件或者文件夹。
	 * @param id
	 * @param list
	 * @param isDocType
	 * @return
	 */
	public List<DocFile> getList(Long id, List<DocFile> list,int isDocType);

	/**
	 * 通过多个id获取文件的名字
	 * @param ids
	 * @return
	 */
	public List getNamesByIds(Long[] ids);

	/**
	 * 获取该文件的分类
	 * @param parentId
	 * @param classify
	 * @return
	 */
	public int getClassify(Long parentId,Long classify);

}





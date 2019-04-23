package com.suneee.platform.service.system;


import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.oa.Exception.TipInfoException;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.dao.system.DictionaryDao;
import com.suneee.platform.dao.system.GlobalTypeDao;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 对象功能:数据字典 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ljf
 * 创建时间:2011-11-23 11:07:27
 */
@Service
public class DictionaryService extends BaseService<Dictionary>
{
	@Resource
	private DictionaryDao dictionaryDao;
	@Resource
	private GlobalTypeDao globalTypeDao;
	@Resource
	private GlobalTypeService   globalTypeService;
	@Resource
	private IdentityService identityService;
	@Resource
	private ShareService shareService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	
	public DictionaryService()
	{
	}
	
	@Override
	protected IEntityDao<Dictionary, Long> getEntityDao() {
		return dictionaryDao;
	}
	
	/**
	 * 根据分类表的nodekey获取数据字典数据。
	 * @param nodeKey
	 * @return
	 */
	public List<Dictionary> getByNodeKey(String nodeKey){
		GlobalType globalType =globalTypeDao.getByDictNodeKey(nodeKey);
		if (globalType == null){
			return new ArrayList<Dictionary>();
		}
		long typeId=globalType.getTypeId();
		 List<Dictionary> list = dictionaryDao.getByTypeId(typeId);
		 return list;
	}

	/**
	 * 根据nodePath获取数据字典
	 * @param nodePath
	 * @return
	 */
	public List<Dictionary> getByNodepath(String nodePath){
		return dictionaryDao.getByNodePath(nodePath);
	}
	
	public List<Dictionary> getByDictTypeAndKey(Map<String, String> map){
		return dictionaryDao.getByDictTypeAndKey(map);
	}
	public List<Dictionary> getByNodeKeyAndEid(String nodeKey,String eid){
		GlobalType globalType =globalTypeDao.getByDictNodeKeyAndEid(nodeKey,eid);
		if (globalType == null){
			return new ArrayList<Dictionary>();
		}
		long typeId=globalType.getTypeId();
		return dictionaryDao.getByTypeIdAndEid(typeId,eid);
	}
	 
	/**
	 * 根据分类ID获取字典。
	 * 根据分类添加一个根节点。
	 * @param typeId 分类ID
	 * @param needRoot	是否需要根节点。
	 * @return
	 */
	public List<Dictionary> getByTypeId(long typeId,boolean needRoot){
		GlobalType globalType=globalTypeDao.getById(typeId);
		
		List<Dictionary> list=dictionaryDao.getByTypeId( typeId);
		for(Dictionary dic:list){
			dic.setType(globalType.getType());
		}
		if(needRoot){
			Dictionary dictionary=new Dictionary();
			dictionary.setDicId(typeId);
			dictionary.setParentId(0L);
			dictionary.setItemName(globalType.getTypeName());
			dictionary.setType(globalType.getType());
			list.add(0, dictionary);
		}
		return list;
	}
	
	/**
	 * 根据分类ID获取字典。
	 * 根据分类添加一个根节点。
	 * @param parentId 父节点ID
	 * @return
	 */
	public List<Dictionary> getByParentId(long parentId){
		List<Dictionary> list=dictionaryDao.getByParentId( parentId);
		return list;
	}
	/**
	 * 根据字典ID删除字典数据。
	 * @param dicId		字典ID。
	 */
	public void delByDicId(Long dicId){
		Dictionary dictionary= dictionaryDao.getById(dicId);
		String nodePath=dictionary.getNodePath();
		List<Dictionary> list= dictionaryDao.getByNodePath(nodePath);
		for(Dictionary dic:list){
			dictionaryDao.delById(dic.getDicId());
		}
	}
	
	/**
	 * 判断关键字是否存在。用于添加时进行判断。
	 * @param typeId		分类id
	 * @param itemKey		字典key
	 * @return
	 */
	public boolean isItemKeyExists(long typeId,String itemKey,String itemValue)
	{
		return dictionaryDao.isItemKeyExists(typeId, itemKey,itemValue);
	}
	
	/**
	 * 判断字典关键字是否存在，用于更新是做判断。
	 * @param dicId
	 * @param typeId
	 * @param itemKey
	 * @return
	 */
	public boolean isItemKeyExistsForUpdate(long dicId, long typeId,String itemKey)
	{
		return dictionaryDao.isItemKeyExistsForUpdate(dicId,typeId, itemKey);
	}

	/**
	 * 判断字典项值是否存在，用于更新是做判断。
	 * @param typeId
	 * @param itemValue
	 * @return
	 */
	public boolean isItemValueExistsForUpdate(long typeId, long dicId,String itemValue)
	{
		return dictionaryDao.isItemValueExistsForUpdate(typeId,dicId,itemValue);
	}

	/**
	 * 更新字典排序。
	 * @param lAryId
	 */
	public void updSn(Long[] lAryId){
		if(BeanUtils.isEmpty(lAryId)) 	return;
		for(int i=0;i<lAryId.length;i++){
			int sn=i+1;
			Long dicId=lAryId[i];
			dictionaryDao.updSn(dicId, sn);
		}
	}
	/**
	 * 拖动移动节点。
	 * @param targetId		目标id
	 * @param dragId		拖动的节点id
	 * @param moveType		拖动类型 (prev,next,inner);
	 */
	public void move(Long targetId,Long dragId,String moveType){
		Dictionary target=dictionaryDao.getById(targetId);
		Dictionary dragged=dictionaryDao.getById(dragId);
		
		String nodePath=dragged.getNodePath();
		List<Dictionary> list=dictionaryDao.getByNodePath(nodePath);
		
		//修改拖动节点的父亲
		for(Dictionary dictionary:list){
			
			if ("prev".equals(moveType) || "next".equals(moveType)) {
				String targetPath=target.getNodePath();
				String parentPath=targetPath.endsWith(".")?targetPath.substring(0,targetPath.length()-1):targetPath;
				//这个路径尾部带 "." 。
				parentPath=parentPath.substring(0,parentPath.lastIndexOf(".")+1) ;
				
				if(dictionary.getDicId().equals(dragId)){
					dictionary.setParentId(target.getParentId());
					dictionary.setNodePath(parentPath + dragId +".");
				}
				else{
					String path = dictionary.getNodePath();
					String tmpPath =parentPath + dragId +"." +   path.replaceAll(nodePath, "");
					dictionary.setNodePath(tmpPath);
				}
				if ("prev".equals(moveType)) {
					dictionary.setSn(target.getSn()-1);
				} else {
					dictionary.setSn(target.getSn() + 1);
				}
			}
			else{
				//
				if(dictionary.getDicId().equals(dragId)){
					//修改拖动的分类对象
					//需改父节点
					dictionary.setParentId(targetId);
					//修改nodepath
					dictionary.setNodePath(target.getNodePath() + dictionary.getDicId() +".");
				}
				else{
					//带点的路径
					String path=dictionary.getNodePath();
					//替换父节点的路径。
					String tmpPath=path.replaceAll(nodePath, "");
					//新的父节路径
					String targetPath=target.getNodePath() ;
					//新的父节点 +拖动的节点id + 后缀
					String tmp =targetPath + dragged.getDicId() + "." + tmpPath;
				
					dictionary.setNodePath(tmp);
				}
			}
			
			dictionaryDao.update(dictionary);
		}
		
	}

	public List<Dictionary> getBytypeIdAndEid(Long typeId, String eid,boolean needRoot) {
		// TODO Auto-generated method stub
		 GlobalType globalType=globalTypeDao.getById(typeId);
		 List<Dictionary> list = dictionaryDao.getByTypeIdAndEid(typeId, eid);
		 for(Dictionary dic:list){
				dic.setType(globalType.getType());
			}
			if(needRoot){
				Dictionary dictionary=new Dictionary();
				dictionary.setDicId(typeId);
				dictionary.setParentId(0L);
				dictionary.setItemName(globalType.getTypeName());
				dictionary.setType(globalType.getType());
				list.add(0, dictionary);
			}
		 return list;
	}

	public List<Dictionary> getByItemValue(String itemValue) {
		// TODO Auto-generated method stub
		return dictionaryDao.getByItemValue(itemValue);
	}
	
	/**
	 * 根据数据字典的类型ID和字典项的name值获取数据字典信息
	 * @param typeId
	 * @param itemName
	 * @return
	 */
	public List<Dictionary> getByTypeAndItemName(Long typeId, String itemName){
		return dictionaryDao.getByTypeAndItemName(typeId, itemName);
	}
	
	/**
	 * 根据类型表的nodeKey字段和数据字典项的name值获取数据字典信息
	 * @param nodeKey
	 * @param itemName
	 * @return
	 */
	public Dictionary getByNodeKeyAndItemName(String nodeKey, String itemName){
		// 根据nodeKey查询数据字典的类型信息
		GlobalType globalType = globalTypeDao.getByCateKeyAndNodeKey(GlobalType.CAT_DIC, nodeKey);
		if(globalType == null){
			return null;
		}
		// 根据类型ID和字典项的name查询数据字典信息
		List<Dictionary> dicList = this.getByTypeAndItemName(globalType.getTypeId(), itemName);
		if(dicList.isEmpty()){
			return null;
		}
		return dicList.get(0);
	}
	
/** 
	 * 根据catKey和nodeKey获取指定企业下的字典信息
	 * @param catKey
	 * @param nodeKey
	 * @param enterpriseCode
	 * @return
	 */
	public Dictionary getByNodeKeyAndItemName(String nodeKey, String itemName, String enterpriseCode){
		// 根据nodeKey查询数据字典的类型信息
		GlobalType globalType = globalTypeDao.getByCateKeyAndNodeKey(GlobalType.CAT_DIC, nodeKey, enterpriseCode);
		if(globalType == null){
			return null;
		}
		// 根据类型ID和字典项的name查询数据字典信息
		List<Dictionary> dicList = this.getByTypeAndItemName(globalType.getTypeId(), itemName);
		if(dicList.isEmpty()){
			return null;
		}
		return dicList.get(0);
	}
	

	public Dictionary getByTypeIdAndItemValue(Long typeId, String itemValue) {
		
		return dictionaryDao.getByTypeIdAndItemValue(typeId,itemValue);
	}

	/**
	 * 对象功能：根据查询条件数据字典
	 */
	public List<Dictionary> getDictionarysByQueryFilter(QueryFilter queryFilter) {

		return dictionaryDao.getDictionarysByQueryFilter(queryFilter);
	}


	public List<Dictionary> getBytypeIdAndItemName(Long typeId, String itemName,boolean needRoot) {
		// TODO Auto-generated method stub
		GlobalType globalType=globalTypeDao.getById(typeId);
		List<Dictionary> list = dictionaryDao.getByTypeIdAndItemName(typeId, itemName);
		for(Dictionary dic:list){
			dic.setType(globalType.getType());
		}
		if(needRoot){
			Dictionary dictionary=new Dictionary();
			dictionary.setDicId(typeId);
			dictionary.setParentId(0L);
			dictionary.setItemName(globalType.getTypeName());
			dictionary.setType(globalType.getType());
			list.add(0, dictionary);
		}
		return list;
	}

	/**
	 * 新版：新增/更新数据字典
	 */
	public void saveGlobaltypeAndDictionary(HttpServletRequest request, HttpServletResponse response, GlobalType globalType, List<Dictionary> dicList) throws Exception{
		//获取树形结构所有子项
		List<Dictionary> childList = new ArrayList<>();
		getTreeChildList(dicList,childList);
		//新增/更新数据字典类型
		long typeId = saveGlobaltype(request,globalType);
		//对比更新数据和数据库信息，从数据库删除已删掉数据
		if(globalType.getTypeId() != 0){
			//获取数据库中该数据字典的字典项
			List<Dictionary> databaseDicList = dictionaryDao.getByTypeId(globalType.getTypeId());
			if(databaseDicList.size() > 0){
				for(Dictionary dic1:databaseDicList){
					int number = 0;
					for(Dictionary dic2:childList){
						if(dic1.getDicId().longValue() == dic2.getDicId().longValue()){
							number++;
						}
					}
					if(number == 0){
						dictionaryDao.delById(dic1.getDicId());
					}
				}
			}
		}
		//新增/更新数据字典
		for(Dictionary dic:dicList){
			Long dicId = saveDictionary(typeId,typeId,dic);
			if(dic.getDicList().size() > 0){
				saveDicOrderList(typeId,dicId,dic.getDicList());
			}
		}
	}

	//保存、更新树形数据字典子项
	private void saveDicOrderList(Long typeId,Long parentId,List<Dictionary> dicList) throws UnsupportedEncodingException {
		for(Dictionary dic:dicList){
			Long dicId = saveDictionary(typeId,parentId,dic);
			if(dic.getDicList().size() > 0){
				saveDicOrderList(typeId,dicId,dic.getDicList());
			}
		}
	}

	/**
	 * 获取树结构的所有子项
	 * @param list
	 * @return
	 */
	private void getTreeChildList(List<Dictionary> list,List<Dictionary> childList){
		if(list.size() > 0){
			for(Dictionary dic:list){
				childList.add(dic);
				if(dic.getDicList().size() > 0){
					getTreeChildList(dic.getDicList(),childList);
				}
			}
		}
	}

	/**
	 * 新增/更新数据字典类型
	 * @param globalType
	 * @return
	 */
	private long saveGlobaltype(HttpServletRequest request,GlobalType globalType) throws Exception{
		boolean isadd=globalType.getTypeId()==0;
		//父节点
		long parentId= RequestUtil.getLong(request, "parentId",0);
		//是否根节点
		int isRoot=RequestUtil.getInt(request, "isRoot");
		int isPrivate=RequestUtil.getInt(request, "isPrivate",0);
		Long userId= ContextUtil.getCurrentUserId();

		String enterpriseCode = globalType.getEnterpriseCode();
		if(globalType.getTypeId()==0){
			if(parentId!=0){
				GlobalType parentGlobal=globalTypeService.getById(parentId);
				if(parentGlobal!=null){
					parentGlobal.setIsLeaf(1);
					globalTypeService.update(parentGlobal);
				}
			}
			GlobalType tmpGlobalType=globalTypeService.getInitGlobalType(isRoot,parentId);
			String catKey=tmpGlobalType.getCatKey();
			String nodeKey=getUniqueKeyAdd(0,globalType.getTypeName(),catKey,enterpriseCode);
			globalType.setNodeKey(nodeKey);
			//分类key不为数据字典的情况
			if(!catKey.equals(GlobalType.CAT_DIC)){
				globalType.setType(tmpGlobalType.getType());
			}
			//设置用户ID
			if(isPrivate==1){
				globalType.setUserId(userId);
			}
			globalType.setCatKey(catKey);
			globalType.setNodePath(tmpGlobalType.getNodePath());
			globalType.setTypeId(tmpGlobalType.getTypeId());
			globalType.setDepth(1);
			globalType.setSn(0L);
			globalType.setIsLeaf(0);
			if(globalType.getNodeCodeType().equals(GlobalType.NODE_CODE_TYPE_AUTO_Y)){
				globalType.setNodeCode(identityService.nextId(globalType.getNodeCode()));
			}
			globalTypeService.add(globalType);
		}else{
			if(isRoot==1){
				globalType.setNodePath(parentId +"." + globalType.getTypeId() +".");
			} else{
				GlobalType parentGlobalType=globalTypeDao.getById(parentId);
				globalType.setNodePath(parentGlobalType.getNodePath() +globalType.getTypeId() +".");
			}
			if(StringUtil.isEmpty(globalType.getNodeKey())){
				throw new TipInfoException("数据字典key不能为空!");
			}
			boolean nodeKeyExistsForUpdate = globalTypeService.isNodeKeyExistsForUpdate(globalType.getTypeId(), GlobalType.CAT_DIC, globalType.getNodeKey(), CookieUitl.getCurrentEnterpriseCode());
			if(nodeKeyExistsForUpdate){
				throw new TipInfoException("数据字典key已存在!");
			}
			globalTypeService.updateAndSubPath(globalType);
		}
		try {
			SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
			SysAuditThreadLocalHolder.putParamerter("typeId", globalType.getTypeId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return globalType.getTypeId();
	}

	/**
	 * 新增/更新数据字典
	 * @param typeId
	 * @param dictionary
	 */
	private Long saveDictionary(Long typeId,Long parentId,Dictionary dictionary) throws UnsupportedEncodingException {
		Long dicId=dictionary.getDicId();
		String itemKey=dictionary.getItemKey();
		String itemValue=dictionary.getItemValue();
		//添加
		dictionary.setTypeId(typeId);
		dictionary.setParentId(parentId);
		if(dicId==0){
			if(StringUtil.isNotEmpty(itemKey)||StringUtil.isNotEmpty(itemValue)){
				boolean rtn= isItemKeyExists(typeId, itemKey,itemValue);
				if(rtn){
					throw new TipInfoException("字典项值已存在");
				}
			}
			//生成唯一的字典关键字
			itemKey = getUniqueAddItemKey(0, dictionary.getItemName(), typeId, itemValue);
			dictionary.setItemKey(itemKey);
			try{
				dicId= UniqueIdUtil.genId();
				dictionary.setDicId(dicId);
				//dictionary.setSn(0L);
				//根节点。
				if(parentId.equals(typeId)){
					dictionary.setNodePath(parentId +"." + dicId +".");
				}
				else{
					Dictionary parentDic=getById(parentId);
					dictionary.setParentId(parentId);
					dictionary.setNodePath(parentDic.getNodePath() + dicId +".");
				}

				add(dictionary);
				SysAuditThreadLocalHolder.putParamerter("isAdd", true);
				SysAuditThreadLocalHolder.putParamerter("dicId", dictionary.getDicId().toString());
			}
			catch (Exception ex){
				String str = MessageUtil.getMessage();
				ex.printStackTrace();
				logger.error(ex.getMessage());
				if (StringUtil.isNotEmpty(str)) {
					throw new TipInfoException("添加字典失败:"+str);
				} else {
					String message = ExceptionUtil.getExceptionMessage(ex);
					throw new TipInfoException(message);
				}
			}
		}
		else{
			if(StringUtil.isNotEmpty(itemValue)){
				boolean rtn= isItemValueExistsForUpdate(typeId, dicId,itemValue);
				if(rtn){
					throw new TipInfoException("字典项值已存在");
				}
			}
			/*if(StringUtil.isNotEmpty(itemKey)){
				boolean rtn= isItemKeyExistsForUpdate(dicId, typeId, itemKey);
				if(rtn){
					throw new TipInfoException("字典关键字已存在");
				}
			}*/
			itemKey = getUniqueUpdateItemKey(0, dictionary.getItemName(), dicId, typeId);
			dictionary.setItemKey(itemKey);
			try{
				update(dictionary);
				SysAuditThreadLocalHolder.putParamerter("isAdd", false);
				SysAuditThreadLocalHolder.putParamerter("dicId", dictionary.getDicId().toString());
			}
			catch (Exception e) {
				String str = MessageUtil.getMessage();
				e.printStackTrace();
				logger.error(e.getMessage());
				if (StringUtil.isNotEmpty(str)) {
					throw new TipInfoException("编辑字典失败:"+str);
				} else {
					String message = ExceptionUtil.getExceptionMessage(e);
					throw new TipInfoException(message);
				}
			}
		}
		return dictionary.getDicId();
	}

	/**
	 * 获取唯一key
	 * @param pos
	 * @param name
	 * @param catKey
	 * @param enterpriseCode
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getUniqueKeyAdd(int pos,String name,String catKey,String enterpriseCode) throws UnsupportedEncodingException {
		String nodeKey="";
		if (pos>0){
			nodeKey=shareService.getPingyin(name+pos);
		}else {
			nodeKey=shareService.getPingyin(name);
		}
		boolean isExistEnterprise=globalTypeService.isNodeKeyExistsForEnterprise(catKey, nodeKey,enterpriseCode);
		boolean isExistNode=globalTypeService.isNodeKeyExists(catKey,nodeKey,enterpriseCode);
		if (isExistEnterprise||isExistNode){
			return getUniqueKeyAdd(++pos,name,catKey,enterpriseCode);
		}else {
			return nodeKey;
		}
	}
	private String getUniqueKeyUpdate(int pos,String name,Long typeId,String catKey,String enterpriseCode) throws UnsupportedEncodingException {
		String nodeKey="";
		if (pos>0){
			nodeKey=shareService.getPingyin(name+pos);
		}else {
			nodeKey=shareService.getPingyin(name);
		}
		boolean isExistEnterprise=globalTypeService.isNodeKeyExistsForEnterprise(catKey, nodeKey,enterpriseCode);
		boolean isExistNode=globalTypeService.isNodeKeyExistsForUpdate(typeId,catKey,nodeKey,CookieUitl.getCurrentEnterpriseCode());
		if (isExistEnterprise||isExistNode){
			return getUniqueKeyUpdate(++pos,name,typeId,catKey,enterpriseCode);
		}else {
			return nodeKey;
		}
	}

	//生成唯一的数据字典关键字
	private String getUniqueAddItemKey(int pos,String name,Long typeId,String itemValue) throws UnsupportedEncodingException {
		String itemKey = "";
		if (pos > 0) {
			itemKey = shareService.getPingyin(name + pos);
		} else {
			itemKey = shareService.getPingyin(name);
		}
		boolean rtn = isItemKeyExists(typeId, itemKey, itemValue);
		if (rtn) {
			return getUniqueAddItemKey(++pos,name,typeId,itemValue);
		} else {
			return itemKey;
		}
	}

	private String getUniqueUpdateItemKey(int pos,String name,Long dicId,Long typeId) throws UnsupportedEncodingException {
		String itemKey = "";
		if (pos > 0) {
			itemKey = shareService.getPingyin(name + pos);
		} else {
			itemKey = shareService.getPingyin(name);
		}
		boolean rtn= isItemKeyExistsForUpdate(dicId, typeId, itemKey);
		if (rtn) {
			return getUniqueUpdateItemKey(++pos,name,dicId,typeId);
		} else {
			return itemKey;
		}
	}
}

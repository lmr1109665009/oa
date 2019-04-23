package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.dao.system.DictionaryDao;
import com.suneee.platform.dao.system.GlobalTypeDao;
import com.suneee.platform.dao.system.SysTypeKeyDao;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysTypeKey;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;

/**
 * 对象功能:总分类表 Service类 开发公司:广州宏天软件有限公司 
 * 开发人员:ljf 
 * 创建时间:2011-11-23 11:07:27
 */
@Service
public class GlobalTypeService extends BaseService<GlobalType> {
	@Resource
	private GlobalTypeDao globalTypeDao;
	@Resource
	private DictionaryDao dictionaryDao;
	@Resource
	private SysTypeKeyDao sysTypeKeyDao;
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;

	@Override
	protected IEntityDao<GlobalType, Long> getEntityDao() {
		return globalTypeDao;
	}

	/**
	 * 取得初始分类类型。
	 * @param isRoot	是否根节点。
	 * @param parentId	父节点。
	 * @return
	 * @throws Exception 
	 */
	public GlobalType getInitGlobalType(int isRoot, long parentId) throws Exception{
		GlobalType globalType = new GlobalType();
		Long typeId= UniqueIdUtil.genId();
		//如果是根节点，则从SysTypeKey获取数据构建分类类型
		if(isRoot==1){
			SysTypeKey sysTypeKey=sysTypeKeyDao.getById(parentId);
			globalType.setCatKey(sysTypeKey.getTypeKey());
			globalType.setTypeName(sysTypeKey.getTypeName());
			globalType.setParentId(parentId);
			globalType.setNodePath(parentId +"." + typeId +".");
			globalType.setType(sysTypeKey.getType());
		}
		else{
			//获取父类构建分类类型。
			globalType=globalTypeDao.getById(parentId);
			String nodePath=globalType.getNodePath() ;
			globalType.setNodePath(nodePath +typeId +".");
		}
		globalType.setTypeId(typeId);
		return globalType;
	}

	/**
	 * 根据parentId返回GlobalType列表
	 * @param parentId
	 * @param catKey
	 * @return
	 */
	public List<GlobalType> getByParentId(Long parentId, String catKey)
	{
		return globalTypeDao.getByParentId(parentId, catKey);
	}

	/**
	 * 根据查询条件返回GlobalType列表
	 * @param filter
	 * @return
	 */
	public List<GlobalType> getByQueryfilter(QueryFilter filter)
	{
		return globalTypeDao.getByQueryfilter(filter);
	}

	/**
	 * 根据类型Id删除类型，删除数据字典。
	 * @param typeId
	 */
	public void delByTypeId(Long typeId )
	{
		if(BeanUtils.isEmpty(typeId)) return;
		 
		GlobalType gt=globalTypeDao.getById(typeId);
		//留下路径
		String oldNodePath=gt.getNodePath();
		//取得下级的所有节点
		List<GlobalType> childrenList= globalTypeDao.getByNodePath(oldNodePath);
		
		for(GlobalType globalType:childrenList){
			long Id=globalType.getTypeId();
			globalTypeDao.delById(Id);
			//删除数据字典。
			dictionaryDao.delByTypeId(Id);
		}
	}
		
	/**
	 * 根据nodePath查询
	 * @param nodePath
	 * @return
	 */
	public List<GlobalType> getByNodePath(String nodePath){
		return globalTypeDao.getByNodePath(nodePath);
	}
	public GlobalType getByNodeKey(String nodeKey){
		return globalTypeDao.getByNodeKey(nodeKey);
	}
	/**
	 * 对象功能：判断是否存在该接节点key的记录
	 */
	public boolean isNodeKeyExists(String catKey,String nodeKey,String enterpriseCode)
	{
		return globalTypeDao.isNodeKeyExists(catKey, nodeKey,enterpriseCode);
	}	
	
	public boolean isNodeKeyExistsForUpdate(Long typeId,String catKey,String nodeKey,String enterpriseCode){
		return globalTypeDao.isNodeKeyExistsForUpdate(typeId,catKey, nodeKey,enterpriseCode);
	}

	/**
	 * 据企业编码查询分类是否重复
	 * @param typeId
	 * @param catKey
	 * @param nodeKey
	 * @param enterpriseCode
	 * @return
	 */
	public boolean isNodeKeyExistsForEnterprise(Long typeId,String catKey,String nodeKey,String enterpriseCode){
		return globalTypeDao.isNodeKeyExistsForEnterprise(typeId,catKey, nodeKey,enterpriseCode);
	}
	public boolean isNodeKeyExistsForEnterprise(String catKey,String nodeKey,String enterpriseCode){
		return globalTypeDao.isNodeKeyExistsForEnterprise(catKey, nodeKey,enterpriseCode);
	}

	/**
	 *  更新字段的排序。
	 * @param typeId
	 * @param sn
	 */
	public void updSn(Long typeId,Long sn){
		globalTypeDao.updSn(typeId, sn);
	}
	
	/**
	 * 拖动移动节点。
	 * @param targetId		目标id
	 * @param dragId		拖动的节点id
	 * @param moveType		拖动类型 (prev,next,inner);
	 */
	public void move(Long targetId,Long dragId,String moveType){
		GlobalType target=globalTypeDao.getById(targetId);
		GlobalType dragged=globalTypeDao.getById(dragId);
		
		String nodePath=dragged.getNodePath();
		List<GlobalType> list=this.getByNodePath(nodePath);
		
		//修改拖动节点的父亲
		for(GlobalType globalType:list){
			
			//向目标节点的前后拖动。
			if ("prev".equals(moveType) || "next".equals(moveType)) {
				String targetPath=target.getNodePath();
				String parentPath=targetPath.endsWith(".")?targetPath.substring(0,targetPath.length()-1):targetPath;
				//这个路径尾部带 "." 。
				parentPath=parentPath.substring(0,parentPath.lastIndexOf(".")+1) ;
				
				if(globalType.getTypeId().equals(dragId)){
					globalType.setParentId(target.getParentId());
					globalType.setNodePath(parentPath + dragId +".");
				}
				else{
					String path = globalType.getNodePath();
					String tmpPath =parentPath + dragId +"." +   path.replaceAll(nodePath, "");
					globalType.setNodePath(tmpPath);
				}
				globalType.setDepth(target.getDepth());
				
				if ("prev".equals(moveType)) {
					globalType.setSn(target.getSn()-1);
				} else {
					globalType.setSn(target.getSn() + 1);
				}
			}
			else{
				//
				if(globalType.getTypeId().equals(dragId)){
					//修改拖动的分类对象
					//需改父节点
					globalType.setParentId(targetId);
					//修改nodepath
					globalType.setNodePath(target.getNodePath() + globalType.getTypeId() +".");
				}
				else{
					//带点的路径
					String path=globalType.getNodePath();
					//替换父节点的路径。
					String tmpPath=path.replaceAll(nodePath, "");
					//新的父节路径
					String targetPath=target.getNodePath() ;
					//新的父节点 +拖动的节点id + 后缀
					String tmp =targetPath + dragged.getTypeId() + "." + tmpPath;
				
					globalType.setNodePath(tmp);
				}
			}
			
			globalTypeDao.update(globalType);
		}
	
	}
	
	/**
	 * 根据catkey获取数据。
	 * @param catKey
	 * @return
	 */
	public List<GlobalType> getByCatKey(String catKey, boolean hasRoot, String nodePath){
		String ecode= ContextSupportUtil.getCurrentEnterpriseCode();
		List<GlobalType> list= globalTypeDao.getByCatKey(catKey, ecode,nodePath);
		//是否有根节点。
		if(hasRoot){
			SysTypeKey  sysTypeKey=sysTypeKeyDao.getByKey(catKey);
			GlobalType globalType=new GlobalType();
			globalType.setTypeName(sysTypeKey.getTypeName());
			globalType.setCatKey(sysTypeKey.getTypeKey());
			globalType.setParentId(0L);
			globalType.setIsParent("true");
			globalType.setIsType(1);
			globalType.setTypeId(sysTypeKey.getTypeId());
			globalType.setType(sysTypeKey.getType());
			globalType.setNodePath(sysTypeKey.getTypeId() +".");
			globalType.setChildNodes(list.size());
			list.add(0, globalType);
		}
		return list;
	}
	public List<GlobalType> getByCatKey(String catKey,boolean hasRoot){
		return getByCatKey(catKey,hasRoot,null);
	}
	
	
	
	/**
	 * 根据用户ID,角色Id,组织ID取到用户有权限访问的表单分类列表	
	 * @param userId
	 * @param roleIds
	 * @param orgIds
	 * @param hasRoot
	 * @return
	 */
	public Set<GlobalType> getByFormRightCat(Long userId,String roleIds,String orgIds,boolean hasRoot){
		
		Set<GlobalType> globalTypeSet=new LinkedHashSet<GlobalType>();
		
		List<GlobalType> globalTypeList= globalTypeDao.getByFormRights(GlobalType.CAT_FORM, userId, roleIds, orgIds);
		globalTypeSet.addAll(globalTypeList);
		//循环查找下面的所有分类列表，若有父类，则把所有的父类实体也加进来，除了根
		for(GlobalType globalType:globalTypeList){
			if(StringUtils.isNotEmpty(globalType.getNodePath())){
				String parentNodePath=globalType.getNodePath();
				int index=parentNodePath.indexOf(globalType.getTypeId().toString());
				if(index!=-1){
					parentNodePath=parentNodePath.substring(0,index);
				}
				String[] nodePaths =parentNodePath.split("[.]");
				//the nodePaths is like 3.1336721342809.1337595123689.
				if(nodePaths.length>=2){
					for(int i=1;i<nodePaths.length;i++){
						GlobalType parentType=globalTypeDao.getById(new Long(nodePaths[i]));
						globalTypeSet.add(parentType);
					}
				}
			}
		}

		if(hasRoot){
			SysTypeKey  sysTypeKey=sysTypeKeyDao.getByKey(GlobalType.CAT_FORM);
			GlobalType globalType=new GlobalType();
			globalType.setTypeName(sysTypeKey.getTypeName());
			globalType.setCatKey(sysTypeKey.getTypeKey());
			globalType.setParentId(0L);
			globalType.setIsParent("true");
			globalType.setTypeId(sysTypeKey.getTypeId());
			globalType.setType(sysTypeKey.getType());
			globalType.setNodePath(sysTypeKey.getTypeId() +".");
			globalTypeSet.add(globalType);
		}
		return globalTypeSet;
	}
	
	/**
	 * 根据用户id取得类型列表。
	 * @param catKey	分类ID
	 * @param userId	用户ID
	 * @param hasRoot	是否添加根节点
	 * @return
	 */
	public List<GlobalType> getPersonType(String catKey,Long userId,boolean hasRoot){
		List<GlobalType> list= globalTypeDao.getPersonType( catKey,userId);
		//是否有根节点。
		if(hasRoot){
			SysTypeKey  sysTypeKey=sysTypeKeyDao.getByKey(catKey);
			GlobalType globalType=new GlobalType();
			globalType.setTypeName(sysTypeKey.getTypeName());
			globalType.setCatKey(sysTypeKey.getTypeKey());
			globalType.setParentId(0L);
			globalType.setIsParent("true");
			globalType.setTypeId(sysTypeKey.getTypeId());
			globalType.setType(sysTypeKey.getType());
			globalType.setNodePath(sysTypeKey.getTypeId() +".");
			list.add(0, globalType);
		}
		return list;
	}

	
	/**
	 * 根据catKey获取分类的xml数据。
	 * @param catKEY
	 * @return
	 */
	public String getXmlByCatkey(String catKEY){
		String ecode=ContextSupportUtil.getCurrentEnterpriseCode();
		List<GlobalType> list= globalTypeDao.getByCatKey(catKEY, ecode);// 顶级
		SysTypeKey sysTypeKey=sysTypeKeyDao.getByKey(catKEY);
		Long typeId=sysTypeKey.getTypeId();
		
		StringBuffer sb = new StringBuffer("<folder id='0' label='全部'>");
		if(BeanUtils.isNotEmpty(list)){
			for(GlobalType gt : list){
				if(!typeId.equals(gt.getParentId())) continue;
				sb.append("<folder id='" + gt.getTypeId() + "' label='" + gt.getTypeName() + "'>");
				sb.append(getBmpChildList(list,gt.getTypeId()));
				sb.append("</folder>");
			}
		}
		sb.append("</folder>");
		return sb.toString();
	}
	
	/**
	 * 递归调用获取xml。
	 * @param list
	 * @param parentId
	 * @return
	 */
	private String getBmpChildList(List<GlobalType> list,Long parentId){
		StringBuffer sb = new StringBuffer("");
		if(BeanUtils.isNotEmpty(list)){
			for(GlobalType gt : list){
				if(gt.getParentId().equals(parentId)){
					sb.append("<folder id='" + gt.getTypeId() + "' label='" + gt.getTypeName() + "'>");
					sb.append(getBmpChildList(list,gt.getTypeId()));
					sb.append("</folder>");
				}
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * 根据分类的nodekey获取数据字典项的分类。
	 * @param nodeKey		nodeKey
	 * @return
	 */
	public GlobalType getByDictNodeKey(String nodeKey){
		return globalTypeDao.getByDictNodeKey(nodeKey);
	}
	/**
	 * 根据分类的nodekey获取数据字典项的分类。
	 * @param nodeKey		nodeKey
	 * @return
	 */
	public GlobalType getByCateKeyAndNodeKey(String catKey,String nodeKey){
		return globalTypeDao.getByCateKeyAndNodeKey(catKey,nodeKey);
	}
	

	
	
	/**
	 * 导出系统分类
	 * @param typeId     
	 * @return
	 */
	public String exportXml(long typeId) {
		String stringXml="";
		String key="";
		String name="";
		String catKey="";
		int type=1;
		boolean isDic=false;
		GlobalType globalType=globalTypeDao.getById(typeId);
		if(globalType==null){
			SysTypeKey sysTypeKey=sysTypeKeyDao.getById(typeId);
			key=sysTypeKey.getTypeKey();
			name=sysTypeKey.getTypeName();
		}else{
			key=globalType.getNodeKey();
			name=globalType.getTypeName();
			catKey=globalType.getCatKey();
			type=globalType.getType();
		}
		if(key.equals("DIC")|| catKey.equals("DIC")){
			isDic=true;
		}
		Document doc = DocumentHelper.createDocument();	
		Element root = doc.addElement("items");	
		Element node=root.addElement("item");
		node.addAttribute("key",key );
		node.addAttribute("name",name);
		node.addAttribute("type", Integer.toString(type));
		if(isDic){
			List<com.suneee.platform.model.system.Dictionary> list=dictionaryDao.getByParentId(typeId);
			if(list!=null&&list.size()!=0){
				for(com.suneee.platform.model.system.Dictionary dic:list){
					addElementByDic(dic, node);
				}
			}
		}
		List<GlobalType> subs=globalTypeDao.getByParentId(typeId, null);
		if(subs!=null&&subs.size()!=0){
			for(GlobalType g:subs){
				addElements(g,node,isDic);
			}
		}
		stringXml=doc.asXML();
		return stringXml;
	}
	
	/**
	 * 根据系统分类及其子节点 添加元素
	 * @param g
	 * @param e
	 * @param isDic
	 */
	public void addElements(GlobalType g,Element e,boolean isDic){
		Element nodes=e.addElement("item");
		nodes.addAttribute("key",g.getNodeKey());
		nodes.addAttribute("name",g.getTypeName());
		nodes.addAttribute("type",Integer.toString(g.getType()));
		if(!isDic){
			if(g.getIsLeaf()==0||g.getIsLeaf()==null){return;}
		}else{
			List<com.suneee.platform.model.system.Dictionary> list=dictionaryDao.getByTypeId(g.getTypeId());
			if(list!=null&&list.size()!=0){
				for(com.suneee.platform.model.system.Dictionary dic:list){
					addElementByDic(dic,nodes);
				}
			}
		}
		List<GlobalType> subs=globalTypeDao.getByParentId(g.getTypeId(), null);
		if(subs!=null&&subs.size()!=0){
			for(GlobalType gt:subs){
				if(gt.getIsLeaf()==1){
					addElements(gt,nodes,isDic);
				}else{
					Element node=nodes.addElement("item");
					node.addAttribute("key", gt.getNodeKey());
					node.addAttribute("name", gt.getTypeName());
					node.addAttribute("type", Integer.toString(g.getType()));
				}
			}
		}
	}
	
	/**
	 * 根据数据字典数据 添加 元素   
	 * @param dic
	 * @param e
	 */
	public void addElementByDic(com.suneee.platform.model.system.Dictionary dic, Element e){
		Element data=e.addElement("data");
		data.addAttribute("key", dic.getItemKey());
		data.addAttribute("name", dic.getItemName());
		data.addAttribute("type", Integer.toString(dic.getType()));
		data.setText(dic.getItemValue());
		List<com.suneee.platform.model.system.Dictionary> list=dictionaryDao.getByParentId(dic.getDicId());
		if(list!=null&&list.size()!=0){
			for(com.suneee.platform.model.system.Dictionary dictionary:list){
				List<com.suneee.platform.model.system.Dictionary> subs=dictionaryDao.getByParentId(dictionary.getDicId());
				if(subs!=null&&subs.size()!=0){
					addElementByDic(dictionary,data);
				}else{
					Element sub=data.addElement("data");
					sub.addAttribute("name", dictionary.getItemName());
					sub.addAttribute("key", dictionary.getItemKey());
					sub.addAttribute("type", Integer.toString(dic.getType()));
					sub.setText(dictionary.getItemValue());
				}
			}
		}
	}
	
	/**
	 * 导入系统分类
	 * @param inputStream
	 * @param typeId
	 */
	public void importXml(InputStream inputStream,long typeId) {
		SysTypeKey sysTypeKey=null;
		GlobalType globalType=globalTypeDao.getById(typeId);
		boolean isDic=false;
		String catKey="";
		String basePath="";
		if(globalType==null){
			sysTypeKey=sysTypeKeyDao.getById(typeId);
			catKey=sysTypeKey.getTypeKey();
			basePath=typeId+".";
		}else {
			catKey=globalType.getCatKey();
			basePath=globalType.getNodePath();
		}
		
		Document doc= Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		List<Element> list=root.elements();
		for(Element node: list){
			if(catKey.equals("DIC") && node.getName().equals("data")){
					addDicData(node,typeId+".");
			}else{
				addGlobalType(node,basePath,catKey);
			}
		}
	}
	
	/**
	 * 导入系统分类时解析 xml文件，添加字典项
	 * @param e
	 * @param basePath
	 */
	public void addDicData(Element e,String basePath){
		String name=e.attributeValue("name");
		String key=e.attributeValue("key");
		String type=e.attributeValue("type");
		String value=e.getText();
		long dicId=UniqueIdUtil.genId();
		com.suneee.platform.model.system.Dictionary dic=new Dictionary();
		dic.setDicId(dicId);
		String[] paths=basePath.split("\\.");
		String parentId=paths[paths.length-1];
		dic.setParentId(Long.parseLong(parentId));
		dic.setItemKey(key);
		if(type==null){
			dic.setType(1);
		}else{
			dic.setType(Integer.parseInt(type));
		}
		dic.setItemName(name);
		dic.setItemValue(value);
		dic.setSn(0L);
		dic.setNodePath(basePath+dicId+".");
		dic.setTypeId(Long.parseLong(paths[0]));
		dictionaryDao.add(dic);
		List<Element> subs=e.elements();
		if(subs!=null && subs.size()!=0){
			for(Element data: subs){
				addDicData(data,dic.getNodePath());
			}
		}
		
	}
	
	/**
	 * 解析xml 文件 添加GlobalType 实体
	 * @param e
	 * @param typeId
	 * @param catKey
	 * @return
	 */
	public void addGlobalType(Element e,String typeId,String catKey){
		String name=e.attributeValue("name");
		String key=e.attributeValue("key");
		String type=e.attributeValue("type");
		long id=UniqueIdUtil.genId();
		String [] paths=typeId.split("\\.");
		GlobalType g=new GlobalType();
		g.setTypeId(id);
		g.setCatKey(catKey);
		g.setDepth(paths.length);
		g.setNodePath(typeId+id+".");
		g.setParentId(Long.parseLong(paths[paths.length-1]));
		g.setNodeKey(key);
		g.setTypeName(name);
		if(type==null){
			g.setType(1);
		}else{
			g.setType(Integer.parseInt(type));
		}
		g.setSn(0L);
		if(e.elements()!=null||e.elements().size()!=0){
			g.setIsLeaf(1);
			add(g);
			List<Element>list=e.elements();
			for(Element node:list){
				if(catKey.equals("DIC")&& node.getName().equals("data")){
					addDicData(node,id+".");
				}else{
					addGlobalType(node,g.getNodePath(),catKey);
				}
			}
		}else{
			g.setIsLeaf(0);
			add(g);
		}
	}

	/**
	 * 根据catkey取得根节点
	 * @param catKey
	 * @return
	 */
	public GlobalType getRootByCatKey(String catKey){
		SysTypeKey  sysTypeKey=sysTypeKeyDao.getByKey(catKey);
		GlobalType globalType=new GlobalType();
		globalType.setTypeName(sysTypeKey.getTypeName());
		globalType.setCatKey(sysTypeKey.getTypeKey());
		globalType.setParentId(0L);
		globalType.setIsParent("true");
		globalType.setTypeId(sysTypeKey.getTypeId());
		globalType.setType(sysTypeKey.getType());
		globalType.setNodePath(sysTypeKey.getTypeId() +".");
		return globalType;
	}

	/**
	 * 更新分类信息及子分类path
	 * @param globalType
	 */
	public void updateAndSubPath(GlobalType globalType){
		GlobalType oldType=globalTypeDao.getById(globalType.getTypeId());
		Map<String,Object> map=getParentMap(globalType.getParentId());
		GlobalType parent= (GlobalType) map.get("globalType");
		globalType.setNodePath(parent.getNodePath()+globalType.getTypeId()+".");
		globalTypeDao.update(globalType);
		// 更新子分类与企业编码的关联关系
		updateSubNodePath(oldType.getNodePath(),globalType);
	}

	/**
	 * 更新子分类路径
	 * @param oldPath
	 * @param parentType
	 */
	private void updateSubNodePath(String oldPath,GlobalType parentType){
		List<GlobalType> typeList=globalTypeDao.getByNodePath(oldPath);
		for (GlobalType type:typeList){
			GlobalType tmpType=new GlobalType();
			tmpType.setTypeId(type.getTypeId());
			tmpType.setNodePath(type.getNodePath().replace(oldPath,parentType.getNodePath()));
			tmpType.setEnterpriseCode(parentType.getEnterpriseCode());
			globalTypeDao.updateTypeInfo(tmpType);
		}
	}

	/**
	 * 获取父级分类
	 * @param parentId
	 * @return
	 */
	private Map<String,Object> getParentMap(Long parentId){
		GlobalType globalType=globalTypeDao.getById(parentId);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("isRoot",false);
		if(globalType!=null){
			map.put("globalType",globalType);
			return map;
		}
		SysTypeKey  sysTypeKey=sysTypeKeyDao.getById(parentId);
		globalType=new GlobalType();
		globalType.setTypeName(sysTypeKey.getTypeName());
		globalType.setCatKey(sysTypeKey.getTypeKey());
		globalType.setParentId(0L);
		globalType.setIsParent("true");
		globalType.setTypeId(sysTypeKey.getTypeId());
		globalType.setType(sysTypeKey.getType());
		globalType.setNodePath(sysTypeKey.getTypeId() +".");
		map.put("isRoot",true);
		map.put("globalType",globalType);
		return map;
	}
	

	public List<GlobalType> selectByCatekey(String catKey){
		List<GlobalType> globalType = globalTypeDao.selectByCatekey(catKey);
		return globalType;
	}

	/**
	 * 根据catkey获取分类数据（无权限）
	 * @param catKey
	 * @param hasRoot
	 * @param nodePath
	 * @return
	 */
	public List<GlobalType> getByCatKeyWithoutLastLeaf(String catKey,boolean hasRoot,String nodePath){
		List<GlobalType> list= globalTypeDao.getByCatKey(catKey, (List<Long>) null,nodePath);
		//是否有根节点。
		if(hasRoot){
			SysTypeKey  sysTypeKey=sysTypeKeyDao.getByKey(catKey);
			GlobalType globalType=new GlobalType();
			globalType.setTypeName(sysTypeKey.getTypeName());
			globalType.setCatKey(sysTypeKey.getTypeKey());
			globalType.setParentId(0L);
			globalType.setIsParent("true");
			globalType.setIsType(1);
			globalType.setTypeId(sysTypeKey.getTypeId());
			globalType.setType(sysTypeKey.getType());
			globalType.setNodePath(sysTypeKey.getTypeId() +".");
			globalType.setChildNodes(list.size());
			list.add(0, globalType);
		}
		return list;
	}

	/**
	 * 批量删除
	 * @param typeIds
	 */
	public void delByTypeIds(Long[] typeIds){
		for(Long typeId:typeIds){
			GlobalType gt=globalTypeDao.getById(typeId);
			if(gt != null) {
				//留下路径
				String oldNodePath=gt.getNodePath();
				//取得下级的所有节点
				List<GlobalType> childrenList= globalTypeDao.getByNodePath(oldNodePath);

				for(GlobalType globalType:childrenList){
					long Id=globalType.getTypeId();
					globalTypeDao.delById(Id);
					//删除数据字典。
					dictionaryDao.delByTypeId(Id);
				}
			}
		}
	}

	/**
	 * 根据企业编码查询总分类ID列表
	 * @param ecodes 企业编码
	 * @return 返回总分类ID列表
	 */
	public List<Long> getTypeIdsByEcodes(Set<String> ecodes){
		return globalTypeDao.getTypeIdsByEcodes(ecodes);
	}

	/**
	 * 根据企业编码获取分类ID
	 * @param ecode
	 * @return
	 */
	public List<Long> getTypeIdsByEcode(String ecode){
		return globalTypeDao.getTypeIdsByEcode(ecode);
	}

    /**
     * 根据分类标识和企业编码获取默认分类
     * @param catKey
     * @param enterpriseCode
     * @return
     */
    public GlobalType getDefaultType(String catKey,String enterpriseCode){
        Map<String,Object> params=new HashMap<String, Object>();
        params.put("catKey",catKey);
        params.put("enterpriseCode",enterpriseCode);
        params.put("nodeKey",GlobalType.CAT_DEFAULT);
        return globalTypeDao.getUnique("getByKeyAndCode",params);
    }

    /**
     * 根据分类标识及企业编码添加默认分类
     * @param catKey
     * @param enterpriseCode
     * @return
     */
    public GlobalType addDefaultType(String catKey,String enterpriseCode){
        SysTypeKey typeKey=sysTypeKeyDao.getByKey(catKey);
        Long typeId=UniqueIdUtil.genId();
        GlobalType globalType=new GlobalType();
        globalType.setTypeId(typeId);
        globalType.setParentId(typeKey.getTypeId());
        globalType.setEnterpriseCode(enterpriseCode);
        globalType.setNodeKey(GlobalType.CAT_DEFAULT);
        globalType.setTypeName(GlobalType.TYPE_NAME_DEFAULT);
        globalType.setCatKey(catKey);
        globalType.setNodePath(typeKey.getTypeId()+"."+typeId+".");
        globalType.setDepth(1);
        globalType.setSn(0L);
        globalType.setIsLeaf(0);
        globalTypeDao.add(globalType);
        return globalType;
    }

	/**
	 * 获取子分类id
	 * @param typeId
	 * @return
	 */
	public List<Long> subTypeIdByParent(Long typeId){
		GlobalType globalType=globalTypeDao.getById(typeId);
		List<GlobalType> globalTypeList = globalTypeDao.getByNodePath(globalType.getNodePath());
		List<Long> typeIds=new ArrayList<Long>();
		for (GlobalType typeItem:globalTypeList){
			typeIds.add(typeItem.getTypeId());
		}
		return typeIds;
	}
}

package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.*;
import com.suneee.oa.dao.system.ResourcesExtendDao;
import com.suneee.platform.dao.system.ResourcesDao;
import com.suneee.platform.dao.system.ResourcesUrlDao;
import com.suneee.platform.dao.system.RoleResourcesDao;
import com.suneee.platform.dao.system.SubSystemDao;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.xml.system.ResourcesXml;
import com.suneee.platform.xml.system.ResourcesXmlList;
import com.suneee.platform.xml.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;

/**
 * 对象功能:子系统资源 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-05 17:00:54
 */
@Service
public class ResourcesService extends BaseService<Resources>
{
	@Resource
	private ResourcesDao resourcesDao;
	@Resource
	private ResourcesUrlDao resourcesUrlDao;
	@Resource
	private SubSystemDao subSystemDao;
	@Resource
	private RoleResourcesDao roleResourcesDao;
	@Resource
	private ResourcesExtendDao resourcesExtendDao;
	
	
	public ResourcesService(){
	}
	
	@Override
	protected IEntityDao<Resources, Long> getEntityDao()
	{
		return resourcesDao;
	}
	
	/**
	 * 添加资源。
	 * @param resources
	 * @param aryName
	 * @param aryUrl
	 * @throws Exception
	 */
	public Long addRes(Resources resources, String[] aryName, String[] aryUrl) throws Exception{
		Long resId= UniqueIdUtil.genId();
		resources.setResId(resId);
		String path="";
		Long parentId=resources.getParentId();
		Resources parentRes=resourcesDao.getById(parentId);
		if(BeanUtils.isNotEmpty(parentRes)){
			if(StringUtil.isNotEmpty(parentRes.getPath())){
				path=parentRes.getPath()+":"+resId;
			}
		}else{
			path=resId.toString();
		}
		resources.setPath(path);
		resourcesDao.add(resources);
		
		if(BeanUtils.isEmpty(aryName)) return resId;
	
		for(int i=0;i<aryName.length;i++){
			String url=aryUrl[i];
			if(StringUtil.isEmpty(url)) continue;
			ResourcesUrl resouceUrl=new ResourcesUrl();
			resouceUrl.setResId(resId);
			resouceUrl.setResUrlId(UniqueIdUtil.genId());
			resouceUrl.setName(aryName[i]);
			resouceUrl.setUrl(url);
			resourcesUrlDao.add(resouceUrl);
		}
		return resId;
	}
	
	/**
	 * 更新资源。
	 * @param resources
	 * @param aryName
	 * @param aryUrl
	 * @throws Exception
	 */
	public void updRes(Resources resources,String[] aryName,String[] aryUrl) throws Exception{
		Long resId=resources.getResId();
		String path="";
		Long parentId=resources.getParentId();
		Resources parentRes=resourcesDao.getById(parentId);
		if(BeanUtils.isNotEmpty(parentRes)){
			if(StringUtil.isNotEmpty(parentRes.getPath())){
				path=parentRes.getPath()+":"+resId;
			}
		}else{
			path=resId.toString();
		}
		resources.setPath(path);
		resourcesDao.update(resources);
		//删除资源的url。
		resourcesUrlDao.delByResId(resId);
		
		if(BeanUtils.isEmpty(aryName)) return;
		
		for(int i=0;i<aryName.length;i++){
			String url=aryUrl[i];
			if(StringUtil.isEmpty(url))	continue;
			ResourcesUrl resouceUrl=new ResourcesUrl();
			resouceUrl.setResId(resId);
			resouceUrl.setResUrlId(UniqueIdUtil.genId());
			resouceUrl.setName(aryName[i]);
			resouceUrl.setUrl(url);
			resourcesUrlDao.add(resouceUrl);
		}
	}
	
	/**
	 * 根据系统id查询
	 * @param systemId	系统id
	 * @return
	 */
	public List<Resources> getBySystemId(long systemId, Short fromType){
		List<Resources> resourcesList= resourcesDao.getBySystemId(systemId, fromType);
		
		return resourcesList;
	}
	

	/**
	 * 根据系统id和父节点id获取资源节点。
	 * <pre>
	 * 1.根据父节点id获取资源获取到即返回。
	 * 2.如果获取不到则根据子系统相关数据构建资源根节点进行返回。
	 * </pre>
	 * @param parentId
	 * @return
	 */
	public Resources getParentResourcesByParentId(long systemId,long parentId) {
		Resources parent = resourcesDao.getById(parentId);
		if(parent!=null) return parent;
	
		SubSystem sys=subSystemDao.getById(systemId);
		
		parent = new Resources();
		parent.setResId(Resources.ROOT_ID);
		parent.setParentId(Resources.ROOT_PID);
		parent.setSn(0);
		parent.setSystemId(systemId);
		
		parent.setAlias(sys.getAlias());
		
		parent.setIsDisplayInMenu(Resources.IS_DISPLAY_IN_MENU_Y);
		parent.setIsFolder(Resources.IS_FOLDER_Y);
		parent.setIsOpen(Resources.IS_OPEN_Y);
		parent.setResName(sys.getSysName());
		
		return parent;
		
	}
	
	/**
	 * 根据资源id查询在列表中他所有的子节点。
	 * @param resId
	 * @param allRes
	 * @return
	 */
	private List<Resources> getChildsByResId(Long resId,List<Resources> allRes){
		List<Resources> rtnList=new ArrayList<Resources>();
		for(Iterator<Resources> it=allRes.iterator();it.hasNext();){
			Resources res=it.next();
			if(!res.getParentId().equals(resId)) continue;
			rtnList.add(res);
			recursiveChilds(res.getResId(),rtnList,allRes);
		}
		return rtnList;
	}
	
	/**
	 * 递归查找节点。
	 * @param resId
	 * @param rtnList
	 * @param allRes
	 */
	private void recursiveChilds(Long resId,List<Resources> rtnList,List<Resources> allRes){
		for(Iterator<Resources> it=allRes.iterator();it.hasNext();){
			Resources res=it.next();
			if(!res.getParentId().equals(resId)) continue;
			rtnList.add(res);
			recursiveChilds(res.getResId(),rtnList,allRes);
		}
	}
	

	/**
	 * 删除资源将删除其下所有的资源节点。
	 */
	public void delById(Long resId){
		//所属系统
		Resources res=resourcesDao.getById(resId);
		List<Resources> allRes=resourcesDao.getBySystemId(res.getSystemId(), Resources.FROMTYPE_SERVER);
		List<Resources> allChilds=getChildsByResId(resId,allRes);
		
		for(Iterator<Resources> it=allChilds.iterator();it.hasNext();){
			Resources resources=it.next();
			Long childId=resources.getResId();
			//删除关联的URL
			resourcesUrlDao.delByResId(childId);
			//删除url关联的角色
			roleResourcesDao.delByResId(childId);
			//删除资源自身
			resourcesDao.delById(childId);
		}
		
		//删除关联的URL
		resourcesUrlDao.delByResId(resId);
		//删除url关联的角色
		roleResourcesDao.delByResId(resId);
		//删除主键
		resourcesDao.delById(resId);
	}
	
	
	/**
	 * 获取系统的资源，并把某角色拥有的资源做一个选择标记。
	 * @param systemId		系统id。
	 * @param roleId		角色id。
	 * @param fromType 
	 * @return
	 */
	public List<Resources> getBySysRolResChecked(Long systemId,Long roleId, Short fromType){
		List<Resources>  resourcesList=resourcesDao.getBySystemId(systemId, fromType);
		List<RoleResources> roleResourcesList=roleResourcesDao.getBySysAndRole(systemId,roleId, fromType);
		
		Set<Long> set=new HashSet<Long> ();
		
		if(BeanUtils.isNotEmpty(roleResourcesList)){
			for(RoleResources rores:roleResourcesList){
				set.add(rores.getResId());
			}
		}
		
		if(BeanUtils.isNotEmpty(resourcesList)){
			for(Resources res:resourcesList){
				if(set.contains(res.getResId())){
					res.setChecked(Resources.IS_CHECKED_Y);
				}else{
					res.setChecked(Resources.IS_CHECKED_N);
				}
			}
		}
		return resourcesList;
	}
	
	/**
	 * 根据用户获取菜单数据。
	 * 
	 * @param sys	子系统
	 * @param user	用户
	 * @param fromType 资源来源
	 * @return
	 */
	public List<Resources> getSysMenu(SubSystem sys, SysUser user, Short fromType){
		Long systemId=sys.getSystemId();
		String rolealias = user.getRoles();
		if(StringUtil.isNotEmpty(rolealias)){
			String arrys[] = rolealias.split(",");
			rolealias = "";
			if(arrys.length>0){
				for (int i = 0; i < arrys.length; i++){
					rolealias +="'"+arrys[i]+"',";
				}
				rolealias = rolealias.substring(0, rolealias.length()-1);
			}
			
		}
		List<Resources> resourcesList=new ArrayList<Resources>();
		// 是否是超级管理员
		if(ContextUtil.isSuperAdmin()){
			resourcesList=resourcesDao.getSuperMenu(systemId, fromType);
		}else{
			if(StringUtil.isNotEmpty(rolealias)){
				resourcesList=resourcesDao.getNormMenuByAllRole(systemId,rolealias,fromType);
			}else{
				resourcesList=resourcesDao.getNormMenuByRole(systemId,user.getUserId(), fromType);
			}
		}
		
		short isLocal=sys.getIsLocal()==null?1:sys.getIsLocal().shortValue();
		//外地系统
		if(isLocal==SubSystem.isLocal_N){
			//前缀+外地址
			for(Resources res:resourcesList){
				res.setDefaultUrl(sys.getDefaultUrl()+res.getDefaultUrl());
			}
		}
		return resourcesList;
	}
	
	/** 
	 * 获取超级管理员资源
	 * @param systemId
	 * @param fromType
	 * @return
	 */
	public List<Resources> getSuperMenu(Long systemId, Short fromType){
		return resourcesDao.getSuperMenu(systemId, fromType);
	}
	
	/** 
	 * 获取普通用户资源
	 * @param systemId
	 * @param userId
	 * @param fromType
	 * @return
	 */
	public List<Resources> getNormMenuByRole(Long systemId, Long userId, Short fromType, String enterpriseCode){
		return resourcesDao.getNormMenuByRole(systemId, userId, fromType, enterpriseCode);
	}
	
	/**
	 * 判断别名在该系统中是否存在。
	 * @param systemId	系统id
	 * @param alias		系统别名
	 * @return
	 */
	public Integer isAliasExists(Resources resources){
		Long systemId=resources.getSystemId();
		String alias=resources.getAlias();
		return resourcesDao.isAliasExists(systemId, alias);
	}
	
	
	/**
	 * 判断别名是否存在。
	 * @param systemId
	 * @param resId
	 * @param alias
	 * @return
	 */
	public Integer isAliasExistsForUpd(Resources resources){
		Long systemId=resources.getSystemId();
		String alias=resources.getAlias();
		Long resId=resources.getResId();
		return resourcesDao.isAliasExistsForUpd(systemId, resId, alias);
	}
	
	/**
	 * 根据栏目更多路径获取相应的资源的功能模块
	 * 如果取得的资源不唯一则随意取一功能模块
	 * @param url  
	 * @return
	 */
	public Resources getByUrl(String url) {
		List<Resources>list=resourcesDao.getByUrl(url);
		if(list.size()!=0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Resources> getByParentId(Long id, Short fromType){
		return resourcesDao.getByParentId(id, fromType);
	}
	
	/**
	 * 对资源进树行拖动。
	 * @param sourceId		原节点。
	 * @param targetId		目标节点。
	 */
	public void move(Long sourceId,Long targetId){
		//成为子节点
		Resources source = resourcesDao.getById(sourceId);
		if(targetId==0){
			source.setParentId((long)0);
			source.setPath(sourceId.toString());
		}
		else {
			Resources target = resourcesDao.getById(targetId);
			//将拖动的节点父节点设置为目标节点的ID
			source.setParentId(target.getResId());
			//设置资源移动后的路径
			if(StringUtil.isNotEmpty(target.getPath())){
				source.setPath(target.getPath()+":"+sourceId);
			}else{
				source.setPath(sourceId.toString());
			}
		}
		resourcesDao.update(source);
	}
	
	/**
	 * 给资源的图标添加上下文的路径。
	 * @param list
	 * @param ctxPath
	 */
	public static void addIconCtxPath(List<Resources> list,String ctxPath){
		for(Iterator<Resources> it=list.iterator();it.hasNext();){
			Resources res=it.next();
			String icon=res.getIcon();
			if(StringUtil.isNotEmpty(icon)){
				res.setIcon(ctxPath+ icon);
			}
		}
	}

	/**
	 * 导出资源
	 * @param resId
	 * @return
	 * @throws Exception 
	 */
	public String exportXml(long resId,Map<String,Boolean> map) throws Exception {
		Resources resources=resourcesDao.getById(resId);
		ResourcesXmlList resXmlList=new ResourcesXmlList();
		ResourcesXml resourcesXml=new ResourcesXml();
		if(BeanUtils.isNotEmpty(resources)){
			List<ResourcesXml> resList=new ArrayList<ResourcesXml>();
			resourcesXml=getResourcesXml(resourcesXml,resources);
			resList.add(resourcesXml);
			resXmlList.setResourcesXmlList(resList);
		}
		return XmlBeanUtil.marshall(resXmlList, ResourcesXmlList.class);
	}
	
	/**
	 * 递归查找子资源(为导出资源)
	 * @param resXml
	 * @param res
	 * @return
	 */
	private ResourcesXml getResourcesXml(ResourcesXml resXml,Resources res){
		resXml.setResour(res);
		List<Resources> resList=getByParentId(res.getResId(), res.getFromType());
		if(BeanUtils.isNotEmpty(resList)){
			List<ResourcesXml> resourcesXmls=resXml.getResourcesList();
			for(Resources resource:resList){
				ResourcesXml  resourcesXml=new ResourcesXml();
				resourcesXml=getResourcesXml(resourcesXml, resource);
				resourcesXmls.add(resourcesXml);
			}
			resXml.setResourcesList(resourcesXmls);
		}
		return resXml;
	}

	/**
	 * 导入资源
	 * @param inputStream
	 * @param resId
	 * @param systemId
	 * @throws Exception 
	 */
	public void importXml(InputStream inputStream, long resId,long systemId) throws Exception {
		ResourcesXmlList resXmlList = getResourcesXmlList(inputStream);
		addResource(resXmlList.getResourcesXmlList().get(0),resId,systemId);
	}

	public void importXml(InputStream inputStream, Resources parentRes) throws Exception {
		ResourcesXmlList resXmlList = getResourcesXmlList(inputStream);
		addResource(resXmlList.getResourcesXmlList().get(0),parentRes);
	}

	private ResourcesXmlList getResourcesXmlList(InputStream inputStream) throws Exception {
		String xml= FileUtil.inputStream2String(inputStream);
		Document doc= Dom4jUtil.loadXml(xml);
		Element root = doc.getRootElement();
		XmlUtil.checkXmlFormat(root, "res", "resources");
		return (ResourcesXmlList) XmlBeanUtil.unmarshall(xml, ResourcesXmlList.class);
	}

	/**
	 * 解析xml递归插入资源
	 * @param resXml
	 * @param parentId
	 * @param systemId
	 */
	private void addResource(ResourcesXml resXml,long parentId,long systemId) {
		Resources res=resXml.getResour();
		res.setParentId(parentId);
		res.setSystemId(systemId);
		if (isExistAlias(res)){
			MessageUtil.addMsg(res.getResName()+"资源的资源别名"+res.getAlias()+"系统已存在，不可重复入库");
			return;
		}
		res.setResId(UniqueIdUtil.genId());
		this.add(res);
		List<ResourcesXml> resXmlList=resXml.getResourcesList();
		for(ResourcesXml resourcesXml:resXmlList){
			addResource(resourcesXml,res.getResId(),systemId);
		}
	}
	private void addResource(ResourcesXml resXml, Resources parentRes) {
		boolean isRoot=false;
		if (parentRes==null){
			parentRes=new Resources();
			parentRes.setResId(0L);
			parentRes.setPath("");
			parentRes.setSystemId(1L);
			isRoot=true;
		}
		Resources res=resXml.getResour();
		res.setParentId(parentRes.getResId());
		res.setSystemId(parentRes.getSystemId());
		if (isExistAlias(res)){
			MessageUtil.addMsg(res.getResName()+"资源的资源别名"+res.getAlias()+"系统已存在，不可重复入库");
			return;
		}
		res.setResId(UniqueIdUtil.genId());
		if (isRoot){
			res.setPath(res.getResId().toString());
		}else {
			res.setPath(parentRes.getPath()+":"+res.getResId());
		}
		this.add(res);
		List<ResourcesXml> resXmlList=resXml.getResourcesList();
		for(ResourcesXml resourcesXml:resXmlList){
			addResource(resourcesXml,res);
		}
	}

	/**
	 * 判断别名是否存在
	 * @param res
	 * @return
	 */
	private boolean isExistAlias(Resources res){
		Resources temp=resourcesExtendDao.getByAliasForCheck(null,res.getAlias(),res.getSystemId());
		if(temp!=null){
			return true;
		}
		return  false;
	}
	
	//更新sn
	public void updSn(Long resId, long sn) {
		resourcesDao.updSn(resId, sn);
		
	}

	/**
	 * 根据资源别名获取资源。
	 * @param systemId
	 * @param alias
	 * @return
	 */
	public Resources getByAlias(Long systemId,String alias){
		return resourcesDao.getByAlias(systemId, alias);
	}
	
	/**
	 * 根据父id和用户id获取下级tab资源。
	 * @param resId
	 * @param userId
	 * @return
	 * @deprecated
	 */
	public List<Resources> getByParentUserId(Long resId,Long userId){
		return resourcesDao.getByParentUserId(resId, userId);
	}
	
	/**
	 * 根据父id和用户id获取下级tab资源。
	 * @param resId
	 * @param userId
	 * @return
	 */
	public List<Resources> getNormMenuByAllRoleParentId(Long resId,String rolealias){
		if(StringUtil.isNotEmpty(rolealias)){
			String arrys[] = rolealias.split(",");
			rolealias = "";
			if(arrys.length>0){
				for (int i = 0; i < arrys.length; i++){
					rolealias +="'"+arrys[i]+"',";
				}
				rolealias = rolealias.substring(0, rolealias.length()-1);
			}
		}
		
		return resourcesDao.getNormMenuByAllRoleParentId(resId, rolealias);
	}
	
	
}

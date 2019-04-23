
package com.suneee.platform.service.system;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.platform.dao.system.ResourcesDao;
import com.suneee.platform.dao.system.SubSystemDao;
import com.suneee.platform.model.system.Resources;
import com.suneee.platform.model.system.SubSystem;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SystemConst;
import com.suneee.platform.xml.system.ResourcesXml;
import com.suneee.platform.xml.system.ResourcesXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.platform.dao.system.ResourcesDao;
import com.suneee.platform.dao.system.SubSystemDao;
import com.suneee.platform.model.system.Resources;
import com.suneee.platform.model.system.SubSystem;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SystemConst;
import com.suneee.platform.xml.system.ResourcesXml;
import com.suneee.platform.xml.system.ResourcesXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;

/**
 * 对象功能:子系统管理 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-11-21 12:22:06
 */
@Service
public class SubSystemService extends BaseService<SubSystem>
{
	
	@Resource
	private SubSystemDao subSystemDao;
	@Resource
	private ResourcesDao resourcesDao;
	
	public SubSystemService()
	{
	}
	
	@Override
	protected IEntityDao<SubSystem, Long> getEntityDao() {
		return subSystemDao;
	}
	
	
	public void setCurrentSystem( Long systemId,HttpServletRequest request, HttpServletResponse response){
		
		SubSystem subSystem=subSystemDao.getById(systemId);
		if(subSystem!=null){
			writeCurrentSystemCookie(String.valueOf(systemId),  request,  response);
			request.getSession().setAttribute(SubSystem.CURRENT_SYSTEM,subSystem);	
		}
		
		
	}
	public void writeCurrentSystemCookie(String systemId, HttpServletRequest request, HttpServletResponse response){
		if (CookieUitl.isExistByName(SubSystem.CURRENT_SYSTEM, request)) {
			CookieUitl.delCookie(SubSystem.CURRENT_SYSTEM, request, response);
		}
		int tokenValiditySeconds = 86400 * 14; // 14 days
		CookieUitl.addCookie(SubSystem.CURRENT_SYSTEM, systemId, tokenValiditySeconds, request, response);
	}
	/**
	 * 取得拥用的系统	
	 * @param user
	 * @return
	 */
	public List<SubSystem> getByUser(SysUser user){
		if(user.getAuthorities().contains(SystemConst.ROLE_GRANT_SUPER)){
			return getAll();
		}else{
			Collection<GrantedAuthority> roles= user.getAuthorities();
			List<SubSystem> sysList=new ArrayList<SubSystem>();
			if(BeanUtils.isEmpty(roles)){
				return sysList;
			}
			String roleNames="";
			for(GrantedAuthority auth:roles){
				if(roleNames.equals("")){
					roleNames+="'" + auth.getAuthority() +"'";
				}
				else{
					roleNames+=",'" + auth.getAuthority() +"'";
				}
			}
			return subSystemDao.getByRoles(roleNames);
		}
	}
	
	/**
	 * 取得本地子系统。
	 * 本地子系统的概念是系统在同一个应用当中。只不过按照功能划分成不同的系统。
	 * @return
	 */
	public List<SubSystem> getLocalSystem(){
		return subSystemDao.getLocalSystem();
	}
	
	/**
	 * 判断系统别名是否已存在。
	 * @param alias
	 * @return
	 */
	public Integer isAliasExist(String alias){
		return subSystemDao.isAliasExist(alias);
	}
	
	/**
	 * 更新时判断别名是否存在。
	 * @param alias		别名
	 * @param systemId	系统ID
	 * @return
	 */
	public Integer isAliasExistForUpd(String alias,Long systemId){
		return subSystemDao.isAliasExistForUpd(alias,systemId);
	}
	
	/**
	 * 获取已经激活的系统。
	 * @return
	 */
	public List<SubSystem> getActiveSystem(){
		return subSystemDao.getActiveSystem();
	}
	
	public SubSystem getByAlias(String systemName) {
		return subSystemDao.getByAlias(systemName);
	}
	/**
	 * 导出子系统资源Xml
	 * @param systemId
	 * @return
	 * @throws Exception
	 */
	public String exportXml(long systemId, Short fromType)throws Exception
	{
		ResourcesXmlList resourcesXmls=new ResourcesXmlList();
		ResourcesXml resourcesXml=new ResourcesXml();
		List<ResourcesXml> list=new ArrayList<ResourcesXml>();
		List<Resources> resources=resourcesDao.getBySystemId(systemId, fromType);
		if(BeanUtils.isNotEmpty(resources))
		{
			resourcesXml.setResList(resources);
			list.add(resourcesXml);
		}
		resourcesXmls.setResourcesXmlList(list);
		return XmlBeanUtil.marshall(resourcesXmls, ResourcesXmlList.class);
		
	}
	
	/**
	 * 导入子系统资源Xml
	 * @param inputStream
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream,Long systemId)throws Exception
	{
		Document doc = Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		// 验证格式是否正确
		XmlUtil.checkXmlFormat(root, "res", "resources");
		String xmlStr = root.asXML();
		ResourcesXmlList resourcesXmlList=(ResourcesXmlList)XmlBeanUtil.unmarshall(xmlStr, ResourcesXmlList.class);
		List<ResourcesXml> list=resourcesXmlList.getResourcesXmlList();
		for(ResourcesXml pesourcesXml:list)
		{
			// 导入表，并解析相关信息
			this.importResourcesXml(pesourcesXml,systemId);
		}
	}
	/**
	 * 导入时生成子系统资源
	 * @param pesourcesXml
	 * @throws Exception 
	 */
	private void importResourcesXml(ResourcesXml pesourcesXml,Long systemId) throws Exception 
	{
		List<Resources> list=pesourcesXml.getResList();
		if(BeanUtils.isEmpty(list))
		{
			throw new Exception();
		}
		Map<Long,String> beforeMap = new HashMap<Long,String>();
		Map<String,Long> afterAliasParentMap = new HashMap<String,Long>();
		Map<String,Long> afterAliasResMap = new HashMap<String,Long>();
		List<Resources> resourceList = list;
		for(Resources resource:resourceList)
		{
			beforeMap.put(resource.getResId(),resource.getAlias());
			resource.setResId(UniqueIdUtil.genId());
			resource.setSystemId(systemId);
			afterAliasResMap.put(resource.getAlias(), resource.getResId());
			afterAliasParentMap.put(resource.getAlias(), resource.getParentId());
		}
		for(Resources resource:resourceList)
		{
			//设置parentId
			if(resource.getParentId()!=0){
				resource.setParentId(afterAliasResMap.get(beforeMap.get(afterAliasParentMap.get(resource.getAlias()))));
			}
			resource.setAlias(resource.getAlias()+"_1");
			resourcesDao.add(resource);
		}
		SubSystem subSystem=subSystemDao.getById(systemId);
		MsgUtil.addMsg(MsgUtil.SUCCESS, "名称为"+subSystem.getSysName()+"的子系统资源导入成功！");
	}
}


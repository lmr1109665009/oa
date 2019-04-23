package com.suneee.platform.service.system;
import java.util.Date;

import javax.annotation.Resource;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.platform.dao.system.SysKnowPerRefDao;
import com.suneee.platform.model.system.SysKnowPerRef;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.platform.dao.system.SysKnowPerRefDao;
import com.suneee.platform.model.system.SysKnowPerRef;
import com.suneee.platform.model.system.SysUser;

/**
 *<pre>
 * 对象功能:权限关联主表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:dyg
 * 创建时间:2015-12-31 16:05:42
 *</pre>
 */
@Service
public class SysKnowPerRefService extends BaseService<SysKnowPerRef>
{
	@Resource
	private SysKnowPerRefDao dao;
	@Resource
	private SysKnowledgePerService sysKnowledgePerService;
	
	
	
	public SysKnowPerRefService()
	{
	}
	
	@Override
	protected IEntityDao<SysKnowPerRef, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 根据json字符串获取SysKnowPerRef对象
	 * @param json
	 * @return
	 */
	public SysKnowPerRef getSysKnowPerRef(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		SysKnowPerRef sysKnowPerRef = JSONObjectUtil.toBean(json, SysKnowPerRef.class);
		return sysKnowPerRef;
	}
	
	/**
	 * 保存 权限关联主表 信息
	 * @param sysKnowPerRef
	 * @param sysKnowObj 
	 * @return 
	 */
	public String save(SysKnowPerRef sysKnowPerRef, String sysKnowObj){
		Long id=sysKnowPerRef.getId();
		if(id==null || id==0L){
			id= UniqueIdUtil.genId();
			sysKnowPerRef.setId(id);
			sysKnowPerRef.setCreatetime(new Date());
			ISysUser user = new SysUser();
			user = ContextUtil.getCurrentUser();
			sysKnowPerRef.setCreatorid(user.getUserId());
			sysKnowPerRef.setCreator(user.getFullname());
			this.add(sysKnowPerRef);
			sysKnowledgePerService.save(id,sysKnowObj);//添加权限
			return "添加";
		}
		else{
			sysKnowledgePerService.save(id,sysKnowObj);//添加权限
			this.update(sysKnowPerRef);
			return "更新";
		}
	}
	
}

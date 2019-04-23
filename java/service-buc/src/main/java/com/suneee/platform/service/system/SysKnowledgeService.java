package com.suneee.platform.service.system;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.model.CurrentUser;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysKnowledgeDao;
import com.suneee.platform.model.system.SysKnowledge;
import com.suneee.platform.service.util.ServiceUtil;
import org.springframework.stereotype.Service;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.model.CurrentUser;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysKnowledgeDao;
import com.suneee.platform.model.system.SysKnowledge;
import com.suneee.platform.service.util.ServiceUtil;

/**
 *<pre>
 * 对象功能:知识库 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-28 10:15:59
 *</pre>
 */
@Service
public class SysKnowledgeService extends BaseService<SysKnowledge>
{
	@Resource
	private SysKnowledgeDao dao;
	@Resource
	private SysKnowledgePerService sysKnowledgePerService;
	@Resource
	private SysKnowledgeMarkService sysKnowledgeMarkService;
	@Resource
	private CurrentUserService currentUserService;
	
	public SysKnowledgeService()
	{
	}
	
	@Override
	protected IEntityDao<SysKnowledge, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 保存 知识库 信息
	 * @param sysKnowledge
	 * @param markStr 
	 * @return 
	 */
	public String save(SysKnowledge sysKnowledge, String markStr){
		Long id=sysKnowledge.getId();
		ISysUser sysUser = ContextUtil.getCurrentUser();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			sysKnowledge.setCreatorid(sysUser.getUserId());
			sysKnowledge.setCreator(sysUser.getFullname());
			sysKnowledge.setCreatetime(new Date());
			sysKnowledge.setUpdatetime(new Date());
			sysKnowledge.setId(id);
			this.add(sysKnowledge);
			sysKnowledgeMarkService.saveMarkStr(markStr,id);
			return "添加";
		}
		else{
			sysKnowledge.setUpdatorid(sysUser.getUserId());
			sysKnowledge.setUpdator(sysUser.getFullname());
			sysKnowledge.setUpdatetime(new Date());
			this.update(sysKnowledge);
			sysKnowledgeMarkService.saveMarkStr(markStr,id);
			return "更新";
		}
	}

	//修改所属分类
	public void updateType(Long typeId, List<Long> keyList) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("typeId", typeId);
		map.put("keyList", keyList);
		dao.update("updateType", map);
	}

	//根据typeId取
	public List<SysKnowledge> getByTypeId(QueryFilter queryFilter, Long typeId) {
		queryFilter.addFilter("typeId", typeId);
		return dao.getBySqlKey("getByTypeId", queryFilter);
	}

	public void delByType(Long typeId) {
		dao.delByType(typeId);
	}

	public List<SysKnowledge> getByMarkId(Long markId) {
		CurrentUser currentUser = ServiceUtil.getCurrentUser();
		Map<String, List<Long>> relationMap = currentUserService
				.getUserRelation(currentUser);
		boolean isSuperAdmin  = ContextUtil.isSuperAdmin();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("markId", markId);
		map.put("isSuperAdmin", isSuperAdmin);
		map.put("relationMap", relationMap);
		List<SysKnowledge> knowList = new ArrayList<SysKnowledge>();
		knowList =dao.getBySqlKey("getByMarkId", map);
		return knowList;
	}
}

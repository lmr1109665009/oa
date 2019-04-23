package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.model.CurrentUser;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysKnowledgePerDao;
import com.suneee.platform.model.system.SysKnowledgePer;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 *<pre>
 * 对象功能:权限 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:dyg
 * 创建时间:2015-07-28 10:23:07
 *</pre>
 */
@Service
public class SysKnowledgePerService extends BaseService<SysKnowledgePer>
{
	@Resource
	private SysKnowledgePerDao dao;
	@Resource
	private CurrentUserService currentUserService;
	
	
	
	public SysKnowledgePerService()
	{
	}
	
	@Override
	protected IEntityDao<SysKnowledgePer, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 根据typeid取权限list，不需要分页
	 * @param queryFilter
	 * @param typeid
	 * @return
	 */
	public List<SysKnowledgePer> getListByTypeId(Long typeId) {
		List<SysKnowledgePer> list = dao.getBySqlKey("getListByTypeId", typeId);
		return list;
	}
	
	public SysKnowledgePer getPerById(Long id) {
		return (SysKnowledgePer) dao.getOne("getPerById", id);
	}

	public List<SysKnowledgePer> getAllList(QueryFilter queryFilter) {
		return dao.getAllList(queryFilter);
	}
	

	/**
	 * 根据CurrentUser获取分类的权限Map<分类ID,权限字符串edit，add....>
	 * @param currentUser
	 * @return
	 */
	public Map<Long,String> getUserTypePer(CurrentUser currentUser){
		Map<Long,String> map = new HashMap<Long,String>();
		List<SysKnowledgePer> sysKnowledgePerList = getByUserRelation(currentUser);
		for(SysKnowledgePer sysKnowledgePer : sysKnowledgePerList){
			map.put(sysKnowledgePer.getTypeId(), sysKnowledgePer.getPermissionJson());
		}
		return map ;
	}
	/**
	 * 用户已经分配了组织权限，则根据用户的组织岗位关系来查询知识库权限表
	 * @param user
	 * @return
	 */
	public List<SysKnowledgePer> getByUserRelation(CurrentUser user) {
		Map<String, List<Long>> map = currentUserService
				.getUserRelation(user);
		List<SysKnowledgePer> sysKnowledgePerList = dao.getByUserIdFilter(map);
		List<SysKnowledgePer> newPerList  = mergerKnow(sysKnowledgePerList);
		return newPerList;
	}

	/**
	 * 如果用户没有分配组织岗位的情况，只根据user来查询知识库权限表
	 * @param userId
	 * @return
	 */
	public List<SysKnowledgePer> getByOnlyUser(Long userId) {
		List<Long> userList = new ArrayList<Long>();
		userList.add(userId);
		Map<String, List<Long>> map = new HashMap<String, List<Long>>();
		map.put("user", userList);
		List<SysKnowledgePer> sysKnowledgePerList = dao.getByUserIdFilter(map);
		List<SysKnowledgePer> newPerList  = mergerKnow(sysKnowledgePerList);
		return newPerList;
	}
	/**
	 * 合并相同分类的权限
	 * 如果同一个分类如果在不同的SysKnowledgePer对象分配了不同的权限，这个时候需要合并
	 * @param sysKnowledgePerList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<SysKnowledgePer> mergerKnow(List<SysKnowledgePer> sysKnowledgePerList) {
		Map<Long, SysKnowledgePer> map = new  HashMap<Long, SysKnowledgePer>();
		List<SysKnowledgePer> newPerList = new ArrayList<SysKnowledgePer>();
		for(SysKnowledgePer sysKnowPer:sysKnowledgePerList){
			if(map.containsKey(sysKnowPer.getTypeId())){
				SysKnowledgePer mapKnowPer = map.get(sysKnowPer.getTypeId());
				String mergerJson=mapKnowPer.getPermissionJson();
				String json=sysKnowPer.getPermissionJson();
				
				Set<String> set = new TreeSet<String>();
				
				if(StringUtil.isNotEmpty(mergerJson)){
					String[] aArrayPer=mergerJson.split(",");
					 set.addAll(Arrays.asList(aArrayPer));
				}
				if(StringUtil.isNotEmpty(json)){
					String[] bArrayPer=json.split(",");
					set.addAll(Arrays.asList(bArrayPer));
				}
				if(set.size()>0){
					String newPerJson = StringUtils.join(set.toArray(), ",");
					mapKnowPer.setPermissionJson(newPerJson);
				}
				
				map.put(sysKnowPer.getTypeId(), mapKnowPer);
				continue;
			}
			map.put(sysKnowPer.getTypeId(), sysKnowPer);
		}
		newPerList.addAll(map.values());
		return newPerList;
	}

	public void save(Long perRefId, String sysKnowObj) {
		dao.delByRefId(perRefId);//先删除旧数据
		JSONArray knowObjArray = JSONArray.fromObject(sysKnowObj);
		for (Object obj : knowObjArray) {
			SysKnowledgePer sysKnowledgePer = JSONObjectUtil.toBean(obj.toString(), SysKnowledgePer.class);
			Long newId = UniqueIdUtil.genId();
			sysKnowledgePer.setRefId(perRefId);
			sysKnowledgePer.setId(newId);
			this.add(sysKnowledgePer);
		}
	}

	public List<SysKnowledgePer> getByRefId(Long refId) {
		return dao.getBySqlKey("getByRefId", refId);
	}

	public void delByRefIds(Long[] lAryId) {
		for(Long refId : lAryId){
			dao.delBySqlKey("delByRefId", refId);
		}
	}

}

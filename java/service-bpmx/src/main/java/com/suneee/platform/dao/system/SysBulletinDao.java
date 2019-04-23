
package com.suneee.platform.dao.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.BaseDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.SysBulletin;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SysBulletinDao extends BaseDao<SysBulletin>
{
	@Resource 
	SysReadRecodeDao readRecodeDao;
	
	@Override
	public Class<?> getEntityClass()
	{
		return SysBulletin.class;
	}
	
	@Override
	public int delById(Long id) {
		readRecodeDao.deleteByParam(null, id, null);
		return super.delById(id);
	}
	
	public void delByColumnId(Long columnId) {
		readRecodeDao.deleteByParam(columnId, null, null);
		this.delBySqlKey("delByColumnId", columnId)  ;
	}

	public List<SysBulletin> getAllByAlias(String alias, PageBean pb) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", ContextUtil.getCurrentUserId());
		map.put("alias", alias);
		return getBySqlKey("getAllByAlias",map,pb);
	}

	public List<SysBulletin> getAllByAlias(QueryFilter queryFilter) {
		return getBySqlKey("getAllByAlias",queryFilter);
	}
	
	
	/***修改公告状态**/
	public void updateStatus(Long id, int status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("status", status);
		this.update("updateStatus",map);
	}

	public PageList<SysBulletin> getByColumnId(QueryFilter queryFilter){
		PageList<SysBulletin> bulletins= (PageList<SysBulletin>) this.getBySqlKey("getByColumnId",queryFilter);
		return bulletins;
		
	}

	public List<SysBulletin> getTopNews(int top) {
		return this.getBySqlKey("getTopNews", top);
	}

	/**
	 * 获取所有公告
	 * @param filter
	 * @return
	 */
	public List<SysBulletin> getAllBulletin(QueryFilter filter){
		return this.getBySqlKey("getAllBulletin",filter);
	}

	/**
	 * 获取当前用户创建的公告
	 * @param filter
	 * @return
	 */
	public List<SysBulletin> listCreateByMe(QueryFilter filter){
		return this.getBySqlKey("listCreateByMe",filter);
	}

	/**
	 * 添加数据到公告组织表
	 */
	public void addToBulletinOrg(Long bulletinId,String orgId){
		Map<String,Object> params = new HashMap<>();
		params.put("bulletinId",bulletinId);
		params.put("orgId",orgId);
		this.insert("addToBulletinOrg",params);
	}

	/**
	 * 通过公告id删除公告组织中间表的数据
	 */
	public void dellFromBulletinOrgByBulletin(Long bulletinId){
		Map<String,Long> params = new HashMap<>();
		params.put("bulletinId",bulletinId);
		this.delBySqlKey("dellFromBulletinOrgByBulletin",params);
	}

	public SysBulletin getByBulletinId(Long id){
		Map<String,Long> params = new HashMap<>();
		params.put("bulletinId",id);
		return (SysBulletin) this.getOne("getByBulletinId",params);
	}
}
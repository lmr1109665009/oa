package com.suneee.platform.service.oa;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.api.util.ContextUtil;
import javax.annotation.Resource;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.oa.OaLinkmanDao;
import com.suneee.platform.model.oa.OaLinkman;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.oa.OaLinkman;
import com.suneee.platform.dao.oa.OaLinkmanDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:联系人 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-14 09:13:57
 *</pre>
 */
@Service
public class OaLinkmanService extends BaseService<OaLinkman>
{
	@Resource
	private OaLinkmanDao dao;
	
	
	
	public OaLinkmanService()
	{
	}
	
	@Override
	protected IEntityDao<OaLinkman, Long> getEntityDao()
	{
		return dao;
	}
	
	
	
	
	
	
	/**
	 * 保存 联系人 信息
	 * @param oaLinkman
	 */
	public void save(OaLinkman oaLinkman){
		Long id=oaLinkman.getId();
		if(id==null || id==0){
			Long userid = ContextUtil.getCurrentUserId();
			id= UniqueIdUtil.genId();
			oaLinkman.setId(id);
			oaLinkman.setUserid(userid);
			this.add(oaLinkman);
		}
		else{
			this.update(oaLinkman);
		}
	}

	/**
	 * 根据用户id获取该用户下的所有联系人
	 * @param queryFilter
	 * @param userId
	 * @return 
	 * List&ltOaLinkman>
	 */
	public List<OaLinkman> getByUserId(QueryFilter queryFilter, Long userId) {
		queryFilter.addFilter("userId", userId);
		List<OaLinkman> list = dao.getBySqlKey("getByUserId", queryFilter);
		return list;
	}

	/**
	 * 根据用户id获取该用户下“启动”状态的联系人列表
	 * @param queryFilter
	 * @param userId
	 * @return 
	 * List&ltOaLinkman>
	 */
	public List<OaLinkman> getSelectorList(QueryFilter queryFilter, Long userId) {
		queryFilter.addFilter("userId", userId);
		List<OaLinkman> list = dao.getBySqlKey("getSelectorList", queryFilter);
		return list;
	}
	/**
	 * 检查最近联系人
	 * @param userId
	 * @return
	 */
	public List<OaLinkman> getMailLinkMan(Long userId) {
		List<OaLinkman> list = dao.getBySqlKey("getMailLinkMan", userId);
		return list;
	}

	/**
	 * 拿到联系人map
	 * @param userId
	 * @return
	 */
	public Map<String, String> getLinkManMap(Long userId) {
		List<OaLinkman> linkManList = this.getMailLinkMan(userId);
		Map<String,String> linkManMap = new HashMap<String,String>();
		for(OaLinkman linkMan:linkManList){
			linkManMap.put(linkMan.getName(),linkMan.getEmail());
		}
		return linkManMap;
	}
	
	/**
	 * 
	 * 判断联系人是否已经存在
	 * @param params
	 * @return 
	 * boolean
	 * @exception 
	 * @since  1.0.0
	 */
	public boolean isOaLinkExist(Long userId,String email){
		return dao.getByUserEmail(userId, email).size()>0;
	}

	
}

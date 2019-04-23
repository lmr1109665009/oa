package com.suneee.platform.dao.oa;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.oa.OaLinkman;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *<pre>
 * 对象功能:联系人 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-14 09:13:57
 *</pre>
 */
@Repository
public class OaLinkmanDao extends BaseDao<OaLinkman>
{
	@Override
	public Class<?> getEntityClass()
	{
		return OaLinkman.class;
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
	public List<OaLinkman> getByUserEmail(Long userId,String email){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("userId",userId);
		params.put("email",email);
	    return this.getBySqlKey("getByUserEmail", params);
	}
	
	
}
/**
 * @Title: EnterpriseinfoDao.java 
 * @Package com.suneee.oa.dao.system 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.dao.user;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.base.model.system.Enterpriseinfo;

/**
 * @ClassName: EnterpriseinfoDao 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-07 13:43:21 
 *
 */
@Repository
public class EnterpriseinfoDao extends UcpBaseDao<Enterpriseinfo>{

	/** (non-Javadoc)
	 * @Title: getEntityClass 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see com.suneee.core.db.GenericDao#getEntityClass()
	 */
	@Override
	public Class<Enterpriseinfo> getEntityClass() {
		// TODO Auto-generated method stub
		return Enterpriseinfo.class;
	}

	/** 
	 * 通过企业编码查询企业信息
	 * @param compCode
	 * @return
	 */
	public Enterpriseinfo getByCompCode(String compCode){
		return this.getUnique("getByCompCode", compCode);
	}
	
	/** 
	 * 获取用户的所属企业信息
	 * @param userId
	 * @return
	 */
	public List<Enterpriseinfo> getByUserId(Long userId){
		return this.getBySqlKey("getByUserId", userId);
	}
}

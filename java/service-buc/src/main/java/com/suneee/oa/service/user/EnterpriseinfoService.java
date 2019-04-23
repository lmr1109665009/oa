/**
 * @Title: EnterpriseinfoService.java 
 * @Package taskToStart 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.service.user;

import com.suneee.core.db.IEntityDao;
import com.suneee.oa.dao.user.EnterpriseinfoDao;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.UcpBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName: EnterpriseinfoService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-07 11:41:17 
 *
 */
@Service
public class EnterpriseinfoService extends UcpBaseService<Enterpriseinfo>{
	@Resource
	private EnterpriseinfoDao enterpriseinfoDao;

	/** (non-Javadoc)
	 * @Title: getEntityDao 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see com.suneee.core.service.GenericService#getEntityDao()
	 */
	@Override
	protected IEntityDao<Enterpriseinfo, Long> getEntityDao() {
		// TODO Auto-generated method stub
		return enterpriseinfoDao;
	}
	
	/** 
	 * 根据企业编码查询企业信息是否存在
	 * @param compCode
	 * @return
	 */
	public boolean isExist(String compCode){
		Enterpriseinfo enterpriseinfo = this.getByCompCode(compCode);
		if(enterpriseinfo == null){
			return false;
		}
		return true;
	}

	/** 
	 * 根据企业编码查询企业信息
	 * @param compCode 企业编码
	 * @return
	 */
	public Enterpriseinfo getByCompCode(String compCode){
		return enterpriseinfoDao.getByCompCode(compCode);
	}
	
	/** 
	 * 获取用户所属企业信息
	 * @param userId
	 * @return
	 */
	public List<Enterpriseinfo> getByUserId(Long userId){
		return enterpriseinfoDao.getByUserId(userId);
	}
	
	/** 
	 * 查询用户所属企业的企业编码与集团编码键值对：Map<企业编码，集团编码>
	 * @param userId 用户ID
	 * @return
	 */
	public Map<String, String> getCodeMapByUserId(Long userId){
		Map<String, String> codeMap = new HashMap<String, String>();
		List<Enterpriseinfo> enterpriseinfoList = this.getByUserId(userId);
		for(Enterpriseinfo enterpriseinfo : enterpriseinfoList){
			codeMap.put(enterpriseinfo.getComp_code(), enterpriseinfo.getGroupCode());
		}
		return codeMap;
	}
	
	/** 
	 * 查询用户所属企业的企业编码集合
	 * @param userId 用户ID
	 * @return
	 */
	public Set<String> getCodeSetByUserId(Long userId){
		Set<String> codeSet = new HashSet<String>();
		List<Enterpriseinfo> enterpriseinfoList = this.getByUserId(userId);
		for(Enterpriseinfo enterpriseinfo : enterpriseinfoList){
			codeSet.add(enterpriseinfo.getComp_code());
		}
		return codeSet;
	}
}

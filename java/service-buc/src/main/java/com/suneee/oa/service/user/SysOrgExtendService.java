package com.suneee.oa.service.user;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.oa.dao.user.SysOrgExtendDao;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.service.system.SysOrgExtService;
import com.suneee.ucp.base.util.HttpUtils;
import com.suneee.ucp.base.util.JsonUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织信息SysOrg扩展Service类
 * @author xiongxianyun
 *
 */
@Service
public class SysOrgExtendService extends UcpBaseService<SysOrg> {
	@Resource
	private SysOrgExtendDao sysOrgExtendDao;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private SysOrgExtService sysOrgExtService;
	
	@Override
	protected IEntityDao<SysOrg, Long> getEntityDao() {
		return this.sysOrgExtendDao;
	}

	/**
	 * 根据维度ID获取系统所有组织信息
	 * @param demId
	 * @return
	 */
	public List<SysOrg> getByDemId(Long demId) {
	     return this.sysOrgExtendDao.getByDemId(demId);
	}
	
	/**
	 * 根据维度ID删除组织信息
	 * @param demId
	 */
	public void delByDemId(Long demId) throws IOException {
		// 查询父节点为维度ID的组织
		List<SysOrg> orgList = sysOrgService.getOrgByOrgSupId(demId);
		// 删除组织
		for(SysOrg sysOrg : orgList){
			sysOrgService.delById(sysOrg.getOrgId());
		}
	}
	
	@Override
	public void delByIds(Long[] orgIds) throws IOException {
		if(orgIds == null || orgIds.length == 0){
			return;
		}
		for(Long orgId : orgIds){
			sysOrgService.delById(orgId);
		}
	}
	
	/**
	 * 根据组织ID获取组织详情
	 * @param orgId
	 * @return
	 */
	public SysOrg getByOrgId(Long orgId){
		return sysOrgExtendDao.getByOrgId(orgId);
	}
	
	/** 
	 * 根据查询条件获取组织信息列表(包含上级组织名称、维度名称，连表查询)
	 * @param filter
	 * @return
	 */
	public List<SysOrg> getByCondition(QueryFilter filter){
		return sysOrgExtendDao.getByCondition(filter);
	}
	
	/**
	 * 保存组织信息
	 * @param sysOrg
	 * @param isAdd
	 */
	public void save(SysOrg sysOrg, boolean isAdd) throws Exception{
		// 深度
		sysOrg.setDepth(sysOrg.getOrgPathname().split(Constants.SEPARATOR_BACK_SLANT).length - 2);
		// 组织代码
		String code = sysOrgExtService.generateCode(sysOrg.getOrgCode(), sysOrg.getOrgName(), sysOrg.getOrgId());
		sysOrg.setCode(code);
		
		if(isAdd){
			sysOrg.setCreatorId(ContextUtil.getCurrentUserId());
			sysOrgService.addOrg(sysOrg);
		} else {
			sysOrg.setUpdateId(ContextUtil.getCurrentUserId());
			sysOrgService.updOrg(sysOrg);
			// 更新子组织信息
			this.upSysOrgBySupId(sysOrg.getOrgId(), sysOrg.getPath(), sysOrg.getOrgPathname());
		}
	}
	
	/**
	 * 根据企业编码从权限中心获取企业信息
	 * @param enterpriseCode
	 * @return
	 * @throws IOException
	 */
	public List<Enterpriseinfo> getEnterpriseinfoList(String enterpriseCode) throws IOException{
		Map<String, Object> reqParam = new HashMap<String, Object>();
		reqParam.put("compCode", enterpriseCode);
		reqParam.put("type", 1);
		reqParam.put("auditFlag", "");
		JSONObject result = HttpUtils.sendToAuthorityCenter(Constants.AUTHORITY_CENTER_ENTERPRISEINFO_API, JSONObject.fromObject(reqParam).toString());
		List<Enterpriseinfo> enterpriseinfoList = new ArrayList<Enterpriseinfo>();
		if(result.getBoolean("success")){
			enterpriseinfoList = JsonUtils.jsonToList(result.getString("data"), Enterpriseinfo[].class);
		} else {
			throw new RuntimeException(result.getString("msg"));
		}
		return enterpriseinfoList;
	}
	
	/** 
	 * 组织排序
	 * @param orgIds
	 */
	public void sort(Long[] orgIds){
		if(orgIds == null){
			return;
		}
		int length = orgIds.length;
		for(int i = 0; i < length; i++){
			sysOrgService.updSn(orgIds[i], i + 1);
		}
	}
	
	/** 
	 * 获取组织基本信息列表
	 * @param queryFilter
	 * @return
	 */
	public List<SysOrg> getSimpleByCondition(QueryFilter queryFilter){
		return sysOrgExtendDao.getSimpleByCondition(queryFilter);
	}
	
	/**
	 * 根据父组织更新子组织信息
	 * @param orgSupId
	 * @param path
	 * @param supPathName
	 * @throws Exception
	 */
	private void upSysOrgBySupId(Long orgSupId, String path, String supPathName) throws Exception{
		//根据id查找子组织
		List<SysOrg> sysOrgs = sysOrgService.getOrgByOrgSupId(orgSupId);
		if (sysOrgs != null && sysOrgs.size() > 0) {
			SysOrg sysOrg = null;
			for (int i = 0; i < sysOrgs.size(); i++) {
				sysOrg = sysOrgs.get(i);
				String pathName= supPathName + Constants.SEPARATOR_BACK_SLANT + sysOrg.getOrgName();
				sysOrg.setOrgPathname(pathName);
				sysOrg.setPath(path + sysOrg.getOrgId() + Constants.SEPARATOR_PERIOD);
				sysOrg.setDepth(pathName.split(Constants.SEPARATOR_BACK_SLANT).length - 2);
				sysOrgService.updOrg(sysOrg);
				//递归遍历是否存在子组织，存在就继续修改，不存在就结束
				this.upSysOrgBySupId(sysOrg.getOrgId(), sysOrg.getPath(), pathName);
			}
		}
	}

	public List<SysOrg> getOrgListByUserId(Long userId, String enterpriseCode){
		return sysOrgExtendDao.getOrgListByUserId(userId, enterpriseCode);
	}
}
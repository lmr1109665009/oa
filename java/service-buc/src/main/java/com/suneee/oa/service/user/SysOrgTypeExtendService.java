package com.suneee.oa.service.user;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.SysOrgType;
import com.suneee.platform.service.system.SysOrgTypeService;
import com.suneee.oa.dao.user.SysOrgTypeExtendDao;
import com.suneee.ucp.base.service.UcpBaseService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 组织类型SysOrgType扩展Service类
 * @author xiongxianyun
 *
 */
@Service
public class SysOrgTypeExtendService extends UcpBaseService<SysOrgType> {

	@Resource
	private SysOrgTypeExtendDao sysOrgTypeExtendDao;

	@Resource
	private SysOrgTypeService sysOrgTypeService;

	@Override
	protected IEntityDao<SysOrgType, Long> getEntityDao() {
		return this.sysOrgTypeExtendDao;
	}

	/**
	 * 保存组织类型
	 * @param demId
	 * @param orgTypeStr
	 */
	public void saveOrgType(Long demId, String orgTypeStr) {
	    if ((demId == null) || (StringUtils.isBlank(orgTypeStr))) {
	      return;
	    }
	
	    // 根据维度ID获取已经存在的组织类型
	    List<SysOrgType> orgTypeList = this.sysOrgTypeService.getByDemId(demId.longValue());
	    // 已经存在的组织类型ID集合
	    List<Long> orgTypeIds = new ArrayList<Long>();
	    
	    // 将组织类型信息json字符串转换为JSON数组
	    JSONArray orgTypeJsonArr = JSONArray.fromObject(orgTypeStr);
	    SysOrgType orgType = null;
	    for (Object orgTypeJsonObj : orgTypeJsonArr) { 
	    	orgType = (SysOrgType)JSONObject.toBean((JSONObject)orgTypeJsonObj, SysOrgType.class);
	    	orgType.setDemId(demId);
	    	// 如果组织类型ID不存在，则新建组织
	    	if (orgType.getId() == null) {
	    		orgType.setId(Long.valueOf(UniqueIdUtil.genId()));
	    		
	    		this.sysOrgTypeExtendDao.add(orgType);
	    	}
	    	// 如果组织类型ID存在，则更新
	    	else {
	    		orgTypeIds.add(orgType.getId());
	    		this.sysOrgTypeExtendDao.update(orgType);
	    	}
	    }
	
	    // 删除再已存在的组织类型ID集合中不存在的组织类型
	    for (SysOrgType sysOrgType : orgTypeList){
	    	if (!(orgTypeIds.contains(sysOrgType.getId()))){
	    		this.sysOrgTypeExtendDao.delById(sysOrgType.getId());
	    	}
	    }
	}

	/**
	 * 根据维度ID删除组织类型
	 * @param demId
	 */
	public void delByDemId(Long demId)  {
		this.sysOrgTypeExtendDao.delByDemId(demId);
	}
}
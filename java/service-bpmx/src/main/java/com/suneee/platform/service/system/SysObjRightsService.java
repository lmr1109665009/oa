package com.suneee.platform.service.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysObjRightsDao;
import com.suneee.platform.model.system.SysObjRights;
import com.suneee.platform.service.util.ServiceUtil;
import net.sf.json.util.JSONUtils;
import net.sf.ezmorph.object.DateMorpher;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.StringUtil;
import net.sf.json.JSONObject;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.SysObjRights;
import com.suneee.platform.dao.system.SysObjRightsDao;
import com.suneee.core.service.BaseService;

/**
 * <pre>
 * 对象功能:对象权限表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-04-09 17:19:22
 * </pre>
 */
@Service
public class SysObjRightsService extends BaseService<SysObjRights> {
	@Resource
	private SysObjRightsDao dao;
	
	@Resource
	private CurrentUserService currentUserService;

	public SysObjRightsService() {
	}

	@Override
	protected IEntityDao<SysObjRights, Long> getEntityDao() {
		return dao;
	}

	public void deleteByObjTypeAndObjectId(String objType, String objectId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objType", objType);
		map.put("objectId", objectId);
		dao.delBySqlKey("deleteByObjTypeAndObjectId", map);
	}

	/**
	 * 保存 对象权限表 信息
	 * 
	 * @param sysObjRights
	 */
	public void save(SysObjRights sysObjRights) {
		Long id = sysObjRights.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			sysObjRights.setId(id);
			this.add(sysObjRights);
		} else {
			this.update(sysObjRights);
		}
	}

	public List<SysObjRights> getByObjTypeAndObjectId(String objType, String objectId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objType", objType);
		map.put("objectId", objectId);
		return dao.getBySqlKey("getObject", map);
	}
	
	public List<SysObjRights> getHashRights(String objType) {
		
		Map<String, List<Long>> userMap= currentUserService.getUserRelation(ServiceUtil.getCurrentUser());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objType", objType);
		
		map.put("userMap", userMap);
		
		return dao.getBySqlKey("getHashRights", map);
	}
	
	
}

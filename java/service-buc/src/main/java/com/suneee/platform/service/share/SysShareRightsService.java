package com.suneee.platform.service.share;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.share.SysShareRightsDao;
import com.suneee.platform.model.share.SysShareRights;
import com.suneee.platform.service.share.rights.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.share.SysShareRightsDao;
import com.suneee.platform.model.share.SysShareRights;
import com.suneee.platform.service.share.rights.DataTemplateVO;
import com.suneee.platform.service.share.rights.IShareRightsService;
import com.suneee.platform.service.share.rights.ShareRightsCalc;
import com.suneee.platform.service.share.rights.ShareRightsContainer;
import com.suneee.platform.service.share.rights.ShareRightsUtil;

@Service
public class SysShareRightsService extends BaseService<SysShareRights> {
	@Resource
	private SysShareRightsDao dao;
	@Resource
	private ShareRightsCalc shareRightsCalc;
	
	@Resource
    ShareRightsContainer shareRightsContainer;
	
	public SysShareRightsService() {
	}

	@Override
	protected IEntityDao<SysShareRights, Long> getEntityDao() {
		return dao;
	}

	public void save(SysShareRights sysShareRights) throws Exception {
		Long id = UniqueIdUtil.genId();
		sysShareRights.setId(id);
		shareRightsCalc.execute(sysShareRights);
		dao.add(sysShareRights);
	}

	@SuppressWarnings("unchecked")
	public void update(SysShareRights sysShareRights){
		shareRightsCalc.execute(sysShareRights);
		dao.update(sysShareRights);
	}

	public JSONArray getAllTypes(JSONArray allTypes){
		List<IShareRightsService> shareRightsList = shareRightsContainer.getShareRightsList();
		if(allTypes.size()==0){
			for(IShareRightsService shareRightsService: shareRightsList){
				JSONObject jo = new JSONObject();
				jo.put("type", shareRightsService.getShareType());
				jo.put("desc", shareRightsService.getShareDesc());
				allTypes.add(jo);
			}
		}
		return allTypes;
	}
	public void setTypeAndDesc(List<SysShareRights> list, JSONArray allTypes){
		for(SysShareRights s : list){
			JSONObject shareItem = JSONObject.fromObject(s.getShareItem());
			String type = shareItem.getString("type");
			for(Object o : allTypes){
				JSONObject typeJo = (JSONObject) o;
				if(typeJo.getString("type").equalsIgnoreCase(type)){
					s.setShareItem(typeJo.getString("desc"));
					break;
				}
			}
		}
	}
	public JSONObject getPermisionJo(String ruleId,String ids,short sourceType, String shareType){
		DataTemplateVO vo = shareRightsCalc.getDataTemplateVO(shareType, ruleId.toString());
		JSONObject jo1 = new JSONObject();
		jo1.put("display",JSONObject.fromObject(ShareRightsUtil.getPermissionByRule(vo, ids, sourceType,ShareRightsUtil.RIGHTS_TYPE_DISPLAY)));
		jo1.put("manager",JSONObject.fromObject(ShareRightsUtil.getPermissionByRule(vo, ids, sourceType,ShareRightsUtil.RIGHTS_TYPE_MANAGER)));
		jo1.put("filter",JSONObject.fromObject(ShareRightsUtil.getPermissionByRule(vo, ids, sourceType,ShareRightsUtil.RULE_TYPE_FILTER)));
		jo1.put("exports",JSONObject.fromObject(ShareRightsUtil.getPermissionByRule(vo, ids, sourceType,ShareRightsUtil.RULE_TYPE_EXPORT)));
		return jo1;
	}
}

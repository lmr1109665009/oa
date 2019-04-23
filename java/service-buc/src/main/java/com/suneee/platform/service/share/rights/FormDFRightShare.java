package com.suneee.platform.service.share.rights;

import com.suneee.core.util.BeanUtils;
import com.suneee.platform.dao.share.SysShareRightsDao;
import com.suneee.platform.model.share.SysShareRights;
import com.suneee.platform.service.bus.QueryUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Iterator;

public abstract class FormDFRightShare implements IShareRightsService{
	@Resource
	protected SysShareRightsDao sysShareRightsDao;

	protected SysShareRights sysShareRights;

	@Resource
	protected ShareRightsCalc shareRightsCalc;


	public abstract DataTemplateVO getDataTemplateVO(String ruleId);
	public abstract void updateDataTemplateVO(DataTemplateVO vo);
	@Override
	public void addShare(SysShareRights sysShareRights) {
		this.sysShareRights = sysShareRights;
		Long id=sysShareRights.getId();
		JSONObject shareItem = JSONObject.fromObject(sysShareRights.getShareItem());
		String[] ruleIds = shareItem.getString("ids").split(",");
		SysShareRights oldSysShareRights = sysShareRightsDao.getById(id);
		Boolean isAdd = BeanUtils.isEmpty(oldSysShareRights);
		if(!isAdd)
			shareRightsCalc.removeShareRights(oldSysShareRights);
		if(sysShareRights.getEnable() == SysShareRights.YES){
			for(String ruleId : ruleIds){
				JSONObject shareRule = shareItem.getJSONObject("v_"+ruleId);
				if(BeanUtils.isEmpty(shareRule)) continue;
				DataTemplateVO bpmDataTemplate = this.getDataTemplateVO(ruleId);

				JSONArray brDisplayJa = JSONArray.fromObject(bpmDataTemplate.getDisplayField());
				this.setDetail(shareRule.getJSONObject(ShareRightsUtil.RIGHTS_TYPE_DISPLAY_STR),brDisplayJa,ShareRightsUtil.RIGHTS_TYPE_DISPLAY);
				bpmDataTemplate.setDisplayField(brDisplayJa.toString());

				JSONArray brManagerJa = JSONArray.fromObject(bpmDataTemplate.getManageField());
				this.setDetail(shareRule.getJSONObject(ShareRightsUtil.RIGHTS_TYPE_MANAGER_STR),brManagerJa,ShareRightsUtil.RIGHTS_TYPE_MANAGER);
				bpmDataTemplate.setManageField(brManagerJa.toString());

				JSONArray brFilterJa = JSONArray.fromObject(bpmDataTemplate.getFilterField());
				this.setDetail(shareRule.getJSONObject(ShareRightsUtil.RULE_TYPE_FILTER_STR),brFilterJa,ShareRightsUtil.RULE_TYPE_FILTER);
				bpmDataTemplate.setFilterField(brFilterJa.toString());

				JSONArray brExportsJa = JSONArray.fromObject(bpmDataTemplate.getExportField());
				this.setDetail(shareRule.getJSONObject(ShareRightsUtil.RULE_TYPE_EXPORT_STR),brExportsJa.getJSONObject(0).getJSONArray("fields"),ShareRightsUtil.RULE_TYPE_EXPORT);
				bpmDataTemplate.setExportField(brExportsJa.toString());

				this.updateDataTemplateVO(bpmDataTemplate);
			}
		}
	}
	@Override
	public void removeShareRights(SysShareRights sysShareRights) {
		String[] ruleIds = JSONObject.fromObject(sysShareRights.getShareItem()).getString("ids").split(",");
		for (String ruleId : ruleIds) {
			DataTemplateVO bpmDataTemplate = this.getDataTemplateVO(ruleId);
			JSONArray brDisplayJa = JSONArray.fromObject(bpmDataTemplate.getDisplayField());
			this.removeShareDetail(brDisplayJa);
			bpmDataTemplate.setDisplayField(brDisplayJa.toString());

			JSONArray brManagerJa = JSONArray.fromObject(bpmDataTemplate.getManageField());
			this.removeShareDetail(brManagerJa);
			bpmDataTemplate.setManageField(brManagerJa.toString());

			JSONArray brFilterJa = JSONArray.fromObject(bpmDataTemplate.getFilterField());
			this.removeShareDetail(brFilterJa);
			bpmDataTemplate.setFilterField(brFilterJa.toString());

			JSONArray brExportsJa = JSONArray.fromObject(bpmDataTemplate.getExportField());
			this.removeShareDetail(brExportsJa.getJSONObject(0).getJSONArray("fields"));
			bpmDataTemplate.setExportField(brExportsJa.toString());

			this.updateDataTemplateVO(bpmDataTemplate);
		}
	}
	public void setDetail(JSONObject srDf,JSONArray brJa,short type){
		JSONObject target = JSONObject.fromObject(sysShareRights.getTarget());
		boolean isAll = sysShareRights.getIsAll() == SysShareRights.YES;
		Iterator<String> srDfKeys = srDf.keys();//共享的条目遍历
		while(srDfKeys.hasNext()){
			String srDfKey = srDfKeys.next();
			JSONObject srDfKeyJo = srDf.getJSONObject(srDfKey);
			if(isAll||srDfKeyJo.getBoolean("r")){
				Iterator<JSONObject> brIt = brJa.iterator(); //权限规则遍历器
				while(brIt.hasNext()){//遍历每一个权限规则
					JSONObject dfItNext = brIt.next();
					String v = QueryUtil.getRightsName(dfItNext);
					if(v.equals(srDfKey)){
						JSONObject newRights = new JSONObject();
						newRights.put("s", type);
						String t = "";
						if(target.containsKey("v"))
							t = target.getString("v");
						else if(target.containsKey("type"))
							t = target.getString("type");
						newRights.put("type", t );
						newRights.put("id", target.getString("ids"));
						newRights.put("name", target.getString("names"));
						newRights.put("srid",sysShareRights.getId());
						newRights.put("source", sysShareRights.getSource());
						JSONArray dfRightsArr = dfItNext.getJSONArray("right");
						dfRightsArr.add(newRights);
						//存数据
						break;
					}
				}
			}
		}
	}
	public void removeShareDetail(JSONArray brJa){
		if(brJa.toString().equalsIgnoreCase("[null]")) return;
		Iterator<JSONObject> brIt = brJa.iterator();
		while (brIt.hasNext()) {
			JSONObject dfItNext = brIt.next();
			JSONArray dfRightsArr = dfItNext.getJSONArray("right");
			JSONArray removeArr = new JSONArray();
			for (int i = 0; i < dfRightsArr.size(); i++) {
				JSONObject jsonObject = dfRightsArr.getJSONObject(i);
				if (jsonObject.containsKey("srid")) {
					String srid = jsonObject.getString("srid");
					if(StringUtils.isNotEmpty(srid)&&srid.equals(sysShareRights.getId().toString()))
						removeArr.add(jsonObject);
				}
			}
			dfRightsArr.removeAll(removeArr);
		}
	}
}
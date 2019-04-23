package com.suneee.weixin.impl;

import java.util.List;

import com.suneee.core.util.HttpUtil;
import com.suneee.weixin.api.IOrgService;
import com.suneee.weixin.api.WeixinConsts;
import com.suneee.weixin.model.WxOrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.util.HttpUtil;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.weixin.api.IOrgService;
import com.suneee.weixin.api.WeixinConsts;
import com.suneee.weixin.model.WxOrg;
import com.suneee.weixin.util.OrgConvertUtil;
@Component
public class WxOrgService implements IOrgService {
	
	
	protected Logger logger = LoggerFactory.getLogger(WxOrgService.class);
	

	@Override
	public void create(SysOrg org) {
		WxOrg wxorg = OrgConvertUtil.sysOrgToWxOrg(org);
		
		String resultJson = HttpUtil.sendHttpsRequest(WeixinConsts.getCreateOrgUrl(), wxorg.toString(), "POST");
		JSONObject result = JSONObject.parseObject(resultJson);
		String errcode = result.getString("errcode");
		if("0".equals(errcode))return;
		
		logger.debug(wxorg.toString());
		logger.debug(resultJson);
		// 表示已经存在 跳过
		if("60008".equals(errcode)){
			return;
		}
		throw new RuntimeException(org.getOrgName()+" 添加微信通讯录组织失败 ： "+result.getString("errmsg"));
	}

	@Override
	public void update(SysOrg org) {
		WxOrg wxorg = OrgConvertUtil.sysOrgToWxOrg(org);
		
		String resultJson = HttpUtil.sendHttpsRequest(WeixinConsts.getUpdateOrgUrl(), wxorg.toString(), "POST");
		JSONObject result = JSONObject.parseObject(resultJson);
		String errcode = result.getString("errcode");
		if("0".equals(errcode))return;
		
		throw new RuntimeException(org.getOrgName()+"添加微信通讯录组织失败 ： "+result.getString("errmsg"));
	}

	@Override
	public void delete(String orgId) {
		String url=WeixinConsts.getDeleteOrgUrl()+orgId;
		String resultJson = HttpUtil.sendHttpsRequest(url, "", "POST");
		JSONObject result = JSONObject.parseObject(resultJson);
		
		if("0".equals(result.getString("errcode"))) return;
		//尚未同步的组织
		if("60003".equals(result.getString("errcode"))){
			logger.error(orgId+"删除微信通讯录失败 ： "+result.getString("errmsg"));
			return;
		}
		
		throw new RuntimeException(orgId+"删除微信通讯录失败 ： "+result.getString("errmsg"));
	}

	@Override
	public void deleteAll(String orgIds) {
		String delUrl=WeixinConsts.getDeleteAllUserUrl();
		JSONObject users= new JSONObject();
		users.put("useridlist", orgIds.split(","));
		String resultJson = HttpUtil.sendHttpsRequest(delUrl,users.toJSONString(), "POST");
		JSONObject result = JSONObject.parseObject(resultJson);
		if("0".equals(result.getString("errcode"))) return;
		
		throw new RuntimeException("批量删除微信通讯录用户失败 ： "+result.getString("errmsg"));
		
	}
	
	@Override
	public void addAll(List<SysOrg> sysOrgList){
		for (SysOrg org : sysOrgList){
			this.create(org);
		}
	}

}

package com.suneee.weixin.impl;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.HttpUtil;
import com.suneee.weixin.api.IUserService;
import com.suneee.weixin.api.WeixinConsts;
import com.suneee.weixin.model.WxUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.HttpUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.weixin.api.IUserService;
import com.suneee.weixin.api.WeixinConsts;
import com.suneee.weixin.model.WxUser;
import com.suneee.weixin.util.OrgConvertUtil;
@Component
public class WxUserService implements IUserService {
	private final Log logger = LogFactory.getLog(WxUserService.class);
	@Resource 
	SysUserService sysUserService;

	@Override
	public void create(SysUser sysUser){
		SysUser sUser =(SysUser) sysUser;
		if(StringUtils.isEmpty(sUser.getWeixinid())) return;
		if(sUser.getHasSyncToWx() ==1 ) this.update(sysUser);
		
		WxUser user = OrgConvertUtil.sysUserToWxUser(sysUser);
		if(BeanUtils.isEmpty(user.getDepartment())) return;
		
		String resultJson = HttpUtil.sendHttpsRequest(WeixinConsts.getCreateUserUrl(), user.toString(), "POST");
		JSONObject result = JSONObject.parseObject(resultJson);
		
		String errcode = result.getString("errcode");
		if("0".equals(errcode)){
			this.invite(user.getUserid());
			sUser.setHasSyncToWx(1);
			sysUserService.update(sUser);
			return;
		}
		
		// 表示已经存在 
		if("60102".equals(errcode)){
			this.update(sysUser); 
			return;
		}
		throw new RuntimeException(user.getName()+"添加微信通讯录失败 ： "+result.getString("errmsg"));
		
	}

	@Override
	public void update(SysUser sysUser){
		WxUser user = OrgConvertUtil.sysUserToWxUser(sysUser);
		String url=WeixinConsts.getUpdateUserUrl();
		
		String resultJson = HttpUtil.sendHttpsRequest(url, user.toString(), "POST");
		JSONObject result = JSONObject.parseObject(resultJson);
		
		if("0".equals(result.getString("errcode"))){
			SysUser u =(SysUser) sysUser;
			if(u.getHasSyncToWx()==1) this.invite(user.getUserid());
			return;
		}
		
		throw new RuntimeException(user.getName()+"更新微信通讯录失败 ： "+result.getString("errmsg"));
	}

	@Override
	public void delete(String userId) {
		String url=WeixinConsts.getDeleteUserUrl()+userId;
		String resultJson = HttpUtil.sendHttpsRequest(url, "", "POST");
		JSONObject result = JSONObject.parseObject(resultJson);
		if("0".equals(result.getString("errcode"))) return;
		
		throw new RuntimeException(userId+"删除微信通讯录失败 ： "+result.getString("errmsg"));
	}
	
	@Override
	public void deleteAll(String userIds) {
		JSONObject users= new JSONObject();
		users.put("useridlist", userIds.split(","));
		String resultJson = HttpUtil.sendHttpsRequest(WeixinConsts.getDeleteAllUserUrl(),users.toJSONString(), "POST");
		JSONObject result = JSONObject.parseObject(resultJson);
		if("0".equals(result.getString("errcode"))) return;
		
		throw new RuntimeException("批量删除微信通讯录用户失败 ： "+result.getString("errmsg"));
	}
	
	@Override
	public void addAll(List<SysUser> sysUserList) {
		for (SysUser user : sysUserList){
			SysUser u =(SysUser) user;
			if(StringUtils.isEmpty(u.getWeixinid()) || u.getHasSyncToWx() ==1) 
				continue;
			
			this.create(user);
		}
	}
	
	public void invite(String wxUserId){
		JSONObject inviteData= new JSONObject();
		inviteData.put("userid", wxUserId);
		inviteData.put("invite_tips", PropertyUtil.getByAlias("wx.invite_tips","宏天流程业务平台邀请您关注！"));
		String resultJson = HttpUtil.sendHttpsRequest(WeixinConsts.getInviteUserUrl(),inviteData.toJSONString(), "POST");
		JSONObject result = JSONObject.parseObject(resultJson);
		if("0".equals(result.getString("errcode"))) return;
		logger.error("微信邀请失败！"+result.getString("errmsg"));
	}

}

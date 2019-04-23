package com.suneee.weixin.util;

import java.util.ArrayList;
import java.util.List;

import com.suneee.core.util.AppUtil;
import com.suneee.platform.model.system.Position;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.PositionService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.weixin.model.WxOrg;
import com.suneee.weixin.model.WxUser;
import com.suneee.core.util.AppUtil;
import com.suneee.weixin.model.WxOrg;
import com.suneee.weixin.model.WxUser;

public class OrgConvertUtil {
	
	public static WxUser sysUserToWxUser(SysUser sysUser){
		WxUser user = new WxUser();
		user.setWeixinid((String)sysUser.getWeixinid());
		user.setEmail((String)sysUser.getEmail());
		user.setGender((String)sysUser.getSex());
		user.setMobile((String)sysUser.getMobile());
		user.setName(sysUser.getFullname());
		user.setUserid(sysUser.getAccount());
		
		//获取组织岗位
		SysOrgService orgService = AppUtil.getBean(SysOrgService.class);
		PositionService positionService =AppUtil.getBean(PositionService.class);
		
		List<SysOrg> orgs = orgService.getOrgsByUserId(sysUser.getUserId());
		List<Position> postList = positionService.getByUserId(sysUser.getUserId());
		
		String [] department = new String[orgs.size()];
		for (int i = 0; i < orgs.size(); i++) {
			department[i] =orgs.get(i).getCode();
		}
		user.setDepartment(department);
		
		String postStr = "";
		for (Position post : postList) {
			if(!postStr.equals(""))postStr+="/";
			postStr+=post.getPosName();
		}
		user.setPosition(postStr);
		
		return user;
	}
	
	
	public static WxOrg sysOrgToWxOrg(SysOrg org){
		SysOrg sysOrg = (SysOrg)org;
		WxOrg wxorg = new WxOrg();
		wxorg.setId(org.getCode());
		wxorg.setParentid(sysOrg.getSupCode());
		wxorg.setName(org.getOrgName());
		return wxorg;
	}
	
	public static List<WxUser> sysUsersToWxUsers(List<SysUser> users){
		List<WxUser> wxUserList = new ArrayList<WxUser>();
		for (SysUser sysUser : users) {
			wxUserList.add(OrgConvertUtil.sysUserToWxUser(sysUser));
		}
		
		return wxUserList;
	}
	
	
	
	
	
}


package com.suneee.platform.service.share.rights.impl;

import com.suneee.platform.model.share.SysShareRights;
import com.suneee.platform.service.share.rights.DataTemplateVO;
import com.suneee.platform.service.share.rights.IShareRightsService;

/**
 * @author as xianggang
 * 
 */
public class RoleDFRightsShare implements IShareRightsService {

	@Override
	public void addShare(SysShareRights sysShareRights) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getShareType() {
		// TODO Auto-generated method stub
		return "roleDF";
	}

	@Override
	public String getShareDesc() {
		// TODO Auto-generated method stub
		return "菜单数据权限";
	}

	@Override
	public DataTemplateVO getDataObject(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeShareRights(SysShareRights sysShareRights) {
		// TODO Auto-generated method stub
		
	}}

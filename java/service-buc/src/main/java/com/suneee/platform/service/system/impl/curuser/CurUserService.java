package com.suneee.platform.service.system.impl.curuser;

import com.suneee.core.model.CurrentUser;
import com.suneee.platform.service.system.ICurUserService;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户授权
 *
 */
public class CurUserService implements ICurUserService {

	@Override
	public List<Long> getByCurUser(CurrentUser currentUser) {
		List<Long> list=new ArrayList<Long>();
		list.add(currentUser.getUserId());
		return list;
	}

	@Override
	public String getKey() {
		return "user";
	}

	@Override
	public String getTitle() {
		return "用户授权";
	}

	
	
}

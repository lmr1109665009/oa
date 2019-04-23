package com.suneee.platform.service.system.impl.curuser;

import java.util.List;

import com.suneee.core.model.CurrentUser;
import com.suneee.platform.service.system.ICurUserService;
import com.suneee.core.model.CurrentUser;
import com.suneee.platform.service.system.ICurUserService;

/**
 * 所有人
 *
 */
public class AllUserService  implements ICurUserService {

	@Override
	public List<Long> getByCurUser(CurrentUser currentUser) {
		return null;
	}

	@Override
	public String getKey() {
		return "all";
	}

	@Override
	public String getTitle() {
		return "所有人";
	}

	
}

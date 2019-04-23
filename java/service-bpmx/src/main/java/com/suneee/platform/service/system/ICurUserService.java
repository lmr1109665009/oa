package com.suneee.platform.service.system;

import com.suneee.core.model.CurrentUser;

import java.util.List;

public interface ICurUserService {
	
	List<Long> getByCurUser(CurrentUser currentUser);

	String getKey();
	
	String getTitle();
	

}

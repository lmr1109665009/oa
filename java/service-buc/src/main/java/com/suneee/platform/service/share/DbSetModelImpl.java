package com.suneee.platform.service.share;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IDbSetModel;
import com.suneee.core.model.BaseModel;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IDbSetModel;
import com.suneee.core.model.BaseModel;

public class DbSetModelImpl implements IDbSetModel {

	@Override
	public void setAdd(BaseModel model) {
		ISysUser user= ContextUtil.getCurrentUser();
		if(user!=null){
			model.setCreateBy(user.getUserId());
		}
		
	}

	@Override
	public void setUpd(BaseModel model) {
		ISysUser user=ContextUtil.getCurrentUser();
		if(user!=null){
			model.setUpdateBy(user.getUserId());
		}
	}

}

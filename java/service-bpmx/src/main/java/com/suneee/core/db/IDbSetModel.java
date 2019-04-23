package com.suneee.core.db;

import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;

/**
 * 设置数据接口。
 * @author ray
 */
public interface IDbSetModel {
	/**
	 * 在添加设置model
	 * @param model
	 */
	void setAdd(BaseModel model);
	
	/**
	 * 在更新时更新model。
	 * @param model
	 */
	void setUpd(BaseModel model);
}

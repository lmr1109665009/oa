package com.suneee.core.api.system;

import com.suneee.core.api.system.model.ISysDataSource;


public interface ISysDataSourceService {

	/**
	 * 根据别名获取数据源。
	 * @param alias
	 * @return
	 */
	ISysDataSource getByAlias(String alias);
	
}

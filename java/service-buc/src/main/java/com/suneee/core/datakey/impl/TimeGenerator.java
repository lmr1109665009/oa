package com.suneee.core.datakey.impl;

import com.suneee.core.datakey.IKeyGenerator;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.datakey.IKeyGenerator;
import com.suneee.core.util.UniqueIdUtil;

/**
 * 时间序列产生器。
 * @author ray
 *
 */
public class TimeGenerator implements IKeyGenerator {

	@Override
	public Object nextId() throws Exception {
		// TODO Auto-generated method stub
		return UniqueIdUtil.genId();
	}

	@Override
	public void setAlias(String alias) {
	}

}

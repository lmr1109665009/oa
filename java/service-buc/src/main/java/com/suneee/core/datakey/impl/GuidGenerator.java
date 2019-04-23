package com.suneee.core.datakey.impl;

import com.suneee.core.datakey.IKeyGenerator;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.datakey.IKeyGenerator;
import com.suneee.core.util.UniqueIdUtil;

/**
 * guid产生ID。
 * @author zhangyg
 *
 */
public class GuidGenerator implements IKeyGenerator {

	@Override
	public Object nextId() throws Exception {
		return UniqueIdUtil.getGuid();
	}

	@Override
	public void setAlias(String alias) {
	}

}

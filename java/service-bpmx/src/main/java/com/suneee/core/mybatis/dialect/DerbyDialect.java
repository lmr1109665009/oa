package com.suneee.core.mybatis.dialect;

import com.suneee.core.mybatis.Dialect;

public class DerbyDialect extends Dialect
{
	public boolean supportsLimit()
	{
		return false;
	}

	public boolean supportsLimitOffset()
	{
		return false;
	}

	public String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder)
	{
		throw new UnsupportedOperationException("paged queries not supported");
	}
}

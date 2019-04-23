<#assign package=table.variable.package>
<#assign class=table.variable.class>
<#assign classVar=table.variable.classVar>

package com.hotent.${system}.dao.${package};

import java.util.List;
import org.springframework.stereotype.Repository;

import com.hotent.core.db.BaseDao;
<#if table.isMain!=1>
import com.hotent.core.util.UniqueIdUtil;
import com.hotent.core.util.BeanUtils;
import com.hotent.core.web.query.QueryFilter;
</#if>

<#if flowKey?exists>
import com.hotent.core.db.WfBaseDao;
<#else>
import com.hotent.core.db.BaseDao;
</#if>

import com.hotent.${system}.model.${package}.${class};

@Repository
public class ${class}Dao extends <#if flowKey?exists>WfBaseDao<#else>BaseDao</#if><${class}>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ${class}.class;
	}

	<#if table.isMain!=1>
	/**
	 * 根据外键获取${table.tableDesc}列表
	 * @param refId
	 * @return
	 */
	public List<${class}> getByMainId(Long refId) {
		return this.getBySqlKey("get${class}List", refId);
	}
	
	/**
	 * 根据外键删除${table.tableDesc}
	 * @param refId
	 * @return
	 */
	public void delByMainId(Long refId) {
		this.delBySqlKey("delByMainId", refId);
	}
	</#if>	
}
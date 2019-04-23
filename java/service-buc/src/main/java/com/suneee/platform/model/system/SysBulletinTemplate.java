package com.suneee.platform.model.system;

import java.util.ArrayList;
import java.util.List;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import com.suneee.core.model.BaseModel;

/**
 * 对象功能:公告表 Model对象
 */
public class SysBulletinTemplate extends BaseModel {
	// 主键
	protected Long id;
	/**
	 * 别名
	 */
	protected String alias;
	/**
	 * 名称
	 */
	protected String name;
	
	/**
	 * 模板
	 */
	protected String template;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
}
	
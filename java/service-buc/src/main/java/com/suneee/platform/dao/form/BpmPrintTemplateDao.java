package com.suneee.platform.dao.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.form.BpmPrintTemplate;
/**
 *<pre>
 * 对象功能:表打 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-12-31 10:01:30
 *</pre>
 */
@Repository
public class BpmPrintTemplateDao extends BaseDao<BpmPrintTemplate>
{
	@Override
	public Class<?> getEntityClass()
	{
		return BpmPrintTemplate.class;
	}

	public int getCountByFormKey(String formKey) {
		
		return (Integer)this.getOne("getCountByFormKey", formKey);
	}

	public void updateIsNotDefault(Long formKey) {
		this.update("updateIsNotDefault", formKey);
	}

	public BpmPrintTemplate getDefaultByFormKey(String formKey) {
		return this.getUnique("getDefaultByFormKey", formKey);
	}

	public void delByFormKey(Long formKey) {
		this.delBySqlKey("delByFormKey", formKey);
	}

	public List<BpmPrintTemplate> getByFormKey(Long formKey) {
		return getBySqlKey("getByFormKey", formKey);
	}
	
	public BpmPrintTemplate getByFormKeyAndAlias(String formKey, String alias) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("formKey", formKey);
		params.put("alias", alias);
		return this.getUnique("getByFormKeyAndAlias", params);
	}
	
	public List<BpmPrintTemplate> getAllByFormKeyAndFlowFinish(String formKey) {
		return getBySqlKey("getAllByFormKeyAndFlowFinish", formKey);
	}

	public List<BpmPrintTemplate> getAllByFormKeyAndForm(String formKey) {
		return getBySqlKey("getAllByFormKeyAndForm", formKey);
	}

	public boolean isExistAlias(String alias, Long id) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("alias", alias);
		params.put("id", id);
    	return (Integer)this.getOne("isExistAlias", params) > 0;
	}

}
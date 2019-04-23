package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.FormDefTree;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:form_def_tree Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-05-12 14:46:20
 *</pre>
 */
@Repository
public class FormDefTreeDao extends BaseDao<FormDefTree>
{
	@Override
	public Class<?> getEntityClass()
	{
		return FormDefTree.class;
	}
	
	/**
	 * 根据formKey获取FormDefTree对象。
	 * @param formKey
	 * @return
	 */
	public FormDefTree getByFormKey(String formKey){
		FormDefTree defTree=this.getUnique("getByFormKey", formKey);
		return defTree;
	}
	
	public void delByFormDefKey(String formKey){
		this.delBySqlKey("delByFormDefKey", formKey);
	}
	
}
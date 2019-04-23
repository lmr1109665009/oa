package com.suneee.platform.service.form;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.form.SubTableSortDao;
import com.suneee.platform.model.form.SubTableSort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *<pre>
 * 对象功能:bpm_subtable_sort Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-03-12 13:56:01
 *</pre>
 */
@Service
public class SubTableSortService extends BaseService<SubTableSort>
{
	@Resource
	private SubTableSortDao dao;
	
	
	
	public SubTableSortService()
	{
	}
	
	@Override
	protected IEntityDao<SubTableSort, Long> getEntityDao()
	{
		return dao;
	}

	public List<SubTableSort> getByActDefKeyAndTableName(String actDefKey,
                                                         List<String> tableName) {
		
		return dao.getByActDefKeyAndTableName(actDefKey,tableName);
	}

	public void addList(String actDefKey,List<SubTableSort> subTableSortList) {
		dao.delByActDefKey(actDefKey);	// 先删除，后保存
		for (SubTableSort subTableSort : subTableSortList) {
			dao.add(subTableSort);
		}
		
	}

	public SubTableSort getByAKeyAndTName(String actDefKey, String tableName) {
		return dao.getByAKeyAndTName(actDefKey,tableName);
	}
	
}

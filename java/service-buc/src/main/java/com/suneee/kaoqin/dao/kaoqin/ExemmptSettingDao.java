package com.suneee.kaoqin.dao.kaoqin;

import com.suneee.core.db.BaseDao;
import com.suneee.kaoqin.model.kaoqin.ExemmptSetting;
import org.springframework.stereotype.Repository;
/**
 *<pre>
 * 对象功能:免签设置 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-02 10:06:05
 *</pre>
 */
@Repository
public class ExemmptSettingDao extends BaseDao<ExemmptSetting>
{
	@Override
	public Class<?> getEntityClass()
	{
		return ExemmptSetting.class;
	}

	public ExemmptSetting getByTargetId(Long targetId){
		return (ExemmptSetting)this.getOne("getByTargetId", targetId);
	}
	
}
package com.suneee.platform.dao.ats;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.ats.AtsAttenceGroupDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *<pre>
 * 对象功能:考勤组明细 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-26 10:07:59
 *</pre>
 */
@Repository
public class AtsAttenceGroupDetailDao extends BaseDao<AtsAttenceGroupDetail>
{
	@Override
	public Class<?> getEntityClass()
	{
		return AtsAttenceGroupDetail.class;
	}

	public List<AtsAttenceGroupDetail> getByGroupId(Long groupId) {
		return this.getBySqlKey("getByGroupId", groupId);
	}

	public void delByGroupId(Long groupId) {
		this.delBySqlKey("delByGroupId", groupId);
	}

	public void delByFileId(Long fileId) {
		this.delBySqlKey("delByFileId", fileId);
	}
	
}
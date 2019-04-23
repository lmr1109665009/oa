package com.suneee.eas.oa.dao.system;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.system.SysFile;

/**
 * 文件管理
 * @user 子华
 * @created 2018/7/31
 */
@Repository
public class SysFileDao extends BaseDao<SysFile> {
	public List<SysFile> getByIds(List<Long> fileIds){
		return this.getSqlSessionTemplate().selectList(getNamespace() + ".getByIds", fileIds);
	}

}

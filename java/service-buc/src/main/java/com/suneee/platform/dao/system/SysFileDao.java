/**
 * 对象功能:附件 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-11-26 18:19:16
 */
package com.suneee.platform.dao.system;

import com.suneee.core.db.BaseDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.model.system.SysFile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SysFileDao extends BaseDao<SysFile> {
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return SysFile.class;
	}
	
	public List<SysFile> getFileAttch(QueryFilter fileter){
		return getBySqlKey("getAllPersonalFile", fileter);
	}



	/**
	 * 根据文件路径查询其下的附件信息。
	 * @param filePath		文件路径
	 * @return
	 */
	public List<SysFile> getByFilePath(String filePath){
		Map<String,String> params=new HashMap<String, String>();
		params.put("filePath", filePath+"%");
		return this.getBySqlKey("getByFilePath", params);
	}


}
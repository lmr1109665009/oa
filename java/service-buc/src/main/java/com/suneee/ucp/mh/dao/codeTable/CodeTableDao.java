package com.suneee.ucp.mh.dao.codeTable;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.ucp.base.dao.UcpBaseDao;
import com.suneee.ucp.mh.model.codeTable.CodeTable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public class CodeTableDao extends UcpBaseDao<CodeTable>{

	@Override
	public Class<?> getEntityClass() {
		// TODO Auto-generated method stub
		return CodeTable.class;
	}

	public boolean isExist(String itemId, String itemValue) {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("itemId", itemId);
		params.put("itemValue", itemValue);
		List<CodeTable> list = this.getBySqlKey("isExist", params);
		if(list.size()>0){
			return true;
		}else{
		return false;
		}
	}
	
	public List<CodeTable> getByCondition(Map<String, Object> params){
		return this.getBySqlKey("getByCondition", params);
	}

	public List<CodeTable> getByItemId(String itemId, String settingType) {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("itemId", itemId);
		params.put("settingType", settingType);
		return this.getBySqlKey("getByItemId", params);
	}

	public List<CodeTable> getTypeList(QueryFilter filter) {
		// TODO Auto-generated method stub
		return this.getBySqlKey("getTypeList", filter);
	}

	public List<CodeTable> getOtherList(QueryFilter filter) {
		// TODO Auto-generated method stub
		return this.getBySqlKey("getOtherList", filter);
	}


	/**
	 * 获取总记录数
	 * */
	public int getTypeListCount(QueryFilter filter){
		int count = (int) this.getOne("getTypeListCount", filter);
		return count;
	}




	/**
	 * 获取总记录数
	 *
	 * */
	public int getOtherListCount(QueryFilter filter){
		int count = (int) this.getOne("getOtherListCount", filter);
		return count;
	}



}

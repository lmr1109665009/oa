package com.suneee.eas.oa.dao.conference;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.suneee.eas.common.dao.BaseDao;
import com.suneee.eas.oa.model.conference.AuthorityInfo;
import org.springframework.stereotype.Repository;

/**
 * @Description: AuthorityInfoDao
 * @Author: kaize
 * @Date: 2018/8/2 15:51
 */
@Repository
public class AuthorityInfoDao extends BaseDao<AuthorityInfo> {
	/** 
	 * 批量保存授权信息
	 * @param list
	 * @return
	 */
	public int batchSave(List<AuthorityInfo> list){
		return getSqlSessionTemplate().insert(getNamespace()+".batchSave",list);
	}
	
	/** 
	 * 删除指定权限类型下的指定
	 * @param params
	 * @return
	 */
	public int deleteBy(Map<String, Object> params){
		return getSqlSessionTemplate().delete(getNamespace()+".deleteBy",params);
	}
}

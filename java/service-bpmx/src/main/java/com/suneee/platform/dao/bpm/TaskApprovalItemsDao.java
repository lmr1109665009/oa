/**
 * 对象功能:常用语管理 Dao类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:cjj
 * 创建时间:2012-03-16 10:53:20
 */
package com.suneee.platform.dao.bpm;

import com.suneee.core.db.BaseDao;
import com.suneee.platform.model.bpm.TaskApprovalItems;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TaskApprovalItemsDao extends BaseDao<TaskApprovalItems> {
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return TaskApprovalItems.class;
	}


	/**
	 * 取流程常用语
	 *
	 * @param defKey
	 * @param TypeId
	 * @param curUserId
	 * @return
	 */
	public List<TaskApprovalItems> getApprovalByDefKeyAndTypeId(String defKey,
																Long TypeId,Long curUserId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("defKey", defKey);
		map.put("typeId", TypeId);
		map.put("curUserId", curUserId);
		return this.getBySqlKey("getApprovalByDefKeyAndTypeId", map);
	}


	public List<TaskApprovalItems> getByUserAndAdmin(Long currUserId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("currUserId", currUserId);
		return this.getBySqlKey("getByUserAndAdmin", map);
	}


	//本人的，系统全局的，和流程下的常用语
	public List<TaskApprovalItems> getByDefKeyAndUserAndSys(String defKey, Long curUserId) {
		Map<String, Object>map =new HashMap<String, Object>();
		map.put("defKey", defKey);
		map.put("curUserId", curUserId);
		return this.getBySqlKey("getByDefKeyAndUserAndSys", map);
	}


	public List<TaskApprovalItems> getByType(Short type) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("type", type);
		return this.getBySqlKey("getByType", map);
	}

	public void updSn(Long itemId, long sn) {
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("itemId", itemId);
		map.put("sn", sn);
		this.update("updSn", map);
	}

	public List<TaskApprovalItems> getByTypeAndUserId(Long userId) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("userId", userId);
		return this.getBySqlKey("getByTypeAndUserId", map);
	}




	/**
	 * 根据常用语类型和常用语查询常用语数量
	 * @param expression 常用语
	 * @return
	 */

	public boolean getByTypeCount(String expression,short type){
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("expression", expression);
		map.put("type", type);
		Integer count= (Integer) this.getOne("getByTypeCount", map);
		return count>=1;
	}

	/**
	 * 个人常用语允许其他人添加相同的常用语但不允许自己添加相同常用语
	 * @param expression 常用语
	 * @return
	 */

	public boolean getByTypeAndUserIdCount(String expression,Long userId){
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("expression", expression);
		map.put("userId", userId);
		Integer count= (Integer) this.getOne("getByTypeAndUserIdCount", map);
		return count>=1;
	}

	/**
	 * 个人常用语允许其他人添加相同的常用语但不允许自己添加相同常用语
	 * @param expression 常用语
	 * @return
	 */

	public boolean getBydefKeyCount(String expression,short type,String defKey){
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("expression", expression);
		map.put("type", type);
		map.put("defKey", defKey);
		Integer count= (Integer) this.getOne("getBydefKeyCount", map);
		return count>=1;
	}
}
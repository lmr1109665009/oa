package com.suneee.platform.service.bpm;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.bpm.TaskApprovalItemsDao;
import com.suneee.platform.model.bpm.TaskApprovalItems;
import com.suneee.platform.service.system.GlobalTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 对象功能:常用语管理 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:cjj
 * 创建时间:2012-03-16 10:53:20
 */
@Service
public class TaskApprovalItemsService extends BaseService<TaskApprovalItems>
{
	@Resource
	private TaskApprovalItemsDao dao;
	@Resource
	private GlobalTypeService globalTypeService;

	public TaskApprovalItemsService()
	{
	}

	@Override
	protected IEntityDao<TaskApprovalItems, Long> getEntityDao()
	{
		return dao;
	}

	/**
	 *
	 * @param itemId:常用语id
	 * @param exp
	 * @param type
	 * @param typeIdArr
	 * @param defKeyArr
	 * @param curUserId
	 * @throws Exception
	 */
	public void updateTaskApproval(Long itemId,String exp, Short type, String typeIdArr,
								   String  defKeyArr, Long curUserId) throws Exception{
		String[] expressions=exp.split("\n");
		if (type==4) {
			for (String expression:expressions) {
				if (StringUtil.isNotEmpty(expression)) {
					TaskApprovalItems taItem = null;
					taItem = new TaskApprovalItems();
					taItem.setItemId(itemId);
					taItem.setType(type);
					taItem.setExpression(expression);
					taItem.setUserId(curUserId);
					dao.update(taItem);
				}
			}
		}else if (type==1) {
			for (String expression:expressions) {
				if (StringUtil.isNotEmpty(expression)) {
					TaskApprovalItems taItem = null;
					taItem = new TaskApprovalItems();
					taItem.setItemId(itemId);
					taItem.setType(type);
					taItem.setExpression(expression);
//					taItem.setUserId(curUserId);
					dao.update(taItem);
				}

			}
		}else if (type==2) {
			String[] typeIds=typeIdArr.split(",");
			for (String typeId:typeIds) {
				for (String expression:expressions) {
					if (StringUtil.isNotEmpty(expression)) {
						TaskApprovalItems taItem = null;
						taItem = new TaskApprovalItems();
						taItem.setItemId(itemId);
						taItem.setTypeId(Long.parseLong(typeId));
						taItem.setType(type);
						taItem.setExpression(expression);
//						taItem.setUserId(curUserId);
						dao.update(taItem);
					}
				}
			}
		}else if (type==3) {
				for (String expression : expressions) {
					if (StringUtil.isNotEmpty(expression)) {
						TaskApprovalItems taItem = null;
						taItem = new TaskApprovalItems();
						taItem.setItemId(itemId);
						taItem.setDefKey(defKeyArr);
						taItem.setType(type);
						taItem.setExpression(expression);
//						taItem.setUserId(curUserId);
						dao.update(taItem);
					}
				}
			}
	}

	/**
	 * 添加常用语
	 * @param exp
	 * @throws Exception
	 */
	public void addTaskApproval(String exp, Short type, String typeIdArr,
								String  defKeyArr, Long curUserId) throws Exception
	{
		String[] expressions=exp.split("\n");
		if (type==1) {
			for (String expression:expressions) {
				if (StringUtil.isNotEmpty(expression)) {
					TaskApprovalItems taItem = null;
					taItem = new TaskApprovalItems();
					taItem.setItemId(UniqueIdUtil.genId());
					taItem.setType(type);
					taItem.setExpression(expression);
//					taItem.setUserId(curUserId);
					dao.add(taItem);
				}

			}
		}else if (type==4) {
			for (String expression:expressions) {
				if (StringUtil.isNotEmpty(expression)) {
					TaskApprovalItems taItem = null;
					taItem = new TaskApprovalItems();
					taItem.setItemId(UniqueIdUtil.genId());
					taItem.setType(type);
					taItem.setExpression(expression);
					taItem.setUserId(curUserId);
					dao.add(taItem);
				}

			}
		}else if (type==2) {
			String[] typeIds=typeIdArr.split(",");
			for (String typeId:typeIds) {
				for (String expression:expressions) {
					if (StringUtil.isNotEmpty(expression)) {
						TaskApprovalItems taItem = null;
						taItem = new TaskApprovalItems();
						taItem.setItemId(UniqueIdUtil.genId());
						taItem.setTypeId(Long.parseLong(typeId));
						taItem.setType(type);
						taItem.setExpression(expression);
//						taItem.setUserId(curUserId);
						dao.add(taItem);
					}
				}
			}
		}else if (type==3) {
				for (String expression:expressions) {
					if (StringUtil.isNotEmpty(expression)) {
						TaskApprovalItems taItem = null;
						taItem = new TaskApprovalItems();
						taItem.setItemId(UniqueIdUtil.genId());
						taItem.setDefKey(defKeyArr);
						taItem.setType(type);
						taItem.setExpression(expression);
//						taItem.setUserId(curUserId);
						dao.add(taItem);
					}
				}
			}
	}

	/**
	 * 取流程常用语。
	 * @param defKey	流程定义Key。
	 * @param typeId	流程分类Id。
	 * @return
	 */
	public List<String> getApprovalByDefKeyAndTypeId(String defKey,Long typeId){
		List<String> taskAppItemsList = new ArrayList<String>();
		Long curUserId= ContextUtil.getCurrentUserId();
		//先获取本人的，系统全局的，和流程下的常用语
		List<TaskApprovalItems> taskAppItem1=dao.getByDefKeyAndUserAndSys(defKey,curUserId);
		//获取分类为3的所有的常用语
		List<TaskApprovalItems> taskAppItem2=dao.getByType(TaskApprovalItems.TYPE_FLOWTYPE);

		if (BeanUtils.isNotEmpty(taskAppItem1)) {
			for(TaskApprovalItems taskAppItem:taskAppItem1){
				taskAppItemsList.add(taskAppItem.getExpression());
			}
		}
		if (BeanUtils.isNotEmpty(taskAppItem2)) {
			if(BeanUtils.isEmpty(typeId))return taskAppItemsList;
			//获取分类的父路径
			String typeIdPath=globalTypeService.getById(typeId).getNodePath();
			String[] typeIds=typeIdPath.split("\\.");
			for (int i=1; i< typeIds.length ;i++) {
				for (TaskApprovalItems taskAppItem:taskAppItem2) {
					if ((taskAppItem.getTypeId().toString()).equals(typeIds[i])) {
						taskAppItemsList.add(taskAppItem.getExpression());
					}
				}
			}
		}
		//去除重复的元素
		this.removeDuplicate(taskAppItemsList);
		return taskAppItemsList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void removeDuplicate (List<?> list){
		HashSet h = new HashSet(list);
		list.clear();
		list.addAll(h);
	}

	public List<TaskApprovalItems> getByUserAndAdmin(Long currUserId) {
		List<TaskApprovalItems> taskAppItems = dao.getByUserAndAdmin(currUserId);
		return taskAppItems;
	}

	public List<TaskApprovalItems> getByType( Short type){
		return dao.getByType(type);
	}


	//更新sn

	public void sort(Long[] lAryId){
		if(lAryId == null){
			return;
		}
		int length = lAryId.length;
		for(int i = 0; i < length; i++){
			dao.updSn(lAryId[i], i + 1);
		}
	}

	/** 全局常用语只能存在一个常用语
	 *
	 *
	 */
	public boolean getByTypeCount(String expression,short type){

		boolean count =dao.getByTypeCount(expression,type);
		return count;
	}

	public List<TaskApprovalItems> getByTypeAndUserId( Long userId){
		return dao.getByTypeAndUserId(userId);
	}

	/** 个人常用语允许其他人添加相同的常用语但不允许自己添加相同常用语
	 *
	 *
	 */
	public boolean getByTypeAndUserIdCount(String expression,Long userId){

		boolean count =dao.getByTypeAndUserIdCount(expression,userId);
		return count;
	}

	/** 特定常用语不同流程允许添加相同常用语
	 *
	 *
	 */
	public boolean getBydefKeyCount(String expression,short type,String defKey){

		boolean count =dao.getBydefKeyCount(expression,type,defKey);
		return count;
	}
}

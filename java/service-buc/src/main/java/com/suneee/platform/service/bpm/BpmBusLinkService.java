package com.suneee.platform.service.bpm;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.bpm.BpmBusLinkDao;
import com.suneee.platform.model.bpm.BpmBusLink;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 *<pre>
 * 对象功能:业务数据关联表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2013-08-21 16:51:49
 *</pre>
 */
@Service
public class BpmBusLinkService extends BaseService<BpmBusLink>
{
	@Resource
	private BpmBusLinkDao dao;
	
	
	
	public BpmBusLinkService()
	{
	}
	
	@Override
	protected IEntityDao<BpmBusLink, Long> getEntityDao()
	{
		return dao;
	}
	
	public BpmBusLink getByPk(Long pk){
		return dao.getByPk(pk);
	}
	
	public BpmBusLink getByPkStr(String pk){
		return dao.getByPkStr(pk);
	}
	
	
//	public void updInstByRunId(Long runId,Long actInstId){
//		dao.updInstByRunId(runId,actInstId);
//	}
	
	public void add(String pk, ProcessRun processRun, boolean isNumber, String tableName){
		
		SysUser curUser=(SysUser) ContextUtil.getCurrentUser();
		SysOrg curOrg=(SysOrg) ContextUtil.getCurrentOrg();
		Long id= UniqueIdUtil.genId();
		BpmBusLink busLink=new BpmBusLink();
		busLink.setBusId(id);
	
		if(isNumber){
			busLink.setBusPk(Long.parseLong(pk));
		}
		else{
			busLink.setBusPkstr(pk);
		}
		
		//加入表明 (分区字段)
		if(StringUtil.isNotEmpty(tableName)){
			createTablePartition(tableName);
			busLink.setBusFormTable(tableName);
		}else {
			busLink.setBusFormTable(BpmBusLink.TABLE_UNCREATED);
		}
		
		busLink.setBusCreatorId(curUser.getUserId());
		busLink.setBusCreator(curUser.getFullname());
		busLink.setBusCreatetime(new Date());
		if(BeanUtils.isNotEmpty(curOrg)){
			busLink.setBusOrgName(curOrg.getOrgName());
			busLink.setBusOrgId(curOrg.getOrgId());
		}
		busLink.setBusUpdid(curUser.getUserId());
		busLink.setBusUpd(curUser.getFullname());
		busLink.setBusUpdtime(new Date());
		
		if(processRun!=null){
			busLink.setBusDefId(processRun.getDefId());
			busLink.setBusFlowRunid(processRun.getRunId());
			busLink.setBusStatus(BpmBusLink.BUS_STATUS_RUNNING);
			
//			if(StringUtil.isNotEmpty(processRun.getActInstId())){
//				busLink.setBusProcInstId(new Long( processRun.getActInstId()));
//			}
			
			
		}
		else{
			busLink.setBusStatus(BpmBusLink.BUS_STATUS_BUSINESS);
		}
		dao.add(busLink);
	}

	
	/**
	 * 添加业务数据。
	 * @param pk
	 * @param defId
	 * @param flowRunId
	 * @param bpmFormTable
	 */
	public void add(String pk,ProcessRun processRun, BpmFormTable bpmFormTable){
		boolean isNumber= BpmFormTable.PKTYPE_NUMBER.equals(bpmFormTable.getKeyDataType());
		add(pk, processRun, isNumber, bpmFormTable.getTableName());
	}
	
	/**
	 * 更新业务关联数据。
	 * @param pk
	 * @param bpmformtable
	 */
	public void updBusLink(String pk,ProcessRun processRun, BpmFormTable bpmformtable){
		SysUser curUser=(SysUser) ContextUtil.getCurrentUser();
		BpmBusLink link=null;
		if(bpmformtable.isExtTable()){
			if (BpmFormTable.PKTYPE_NUMBER.equals(bpmformtable.getKeyDataType())){
				link=dao.getByPk(new Long(pk));
			}
			else{
				link=dao.getByPkStr(pk);
			}
		}
		else{
			link=dao.getByPk(new Long(pk));
		}
		if(BeanUtils.isNotEmpty(link)){
			if(processRun==null){
				link.setBusStatus(BpmBusLink.BUS_STATUS_BUSINESS);
			}
			else{
				link.setBusStatus(BpmBusLink.BUS_STATUS_RUNNING);
			}
			link.setBusUpdid(curUser.getUserId());
			link.setBusUpd(curUser.getFullname());
			link.setBusUpdtime(new Date());
			dao.update(link);
		}
		
	}
	
	/**
	 * 删除关联数据记录。
	 * @param pk
	 * @param bpmformtable
	 */
	public void delBusLink(String pk,BpmFormTable bpmformtable){
		if(bpmformtable.isExtTable()){
			if (BpmFormTable.PKTYPE_NUMBER.equals(bpmformtable.getKeyDataType())){
				dao.delByPk(new Long(pk));
			}
			else{
				dao.delByPkStr(pk);
			}
		}
		else{
			dao.delByPk(new Long(pk));
		}
		
	}
	
	/**
	 * 手动添加业务数据，在不调用系统自带的保存方法下 ，需要关联业务数据，所添加的数据才能在在线表单中显示
	 * @param pk
	 * @param defId
	 * @param runId
	 */
	public void addBusLinkWithManual(Long pk,Long defId, Long runId){
		SysUser curUser=(SysUser) ContextUtil.getCurrentUser();
		SysOrg curOrg=(SysOrg) ContextUtil.getCurrentOrg();
		Long id=UniqueIdUtil.genId();
		BpmBusLink busLink=new BpmBusLink();
		busLink.setBusId(id);
		busLink.setBusPk(pk);
		
		busLink.setBusCreatorId(curUser.getUserId());
		busLink.setBusCreator(curUser.getFullname());
		busLink.setBusCreatetime(new Date());
		if(BeanUtils.isNotEmpty(curOrg)){
			busLink.setBusOrgName(curOrg.getOrgName());
			busLink.setBusOrgId(curOrg.getOrgId());
		}
		busLink.setBusUpdid(curUser.getUserId());
		busLink.setBusUpd(curUser.getFullname());
		busLink.setBusUpdtime(new Date());
		
		busLink.setBusDefId(defId);
		busLink.setBusFlowRunid(runId);
		dao.add(busLink);
	}
	
	/**
	 * 手动更新业务数据，在不调用系统自带的保存方法下 ，需要关联业务数据，所添加的数据才能在在线表单中显示
	 * @param pk
	 */
	public void updBusLinkWithManual(Long pk){
		SysUser curUser=(SysUser) ContextUtil.getCurrentUser();
		BpmBusLink link=null;
		link=dao.getByPk(pk);
		if(BeanUtils.isNotEmpty(link)){
			link.setBusUpdid(curUser.getUserId());
			link.setBusUpd(curUser.getFullname());
			link.setBusUpdtime(new Date());
			dao.update(link);
		}
		
	}
	
	private void createTablePartition(String tableName) {
		dao.createTablePartition(tableName);
	}
	
	/**
	 * @param pk
	 */
	public boolean checkBusExist(String businessKey){
		return dao.checkBusExist(businessKey);
	}
	

	 
}

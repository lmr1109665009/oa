package com.suneee.platform.service.bpm;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.page.PageBean;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.dao.bpm.ExecutionDao;
import com.suneee.platform.dao.bpm.ProcessRunDao;
import com.suneee.platform.dao.bpm.TaskOpinionDao;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.platform.webservice.model.BpmFinishTask;
import freemarker.template.TemplateException;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 对象功能:流程任务审批意见 Service类 
 * 开发公司:广州宏天软件有限公司 
 * 开发人员:csx 
 * 创建时间:2012-01-11 16:06:11
 */
@Service
public class TaskOpinionService extends BaseService<TaskOpinion> {
	@Resource
	private TaskOpinionDao dao;

	@Resource
	private ExecutionDao executionDao;
	@Resource
	private BpmService bpmService;
	
	@Resource
	private SysUserService sysUserService;
	@Resource
	private TaskUserService taskUserService;
	
	@Resource
	private ProcessRunDao processRunDao;
	
	
	protected static  Logger logger = LoggerFactory.getLogger(TaskOpinionService.class);

	public TaskOpinionService() {
	}

	@Override
	protected IEntityDao<TaskOpinion, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 取得对应该任务的执行
	 * 
	 * @param taskId
	 * @return
	 */
	public TaskOpinion getByTaskId(Long taskId) {
		return dao.getByTaskId(taskId);
	}

	/**
	 * 取得某个任务的所有审批意见 按时间排序
	 * 
	 * @param actInstId
	 * @return
	 */
	public List<TaskOpinion> getByActInstId(String actInstId,boolean isAsc) {
		//取得顶级的流程实例ID
		String supInstId=getTopInstId(actInstId);
		
		List<String> instList=new ArrayList<String>();
		instList.add(supInstId);
		//递归往下查询
		getChildInst(supInstId,instList);
				
		return dao.getByActInstId(instList,isAsc);
	}
	/**
	 * 取得某个任务的所有审批意见 
	 * 
	 * @param actInstId
	 * @return
	 */
	public List<TaskOpinion> getByActInstId(String actInstId) {
		return getByActInstId( actInstId,true);
	}
	
	/**
	 * 根据流程实例ID获取流程实例列表包括子流程实例。
	 * @param actInstId
	 * @return 
	 * Set&lt;String>
	 */
	public Set<String> getInstListByInstId(String actInstId){
		List<TaskOpinion> opinions= getByActInstId(actInstId);
		Set<String> instSet=new HashSet<String>();
		for(TaskOpinion opinion:opinions){
			if(StringUtil.isNotEmpty(opinion.getActInstId())){
				instSet.add(opinion.getActInstId());
			}
		}
		return instSet;
	}
	
	
	/**
	 * 根据runId获取流程意见列表。
	 * @param runId
	 * @return
	 */
	public List<TaskOpinion> getByRunId(Long runId) {
		List<String> actInstIdList=getProcessRunList(runId);
		List<TaskOpinion> list=new ArrayList<TaskOpinion>();
		
		int len=actInstIdList.size();
		for(int i=len-1;i>=0;i--){
			String instId=actInstIdList.get(i);
			List<TaskOpinion> tmplist=getByActInstId(instId);
			list.addAll(tmplist);
		}
		return list;
	}
	
	/**
	 * 根据runId获取流程实例ID。
	 * @param runId
	 * @return
	 */
	private List<String> getProcessRunList(Long runId){
		ProcessRun processRun= getTopProcessRun(runId);
		String tmpActInstId= processRun.getActInstId();
		
		List<String> list=new ArrayList<String>();
		Long relRunId=processRun.getRelRunId();
		list.add(tmpActInstId);
		String actDefId=processRun.getActDefId();
		while(BeanUtils.isNotIncZeroEmpty(relRunId) ){
			ProcessRun tmpRun=processRunDao.getByHistoryId(relRunId);
			if(!actDefId.equals(tmpRun.getActDefId())){
				break;
			}
			list.add(tmpRun.getActInstId());
			relRunId=tmpRun.getRelRunId();
			actDefId=tmpRun.getActDefId();
		}
		
		return list;
	}
	
	/**
	 * 获取顶级流程实例。
	 * @param runId
	 * @return
	 */
	private ProcessRun getTopProcessRun(Long runId){
		ProcessRun	processRun=processRunDao.getById(runId);
		Long parentId=processRun.getParentId();
		while(BeanUtils.isNotIncZeroEmpty(parentId) ){
			processRun=processRunDao.getById(parentId);
			parentId=processRun.getParentId();
		}
		return processRun;
	}
	
	/**
	 * 向上查询得到顶级的流程实例。
	 * @param instId
	 * @return String
	 */
	private String getTopInstId(String actInstId){
		String rtn=actInstId;
		String supInstId=dao.getSupInstByActInstId(actInstId);
		while(StringUtil.isNotEmpty(supInstId)){
			rtn=supInstId;
			supInstId=dao.getSupInstByActInstId(supInstId);
		}
		return rtn;
	}
	
	

	/**
	 * 向下查询流程实例。
	 * @param supperId
	 * @param instList 
	 * void
	 */
	private void getChildInst(String supperId,List<String> instList){
		List<TaskOpinion> list=dao.getBySupInstId(supperId);
		if(BeanUtils.isEmpty(list)) return ;
		for(TaskOpinion taskOpinion:list){
			String instId=taskOpinion.getActInstId();
			instList.add(instId);
			getChildInst(instId,instList);
		}
	}


	/**
	 * 根据act流程定义Id删除对应在流程任务审批意见
	 * 
	 * @param actDefId
	 */
	public void delByActDefIdTaskOption(String actDefId) {
		dao.delByActDefIdTaskOption(actDefId);
	}

	/**
	 * 根据流程实例Id及任务定义Key取得审批列表
	 * 
	 * @param actInstId
	 * @param taskKey
	 * @return
	 */
	public List<TaskOpinion> getByActInstIdTaskKey(Long actInstId, String taskKey) {
		return dao.getByActInstIdTaskKey(actInstId, taskKey);
	}
	
	/**
	 * 取到最新的某个节点的审批记录
	 * 
	 * @param actInstId
	 * @param taskKey
	 * @return
	 */
	public TaskOpinion getLatestTaskOpinion(Long actInstId, String taskKey) {
		List<TaskOpinion> list = getByActInstIdTaskKey(actInstId, taskKey);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 取得某个流程实例中，某用户最新的完成的审批记录
	 * 
	 * @param actInstId
	 * @param exeUserId
	 * @return
	 */
	public TaskOpinion getLatestUserOpinion(String actInstId, Long exeUserId) {
		List<TaskOpinion> taskOpinions = dao.getByActInstIdExeUserId(actInstId, exeUserId);
		if (taskOpinions.size() == 0)
			return null;
		return taskOpinions.get(0);
	}

	/**
	 * 按任务ID删除
	 * 
	 * @param taskId
	 */
	public void delByTaskId(Long taskId) {
		dao.delByTaskId(taskId);
	}

	/**
	 * 取得已经审批完成的任务
	 * 
	 * @param userId
	 * @param subject
	 *            事项名
	 * @param taskName
	 *            任务名
	 * @param pb
	 *            分页
	 * @return
	 */
	public List<BpmFinishTask> getByFinishTask(Long userId, String subject, String taskName, PageBean pb) {
		return dao.getByFinishTask(userId, subject, taskName, pb);
	}
	
	
	/**
	 * 获取正在审批的意见。
	 * @param actInstId
	 * @return
	 */
	public List<TaskOpinion> getCheckOpinionByInstId(Long actInstId){
		return dao.getCheckOpinionByInstId(actInstId);
	}

	public List<TaskOpinion> setTaskOpinionExecutor(List<TaskOpinion> opinions){
		for (TaskOpinion taskOpinion : opinions) {
			if(taskOpinion.getCheckStatus()==TaskOpinion.STATUS_CHECKING){
				TaskEntity task = bpmService.getTask(taskOpinion.getTaskId().toString());
				if(BeanUtils.isNotEmpty(task) ){
					//执行人不为空
					String assignee=task.getAssignee();
					if(ServiceUtil.isAssigneeNotEmpty(assignee)){
						String fullname = "";
						SysUser sysuser = sysUserService.getById(new Long(assignee));
						if(BeanUtils.isNotEmpty(sysuser))
//							fullname =  sysuser.getFullname();
							fullname = ContextSupportUtil.getUsername(sysuser);
						taskOpinion.setExeFullname(fullname);
					}
					//获取候选人
					else{
						 Set<TaskExecutor> set= taskUserService.getCandidateExecutors(task.getId());
						 String fullname="";
						 Set<SysUser> sysUsers=new HashSet<SysUser>();
						 for(Iterator<TaskExecutor> it=set.iterator();it.hasNext();){
							 TaskExecutor taskExe=it.next();
							 if(taskExe==null){
								 continue;
							 }
							 Set<ISysUser> users = taskExe.getSysUser();
							 for(ISysUser user:users){
								 sysUsers.add((SysUser) user);
//								 fullname+=((SysUser) user).getAliasName()+"，";
								 fullname+=ContextSupportUtil.getUsername(((SysUser) user))+"，";
							 }
							 
						 }
						 if(taskOpinion.getCandidateUsers()==null){
							 taskOpinion.setCandidateUsers(new ArrayList<SysUser>());
						 }
						taskOpinion.getCandidateUsers().addAll(sysUsers);
						taskOpinion.setExeFullname(fullname.substring(0,fullname.length()-1));
					}
				}
			}
	}
		return opinions;
	}
	
	/**
	 * 设置执行人的名称
	 * @param list
	 * @return
	 */
	public List<TaskOpinion> setTaskOpinionExeFullname(List<TaskOpinion> list) {
		for (TaskOpinion taskOpinion : list) {
			if(taskOpinion.getCheckStatus()==TaskOpinion.STATUS_CHECKING){
				TaskEntity task = bpmService.getTask(taskOpinion.getTaskId().toString());
				if(BeanUtils.isNotEmpty(task) ){
					//执行人为空
					String assignee=task.getAssignee();
					if(ServiceUtil.isAssigneeNotEmpty(assignee)){
						String fullname = "";
						SysUser sysuser = sysUserService.getById(new Long(assignee));
						if(BeanUtils.isNotEmpty(sysuser))
//							fullname =  sysuser.getFullname();
							fullname =  ContextSupportUtil.getUsername(sysuser);
						taskOpinion.setExeFullname(fullname);
					}
					//获取候选人
					else{
						 Set<TaskExecutor> set= taskUserService.getCandidateExecutors(task.getId());
						 String fullname="";
						 for(Iterator<TaskExecutor> it=set.iterator();it.hasNext();){
							 TaskExecutor taskExe=it.next();
							 String type=taskExe.getType();
							 if(taskExe.getType().equals(TaskExecutor.USER_TYPE_USER)){
//								  fullname +=  sysUserService.getById(new Long(taskExe.getExecuteId())).getFullname() +"<br/>";
								 SysUser sysUser1 = sysUserService.getById(new Long(taskExe.getExecuteId()));
								 fullname += ContextSupportUtil.getUsername(sysUser1);
							 } 
						 }
						taskOpinion.setExeFullname(fullname);
						taskOpinion.setCandidateUser(fullname);
					}
				}
			}
		}
		return list;
	}
	
	
	/**
	 * 设置执行人的名称
	 * @param list
	 * @return
	 */
	public List<TaskOpinion> setTaskOpinionListExeFullname(List<TaskOpinion> list) {
		for(TaskOpinion taskOpinion:list){
			if(taskOpinion.getCheckStatus()==TaskOpinion.STATUS_CHECKING){
				TaskEntity task = bpmService.getTask(taskOpinion.getTaskId().toString());
				if(BeanUtils.isNotEmpty(task) ){
					//执行人为空
					String assignee=task.getAssignee();
					if(ServiceUtil.isAssigneeNotEmpty(assignee)){
//						String fullname =  sysUserService.getById(new Long(assignee)).getFullname();
						SysUser sysUser = sysUserService.getById(new Long(assignee));
						String fullname = ContextSupportUtil.getUsername(sysUser);
						taskOpinion.setExeFullname(fullname);
					}
					//获取候选人
					else{
						 Set<TaskExecutor> set= taskUserService.getCandidateExecutors(task.getId());
						 String fullname="";
						 for(Iterator<TaskExecutor> it=set.iterator();it.hasNext();){
							 TaskExecutor taskExe=it.next();
							 String type=taskExe.getType();
							 if(taskExe.getType().equals(TaskExecutor.USER_TYPE_USER)){
//								  fullname +=  sysUserService.getById(new Long(assignee)).getFullname() +"<br/>";
								 SysUser sysUser = sysUserService.getById(new Long(assignee));
								 fullname +=  ContextSupportUtil.getUsername(sysUser) +"<br/>";
							 } 
						 }
						taskOpinion.setExeFullname(fullname);
						taskOpinion.setCandidateUser(fullname);
					}
				}else{
					if(BeanUtils.isNotIncZeroEmpty(taskOpinion.getExeUserId())){
//						String fullname =  sysUserService.getById(new Long(taskOpinion.getExeUserId())).getFullname();
						SysUser sysUser = sysUserService.getById(new Long(taskOpinion.getExeUserId()));
						String fullname =  ContextSupportUtil.getUsername(sysUser) ;
						taskOpinion.setExeFullname(fullname);
					}
				}
			}
			else{
				if(BeanUtils.isNotIncZeroEmpty(taskOpinion.getExeUserId())){
//					String fullname =  sysUserService.getById(new Long(taskOpinion.getExeUserId())).getFullname();
					SysUser sysUser = sysUserService.getById(new Long(taskOpinion.getExeUserId()));
					String fullname =  ContextSupportUtil.getUsername(sysUser) ;
					taskOpinion.setExeFullname(fullname);
				}
			}
		}
		return list;
	}
	
	
	/**
	 * 根据实例节点获取任务实例状态。
	 * @param actInstId
	 * @param taskKey
	 * @param checkStatus
	 * @return
	 */
	public List<TaskOpinion> getByActInstIdTaskKeyStatus(String actInstId,
			String taskKey, Short checkStatus){
		return dao.getByActInstIdTaskKeyStatus(actInstId,taskKey,checkStatus);
	}
	
	public TaskOpinion getOpinionByTaskId(Long taskId,Long userId) {
		return dao.getOpinionByTaskId(taskId, userId);
	}
	
	
	/**
	 * 根据actInstId更新。
	 * @param actInstId
	 * @param oldActInstId
	 * @return
	 */
	public int updateActInstId(String actInstId,String oldActInstId){
		return dao.updateActInstId(actInstId, oldActInstId);
	}
	
	/**
	 * 根据意见值获取意见。
	 * @param opinion
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String getOpinion(TaskOpinion opinion,boolean isHtml) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("opinion", opinion);
		FreemarkEngine freemarkEngine= AppUtil.getBean(FreemarkEngine.class);
		String rtn="";
		try {
			String template=isHtml?"opinion.ftl":"opiniontext.ftl";
			rtn = freemarkEngine.mergeTemplateIntoString(template, model);
			if(!isHtml){
				//原有功能，去掉html标签过滤
				//rtn=rtn.replaceAll("</?[^>]+>", "");
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return rtn;
	}

	public TaskOpinion getByActInstIds(String actInstId) {
		// TODO Auto-generated method stub
		return dao.getByActInstIds(actInstId);
	}
}

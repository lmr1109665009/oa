package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.JobDao;
import com.suneee.platform.dao.system.JobParamDao;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.Job;
import com.suneee.platform.model.system.JobParam;
import com.suneee.platform.model.system.Position;
import com.suneee.ucp.base.common.Constants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *<pre>
 * 对象功能:职务表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-11-28 16:17:48
 *</pre>
 */
@Service
public class JobService extends BaseService<Job>
{
	@Resource
	private JobDao dao;
	@Resource
	private JobParamDao jobParamDao;
	@Resource 
	private UserPositionService userPositionService;
	@Resource
	private PositionService positionService;
	@Resource 
	private DictionaryService dictionaryService;
	
	public JobService()
	{
	}
	
	@Override
	protected IEntityDao<Job, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 删除职务，实际是修改标志位
	 * 同时修改sys_user_pos用户岗位关系表、sys_pos岗位表
	 * @author hjx
	 * @version 创建时间：2013-12-4  上午10:50:27
	 * @param posId
	 */
	public void deleteByUpdateFlag(Long id){
		  dao.deleteByUpdateFlag(id);
		  userPositionService.delByJobId(id);
		  positionService.deleByJobId(id);
	}

	/**
	 * 判断职务名称是否存在
	 * @param jobname
	 * @return
	 */
	public boolean isExistJobCode(String jobCode) {
		return dao.isExistJobCode(jobCode);
	}
	
	
	
	public boolean isExistJobCodeForUpd(String jobCode,Long jobId) {
		return dao.isExistJobCodeForUpd(jobCode,jobId);
	}
	/**
	 * 通过职务代码获取职务
	 * @param jobCode
	 * @return
	 */
	public Job getByJobCode(String jobCode){
		return dao.getByJobCode(jobCode);
	}
	
	public Job getByJobCodeForUpd(String jobCode, Long jobid){
		Job job = dao.getByJobCodeForUpd(jobCode, jobid);
		return job;
	}
	
	
	/**
	 * 保存 职务表 信息
	 * @param job
	 */
	public void save(Job job){
		Long id=job.getJobid();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			job.setJobid(id);
			this.add(job);
		}
		else{
			this.update(job);
		}
	}
	
	/**
	 * 添加数据 
	 * @param job
	 * @throws Exception
	 */
	@Override
	public void add(Job job){
		super.add(job);
		this.addSubList(job);
	}
	
	/**
	 * 更新数据
	 * @param job
	 * @throws Exception
	 */
	@Override
	public void update(Job job) {
		super.update(job);
		delByPk(job.getJobid());
		addSubList(job);
	}
	
	/**
	 * 
	 * 添加子表记录
	 * @param job
	 * @throws Exception
	 */
	private void addSubList(Job job) {
		List<JobParam> jobParamList=job.getJobParamList();
		if(BeanUtils.isNotEmpty(jobParamList)){
			for(JobParam jobParam:jobParamList){
				jobParam.setJobid(job.getJobid());
				jobParam.setId(UniqueIdUtil.genId());
				jobParamDao.add(jobParam);
			}
		}
	}
	
	/**
	 * 根据外键删除子表记录
	 * @param jobid
	 */
	private void delByPk(Long jobid){
		jobParamDao.delByMainId(jobid);
	}
	
	/**
	 * 根据外键获得职务参数列表
	 * @param jobid
	 * @return
	 */
	public List<JobParam> getJobParamList(Long jobid) {
		return jobParamDao.getByMainId(jobid);
	}
	
	/**
	 * 根据当前用户的岗位，获取用户职务相对应的value值
	 * @param key
	 * @param userId
	 * @return
	 */
	public String getJobParamValueByKey(String key){
		Position pos = (Position) ContextUtil.getCurrentPos();
		if(pos == null){
			return "";
		}
		Long jobId = pos.getJobId();
		return jobParamDao.getValueByKeyJobId(key,jobId);
	}
	
	/**
	 * 根据用户ID，获取用户主岗位的职务相对应的value值
	 * @return
	 */
	public String getJobParamValueByKeyUserId(String key,Long userId){
		Position pos = positionService.getPrimaryPositionByUserId(userId);
		if(pos == null){
			return "";
		}
		Long jobId = pos.getJobId();
		return jobParamDao.getValueByKeyJobId(key,jobId);
	}
	
	/** 
	 * 获取职务信息
	 * @param jobCode
	 * @param orgCode
	 * @param jobName
	 * @param gradeName
	 * @param categoryName
	 * @param index
	 * @return
	 */
	public Job generateJob(String jobCode,String orgCode, String jobName, String gradeName, String categoryName, int index){
		String tmpCode = jobCode;
		if(index > 0){
			tmpCode = jobCode + index;
		}
		
		// 根据jobCode查询职务信息
		Job job = this.getByJobCode(tmpCode);
		// 当职务信息为空时，则添加新的职务
		if(job == null){
			Short grade = this.getJobGradeByGradeName(gradeName, orgCode);
			Long  jobCategory = this.getJobCategoryByCategoryName(categoryName, orgCode);
			job = this.addJob(tmpCode, jobName, grade, jobCategory, orgCode);
		}
		// 职务信息不为空
		else{
			// 职务未被删除且职务名称与传入职务名称相等，则更新职务等级
			if(job.getJobname().equals(jobName)){
				job.setGrade(this.getJobGradeByGradeName(gradeName, orgCode));
				job.setJobCategory(this.getJobCategoryByCategoryName(categoryName, orgCode));
				this.update(job);
			}
			// 职务被删除或者职务未被删除但是职务名称与传入的职务名称不相等，则重新获取职务信息
			else{
				index++;
				job = this.generateJob(jobCode, orgCode, jobName, gradeName, categoryName, index);
			}
		}
		return job;
	}
	
	/** 
	 * 添加职务信息
	 * @param jobCode
	 * @param jobName
	 * @param grade
	 * @param jobCategory
	 * @param orgCode
	 * @return
	 */
	public Job addJob(String jobCode, String jobName, Short grade, Long jobCategory, String orgCode){
		Job job = new Job();
		job.setJobid(UniqueIdUtil.genId());
		job.setJobname(jobName);
		job.setJobcode(jobCode);
		job.setGrade(grade);
		job.setJobCategory(jobCategory);
		job.setEnterpriseCode(orgCode);
		this.add(job);
		return job;
	}
	
	/**
	 * 根据职务级别名称从数据字典中获取职务等级（默认为80=管理人员）
	 * @param gradeName 职务级别名称
	 * @return
	 */
	private Short getJobGradeByGradeName(String gradeName, String orgCode){
		Dictionary dictionary = dictionaryService.getByNodeKeyAndItemName(Constants.DIC_NODEKEY_ZWJB, gradeName, orgCode);
		Short jobGrade = 80;
		if(dictionary != null){
			jobGrade = Short.valueOf(dictionary.getItemValue());
		}
		return jobGrade;
	}
	
	/** 
	 * 根据职务分类名称从数据字典中获取职务分类（默认为30=基层人员）
	 * @param categoryName
	 * @param orgCode
	 * @return
	 */
	private Long getJobCategoryByCategoryName(String categoryName, String orgCode){
		Dictionary dictionary = dictionaryService.getByNodeKeyAndItemName(Job.CATEGORY_NODE_KEY, categoryName, orgCode);
		Long jobCategory = Long.valueOf(AppConfigUtil.get(Constants.DEFAULT_JOB_CATEGORY, "30"));
		if(dictionary != null){
			jobCategory = Long.valueOf(dictionary.getItemValue());
		}
		return jobCategory;
	}
	
	/** 
	 * 获取用户的最高职务分类级别
	 * @param userId
	 * @param enterpriseCode
	 * @return
	 */
	public Long getMaxCategoryByUidAndEcode(Long userId, String enterpriseCode){
		return dao.getMaxCategoryByUidAndEcode(userId, enterpriseCode);
	}
}

/**
 * 
 */
package com.suneee.oa.controller.user;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.Job;
import com.suneee.platform.model.system.JobParam;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.JobService;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.oa.service.user.JobExtendService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 职务信息Job扩展Controller类
 * 
 * @author xiongxianyun
 *
 */
@Controller
@RequestMapping("/oa/user/jobExtend/")
public class JobExtendController extends UcpBaseController{
	@Resource
	private JobExtendService jobExtendService;
	@Resource
	private JobService jobService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;


	/**
	 * 获取系统所有职务信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getAll")
	@Action(description="获取系统所有职务信息", detail="获取系统所有职务信息", execOrder=ActionExecOrder.AFTER, exectype="管理日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo getAll(HttpServletRequest request, HttpServletResponse response){
		try {
			//查询登录用户当前所属企业的下的职务
			QueryFilter filter = new QueryFilter(request,false);
			String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
			filter.addFilter("enterpriseCode", enterpriseCode);
			
			List<Job> list = jobExtendService.getAll(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统所有职务信息成功！", list);
		} catch (Exception e) {
			logger.error("获取系统所有职务信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统所有职务信息失败：" + e.getMessage());
		}
	}
	
	/**
	 * 获取系统职务信息分页列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("list")
	@Action(description="获取系统职务信息分页列表", detail="获取系统职务信息分页列表", execOrder=ActionExecOrder.AFTER, exectype="管理日志", 
		ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo list(HttpServletRequest request, HttpServletResponse response){
		try {
			QueryFilter filter = new QueryFilter(request,true);
			//获取当前用户企业编码
			String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
			filter.addFilter("enterpriseCode", enterpriseCode);
			PageList<Job> list= (PageList<Job>) jobService.getAll(filter);

			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统职务信息分页列表成功！", PageUtil.getPageVo(list));
		} catch (Exception e) {
			logger.error("获取系统职务信息分页列表失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统职务信息分页列表失败：" + e.getMessage());
		}
	}
	
	/**
	 * 获取职务信息详情
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("details")
	@Action(description="获取职务信息详情", detail="获取职务<#if jobId??><#assign entity=jobService.getById(Long.valueOf(jobId))/>"
			+ "<#if entity!=''>【${SysAuditLinkService.getJobLink(entity)}】</#if></#if>详情", 
		execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo details(HttpServletRequest request, HttpServletResponse response){
		Long jobId = RequestUtil.getLong(request, "jobId");
		if(jobId == 0 || jobId == null){
			logger.error("获取职务信息详情失败：请求参数为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取职务信息详情失败：请求参数错误！");
		}
		try {
			Job job = jobExtendService.getById(jobId);
			List<JobParam> jobParamList=jobService.getJobParamList(jobId);
			Map<String,Object> map = new HashMap<>();
			map.put("job",job);
			map.put("jobParamList",jobParamList);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取职务信息详情成功！", map);
		} catch (Exception e) {
			logger.error("获取职务信息详情失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取职务信息详情失败：" + e.getMessage());
		}
	}

	/**
	 * 	编辑职务表
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description="编辑职务表")
	public ResultVo edit(HttpServletRequest request) throws Exception
	{
		Long jobid=RequestUtil.getLong(request,"jobId",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		Job job=jobService.getById(jobid);
		List<JobParam> jobParamList=jobService.getJobParamList(jobid);
		List<Dictionary> dicList = dictionaryService.getByNodeKey(Job.NODE_KEY);

		Map<String,Object> map = new HashMap<>();
		map.put("job",job);
		map.put("jobParamList",jobParamList);
		map.put("returnUrl",returnUrl);
		map.put("dicList",dicList);

		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取职务信息成功！",map);
	}

	/**
	 * 删除职务信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("del")
	@Action(description="删除职务信息", detail="删除职务信息<#if jobId??><#list StringUtils.split(jobId, \",\") as item>"
			+ "<#assign entity=jobService.getById(Long.valueOf(item))/><#if entity!=''>【${entity.jobname}】</#if></#list></#if>", 
			execOrder=ActionExecOrder.BEFORE, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo del(HttpServletRequest request, HttpServletResponse response){
		Long[] lAryId = RequestUtil.getLongAryByStr(request, "jobId");
		if(lAryId == null){
			logger.error("删除职务信息失败：请求参数不能为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除职务信息失败：请求参数错误！");
		}
		//判断职务是否有人员使用
		for(int i=0,n=lAryId.length;i<n;i++){
			if(jobExtendService.isJobUsedByUser(lAryId[i])){
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"职务被人员使用，不能删除！");
			}else{
				jobService.deleteByUpdateFlag(lAryId[i]);
			}
		}
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除职务信息成功！");
	}
	
	/**
	 * 保存职务信息
	 * @param request
	 * @param response
	 * @param job
	 * @return
	 */
	@RequestMapping("save")
	@Action(description="保存职务信息", detail="<#if isAdd>添加<#else>更新</#if>职务信息<#if jobId??>"
			+ "<#assign entity=jobService.getById(Long.valueOf(jobId))/><#if entity!=''>"
			+ "【${SysAuditLinkService.getJobLink(entity)}】</#if></#if><#if isSuccessed>成功<#else>失败</#if>", 
			execOrder=ActionExecOrder.AFTER, exectype="管理日志", ownermodel=SysAuditModelType.USER_MANAGEMENT)
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, Job job) throws Exception {
		SysAuditThreadLocalHolder.putParamerter("isAdd", job.getJobid() == null);
		SysAuditThreadLocalHolder.putParamerter("isSuccessed", false);
		String resultMsg=null;
		try{
			// 生成职务代码，生成规则：企业编码_职务名称全拼
			String pinYin = PinyinUtil.getPinYinHeadCharFilter(job.getJobname());
			String jobCode = job.getEnterpriseCode().toLowerCase() + Constants.SEPARATOR_UNDERLINE + pinYin;

			Job jobDb = null;
			String tmpCode = jobCode;
			int index = 1;
			Long jobId = job.getJobid();
			while(true){
				// 根据职务代码查询职务信息，判断组织代码是否存在
				if(jobId == null || jobId == 0){
					jobDb = jobService.getByJobCode(tmpCode);
				}else{
					jobDb = jobService.getByJobCodeForUpd(tmpCode, job.getJobid());
				}
				// 职务代码不存在，则保存职务信息
				if(jobDb == null){
					job.setJobcode(tmpCode);
					if(jobId == null || jobId == 0){
						jobService.save(job);
						resultMsg="添加职务成功";
					}else{
						jobService.update(job);
						resultMsg="更新职务成功";
					}
					SysAuditThreadLocalHolder.putParamerter("jobId", job.getJobid());
					SysAuditThreadLocalHolder.putParamerter("isSuccessed", true);
					return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, resultMsg);
				}
				// 职务代码存在
				else{
					// 数据库中的职务名称与请求的职务名称相同，则提示用户职务名称已经存在
					if(jobDb.getJobname().equals(job.getJobname())){
						resultMsg = "职务名称已经存在";
						return new ResultVo(ResultVo.COMMON_STATUS_FAILED, resultMsg);
					}
					// 数据库中的职务名称与请求的职务名称不相同
					else{
						tmpCode = jobCode + Constants.SEPARATOR_UNDERLINE + index;
						index++;
					}
				}
			}
		}
		catch(Exception e){
			logger.error(resultMsg + "：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, resultMsg + "：系统内部错误！");
		}
	}

	/**
	 * 获取职务级别列表
	 * @return
	 */
	@RequestMapping("getJobGrade")
	@ResponseBody
	public ResultVo getJobGrade() throws Exception{
		List<Dictionary> dicList = dictionaryService.getByNodeKey(Job.NODE_KEY);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取职务级别列表数据成功！",dicList);
	}
	
	/** 
	 * 获取企业职务列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getByEnterpriseCode")
	@ResponseBody
	public ResultVo getByEnterpriseCode(HttpServletRequest request, HttpServletResponse response)throws Exception{
		String enterpriseCode = RequestUtil.getString(request, "enterpriseCode");
		if(StringUtils.isBlank(enterpriseCode)){
			logger.error("获取企业职务列表失败：企业编码为空！");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取企业职务列表失败：请求参数错误！");
		}
		// 当前用户所属企业编码
		Set<String> enterpriseCodeSet = enterpriseinfoService.getCodeSetByUserId(ContextUtil.getCurrentUserId());
		// 查询职务的所属企业为组织所属企业与当前用户所属企业的交集
		String jobEnterpriseCode =null;
		if(enterpriseCodeSet.contains(enterpriseCode)){
			jobEnterpriseCode = enterpriseCode;
		}
		
		if(StringUtils.isBlank(jobEnterpriseCode)){
			logger.error("获取企业职务列表失败：所选组织所属企业编码【" + enterpriseCode + "】与当前用户【" 
				+ ContextUtil.getCurrentUserId() + "】所属企业没有交集");
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取企业职务列表失败：没有权限查看所选组织的职务！");
		}
		
		QueryFilter filter = new QueryFilter(request,false);
		filter.addFilter("enterpriseCode", enterpriseCode);
		List<Job> list = jobExtendService.getAll(filter);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取企业职务列表成功！", list);
	}
}

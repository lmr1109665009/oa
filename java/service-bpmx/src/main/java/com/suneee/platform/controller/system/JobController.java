package com.suneee.platform.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.Job;
import com.suneee.platform.model.system.JobParam;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.JobService;
import com.suneee.platform.service.system.PositionService;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/**
 *<pre>
 * 对象功能:职务表 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-11-28 16:17:48
 *</pre>
 */
@Controller
@RequestMapping("/platform/system/job/")
public class JobController extends BaseController
{
	@Resource
	private JobService jobService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private PositionService positionService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	
	
	/**
	 * 添加或更新职务表。
	 * @param request
	 * @param response
	 * @param job 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新职务表")
	public void save(HttpServletRequest request, HttpServletResponse response,Job job) throws Exception
	{
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
					 jobService.save(job);
					 if(jobId == null || jobId == 0){
						 resultMsg="添加职务成功";
					 }else{
						 resultMsg="更新职务成功";
					 }
					 writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
					break;
				 }
				 // 职务代码存在
				 else{
					 // 数据库中的职务名称与请求的职务名称相同，则提示用户职务名称已经存在
					 if(jobDb.getJobname().equals(job.getJobname())){
						 resultMsg = "职务名称已经存在";
						 writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Fail);
						 break;
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
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	/**
	 * 取得职务表分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看职务表分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		QueryFilter filter = new QueryFilter(request,"jobItem");
		Set<String> enterpriseCodeSet = enterpriseinfoService.getCodeSetByUserId(ContextUtil.getCurrentUserId());
		filter.addFilter("enterpriseCodes", enterpriseCodeSet);
		List<Job> list=jobService.getAll(filter);
		ModelAndView mv=this.getAutoView().addObject("jobList",list);
		
		return mv;
	}
	
	/**
	 * 删除职务表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除职务表")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			//非物理删除，修改删除标志
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "jobid");
			
			for(int i=0,n=lAryId.length;i<n;i++){
				if(positionService.isJobUsedByPos(lAryId[i])){
					message = new ResultMessage(ResultMessage.Fail,"职务被岗位使用，不能删除！");
					break;
				}else{
					jobService.deleteByUpdateFlag(lAryId[i]);
					message=new ResultMessage(ResultMessage.Success, "删除职务表成功!");
				}
			}

		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑职务表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑职务表")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long jobid=RequestUtil.getLong(request,"jobid",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		Job job=jobService.getById(jobid);
		List<JobParam> jobParamList=jobService.getJobParamList(jobid);
		List<Dictionary> dicList = dictionaryService.getByNodeKey(Job.NODE_KEY);
		
		return getAutoView().addObject("job",job)
							.addObject("jobParamList",jobParamList)
							.addObject("returnUrl",returnUrl)
							.addObject("dicList", dicList);
	}

	/**
	 * 取得职务表明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看职务表明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long jobid=RequestUtil.getLong(request,"jobid");
		Job job = jobService.getById(jobid);	
		List<JobParam> jobParamList=jobService.getJobParamList(jobid);
		return getAutoView().addObject("job", job).addObject("jobParamList",jobParamList);
	}
	
	
	/**
	 * 角色对话框的展示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("dialog")
	@Action(description="角色对话框显示",
			execOrder=ActionExecOrder.AFTER,
			detail="角色对话框显示",
			exectype = "管理日志")
	public ModelAndView dialog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String isSingle =RequestUtil.getString(request, "isSingle");
		return getAutoView().addObject("isSingle",isSingle);
		
	}
	
	/**
	 * 角色对话框的展示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("selector")
	@Action(description="角色对话框显示",
			execOrder=ActionExecOrder.AFTER,
			detail="角色对话框显示",
			exectype = "管理日志")
	public ModelAndView selector(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Job> list = new ArrayList<Job>();
		String isSingle = RequestUtil.getString(request, "isSingle", "false");
		QueryFilter queryFilter = new QueryFilter(request, "jobItem");
		
		// 组织所属企业编码
		String enterpriseCode = RequestUtil.getString(request, "enterpriseCode");
		if(StringUtils.isBlank(enterpriseCode)){
			logger.debug("职务选择器获取职务失败：组织所属企业编码为空！");
			queryFilter.setForWeb();
		} else {
			// 当前用户所属企业编码
			Set<String> enterpriseCodeSet = enterpriseinfoService.getCodeSetByUserId(ContextUtil.getCurrentUserId());
			// 查询职务的所属企业为组织所属企业与当前用户所属企业的交集
			String jobEnterpriseCode =null;
			if(enterpriseCodeSet.contains(enterpriseCode)){
				jobEnterpriseCode = enterpriseCode;
			}
			if(jobEnterpriseCode == null){
				logger.debug("所选组织所属企业编码【" + enterpriseCode + "】与当前用户【" + ContextUtil.getCurrentUserId() + "】所属企业没有交集");
				queryFilter.setForWeb();
			} else {
				queryFilter.addFilter("enterpriseCode", jobEnterpriseCode);
				list = jobService.getAll(queryFilter);
			}
		}
		
		ModelAndView mv = this.getAutoView()
				.addObject("jobList", list)
				.addObject("isSingle", isSingle)
				.addObject("enterpriseCode", enterpriseCode);
		return mv;
	}
	
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
			String[] enterpriseCodes = RequestUtil.getStringAryByStr(request, "enterpriseCodeString");
			QueryFilter filter = new QueryFilter(request,false);
			if(enterpriseCodes != null){
				filter.addFilter("enterpriseCodes", enterpriseCodes);
			}
			
			List<Job> list = jobService.getAll(filter);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统所有职务信息成功！", list);
		} catch (Exception e) {
			logger.error("获取系统所有职务信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取系统所有职务信息失败：" + e.getMessage());
		}
	}
	
}

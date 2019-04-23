package com.suneee.eas.oa.controller.system;

import com.suneee.eas.common.api.config.ScheduleApi;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.oa.model.system.DicItem;
import com.suneee.eas.oa.model.system.DicType;
import com.suneee.eas.oa.service.system.DicItemService;
import com.suneee.eas.oa.service.system.DicTypeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: 定时任务控制器
 * @Author: 子华
 * @Date: 2018/7/31 17:54
 */
@RestController
@RequestMapping(ModuleConstant.SYSTEM_MODULE+FunctionConstant.SCHEDULE_QUARTZ)
public class ScheduleApiController {
	private Logger log= LogManager.getLogger(ScheduleApiController.class);
	//定时任务数据字典码
	private static final String DIC_CODE="sys_schedule";
	private final String PREFIX_GROUP="service-oa_";

	@Autowired
	private DicTypeService dicTypeService;
	@Autowired
	private DicItemService dicItemService;

	/**
	 * 获取定时器分组
	 * @return
	 */
	private String getGroup(){
		return PREFIX_GROUP+ ContextSupportUtil.getCurrentEnterpriseCode();
	}

	/**
	 * 获取定时任务列表
	 * @param request
	 * @return
	 */
	@RequestMapping("jobList")
	public ResponseMessage jobList(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("group",getGroup());
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl() +ScheduleApi.jobList,ResponseMessage.class,params);

		List<Map<String,Object>> dataMapList= (List<Map<String, Object>>) message.getData();
		DicType dicType =dicTypeService.getByCode(DIC_CODE);
		if (dicType==null||message.getStatus()==ResponseMessage.STATUS_FAIL){
			return message;
		}
		dicType.setList(dicItemService.listByDicId(dicType.getDicId()));
		for (Map<String,Object> dataMap:dataMapList){
			String cls= (String) dataMap.get("cls");
			for (DicItem dicItem:dicType.getList()){
				if (dicItem.getVal().equals(cls)){
					dataMap.put("className",dicItem.getOption());
					break;
				}
			}
		}
		return message;
	}

	/**
	 * 添加定时任务
	 * @param request
	 * @return
	 */
	@RequestMapping("addJob")
	public ResponseMessage addJob(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String cls= RequestUtil.getString(request,"cls");
		String jobName=RequestUtil.getString(request,"jobName");
		String cron=RequestUtil.getString(request,"cron");
		String desc=RequestUtil.getString(request,"desc");
		String jsonParams=RequestUtil.getString(request,"params");
		if (StringUtils.isEmpty(cls)){
			return ResponseMessage.fail("定时任务包名不允许为空");
		}
		if (StringUtils.isEmpty(jobName)){
			return ResponseMessage.fail("定时任务名称不允许为空");
		}
		params.add("jobName",jobName);
		params.add("cls",cls);
		params.add("group",getGroup());
		if (StringUtils.isNotBlank(cron)){
			params.add("cron",cron);
		}
		if (StringUtils.isNotBlank(desc)){
			params.add("desc",desc);
		}
		if (StringUtils.isNotBlank(jsonParams)){
			params.add("params",jsonParams);
		}
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.addJob,ResponseMessage.class,params);
		return message;

	}

	/**
	 * 更新定时任务
	 * @param request
	 * @return
	 */
	@RequestMapping("updateJob")
	public ResponseMessage updateJob(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String jobName=RequestUtil.getString(request,"jobName");
		String desc=RequestUtil.getString(request,"desc");
		String jsonParams=RequestUtil.getString(request,"params");
		if (StringUtils.isEmpty(jobName)){
			return ResponseMessage.fail("定时任务名称不允许为空");
		}
		params.add("jobName",jobName);
		params.add("group",getGroup());
		if (StringUtils.isNotBlank(desc)){
			params.add("desc",desc);
		}
		if (StringUtils.isNotBlank(jsonParams)){
			params.add("params",jsonParams);
		}
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.updateJob,ResponseMessage.class,params);
		return message;

	}

	/**
	 * 删除定时任务
	 * @param request
	 * @return
	 */
	@RequestMapping("delJob")
	public ResponseMessage delJob(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String jobName=RequestUtil.getString(request,"jobName");
		if (StringUtils.isEmpty(jobName)){
			return ResponseMessage.fail("定时任务名称不允许为空");
		}
		params.add("jobName",jobName);
		params.add("group",getGroup());
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.delJob,ResponseMessage.class,params);
		return message;
	}

	/**
	 * 获取计划任务列表
	 * @param request
	 * @return
	 */
	@RequestMapping("triggerList")
	public ResponseMessage triggerList(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String jobName=RequestUtil.getString(request,"jobName");
		if (StringUtils.isEmpty(jobName)){
			return ResponseMessage.fail("定时任务名称不允许为空");
		}
		params.add("jobName",jobName);
		params.add("group",getGroup());
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.triggerList,ResponseMessage.class,params);
		return message;
	}

	/**
	 * 添加计划任务
	 * @param request
	 * @return
	 */
	@RequestMapping("addTrigger")
	public ResponseMessage addTrigger(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String jobName=RequestUtil.getString(request,"jobName");
		String triggerName=RequestUtil.getString(request,"triggerName");
		String planJson=RequestUtil.getString(request,"planJson");
		if (StringUtils.isEmpty(jobName)){
			return ResponseMessage.fail("定时任务名称不允许为空");
		}
		if (StringUtils.isEmpty(triggerName)){
			return ResponseMessage.fail("计划任务名称不允许为空");
		}
		if (StringUtils.isEmpty(planJson)){
			return ResponseMessage.fail("计划任务配置不允许为空");
		}
		params.add("jobName",jobName);
		params.add("triggerName",triggerName);
		params.add("group",getGroup());
		params.add("planJson",planJson);
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.addTrigger,ResponseMessage.class,params);
		return message;

	}

	/**
	 * 更新计划任务
	 * @param request
	 * @return
	 */
	@RequestMapping("updateTrigger")
	public ResponseMessage updateTrigger(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String jobName=RequestUtil.getString(request,"jobName");
		String triggerName=RequestUtil.getString(request,"triggerName");
		String planJson=RequestUtil.getString(request,"planJson");
		if (StringUtils.isEmpty(jobName)){
			return ResponseMessage.fail("定时任务名称不允许为空");
		}
		if (StringUtils.isEmpty(triggerName)){
			return ResponseMessage.fail("计划任务名称不允许为空");
		}
		if (StringUtils.isEmpty(planJson)){
			return ResponseMessage.fail("计划任务配置不允许为空");
		}
		params.add("jobName",jobName);
		params.add("triggerName",triggerName);
		params.add("group",getGroup());
		params.add("planJson",planJson);
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.updateTrigger,ResponseMessage.class,params);
		return message;

	}

	/**
	 * 删除计划任务
	 * @param request
	 * @return
	 */
	@RequestMapping("delTrigger")
	public ResponseMessage delTrigger(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String triggerName=RequestUtil.getString(request,"triggerName");
		if (StringUtils.isEmpty(triggerName)){
			return ResponseMessage.fail("计划任务名称不允许为空");
		}
		params.add("triggerName",triggerName);
		params.add("group",getGroup());
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.delTrigger,ResponseMessage.class,params);
		return message;

	}


	/**
	 * 获取计划任务编辑数据
	 * @param request
	 * @return
	 */
	@RequestMapping("getTrigger")
	public ResponseMessage getTrigger(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String triggerName=RequestUtil.getString(request,"triggerName");
		if (StringUtils.isEmpty(triggerName)){
			return ResponseMessage.fail("计划任务名称不允许为空");
		}
		params.add("triggerName",triggerName);
		params.add("group",getGroup());
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.getTrigger,ResponseMessage.class,params);
		return message;

	}


	/**
	 * 开启/暂停计划任务
	 * @param request
	 * @return
	 */
	@RequestMapping("toggleTriggerRun")
	public ResponseMessage toggleTriggerRun(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String triggerName=RequestUtil.getString(request,"triggerName");
		if (StringUtils.isEmpty(triggerName)){
			return ResponseMessage.fail("计划任务名称不允许为空");
		}
		params.add("triggerName",triggerName);
		params.add("group",getGroup());
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.toggleTriggerRun,ResponseMessage.class,params);
		return message;

	}


	/**
	 * 获取任务详情
	 * @param request
	 * @return
	 */
	@RequestMapping("getJobDetail")
	public ResponseMessage getJobDetail(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String jobName=RequestUtil.getString(request,"jobName");
		if (StringUtils.isEmpty(jobName)){
			return ResponseMessage.fail("定时任务名称不允许为空");
		}
		params.add("jobName",jobName);
		params.add("group",getGroup());
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.getJobDetail,ResponseMessage.class,params);
		DicType dicType =dicTypeService.getByCode(DIC_CODE);
		if (dicType==null||message.getStatus()==ResponseMessage.STATUS_FAIL){
			return message;
		}
		dicType.setList(dicItemService.listByDicId(dicType.getDicId()));
		Map<String,Object> jobDetail= (Map<String, Object>) message.getData();
		String cls= (String) jobDetail.get("cls");
		for (DicItem dicItem:dicType.getList()){
			if (dicItem.getVal().equals(cls)){
				jobDetail.put("className",dicItem.getOption());
				break;
			}
		}
		jobDetail.put("dicOption",dicType.getList());
		return message;
	}


	/**
	 * 手动执行定时任务
	 * @param request
	 * @return
	 */
	@RequestMapping("executeJob")
	public ResponseMessage executeJob(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String jobName=RequestUtil.getString(request,"jobName");
		if (StringUtils.isEmpty(jobName)){
			return ResponseMessage.fail("定时任务名称不允许为空");
		}
		params.add("jobName",jobName);
		params.add("group",getGroup());
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.executeJob,ResponseMessage.class,params);
		return message;
	}

	/**
	 * 验证定时任务是否可用
	 * @param request
	 * @return
	 */
	@RequestMapping("validateJob")
	public ResponseMessage validateJob(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String jobName=RequestUtil.getString(request,"jobName");
		if (StringUtils.isEmpty(jobName)){
			return ResponseMessage.fail("定时任务名称不允许为空");
		}
		params.add("jobName",jobName);
		params.add("group",getGroup());
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.validateJob,ResponseMessage.class,params);
		return message;
	}

	/**
	 * 日志列表
	 * @return
	 */
	@RequestMapping("logList")
	public ResponseMessage logList(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("group",getGroup());
		Integer pageNum=RequestUtil.getInt(request,"pageNum",0);
		Integer pageSize=RequestUtil.getInt(request,"pageSize",0);
		String jobName = RequestUtil.getString(request, "jobName");
		params.add("jobName",jobName);
		if (pageNum>0){
			params.add("pageNum",pageNum);
		}
		if (pageSize>0){
			params.add("pageSize",pageSize);
		}
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.logList,ResponseMessage.class,params);
		return message;
	}

	/**
	 * 删除日志
	 * @return
	 */
	@RequestMapping("delLog")
	public ResponseMessage delLog(HttpServletRequest request){
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		String ids=RequestUtil.getString(request,"ids");
		params.add("ids",ids);
		ResponseMessage message=RestTemplateUtil.post(ServiceConstant.getConfigServiceUrl()+ScheduleApi.delLog,ResponseMessage.class,params);
		return message;
	}

}

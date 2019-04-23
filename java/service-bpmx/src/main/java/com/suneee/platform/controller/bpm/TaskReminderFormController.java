package com.suneee.platform.controller.bpm;

import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.TaskReminder;
import com.suneee.platform.service.bpm.TaskReminderService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 对象功能:任务节点催办时间设置 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-17 13:56:53
 */
@Controller
@RequestMapping("/platform/bpm/taskReminder/")
public class TaskReminderFormController extends BaseFormController
{
	@Resource
	private TaskReminderService taskReminderService;
	
	/**
	 * 添加或更新任务节点催办时间设置。
	 * @param request
	 * @param taskReminder 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@ResponseBody
	@Action(description="添加或更新任务节点催办时间设置")
	public ResultVo save(HttpServletRequest request, TaskReminder taskReminder, BindingResult bindResult) throws Exception
	{
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		ResultMessage resultMessage=validForm("taskReminder", taskReminder, bindResult, request);
		//add your custom validation rule here such as below code:
		//bindResult.rejectValue("name","errors.exist.student",new Object[]{"jason"},"重复姓名");
		if(resultMessage.getResult()==ResultMessage.Fail)
		{
			result.setMessage(resultMessage.getMessage());
			return result;
		}
		String resultMsg=null;
		int reminderStartDay= RequestUtil.getInt(request, "reminderStartDay");
		int reminderStartHour=RequestUtil.getInt(request, "reminderStartHour");
		int reminderStartMinute=RequestUtil.getInt(request, "reminderStartMinute");
		//计算任务催办开始时间为多少个工作日，以分钟计算
		int reminderStart=(reminderStartDay*24+reminderStartHour)*60+reminderStartMinute;
		
	    int reminderEndDay=RequestUtil.getInt(request, "reminderEndDay");
	    int reminderEndHour=RequestUtil.getInt(request, "reminderEndHour");
	    int reminderEndMinute=RequestUtil.getInt(request, "reminderEndMinute");
	    //计算任务催办结束时间为多少个工作日，以分钟计算
	    int reminderEnd=(reminderEndDay*24+reminderEndHour)*60+reminderEndMinute;
	    
	    int completeTimeDay=RequestUtil.getInt(request,"completeTimeDay");
	    int completeTimeHour=RequestUtil.getInt(request,"completeTimeHour");
	    int completeTimeMinute=RequestUtil.getInt(request, "completeTimeMinute");
	    //计算办结时间为多少个工作日，以分钟计算
	    int completeTime=(completeTimeDay*24+completeTimeHour)*60+completeTimeMinute;
	    
	    taskReminder.setReminderStart(reminderStart);
		taskReminder.setReminderEnd(reminderEnd);
		taskReminder.setCompleteTime(completeTime);
		
		try {
			if (taskReminder.getTaskDueId() == null || taskReminder.getTaskDueId() == 0){
				taskReminder.setTaskDueId(UniqueIdUtil.genId());
				taskReminderService.add(taskReminder);
				resultMsg="添加任务节点催办设置成功";
			}else{
				taskReminderService.update(taskReminder);
				resultMsg="更新任务节点催办设置成功";
			}
			result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			result.setMessage(resultMsg);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(resultMsg + "," + e.getMessage());
		}
		return result;
	}
}

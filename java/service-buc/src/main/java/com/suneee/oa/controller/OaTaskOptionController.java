package com.suneee.oa.controller;

import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:流程任务审批意见控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2017-12-15 14:03:00
 * </pre>
 */
@Controller
@RequestMapping("/oa/oaTaskOption/")
public class OaTaskOptionController extends BaseController {

	@Resource
	private TaskOpinionService taskOpinionService;
	@Resource
	private ProcessRunService processRunService;



	/**
	 * 取得某个流程任务审批意见分页列表
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description = "查看流程任务审批意见分页列表")
	public Object list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> resultMap = new HashMap<>();
		String preUrl = RequestUtil.getPrePage(request);
		Long runId = RequestUtil.getLong(request, "runId", 0L);
		if (runId.equals(0L)) {
			return resultMap.put("content","请输入流程运行ID");
		}

		Map<String,Object> rtnMap= getOptionByRunId(runId);
		resultMap.put("taskOpinionList",rtnMap.get("taskOpinionList"));
		resultMap.put("processRun",rtnMap.get("processRun"));
		resultMap.put("returnUrl",preUrl);

		return resultMap;
	}


	/**
	 * 显示意见列表。
	 * @param runId
	 * @return
	 */
	@RequestMapping("listJson")
	@ResponseBody
	public Map<String,Object> getOptionByRunId(@RequestParam(value="runId") Long runId){
		Map<String,Object> rtnMap=new HashMap<String,Object>();

		ProcessRun processRun = processRunService.getById(runId);
		//取得关联的流程实例ID
		List<TaskOpinion> list = taskOpinionService.getByRunId(runId);
		//设置代码执行人
		list = taskOpinionService.setTaskOpinionExecutor(list);

		JSONArray jsonArray=formatTaskOpinionList(list);

		rtnMap.put("processRun", processRun);
		rtnMap.put("taskOpinionList", jsonArray);

		return rtnMap;

	}

	/**
	 * 批量获取显示意见列表（外部调用接口）
	 * @param
	 * @return
	 */
	@RequestMapping("listJsonBat")
	@ResponseBody
	public ResultVo listJsonBat(@RequestBody Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<>();
		String runIds = map.get("runIds").toString();
		String[] runId = runIds.split(",");
		for(String id:runId){
			Map<String,Object> rtnMap=new HashMap<String,Object>();

			ProcessRun processRun = processRunService.getById(Long.valueOf(id));
			//取得关联的流程实例ID
			List<TaskOpinion> list = taskOpinionService.getByRunId(Long.valueOf(id));
			//设置代码执行人
			list = taskOpinionService.setTaskOpinionExecutor(list);

			JSONArray jsonArray=formatTaskOpinionList(list);

			rtnMap.put("processRun", processRun);
			rtnMap.put("taskOpinionList", jsonArray);

			resultMap.put(id,rtnMap);
		}

		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取审批历史数据成功！",resultMap);
	}

	/**
	 * <pre>
	 * 把taskOpinion封装成jsonArray对象，组成结构:
	 * 思路:
	 * 按照时间排序，如果i上的taskName跟i+1上的一致就放在同一个json上
	 * eg:
	 * json:{taskName:name,list:[taskopinion1,taskopinion2,...]}
	 * </pre>
	 * @param list
	 * @return JSONArray
	 * @exception
	 * @since 1.0.0
	 */
	private JSONArray formatTaskOpinionList(List<TaskOpinion> list) {
		JSONArray jsonArray = new JSONArray();
		for (TaskOpinion to : list) {
			String tn = "";
			List<JSONObject> tos = null;
			JSONObject jsonObject = null;
			String taskName = to.getTaskName();
			if(StringUtil.isNotEmpty(to.getSuperExecution())){
				taskName=taskName+"【子流程】";
			}

			if (!jsonArray.isEmpty()) {
				jsonObject = jsonArray.getJSONObject(jsonArray.size() - 1);
				tn = jsonObject.getString("taskName");
				tos = (List<JSONObject>) jsonObject.get("list");
			}

			if (tn.equals(taskName)) {
				JSONObject jsonObj=convertOpinion(to);
				tos.add(jsonObj);
			} else {
				jsonObject = new JSONObject();
				tos = new ArrayList<JSONObject>();
				JSONObject jsonObj=convertOpinion(to);
				tos.add(jsonObj);
				jsonObject.put("taskName", taskName);
				jsonObject.put("list", tos);
				jsonArray.add(jsonObject);
			}
		}
		return jsonArray;
	}

	private JSONObject convertOpinion(TaskOpinion opinion){
		JSONObject jsonObj=new JSONObject();

		jsonObj.accumulate("opinionId", opinion.getOpinionId()) ;
		String taskName = opinion.getTaskName();
		if(StringUtil.isNotEmpty(opinion.getSuperExecution())){
			taskName=taskName+"【子流程】";
		}
		jsonObj.accumulate("taskName",taskName ) ;
		jsonObj.accumulate("exeFullname", opinion.getExeFullname()) ;
		jsonObj.accumulate("exeUserId", opinion.getExeUserId()) ;
		jsonObj.accumulate("status", opinion.getStatus()) ;
		jsonObj.accumulate("checkStatus", opinion.getCheckStatus()) ;
		jsonObj.accumulate("startTimeStr", opinion.getStartTimeStr()) ;
		jsonObj.accumulate("durTimeStr", opinion.getDurTimeStr()) ;
		jsonObj.accumulate("endTimeStr", opinion.getEndTimeStr()) ;
		jsonObj.accumulate("opinion", opinion.getOpinion()==null?"":opinion.getOpinion()) ;

		List<SysUser> users= opinion.getCandidateUsers();
		if(BeanUtils.isNotEmpty(users)){
			JSONArray ary=new JSONArray();
			for(SysUser user:users){
				JSONObject userJson=new JSONObject();
				userJson.accumulate("userId",user.getUserId());
				userJson.accumulate("fullname",user.getFullname());
				ary.add(userJson);
			}
			jsonObj.accumulate("candidateUsers", ary) ;
		}
		return jsonObj;
	}
}

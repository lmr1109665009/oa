package com.suneee.weixin.controller;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.dao.bpm.TaskDao;
import com.suneee.platform.model.system.SysBulletin;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.ITaskService;
import com.suneee.platform.service.system.SysBulletinService;
import com.suneee.platform.service.system.SysUserService;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/weixin/")
public class IndexController {
	
	@Resource
	private FreemarkEngine freemarkEngine;
	@Resource
	private TaskDao taskDao;
	@Resource
	private SysBulletinService sysBulletinService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private ITaskService taskServiceImpl;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	
	@RequestMapping("index")
	@ResponseBody
	public JSONObject index(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateException, InterruptedException{
		ISysUser user= ContextUtil.getCurrentUser();
		JSONObject data=getData(request,user);
//		data.put("curUser", user.getFullname());
		data.put("curUser", ContextSupportUtil.getUsername((SysUser) user));
		System.out.println(data.toString());
		return data;
	}
	
	private JSONObject getData(HttpServletRequest request, ISysUser user){
		JSONObject jsonObj=new JSONObject();
		
		List<JSONObject> taskModel=getTaskModel(request,user.getUserId());
		List<JSONObject> bulletin=getBulletinModel();
		
		jsonObj.put("task", taskModel);
		jsonObj.put("bulletin", bulletin);
		
		return jsonObj;
	}
	
	/**
	 * 获取任务列表。
	 * @param userId
	 * @return
	 */
	private List<JSONObject> getTaskModel(HttpServletRequest request, Long userId){
		List<JSONObject> taskList=new ArrayList<JSONObject>();
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("userId",userId);
		QueryFilter queryFilter=new QueryFilter(request, false);
		List<ProcessTask> list= taskServiceImpl.getMyMobileTasks(queryFilter);
		int taskAmount=0;
		for(ProcessTask task:list){
			taskAmount++;
			if(taskAmount>5) break;
			JSONObject obj=new JSONObject();
			obj.put("id", task.getId());
			obj.put("subject", task.getSubject());
			obj.put("createTime", TimeUtil.getDateString(task.getCreateTime()));
			obj.put("defId", task.getDefId());
			obj.put("runId", task.getRunId());
			obj.put("creator", task.getCreator());
			obj.put("nodename", task.getName());
			
			taskList.add(obj);
			
		}
		return taskList;
	}
	
	/**
	 * 获取公告列表。
	 * @return
	 */
	private List<JSONObject> getBulletinModel(){
		List<JSONObject> bulletinList=new ArrayList<JSONObject>();
	
		List<SysBulletin> sysBulletins= sysBulletinService.getTopBulletin(5);
		for(SysBulletin bulletin:sysBulletins){
			String date=TimeUtil.getDateString(bulletin.getCreatetime() );
			JSONObject object=new JSONObject();
			
			object.put("subject", bulletin.getSubject());
			object.put("id", bulletin.getId());
			object.put("date", date);
			
			bulletinList.add(object);
		}
		return bulletinList;
	}

	
}

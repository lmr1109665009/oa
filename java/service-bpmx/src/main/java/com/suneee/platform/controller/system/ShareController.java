package com.suneee.platform.controller.system;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.platform.annotion.Action;
import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.service.system.ShareService;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.ucp.base.vo.ResultVo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 对象功能:JDBC 数据 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:Raise
 * 创建时间:2013-01-26
 */
@Controller
@RequestMapping("/platform/system/share/")
public class ShareController extends BaseController
{	
	@Resource
	private ShareService shareService;
	
	@RequestMapping("getPingyin")
	@ResponseBody
	@Action(description="获取拼音")
	public Map<String,Object> getPingyin(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String,Object> map=new HashMap<String, Object>();
		String input= RequestUtil.getString(request, "input");
		String output=shareService.getPingyin(input);
		map.put("output", output);
		return map;
	}

	@RequestMapping("getInformType")
	@ResponseBody
	@Action(description="获取系统通知类型")
	public JSONArray getInformType(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String, IMessageHandler> handlersMap=ServiceUtil.getHandlerMap();
		JSONArray ary=new JSONArray();
		for (Map.Entry<String, IMessageHandler> entry : handlersMap.entrySet()) { 
			IMessageHandler handler=entry.getValue();
			JSONObject jsonObj=new JSONObject();
			jsonObj.accumulate("value", handler.getTitle());
			jsonObj.accumulate("key", entry.getKey());
			jsonObj.accumulate("default", handler.getIsDefaultChecked()?1:0);
			ary.add(jsonObj);
		}
		return ary;
	}
	
	@RequestMapping("getQuanPin")
	@ResponseBody
	@Action(description="获取输入全拼")
	public ResultVo getQuanPin(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			String source = RequestUtil.getString(request, "source");
			String target = PinyinUtil.getPinyinToLowerCase(source);
			if(target.contains(",")){
				target = target.substring(0, target.indexOf(","));
			}
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取输入全拼成功！", target);
		} catch (Exception e) {
			logger.error("获取输入全拼失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取输入全拼失败！");
		}
	}
}

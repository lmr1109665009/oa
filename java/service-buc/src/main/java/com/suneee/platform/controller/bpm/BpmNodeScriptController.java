package com.suneee.platform.controller.bpm;

import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmNodeScript;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmNodeScriptService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:节点运行脚本 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:phl
 * 创建时间:2011-12-30 14:31:20
 */
@Controller
@RequestMapping("/platform/bpm/bpmNodeScript/")
@Action(ownermodel=SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmNodeScriptController extends BaseController
{
	@Resource
	private BpmNodeScriptService bpmNodeScriptService;

	/**
	 * 添加或更新节点运行脚本。
	 * @param request
	 * @param bpmNodeScript 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@ResponseBody
	@Action(description="添加或更新节点运行脚本",
			detail="保存流程定义 【${SysAuditLinkService.getBpmDefinitionLink(actDefId)}】" +
					"的节点 【${SysAuditLinkService.getNodeName(actDefId,nodeId)}】 的脚本设置（事件设置）"
	)
	public ResultVo save(HttpServletRequest request) throws Exception
	{
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		String nodeId= RequestUtil.getString(request, "nodeId");
		String actDefId=RequestUtil.getString(request, "actDefId");
		
		String[] aryScript=request.getParameterValues("script");
		String[] aryScriptType=request.getParameterValues("scriptType");
		List<BpmNodeScript> list=new ArrayList<BpmNodeScript>();
		for(int i=0;i<aryScriptType.length;i++){
			String script=aryScript[i];
			Integer type=Integer.parseInt(aryScriptType[i]);
			if(StringUtil.isEmpty(script)) continue;
			
			BpmNodeScript bpmNodeScript=new BpmNodeScript();
			bpmNodeScript.setScript(script);
			bpmNodeScript.setScriptType(type);
			list.add(bpmNodeScript);
			
		}
		try{
			bpmNodeScriptService.saveScriptDef(actDefId, nodeId, list);
			result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			result.setMessage("保存节点脚本成功");
		}
		catch ( Exception e) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				result.setMessage("保存节点脚本失败:" + str);
			} else {
				String message = ExceptionUtil.getExceptionMessage(e);
				result.setMessage(message);
			}
		}
		return result;
	}

	@RequestMapping("edit")
	@Action(description="编辑节点运行脚本")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long defId=RequestUtil.getLong(request, "defId");
		String nodeId=RequestUtil.getString(request, "nodeId");
		String actDefId=RequestUtil.getString(request, "actDefId");
		String type=RequestUtil.getString(request, "type");
		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");
		ModelAndView mv=getAutoView();
		String vers= request.getHeader("USER-AGENT");
		if(vers.indexOf("MSIE 6")!=-1){
			mv= new ModelAndView("/platform/bpm/bpmNodeScriptEdit_ie6.jsp");
		}
		Map<String,BpmNodeScript> map= bpmNodeScriptService.getMapByNodeScriptId(nodeId, actDefId);
		return mv.addObject("map",map)
				.addObject("type", type)
				.addObject("nodeId", nodeId)
				.addObject("actDefId", actDefId)
				.addObject("defId", defId)
				.addObject("parentActDefId", parentActDefId);
				
	}

}

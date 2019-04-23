package com.suneee.platform.controller.bpm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.util.*;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONArray;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.bpm.BpmButton;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNodeButton;
import com.suneee.platform.model.bpm.BpmNodeButtonXml;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeButtonService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.FormUtil;

/**
 * 对象功能:自定义工具条 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-07-25 18:26:13
 */
@Controller
@RequestMapping("/platform/bpm/bpmNodeButton/")
@Action(ownermodel=SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmNodeButtonController extends BaseController
{
	@Resource
	private BpmNodeButtonService bpmNodeButtonService;
	
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private BpmService bpmService;
	
	/**
	 * 取得自定义工具条分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
//	@Action(description="查看自定义工具条分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Long defId= RequestUtil.getLong(request, "defId");
		
		BpmDefinition bpmDefinition=bpmDefinitionService.getById(defId);
		
		String actDefId=bpmDefinition.getActDefId();
		
		List<BpmNodeSet> list=bpmNodeSetService.getByDefId(defId);
		
		Map<String,FlowNode> taskMap= NodeCache.getByActDefId(actDefId);
		
		Map<String,List<BpmNodeButton>> btnMap= bpmNodeButtonService.getMapByDefId(defId);
		
		//读按钮配置文件button.xml
		String buttonPath = FormUtil.getDesignButtonPath();
		String xml = FileUtil.readFile(buttonPath + "button.xml");
		Document document = Dom4jUtil.loadXml(xml);
		Element root = document.getRootElement();
		String xmlStr = root.asXML();
		BpmNodeButtonXml bpmButtonList = (BpmNodeButtonXml) XmlBeanUtil
				.unmarshall(xmlStr, BpmNodeButtonXml.class);
		List<BpmButton> btnList = bpmButtonList.getButtons();
		//对按钮进行分类
		List<BpmButton> startBtnList = new ArrayList<BpmButton>();//起始节点按钮
		List<BpmButton> firstNodeBtnList = new ArrayList<BpmButton>();//第一个节点按钮
		List<BpmButton> signBtnList = new ArrayList<BpmButton>();//会签节点按钮
		List<BpmButton> commonBtnList = new ArrayList<BpmButton>();//普通节点按钮
		for(BpmButton button:btnList){
			if(button.getInit()==0) continue;
			if(button.getType()==0){
				startBtnList.add(button);
			}
			else if(button.getType()==1){
				firstNodeBtnList.add(button);
			}
			else if(button.getType()==2){
				signBtnList.add(button);
			}
			else if(button.getType()==3){
				commonBtnList.add(button);
			}
//			else if(button.getType()==4){
//				signBtnList.add(button);
//				commonBtnList.add(button);
//			}
		}
		
		return this.getAutoView()
				.addObject("btnMap", btnMap)
				.addObject("taskMap", taskMap)
				.addObject("bpmNodeSetList", list)
				.addObject("bpmDefinition", bpmDefinition)
				.addObject("startBtnList", startBtnList)
				.addObject("firstNodeBtnList", firstNodeBtnList)
				.addObject("signBtnList", signBtnList)
				.addObject("commonBtnList", commonBtnList);
		
	}
	
	@RequestMapping("getByNode")
//	@Action(description="设置节点的操作按钮")
	public ModelAndView getByNode(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Boolean buttonFlag =  RequestUtil.getBoolean(request, "buttonFlag",true);
		long defId=RequestUtil.getLong(request,  "defId",0);
		String nodeId=RequestUtil.getString(request, "nodeId");
		if(defId==0){
			throw new Exception("没有输入流程定义ID");
		}
		
		BpmDefinition bpmDefinition=bpmDefinitionService.getById(defId);
		
		
		ModelAndView mv=this.getAutoView();
		if(StringUtil.isEmpty(nodeId)){
			List<BpmNodeButton> list=bpmNodeButtonService.getByStartForm(defId);
			mv.addObject("btnList", list);
			mv.addObject("isStartForm", 1);
		}
		else{
			List<BpmNodeButton> list=bpmNodeButtonService.getByDefNodeId(defId, nodeId);
			mv.addObject("btnList", list);
			mv.addObject("isStartForm", 0);
		}
		mv.addObject("bpmDefinition", bpmDefinition)
		.addObject("defId", defId)
		.addObject("nodeId", nodeId)
		.addObject("buttonFlag",buttonFlag);
		return mv;
	} 
	
	/**
	 * 删除自定义工具条
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除自定义工具条",
			execOrder=ActionExecOrder.BEFORE,
			detail="<#assign __index=0 />"+
					"<#list StringUtils.split(id,\",\") as item>" +
						"<#assign entity = bpmNodeButtonService.getById(Long.valueOf(item))/>" +
						"<#if item_index==0>" +
							"删除流程定义【${SysAuditLinkService.getBpmDefinitionLink(entity.defId)}】的节点" +
							"<#if entity.nodeid!=''>" +
							     "【${SysAuditLinkService.getNodeName(entity.defId,entity.nodeid)}】" +
							"</#if>" +
							"自定义工具按钮：" +
						"</#if>"+
						"【 ${entity.btnname}】 " +
					"</#list>"
	)
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage resultMessage = null;
		try{
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			bpmNodeButtonService.delByIds(lAryId);
			resultMessage = new ResultMessage(ResultMessage.Success, "删除自定义工具条成功!");
		}
		catch(Exception ex){
			resultMessage = new ResultMessage(ResultMessage.Fail, "自定义工具条删除失败:" + ex.getMessage());
		}
		addMessage(resultMessage, request);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("edit")
	@Action(description="编辑自定义工具条")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Boolean buttonFlag =  RequestUtil.getBoolean(request, "buttonFlag",true);
		Long id=RequestUtil.getLong(request,"id");
		
		BpmNodeButton bpmNodeButton=null;
		
		ModelAndView mv=this.getAutoView();
		
		Long defId=RequestUtil.getLong(request, "defId",0);
		String nodeId=RequestUtil.getString(request, "nodeId");
		
		if(id!=0){
			 bpmNodeButton= bpmNodeButtonService.getById(id);
			 BpmDefinition bpmDefinition= bpmDefinitionService.getById(bpmNodeButton.getDefId());
			 mv.addObject("bpmDefinition", bpmDefinition);
			 
		}else{
			BpmDefinition bpmDefinition= bpmDefinitionService.getById(defId);
			mv.addObject("bpmDefinition", bpmDefinition);
			
			String actDefId=bpmDefinition.getActDefId();
			
			bpmNodeButton=new BpmNodeButton();
			bpmNodeButton.setDefId(defId);
			if(StringUtil.isNotEmpty(nodeId)){
				boolean rtn= bpmService.isSignTask(actDefId,nodeId);
				bpmNodeButton.setNodetype(rtn?1:0);
				bpmNodeButton.setIsstartform(0);
			}
			else{
				bpmNodeButton.setIsstartform(1);
			}
			bpmNodeButton.setActdefid(actDefId);
			bpmNodeButton.setNodeid(nodeId);
		}
		//读按钮配置文件button.xml
		String buttonPath = FormUtil.getDesignButtonPath();
		String xml = FileUtil.readFile(buttonPath + "button.xml");
		Document document = Dom4jUtil.loadXml(xml);
		Element root = document.getRootElement();
		String xmlStr = root.asXML();
		BpmNodeButtonXml bpmButtonList = (BpmNodeButtonXml) XmlBeanUtil
				.unmarshall(xmlStr, BpmNodeButtonXml.class);
		List<BpmButton> list = bpmButtonList.getButtons();
		JSONArray array = JSONArray.fromObject(list);
	    String buttonStr = array.toString();
	    
		return mv.addObject("bpmNodeButton",bpmNodeButton)
				.addObject("defId",defId)
				.addObject("nodeId",nodeId)
				.addObject("buttonFlag",buttonFlag)
				.addObject("buttonStr",buttonStr);
	}

	/**
	 * 取得自定义工具条明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看自定义工具条明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"id");
		BpmNodeButton bpmNodeButton = bpmNodeButtonService.getById(id);		
		return getAutoView().addObject("bpmNodeButton", bpmNodeButton);
	}
	
	
	/**
	 * 取得自定义工具条明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("sort")
	@Action(description="查看自定义工具条明细")
	public void sort(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ResultMessage resultMessage = null;
		String ids = RequestUtil.getString(request, "ids");
		if(StringUtil.isEmpty(ids)){
			resultMessage = new ResultMessage(ResultMessage.Fail,"没有设置操作类型!");
			response.getWriter().print(resultMessage);
			return;
		}
		try{
			bpmNodeButtonService.sort(ids);
			resultMessage = new ResultMessage(ResultMessage.Success,"没有设置操作类型!");
			response.getWriter().print(resultMessage);
		}
		catch(Exception ex){
			String str = MessageUtil.getMessage();
			if(StringUtil.isNotEmpty(str)){
			    resultMessage = new ResultMessage(ResultMessage.Fail, "设置操作类型失败:" + str);
			    response.getWriter().print(resultMessage);
			}
			else{
				String message = ExceptionUtil.getExceptionMessage(ex);
				resultMessage = new ResultMessage(ResultMessage.Fail, message);
				response.getWriter().print(resultMessage);
			}
		}
	}
	
	
	@RequestMapping("init")
	@Action(description="初始化操作按钮",
			detail="初始化流程定义 【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】节点" +
					"<#if !StringUtil.isEmpty(nodeId)>" +
						"【${SysAuditLinkService.getNodeName(Long.valueOf(defId),nodeId)}】" +
					"</#if>" +
					"的操作按钮"
	)
	public void init(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String returnUrl=RequestUtil.getPrePage(request);
		Long defId=RequestUtil.getLong(request, "defId",0);
		String nodeId=RequestUtil.getString(request, "nodeId");
		ResultMessage resultMessage=null;
		try{
			bpmNodeButtonService.init(defId, nodeId);
			 resultMessage=new ResultMessage(ResultMessage.Success,"初始化按钮成功!");
		}
		catch(Exception ex){
		     resultMessage = new ResultMessage(ResultMessage.Fail, "初始化按钮失败:" + ex.getMessage());
		}
		addMessage(resultMessage, request);
		response.sendRedirect(returnUrl);
		
	}
	
	@RequestMapping("initAll")
	@Action(description = "初始化操作按钮",
			detail="初始化流程定义 【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】的全部操作按钮"
	)
	public void initAll(HttpServletRequest request, HttpServletResponse response)throws Exception 
	{
		String returnUrl = RequestUtil.getPrePage(request);
		Long defId = RequestUtil.getLong(request, "defId", 0);

		ResultMessage resultMessage = null;
		try {
			bpmNodeButtonService.initAll(defId);
			resultMessage = new ResultMessage(ResultMessage.Success, "初始化按钮成功!");
		} catch (Exception ex) {
			resultMessage = new ResultMessage(ResultMessage.Fail,"初始化按钮失败:" + ex.getMessage());
		}
		addMessage(resultMessage, request);
		response.sendRedirect(returnUrl);

	}
	
	
	@RequestMapping("delByDefId")
	@Action(description="清除按钮配置",
	
			detail="清除流程定义 【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】的表单按钮"
	)
	public void delByDefId(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String returnUrl=RequestUtil.getPrePage(request);
		Long defId=RequestUtil.getLong(request, "defId",0);
		
		ResultMessage resultMessage=null;
		try{
			bpmNodeButtonService.delByDefId(defId);
			 resultMessage=new ResultMessage(ResultMessage.Success,"清除流程表单按钮成功!");
		}
		catch(Exception ex){
			resultMessage = new ResultMessage(ResultMessage.Fail,"清除流程表单按钮失败:" +ex.getMessage());
		}
		addMessage(resultMessage, request);
		response.sendRedirect(returnUrl);
		
	}
	
	/**
	 * 根据流程定义Id和节点ID删除按钮操作。
	 * @param defId		流程定义id
	 * @param nodeId	流程节点ID
	 * @throws IOException 
	 */
	@RequestMapping("deByDefNodeId")
	@Action(description="清除按钮配置",
			detail="删除流程定义 【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】的节点的按钮操作"
	)
	public void deByDefNodeId(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String returnUrl=RequestUtil.getPrePage(request);
		Long defId=RequestUtil.getLong(request, "defId",0);
		String nodeId=RequestUtil.getString(request, "nodeId");
	
		ResultMessage resultMessage=null;
		try{
			bpmNodeButtonService.delByDefNodeId(defId,nodeId);
			 resultMessage=new ResultMessage(ResultMessage.Success,"删除流程表单按钮成功!");
		}
		catch(Exception ex){
			 resultMessage = new ResultMessage(ResultMessage.Fail,"删除流程表单按钮失败:" + ex.getMessage());
		}
		addMessage(resultMessage, request);
		response.sendRedirect(returnUrl);
	}


}

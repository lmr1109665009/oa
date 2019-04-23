package com.suneee.platform.controller.bpm;

import com.suneee.core.bpm.util.BpmUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.HttpUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 对象功能:节点设置 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-09 14:57:19
 */
@Controller
@RequestMapping("/platform/bpm/bpmNodeSet/")
@Action(ownermodel= SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmNodeSetController extends BaseController
{
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmService bpmService;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	
	/**
	 * 取得节点设置列表。
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看节点设置分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		Long defId= RequestUtil.getLong(request, "defId");
		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");
		String isNew = "yes";         //是否是新的流程 或者 重新设置    并且没有绑定任何表单的流程   
		BpmDefinition bpmDefinition=bpmDefinitionService.getById(defId);
		String actDefId=bpmDefinition.getActDefId();
		
		String deployId=bpmDefinition.getActDeployId().toString();
		List<String> nodeList=new ArrayList<String>();
		
		Map<String, Map<String, String>> activityList=new HashMap<String, Map<String, String>>();
		String defXml = bpmService.getDefXmlByDeployId(deployId);
		Map<String, Map<String, String>> activityAllList= BpmUtil.getTranstoActivitys(defXml, nodeList);
		List<BpmNodeSet> list = null;
		if(StringUtil.isEmpty(parentActDefId)){
			list = bpmNodeSetService.getByDefId(defId);
		}else{
			list = bpmNodeSetService.getByDefId(defId, parentActDefId);
		}
		Map<String, String> taskMap=activityAllList.get("任务节点");
		for(int i=0;i<list.size();i++){	
			String nodeId=list.get(i).getNodeId();
			Map<String, String> tempMap=new HashMap<String,String>();
			Set<Map.Entry<String, String>> set = taskMap.entrySet();
			for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
			   Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
			   if(!nodeId.equals(entry.getKey())){
				   tempMap.put(entry.getKey(), entry.getValue());
			   }
			}
			activityList.put(list.get(i).getNodeId(), tempMap);
		}
		
		BpmNodeSet globalForm = null;
		BpmNodeSet bpmForm = null;
		if(StringUtil.isEmpty(parentActDefId)){
			globalForm = bpmNodeSetService.getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm);
			bpmForm = bpmNodeSetService.getBySetType(actDefId, BpmNodeSet.SetType_BpmForm);
		}else{
			globalForm = bpmNodeSetService.getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm, parentActDefId);
			bpmForm = bpmNodeSetService.getBySetType(actDefId, BpmNodeSet.SetType_BpmForm, parentActDefId);
		}
		if (BeanUtils.isNotEmpty(globalForm)) {
			BpmFormDef bpmFormDef = bpmFormDefService.getDefaultPublishedByFormKey(globalForm.getFormKey());
			if(BeanUtils.isNotEmpty(bpmFormDef) &&  BeanUtils.isNotIncZeroEmpty(bpmFormDef.getTableId()) ){
				
				List<BpmFormTable> formTablelist = bpmFormTableService.getSubTableByMainTableId(bpmFormDef.getTableId());
				if(BeanUtils.isNotEmpty(formTablelist)){
					globalForm.setExistSubTable(new Short("1"));
				}else{
					globalForm.setExistSubTable(new Short("0"));
				}
			}
		}
		for(BpmNodeSet bpmNodeSet:list){
			BpmFormDef bpmFormDef = bpmFormDefService.getDefaultPublishedByFormKey(bpmNodeSet.getFormKey());
			if(BeanUtils.isNotEmpty(bpmFormDef) &&  BeanUtils.isNotIncZeroEmpty(bpmFormDef.getTableId()) ){
				if("yes".equals(isNew)){
					isNew = "no";
				}
				List<BpmFormTable> formTablelist = bpmFormTableService.getSubTableByMainTableId(bpmFormDef.getTableId());
				if(BeanUtils.isNotEmpty(formTablelist)){
					bpmNodeSet.setExistSubTable(new Short("1"));
				}else{
					bpmNodeSet.setExistSubTable(new Short("0"));
				}
			}
		}
		
		if( "yes".equals(isNew)&& (BeanUtils.isNotEmpty(globalForm)||BeanUtils.isNotEmpty(bpmForm)) ){    //当 bpmNodeSetList globalForm bpmForm 三个都为空时 表示没有绑定表单
			isNew = "no";
		}
		
		ModelAndView mv=this.getAutoView()
				.addObject("bpmNodeSetList",list)
				.addObject("bpmDefinition",bpmDefinition)
				.addObject("globalForm", globalForm)
				.addObject("bpmForm", bpmForm)
				.addObject("activityList", activityList)
		        .addObject("isNew", isNew)
		        .addObject("parentActDefId", parentActDefId);
		return mv;
	}
	
	/**
	 * 删除节点设置
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除节点设置",
			execOrder= ActionExecOrder.BEFORE,
			detail="<#list StringUtils.split(setId,\",\") as item>" +
						"<#assign entity = bpmNodeSetService.getById(Long.valueOf(item))/>" +
						"<#if item_index==0>" +
							"删除流程定义【${SysAuditLinkService.getBpmDefinitionLink(entity.defId)}】的节点" +
						"</#if>"+
						"【${entity.nodeName}】" +
					"</#list>的设置"
	)
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		Long[] lAryId =RequestUtil.getLongAryByStr(request, "setId");
		bpmNodeSetService.delByIds(lAryId);
		response.sendRedirect(preUrl);
	}

	@RequestMapping("edit")
	@Action(description="编辑节点设置")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long setId=RequestUtil.getLong(request,"setId");
		String returnUrl=RequestUtil.getPrePage(request);
		BpmNodeSet bpmNodeSet=null;
		if(setId!=null){
			 bpmNodeSet= bpmNodeSetService.getById(setId);
		}else{
			bpmNodeSet=new BpmNodeSet();
		}
		return getAutoView().addObject("bpmNodeSet",bpmNodeSet).addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得节点设置明细
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看节点设置明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long id=RequestUtil.getLong(request,"setId");
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getById(id);		
		return getAutoView().addObject("bpmNodeSet", bpmNodeSet);
	}
	
	
	@RequestMapping("save")
	@Action(description="成功设置流程节点表单",
			detail="设置流程定义${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}的节点表单及跳转方式"
	)
	public void save(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Long defId=RequestUtil.getLong(request,"defId");
		//父流程定义Id
		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");
		BpmDefinition bpmDefinition=bpmDefinitionService.getById(defId);
		
		String[] nodeIds=request.getParameterValues("nodeId");
		String[] nodeNames=request.getParameterValues("nodename");
		String[] formTypes=request.getParameterValues("formType");
		String[] aryFormKey=request.getParameterValues("formKey");
		String[] formUrls=request.getParameterValues("formUrl");
		String[] formDefNames=request.getParameterValues("formDefName");
		String[] aryBeforeHandler=request.getParameterValues("beforeHandler");
		String[] aryAfterHandler=request.getParameterValues("afterHandler");
		String[] aryDetailUrl=request.getParameterValues("detailUrl");
		String[] aryMobileFormKey=request.getParameterValues("mobileFormKey");
		String[] aryMobileFormName=request.getParameterValues("mobileFormName");
		Map<String,BpmNodeSet> nodeMap = null;
		if(StringUtil.isEmpty(parentActDefId)){
			nodeMap = bpmNodeSetService.getMapByDefId(defId);
		}else{//子流程的节点数据
			nodeMap = bpmNodeSetService.getMapByDefId(defId, parentActDefId);
		}
	
		List<BpmNodeSet> nodeList=new ArrayList<BpmNodeSet>();
		
		boolean suppportMobile=false;
		if (null == nodeIds){
			HttpUtil.writeResponseJson(response, ResponseMessage.fail("请先绑定表单！"));
		}else {
			for(int i=0;i<nodeIds.length;i++){
				String nodeId=nodeIds[i];
				BpmNodeSet nodeSet=new BpmNodeSet();
				if(nodeMap.containsKey(nodeId)){
					nodeSet=nodeMap.get(nodeId);
					//设置原有的表单key，用于删除之前设置表单节点的权限。
					if(StringUtil.isNotEmpty(nodeSet.getFormKey())){
						nodeSet.setOldFormKey(nodeSet.getFormKey());
					}
				}
				nodeSet.setNodeId(nodeId);
				nodeSet.setActDefId(bpmDefinition.getActDefId());
				nodeSet.setNodeName(nodeNames[i]);


				short formType= Short.parseShort(formTypes[i]);

				String beforeHandler=aryBeforeHandler[i];
				String afterHandler=aryAfterHandler[i];

				beforeHandler=getHandler(beforeHandler);
				afterHandler=getHandler(afterHandler);

				String detailUrl=aryDetailUrl[i];
				String formUrl=formUrls[i];


				nodeSet.setFormType(formType);
				//没有选择表单
				if(formType==-1){
					nodeSet.setFormUrl("");
					nodeSet.setDetailUrl("");

					nodeSet.setFormKey("");
					nodeSet.setFormDefName("");

					nodeSet.setMobileFormKey("");
					nodeSet.setMobileFormName("");

				}
				//在线表单
				else if(formType==0){
					nodeSet.setFormUrl("");
					nodeSet.setDetailUrl("");

					String formKey="";
					if(StringUtil.isNotEmpty(aryFormKey[i]) || StringUtil.isNotEmpty(aryMobileFormKey[i])){
						formKey=aryFormKey[i];

						nodeSet.setFormKey(formKey);
						//获取当前默认版本的表单
						BpmFormDef defaultVersionByFormKey = bpmFormDefService.getDefaultVersionByFormKey(formKey);
						//设置当前版本的表单
						if(BeanUtils.isNotEmpty(defaultVersionByFormKey)){
							nodeSet.setFormDefId(defaultVersionByFormKey.getFormDefId());
						}
						nodeSet.setFormDefName(formDefNames[i]);
						//手机表单
						if(StringUtil.isNotEmpty(aryMobileFormKey[i])){
							nodeSet.setEnableMobile(1);
							nodeSet.setMobileFormKey(aryMobileFormKey[i]);
							nodeSet.setMobileFormName(aryMobileFormName[i]);
							suppportMobile=true;
						}
						else{
							nodeSet.setEnableMobile(0);
							nodeSet.setMobileFormKey("");
							nodeSet.setMobileFormName("");
						}

					}
					else{
						nodeSet.setFormKey("");
						nodeSet.setFormDefName("");
						nodeSet.setMobileFormKey("");
						nodeSet.setMobileFormName("");
						nodeSet.setEnableMobile(0);
						nodeSet.setFormType((short) -1);

					}
				}
				//url表单
				else{
					nodeSet.setFormKey("");
					nodeSet.setFormDefName("");

					nodeSet.setFormUrl(formUrl);
					nodeSet.setDetailUrl(detailUrl);

				}

				nodeSet.setBeforeHandler(beforeHandler);
				nodeSet.setAfterHandler(afterHandler);

				nodeSet.setDefId(new Long(defId));

				String[] jumpType=request.getParameterValues("jumpType_"+nodeId);
				if(jumpType!=null){
					nodeSet.setJumpType(StringUtil.getArrayAsString(jumpType));
				}else{
					nodeSet.setJumpType("");
				}
				String isHideOption=request.getParameter("isHideOption_"+nodeId);
				String isRequired=request.getParameter("isRequired_"+nodeId);
				String isPopup=request.getParameter("isPopup_"+nodeId);
				String opinionField=request.getParameter("opinionField_"+nodeId);
				String isHidePath=request.getParameter("isHidePath_"+nodeId);
				String opinionHtml=request.getParameter("opinionHtml_"+nodeId);
				if(StringUtil.isNotEmpty(isHideOption)){
					nodeSet.setIsHideOption(BpmNodeSet.HIDE_OPTION);
				}else{
					nodeSet.setIsHideOption(BpmNodeSet.NOT_HIDE_OPTION);
				}
				if(StringUtil.isNotEmpty(isRequired)){
					nodeSet.setIsRequired(BpmNodeSet.IS_REQUIRED);
				}else{
					nodeSet.setIsRequired(BpmNodeSet.NOT_IS_REQUIRED);
				}
				if(StringUtil.isNotEmpty(isPopup)){
					nodeSet.setIsPopup(BpmNodeSet.IS_POPUP);
				}else{
					nodeSet.setIsPopup(BpmNodeSet.NOT_IS_POPUP);
				}

				nodeSet.setOpinionField(opinionField);

				if(StringUtil.isNotEmpty(isHidePath)){
					nodeSet.setIsHidePath(BpmNodeSet.HIDE_PATH);
				}else{
					nodeSet.setIsHidePath(BpmNodeSet.NOT_HIDE_PATH);
				}

				if(StringUtil.isNotEmpty(opinionHtml)){
					nodeSet.setOpinionHtml((short)1);
				}else{
					nodeSet.setOpinionHtml((short)0);
				}
				nodeSet.setSetType(BpmNodeSet.SetType_TaskNode);
				nodeSet.setParentActDefId(parentActDefId);
				nodeList.add(nodeSet);
			}
			//是否支持手机端。
			bpmDefinition.setAllowMobile(suppportMobile?1:0);
			List<BpmNodeSet> list=getGlobalBpm(request,bpmDefinition);
			nodeList.addAll(list);
			String returnUrl = "list.ht?defId="+defId +"&time=" + System.currentTimeMillis();
			try{
				//更新是否允许手机状态标记。
				bpmDefinitionService.update(bpmDefinition);
				bpmNodeSetService.save(defId, nodeList, parentActDefId);
				addMessage(new ResultMessage(ResultMessage.Success,"成功设置流程节点表单及跳转方式 !"), request);
				if(StringUtil.isNotEmpty(parentActDefId)){
					returnUrl += "&parentActDefId="+parentActDefId;
				}
				response.sendRedirect(returnUrl);
			}
			catch(Exception ex){
				ex.printStackTrace();
				addMessage(new ResultMessage(ResultMessage.Fail,ex.getMessage()), request);
				response.sendRedirect(returnUrl);
			}
		}
	}
	
	/**
	 * 从request中构建全局和流程实例表单数据。
	 * @param request
	 * @param bpmDefinition
	 * @return
	 */
	private List<BpmNodeSet> getGlobalBpm(HttpServletRequest request,BpmDefinition bpmDefinition){
		List<BpmNodeSet> list=new ArrayList<BpmNodeSet>();
		String parentActDefId = RequestUtil.getString(request, "parentActDefId", "");
		int globalFormType=RequestUtil.getInt(request, "globalFormType");
		if(globalFormType>=0){
			String defaultFormKey=RequestUtil.getString(request, "defaultFormKey");
			String defaultFormName=RequestUtil.getString(request, "defaultFormName");

			
			String defaultMobileFormKey=RequestUtil.getString(request, "defaultMobileFormKey");
			String defaultMobileFormName=RequestUtil.getString(request, "defaultMobileFormName");
			
			String formUrlGlobal=RequestUtil.getString(request, "formUrlGlobal");
			String detailUrlGlobal=RequestUtil.getString(request,"detailUrlGlobal");
			
			BpmNodeSet bpmNodeSet=new BpmNodeSet();
			bpmNodeSet.setDefId(bpmDefinition.getDefId());
			bpmNodeSet.setActDefId(bpmDefinition.getActDefId());
			bpmNodeSet.setFormKey(defaultFormKey);
			bpmNodeSet.setFormDefName(defaultFormName);
			bpmNodeSet.setFormUrl(formUrlGlobal);

			bpmNodeSet.setFormType((short)globalFormType);
			bpmNodeSet.setDetailUrl(detailUrlGlobal);
			bpmNodeSet.setSetType(BpmNodeSet.SetType_GloabalForm);
			bpmNodeSet.setParentActDefId(parentActDefId);
			
			int enableMobile=StringUtil.isNotEmpty(defaultMobileFormKey)?1:0;
			bpmNodeSet.setEnableMobile(enableMobile);
			if(enableMobile==1){
				bpmDefinition.setAllowMobile(1);
			}
			
			bpmNodeSet.setMobileFormKey(defaultMobileFormKey);
			bpmNodeSet.setMobileFormName(defaultMobileFormName);
			
			
			if(globalFormType==BpmNodeSet.FORM_TYPE_ONLINE){
				if(StringUtil.isNotEmpty(defaultFormKey)){
					list.add(bpmNodeSet);
				}
			}
			else{
				if(StringUtil.isNotEmpty(formUrlGlobal)){
					bpmNodeSet.setFormKey(null);
					list.add(bpmNodeSet);
				}
			}
		}
		int bpmFormType=RequestUtil.getInt(request, "bpmFormType");
		if(bpmFormType>=0){
			String bpmFormKey=RequestUtil.getString(request, "bpmFormKey");
			String bpmFormName=RequestUtil.getString(request, "bpmFormName");
			String bpmFormUrl=RequestUtil.getString(request, "bpmFormUrl");
			
			String bpmFormMobileFormKey=RequestUtil.getString(request, "mobileBpmFormKey");
			String bpmFormMobileFormName=RequestUtil.getString(request, "mobileBpmFormName");
			
			BpmNodeSet bpmNodeSet=new BpmNodeSet();
			bpmNodeSet.setDefId(bpmDefinition.getDefId());
			bpmNodeSet.setActDefId(bpmDefinition.getActDefId());
			
			if(StringUtil.isNotEmpty(bpmFormKey)){
				bpmNodeSet.setFormKey(bpmFormKey);
				bpmNodeSet.setFormDefName(bpmFormName);
			}
			bpmNodeSet.setFormUrl(bpmFormUrl);
			bpmNodeSet.setFormType((short)bpmFormType);
			bpmNodeSet.setSetType(BpmNodeSet.SetType_BpmForm);
			bpmNodeSet.setParentActDefId(parentActDefId);
			
			if(StringUtil.isNotEmpty(bpmFormMobileFormKey)){
				bpmNodeSet.setEnableMobile(1);
			}
			
			bpmNodeSet.setMobileFormKey(bpmFormMobileFormKey);
			bpmNodeSet.setMobileFormName(bpmFormMobileFormName);
			
			list.add(bpmNodeSet);
		}
		return list;
	}
	
	private String getHandler(String handler){
		if(StringUtil.isEmpty(handler) || handler.indexOf(".")==-1){
			handler="";
		}
		return handler;
	}
	
	/**
	 * 验证handler。
	 * 输入格式为 serviceId +"." + 方法名。
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("validHandler")
	@Action(description="验证处理器")
	public void validHandler(HttpServletRequest request,HttpServletResponse response) throws IOException {
		String handler=RequestUtil.getString(request,"handler");
		int rtn=BpmUtil.isHandlerValid(handler);
		String template="{\"result\":\"%s\",\"msg\":\"%s\"}";
		String msg="";
		switch (rtn) {
			case 0:
				msg="输入有效";
				break;
			case -1:
				msg="输入格式无效";
				break;
			case -2:
				msg="没有service类";
				break;
			case -3:
				msg="没有对应的方法";
				break;
			default:
				msg="其他错误";
				break;
		}
		String str=String.format(template, rtn,msg);
		response.getWriter().print(str);
	}
	
	/**
	 * 获取流程节点
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("flowScope")
	@Action(description="获取流程节点")
	public ModelAndView flowScope(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String actDefId = RequestUtil.getString(request, "actDefId");
		String nodeId = RequestUtil.getString(request, "nodeId");
		String parentActDefId= RequestUtil.getString(request, "parentActDefId");
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);		
		return getAutoView().addObject("bpmNodeSet", bpmNodeSet);
	}	
	/**
	 * 为流程节点设置选择器的的使用范围
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("savaScope")
	@ResponseBody
	@Action(description=" 为流程节点设置选择器的的使用范围")
	public ResultVo savaScope(HttpServletRequest request) throws Exception
	{
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		Long setId=RequestUtil.getLong(request, "setId", 0);
		String scope=RequestUtil.getString(request, "scope");
		try {
			bpmNodeSetService.updateScopeById(setId,scope);
			result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			result.setMessage("流程节点设置选择器使用范围成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage("设置失败!");
		}
		return result;
	}

	/**
	 * 为流程节点设置沟通人员
	 * @param request
	 * @param response
	 * @param bpmNodeSet
	 * @throws Exception
	 */
	@RequestMapping("savaCommunicate")
	@ResponseBody
	@Action(description=" 为流程节点设置沟通人员")
	public ResultVo savaCommunicate(HttpServletRequest request, HttpServletResponse response, BpmNodeSet bpmNodeSet) throws Exception
	{
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		Long setId=RequestUtil.getLong(request, "setId", 0);
		String communicate=RequestUtil.getString(request, "communicate");
		try {
			bpmNodeSetService.updateCommunicateById(setId,communicate);
			result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			result.setMessage("流程节点设置选择器使用范围成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage("设置失败!");
		}
		return result;
	}
	
	@RequestMapping("communicate")
	@Action(description="获取流程节点沟通人员设置")
	public ModelAndView communicate(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String actDefId = RequestUtil.getString(request, "actDefId");
		String nodeId = RequestUtil.getString(request, "nodeId");
		String parentActDefId= RequestUtil.getString(request, "parentActDefId");
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);		
		return getAutoView().addObject("bpmNodeSet", bpmNodeSet);
	}
}

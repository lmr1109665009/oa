package com.suneee.platform.controller.bpm;

import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmDefVar;
import com.suneee.platform.service.bpm.BpmDefVarService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 对象功能:流程变量定义 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-02 13:23:25
 */
@Controller
@RequestMapping("/platform/bpm/bpmDefVar/")
public class BpmDefVarFormController extends BaseFormController
{
	@Resource
	private BpmDefVarService bpmDefVarService;
	
	/**
	 * 添加或更新流程变量定义。
	 * @param request
	 * @param bpmDefVar 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新流程变量定义")
	public ResultVo save(HttpServletRequest request, BpmDefVar bpmDefVar,BindingResult bindResult) throws Exception
	{
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		ResultMessage resultMessage=validForm("bpmDefVar", bpmDefVar, bindResult, request);
		if(resultMessage.getResult()==ResultMessage.Fail)
		{
			result.setMessage(resultMessage.getMessage());
			return result;
		}
		String nodeId=null;
		String nodeName=null;
		/*if(bpmDefVar.getVarScope().equals("task")){
			if(StringUtils.isNotEmpty(bpmDefVar.getNodeId())){
				String [] nodeSets=bpmDefVar.getNodeId().split(",");
				nodeId=nodeSets[0];
				nodeName=nodeSets[1];
			}
		}*/
		String resultMsg=null;
			if(bpmDefVar.getVarId()==null){
				boolean isExist=bpmDefVarService.isVarNameExist(bpmDefVar.getVarName(),bpmDefVar.getVarKey(),bpmDefVar.getDefId());
				if(!isExist){
				bpmDefVar.setVarId(UniqueIdUtil.genId());
				bpmDefVar.setNodeId(nodeId);
				bpmDefVar.setNodeName(nodeName);
				bpmDefVarService.add(bpmDefVar);
				resultMsg="添加流程变量成功";
				result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			 }else{
			    	resultMsg="该流程中已经存在该变量名称或变量key!";
			    }
			}
			else{
				//1名称和key都不修改
				//2只修改名称，但不修改key
				//3只修改key,但不修改名称
				//4名称和key同时修改
				BpmDefVar var=bpmDefVarService.getById(bpmDefVar.getVarId());
				if(var.getVarName().equals(bpmDefVar.getVarName())&&var.getVarKey().equals(bpmDefVar.getVarKey())){
					bpmDefVar.setNodeId(nodeId);
					bpmDefVar.setNodeName(nodeName);
					bpmDefVarService.update(bpmDefVar);
					resultMsg="更新流程变量成功";
					result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
				}else if(var.getVarName().equals(bpmDefVar.getVarName())&&!var.getVarKey().equals(bpmDefVar.getVarKey())){
					boolean rtn=bpmDefVarService.isVarNameExist(null,bpmDefVar.getVarKey(),bpmDefVar.getDefId());
					if(rtn){
						resultMsg="流程中已经存在该变量key!";
					}else{
						bpmDefVar.setNodeId(nodeId);
						bpmDefVar.setNodeName(nodeName);
						bpmDefVarService.update(bpmDefVar);
						resultMsg="更新流程变量成功";
						result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
					}
				}else if(!var.getVarName().equals(bpmDefVar.getVarName())&&var.getVarKey().equals(bpmDefVar.getVarKey())){
					boolean rtn=bpmDefVarService.isVarNameExist(bpmDefVar.getVarName(),null,bpmDefVar.getDefId());
					if(rtn){
						resultMsg="流程中已经存在该变量名称!";
					}else{
						bpmDefVar.setNodeId(nodeId);
						bpmDefVar.setNodeName(nodeName);
						bpmDefVarService.update(bpmDefVar);
						resultMsg="更新流程变量成功";
						result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
					}
				}else{
					bpmDefVar.setNodeId(nodeId);
					bpmDefVar.setNodeName(nodeName);
					bpmDefVarService.update(bpmDefVar);
					resultMsg="更新流程变量成功";
					result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
				}
			}
		result.setMessage(resultMsg);
		return result;
}
	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param varId
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected BpmDefVar getFormObject(@RequestParam("varId") Long varId,Model model) throws Exception {
		logger.debug("enter BpmDefVar getFormObject here....");
		BpmDefVar bpmDefVar=null;
		if(varId!=null){
			bpmDefVar=bpmDefVarService.getById(varId);
		}else{
			bpmDefVar= new BpmDefVar();
		}
		return bpmDefVar;
    }

}

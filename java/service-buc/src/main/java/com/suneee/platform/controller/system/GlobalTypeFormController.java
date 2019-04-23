package com.suneee.platform.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对象功能:系统分类表单控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2012-01-14 09:28:34
 */
@Controller
@RequestMapping("/platform/system/globalType/")
@Action(ownermodel=SysAuditModelType.SYSTEM_SETTING)
public class GlobalTypeFormController extends BaseFormController
{
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private IdentityService identityService;

	/**
	 * 添加或更新系统分类。
	 * @param request
	 * @param response
	 * @param globalType 添加或更新的实体
	 * @param bindResult
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新系统分类",
			detail="<#if isAdd>添加<#else>更新</#if>系统分类" +
					"【${SysAuditLinkService.getGlobalTypeLink(Long.valueOf(typeId))}】")
	public void save(HttpServletRequest request, HttpServletResponse response, GlobalType globalType,BindingResult bindResult) throws Exception
	{
		ResultMessage resultMessage=validForm("globalType", globalType, bindResult, request);
		if(resultMessage.getResult()==ResultMessage.Fail){
			writeResultMessage(response.getWriter(),resultMessage);
			return;
		}
		boolean isadd=globalType.getTypeId()==0;
		//父节点
		long parentId= RequestUtil.getLong(request, "parentId",0);
		//是否根节点
		int isRoot=RequestUtil.getInt(request, "isRoot");

		int isPrivate=RequestUtil.getInt(request, "isPrivate",0);

		Long userId= ContextUtil.getCurrentUserId();

		String nodeKeyOld=globalType.getNodeKey();
		String nodeKey = globalType.getNodeKey();
		String resultMsg=null;
		if(globalType.getTypeId()==0){
			if(parentId!=0){
				GlobalType parentGlobal=globalTypeService.getById(parentId);
				if(parentGlobal!=null){
					parentGlobal.setIsLeaf(1);
					globalTypeService.update(parentGlobal);
					globalType.setEnterpriseCode(parentGlobal.getEnterpriseCode());
				}
			}
			GlobalType tmpGlobalType=globalTypeService.getInitGlobalType(isRoot,parentId);
			String catKey=tmpGlobalType.getCatKey();
			if(StringUtil.isNotEmpty(nodeKey)){
				boolean isExist=globalTypeService.isNodeKeyExistsForEnterprise(catKey, nodeKey,globalType.getEnterpriseCode());
				if(isExist){
					resultMsg="节点KEY已存在!,你可以在填写的key值后加上一些字符";
					writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Fail);
					return;
				}else {
					isExist=globalTypeService.isNodeKeyExists(catKey,nodeKey,globalType.getEnterpriseCode());
					if(isExist){
						writeResultMessage(response.getWriter(),resultMsg,"key_exist",ResultMessage.Fail);
						return;
					}
				}
			}
			//分类key不为数据字典的情况
			if(!catKey.equals(GlobalType.CAT_DIC)){
				globalType.setType(tmpGlobalType.getType());
			}
			//设置用户ID
			if(isPrivate==1){
				globalType.setUserId(userId);
			}
			globalType.setCatKey(catKey);
			globalType.setNodePath(tmpGlobalType.getNodePath());
			globalType.setTypeId(tmpGlobalType.getTypeId());
			globalType.setDepth(1);
			globalType.setSn(0L);
			globalType.setIsLeaf(0);
			if(globalType.getNodeCodeType().equals(GlobalType.NODE_CODE_TYPE_AUTO_Y)){
				globalType.setNodeCode(identityService.nextId(globalType.getNodeCode()));
			}
			globalTypeService.add(globalType);
			resultMsg="添加系统分类成功";
		}else{
			GlobalType parentGlobalType=globalTypeService.getById(parentId);
			if (parentGlobalType==null){
				globalType.setNodePath(parentId +"." + globalType.getTypeId() +".");
			}else {
				globalType.setNodePath(parentGlobalType.getNodePath() +globalType.getTypeId() +".");
				globalType.setEnterpriseCode(parentGlobalType.getEnterpriseCode());
			}
			Long typeId=globalType.getTypeId();
			String catKey=globalType.getCatKey();
			//判断是否存在。
			boolean isExist= globalTypeService.isNodeKeyExistsForEnterprise(typeId, catKey, nodeKey,globalType.getEnterpriseCode());
			if(isExist){
				resultMsg="节点KEY已存在!";
				writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Fail);
				return;
			}else{
				isExist=globalTypeService.isNodeKeyExistsForUpdate(typeId,catKey,nodeKey, CookieUitl.getCurrentEnterpriseCode());
				if (isExist){
					writeResultMessage(response.getWriter(),resultMsg,"key_exist",ResultMessage.Fail);
					return;
				}
			}
			globalTypeService.updateAndSubPath(globalType);
			resultMsg="更新分类成功";
		}
		writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);

		try {
			SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
			SysAuditThreadLocalHolder.putParamerter("typeId", globalType.getTypeId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param typeId
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected GlobalType getFormObject(@RequestParam("typeId") Long typeId,Model model) throws Exception {
		GlobalType globalType=null;
		
		if(typeId!=0){
			globalType=globalTypeService.getById(typeId);
		}else{
			globalType= new GlobalType();
		}
		return globalType;
    }

}
